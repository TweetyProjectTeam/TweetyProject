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
 *  Copyright 2024 The TweetyProject Team <http://tweetyproject.org/contact/>
 */

package org.tweetyproject.arg.setaf.syntax;

import org.tweetyproject.arg.dung.semantics.ArgumentStatus;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.ArgumentationFramework;
import org.tweetyproject.commons.BeliefSet;
import org.tweetyproject.commons.Signature;
import org.tweetyproject.commons.util.SetTools;
import org.tweetyproject.graphs.*;
import org.tweetyproject.math.matrix.Matrix;

import java.util.*;


/**
 * This class implements a SetAF in the sense of Nielson/Parson:
 * <br>
 * @see "Nielsen, Soren Holbech and Parsons, Simon. A Generalization of Dung's Abstract Framework for Argumentation: Arguing with Sets of Attacking Arguments.
 * Argumentation in Multi-Agent Systems: Third International Workshop, ArgMAS 2006."
 *
 *
 * @author Sebastian Franke
 * @author Lars Bengel
 */
public class SetAf extends BeliefSet<Argument,SetAfSignature> implements DirHyperGraph<Argument>, Comparable<SetAf>, ArgumentationFramework<Argument> {

    /** The set of nodes */
    protected Set<Argument> nodes;

    /** The set of edges */
    protected Set<SetAttack> edges;

    /**
     * Initializes a new enpty SetAf
     */
    public SetAf(){
        this.nodes = new HashSet<>();
        this.edges = new HashSet<>();
    }

    /**
     * For archiving sub DirHyperGraphs
     */
    private static Map<SetAf, Collection<DirHyperGraph<Argument>>> archivedSubgraphs = new HashMap<SetAf, Collection<DirHyperGraph<Argument>>>();

    @Override
    public int size(){
        return this.nodes.size();
    }
    @Override
    public Iterator<Argument> iterator(){
        return this.nodes.iterator();
    }

    /**
     * Creates a new theory from the given DirHyperGraph.
     * @param graph some DirHyperGraph
     */
    public SetAf(SetAf graph){
        super(graph.getNodes());
        this.nodes = new HashSet<>(graph.getNodes());
        this.edges = new HashSet<>();
        for(Object e: graph.getEdges()) {
            this.edges.add((SetAttack) e);
        }
    }

    /**
     * clones the SetAf
     * @return a SetAf
     */
    public SetAf clone() {
        SetAf result = new SetAf(this);
        return result;
    }

    /* (non-Javadoc)
     * @see org.tweetyproject.kr.BeliefBase#getSignature()
     */
    public Signature getMinimalSignature(){
        return new SetAfSignature(this);
    }

    /**
     * returns true if <code>arguments</code> attack all other arguments in the theory
     * @param ext An extension contains a set of arguments.
     * @return true if <code>arguments</code> attack all other arguments in the theory
     */
    public boolean isAttackingAllOtherArguments(Extension<SetAf> ext){
        Collection<Argument> other = new HashSet<>(this.nodes);
        other.removeAll(ext);
        other.removeAll(getAttacked(ext));
        return other.isEmpty();
    }

    /**
     * returns true if one arguments in <code>arguments</code> attacks another within this attack relation.
     * @param arguments a list of arguments
     * @return returns true if one arguments in <code>arguments</code> attacks another.
     */
    public boolean isConflictFree(Extension<SetAf> arguments){
        for (SetAttack att : this.edges) {
            if (arguments.contains(att.getNodeB()) && arguments.containsAll(att.getNodeA())) {
                return false;
            }
        }
        return true;
    }

    /**
     * returns true if every attacker on <code>argument</code> is attacked by some
     * accepted argument wrt. the given theory.
     * @param argument an argument
     * @param ext an extension (the knowledge base)
     * @return true if every attacker on <code>argument</code> is attacked by some
     * accepted argument wrt. the given theory.
     */
    public boolean isAcceptable(Argument argument, Extension<SetAf> ext){
        Collection<Argument> attacked = getAttacked(ext);
        Collection<Collection<Argument>> attackers = getAttackers(argument);
        for (Collection<Argument> attacker: attackers) {
            boolean isAttacked = false;
            for (Argument arg: attacker) {
                if (attacked.contains(arg)) {
                    isAttacked = true;
                    break;
                }
            }
            if (!isAttacked) return false;
        }
        return true;
    }

    /**
     * returns true if every accepted argument of this is defended by some accepted
     * argument wrt. the given SetAf theory.
     * @param ext an extension.
     * @return true if every accepted argument of this is defended by some accepted
     * argument wrt. the given SetAf theory.
     */
    public boolean isAdmissible(Extension<SetAf> ext){
        if(!this.isConflictFree(ext)) return false;
        for (Argument argument : ext.getArgumentsOfStatus(ArgumentStatus.IN)) {
            if (!this.isAcceptable(argument, ext))
                return false;
        }
        return true;
    }

    /**
     * Computes the set {A | (A,argument) in attacks}.
     * @param node an argument
     * @return the set of all sets of arguments that attack <code>argument</code>.
     */
    public Collection<Collection<Argument>> getAttackers(Argument node){
        Collection<Collection<Argument>> s = new HashSet<>();
        for(SetAttack e : this.edges) {
            if(e.getNodeB().equals(node))
                s.add(e.getNodeA());
        }
        return s;
    }

    /**
     * Computes the set of arguments collectively attacked by <code>arguments</code>.
     * @param arguments a set of arguments
     * @return set of arguments collectively attacked by <code>arguments</code>
     */
    public Collection<Argument> getAttacked(Collection<Argument> arguments) {
        Collection<Argument> attacked = new HashSet<>();
        for (SetAttack att: this.edges) {
            if (arguments.containsAll(att.getNodeA())) {
                attacked.add(att.getNodeB());
            }
        }
        return attacked;
    }

    /**
     * returns true if some argument of <code>ext</code> attacks argument.
     * @param argument an argument
     * @param ext an extension, i.e. a set of arguments
     * @return true if some argument of <code>ext</code> attacks argument.
     */
    @SuppressWarnings("rawtypes")
    public boolean isAttacked(Argument argument, Extension<? extends ArgumentationFramework> ext){
        return this.isAttackedBy(argument, ext);
    }

    /**
     * returns true if some argument of <code>ext</code> is attacked by argument.
     * @param arg2 an argument
     * @param ext an extension, i.e. a set of arguments
     * @return true if some argument of <code>ext</code> is attacked by argument.
     */
    public boolean isAttackedBy(Argument arg2, Collection<Argument> ext){
        Collection<Argument> attacked = getAttacked(ext);
        return attacked.contains(arg2);
    }

    /**
     * Checks whether arg1 is attacked by arg2.
     * @param arg1 an argument.
     * @param arg2 an argument.
     * @return "true" if arg1 is attacked by arg2
     */
    public boolean isAttackedBy(Argument arg1, Argument arg2){
        for(SetAttack e: this.edges) {
            if(e.getNodeB().equals(arg1) && e.getNodeA().contains(arg2))
                return true;
        }
        return false;
    }

    /**
     * Checks whether the given extension is stable wrt. this theory.
     * @param ext some extension
     * @return "true" iff the extension is stable.
     */
    public boolean isStable(Extension<SetAf> ext) {
        if (!isAdmissible(ext)) return false;
        return isAttackingAllOtherArguments(ext);
    }

    /**
     * The characteristic function of an abstract argumentation framework: F_AF(S) = {A|A is acceptable wrt. S}.
     * @param extension an extension (a set of arguments).
     * @return an extension (a set of arguments).
     */
    public Extension<SetAf> faf(Extension<SetAf> extension){
        Extension<SetAf> newExtension = new Extension<SetAf>();
        for (Argument argument : this) {
            if (this.isAcceptable(argument, extension))
                newExtension.add(argument);
        }
        return newExtension;
    }

    // Misc methods

    /**
     * Add argument to SetAf
     * @param arg some argument
     * @return true if argument has been added
     */
    public boolean add(Argument arg){
        this.nodes.add(arg);
        return true;
    }

    /**
     * Adds an attack from the first argument to the second to this SetAf theory.
     * @param hashSet some arguments
     * @param attacked some argument
     * @return "true" if the set of attacks has been modified.
     */
    public boolean addAttack(HashSet<Argument> hashSet, Argument attacked){
        SetAttack s = new SetAttack(hashSet, attacked);
        this.edges.add(s);
        return true;
    }

    /**
     * Adds an attack from the first argument to the second to this SetAf theory.
     * @param attacker some argument
     * @param attacked some argument
     * @return "true" if the set of attacks has been modified.
     */
    public boolean addAttack(Argument attacker, Argument attacked){
        SetAttack s = new SetAttack(attacker, attacked);
        this.edges.add(s);
        return true;
    }

    /**
     * Removes the given attack from this SetAf theory.
     * @param attack an attack
     * @return "true" if the set of attacks has been modified.
     */
    public boolean remove(SetAttack attack){
        this.edges.remove(attack);
        return true;
    }

    /**
     * Removes the argument and all its attacks
     * @param a some argument
     * @return true if this structure has been changed
     */
    public boolean remove(Argument a){
        for (SetAttack e: this.edges) {
            e.remove(a);
            if(e.getNodeA().isEmpty() || e.getNodeB() == null) {
                this.edges.remove(e);
            }
        }
        this.nodes.remove(a);
        return true;
    }

    /* (non-Javadoc)
     * @see org.tweetyproject.commons.BeliefSet#removeAll(java.util.Collection)
     */
    public boolean removeAll(Collection<?> c){
        boolean result = true;
        for(Object a: c)
            if(a instanceof Argument)
                result |= this.remove((Argument)a);
            else if(a instanceof SetAttack)
                result |= this.remove((SetAttack)a);
        return result;
    }

    /* (non-Javadoc)
     * @see org.tweetyproject.kr.BeliefSet#containsAll(java.util.Collection)
     */
    @Override
    public boolean containsAll(Collection<?> c){
        for(Object o: c)
            if(!this.contains(o))
                return false;
        return true;
    }

    /**
     * Checks whether this theory contains the given attack.
     * @param att some attack
     * @return "true" iff this theory contains the given attack.
     */
    public boolean containsAttack(SetAttack att) {
        return this.edges.contains(att);
    }

    /**
     * Adds the set of attacks to this SetAf theory.
     * @param edges2 a collection of attacks
     * @return "true" if this SetAf theory has been modified.
     */
    public boolean addAllAttacks(Set<SetAttack> edges2){
        boolean result = false;
        for(SetAttack att: edges2)
            result |= this.add(att);
        return result;
    }

    /**
     * Adds all arguments and attacks of the given theory to
     * this theory
     * @param theory some SetAf theory
     * @return "true" if this SetAf Theory has been modified
     */
    public boolean add(SetAf theory){
        boolean b1 = this.addAll(theory);
        boolean b2 = this.addAllAttacks(theory.edges);
        return b1 || b2;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((this.edges == null) ? 0 : this.edges.hashCode());
        return result;
    }

    /* (non-Javadoc)
     * @see java.lang.Object#equals(java.lang.Object)
     */
    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (getClass() != obj.getClass())
            return false;
        if(!this.equals(obj))
            return false;
        return true;
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo(SetAf o) {
        // SetAfTheory implements Comparable in order to
        // have a fixed (but arbitrary) order among all theories
        // for that purpose we just use the hash code.
        return this.hashCode() - o.hashCode();
    }

    @Override
    protected SetAfSignature instantiateSignature() {
        return new SetAfSignature();
    }

    /**
     *
     * @param edge attack
     * @return whether the attack was added or not
     */
    public boolean add(SetAttack edge) {
        for(Argument e: edge.getNodeA())
            if(!this.nodes.contains(e))
                throw new IllegalArgumentException("The edge connects nodes that are not in this graph.");

        if (!this.nodes.contains(edge.getNodeB()))
            throw new IllegalArgumentException("The edge connects nodes that are not in this graph.");
        this.edges.add(edge);
        return true;
    }

    @Override
    public Collection<Argument> getNodes() {
        return this.nodes;
    }

    @Override
    public int getNumberOfNodes() {
        return this.nodes.size();
    }

    @Override
    public boolean areAdjacent(Argument a, Argument b) {
        for(HyperDirEdge<Argument> e : this.edges) {
            if((e.getNodeA().contains(a) && e.getNodeB().equals(b)) ||
                    (e.getNodeA().contains(b) && e.getNodeB().equals(a))) {
                return true;
            }
        }
        return false;
    }

    @Override
    public SetAttack getEdge(Argument a, Argument b) {
        System.err.println("an edge in a hypergraph is comprised of a set of Elements in Node A and an Element in Node B");
        return null;
    }

    /**
     *
     * @return all attacks
     */
    public Set<SetAttack> getAttacks() {
        return new HashSet<>(this.edges);
    }

    /**
     *
     * @param node1 an attacking set
     * @param b an argument
     * @return the edge between the two
     */
    public SetAttack getDirEdge(Set<Argument> node1, Node b) {
        for(SetAttack e : this.edges) {
            if(e.getNodeA().equals(node1) && e.getNodeB().equals(b))
                return e;
        }
        return null;
    }

    @SuppressWarnings({ "unchecked", "rawtypes" })
    @Override
    public Collection getEdges() {
        return this.edges;
    }

    @Override
    public boolean contains(Object obj) {
        if(obj instanceof Argument)
            return this.nodes.contains((Argument) obj);
        if(obj instanceof HyperDirEdge)
            return this.edges.contains((SetAttack) obj);
        return false;
    }

    /**
     *
     * @param node the node
     * @return the children of the node
     */
    public Collection<Argument> getChildren(Set<Argument> node) {
        HashSet<Argument> result = new HashSet<Argument>();
        for(SetAttack e : this.edges) {
            if(e.getNodeA().equals(node)) {
                result.add((Argument) e.getNodeB());
            }
        }
        return result;

    }

    @Override
    public Collection<Argument> getParents(Node node) {
        System.err.println("The return type for getParents in SetAfs is Collection<Set<Argument>>");
        return null;
    }

    /**
     *
     * @param node an argument
     * @return the arguments' parents
     */
    public Collection<Set<Argument>> getParents(Argument node) {
        HashSet<Set<Argument>> result = new HashSet<Set<Argument>>();
        for(SetAttack e : this.edges) {
            if(e.getNodeB().equals(node)) {
                result.add(e.getNodeA());
            }
        }
        return result;
    }

    /**
     *
     * @param <S> a node
     * @param hyperGraph a setAf
     * @param node1 an argument
     * @param node2 an argument
     * @return whether the path exists
     */
    public static <S extends Node> boolean existsDirectedPath(SetAf hyperGraph, Argument node1, Argument node2) {
        if (!hyperGraph.getNodes().contains(node1) || !hyperGraph.getNodes().contains(node2))
            throw new IllegalArgumentException("The nodes are not in this graph.");
        if (node1.equals(node2))
            return true;
        // we perform a DFS.
        Stack<Argument> stack = new Stack<Argument>();
        Collection<Argument> visited = new HashSet<Argument>();
        stack.add((Argument) node1);
        while (!stack.isEmpty()) {
            Argument node = stack.pop();
            visited.add(node);
            if (node.equals(node2))
                return true;
            stack.addAll(hyperGraph.getChildren(node));
            stack.removeAll(visited);
        }
        return false;
    }

    @Override
    public boolean existsDirectedPath(Argument node1, Argument node2) {
        return SetAf.existsDirectedPath(this, node1, node2);
    }

    @Override
    public Collection<Argument> getNeighbors(Argument node) {
        HashSet<Argument> result = new HashSet<Argument>();
        for(SetAttack a : this.edges) {
            if(a.getNodeB().equals(node)) {
                result.addAll(a.getNodeA());
            }
            if(a.getNodeA().contains(node)) {
                result.add((Argument) a.getNodeB());
            }
        }
        return result;
    }

    @Override
    public Matrix getAdjacencyMatrix() {
        // A matrix representation o a hypergraph is not known to me
        return null;
    }

    /**
     *
     * @param originalSet original set
     * @return the powerset of
     */
    public Set<Set<Argument>> powerSet(Set<Argument> originalSet) {
        HashSet<Set<Argument>> sets = new HashSet<Set<Argument>>();
        if (originalSet.isEmpty()) {
            sets.add(new HashSet<Argument>());
            return sets;
        }
        ArrayList<Argument> list = new ArrayList<Argument>(originalSet);
        Argument head = list.get(0);
        HashSet<Argument> rest = new HashSet<Argument>(list.subList(1, list.size()));
        for (Set<Argument> set : powerSet(rest)) {
            Set<Argument> newSet = new HashSet<Argument>();
            newSet.add(head);
            newSet.addAll(set);
            sets.add(newSet);
            sets.add(set);
        }

        return sets;
    }

    public SetAf getComplementGraph(int selfloops) {
        throw new UnsupportedOperationException("Not supported for SetAfs");
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    @Override
    public Collection getStronglyConnectedComponents() {
        // TODO Auto-generated method stub
        //algorithm yet to be implemented, not important for the next time
        return null;
    }

    /**
     * Returns the set of sub graphs of the given graph.
     * @param g a graph
     *
     * @return the set of sub graphs of the given graph.
     */
    public Collection<SetAf> getSubgraphs(SetAf g) {
        // not very efficient but will do for now
        Collection<SetAf> result = new HashSet<SetAf>();
        Set<Set<Argument>> subNodes = new SetTools<Argument>().subsets(g.getNodes());
        for (Set<Argument> nodes : subNodes) {
            @SuppressWarnings("unchecked")
            Set<Set<SetAttack>> edges = new SetTools<SetAttack>()
                    .subsets((Set<SetAttack>) g.getRestriction(nodes).getEdges());
            for (Set<SetAttack> es : edges) {
                SetAf newg = new SetAf();
                newg.nodes.addAll(nodes);
                newg.edges.addAll(es);
                result.add(newg);
            }
        }
        return result;
    }

    @Override
    public SetAf getRestriction(Collection<Argument> nodes) {
        SetAf graph = new SetAf();
        graph.nodes.addAll(nodes);
        for (HyperDirEdge<Argument> e : this.edges)
            if (nodes.containsAll(e.getNodeA()) && nodes.contains(e.getNodeB()))
                graph.add(e);
        return graph;
    }

    @Override
    public boolean hasSelfLoops() {
        for (Argument node1 : this.nodes)
            if (this.areAdjacent(node1, node1))
                return true;
        return false;
    }

    @Override
    public boolean isWeightedGraph() {
        return false;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean add(GeneralEdge edge) {
        return this.edges.add((SetAttack)edge);
    }
    /*
     * (non-Javadoc)
     *
     * @see java.lang.Object#toString()
     */
    public String toString() {
        return "<" + this.nodes.toString() + "," + this.edges.toString() + ">";
    }

    @Override
    public Collection<Argument> getChildren(Node node) {
        HashSet<Argument> children = new HashSet<Argument>();
        for(SetAttack att : this.edges) {
            if(att.getNodeA().contains(node))
                children.add(att.getNodeB());
        }
        return children;
    }

    @Override
    public Collection<DirHyperGraph<Argument>> getSubGraphs() {
        if(!SetAf.archivedSubgraphs.containsKey(this))
            SetAf.archivedSubgraphs.put(this, getSubgraphsHelper(this));
        return SetAf.archivedSubgraphs.get(this);
    }

    /**
     *
     * @param g the SetAf
     * @return the subgraphs of the SetAf
     */
    public static Collection<DirHyperGraph<Argument>> getSubgraphsHelper(SetAf g) {
        // not very efficient but will do for now
        Collection<DirHyperGraph<Argument>> result = new HashSet<DirHyperGraph<Argument>>();
        Set<Set<Argument>> subNodes = new SetTools<Argument>().subsets(g.getNodes());
        for (Set<Argument> nodes : subNodes) {
            @SuppressWarnings("unchecked")
            Set<Set<SetAttack>> edges = new SetTools<SetAttack>()
                    .subsets((Set<SetAttack>) g.getRestriction(nodes).getEdges());
            for (Set<SetAttack> es : edges) {
                SetAf newg = new SetAf();
                newg.nodes.addAll(nodes);
                newg.edges.addAll(es);
                result.add(newg);
            }
        }
        return result;
    }

    /**
     * Pretty prints the SetAf
     * @return string representation of the SetAf
     */
    public String prettyPrint() {
        StringBuilder output = new StringBuilder();
        for (Argument argument : this) output.append("argument(").append(argument.toString()).append(").\n");
        output.append("\n");
        for (SetAttack attack : this.getAttacks()) output.append("attack").append(attack.toString()).append(".\n");
        output.append("\n");
        return output.toString();
    }
}
