/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
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
package net.sf.tweety.arg.dung.test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import net.sf.tweety.arg.dung.*;
import net.sf.tweety.arg.dung.semantics.*;
import net.sf.tweety.arg.dung.syntax.*;
import net.sf.tweety.arg.dung.util.DungTheoryGenerator;
import net.sf.tweety.arg.dung.util.IsoSafeEnumeratingDungTheoryGenerator;
import net.sf.tweety.graphs.Graph;
import net.sf.tweety.graphs.Node;
import net.sf.tweety.graphs.util.GraphUtil;
import net.sf.tweety.math.ComplexNumber;

public class AnalysisTest {

	public static void EigenvalueTest(){
		DungTheoryGenerator gen = new IsoSafeEnumeratingDungTheoryGenerator();
		StableReasoner reasoner;
		// 1: theory
		// 2: number of stable extensions
		// 3: number of Eigenvalues
		// 4: actual Eigenvalues
		
		for(int i = 0; i < 40; i++){
			DungTheory theory = gen.generate();
			reasoner = new StableReasoner(theory);			
			
			ComplexNumber[] eigenvalues = GraphUtil.eigenvalues(theory);
			System.out.print(theory + "\t" +
					reasoner.getExtensions().size() + "\t" +
					eigenvalues.length + "\t");			 
			for(ComplexNumber n: eigenvalues){
				System.out.print(n +"\t");
			}
			System.out.println();
		}
	}

	// ========================================================================================================================================================================================================
	
	public static void PageRankTest(){
		DungTheoryGenerator gen = new IsoSafeEnumeratingDungTheoryGenerator();
		GroundReasoner reasoner;
		for(int i = 0; i < 800; i++){
			DungTheory theory = gen.generate();
			// skip theories with selfloops for now
			if(theory.hasSelfLoops()) continue;
			reasoner = new GroundReasoner(theory);
			Labeling lab = new Labeling(theory,reasoner.getExtensions().iterator().next());
			
			DungTheory invertedTheory = theory;//.getComplementGraph(Graph.IGNORE_SELFLOOPS);
			//System.out.println(theory + "\t\t" + invertedTheory);
			for(Argument arg: invertedTheory){
				for(Argument arg2: invertedTheory)
					if(lab.get(arg).equals(ArgumentStatus.IN) && lab.get(arg2).equals(ArgumentStatus.OUT)){
						if( GraphUtil.pageRank(invertedTheory, arg, 0.75, 0.0001) > GraphUtil.pageRank(invertedTheory, arg2, 0.75, 0.0001)){
							System.out.print(".");
						}else{
							System.out.println(".");
							System.out.println(theory + "\t\t" + invertedTheory);
							System.out.println(lab.get(arg) + "\t" + GraphUtil.pageRank(invertedTheory, arg, 0.75, 0.0001));
							System.out.println(lab.get(arg2) + "\t" + GraphUtil.pageRank(invertedTheory, arg2, 0.75, 0.0001));
						}
					}
					
				//System.out.println(lab.get(arg) + "\t" + GraphUtil.pageRank(invertedTheory, arg, 0.75, 0.001));
			}
			//System.out.println();
			
		}
	}

	// ========================================================================================================================================================================================================
	
	public static void PageRankTest2(){
		GroundReasoner reasoner;
		for(int i = 0; i < 100; i++){
			DungTheory theory = new DungTheory();
			Argument prev = new Argument("A0");
			theory.add(prev);
			for(int j = 1; j <= i; j++){
				Argument next = new Argument("A"+j);
				theory.add(next);
				theory.add(new Attack(prev,next));
				prev = next;
			}
			
			reasoner = new GroundReasoner(theory);
			Labeling lab = new Labeling(theory,reasoner.getExtensions().iterator().next());
			
			DungTheory invertedTheory = theory.getComplementGraph(Graph.IGNORE_SELFLOOPS);
			System.out.println(theory + "\t\t" + invertedTheory);
			for(Argument arg: invertedTheory){
				System.out.println(arg + "\t" + lab.get(arg) + "\t" + GraphUtil.hitsRank(invertedTheory, arg, 0.0001, true) + "\t" + GraphUtil.hitsRank(invertedTheory, arg, 0.0001, false));  //GraphUtil.pageRank(invertedTheory, arg, 0.75, 0.0001));				
			}
			System.out.println();
			
		}
	}
	
	// ========================================================================================================================================================================================================
	
	/**
	 * Inverted PageRank
	 */
	public static Double pageRankInverted(Graph<? extends Node> g, Node n, double dampingFactor, double precision){
		Map<Node,Double> pageRanks = new HashMap<Node,Double>();
		// init
		double m = g.getNodes().size();
		Set<Node> sinks = new HashSet<Node>();
		for(Node v: g){
			pageRanks.put(v,1/m);
			if(g.getChildren(v).isEmpty())
				sinks.add(v);
		}
		// iterate
		double maxDiff;
		double sum;
		Map<Node,Double> pageRanks_tmp;		
		do{			
	//		for(int i = 0; i < m; i++)
		//		System.out.print("A"+i + " = " + pageRanks.get(new Argument("A"+i)) + "\t\t");
			//System.out.println();
			maxDiff = 0;
			pageRanks_tmp = new HashMap<Node,Double>();			
			for(Node v: g){
				sum = 0;
				for(Node w: g.getParents(v)){
					sum += pageRanks.get(w) * 1/g.getChildren(w).size();
				}
				//for(Node w: sinks)
				//	sum += pageRanks.get(w)/m;
				pageRanks_tmp.put(v, ((1-dampingFactor)/m) + (dampingFactor * (1-sum)));						
				maxDiff = Math.max(maxDiff, Math.abs(pageRanks.get(v)-pageRanks_tmp.get(v)));				
			}
			pageRanks = pageRanks_tmp;			
		}while(maxDiff > precision);		
		return pageRanks.get(n);
	}
	
	// ========================================================================================================================================================================================================
	
	public static void InvertedPageRankTest(){
		
		DungTheory theory = new DungTheory();
		Argument prev = new Argument("A0");
		theory.add(prev);
		for(int j = 1; j <= 5; j++){
			Argument next = new Argument("A"+j);
			theory.add(next);
			theory.add(new Attack(prev,next));
			prev = next;
		}
		AnalysisTest.pageRankInverted(theory, prev, 0.99, 0.01);		
	}

	// ========================================================================================================================================================================================================
	
	public static void InvertedPageRankTest2(){
		DungTheoryGenerator gen = new IsoSafeEnumeratingDungTheoryGenerator();
		GroundReasoner reasoner;
		for(int i = 0; i < 30; i++){
			DungTheory theory = gen.generate();
			// skip theories with selfloops for now
			if(theory.hasSelfLoops()) continue;
			reasoner = new GroundReasoner(theory);
			Labeling lab = new Labeling(theory,reasoner.getExtensions().iterator().next());
			System.out.println(theory);
			for(Argument arg: theory){
				System.out.print(arg + " " + lab.get(arg) + " " + AnalysisTest.pageRankInverted(theory, arg, 0.99, 0.01) + "\t");
			}
			System.out.println();
			System.out.println("===============================================================================");
			
		}		
	}
	
	// ========================================================================================================================================================================================================
	
	/*
	 * Inverted HITS rank
	 */
	public static Double hitsRankInverted(Graph<? extends Node> g, Node n, double precision, boolean getAuth){
		Map<Node,Double> auth = new HashMap<Node,Double>();
		Map<Node,Double> hub = new HashMap<Node,Double>();
		// init
		for(Node v: g){
			auth.put(v,1d);
			hub.put(v,1d);			
		}
		// iterate
		double maxDiff;
		double sum;
		double norm;
		Map<Node,Double> auth_tmp, hub_tmp;		
		do{						
			maxDiff = 0;
			norm = 0;
			auth_tmp = new HashMap<Node,Double>();
			for(Node v: g){
				sum = 0;
				for(Node w: g.getParents(v))
					sum += hub.get(w);
				auth_tmp.put(v, 1-sum);
				norm += Math.pow(1-sum, 2);
			}
			norm = Math.sqrt(norm);
			for(Node v: g){
				auth_tmp.put(v, auth_tmp.get(v) / norm);
				maxDiff = Math.max(maxDiff, Math.abs(auth.get(v)-auth_tmp.get(v)));
			}
			norm = 0;
			hub_tmp = new HashMap<Node,Double>();
			for(Node v: g){
				sum = 0;
				for(Node w: g.getChildren(v))
					sum += auth.get(w);
				hub_tmp.put(v, 1-sum);
				norm += Math.pow(1-sum, 2);
			}
			norm = Math.sqrt(norm);
			for(Node v: g){
				hub_tmp.put(v, hub_tmp.get(v) / norm);
				maxDiff = Math.max(maxDiff, Math.abs(hub.get(v)-hub_tmp.get(v)));
			}
			auth = auth_tmp;
			hub = hub_tmp;			
		}while(maxDiff > precision);	
		return getAuth ? auth.get(n) : hub.get(n);		
	}
	
	// ========================================================================================================================================================================================================
	
	public static void InvertedHITSRankTest(){
		DungTheoryGenerator gen = new IsoSafeEnumeratingDungTheoryGenerator();
		GroundReasoner reasoner;
		for(int i = 0; i < 40; i++){
			DungTheory theory = gen.generate();
			// skip theories with selfloops for now
			if(theory.hasSelfLoops()) continue;
			reasoner = new GroundReasoner(theory);
			Labeling lab = new Labeling(theory,reasoner.getExtensions().iterator().next());
			System.out.println(theory);
			for(Argument arg: theory){
				System.out.print(arg + " " + lab.get(arg) + " " + AnalysisTest.hitsRankInverted(theory, arg, 0.01, true) + " " + AnalysisTest.hitsRankInverted(theory, arg, 0.01, false) + "\t");
			}
			System.out.println();
			System.out.println("===============================================================================");
			
		}		
	}
	
	// ========================================================================================================================================================================================================
	
	public static void main(String[] args){
		//AnalysisTest.EigenvalueTest();
		//AnalysisTest.PageRankTest();
		//AnalysisTest.PageRankTest2();
		//AnalysisTest.InvertedPageRankTest();
		//AnalysisTest.InvertedPageRankTest2();
		AnalysisTest.InvertedHITSRankTest();
	}
}


