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
package net.sf.tweety.arg.adf.syntax;

import java.util.Collection;

import net.sf.tweety.commons.BeliefSet;
import net.sf.tweety.commons.Signature;
import net.sf.tweety.graphs.Edge;
import net.sf.tweety.graphs.Graph;
import net.sf.tweety.graphs.Node;
import net.sf.tweety.math.matrix.Matrix;

/**
 * This class implements abstract dialectical frameworks, cf.
 * [Brewka, Ellmauthaler, Strass, Wallner, Woltran. Abstract Dialectical Frameworks Revisited. IJCAI'13]
 * 
 * @author Matthias Thimm
 */
public class AbstractDialecticalFramework extends BeliefSet<Argument> implements Graph<Argument>, Comparable<AbstractDialecticalFramework> {

	@Override
	public int compareTo(AbstractDialecticalFramework o) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean add(Edge<Argument> edge) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Collection<Argument> getNodes() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getNumberOfNodes() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean areAdjacent(Argument a, Argument b) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Edge<Argument> getEdge(Argument a, Argument b) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<? extends Edge<? extends Argument>> getEdges() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Argument> getChildren(Node node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Argument> getParents(Node node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean existsDirectedPath(Argument node1, Argument node2) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Collection<Argument> getNeighbors(Argument node) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Matrix getAdjancyMatrix() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Graph<Argument> getComplementGraph(int selfloops) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Collection<Argument>> getStronglyConnectedComponents() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<Graph<Argument>> getSubgraphs() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Graph<Argument> getRestriction(Collection<Argument> nodes) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean hasSelfLoops() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isWeightedGraph() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Signature getSignature() {
		// TODO Auto-generated method stub
		return null;
	}
	
}
