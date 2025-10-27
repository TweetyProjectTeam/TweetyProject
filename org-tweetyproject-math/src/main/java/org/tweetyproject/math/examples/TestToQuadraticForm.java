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
 *  Copyright 2020 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.math.examples;

import java.util.ArrayList;

import org.tweetyproject.math.term.*;
import org.tweetyproject.math.term.FloatConstant;
import org.tweetyproject.math.term.IntegerVariable;
import org.tweetyproject.math.term.Sum;
import org.tweetyproject.math.term.Term;

/**
 * testing the function to model a quadratic function to quadratic form
 * @author Sebastian
 *
 */
public class TestToQuadraticForm {

	// Default constructor
	public TestToQuadraticForm() {
		// Default constructor
	}

    /**
     * main method
     * @param args arguments
     */
	public static void main(String[] args) {
		IntegerVariable m1 = new IntegerVariable("Maschine_A");
		ArrayList<Term> opts = new ArrayList<Term>();

		opts.add(new Product(new Sum(m1, new FloatConstant(4)), new FloatConstant(2)));
		opts.add(new Product(new Sum(m1, new FloatConstant(4)), new Sum(m1, new FloatConstant(4))));
		opts.add(new Product(new Product(m1, new FloatConstant(4)), new Product(m1, new FloatConstant(4))));
		opts.add(new Product(new Product(m1, new FloatConstant(4)), new Sum(m1, new FloatConstant(4))));
		opts.add(new Product(new Sum(m1, new FloatConstant(4)), new Product(m1, new FloatConstant(4))));
		opts.add(new Sum(new Sum(m1, new FloatConstant(4)), new Sum(m1, new FloatConstant(4))));
		opts.add(new Sum(new Product(m1, new FloatConstant(4)), new Product(m1, new FloatConstant(4))));
		opts.add(new Sum(new Product(m1, new FloatConstant(4)), new Sum(m1, new FloatConstant(4))));
		opts.add(new Sum(new Sum(m1, new FloatConstant(4)), new Product(m1, new FloatConstant(4))));
		//Term opt = new Sum(new Product(m2, new IntegerConstant(3)), new Sum(m1, new IntegerConstant(3)));
		for(Term t : opts) {
			System.out.println(t.toQuadraticForm().simplify().toString()+ "\n");

			//System.out.println(t.getSums().toString());
		}

//		for(Term t : opts) {
//			ArrayList<Sum> sums = new ArrayList<Sum>();
//			sums.addAll(t.toQuadraticForm().getSums());
//			for(Sum s : sums)
//				System.out.println(s.toString());
//			System.out.print("\n");
//
//		}
//
	}
}
