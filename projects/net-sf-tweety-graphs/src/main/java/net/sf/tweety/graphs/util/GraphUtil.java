/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.graphs.util;

import java.util.*;

import Jama.EigenvalueDecomposition;
import net.sf.tweety.commons.util.MapTools;
import net.sf.tweety.commons.util.Pair;
import net.sf.tweety.graphs.DirectedEdge;
import net.sf.tweety.graphs.Graph;
import net.sf.tweety.graphs.Node;
import net.sf.tweety.graphs.UndirectedEdge;
import net.sf.tweety.math.ComplexNumber;
import net.sf.tweety.math.matrix.Matrix;

/**
 * This abstract class contains some auxiliary methods for working
 * with graphs.
 * 
 * @author Matthias Thimm
 */
public abstract class GraphUtil {

	/** For archiving page rank values. */
	private static Map<Graph<? extends Node>,Map<Double,Map<Double,Map<Node,Double>>>> archivePageRank = new HashMap<Graph<? extends Node>,Map<Double,Map<Double,Map<Node,Double>>>>();
	
	/** For archiving HITS rank values. */
	private static Map<Graph<? extends Node>,Map<Double,Map<Node,Double>>> archiveHITSAuthRank = new HashMap<Graph<? extends Node>,Map<Double,Map<Node,Double>>>();
	private static Map<Graph<? extends Node>,Map<Double,Map<Node,Double>>> archiveHITSHubRank = new HashMap<Graph<? extends Node>,Map<Double,Map<Node,Double>>>();
	
	/**
	 * Computes the PageRank of the given node in the given graph.
	 * @param g a graph
	 * @param n a node
	 * @param dampingFactor the damping factor for PageRank
	 * @param precision the precision (smaller values mean higher precision)
	 * @return the PageRank of the given node in the given graph.
	 */
	public static Double pageRank(Graph<? extends Node> g, Node n, double dampingFactor, double precision){
		if(GraphUtil.archivePageRank.containsKey(g) &&
				GraphUtil.archivePageRank.get(g).containsKey(dampingFactor) &&
				GraphUtil.archivePageRank.get(g).get(dampingFactor).containsKey(precision))
			return GraphUtil.archivePageRank.get(g).get(dampingFactor).get(precision).get(n);
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
			maxDiff = 0;
			pageRanks_tmp = new HashMap<Node,Double>();			
			for(Node v: g){
				sum = 0;
				for(Node w: g.getParents(v)){
					sum += pageRanks.get(w)/g.getChildren(w).size();
				}
				for(Node w: sinks)
					sum += pageRanks.get(w)/m;
				pageRanks_tmp.put(v, ((1-dampingFactor)/m) + (dampingFactor * sum));						
				maxDiff = Math.max(maxDiff, Math.abs(pageRanks.get(v)-pageRanks_tmp.get(v)));				
			}
			pageRanks = pageRanks_tmp;			
		}while(maxDiff > precision);		
		if(!GraphUtil.archivePageRank.containsKey(g))
			GraphUtil.archivePageRank.put(g, new HashMap<Double,Map<Double,Map<Node,Double>>>());
		if(!GraphUtil.archivePageRank.get(g).containsKey(dampingFactor))
			GraphUtil.archivePageRank.get(g).put(dampingFactor, new HashMap<Double,Map<Node,Double>>());
		GraphUtil.archivePageRank.get(g).get(dampingFactor).put(precision, pageRanks);		
		return pageRanks.get(n);
	}
	
	/**
	 * Computes the HITS rank of the given node in the given graph.
	 * @param g a graph
	 * @param n a node
	 * @param precision the precision (smaller values mean higher precision)
	 * @return the HITS rank of the given node in the given graph.
	 */
	public static Double hitsRank(Graph<? extends Node> g, Node n, double precision, boolean getAuth){
		if(getAuth){
			if(GraphUtil.archiveHITSAuthRank.containsKey(g) &&
					GraphUtil.archiveHITSAuthRank.get(g).containsKey(precision))
				return GraphUtil.archiveHITSAuthRank.get(g).get(precision).get(n);
		}else{
			if(GraphUtil.archiveHITSHubRank.containsKey(g) &&
					GraphUtil.archiveHITSHubRank.get(g).containsKey(precision))
				return GraphUtil.archiveHITSHubRank.get(g).get(precision).get(n);
		}
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
				auth_tmp.put(v, sum);
				norm += Math.pow(sum, 2);
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
				hub_tmp.put(v, sum);
				norm += Math.pow(sum, 2);
			}
			norm = Math.sqrt(norm);
			for(Node v: g){
				hub_tmp.put(v, hub_tmp.get(v) / norm);
				maxDiff = Math.max(maxDiff, Math.abs(hub.get(v)-hub_tmp.get(v)));
			}
			auth = auth_tmp;
			hub = hub_tmp;			
		}while(maxDiff > precision);	
		if(!GraphUtil.archiveHITSHubRank.containsKey(g))
			GraphUtil.archiveHITSHubRank.put(g, new HashMap<Double,Map<Node,Double>>());		
		GraphUtil.archiveHITSHubRank.get(g).put(precision,hub);		
		if(!GraphUtil.archiveHITSAuthRank.containsKey(g))
			GraphUtil.archiveHITSAuthRank.put(g, new HashMap<Double,Map<Node,Double>>());		
		GraphUtil.archiveHITSAuthRank.get(g).put(precision,auth);
		return getAuth ? auth.get(n) : hub.get(n);		
	}
	
	/**
	 * Computes the (real parts of the) Eigenvalues of the given graph.
	 * @param g some graph
	 * @return an array of double (the real parts of the Eigenvalues).
	 */
	public static ComplexNumber[] eigenvalues(Graph<? extends Node> g){
		Matrix m = g.getAdjancyMatrix();
		EigenvalueDecomposition ed = new EigenvalueDecomposition(m.getJamaMatrix());		
		ComplexNumber[] result = new ComplexNumber[ed.getRealEigenvalues().length];
		for(int i = 0; i < ed.getImagEigenvalues().length; i++){
			result[i] = new ComplexNumber(ed.getRealEigenvalues()[i], ed.getImagEigenvalues()[i]);
		}			
		return result;
	}
	
	/**
	 * Checks whether the two graphs are isomorphic.
	 * @param g1 some graph.
	 * @param g2 some graph.
	 * @return "true" iff the two graphs are isomorphic.
	 */
	public static boolean isIsomorphic(Graph<? extends Node> g1, Graph<? extends Node> g2){
		// NOTE: we simply try out every possible permutation (note that this is an NP-hard problem anyway)
		MapTools<Node, Node> mapTools = new MapTools<Node,Node>();
		for(Map<Node,Node> isomorphism: mapTools.allBijections(new HashSet<Node>(g1.getNodes()), new HashSet<Node>(g2.getNodes()))){
			boolean isomorphic = true;
			for(Node a: g1){
				for(Node b: g1.getChildren(a)){
					if(!g2.getChildren(isomorphism.get(a)).contains(isomorphism.get(b))){
						isomorphic = false;
						break;
					}
				}
				if(!isomorphic)
					break;
			}
			if(isomorphic)
				return true;
		}		
		return false;
	}
	
	/**
	 * Returns the (undirected) diameter of the graph, i.e. the longest shortest
	 * path in the undirected version of the graph. 
	 * @param g some graph
	 * @return the (undirected) diameter of the graph
	 */
	@SuppressWarnings("unchecked")
	public static <T extends Node> int undirecteddiameter(Graph<T> g){
		// we use the Floyd-Warshall algorithm for that
		Map<T,Integer> node2ids = new HashMap<T,Integer>();
		Node[] ids2Nodes = new Node[g.getNumberOfNodes()];
		int idx =0;
		for(Node n: g){
			ids2Nodes[idx] = n;
			node2ids.put((T)n, idx);
			idx++;
		}
		int[][] d = new int[g.getNumberOfNodes()][g.getNumberOfNodes()];
		for(int i = 0; i < g.getNumberOfNodes(); i++)
			d[i][i] = 0;
		for(int i = 0; i < g.getNumberOfNodes(); i++)
			for(int j = i+1; j < g.getNumberOfNodes(); j++)
				if(g.areAdjacent((T)ids2Nodes[i], (T)ids2Nodes[j]) || g.areAdjacent((T)ids2Nodes[j], (T)ids2Nodes[i])){
					d[i][j] = 1;
					d[j][i] = 1;
				}
				else{
					d[i][j] = Integer.MAX_VALUE;
					d[j][i] = Integer.MAX_VALUE;
				}
		for(int k =0; k < g.getNumberOfNodes(); k++)
			for(int i =0; i < g.getNumberOfNodes(); i++)
				for(int j =0; j < g.getNumberOfNodes(); j++)
					if(d[i][k] < Integer.MAX_VALUE && d[k][j] < Integer.MAX_VALUE && d[i][k] + d[k][j] < d[i][j]){
						d[i][j] = d[i][k] + d[k][j];
						d[j][i] = d[i][j];
					}
		int maximum = 0;
		for(int i = 0; i < g.getNumberOfNodes(); i++)
			for(int j = i+1; j < g.getNumberOfNodes(); j++)
				if(d[i][j]>maximum)
					maximum = d[i][j];
	    return maximum;
	}

	/**
	 * Returns the global clustering coefficient of the graph (if it is directed it is interpreted
	 * as an undirected version).
	 * @param g
	 * @return
	 */
	public static <T extends Node> double globalclusteringcoefficient(Graph<T> g){
		double numClosedTriplets = 0;
		double numTriplets = 0;
		byte numEdges;
		for(T a: g){
			for(T b: g){
				if(b.equals(a))
					continue;
				for(T c:g){
					if(c.equals(a)|| c.equals(b))
						continue;
					numEdges = 0;
					if(g.areAdjacent(a, b) || g.areAdjacent(b, a))
						numEdges++;
					if(g.areAdjacent(a, c) || g.areAdjacent(c, a))
						numEdges++;
					if(g.areAdjacent(b, c) || g.areAdjacent(c, b))
						numEdges++;
					// note that we count every set {a,b,c} six times
					if(numEdges >= 2)
						numTriplets += 1d/6;
					if(numEdges == 3)
						numClosedTriplets += 1d/6;
				}	
			}
		}
		return numClosedTriplets/numTriplets;
	}	
	
	/**
	 * Enumerates all chordless circuits of the given graph, i.e. all circuits a1,...,an
	 * where there is no edge connecting any ak with aj unless k=j+1 or k=j-1. The algorithm 
	 * of this method is adapted from [Bisdorff, On enumerating chordless circuits in directed graphs, 2010].
	 * @param g
	 * @return
	 */
	public static <T extends Node> Collection<List<T>> enumerateChordlessCircuits(Graph<T> g){
		Collection<List<T>> ccircuits = new HashSet<List<T>>();
		Collection<UndirectedEdge<T>> visitedLEdges = new HashSet<UndirectedEdge<T>>();
		Stack<Pair<List<T>,T>> stack = new Stack<Pair<List<T>,T>>();
		for(T v_init: g.getNodes()) {
			List<T> p_init = new LinkedList<T>();
			p_init.add(v_init);
			stack.push(new Pair<List<T>,T>(p_init,v_init));
			while(!stack.isEmpty()){
				Pair<List<T>,T> elem = stack.pop();
				List<T> p = elem.getFirst();
				T vk = elem.getSecond();
				T vkm1 = p.get(p.size()-1);
				visitedLEdges.add(new UndirectedEdge<T>(vkm1,vk));
				if(g.contains(new DirectedEdge<T>(vkm1,vk)) && !g.contains(new DirectedEdge<T>(vk,vkm1))) 
					ccircuits.add(p);
				else {
					Stack<T> n = new Stack<T>();
					for(T w: g.getChildren(vkm1))
						if(!g.getChildren(w).contains(vkm1))
							n.push(w);
					while(!n.isEmpty()) {
						T v = n.pop();
						if(!visitedLEdges.contains(new UndirectedEdge<T>(vkm1,v))) {
							boolean noChord = true;							
							for(T x: p)
								if(!x.equals(vkm1)) {
									if(g.getChildren(x).contains(v)) {
										noChord = false;
										break;
									}
									if(!x.equals(vk)) {
										if(g.getChildren(v).contains(x)) {
											noChord = false;
											break;
										}
									}							
								}
							if(noChord) {
								List<T> p_current = new LinkedList<T>();
								p_current.addAll(p);
								p_current.add(v);
								stack.push(new Pair<List<T>,T>(p_current,vk));
							}
						}
					}
				}
			}
				
		}
		return ccircuits;
	}		
}
