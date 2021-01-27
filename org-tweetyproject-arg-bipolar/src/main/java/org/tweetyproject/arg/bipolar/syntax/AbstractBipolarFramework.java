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

package org.tweetyproject.arg.bipolar.syntax;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungSignature;
import org.tweetyproject.commons.BeliefSet;
import org.tweetyproject.commons.Signature;
import org.tweetyproject.graphs.*;
import org.tweetyproject.math.matrix.Matrix;

import java.util.*;

/**
 * This class implements a bipolar abstract argumentation framework with attack and support relations.
 * Both relations need to have the form (BipolarEntity, BArgument)
 *
 * @author Lars Bengel
 *
 */
public abstract class AbstractBipolarFramework extends BeliefSet<BArgument, DungSignature> implements Graph<BArgument> {
    /**
     * For archiving sub graphs
     */
    private static Map<AbstractBipolarFramework, Collection<Graph<BArgument>>> archivedSubgraphs = new HashMap<>();

    /**
     * explicit listing of direct supporters and supported (for efficiency reasons)
     */
    protected Map<BArgument, Set<BipolarEntity>> supportParents = new HashMap<>();
    protected Map<BipolarEntity, Set<BArgument>> supportChildren = new HashMap<>();

    /**
     * explicit listing of direct attackers and attackees (for efficiency reasons)
     */
    protected Map<BArgument,Set<BipolarEntity>> attackParents = new HashMap<>();
    protected Map<BipolarEntity,Set<BArgument>> attackChildren = new HashMap<>();

    /**
     * Computes the set {A | (A,argument) in supports}.
     * @param argument an argument
     * @return the set of all bipolar entities that support <code>argument</code>.
     */
    public Set<BipolarEntity> getDirectSupporters(BArgument argument){
        if(!this.supportParents.containsKey(argument))
            return new HashSet<>();
        return new HashSet<BipolarEntity>(this.supportParents.get(argument));
    }

    /**
     * Computes the set {A | (argument,A) in supports}.
     * @param arg an instance of bipolar entity
     * @return the set of all arguments that are supported by <code>argument</code>.
     */
    public Set<BArgument> getDirectSupported(BipolarEntity arg){
        if(!this.supportChildren.containsKey(arg)) {
            return new HashSet<>();
        }
        return new HashSet<>(this.supportChildren.get(arg));
    }

    /**
     * Computes the set {A | (A,argument) in attacks}.
     * @param argument an argument
     * @return the set of all bipolar entities that attack <code>argument</code>.
     */
    public Set<BipolarEntity> getAttackers(BArgument argument){
        if(!this.attackParents.containsKey(argument))
            return new HashSet<>();
        return new HashSet<>(this.attackParents.get(argument));
    }

    /**
     * Computes the set {A | (argument,A) in attacks}.
     * @param arg an instance of bipolar entity
     * @return the set of all arguments that are attacked by <code>argument</code>.
     */
    public Set<BArgument> getAttacked(BipolarEntity arg){
        if(!this.attackChildren.containsKey(arg))
            return new HashSet<>();
        return new HashSet<>(this.attackChildren.get(arg));
    }

    /**
     * checks whether argument is acceptable wrt. ext
     * @param argument some argument
     * @param ext a set of arguments
     * @return "true" if argument is acceptable wrt. ext
     */
    public abstract boolean isAcceptable(BArgument argument, Collection<BArgument> ext);

    /**
     * The characteristic function of an bipolar argumentation framework: F_AF(S) = {A|A is acceptable wrt. S}.
     * @param extension an extension (a set of arguments).
     * @return an extension (a set of arguments).
     */
    public ArgumentSet faf(ArgumentSet extension){
        ArgumentSet newExtension = new ArgumentSet();
        for (BArgument argument : this) {
            if (this.isAcceptable(argument, extension))
                newExtension.add(argument);
        }
        return newExtension;
    }

    /**
     * Checks whether arg1 is directly supported by arg2.
     * @param arg1 an argument.
     * @param arg2 a bipolar entity
     * @return "true" if arg1 is directly supported by arg2
     */
    public boolean isDirectSupportedBy(BArgument arg1, BipolarEntity arg2){
        if(!this.supportParents.containsKey(arg1))
            return false;
        return this.supportParents.get(arg1).contains(arg2);
    }

    /**
     * Checks whether arg1 is attacked by arg2.
     * @param arg1 an argument.
     * @param arg2 a bipolar entity
     * @return "true" if arg1 is directly attacked by arg2
     */
    public boolean isAttackedBy(BArgument arg1, BipolarEntity arg2){
        if(!this.attackParents.containsKey(arg1))
            return false;
        return this.attackParents.get(arg1).contains(arg2);
    }

    /**
     * Returns all supports of this theory.
     * @return all supports of this theory.
     */
    public abstract Set<Support> getSupports();

    /**
     * Returns all attacks of this theory.
     * @return all attacks of this theory.
     */
    public abstract Set<Attack> getAttacks();

    /**
     * Adds the given support to this argumentation framework.
     * @param support a support
     * @return "true" if the set of supports has been modified.
     */
    public abstract boolean add(Support support);

    /**
     * Adds the set of supports to this argumentation framework
     * @param c a collection of supports
     * @return "true" if this argumentation framework has been modified.
     */
    public boolean addAllSupports(Collection<? extends Support> c){
        boolean result = false;
        for(Support supp: c)
            result |= this.add(supp);
        return result;
    }

    /**
     * Adds the given attack to this argumentation framework
     * @param attack an attack
     * @return "true" if the set of attacks has been modified.
     */
    public abstract boolean add(Attack attack);

    /**
     * Adds the set of attacks to this argumentation framework
     * @param c a collection of attacks
     * @return "true" if this argumentation framework has been modified.
     */
    public boolean addAllAttacks(Collection<? extends Attack> c){
        boolean result = false;
        for(Attack att: c)
            result |= this.add(att);
        return result;
    }

    /**
     * Adds all arguments, attacks and supports of the given argumentation framework to
     * this framework
     * @param theory some abstract support framework
     * @return "true" if this framework has been modified
     */
    protected boolean add(AbstractBipolarFramework theory){
        boolean b1 = this.addAll(theory);
        boolean b2 = this.addAllAttacks(theory.getAttacks());
        boolean b3 = this.addAllSupports(theory.getSupports());
        return b1 || b2 || b3 ;
    }

    /**
     * Adds argument to this argumentation framework
     * @param argument some argument
     * @return "true" if this framework has been modified
     */
    public boolean add(BArgument argument) {
        return super.add(argument);
    }

    /**
     * Adds argument to this argumentation framework
     * for better compatibility between dung theories and bipolar argumentation frameworks
     * @param argument some argument
     * @return "true" if this framework has been modified
     */
    public boolean add(Argument argument) {
        return this.add(new BArgument(argument));
    }

    /**
     * Removes the given support from this argumentation framework.
     * @param support a support
     * @return "true" if the set of supports has been modified.
     */
    public boolean remove(Support support){
        boolean result = false;
        if(supportParents.containsKey(support.getSupported()))
            result |= supportParents.get(support.getSupported()).remove(support.getSupporter());
        if(supportChildren.containsKey(support.getSupporter()))
            result |= supportChildren.get(support.getSupporter()).remove(support.getSupported());
        return result;
    }

    /**
     * Removes the given attack from this argumentation framework.
     * @param attack an attack
     * @return "true" if the set of supports has been modified.
     */
    public boolean remove(Attack attack){
        boolean result = false;
        if(attackParents.containsKey(attack.getAttacked()))
            result |= attackParents.get(attack.getAttacked()).remove(attack.getAttacker());
        if(attackChildren.containsKey(attack.getAttacker()))
            result |= attackChildren.get(attack.getAttacker()).remove(attack.getAttacked());
        return result;
    }

    /**
     * Removes the argument and all its attacks and supports
     * @param a some argument
     * @return true if this structure has been changed
     */
    public boolean remove(BArgument a){
        if(this.supportParents.get(a) != null){
            for(BipolarEntity b: this.supportParents.get(a))
                this.supportChildren.get(b).remove(a);
            this.supportParents.remove(a);
        }
        Set<BipolarEntity> supportKeys= new HashSet<>(this.supportChildren.keySet());
        for (BipolarEntity key: supportKeys) {
            if (key.contains(a)) {
                for(BArgument b: this.supportChildren.get(key))
                    this.supportParents.get(b).remove(key);
                this.supportChildren.remove(key);
            }
        }
        if(this.attackParents.get(a) != null){
            for(BipolarEntity b: this.attackParents.get(a))
                this.attackChildren.get(b).remove(a);
            this.attackParents.remove(a);
        }
        Set<BipolarEntity> attackKeys = new HashSet<>(this.attackChildren.keySet());
        for (BipolarEntity key: attackKeys) {
            if (key.contains(a)) {
                for(BArgument b: this.attackChildren.get(key))
                    this.attackParents.get(b).remove(key);
                this.attackChildren.remove(key);
            }
        }
        return super.remove(a);
    }

    /* (non-Javadoc)
     * @see org.tweetyproject.commons.BeliefSet#removeAll(java.util.Collection)
     */
    public boolean removeAll(Collection<?> c){
        boolean result = false;
        for(Object a: c)
            if(a instanceof BArgument)
                result |= this.remove((BArgument) a);
            else if(a instanceof Attack)
                result |= this.remove((Attack)a);
            else if(a instanceof BinarySupport)
                result |= this.remove((Support)a);
        return result;
    }

    /** Pretty print of the framework.
     * @return the pretty print of the framework.
     */
    public String prettyPrint(){
        String output = new String();
        Iterator<BArgument> it = this.iterator();
        while(it.hasNext())
            output += "argument("+it.next().toString()+").\n";
        output += "\n";
        Iterator<Attack> it2 = this.getAttacks().iterator();
        while(it2.hasNext())
            output += "attack"+it2.next().toString()+".\n";
        output += "\n";
        Iterator<Support> it3 = this.getSupports().iterator();
        while(it3.hasNext())
            output += "support"+it3.next().toString()+".\n";
        return output;
    }

    @Override
    protected DungSignature instantiateSignature() {
        return new DungSignature();
    }

    @Override
    public Signature getMinimalSignature() {
        return new DungSignature(this);
    }

    @Override
    public boolean add(Edge<BArgument> edge) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<BArgument> getNodes() {
        return this;
    }

    @Override
    public int getNumberOfNodes() {
        return this.size();
    }

    @Override
    public boolean areAdjacent(BArgument a, BArgument b) {
        return this.isAttackedBy(a, b) || this.isDirectSupportedBy(a, b);
    }

    @Override
    public Edge<BArgument> getEdge(BArgument a, BArgument b) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<? extends Edge<? extends BArgument>> getEdges() {
        // edges can be between a set of nodes and a single node
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<BArgument> getChildren(Node node) {
        if (!(node instanceof BipolarEntity)) {
            throw new IllegalArgumentException("Node of type BArgument expected");
        } else {
            Collection<BArgument> result = this.getAttacked((BipolarEntity) node);
            result.addAll(this.getDirectSupported((BipolarEntity) node));
            return result;
        }
    }

    @Override
    public Collection<BArgument> getParents(Node node) {
        // parents can be arguments or sets of arguments
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean existsDirectedPath(BArgument node1, BArgument node2) {
        return DefaultGraph.existsDirectedPath(this, node1, node2);
    }

    @Override
    public Collection<BArgument> getNeighbors(BArgument node) {
        //Neighbors can be arguments and sets of arguments
        throw new UnsupportedOperationException();
    }

    @Override
    public Matrix getAdjacencyMatrix() {
        throw new UnsupportedOperationException();
    }

    @Override
    public Graph<BArgument> getComplementGraph(int i) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Collection<Collection<BArgument>> getStronglyConnectedComponents() {
        return DefaultGraph.getStronglyConnectedComponents(this);
    }

    @Override
    public Collection<Graph<BArgument>> getSubgraphs() {
        if (!archivedSubgraphs.containsKey(this)) {
            archivedSubgraphs.put(this, DefaultGraph.getSubgraphs(this));
        }

        return (Collection)archivedSubgraphs.get(this);
    }

    @Override
    public Graph<BArgument> getRestriction(Collection<BArgument> collection) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean hasSelfLoops() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isWeightedGraph() {
        return false;
    }
}
