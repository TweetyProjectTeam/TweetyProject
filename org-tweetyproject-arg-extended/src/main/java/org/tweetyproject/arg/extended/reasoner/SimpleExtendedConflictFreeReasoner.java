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

 import org.tweetyproject.arg.dung.semantics.Extension;
 import org.tweetyproject.arg.dung.syntax.Argument;
 import org.tweetyproject.arg.extended.syntax.ExtendedTheory;
 import org.tweetyproject.commons.util.SetTools;

 import java.util.Collection;
 import java.util.HashSet;
 import java.util.Set;

 /**
  * Simple reasoner for computing conflict-free sets of extended theories.
  *
  * This reasoner iterates through all subsets of the argument set in the extended theory
  * and identifies those that are conflict-free.
  *
  * A conflict-free set is one where no argument within the set attacks another argument in the set.
  *
  * @author Lars Bengel
  */
 public class SimpleExtendedConflictFreeReasoner extends AbstractExtendedExtensionReasoner {

     /**
      * Default constructor for {@code SimpleExtendedConflictFreeReasoner}.
      *
      * This constructor initializes the reasoner without any specific parameters.
      */
     public SimpleExtendedConflictFreeReasoner() {
         // No specific initialization required
     }

     /**
      * Returns the collection of conflict-free extensions of the given extended theory.
      *
      * This method iterates over all subsets of the arguments in the extended theory
      * and selects those that are conflict-free.
      *
      * @param bbase the extended theory for which conflict-free extensions are to be computed
      * @return a collection of conflict-free extensions
      */
     @Override
     public Collection<Extension<ExtendedTheory>> getModels(ExtendedTheory bbase) {
         Collection<Extension<ExtendedTheory>> result = new HashSet<>();
         for (Set<Argument> args: new SetTools<Argument>().subsets(bbase)) {
             if (bbase.isConflictFree(args)) {
                 result.add(new Extension<>(args));
             }
         }
         return result;
     }

     /**
      * Returns one conflict-free extension of the given extended theory.
      *
      * In this simple implementation, this method returns an empty extension.
      *
      * @param bbase the extended theory for which a conflict-free extension is to be returned
      * @return a conflict-free extension (currently an empty extension)
      */
     @Override
     public Extension<ExtendedTheory> getModel(ExtendedTheory bbase) {
         return new Extension<>();
     }
 }
