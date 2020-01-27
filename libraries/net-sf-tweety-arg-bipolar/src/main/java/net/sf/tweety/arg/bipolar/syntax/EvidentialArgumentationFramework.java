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

import net.sf.tweety.commons.util.SetTools;

import java.util.*;

/**
 * This class implements a bipolar abstract argumentation theory with support in an evidential sense.
 * ie. we distinguish between prima-facie and standard arguments. Prima-facie arguments do not
 * require any support from other arguments to stand, while standard arguments must be supported
 * by at least one prima-facie argument.
 * <br>
 * <br>See
 * <br>
 * <br>Polberg, Oren. Revisiting Support in Abstract Argumentation Systems. 2014
 *
 *
 * @author Lars Bengel
 *
 */
public class EvidentialArgumentationFramework extends AbstractBipolarFramework implements Comparable<EvidentialArgumentationFramework> {

    /**
     * Special argument Eta, which serves as a representation of the evidence/environment.
     */
    private BArgument eta;

    /**
     * Default constructor; initializes empty sets of arguments, attacks and supports
     */
    public EvidentialArgumentationFramework(){
        super();
        this.eta = new BArgument("eta");
        this.add(eta);
    }

    /**
     * returns true if argument has evidential support from set <code>ext</code>.
     * An argument a has e-support from ext iff a=eta or there exists a subset S
     * of ext which supports a and all arguments in S have e-support from ext \ {a}
     * @param argument an argument
     * @param ext a set of arguments
     * @return true if argument has e-support from <code>ext</code>.
     */
    public boolean hasEvidentialSupport(BArgument argument, Collection<BArgument> ext){
        if(argument == this.eta)
            return true;
        Set<BArgument> extWithoutArgument = new HashSet<BArgument>(ext);
        extWithoutArgument.remove(argument);
        Set<Set<BArgument>> subsets = new SetTools<BArgument>().subsets(ext);
        for(Collection<BArgument> subExt: subsets) {
            if (!subExt.isEmpty() && this.isDirectSupportedBy(argument, new ArgumentSet(subExt))) {
                boolean evidentialSupport = true;
                for (BArgument x : subExt) {
                    if (!this.hasEvidentialSupport(x, extWithoutArgument)) {
                        evidentialSupport = false;
                        break;
                    }
                }
                if (evidentialSupport)
                    return true;
            }
        }
        return false;
    }

    /**
     * returns true if argument has minimal evidential support from set <code>ext</code>.
     * An argument a has minimal e-support from ext iff ext e-supports a and
     * no true subset of ext e-supports a
     * @param argument an argument
     * @param ext a set of arguments
     * @return true if argument has e-support from <code>ext</code>.
     */
    public boolean hasMinimalEvidentialSupport(BArgument argument, Collection<BArgument> ext){
        if (!this.hasEvidentialSupport(argument, ext))
            return false;

        Set<Set<BArgument>> subsets = new SetTools<BArgument>().subsets(ext);
        subsets.remove(ext);
        for (Collection<BArgument> subExt: subsets) {
            if (this.hasEvidentialSupport(argument, subExt))
                return false;
        }
        return true;
    }

    /**
     * computes all subsets which minimal e-support argument
     * @param argument some argument
     * @return set of minimal e-supporters of argument
     */
    public Set<Set<BArgument>> getMinimalEvidentialSupporters(BArgument argument) {
        Set<Set<BArgument>> result = new HashSet<>();
        Set<BipolarEntity> supportingSets = this.getDirectSupporters(argument);
        Set<Set<BArgument>> subsets = new SetTools<BArgument>().subsets(this);
        for (Set<BArgument> subSet: subsets) {
            for (BipolarEntity bipolarEntity: supportingSets) {
                ArgumentSet supportingSet = (ArgumentSet) bipolarEntity;
                if (subSet.contains(this.getEta()) && subSet.containsAll(supportingSet) && this.hasMinimalEvidentialSupport(argument, subSet)) {
                    result.add(subSet);
                }
            }
        }
        return result;
    }

    /**
     * returns true if <code>ext</code> carries out an evidence supported attack on argument
     * ext e-support-attacks an argument a iff a subset S of ext attacks a and all elements of S have e-support from ext.
     * @param argument an argument
     * @param ext a set of arguments
     * @return true if <code>ext</code> e-support-attacks argument
     */
    public boolean isEvidenceSupportedAttack(Collection<BArgument> ext, BArgument argument){
        Set<Set<BArgument>> subsets = new SetTools<BArgument>().subsets(ext);
        for(Collection<BArgument> subExt: subsets) {
            if (this.isAttackedBy(argument, new ArgumentSet(subExt))) {
                boolean evidentialSupport = true;
                for (BArgument attacker : subExt) {
                    if (!this.hasEvidentialSupport(attacker, ext)) {
                        evidentialSupport = false;
                        break;
                    }
                }

                if (evidentialSupport)
                    return true;
            }
        }
        return false;
    }

    /**
     * returns true if <code>ext</code> carries out a minimal evidence supported attack on argument
     * i.e. there is no true subset of <code>ext</code> which e-support-attacks argument
     * @param argument an argument
     * @param ext a set of arguments
     * @return true if <code>ext</code> e-support-attacks argument
     */
    public boolean isMinimalEvidenceSupportedAttack(Collection<BArgument> ext, BArgument argument){
        if (!this.isEvidenceSupportedAttack(ext, argument))
            return false;
        Set<Set<BArgument>> subsets = new SetTools<BArgument>().subsets(ext);
        subsets.remove(ext);
        for (Collection<BArgument> subExt: subsets) {
            if (this.isEvidenceSupportedAttack(subExt, argument))
                return false;
        }
        return true;
    }

    /**
     * computes all subsets which carry out a minimal evidence supported attack on argument
     * @param argument some argument
     * @return set of minimal e-supported attackers of argument
     */
    public Collection<Collection<BArgument>> getMinimalEvidenceSupportedAttackers(BArgument argument) {
        Collection<Collection<BArgument>> result = new HashSet<>();
        Set<BipolarEntity> attackingSets = this.getAttackingSets(argument);
        Set<Set<BArgument>> subsets = new SetTools<BArgument>().subsets(this);
        for (Set<BArgument> subSet: subsets) {
            for (BipolarEntity bipolarEntity: attackingSets) {
                ArgumentSet attackingSet = (ArgumentSet) bipolarEntity;
                if (subSet.contains(this.getEta()) && subSet.containsAll(attackingSet) && this.isMinimalEvidenceSupportedAttack(subSet, argument)) {
                    result.add(subSet);
                }
            }
        }
        return result;

    }

    /**
     * return true if argument is acceptable with respect to <code>ext</code>
     * argument is acceptable wrt. S iff argument is e-supported by S and if
     * a set T minimal e-support-attacks argument, then S carries out an
     * e-supported attack against a member of T.
     * @param argument an argument
     * @param ext a set of arguments
     * @return true if argument is acceptable wrt. <code>ext</code>
     */
    public boolean isAcceptable(BArgument argument, Collection<BArgument> ext){
        //TODO efficiency
        if (!this.hasEvidentialSupport(argument, ext)) {
            return false;
        }
        boolean result = true;
        for (Collection<BArgument> attackingSet: this.getMinimalEvidenceSupportedAttackers(argument)) {
            boolean isAttackingSet = false;
            for (BArgument attacker: attackingSet) {
                if (this.isEvidenceSupportedAttack(ext, attacker)) {
                    isAttackingSet = true;
                    break;
                }
            }
            result &= isAttackingSet;
        }
        return result;
    }

    /**
     * The characteristic function of an abstract argumentation framework: F_ES(S) = {A|A is acceptable wrt. S}.
     * @param extension an extension (a set of arguments).
     * @return an extension (a set of arguments).
     */
    public ArgumentSet fes(ArgumentSet extension){
        ArgumentSet newExtension = new ArgumentSet();
        for (BArgument argument : this) {
            if (this.isAcceptable(argument, extension))
                newExtension.add(argument);
        }
        return newExtension;
    }

    /**
     * Computes the set {A | there is a sequence of direct supports from argumentSet to A}
     * @param argumentSet a set of arguments
     * @return the set of all arguments that are supported by <code>argumentSet</code>.
     */
    public Set<BArgument> getSupported(ArgumentSet argumentSet){
        //TODO FIX set-form
        return this.getSupported(argumentSet, new HashSet<BArgument>());
    }

    /**
     * Computes the set {A | there is a sequence of direct supports from argument to A}.
     * @param argumentSet a set of arguments
     * @param visited already visited arguments
     * @return the set of all arguments that are supported by <code>argumentSet</code>.
     */
    private Set<BArgument> getSupported(ArgumentSet argumentSet, Set<BArgument> visited){
        if(!this.supportChildren.containsKey(argumentSet)) {
            return new HashSet<BArgument>();
        }
        Set<BArgument> directSupportedArguments = this.getDirectSupported(argumentSet);
        Set<BArgument> supportedArguments = new HashSet<>(directSupportedArguments);

        directSupportedArguments.removeAll(visited);
        for(BArgument supported: directSupportedArguments){
            visited.add(supported);
            supportedArguments.addAll(this.getSupported(new ArgumentSet(supported), visited));
        }
        return supportedArguments;
    }

    /**
     * Computes the set {S | (S, argument) in attacks}.
     * @param argument an argument
     * @return the set of all argument sets that are attacking <code>argument</code>.
     */
    public Set<BipolarEntity> getAttackingSets(BArgument argument){
        if(!this.attackParents.containsKey(argument))
            return new HashSet<>();
        return new HashSet<>(this.attackParents.get(argument));
    }

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
        if (supported.equals(this.getEta())) {
            throw new IllegalArgumentException("Eta can not be supported by another argument");
        }
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
    public boolean add(Attack att) {
        if(att instanceof BinaryAttack) {
            return this.addAttack((BArgument) att.getAttacker(), (BArgument) att.getAttacked());
        } else if(att instanceof SetAttack) {
            return this.addAttack((ArgumentSet) att.getAttacker(), (BArgument) att.getAttacked());
        }
        return true;
    }

    /**
     * Adds an attack from the first argument to the second to this evidential argumentation system.
     * @param attacker some argument
     * @param attacked some argument
     * @return "true" if the set of attacks has been modified.
     */
    public boolean addAttack(BArgument attacker, BArgument attacked){
        return addAttack(new ArgumentSet(attacker), attacked);
    }

    /**
     * Adds an attack from the set of arguments to the argument to this evidential argumentation system.
     * @param attacker a set of arguments
     * @param attacked some argument
     * @return "true" if the set of attacks has been modified.
     */
    public boolean addAttack(ArgumentSet attacker, BArgument attacked){
        if (attacked.equals(this.getEta()) || attacker.contains(this.getEta())) {
            throw new IllegalArgumentException("Eta is not allowed to be part of any attack relation.");
        }
        boolean result = false;
        if(!attackParents.containsKey(attacked))
            attackParents.put(attacked, new HashSet<>());
        result |= attackParents.get(attacked).add(attacker);
        if(!attackChildren.containsKey(attacker))
            attackChildren.put(attacker, new HashSet<>());
        result |= attackChildren.get(attacker).add(attacked);
        return result;
    }

    /**
     * Returns all supports of this theory.
     * @return all supports of this theory.
     */
    public Set<Support> getSupports(){
        Set<Support> supports = new HashSet<Support>();
        for(BArgument a: this) {
            if(this.supportParents.containsKey(a)) {
                for(BipolarEntity b: this.supportParents.get(a))
                    supports.add(new SetSupport((ArgumentSet) b, a));
            }
        }
        return supports;
    }

    /**
     * Returns all attacks of this theory.
     * @return all attacks of this theory.
     */
    public Set<Attack> getAttacks(){
        Set<Attack> attacks = new HashSet<Attack>();
        for(BArgument a: this) {
            if(this.attackParents.containsKey(a)) {
                for(BipolarEntity b: this.attackParents.get(a))
                    attacks.add(new SetAttack((ArgumentSet) b, a));
            }
        }
        return attacks;
    }

    /**
     * Adds a argument with evidential support to this evidential argumentation system
     * If the argument is already in this evidential argumentation system, adds evidential support
     * @param argument some argument
     * @return "true" if the argument has been modified.
     */
    public boolean addPrimaFacie(BArgument argument){
        boolean result = false;
        if (!this.contains(argument)) {
            result |= this.add(argument);
        }
        result |= this.addSupport(this.getEta(), argument);

        return result;
    }

    /**
     * removes evidential support from argument, does not remove argument itself
     * @param argument some argument
     * @return "true" if the argument has been modified
     */
    public boolean removePrimaFacie(BArgument argument){
        return this.remove(new SetSupport(this.getEta(), argument));
    }

    /**
     * returns all arguments that have evidential support in this framework
     * @return set of evidence supported arguments
     */
    public Set<BArgument> getEvidenceSupportedArguments() {
        return this.getSupported(new ArgumentSet(this.getEta()));
    }

    /**
     * returns the special argument eta
     * @return eta
     */
    public BArgument getEta() {
        return eta;
    }

    /**
     *
     * @return the minimal form of this evidential argumentation system
     */
    public EvidentialArgumentationFramework getMinimalForm() {
        Set<Attack> attacks = this.getAttacks();
        Set<Support> supports = this.getSupports();

        EvidentialArgumentationFramework minimalEvidentialArgSystem = new EvidentialArgumentationFramework();
        minimalEvidentialArgSystem.addAll(this);

        for (Attack a: attacks) {
            if (!(a instanceof SetAttack)) {
                throw new IllegalArgumentException("Attack of type SetAttack expected");
            } else {
                SetAttack attack = (SetAttack) a;
                Set<Set<BArgument>> subsets = new SetTools<BArgument>().subsets(attack.getAttacker());
                subsets.remove(new HashSet<BArgument>(attack.getAttacker()));
                boolean minimal = true;
                for (Set<BArgument> subSet : subsets) {
                    if (this.isAttackedBy(attack.getAttacked(), new ArgumentSet(subSet))) {
                        minimal = false;
                        break;
                    }
                }
                if (minimal)
                    minimalEvidentialArgSystem.add(attack);
            }
        }

        for (Support support: supports) {
            Set<Set<BArgument>> subsets = new SetTools<BArgument>().subsets((ArgumentSet)support.getSupporter());
            subsets.remove(new HashSet<BArgument>((ArgumentSet)support.getSupporter()));
            boolean minimal = true;
            for (Set<BArgument> subSet: subsets) {
                if (this.isDirectSupportedBy((BArgument)support.getSupported(), new ArgumentSet(subSet))) {
                    minimal = false;
                    break;
                }
            }
            if (minimal)
                minimalEvidentialArgSystem.add(support);


        }

        return minimalEvidentialArgSystem;
    }

    @Override
    public int compareTo(EvidentialArgumentationFramework o) {
        return this.hashCode() - o.hashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        int prime = 1;
        int result = super.hashCode();
        result = 31 * result + (this.attackParents == null ? 0 : this.attackParents.hashCode());
        result = 41 * result + (this.supportParents == null ? 0 : this.supportParents.hashCode());
        return result;
    }
}
