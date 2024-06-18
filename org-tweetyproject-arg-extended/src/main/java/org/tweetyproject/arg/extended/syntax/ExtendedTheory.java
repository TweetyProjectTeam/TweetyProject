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

package org.tweetyproject.arg.extended.syntax;

import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.*;
import org.tweetyproject.commons.BeliefSet;
import org.tweetyproject.commons.Signature;

import java.util.*;

/**
 * Implementation of extended argumentation frameworks in the sense of Modgil
 *
 * @see "Sanjay Modgil. Reasoning about Preferences in Argumentation Frameworks. Artificial Intelligence, 2009."
 *
 * @author Lars Bengel
 */
public class ExtendedTheory extends BeliefSet<Argument, DungSignature> implements ArgumentationFramework<Argument>, Collection<Argument> {

    protected Map<Argument, Collection<DungEntity>> children;
    protected Map<DungEntity, Collection<Argument>> parents;

    /**
     * Initializes an empty Extended Theory
     */
    public ExtendedTheory() {
        this.children = new HashMap<>();
        this.parents = new HashMap<>();
    }

    /**
     * Determines whether the given set of arguments contains no conflicts, i.e., there are no attacks between any of its members
     * @param ext some set of arguments
     * @return "true" if there is no attack between any of the arguments
     */
    public boolean isConflictFree(Collection<Argument> ext) {
        for (Attack att: getBinaryAttacks()) {
            if (ext.contains(att.getAttacker()) && ext.contains(att.getAttacked())) {
                if (!getAttacked(ext).contains(att) || this.children.getOrDefault(att.getAttacked(), new HashSet<>()).contains(att.getAttacker())) {
                    return false;
                }
            }
        }
        return true;
    }

    /**
     * Determines whether there is an undisturbed attack from 'a' to 'b' given 'ext', i.e., whether 'a' attacks 'b' and
     * there is no extended attack from some element of 'ext' onto the attack (a,b)
     *
     * @param a some argument
     * @param b some argument
     * @param ext some set of arguments
     * @return "true" if 'a' attacks 'b' undisturbed given 'ext'
     */
    public boolean isUndisturbedAttack(Argument a, Argument b, Collection<Argument> ext) {
        if (!isAttacked(b, a)) return false;
        return !getAttacked(ext).contains(new Attack(a, b));
    }

    /**
     * Determines whether there is an undisturbed attack from 'a' to 'b' given 'ext', i.e., whether 'a' attacks 'b' and
     * there is no extended attack from some element of 'ext' onto the attack (a,b)
     *
     * @param att some attack (a,b)
     * @param ext some set of arguments
     * @return "true" if 'a' attacks 'b' undisturbed given 'ext'
     */
    public boolean isUndisturbedAttack(Attack att, Collection<Argument> ext) {
        return isUndisturbedAttack(att.getAttacker(), att.getAttacked(), ext);
    }

    /**
     * Determines whether 'W' is a reinstatement set for the attack (a,b)
     * A set of attacks W is a reinstatement set for an attack (a,b) iff (a,b) is an attack undisturbed by the set S and
     * W contains all attack originating from S necessary to reinstate the attack (a,b) against all its attacks
     *
     * @param W some set of attacks
     * @param a some argument
     * @param b some argument
     * @param S some set of arguments
     * @return "true" iff 'W' is a reinstatement for (a,b)
     */
    public boolean isReinstatementSet(Collection<Attack> W, Argument a, Argument b, Collection<Argument> S) {
        if (!isUndisturbedAttack(a, b, S)) throw new IllegalArgumentException("a does not attack b undisturbed by S");
        if (!W.contains(new Attack(a, b))) return false;
        for (Attack att: W) {
            if (!S.contains(att.getAttacker())) return false;
            if (!isUndisturbedAttack(att, S)) {
                return false;
            }
            Collection<Argument> attackers = this.parents.getOrDefault(att, new HashSet<>());
            for (Argument attacker: attackers) {
                boolean isDisabled = false;
                for (Attack att1: W) {
                    if (attacker.equals(att1.getAttacked())) {
                        isDisabled = true;
                        break;
                    }
                }
                if (!isDisabled) return false;
            }
        }
        return true;
    }

    /**
     * Minimizes the given Reinstatement set for (a,b)
     *
     * @param W some set of attacks
     * @param a some argument
     * @param b some argument
     * @param S some set of arguments
     * @return the minimal reinstatement set for (a,b)
     */
    public Collection<Attack> minimizeReinstatementSet(Collection<Attack> W, Argument a, Argument b, Collection<Argument> S) {
        Collection<Attack> W_min = new HashSet<>();
        W_min.add(new Attack(a, b));
        Collection<Argument> attackers = getAttackers(new Attack(a, b));
        int size_W;
        do {
            size_W = W_min.size();
            for (Attack att : W) {
                if (attackers.contains(att.getAttacked())) {
                    W_min.add(att);
                    attackers.addAll(getAttackers(att));
                }
            }
        } while (size_W != W_min.size());
        return W_min;
    }

    /**
     * Computes the reinstatement set for the attack (a,b)
     *
     * @param a some argument
     * @param b some argument
     * @param S a set of arguments
     * @return a reinstatement set for the attack (a,b); null if none exists
     */
    public Collection<Attack> getReinstatementSet(Argument a, Argument b, Collection<Argument> S) {
        if (!isUndisturbedAttack(a, b, S)) throw new IllegalArgumentException("a does not attack b undisturbed by S");
        Collection<Attack> W = new HashSet<>();
        for (Attack att: getBinaryAttacks()) {
            if (S.contains(att.getAttacker()) && isUndisturbedAttack(att, S)) {
                W.add(att);
            }
        }
        if (!W.contains(new Attack(a, b)) || !isReinstatementSet(W, a, b, S)) return null;
        return W;
    }

    /**
     * Determines whether there exists a reinstatement set for the attack (a,b)
     *
     * @param a some argument
     * @param b some argument
     * @param S a set of arguments
     * @return "true" iff there exists a reinstatement set for the attack (a,b)
     */
    public boolean existsReinstatementSet(Argument a, Argument b, Collection<Argument> S) {
        return getReinstatementSet(a, b, S) != null;
    }

    /**
     * Determines whether the argument 'arg' is defended by the set 'ext'
     *
     * @param arg some argument
     * @param ext some set of arguments
     * @return "true" iff 'arg' is defended by 'ext'
     */
    public boolean isAcceptable(Argument arg, Collection<Argument> ext) {
        for (Argument b: this.parents.getOrDefault(arg, new HashSet<>())) {
            if (isUndisturbedAttack(b, arg, ext)) {
                boolean isCounterattacked = false;
                for (Argument c: this.parents.getOrDefault(b, new HashSet<>())) {
                    if (ext.contains(c) && isUndisturbedAttack(c, b, ext) && existsReinstatementSet(c, b, ext)) {
                        isCounterattacked = true;
                        break;
                    }
                }
                if (!isCounterattacked) return false;
            }
        }
        return true;
    }

    /**
     * Determines whether 'ext' is admissible, i.e., conflict-free and defends all its arguments
     * @param ext some set of arguments
     * @return "true" iff 'ext' is admissible
     */
    public boolean isAdmissible(Collection<Argument> ext) {
        if (!isConflictFree(ext)) return false;
        for (Argument arg: ext) {
            if (!isAcceptable(arg, ext)) return false;
        }
        return true;
    }

    /**
     * Determines whether 'ext' is complete, i.e., admissible and contains all arguments defended by it
     * @param ext some set of arguments
     * @return "true" iff 'ext' is complete
     */
    public boolean isComplete(Collection<Argument> ext) {
        if (!isAdmissible(ext)) return false;
        for (Argument arg: this) {
            if (ext.contains(arg)) continue;
            if (isAcceptable(arg, ext)) return false;
        }
        return true;
    }

    /**
     * Computes the set of attackers of 'attack'
     * @param attack some attack
     * @return the attackers of 'attack'
     */
    public Collection<Argument> getAttackers(Attack attack) {
        return new HashSet<>(this.parents.getOrDefault(attack, new HashSet<>()));
    }

    /**
     * Computes the set of attackers of 'argument'
     * @param argument some argument
     * @return the attackers of 'argument'
     */
    public Collection<Argument> getAttackers(Argument argument) {
        return new HashSet<>(this.parents.getOrDefault(argument, new HashSet<>()));
    }

    /**
     * Computes the set of attackers of 'entities'
     * @param entities a set of arguments and attacks
     * @return the attackers of 'entities'
     */
    public Collection<Argument> getAttackers(Collection<DungEntity> entities) {
        Collection<Argument> attackers = new HashSet<>();
        for (DungEntity ent: entities) {
            attackers.addAll(this.parents.getOrDefault(ent, new HashSet<>()));
        }
        return attackers;
    }

    /**
     * Computes the set of Arguments/Attacks attacked by 'argument'
     * @param argument some argument
     * @return the set of Arguments/Attacks attacked by 'argument'
     */
    public Collection<DungEntity> getAttacked(Argument argument) {
        return new HashSet<>(this.children.getOrDefault(argument, new HashSet<>()));
    }

    /**
     * Computes the set of Arguments/Attacks attacked by 'ext'
     * @param ext some set of arguments
     * @return the set of Arguments/Attacks attacked by 'ext'
     */
    public Collection<DungEntity> getAttacked(Collection<Argument> ext) {
        Collection<DungEntity> attacked = new HashSet<>();
        for (Argument arg: ext) {
            attacked.addAll(this.children.getOrDefault(arg, new HashSet<>()));
        }
        return attacked;
    }

    /**
     * Transforms the extended theory into a standard dung theory by flattening the extended attacks via creating meta-arguments
     * @return the flattened theory
     */
    public DungTheory flatten() {
        DungTheory theory = new DungTheory();
        theory.addAll(this);
        for (Attack att: getBinaryAttacks()) {
            Argument att1 = new Argument(String.format("att1_%s", att));
            Argument att2 = new Argument(String.format("att2_%s", att));
            theory.add(att1, att2);
            theory.addAttack(att.getAttacker(), att1);
            theory.addAttack(att1, att2);
            theory.addAttack(att2, att.getAttacked());
        }
        for (ExtendedAttack att: getExtendedAttacks()) {
            Argument att1 = new Argument(String.format("att1_%s", att));
            Argument att2 = new Argument(String.format("att2_%s", att));
            theory.add(att1, att2);
            theory.addAttack(att.getAttacker(), att1);
            theory.addAttack(att1, att2);
            theory.addAttack(att2, new Argument(String.format("att2_%s", att.getAttacked())));
        }
        return theory;
    }

    @Override
    public boolean contains(Object o) {
        if (o instanceof Argument) {
            return this.formulas.contains((Argument) o);
        } else if (o instanceof Attack) {
            return this.getBinaryAttacks().contains((Attack) o);
        } else if (o instanceof ExtendedAttack) {
            return getAllAttacks().contains((ExtendedAttack) o);
        }
        return false;
    }

    @Override
    protected DungSignature instantiateSignature() {
        return new DungSignature();
    }

    @Override
    public boolean add(Argument argument) {
        return super.add(argument);
    }

    /**
     * Adds an attack to the theory
     * @param attacker some argument
     * @param attacked some argument
     * @return true iff the attack has been added successfully
     */
    public boolean addAttack(Argument attacker, Argument attacked){
        if (!contains(attacker) || !contains(attacked)) return false;
        boolean result = false;
        if(!parents.containsKey(attacked))
            parents.put(attacked, new HashSet<>());
        result |= parents.get(attacked).add(attacker);
        if(!children.containsKey(attacker))
            children.put(attacker, new HashSet<>());
        result |= children.get(attacker).add(attacked);
        return result;
    }

    /**
     * Adds an attack to the theory
     * @param attacker some argument
     * @param attacked some attack
     * @return true iff the attack has been added successfully
     */
    public boolean addAttack(Argument attacker, Attack attacked){
        if (!getBinaryAttacks().contains(attacked) || !this.contains(attacker)) {
            return false;
        }
        boolean result = false;
        if(!parents.containsKey(attacked))
            parents.put(attacked, new HashSet<>());
        result |= parents.get(attacked).add(attacker);
        if(!children.containsKey(attacker))
            children.put(attacker, new HashSet<>());
        result |= children.get(attacker).add(attacked);
        return result;
    }

    @Override
    public boolean remove(Object o) {
        throw new UnsupportedOperationException("Unsupported");
    }

    @Override
    public boolean removeAll(Collection<?> c) {
        throw new UnsupportedOperationException("Unsupported");
    }

    @Override
    public boolean retainAll(Collection<?> c) {
        throw new UnsupportedOperationException("Unsupported");
    }

    @Override
    public void clear() {
        throw new UnsupportedOperationException("Unsupported");
    }

    @Override
    public boolean isAttacked(Argument a, Extension<? extends ArgumentationFramework<?>> ext) {
        return getAttacked(ext).contains(a);
    }

    /**
     * Determines whether 'a' is attacked by 'b' in the theory
     * @param a some argument
     * @param b some argument
     * @return "true" iff 'b' attacks 'a'
     */
    public boolean isAttacked(Argument a, Argument b) {
        return children.getOrDefault(b, new HashSet<>()).contains(a);
    }

    /**
     * Determines whether 'a' is attacked by 'b' in the theory
     * @param a some attack
     * @param b some argument
     * @return "true" iff 'b' attacks 'a'
     */
    public boolean isAttacked(Attack a, Argument b) {
        return children.getOrDefault(b, new HashSet<>()).contains(a);
    }

    @Override
    public Collection<Argument> getNodes() {
        return this;
    }

    /**
     * Computes the set of all attacks in the theory
     * @return the set of all attacks of the theory
     */
    public Collection<DungEntity> getAllAttacks() {
        Collection<DungEntity> result = new HashSet<>();
        result.addAll(getExtendedAttacks());
        result.addAll(getBinaryAttacks());
        return result;
    }

    /**
     * Computes the set of all extended attack, i.e., the attacks from arguments to attacks
     * @return the set of extended attacks of the theory
     */
    public Collection<ExtendedAttack> getExtendedAttacks() {
        Set<ExtendedAttack> attacks = new HashSet<>();
        for(Argument a: this) {
            if(this.children.containsKey(a)) {
                for(DungEntity b: this.children.get(a)) {
                    if (b instanceof Attack) {
                        attacks.add(new ExtendedAttack(a, b));
                    }
                }
            }
        }
        return attacks;
    }

    /**
     * Computes the set of all binary attacks, i.e., the attacks between arguments
     * @return the set of binary attacks of the theory
     */
    public Collection<Attack> getBinaryAttacks() {
        Set<Attack> attacks = new HashSet<>();
        for(Argument a: this) {
            if(this.children.containsKey(a)) {
                for(DungEntity b: this.children.get(a)) {
                    if (b instanceof Argument) {
                        attacks.add(new Attack(a, (Argument) b));
                    }
                }
            }
        }
        return attacks;
    }

    @Override
    public Signature getMinimalSignature() {
        return new DungSignature(this);
    }

    /**
     * Pretty Prints the extended theory
     * @return a string representation of the extended theory
     */
    public String prettyPrint(){
        StringBuilder output = new StringBuilder();
        for (Argument argument : this) {
            output.append("argument(").append(argument.toString()).append(").\n");
        }
        output.append("\n");
        for (DungEntity dungEntity : this.getAllAttacks()) {
            output.append("attack").append(dungEntity.toString()).append(".\n");
        }
        return output.toString();
    }
}
