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
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.DungTheory;

import java.util.*;

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
 * @author Lars Bengel
 *
 */
public class DeductiveArgumentationFramework extends AbstractBipolarFramework implements Comparable<DeductiveArgumentationFramework> {
    /**
     * Default constructor; initializes empty sets of arguments, attacks and supports
     */
    public DeductiveArgumentationFramework() {
        super();
    }

    /**
     * Determines if ext is closed under the support relation of this framework
     * i.e., if all arguments supported by ext are an element of ext
     * @param ext an extension
     * @return true if ext is closed under R_sup
     */
    public boolean isClosed(ArgumentSet ext){
        for(BArgument a: ext) {
            Set<BArgument> supportedArguments = this.getDirectSupported(a);
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
    public Set<BinaryAttack> getDeductiveComplexAttacks(){
        Set<BinaryAttack> dAttacks = this.getComplexAttacks();
        Set<BinaryAttack> superMediatedAttacks = new HashSet<>();
        for(BinaryAttack attack: dAttacks) {
            Set<BArgument> superMediatedOrigins = this.getSupporters(attack.getAttacker());
            Set<BArgument> superMediatedTargets = this.getSupporters(attack.getAttacked());

            for(BArgument origin: superMediatedOrigins) {
                superMediatedAttacks.add(new BinaryAttack(origin, attack.getAttacked()));
            }
            for(BArgument target: superMediatedTargets) {
                superMediatedAttacks.add(new BinaryAttack(attack.getAttacker(), target));
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
    public Set<BinaryAttack> getComplexAttacks(){
        Set<Attack> attacks = this.getAttacks();
        Set<BinaryAttack> cAttacks= new HashSet<>();
        for (Attack a: attacks)
            cAttacks.add((BinaryAttack) a);

        for(BArgument argument: this) {
            Set<BArgument> supportedArguments = this.getSupported(argument);
            Set<BArgument> supportedAttackTargets = new HashSet<>();
            Set<BArgument> mediatedAttackOrigins = new HashSet<>();
            for(BArgument arg: supportedArguments) {
                supportedAttackTargets.addAll(this.getAttacked(arg));
                for (BipolarEntity bipolarEntity: this.getAttackers(arg)) {
                    mediatedAttackOrigins.add((BArgument) bipolarEntity);
                }
            }
            for(BArgument target: supportedAttackTargets) {
                cAttacks.add(new BinaryAttack(argument, target));
            }
            for(BArgument origin: mediatedAttackOrigins) {
                cAttacks.add(new BinaryAttack(origin, argument));
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
    public Set<BinaryAttack> getMediatedAttacks(BArgument arg1){
        Set<BArgument> attackedArguments = this.getAttacked(arg1);
        Set<BArgument> mediatedAttackTargets = new HashSet<>();
        for(BArgument a: attackedArguments) {
            mediatedAttackTargets.addAll(this.getSupporters(a));
        }
        Set<BinaryAttack> mediatedAttacks = new HashSet<>();
        for(BArgument target: mediatedAttackTargets) {
            mediatedAttacks.add(new BinaryAttack(arg1, target));
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
    public boolean isMediatedAttack(BArgument arg1, BArgument arg2){
        Set<BArgument> supportedArguments = this.getSupported(arg2);
        return this.isAttacking(arg1, supportedArguments);
    }

    /**
     * Checks whether there exists a super-mediated attack from "arg1" to "arg2", i.e. whether there
     * is a sequence of direct supports from "arg2" to "x" and a direct or supported attack from "arg1" to "x".
     * @param arg1 an argument.
     * @param arg2 an argument.
     * @return "true" iff there is a super-mediated attack from "arg1" to "arg2".
     */
    public boolean isSuperMediatedAttack(BArgument arg1, BArgument arg2){
        Set<BArgument> supportedArguments1 = this.getSupported(arg1);
        Set<BArgument> supportedArguments2 = this.getSupported(arg2);
        return this.isMediatedAttack(arg1, arg2) || this.isAttacking(supportedArguments1, supportedArguments2);
    }

    /**
     * Calculates the set of supported attack from "arg1" to other arguments "y", i.e. whether there
     * is a sequence of direct supports from "arg1" to "x" and a direct attack from "x" to "y".
     * @param arg1 an argument.
     * @return set of supported attacks starting from "arg1".
     */
    public Set<BinaryAttack> getSupportedAttacks(BArgument arg1){
        Set<BinaryAttack> supportedAttacks = new HashSet<>();
        Set<BArgument> supportedArguments = this.getSupported(arg1);
        Set<BArgument> supportedAttackTargets = new HashSet<>();
        for(BArgument a: supportedArguments) {
            supportedAttackTargets.addAll(this.getAttacked(a));
        }
        for(BArgument target: supportedAttackTargets) {
            supportedAttacks.add(new BinaryAttack(arg1, target));
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
    public boolean isSupportedAttack(BArgument arg1, BArgument arg2){
        Set<BArgument> supportedArguments = new HashSet<>(this.getSupported(arg1));
        return this.isAttacked(arg2, supportedArguments);
    }

    /**
     * returns true if some argument of argSet is attacked by argument.
     * @param argument an argument
     * @param argSet a set of arguments
     * @return true if some argument of argSet is attacked by argument.
     */
    public boolean isAttacking(BArgument argument, Set<BArgument> argSet) {
        if(!this.attackChildren.containsKey(argument))
            return false;
        for(BArgument attacked: this.attackChildren.get(argument)) {
            if (argSet.contains(attacked))
                return true;
        }
        return false;
    }

    /**
     * returns true if some argument of argumentSet attacks argument.
     * @param argument an argument
     * @param argumentSet  set of arguments
     * @return true if some argument of argumentSet attacks argument.
     */
    public boolean isAttacked(BArgument argument, Set<BArgument> argumentSet) {
        if(!this.attackParents.containsKey(argument))
            return false;
        for(BipolarEntity attacker: this.attackParents.get(argument)) {
            if (argumentSet.contains((BArgument) attacker))
                return true;
        }
        return false;
    }

    /**
     * Checks whether argumentSet1 is attacking argumentSet2
     * ie any element of argumentSet1 is attacking any element of argumentSet2
     * @param argumentSet1 a set of arguments
     * @param argumentSet2 a set of arguments
     * @return "true" if an element of argumentSet1 is attacking any element of argumentSet2
     */
    public boolean isAttacking(Set<BArgument> argumentSet1, Set<BArgument> argumentSet2) {
        for (BArgument argument: argumentSet1) {
            if (this.isAttacked(argument, argumentSet2))
                return true;
        }
        return false;
    }

    /**
     * Computes the set {A | there is a sequence of direct supports from an element of <code>ext</code> to A
     * @param ext an extension ie. a set of arguments
     * @return the set of all arguments that are supported by <code>ext</code>.
     */
    public Set<BArgument> getSupported(Collection<BArgument> ext){
        Set<BArgument> supported = new HashSet<BArgument>();
        for (BArgument argument: ext)
            supported.addAll(this.getSupported(argument));

        return supported;
    }

    /**
     * Computes the set {A | there is a sequence of direct supports from argument to A
     * @param argument an argument
     * @return the set of all arguments that are supported by <code>argument</code>.
     */
    public Set<BArgument> getSupported(BArgument argument){
        return this.getSupported(argument, new HashSet<BArgument>());
    }

    /**
     * Computes the set {A | there is a sequence of direct supports from argument to A}.
     * @param argument an argument
     * @param visited already visited arguments
     * @return the set of all arguments that are supported by <code>argument</code>.
     */
    private Set<BArgument> getSupported(BArgument argument, Set<BArgument> visited){
        if(!this.supportChildren.containsKey(argument))
            return new HashSet<BArgument>();
        Set<BArgument> directSupportedArguments = this.getDirectSupported(argument);
        Set<BArgument> supportedArguments = new HashSet<BArgument>(directSupportedArguments);

        directSupportedArguments.removeAll(visited);
        for(BArgument supported: directSupportedArguments){
            visited.add(supported);
            supportedArguments.addAll(this.getSupported(supported, visited));
        }
        return supportedArguments;
    }

    /**
     * Computes the set {A | there is a sequence of direct supports from A to argument
     * @param argument an argument
     * @return the set of all arguments that support <code>argument</code>.
     */
    public Set<BArgument> getSupporters(BArgument argument){
        return this.getSupporters(argument, new HashSet<BArgument>());
    }

    /**
     * Computes the set {A | there is a sequence of direct supports from A to argument}.
     * @param argument an argument
     * @param visited already visited arguments
     * @return the set of all arguments that support <code>argument</code>.
     */
    private Set<BArgument> getSupporters(BArgument argument, Set<BArgument> visited){
        if(!this.supportParents.containsKey(argument))
            return new HashSet<>();
        Set<BipolarEntity> directSupportArguments = this.getDirectSupporters(argument);
        Set<BArgument> supportArguments = new HashSet<>();
        for (BipolarEntity bipolarEntity: directSupportArguments)
            supportArguments.add((BArgument) bipolarEntity);

        directSupportArguments.removeAll(visited);
        for(BipolarEntity supporter: directSupportArguments){
            visited.add((BArgument) supporter);
            supportArguments.addAll(this.getSupporters((BArgument) supporter, visited));
        }
        return supportArguments;
    }

    /**
     * Adds the given support to this bipolar argumentation framework.
     * @param supp a support
     * @return "true" if the set of supports has been modified.
     */
    public boolean add(Support supp) {
        if (!(supp instanceof BinarySupport)) {
            throw new IllegalArgumentException("Support of type BinarySupport expected");
        } else {
            BinarySupport support = (BinarySupport) supp;
            return this.addSupport(support.getSupporter(), support.getSupported());
        }
    }

    /**
     * Adds a support from the first argument to the second to this bipolar argumentation framework.
     * @param supporter some argument
     * @param supported some argument
     * @return "true" if the set of supports has been modified.
     */
    public boolean addSupport(BArgument supporter, BArgument supported){
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
     * Adds an attack from the first argument to the second to this deductive argumentation system.
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
    public boolean isAcceptable(BArgument argument, Collection<BArgument> ext) {
        return false;
    }

    @Override
    public Set<Support> getSupports(){
        Set<Support> supports = new HashSet<>();
        for(BArgument a: this) {
            if(this.supportChildren.containsKey(a)) {
                for(BArgument b: this.supportChildren.get(a))
                    supports.add(new BinarySupport(a,b));
            }
        }
        return supports;
    }

    @Override
    public Set<Attack> getAttacks() {
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
     * constructs the complete associated dung theory of this bipolar argumentation framework
     * i.e. constructs all deductive complex attacks of BAF and returns dung theory without supports
     * See Cayrol, Lagasquie-Schiex. Bipolarity in argumentation graphs: Towards a better understanding. 2013
     * @return the complete associated Dung Theory of this bipolar argumentation framework
     */
    public DungTheory getCompleteAssociatedDungTheory(){
        DungTheory completeAssociatedDungTheory = new DungTheory();
        Set<BinaryAttack> dAttacks = this.getDeductiveComplexAttacks();
        completeAssociatedDungTheory.addAll(this.getNodes());
        for (BinaryAttack att: dAttacks) {
            completeAssociatedDungTheory.add(new net.sf.tweety.arg.dung.syntax.Attack((Argument) att.getAttacker(), (Argument) att.getAttacked()));
        }
        return completeAssociatedDungTheory;
    }

    /**
     * constructs the meta dung theory of this bipolar argumentation framework by adding
     * meta-arguments for every attack and support relation
     * See Boella et al. Support in Abstract Argumentation. 2010
     * @return the meta dung theory of this bipolar argumentation framework
     */
    public DungTheory getMetaFramework() {
        DungTheory metaFramework = new DungTheory();
        metaFramework.addAll(this);

        Set<Attack> attacks = this.getAttacks();
        for (Attack attack: attacks){
            Argument a = (Argument) attack.getAttacker();
            Argument b = (Argument) attack.getAttacked();
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
            Argument a = (Argument) support.getSupporter();
            Argument b = (Argument) support.getSupported();
            Argument z_ab = new Argument("Z_" + a.getName() + b.getName());
            metaFramework.add(z_ab);
            metaFramework.addAttack(b, z_ab);
            metaFramework.addAttack(z_ab, a);
        }
        return metaFramework;
    }

    @Override
    public int compareTo(DeductiveArgumentationFramework o) {
        return this.hashCode() - o.hashCode();
    }

    /* (non-Javadoc)
     * @see java.lang.Object#hashCode()
     */
    public int hashCode() {
        int prime = 1;
        int result = super.hashCode();
        result = 31 * result + (this.attackParents == null ? 0 : this.attackParents.hashCode());
        result = 43 * result + (this.supportParents == null ? 0 : this.supportParents.hashCode());
        return result;
    }
}

