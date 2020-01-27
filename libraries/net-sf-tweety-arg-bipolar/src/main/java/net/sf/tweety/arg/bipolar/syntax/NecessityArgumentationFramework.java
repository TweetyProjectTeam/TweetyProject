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

/**
 * This class implements a bipolar abstract argumentation framework with necessities.
 * ie. if an argument a supports b, then the acceptance of a is required for the acceptance of b.
 * If a set of arguments E supports an argument b, then the acceptance of b requires the acceptance of at least one argument of E.
 * <br>
 * <br>See
 * <br>
 * <br>Nouioua. AFs with Necessities: Further Semantics and Labelling Characterization. 2013
 * <br>
 * <br>and
 * <br>
 * <br>Polberg, Oren. Revisiting Support in Abstract Argumentation Systems. 2014
 *
 * @author Lars Bengel
 *
 */
public class NecessityArgumentationFramework extends AbstractBipolarFramework implements Comparable<NecessityArgumentationFramework> {
    /**
     * Default constructor; initializes empty sets of arguments, attacks and supports
     */
    public NecessityArgumentationFramework() {
        super();
    }

    @Override
    public boolean isAcceptable(BArgument argument, Collection<BArgument> ext) {
        return false;
    }

    /**
     * checks if the set of arguments S is coherent in his argumentation framework
     * S is coherent iff it is closed and N-Cycle-Free
     * @param argumentSet a set of arguments
     * @return "true" if argumentSet is coherent
     */
    public boolean isCoherent(Collection<BArgument> argumentSet) {
        return this.isClosed(argumentSet) && this.isNCycleFree(argumentSet);
    }

    /**
     * checks if the given set of arguments is closed under the support relation
     * a set of arguments S is closed iff every argument a in S has
     * support from a non-empty subset of S
     * @param argumentSet a set of arguments
     * @return "true" if argumentSet is closed under the support relation
     */
    public boolean isClosed(Collection<BArgument> argumentSet) {
        for (BArgument argument: argumentSet) {
            for (BipolarEntity s: this.getDirectSupporters(argument)) {
                ArgumentSet supporter = (ArgumentSet) s;
                (supporter).retainAll(argumentSet);
                if (supporter.isEmpty())
                    return false;
            }
        }
        return true;
    }

    /**
     * checks if a set of arguments S is N-Cycle-Free in this argumentation framework
     * ie. if all arguments in S are N-Cycle-Free in S
     * @param argumentSet a set of arguments
     * @return "true" the set of arguments isN-Cycle-Free
     */
    public boolean isNCycleFree(Collection<BArgument> argumentSet) {
        for (BArgument argument: argumentSet) {
            if (!isNCycleFreeIn(argument, argumentSet))
                return false;
        }
        return true;
    }

    /**
     * checks if argument is N-Cycle-Free in the set of arguments
     * @param argument some argument
     * @param argumentSet a set of arguments
     * @return "true" if argument in N-Cycle-Free in the set of arguments
     */
    public boolean isNCycleFreeIn(BArgument argument, Collection<BArgument> argumentSet) {
        return this.isNCycleFreeIn(argument, argumentSet, new HashSet<>());

    }

    /**
     * checks if argument is N-Cycle-Free in the set of arguments
     * an argument a is N-Cycle-Free in a set S iff every supporting set E of a has either
     * an empty intersection with S or one of the arguments in the intersection is N-Cycle-Free in S.
     * @param argument some argument
     * @param argumentSet a set of arguments
     * @param visited set of already checked arguments
     * @return "true" if argument in N-Cycle-Free in the set of arguments
     */
    private boolean isNCycleFreeIn(BArgument argument, Collection<BArgument> argumentSet, Collection<BArgument> visited) {
        if (!argumentSet.contains(argument))
            throw new IllegalArgumentException("argument needs to be in argumentSet");
        Set<BipolarEntity> supporters = this.getDirectSupporters(argument);
        if (supporters.isEmpty())
            return true;
        for (BipolarEntity s: supporters) {
            ArgumentSet supporter = new ArgumentSet((ArgumentSet)s);
            (supporter).retainAll(argumentSet);
            if (supporter.isEmpty()) {
                return true;
            } else {
                supporter.removeAll(visited);
                for (BArgument arg: supporter) {
                    visited.add(argument);
                    if (this.isNCycleFreeIn(arg, argumentSet, visited))
                        return true;
                }
            }
        }
        return false;
    }

    @Override
    public boolean add(Support s) {
        if(s instanceof BinarySupport) {
            return this.addSupport((BArgument) s.getSupporter(), (BArgument) s.getSupported());
        } else if(s instanceof SetSupport) {
            return this.addSupport((ArgumentSet) s.getSupporter(), (BArgument) s.getSupported());
        }
        return true;
    }

    /**
     * Adds a support from the first argument to the second to this argumentation framework.
     * @param supporter some argument
     * @param supported some argument
     * @return "true" if the set of supports has been modified.
     */
    public boolean addSupport(BArgument supporter, BArgument supported){
        return addSupport(new ArgumentSet(supporter), supported);
    }

    /**
     * Adds a support from a set of arguments to an argument to this argumentation framework.
     * @param supporter a set of arguments
     * @param supported some argument
     * @return "true" if the set of supports has been modified.
     */
    public boolean addSupport(ArgumentSet supporter, BArgument supported){
        boolean result = false;
        if(!supportParents.containsKey(supported))
            supportParents.put(supported, new HashSet<>());
        result |= supportParents.get(supported).add(supporter);
        if(!supportChildren.containsKey(supporter))
            supportChildren.put(supporter, new HashSet<>());
        result |= supportChildren.get(supporter).add(supported);
        return result;
    }

    @Override
    public boolean add(Attack a) {
        if (!(a instanceof BinaryAttack)) {
            throw new IllegalArgumentException("Attack of type BinaryAttack expected");
        } else {
            BinaryAttack attack = (BinaryAttack) a;
            return this.addAttack(attack.getAttacker(), attack.getAttacked());
        }
    }

    /**
     * Adds an attack from the first argument to the second to this necessity argumentation system.
     * @param attacker some argument
     * @param attacked some argument
     * @return "true" if the set of attacks has been modified.
     */
    public boolean addAttack(BArgument attacker, BArgument attacked){
        boolean result = false;
        if(!attackParents.containsKey(attacked))
            attackParents.put(attacked, new HashSet<>());
        result |= attackParents.get(attacked).add(attacker);
        if(!attackChildren.containsKey(attacker))
            attackChildren.put(attacker, new HashSet<>());
        result |= attackChildren.get(attacker).add(attacked);
        return result;
    }

    @Override
    public Set<Support> getSupports() {
        Set<Support> supports = new HashSet<>();
        for(BArgument a: this) {
            if(this.supportParents.containsKey(a)) {
                for(BipolarEntity b: this.supportParents.get(a))
                    supports.add(new SetSupport((ArgumentSet) b, a));
            }
        }
        return supports;
    }

    @Override
    public Set<Attack> getAttacks(){
        Set<Attack> attacks = new HashSet<>();
        for(BArgument a: this) {
            if(this.attackParents.containsKey(a)) {
                for(BipolarEntity b: this.attackParents.get(a))
                    attacks.add(new BinaryAttack((BArgument) b, a));
            }
        }
        return attacks;
    }

    @Override
    public int compareTo(NecessityArgumentationFramework o) {
        return this.hashCode() - o.hashCode();
    }


    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        int prime = 1;
        int result = super.hashCode();
        result = 31 * result + (this.attackParents == null ? 0 : this.attackParents.hashCode());
        result = 37 * result + (this.supportParents == null ? 0 : this.supportParents.hashCode());
        return result;
    }

}