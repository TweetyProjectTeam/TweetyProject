/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.tweety.swarms.boids;

import java.awt.*;
import java.lang.Math;
import java.util.*;

public class Boid {

	private Point p = new Point();
    private int direction, speed; // direction in degree with 0 straight down and clockwise
    private Random generator = new Random();
    private int width = 1000, height = 710; // :TODO: Scalability!?
    private int padding = 2;
    private Vector<Boid> boids;
    private Vector<Obstacle> obs;
    private Vector<Food> food;
    private Vector<Line> lines;
    private Vector<Pheromone> trail;
    private int distSensetivity = 200;
    private int id;
    private int hunger = 0, hungerThres = generator.nextInt(500)+200;
    private Color color;
    private int layTrail, trailLength = generator.nextInt(200);
    private Point lastFood;
    private boolean race;
    
    
    public Boid (Vector<Boid> boids, Vector<Obstacle> obs, Vector<Food> food, Vector<Line> lines, Vector<Pheromone> trail, int id, int r) {
    	this.id    = id;
    	this.boids = boids;
    	this.obs   = obs;
    	this.food  = food;
        this.lines = lines;
        this.trail = trail;
    	p.x        = generator.nextInt(width);
    	p.y        = generator.nextInt(height);
    	direction  = generator.nextInt(360);
        if (r == 2) race       = generator.nextBoolean();
        else race = r==0 ? true:false;
    	speed      = generator.nextInt(5)+(race ? 5:7);
    }

    // think Section
	Point currentVector;
	Point vectorSwarmCenter 	= new Point(0,0);
	Point vectorCollisionAvoid 	= new Point(0,0);
	Point vectorObsAvoid 		= new Point(0,0);
	Point vectorVeloMatching 	= new Point(0,0);
	Point vectorFood		 	= new Point(0,0);
	Boid tmpBoid;
	float dist;
	int speedsum,k,centercount;
    
    final Color C00 = new Color(0, 180, 0);
    final Color C01 = new Color(0, 255, 0);
    final Color C02 = new Color(150, 255, 150);
    
    final Color C10 = new Color(0, 0, 180);
    final Color C11 = new Color(0, 0, 255);
    final Color C12 = new Color(150, 150, 255);
	
    public void think () {
    	// set main vector with old randomized old direction
    	direction += generator.nextInt(20) - 10;
    	if (direction > 360) direction -= 360;
    	currentVector = BV.dirSpeedToVector(direction, speed*10); //direction+(generator.nextInt(45)-90)
    	
    	// getting hungry
    	hunger++;
    	
    	//reset vars
    	vectorSwarmCenter.x 		= 0; vectorSwarmCenter.y 	= 0;
    	vectorCollisionAvoid.x 		= 0; vectorCollisionAvoid.y	= 0;
    	vectorVeloMatching.x		= 0; vectorVeloMatching.y 	= 0;
    	vectorObsAvoid.x			= 0; vectorObsAvoid.y 		= 0;
    	vectorFood.x				= 0; vectorFood.y 			= 0;
    	k          = 0;
    	speedsum   = 0;
    	centercount= 0;
    	color      =  race ? C00 : C10;
    	    	
    	// for all other boids
    	for (int i=0; i<boids.size(); i++) {
    		if (i==id) continue; // skip yourself    		
    		tmpBoid = boids.get(i);
    		dist = Math.round(BV.distance(p, tmpBoid.getP()));    		
    		if (dist > distSensetivity) continue; // skip far away
       		if (tmpBoid.getRace() == race) updateSwarmCenterVector();
    		updateVectorCollisionAvoid();
            if (tmpBoid.getRace() == race) updateVectorVeloMatching();
    	}
    	
    	// for all obstacles
    	for( ListIterator<Obstacle> it = obs.listIterator(); it.hasNext(); it.next()) {
    	        avoidObs( it.nextIndex() );
    	}
        
        // for all lines
        for( ListIterator<Line> it = lines.listIterator(); it.hasNext(); it.next()) {
            avoidLines( it.nextIndex() );
        }
        
    	// hungry?
	    if (hunger > hungerThres) {
           // if (color != C00 || color != C10 )
             //   speed -= Math.max(generator.nextInt(3),2);
    
	        // food in sight?
            if (hunger > 2*hungerThres) color = race ? C02 : C12;
            else                        color = race ? C01 : C11;
	        for( ListIterator<Food> it = food.listIterator(); it.hasNext();) {
	            if ( it.next().getS() > 0) 
	                findFood(it.nextIndex()-1);
    	    }
            
            // if no food in sight, search for a trail
            if  (race && vectorFood.x==0 && vectorFood.y==0) 
                findTrail();
    	}
        
    	// set new values
    	if (k>0) speed = Math.round(speedsum/k); // set speed    	
    	setDirection();
        
        // lay Trail
        if ( race && layTrail-- > 0 )
            trail.add( new Pheromone( (Point)p.clone(), Math.round(BV.distance((Point)p.clone(), lastFood)), trailLength ) );
    	
    } // end think
    
    private void updateSwarmCenterVector() {
    	if (dist<50) {
    	    centercount++;
    		Point vectorSelf2TmpBoid 	= BV.vectorSub( tmpBoid.getP(),		 p );
    		vectorSwarmCenter 			= BV.vectorAdd( vectorSwarmCenter,	 vectorSelf2TmpBoid );
    	}
    }
    
    private void updateVectorCollisionAvoid() {
    	if (dist<8) {
        	Point tmpBoid2Self 		= BV.vectorSub( p,						tmpBoid.getP() );
        	vectorCollisionAvoid 	= BV.vectorAdd( vectorCollisionAvoid, 	BV.scaleAntiDist(tmpBoid2Self) );
    	}
    }
    
    //obsAvoid
    private void avoidObs(int i) {
    	if (BV.distance(p, ((Obstacle)obs.get(i)).getp()) < 20) {
        	Point obs2Self 	= BV.vectorSub( p, ((Obstacle)obs.get(i)).getp() );
        	vectorObsAvoid 	= BV.vectorAdd( vectorObsAvoid, 	BV.scaleObs(obs2Self));
    	}
    }
    
//  obsAvoid
    private void avoidLines(int i) {
        int d = lines.get(i).getDistance(p.x, p.y); 
        if (d < 20) {
            vectorObsAvoid = BV.vectorAdd( vectorObsAvoid, BV.scaleObs( BV.scalarMult(2*d, lines.get(i).getNormV(p.x, p.y))));
        }
    }
    
    //Find Food
    private void findFood(int i) {
    	if (BV.distance(p, food.get(i).getp()) < 50) {
    	    
        	Point self2Food = BV.vectorSub( food.get(i).getp(), p );
            
            // get vector to nearest foodsource
        	if (BV.norm(vectorFood) > BV.norm(self2Food) || (vectorFood.x==0 && vectorFood.y==0)) 
        	    vectorFood =  self2Food;
        	
        	// eat and begin trail laying
            if (BV.distance(p, food.get(i).getp()) < 25) {
        	    food.get(i).decrease(10);
        	    hunger = hunger - 1000;
                speed += generator.nextInt(3);
                layTrail = trailLength;
                lastFood = food.get(i).getp();
        	}
    	}
    }

    //Find Food
    private void findTrail() {
        int minDist = Integer.MAX_VALUE, tmpd=0,
            rightPheromone = -1;
        // for all Pheromone
        for( int i=0; i<trail.size(); i++ )
            // if in the near of an trail AND no food in sight
            if ( (BV.distance(p, trail.get(i).getp()) < 30) ) 
                if( (tmpd=trail.get(i).getDistance()) < minDist ){
                    rightPheromone = i;
                    minDist = tmpd;
                }
        if (rightPheromone!=-1)
            vectorFood = BV.vectorSub( trail.get( rightPheromone ).getp(), p );
    }
            
            
    
    private void updateVectorVeloMatching () {
    	if (dist < 30) {
    		vectorVeloMatching = BV.vectorAdd( vectorVeloMatching, BV.dirSpeedToVector(tmpBoid.getDirection(), tmpBoid.getSpeed()) );
    		speedsum += tmpBoid.getSpeed();
    		k++;
    	}
    }
    
    private void setDirection () {
    	
    	int energy = 100;
    	
    	// norm swarmvector
    	if (centercount > 1)
    	    vectorSwarmCenter = BV.scalarMult((double)(3/centercount), vectorSwarmCenter);
    	/*
    	float norm = BV.norm(vectorObsAvoid);
    	if (norm < energy) energy -= Math.round(norm);
    	else if (energy > 0) {
    	    vectorObsAvoid = BV.scalarMult( (double)(100/energy), vectorObsAvoid );
    	    energy = 0;
    		}
    		else vectorObsAvoid = new Point (0,0);
    	*/
    	
    	float norm = BV.norm(vectorCollisionAvoid);
    	if (norm < energy) energy -= Math.round(norm);
       	else if (energy > 0) {
       	    BV.scalarMult( (double) 100/energy, vectorCollisionAvoid);
       	    energy = 0;
       	}
       		else vectorCollisionAvoid = new Point (0,0);
 	
    	norm = BV.norm(vectorSwarmCenter);
    	if (norm < energy) energy -= Math.round(norm);
       	else if (energy > 0) {
       	    BV.scalarMult( (double) 100/energy, vectorSwarmCenter);
       	    energy = 0;
       	}
       		else vectorSwarmCenter = new Point (0,0);
    	

    	norm = BV.norm(vectorVeloMatching);
    	if (norm < energy) energy -= Math.round(norm);
       	else if (energy > 0) {
       	    BV.scalarMult( (double) 100/energy, vectorVeloMatching);
       	    energy = 0;
       	}
       		else vectorVeloMatching = new Point (0,0);
    	
    	norm = BV.norm(currentVector);
    	if (norm < energy) energy -= Math.round(norm);
    	else if (energy > 0) {
       	    BV.scalarMult( (double) 100/energy, currentVector);
       	    energy = 0;
       	}
       		else currentVector = new Point (0,0);
    	
    	//:TODO: vectorFood not in energy
    	
    	direction = BV.vectorAngle( BV.vectorAdd(currentVector, 	vectorVeloMatching, 
    	        								 vectorSwarmCenter, vectorCollisionAvoid,
    	        								 vectorObsAvoid,    vectorFood) );
    }

    // repaint section
    //private int[] polyx, polyy;
    
    private void calculateCoordinates () {
    	double compx = Math.sin(direction*Math.PI/180);
    	double compy = Math.cos(direction*Math.PI/180);
    	p.x += Math.round(speed*compx);
    	p.y += Math.round(speed*compy);
    	if(p.x>width+padding || p.x<-padding || p.y>height+padding || p.y<-padding)
    		direction += 180;
	
	// :TODO: add shapeRotation !?
    	//polyx = new int[] {p.x, p.x-2, p.x, p.x+2};
    	//polyy = new int[] {p.y+2, p.y-2, p.y-1, p.y-2};
    }

    public void paint ( Graphics g ) {
    	calculateCoordinates();
    	g.setColor(color);
    	g.fillOval(p.x, p.y, 2, 2);//g.fillPolygon(polyx, polyy, 4);
    	g.setColor(Color.white);
    }
    
    public int getSpeed() {
    	return speed;
    }
    
    public Point getP () {
    	return p;
    }

    public int getDirection() {
    	return direction;
    }
    
    public boolean getRace() {
        return race;
    }

    public boolean getDeath() {
        return hunger > 3*hungerThres;
    }

}
