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

import org.tweetyproject.commons.util.SetTools;
import org.apache.commons.math.random.AbstractRandomGenerator;

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

    /**
     * creates a necessity argumentation framework from the given evidential argumentation framework
     * @param eaf a bipolar evidential argumentation framework
     */
    public NecessityArgumentationFramework(EvidentialArgumentationFramework eaf) {
        super();
        this.add(eaf.toNAF());
    }

    /**
     * creates a necessity argumentation framework from the given deductive argumentation framework
     * @param daf a bipolar deductive argumentation framework
     */
    public NecessityArgumentationFramework(DeductiveArgumentationFramework daf) {
        super();
        this.add(daf.toNAF());
    }

    /**
     * checks whether ext defends argument
     * a set of arguments S defends an argument a iff S u {a} is coherent and S attacks
     * each coherent set T, which attacks a
     * @param argument some argument
     * @param ext a set of arguments
     * @return "true" if ext defends argument
     */
    public boolean isAcceptable(BArgument argument, Collection<BArgument> ext) {
        Set<BArgument> extWithA = new HashSet<>(ext);
        extWithA.add(argument);
        if (!this.isCoherent(extWithA)) {
            return false;
        }
        for (Set<BArgument> subset: new SetTools<BArgument>().subsets(this)) {
            for (BipolarEntity attacker: this.getAttackers(argument)) {
                if (subset.contains((BArgument)attacker) && !this.isAttackedBy(subset, ext) && this.isCoherent(subset)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * computes the set of deactivated arguments of argumentSet
     * a set of arguments S deactivates an argument a iff either S attacks a or S does not support a
     * @param argumentSet a set of arguments
     * @return the set of deactivated arguments
     */
    public Set<BArgument> getDeactivatedArguments(Collection<BArgument> argumentSet) {
        Set<BArgument> deactivatedArguments = new HashSet<>();
        for (BArgument argument: this) {
            if (this.isAttackedBy(argument, argumentSet)) {
                deactivatedArguments.add(argument);
                continue;
            }
            for (BipolarEntity supporter: this.getDirectSupporters(argument)) {
                Set<Set<BArgument>> intersect = new HashSet<>();
                intersect.add(new HashSet<>((ArgumentSet)supporter));
                intersect.add(new HashSet<>(argumentSet));
                if (new SetTools<BArgument>().hasEmptyIntersection(intersect)) {
                    deactivatedArguments.add(argument);
                }
            }
        }
        return deactivatedArguments;
    }

    /**
     * checks whether the given set of arguments is strongly coherent in this argumentation framework
     * a set of arguments is strongly coherent iff it is coherent an conflict-free
     * @param argumentSet a set of arguments
     * @return "true" if argumentSet is strongly coherent
     */
    public boolean isStronglyCoherent(Collection<BArgument> argumentSet) {
        return this.isConflictFree(argumentSet) && this.isCoherent(argumentSet);
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
                Set<Set<BArgument>> intersect = new HashSet<>();
                intersect.add(new HashSet<>(supporter));
                intersect.add((Set<BArgument>) argumentSet);
                if (new SetTools<BArgument>().hasEmptyIntersection(intersect)) {
                    return false;
                }
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

    /**
     * checks whether the given set is conflict-free wrt. the attack relation
     * @param argumentSet a set of arguments
     * @return "true" if argumentSet is Conflict-Free
     */
    public boolean isConflictFree(Collection<BArgument> argumentSet) {
        for (BArgument argument: argumentSet) {
            for (BipolarEntity attacker: this.getAttackers(argument)) {
                if (argumentSet.contains((BArgument) attacker)) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * checks whether argument is attacked by any argument in argumentSet
     * @param argument some argument
     * @param argumentSet a set of arguments
     * @return "true" if some argument in argumentSet attacks argument
     */
    public boolean isAttackedBy(BArgument argument, Collection<BArgument> argumentSet) {
        for (BArgument arg: argumentSet) {
            if (this.isAttackedBy(argument, arg)) {
                return true;
            }
        }
        return false;
    }

    /**
     * checks whether some argument in argumentSet1 is attacked by any argument in argumentSet2
     * @param argumentSet1 a set of arguments
     * @param argumentSet2 a set of arguments
     * @return "true" if some argument in argumentSet2 attacks any argument in argumentSet1
     */
    public boolean isAttackedBy(Collection<BArgument> argumentSet1, Collection<BArgument> argumentSet2) {
        for (BArgument argument: argumentSet1) {
            if (this.isAttackedBy(argument, argumentSet2)) {
                return true;
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
        if (supporter.isEmpty())
            throw new IllegalArgumentException("Supporting set cannot be empty");
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

    /**
     * translates this necessity argumentation framework into an evidential argumentation framework
     * Translation algorithm from:
     * Polberg, Oren. Revisiting Support in Abstract Argumentation Systems. 2014
     * @return the corresponding evidential argumentation framework
     */
    public EvidentialArgumentationFramework toEAF() {
        EvidentialArgumentationFramework eaf = new EvidentialArgumentationFramework();
        // arguments and attacks are identical
        eaf.addAll(this);
        eaf.addAllAttacks(this.getAttacks());
        //handle support relations
        for (BArgument argument: this) {
            Set<BipolarEntity> supporters = this.getDirectSupporters(argument);
            if (supporters.isEmpty()) {
                // an argument with no supporters in this NAF is considered prima-facie in the corresponding EAF
                eaf.addPrimaFacie(argument);
            } else {
                //arguments with support from any argument in NAF are supported in EAF
                // by all permutations of the supporters in NAF
                Set<Set<BArgument>> supportingSets = new HashSet<>();
                for (BipolarEntity bipolarEntity: supporters) {
                    supportingSets.add(new HashSet<>((ArgumentSet)bipolarEntity));
                }
                for (Set<BArgument> supporter: new SetTools<BArgument>().permutations(supportingSets)) {
                    Support supp = new SetSupport(supporter, argument);
                    eaf.add(supp);
                }
            }
        }
        return eaf;
    }

    /**
     * translates this NAF into the corresponding framework with support in a deductive sense
     * only works for NAFs which contain only binary support relations
     * See Cayrol, Lagasquie-Schiex. Bipolarity in argumentation graphs: Towards a better understanding. 2013
     * @return the corresponding DAF
     */
    public DeductiveArgumentationFramework toDAF() {
        DeductiveArgumentationFramework daf = new DeductiveArgumentationFramework();
        // arguments and attacks are the same
        daf.addAll(this);
        daf.addAllAttacks(this.getAttacks());
        // every support relation is reversed
        for (Support supp: this.getSupports()) {
            SetSupport support = (SetSupport) supp;
            Iterator<BArgument> iterator = support.getSupporter().iterator();
            BArgument supporter = iterator.next();
            if (iterator.hasNext()){
                throw new IllegalArgumentException("Framework can only have binary supports");
            }
            daf.addSupport(support.getSupported(), supporter);
        }
        return daf;
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