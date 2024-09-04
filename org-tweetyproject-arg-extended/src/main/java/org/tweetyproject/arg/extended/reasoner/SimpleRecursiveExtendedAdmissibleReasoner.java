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

 import org.tweetyproject.arg.dung.syntax.DungEntity;
 import org.tweetyproject.arg.extended.syntax.RecursiveExtendedTheory;

 import java.util.Collection;
 import java.util.HashSet;

 /**
  * Simple reasoner for computing admissible sets of recursive extended theories.
  *
  * This reasoner uses the concept of admissible sets in recursive extended theories.
  * A set is admissible if it is conflict-free and defends itself against attacks.
  *
  * The reasoner first generates conflict-free sets using the {@link SimpleRecursiveExtendedConflictFreeReasoner},
  * then checks which of these sets are admissible.
  *
  * @see RecursiveExtendedTheory
  * @see SimpleRecursiveExtendedConflictFreeReasoner
  * @see AbstractRecursiveExtendedExtensionReasoner
  * @see DungEntity
  *
  * @author Lars Bengel
  */
 public class SimpleRecursiveExtendedAdmissibleReasoner extends AbstractRecursiveExtendedExtensionReasoner {

     /**
      * Default constructor for {@code SimpleRecursiveExtendedAdmissibleReasoner}.
      *
      * This constructor initializes the reasoner without any specific parameters.
      */
     public SimpleRecursiveExtendedAdmissibleReasoner() {
         // No specific initialization required
     }

     /**
      * Returns the collection of admissible sets of Dung entities for the given recursive extended theory.
      *
      * This method retrieves all conflict-free sets from the {@link SimpleRecursiveExtendedConflictFreeReasoner},
      * and then filters them to include only those that are admissible.
      *
      * @param bbase the recursive extended theory for which admissible sets are to be computed
      * @return a collection of admissible sets of Dung entities
      */
     @Override
     public Collection<Collection<DungEntity>> getModels(RecursiveExtendedTheory bbase) {
         Collection<Collection<DungEntity>> result = new HashSet<>();

         // Get all conflict-free sets and filter those that are admissible
         for (Collection<DungEntity> ext : new SimpleRecursiveExtendedConflictFreeReasoner().getModels(bbase)) {
             if (bbase.isAdmissible(ext)) {
                 result.add(ext);
             }
         }

         return result;
     }
 }
