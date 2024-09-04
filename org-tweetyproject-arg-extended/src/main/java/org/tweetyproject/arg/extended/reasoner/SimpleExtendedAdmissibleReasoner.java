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

 /**
  * Simple reasoner for computing admissible sets of extended theories.
  *
  * This reasoner extends the abstract extended reasoner and provides
  * a method for finding admissible extensions of an {@link ExtendedTheory}.
  *
  * The admissibility of an extension is determined by checking
  * whether it is conflict-free and defends all its arguments.
  *
  * @author Lars Bengel
  */
 public class SimpleExtendedAdmissibleReasoner extends AbstractExtendedExtensionReasoner {

     /**
      * Default constructor for {@code SimpleExtendedAdmissibleReasoner}.
      *
      * This constructor initializes the reasoner without any specific parameters.
      */
     public SimpleExtendedAdmissibleReasoner() {
         // No specific initialization required
     }

     /**
      * Returns the collection of admissible extensions of the given extended theory.
      *
      * This method first retrieves all conflict-free extensions using the {@link SimpleExtendedConflictFreeReasoner},
      * and then filters them based on whether they are admissible according to the theory.
      *
      * @param bbase the extended theory for which admissible extensions are to be computed
      * @return a collection of admissible extensions
      */
     @Override
     public Collection<Extension<ExtendedTheory>> getModels(ExtendedTheory bbase) {
         Collection<Extension<ExtendedTheory>> result = new HashSet<>();
         for (Extension<ExtendedTheory> ext : new SimpleExtendedConflictFreeReasoner().getModels(bbase)) {
             if (bbase.isAdmissible(ext)) {
                 result.add(ext);
             }
         }
         return result;
     }

     /**
      * Returns one admissible extension of the given extended theory.
      *
      * This method provides a single admissible extension of the theory, but
      * in this simple implementation, it returns an empty extension.
      *
      * @param bbase the extended theory for which an admissible extension is to be returned
      * @return an admissible extension (currently an empty extension)
      */
     @Override
     public Extension<ExtendedTheory> getModel(ExtendedTheory bbase) {
         return new Extension<>();
     }
 }
