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

 package org.tweetyproject.arg.extended.reasoner;

 import org.tweetyproject.arg.dung.syntax.Argument;
 import org.tweetyproject.arg.dung.syntax.DungEntity;
 import org.tweetyproject.arg.extended.syntax.ExtendedAttack;
 import org.tweetyproject.arg.extended.syntax.RecursiveExtendedTheory;
 import org.tweetyproject.commons.util.IncreasingSubsetIterator;
 import org.tweetyproject.commons.util.SubsetIterator;

 import java.util.ArrayList;
 import java.util.Collection;
 import java.util.HashSet;
 import java.util.Set;

 /**
  * Simple reasoner for computing conflict-free sets of recursive extended theories.
  *
  * This reasoner identifies conflict-free sets within a {@link RecursiveExtendedTheory}.
  * A set is considered conflict-free if no argument or attack within the set conflicts
  * with another argument or attack in the set.
  *
  * The reasoner generates all subsets of the arguments and extended attacks in the theory,
  * and then filters out those that are conflict-free.
  *
  * @see RecursiveExtendedTheory
  * @see AbstractRecursiveExtendedExtensionReasoner
  * @see DungEntity
  * @see ExtendedAttack
  *
  * @author Lars Bengel
  */
 public class SimpleRecursiveExtendedConflictFreeReasoner extends AbstractRecursiveExtendedExtensionReasoner {

     /**
      * Default constructor for {@code SimpleRecursiveExtendedConflictFreeReasoner}.
      *
      * This constructor initializes the reasoner without any specific parameters.
      */
     public SimpleRecursiveExtendedConflictFreeReasoner() {
         // No specific initialization required
     }

     /**
      * Returns the collection of conflict-free sets of Dung entities for the given recursive extended theory.
      *
      * This method generates and checks all subsets of extended attacks within the theory,
      * and filters them to include only those that are conflict-free. Arguments are not considered for conflict-freeness.
      * Hence, every combination of arguments is conflict-free.
      *
      * @param bbase the recursive extended theory for which conflict-free sets are to be computed
      * @return a collection of conflict-free sets of Dung entities
      */
     @Override
     public Collection<Collection<DungEntity>> getModels(RecursiveExtendedTheory bbase) {
         // conflict-free subsets of extended attacks, independent of the arguments
         Collection<Set<ExtendedAttack>> cfAttacks = new ArrayList<>();
         SubsetIterator<ExtendedAttack> attIt = new IncreasingSubsetIterator<>(new HashSet<>(bbase.getAllAttacks()));
         while (attIt.hasNext()) {
             Set<ExtendedAttack> atts = attIt.next();
             if (bbase.isConflictFree(new HashSet<>(atts))) {
                 cfAttacks.add(atts);
             }
         }

         // every pair of argument subset and conflict-free attack subset is a distinct result
         Collection<Collection<DungEntity>> result = new HashSet<>();
         SubsetIterator<Argument> argIt = new IncreasingSubsetIterator<>(new HashSet<>(bbase));
         while (argIt.hasNext()) {
             Set<Argument> args = argIt.next();
             for (Set<ExtendedAttack> atts: cfAttacks) {
                 Collection<DungEntity> ext = new HashSet<>(args);
                 ext.addAll(atts);
                 result.add(ext);
             }
         }
         return result;
     }
 }
