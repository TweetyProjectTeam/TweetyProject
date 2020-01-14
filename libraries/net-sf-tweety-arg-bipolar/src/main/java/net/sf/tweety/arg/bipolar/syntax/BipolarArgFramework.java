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

package net.sf.tweety.arg.bipolar.syntax;

import java.util.*;

import net.sf.tweety.arg.dung.syntax.*;
import net.sf.tweety.arg.dung.semantics.*;
import net.sf.tweety.graphs.*;


/**
 * This class implements a bipolar abstract argumentation theory with support in a deductive sense.
 * ie. if a supports b, then the acceptance of a implies the acceptance of b and as a consequence
 * the non-acceptance of b implies the non-acceptance of a.
 * <br>
 * <br>See
 * <br>
 * <br>Cayrol, Lagasquie-Schiex. Bipolarity in argumentation graphs: Towards a better understanding. 2013
 * <br>
 * <br>and
 * <br>
 * <br>Boella et al. Support in Abstract Argumentation. 2010
 *
 * 
 *
 */
public class BipolarArgFramework extends DungTheory {

    /**
     * For archiving sub graphs
     */
    private static Map<BipolarArgFramework, Collection<Graph<Argument>>> archivedSubgraphs = new HashMap<BipolarArgFramework, Collection<Graph<Argument>>>();

    /**
     * explicit listing of direct supporters and supported (for efficiency reasons)
     */
    private Map<Argument, Set<Argument>> supportParents = new HashMap<Argument, Set<Argument>>();
    private Map<Argument, Set<Argument>> supportChildren = new HashMap<Argument, Set<Argument>>();

    /**
     * Default constructor; initializes empty sets of arguments, attacks and supports
     */
    public BipolarArgFramework() {
        super();
    }

    /**
     * Creates a new theory from the given graph.
     *
     * @param graph some graph
     */
    public BipolarArgFramework(Graph<Argument> graph) {
        //TODO
    }

    /**
     * Determines if ext is closed under the support relation of this framework
     * i.e., if all arguments supported by ext are an element of ext
     * @param ext an extension
     * @return true if ext is closed under R_sup
     */
    public boolean isClosed(Extension ext){
        for(Argument a: ext) {
            Set<Argument> supportedArguments = this.getDirectSupported(a);
            if(!ext.containsAll(supportedArguments))
                return false;
        }
        return true;
    }

    /**
     * Calculates the set of deductive complex attacks (d-attacks) in this bipolar argumentation framework
     * i.e. all direct, supported, mediated and super-mediated attacks
     * @return set of d-attacks in this bipolar argumentation framework
     */
    public Set<Attack> getDeductiveComplexAttacks(){
        Set<Attack> dAttacks = this.getComplexAttacks();
        Set<Attack> superMediatedAttacks = new HashSet<Attack>();
        for(Attack attack: dAttacks) {
            Set<Argument> superMediatedOrigins = this.getSupporters(attack.getAttacker());
            Set<Argument> superMediatedTargets = this.getSupporters(attack.getAttacked());

            for(Argument origin: superMediatedOrigins) {
                superMediatedAttacks.add(new Attack(origin, attack.getAttacked()));
            }
            for(Argument target: superMediatedTargets) {
                superMediatedAttacks.add(new Attack(attack.getAttacker(), target));
            }
        }
        dAttacks.addAll(superMediatedAttacks);
        return dAttacks;
    }

    /**
     * Calculates the set of complex attacks in this bipolar argumentation framework
     * i.e. all direct, supported and mediated attacks
     * @return set of complex attacks in this bipolar argumentation framework
     */
    public Set<Attack> getComplexAttacks(){
        Set<Attack> cAttacks = this.getAttacks();
        for(Argument argument: this) {
            Set<Argument> supportedArguments = this.getSupported(argument);
            Set<Argument> supportedAttackTargets = new HashSet<Argument>();
            Set<Argument> mediatedAttackOrigins = new HashSet<Argument>();
            for(Argument a: supportedArguments) {
                supportedAttackTargets.addAll(this.getAttacked(a));
                mediatedAttackOrigins.addAll(this.getAttackers(a));
            }
            for(Argument target: supportedAttackTargets) {
                cAttacks.add(new Attack(argument, target));
            }
            for(Argument origin: mediatedAttackOrigins) {
                cAttacks.add(new Attack(origin, argument));
            }
        }
        return cAttacks;
    }

    /**
     * Calculates the set of mediated attack from "arg1" to other arguments "y", i.e. whether there
     * is a sequence of direct supports from "y" to "x" and a direct attack from "arg1" to "x".
     * @param arg1 an argument.
     * @return set of mediated attacks starting from "arg1".
     */
    public Set<Attack> getMediatedAttacks(Argument arg1){
        Set<Argument> attackedArguments = this.getAttacked(arg1);
        Set<Argument> mediatedAttackTargets = new HashSet<Argument>();
        for(Argument a: attackedArguments) {
            mediatedAttackTargets.addAll(this.getSupporters(a));
        }
        Set<Attack> mediatedAttacks = new HashSet<Attack>();
        for(Argument target: mediatedAttackTargets) {
            mediatedAttacks.add(new Attack(arg1, target));
        }
        return mediatedAttacks;
    }

    /**
     * Checks whether there exists a mediated attack from "arg1" to "arg2", i.e. whether there
     * is a sequence of direct supports from "arg2" to "x" and a direct attack from "arg1" to "x".
     * @param arg1 an argument.
     * @param arg2 an argument.
     * @return "true" iff there is a mediated attack from "arg1" to "arg2".
     */
    public boolean isMediatedAttack(Argument arg1, Argument arg2){
        Set<Argument> supportedArguments = this.getSupported(arg2);
        return this.isAttackedBy(arg1, supportedArguments);
    }

    /**
     * Checks whether there exists a super-mediated attack from "arg1" to "arg2", i.e. whether there
     * is a sequence of direct supports from "arg2" to "x" and a direct or supported attack from "arg1" to "x".
     * @param arg1 an argument.
     * @param arg2 an argument.
     * @return "true" iff there is a super-mediated attack from "arg1" to "arg2".
     */
    public boolean isSuperMediatedAttack(Argument arg1, Argument arg2){
        //TODO isSupportAttackedBy
        Extension supportedArguments1 = new Extension(this.getSupported(arg1));
        Extension supportedArguments2 = new Extension(this.getSupported(arg2));
        return this.isMediatedAttack(arg1, arg2) || this.isAttacked(supportedArguments2, supportedArguments1);
    }

    /**
     * Calculates the set of supported attack from "arg1" to other arguments "y", i.e. whether there
     * is a sequence of direct supports from "arg1" to "x" and a direct attack from "x" to "y".
     * @param arg1 an argument.
     * @return set of supported attacks starting from "arg1".
     */
    public Set<Attack> getSupportedAttacks(Argument arg1){
        Set<Attack> supportedAttacks = new HashSet<Attack>();
        Set<Argument> supportedArguments = this.getSupported(arg1);
        Set<Argument> supportedAttackTargets = new HashSet<Argument>();
        for(Argument a: supportedArguments) {
            supportedAttackTargets.addAll(this.getAttacked(a));
        }
        for(Argument target: supportedAttackTargets) {
            supportedAttacks.add(new Attack(arg1, target));
        }
        return supportedAttacks;
    }

    /**
     * Checks whether there exists a supported attack from "arg1" to "arg2", i.e. whether there
     * is a sequence of direct supports from "arg1" to "x" and a direct attack from "x" to "arg2".
     * @param arg1 an argument.
     * @param arg2 an argument.
     * @return "true" iff there is a supported attack from "arg1" to "arg2".
     */
    public boolean isSupportedAttack(Argument arg1, Argument arg2){
        Extension supportedArguments = new Extension(this.getSupported(arg1));
        return this.isAttacked(arg2, supportedArguments);
    }

    /**
     * Computes the set {A | there is a sequence of direct supports from an element of <code>ext</code> to A
     * @param ext an extension ie. a set of arguments
     * @return the set of all arguments that are supported by <code>ext</code>.
     */
    public Set<Argument> getSupported(Collection<Argument> ext){
        Set<Argument> supported = new HashSet<Argument>();
        for (Argument argument: ext)
            supported.addAll(this.getSupported(argument));

        return supported;
    }

    /**
     * Computes the set {A | there is a sequence of direct supports from argument to A
     * @param argument an argument
     * @return the set of all arguments that are supported by <code>argument</code>.
     */
    public Set<Argument> getSupported(Argument argument){
        return this.getSupported(argument, new HashSet<Argument>());
    }

    /**
     * Computes the set {A | there is a sequence of direct supports from argument to A}.
     * @param argument an argument
     * @param visited already visited arguments
     * @return the set of all arguments that are supported by <code>argument</code>.
     */
    private Set<Argument> getSupported(Argument argument, Set<Argument> visited){
        if(!this.supportChildren.containsKey(argument))
            return new HashSet<Argument>();
        Set<Argument> directSupportedArguments = this.getDirectSupported(argument);
        Set<Argument> supportedArguments = new HashSet<Argument>(directSupportedArguments);

        directSupportedArguments.removeAll(visited);
        for(Argument supported: directSupportedArguments){
            visited.add(supported);
            supportedArguments.addAll(this.getSupported(supported, visited));
        }
        return supportedArguments;
    }

    /**
     * Computes the set {A | (argument,A) in supports}.
     * @param argument an argument
     * @return the set of all arguments that are supported by <code>argument</code>.
     */
    public Set<Argument> getDirectSupported(Argument argument){
        if(!this.supportChildren.containsKey(argument))
            return new HashSet<Argument>();
        return new HashSet<Argument>(this.supportChildren.get(argument));
    }

    /**
     * Computes the set {A | there is a sequence of direct supports from A to argument
     * @param argument an argument
     * @return the set of all arguments that support <code>argument</code>.
     */
    public Set<Argument> getSupporters(Argument argument){
        return this.getSupporters(argument, new HashSet<Argument>());
    }

    /**
     * Computes the set {A | there is a sequence of direct supports from A to argument}.
     * @param argument an argument
     * @param visited already visited arguments
     * @return the set of all arguments that support <code>argument</code>.
     */
    private Set<Argument> getSupporters(Argument argument, Set<Argument> visited){
        if(!this.supportParents.containsKey(argument))
            return new HashSet<Argument>();
        Set<Argument> directSupportArguments = this.getDirectSupporters(argument);
        Set<Argument> supportArguments = new HashSet<Argument>(directSupportArguments);

        directSupportArguments.removeAll(visited);
        for(Argument supporter: directSupportArguments){
            visited.add(supporter);
            supportArguments.addAll(this.getSupporters(supporter, visited));
        }
        return supportArguments;
    }

    /**
     * Computes the set {A | (A,argument) in supports}.
     * @param argument an argument
     * @return the set of all arguments that support <code>argument</code>.
     */
    public Set<Argument> getDirectSupporters(Argument argument){
        if(!this.supportParents.containsKey(argument))
            return new HashSet<Argument>();
        return new HashSet<Argument>(this.supportParents.get(argument));
    }

    /**
     * returns true if some argument of <code>ext</code> supports argument.
     * @param argument an argument
     * @param ext an extension, ie. a set of arguments
     * @return true if some argument of <code>ext</code> supports argument.
     */
    public boolean isSupported(Argument argument, Extension ext){
        if(!this.supportParents.containsKey(argument))
            return false;
        for(Argument supporter: this.supportParents.get(argument))
            if(ext.contains(supporter))
                return true;
        return false;
    }

    /**
     * returns true if some argument of <code>ext</code> is supported by argument.
     * @param argument an argument
     * @param ext an extension, ie. a set of arguments
     * @return true if some argument of <code>ext</code> is supported by argument.
     */
    public boolean isSupportedBy(Argument argument, Collection<Argument> ext){
        if(!this.supportChildren.containsKey(argument))
            return false;
        for(Argument supported: this.supportChildren.get(argument))
            if(ext.contains(supported))
                return true;
        return false;
    }

    /**
     * returns true if some argument of <code>ext2</code> supports some argument
     * in <code>ext1</code>
     * @param ext1 an extension, ie. a set of arguments
     * @param ext2 an extension, ie. a set of arguments
     * @return true if some argument of <code>ext2</code> supports some argument
     * in <code>ext1</code>
     */
    public boolean isSupported(Extension ext1, Extension ext2){
        for(Argument a: ext1)
            if(this.isSupported(a, ext2)) return true;
        return false;
    }

    /**
     * Checks whether there is a sequence of direct supports from arg1 to arg2.
     * @param arg1 an argument.
     * @param arg2 an argument.
     * @return "true" if arg1 is supported by arg2
     */
    public boolean isSupportedBy(Argument arg1, Argument arg2){
        return this.getSupporters(arg1).contains(arg2);
    }

    /**
     * Checks whether arg1 is directly supported by arg2.
     * @param arg1 an argument.
     * @param arg2 an argument.
     * @return "true" if arg1 is directly supported by arg2
     */
    public boolean isDirectSupportedBy(Argument arg1, Argument arg2){
        if(!this.supportParents.containsKey(arg1))
            return false;
        return this.supportParents.get(arg1).contains(arg2);
    }

    /** Pretty print of the framework.
     * @return the pretty print of the framework.
     */
    public String prettyPrint(){
        String output = new String();
        Iterator<Argument> it = this.iterator();
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

    /**
     * Adds the given support to this bipolar argumentation framework.
     * @param support a support
     * @return "true" if the set of supports has been modified.
     */
    public boolean add(Support support) { return this.addSupport(support.getSupporter(), support.getSupported()); }

    /**
     * Adds a support from the first argument to the second to this bipolar argumentation framework.
     * @param supporter some argument
     * @param supported some argument
     * @return "true" if the set of supports has been modified.
     */
    public boolean addSupport(Argument supporter, Argument supported){
        boolean result = false;
        if(!supportParents.containsKey(supported))
            supportParents.put(supported, new HashSet<Argument>());
        result |= supportParents.get(supported).add(supporter);
        if(!supportChildren.containsKey(supporter))
            supportChildren.put(supporter, new HashSet<Argument>());
        result |= supportChildren.get(supporter).add(supported);
        return result;
    }

    /**
     * Removes the given support from this bipolar argumentation framework.
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
     * Removes the argument and all its attacks and supports
     * @param a some argument
     * @return true if this structure has been changed
     */
    public boolean remove(Argument a){
        //super.remove(a);
        if(this.supportParents.get(a) != null){
            for(Argument b: this.supportParents.get(a))
                this.supportChildren.get(b).remove(a);
            this.supportParents.remove(a);
        }
        if(this.supportChildren.get(a) != null){
            for(Argument b: this.supportChildren.get(a))
                this.supportParents.get(b).remove(a);
            this.supportChildren.remove(a);
        }
        return super.remove(a);
    }

    /* (non-Javadoc)
     * @see net.sf.tweety.commons.BeliefSet#removeAll(java.util.Collection)
     */
    public boolean removeAll(Collection<?> c){
        boolean result = true;
        for(Object a: c)
            if(a instanceof Argument)
                result |= this.remove((Argument)a);
            else if(a instanceof Attack)
                result |= this.remove((Attack)a);
            else if(a instanceof Support)
                result |= this.remove((Support)a);
        return result;
    }

    /* (non-Javadoc)
     * @see net.sf.tweety.kr.BeliefSet#contains(java.lang.Object)
     */
    @Override
    public boolean contains(Object o){
        if(o instanceof Argument)
            return super.contains(o);
        if(o instanceof Attack)
            return this.containsAttack((Attack)o);
        if(o instanceof Support)
            return this.containsSupport((Support)o);
        return false;
    }

    /**
     * Checks whether this theory contains the given support.
     * @param supp some support
     * @return "true" iff this theory contains the given support.
     */
    public boolean containsSupport(Support supp) {
        if(this.supportParents.get(supp.getSupported()) == null)
            return false;
        return this.supportParents.get(supp.getSupported()).contains(supp.getSupporter());
    }

    /**
     * Adds the set of supports to this bipolar argumentation framework.
     * @param c a collection of supports
     * @return "true" if this bipolar argumentation framework has been modified.
     */
    public boolean addAllSupports(Collection<? extends Support> c){
        boolean result = false;
        for(Support supp: c)
            result |= this.add(supp);
        return result;
    }

    /**
     * Adds all arguments, attacks and supports of the given framework to
     * this framework
     * @param baf some bipolar argumentation framework
     * @return "true" if this bipolar argumentation framework has been modified
     */
    public boolean add(BipolarArgFramework baf){
        boolean b1 = this.addAll(baf);
        boolean b2 = this.addAllAttacks(baf.getAttacks());
        boolean b3 = this.addAllSupports(baf.getSupports());
        return b1 || b2 || b3;
    }

    /**
     * Returns all supports of this framework.
     * @return all supports of this framework.
     */
    public Set<Support> getSupports(){
        Set<Support> supports = new HashSet<Support>();
        for(Argument a: this) {
            if(this.supportChildren.containsKey(a)) {
                for(Argument b: this.supportChildren.get(a))
                    supports.add(new Support(a,b));
            }
        }
        return supports;
    }

    /**
     * constructs the complete associated dung theory of this bipolar argumentation framework#
     * i.e. constructs all deductive complex attacks of BAF and returns dung theory without supports
     * @return the complete associated Dung Theory of this bipolar argumentation framework
     */
    public DungTheory getCompleteAssociatedDungTheory(){
        DungTheory completeAssociatedDungTheory = new DungTheory();
        Set<Attack> dAttacks = this.getDeductiveComplexAttacks();
        completeAssociatedDungTheory.addAll(this.getNodes());
        completeAssociatedDungTheory.addAllAttacks(dAttacks);
        return completeAssociatedDungTheory;
    }

    /**
     * constructs the meta dung theory of this bipolar argumentation framework by adding
     * meta-arguments for every attack and support relation
     * @return the meta dung theory of this bipolar argumentation framework
     */
    public DungTheory getMetaFramework() {
        DungTheory metaFramework = new DungTheory();
        metaFramework.addAll(this);

        Set<Attack> attacks = this.getAttacks();
        for (Attack attack: attacks){
            Argument a = attack.getAttacker();
            Argument b = attack.getAttacked();
            Argument x_ab = new Argument("X_" + a.getName() + b.getName());
            Argument y_ab = new Argument("Y_" + a.getName() + b.getName());
            metaFramework.add(x_ab);
            metaFramework.add(y_ab);
            metaFramework.addAttack(a, x_ab);
            metaFramework.addAttack(x_ab, y_ab);
            metaFramework.addAttack(y_ab, b);
        }
        Set<Support> supports = this.getSupports();
        for (Support support: supports) {
            Argument a = support.getSupporter();
            Argument b = support.getSupported();
            Argument z_ab = new Argument("Z_" + a.getName() + b.getName());
            metaFramework.add(z_ab);
            metaFramework.addAttack(b, z_ab);
            metaFramework.addAttack(z_ab, a);
        }
        return metaFramework;
    }

    /* (non-Javadoc)
     * @see net.sf.tweety.graphs.Graph#areAdjacent(net.sf.tweety.graphs.Node, net.sf.tweety.graphs.Node)
     */
    @Override
    public boolean areAdjacent(Argument a, Argument b) {
        return this.isAttackedBy(b, a) || this.isSupportedBy(b, a);
    }

    /* (non-Javadoc)
     * @see net.sf.tweety.graphs.Graph#getEdges()
     */
    //@Override
    //public Collection<? extends Edge<? extends Argument>> getEdges() {
    //    return this.getAttacks();
    //}
    //TODO

}

