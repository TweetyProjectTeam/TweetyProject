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
  * Simple reasoner for computing complete extensions of recursive extended theories.
  *
  * This reasoner extends the abstract reasoner and provides methods for finding complete
  * extensions of a {@link RecursiveExtendedTheory}. A complete extension is an admissible
  * extension that includes all arguments it defends.
  *
  * The reasoner utilizes admissible extensions as a base and checks if they are complete.
  *
  * @see RecursiveExtendedTheory
  * @see SimpleRecursiveExtendedAdmissibleReasoner
  * @see AbstractRecursiveExtendedExtensionReasoner
  * @see DungEntity
  *
  * @author Lars Bengel
  */
 public class SimpleRecursiveExtendedCompleteReasoner extends AbstractRecursiveExtendedExtensionReasoner {

     /**
      * Default constructor for {@code SimpleRecursiveExtendedCompleteReasoner}.
      *
      * This constructor initializes the reasoner without any specific parameters.
      */
     public SimpleRecursiveExtendedCompleteReasoner() {
         // No specific initialization required
     }

     /**
      * Returns the collection of complete extensions of the given recursive extended theory.
      *
      * This method retrieves all admissible extensions from the {@link SimpleRecursiveExtendedAdmissibleReasoner},
      * and then filters them to include only those that are complete.
      *
      * @param bbase the recursive extended theory for which complete extensions are to be computed
      * @return a collection of complete extensions
      */
     @Override
     public Collection<Collection<DungEntity>> getModels(RecursiveExtendedTheory bbase) {
         Collection<Collection<DungEntity>> result = new HashSet<>();

         // Get all admissible sets and filter those that are complete
         for (Collection<DungEntity> ext : new SimpleRecursiveExtendedAdmissibleReasoner().getModels(bbase)) {
             if (bbase.isComplete(ext)) {
                 result.add(ext);
             }
         }

         return result;
     }
 }
