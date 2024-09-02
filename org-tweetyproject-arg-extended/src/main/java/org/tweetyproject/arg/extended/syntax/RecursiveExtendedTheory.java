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
  * Implementation of argumentation frameworks with recursive attacks in the sense of Baroni et al.
  *
  * @see "Pietro Baroni, Federico Cerutti, Massimiliano Giacomin, and Giovanni Guida. AFRA: Argumentation framework with recursive attacks. International Journal of Approximate Reasoning, 2011."
  *
  * @author Lars Bengel
  */
 public class RecursiveExtendedTheory extends BeliefSet<Argument, DungSignature> implements ArgumentationFramework<Argument>, Collection<Argument> {
    /** children */
    protected Map<Argument, Collection<DungEntity>> children;
    /** parents
     */
     protected Map<DungEntity, Collection<Argument>> parents;

     /**
      * Initializes a new empty recursive extended theory.
      */
     public RecursiveExtendedTheory() {
         this.children = new HashMap<>();
         this.parents = new HashMap<>();
     }

     /**
      * Determines whether 'attack' attacks 'target'.
      *
      * @param attack some extended attack
      * @param target some argument/attack
      * @return "true" if 'attack' either directly attacks 'target' or recursively
      */
     public boolean isAttackRelation(ExtendedAttack attack, DungEntity target) {
         if (attack.getAttacked().equals(target)) return true;

         if (attack.getAttacked() instanceof Argument) {
             Collection<DungEntity> attacked = getAttacked((Argument) attack.getAttacked());
             for (DungEntity entity: attacked) {
                 if (target.equals(new ExtendedAttack((Argument) attack.getAttacked(), entity))) return true;
             }
         }
         return false;
     }

     /**
      * Computes the set of arguments/attacks attacked by 'attack'.
      *
      * @param attack some extended attack
      * @return the set of elements attacked by 'attack'
      */
     public Collection<DungEntity> getAttacked(ExtendedAttack attack) {
         Collection<DungEntity> result = new HashSet<>();
         for (Argument arg: this) {
             if (isAttackRelation(attack, arg)) result.add(arg);
         }
         for (ExtendedAttack att: getAllAttacks()) {
             if (isAttackRelation(attack, att)) result.add(att);
         }
         return result;
     }

     /**
      * Determines whether the given set of arguments/attacks is conflict-free.
      *
      * @param ext a set of arguments/attacks
      * @return "true" if 'ext' is conflict-free
      */
     public boolean isConflictFree(Collection<DungEntity> ext) {
         for (DungEntity entity1: ext) {
             if (entity1 instanceof Argument) {
                 // No direct action needed for arguments in this context.
             } else if (entity1 instanceof ExtendedAttack) {
                 for (DungEntity entity2: ext) {
                     if (entity2 instanceof ExtendedAttack) {
                         if (isAttackRelation((ExtendedAttack) entity1, entity2)) return false;
                     }
                 }
             } else throw new IllegalArgumentException("Unsupported Entity type");
         }
         return true;
     }

     /**
      * Determines whether 'ext' defends 'entity'.
      *
      * @param entity some argument/attack
      * @param ext some set of arguments/attacks
      * @return "true" if 'entity' is defended by 'ext'
      */
     public boolean isAcceptable(DungEntity entity, Collection<DungEntity> ext) {
         for (ExtendedAttack beta: getAllAttacks()) {
             if (!isAttackRelation(beta, entity)) continue;
             boolean isDefended = false;
             for (DungEntity entity1: ext) {
                 if (entity1 instanceof ExtendedAttack) {
                     if (isAttackRelation((ExtendedAttack) entity1, beta)) {
                         isDefended = true;
                         break;
                     }
                 }
             }
             if (!isDefended) return false;
         }
         return true;
     }

     /**
      * Determines whether 'ext' is admissible.
      *
      * @param ext some set of arguments/attacks
      * @return "true" if 'ext' is admissible
      */
     public boolean isAdmissible(Collection<DungEntity> ext) {
         if (!isConflictFree(ext)) return false;
         for (DungEntity entity : ext) {
             if (!isAcceptable(entity, ext)) return false;
         }
         return true;
     }

     /**
      * Determines whether 'ext' is complete.
      *
      * @param ext some set of arguments/attacks
      * @return "true" if 'ext' is complete
      */
     public boolean isComplete(Collection<DungEntity> ext) {
         for (Argument arg : this) {
             if (isAcceptable(arg, ext) && !ext.contains(arg)) return false;
         }
         for (ExtendedAttack att : getAllAttacks()) {
             if (isAcceptable(att, ext) && !ext.contains(att)) return false;
         }
         return true;
     }

     /**
      * Computes the set of arguments that attack the given argument/attack.
      *
      * @param entity some argument/attack
      * @return the set of arguments attacking 'entity'
      */
     public Collection<Argument> getAttackers(DungEntity entity) {
         return this.parents.getOrDefault(entity, new HashSet<>());

     }

     /**
      * Computes the set of arguments that attack some member of the given set of arguments/attacks.
      *
      * @param entities some set of arguments/attacks
      * @return the set of arguments attacking some element of 'entities'
      */
     public Collection<Argument> getAttackers(Collection<DungEntity> entities) {
         Collection<Argument> attackers = new HashSet<>();
         for (DungEntity ent: entities) {
             attackers.addAll(this.parents.getOrDefault(ent, new HashSet<>()));
         }
         return attackers;
     }

     /**
      * Computes the set of arguments/attacks directly attacked by 'arg'.
      *
      * @param arg some argument
      * @return the set of arguments/attacks directly attacked by 'arg'
      */
     public Collection<DungEntity> getAttacked(Argument arg) {
         return this.children.getOrDefault(arg, new HashSet<>());
     }

     /**
      * Computes the set of arguments/attacks directly attacked by some argument in 'ext'.
      *
      * @param ext some set of arguments
      * @return the set of arguments/attacks directly attacked by 'ext'
      */
     public Collection<DungEntity> getAttacked(Collection<Argument> ext) {
         Collection<DungEntity> attacked = new HashSet<>();
         for (Argument arg: ext) {
             attacked.addAll(this.children.getOrDefault(arg, new HashSet<>()));
         }
         return attacked;
     }

     @Override
     public boolean contains(Object o) {
         if (o instanceof Argument) {
             return this.formulas.contains((Argument) o);
         } else if (o instanceof ExtendedAttack) {
             return this.getAllAttacks().contains((ExtendedAttack) o);
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
      * Adds an attack between arguments to the theory.
      *
      * @param attacker some argument
      * @param attacked some argument
      * @return "true" if the attack has been added successfully
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
      * Adds a recursive attack to the theory.
      *
      * @param attacker some argument
      * @param attacked some extended attack
      * @return "true" if the attack has been added successfully
      */
     public boolean addAttack(Argument attacker, ExtendedAttack attacked) {
         if (!getAllAttacks().contains(attacked) || !this.contains(attacker)) return false;
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
      * Determines whether 'a' is attacked by 'b'.
      *
      * @param a some argument
      * @param b some argument
      * @return "true" if 'a' is attacked by 'b'
      */
     public boolean isAttacked(Argument a, Argument b) {
         return children.getOrDefault(b, new HashSet<>()).contains(a);
     }

     /**
      * Determines whether 'a' is attacked by 'b'.
      *
      * @param a some extended attack
      * @param b some argument
      * @return "true" if 'a' is attacked by 'b'
      */
     public boolean isAttacked(ExtendedAttack a, Argument b) {
         return children.getOrDefault(b, new HashSet<>()).contains(a);
     }

     @Override
     public Collection<Argument> getNodes() {
         return this;
     }

     /**
      * Computes the set of all attacks in the theory.
      *
      * @return the set of all attacks in the theory
      */
     public Collection<ExtendedAttack> getAllAttacks() {
         Set<ExtendedAttack> attacks = new HashSet<>();
         for(Argument a: this) {
             if(this.children.containsKey(a)) {
                 for(DungEntity b: this.children.get(a)) {
                     attacks.add(new ExtendedAttack(a, b));
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
      * Pretty prints the recursive extended theory.
      *
      * @return string representation of the recursive extended theory
      */
     public String prettyPrint(){
         StringBuilder output = new StringBuilder();
         for (Argument argument : this) {
             output.append("argument(").append(argument.toString()).append(").\n");
         }
         output.append("\n");
         for (ExtendedAttack attack : this.getAllAttacks()) {
             output.append("attack").append(attack.toString()).append(".\n");
         }
         return output.toString();
     }
 }
