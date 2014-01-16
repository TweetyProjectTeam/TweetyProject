package net.sf.tweety.swarms.boids;

import java.awt.Point;

public class BV {
	
	public static Point dirSpeedToVector(int direction, int speed) {
    	Point p = new Point();
    	double compx = Math.sin(direction*Math.PI/180);
    	double compy = Math.cos(direction*Math.PI/180);
    	p.x = Math.round(Math.round(speed*compx));
    	p.y = Math.round(Math.round(speed*compy));
    	return p;
    } // end dirSpeedToVector
	
	public static Point vectorAdd(Point p1, Point p2) {
		return new Point(p1.x + p2.x, p1.y + p2.y);
	}

	public static Point vectorAdd(Point p1, Point p2, Point p3, Point p4, Point p5, Point p6) {
		return new Point(p1.x+p2.x+p3.x+p4.x+p5.x+p6.x, p1.y+p2.y+p3.y+p4.y+p5.y+p6.y);
	}
	
	public static Point vectorAdd(Point p1, Point p2, Point p3) {
		return new Point(p1.x+p2.x+p3.x, p1.y+p2.y+p3.y);
	}
	
	public static Point vectorSub(Point p1, Point p2) {
		return new Point(p1.x - p2.x, p1.y - p2.y);
	}
	
    public static float distance(Point p1, Point p2) {
    	return Math.round(Math.sqrt(Math.pow(p1.x - p2.x, 2) + Math.pow(p1.y - p2.y, 2) ));
    }
    
    public static Point scaleAntiDist(Point p) {
    	p.x = Math.round( (float)(500*p.x / Math.pow(norm(p), 3)) );
    	p.y = Math.round( (float)(500*p.y / Math.pow(norm(p), 3)) );
    	return p;
    }

    public static Point scaleObs(Point p) {
    	p.x = Math.round( (float)(1000*p.x / Math.pow(norm(p), 2)) );
    	p.y = Math.round( (float)(1000*p.y / Math.pow(norm(p), 2)) );
    	return p;
    }
    
    public static Point scaleWithDist(Point p) {
    	p.x = Math.round( (float)(p.x * norm(p)) );
    	p.y = Math.round( (float)(p.y * norm(p)) );
    	return p;
    }
    
    public static float norm(Point p) {
    	return (float)Math.sqrt( Math.pow(p.x, 2) + Math.pow(p.y,2) );
    }

    public static int vectorAngle(Point p) {
    	//gets the angle of the vector with the x axis
    	return (Math.round((float)Math.toDegrees(Math.atan2(p.x, p.y))));
	}
    
    public static int vectorAngle(int x, int y) {
        //gets the angle of the vector with the x axis
        return (Math.round((float)Math.toDegrees(Math.atan2(x, y))));
    }
    
    public static Point scalarMult (int s, Point v) {
    	v.x = v.x*s;
    	v.y = v.y*s;
    	return v;
    }
    
    public static Point scalarMult (double s, Point v) {
        v.x = Math.round(Math.round(v.x*s));
    	v.y = Math.round(Math.round(v.y*s));
    	return v;
    }
}
