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
 import org.tweetyproject.arg.extended.syntax.ExtendedTheory;

 import java.util.Collection;
 import java.util.HashSet;

 /**
  * Simple reasoner for computing complete extensions of extended theories.
  *
  * This reasoner extends the abstract reasoner and provides methods for finding
  * complete extensions of an {@link ExtendedTheory}. A complete extension is
  * an admissible extension that contains all arguments it defends.
  *
  * The reasoner utilizes admissible extensions as a base and checks if they are complete.
  *
  * @author Lars Bengel
  */
 public class SimpleExtendedCompleteReasoner extends AbstractExtendedExtensionReasoner {

     /**
      * Default constructor for {@code SimpleExtendedCompleteReasoner}.
      *
      * This constructor initializes the reasoner without any specific parameters.
      */
     public SimpleExtendedCompleteReasoner() {
         // No specific initialization required
     }

     /**
      * Returns the collection of complete extensions of the given extended theory.
      *
      * This method retrieves all admissible extensions using the {@link SimpleExtendedAdmissibleReasoner},
      * and then filters them to include only those that are complete.
      *
      * @param bbase the extended theory for which complete extensions are to be computed
      * @return a collection of complete extensions
      */
     @Override
     public Collection<Extension<ExtendedTheory>> getModels(ExtendedTheory bbase) {
         Collection<Extension<ExtendedTheory>> result = new HashSet<>();
         for (Extension<ExtendedTheory> ext : new SimpleExtendedAdmissibleReasoner().getModels(bbase)) {
             if (bbase.isComplete(ext)) {
                 result.add(ext);
             }
         }
         return result;
     }

     /**
      * Returns one complete extension of the given extended theory.
      *
      * This method retrieves all complete extensions of the theory and returns the first one.
      *
      * @param bbase the extended theory for which a complete extension is to be returned
      * @return a complete extension (first one found)
      */
     @Override
     public Extension<ExtendedTheory> getModel(ExtendedTheory bbase) {
         return getModels(bbase).iterator().next();
     }
 }
