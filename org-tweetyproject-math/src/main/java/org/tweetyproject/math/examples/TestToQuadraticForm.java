package org.tweetyproject.math.examples;

import java.util.ArrayList;

import org.tweetyproject.math.term.*;
import org.tweetyproject.math.term.FloatConstant;
import org.tweetyproject.math.term.IntegerVariable;
import org.tweetyproject.math.term.Sum;
import org.tweetyproject.math.term.Term;

public class TestToQuadraticForm {

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
