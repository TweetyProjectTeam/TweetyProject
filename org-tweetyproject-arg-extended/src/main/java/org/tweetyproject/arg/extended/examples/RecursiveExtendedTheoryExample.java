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

 package org.tweetyproject.arg.extended.examples;

 import org.tweetyproject.arg.dung.syntax.Argument;
 import org.tweetyproject.arg.extended.reasoner.SimpleRecursiveExtendedCompleteReasoner;
 import org.tweetyproject.arg.extended.syntax.ExtendedAttack;
 import org.tweetyproject.arg.extended.syntax.RecursiveExtendedTheory;

 /**
  * This class provides an example demonstrating the usage of Recursive Extended Argumentation Frameworks
  * with standard and extended attacks.
  *
  * The example constructs a recursive extended theory and adds various arguments and attacks,
  * both standard and recursive. It also computes the complete extensions of the theory.
  *
  * @author Lars Bengel
  */
 public class RecursiveExtendedTheoryExample {

     /**
      * Default constructor for the {@code RecursiveExtendedTheoryExample} class.
      *
      * This constructor does not perform any specific initialization but is
      * explicitly provided to adhere to good coding practices.
      */
     public RecursiveExtendedTheoryExample() {
         // No specific initialization required
     }

     /**
      * The main method demonstrating how to construct a Recursive Extended Theory,
      * add arguments, standard attacks, and extended attacks, and compute the complete extensions.
      *
      * @param args command line arguments (not used in this example)
      */
     public static void main(String[] args) {
         // Create a new Recursive Extended Theory
         RecursiveExtendedTheory theory = new RecursiveExtendedTheory();

         // Create arguments
         Argument a = new Argument("a");
         Argument b = new Argument("b");
         Argument c = new Argument("c");
         Argument d = new Argument("d");
         Argument e = new Argument("e");
         Argument f = new Argument("f");

         // Add arguments to the theory
         theory.add(a, b, c, d, e, f);

         // Add standard attacks between arguments
         theory.addAttack(a, b);
         theory.addAttack(b, a);
         theory.addAttack(d, c);
         theory.addAttack(c, d);

         // Create extended attacks
         ExtendedAttack ab = new ExtendedAttack(a, b);
         ExtendedAttack ba = new ExtendedAttack(b, a);
         ExtendedAttack cd = new ExtendedAttack(c, d);
         ExtendedAttack ecd = new ExtendedAttack(e, cd);

         // Add extended attacks between arguments and existing attacks
         theory.addAttack(c, ba);
         theory.addAttack(d, ab);
         theory.addAttack(e, cd);
         theory.addAttack(f, ecd);

         // Print the recursive extended theory
         System.out.println(theory.prettyPrint());

         // Compute and print the complete extensions of the theory
         System.out.println("Complete Extensions: " + new SimpleRecursiveExtendedCompleteReasoner().getModels(theory));
     }
 }
