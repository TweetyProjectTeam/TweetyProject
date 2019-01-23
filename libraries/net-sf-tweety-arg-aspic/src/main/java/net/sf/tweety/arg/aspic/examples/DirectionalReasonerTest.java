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
 *  Copyright 2018 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.arg.aspic.examples;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import net.sf.tweety.arg.aspic.reasoner.DirectionalAspicReasoner;
import net.sf.tweety.arg.aspic.reasoner.ModuleBasedAspicReasoner;
import net.sf.tweety.arg.aspic.syntax.AspicArgumentationTheory;
import net.sf.tweety.arg.aspic.util.RandomAspicArgumentationTheoryGenerator;
import net.sf.tweety.arg.dung.reasoner.AbstractExtensionReasoner;
import net.sf.tweety.arg.dung.semantics.Semantics;
import net.sf.tweety.arg.dung.syntax.DungTheory;
import net.sf.tweety.commons.InferenceMode;
import net.sf.tweety.commons.util.Pair;
import net.sf.tweety.logics.pl.syntax.Proposition;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;

/**
 * Test runtime of module-based vs. directional reasoners.
 * 
 * Also checks if they give the same answers
 * 
 * @author Tjitze Rienstra
 *
 */
public class DirectionalReasonerTest {
	public static void main(String[] args) {		 
		int repetitions = 5000;
		int numberAtoms = 65;
		int numberFormulas = 120;
		int maxLiteralsInPremises = 3;
		double percentageStrictRules = 0.2;
		
		ModuleBasedAspicReasoner<PropositionalFormula> moduleReasoner = new ModuleBasedAspicReasoner<PropositionalFormula>(AbstractExtensionReasoner.getSimpleReasonerForSemantics(Semantics.GR));
		DirectionalAspicReasoner<PropositionalFormula> directionalReasoner = new DirectionalAspicReasoner<PropositionalFormula>(AbstractExtensionReasoner.getSimpleReasonerForSemantics(Semantics.GR));
		
		long totalTimeModuleBased = 0;
		long totalTimeDirectional = 0;
		long totalArgsModuleBased = 0;
		long totalArgsDirectional = 0;
		long totalTrue = 0;
		for(int i = 0; i < repetitions; i++) {
			AspicArgumentationTheory<PropositionalFormula> theory = RandomAspicArgumentationTheoryGenerator.next(numberAtoms, numberFormulas, maxLiteralsInPremises, percentageStrictRules);
			System.out.println(i + "\t" + theory);
			PropositionalFormula query = new Proposition("A1");
			
			// Skip instances taking longer than 10sec
			System.out.println("Module-based...");
			boolean answer1 = false;
			try {
				Pair<Long, Pair<Integer, Boolean>> res = runWithTimeout(new Callable<Pair<Long, Pair<Integer, Boolean>>>() {
					@Override
					public Pair<Long, Pair<Integer, Boolean>> call() throws Exception {
						long millis = System.currentTimeMillis();
						DungTheory af1 = moduleReasoner.getDungTheory(theory, query);
						boolean answer1 = moduleReasoner.query(af1, query, InferenceMode.CREDULOUS);
						return new Pair<Long, Pair<Integer, Boolean>>(millis, new Pair<Integer, Boolean>(af1.getNumberOfNodes(), answer1));
					}
				}, 10, TimeUnit.SECONDS);
				totalTimeModuleBased += System.currentTimeMillis()-res.getFirst();
				totalArgsModuleBased += res.getSecond().getFirst();
				answer1 = res.getSecond().getSecond();
				if (res.getSecond().getSecond()) totalTrue++;
			} catch (Exception e) {
				System.out.println("Timeout... skipping");
				continue;
			}
				
			System.out.println("Directional...");
			long millis = System.currentTimeMillis();
			DungTheory af2 = directionalReasoner.getDungTheory(theory, query);
			boolean answer2 = directionalReasoner.query(af2, query, InferenceMode.CREDULOUS);
			totalTimeDirectional += System.currentTimeMillis()-millis;
			totalArgsDirectional += af2.getNumberOfNodes();
			
			if (answer1 != answer2) {
				System.out.println("Module-based and directional reasoners gave different answers");
				System.out.println("Module-based answer: " + answer1 + " directional answer: " + answer2);
				System.out.println("Query: " + query);
				System.out.println("Theory: " + theory.toString());
				throw new RuntimeException();
			}
			millis = System.currentTimeMillis();
		}	
		System.out.println();
		System.out.println("Runtime module-based        : " + totalTimeModuleBased + "ms");
		System.out.println("Argument count module-based : " + totalArgsModuleBased);
		System.out.println("Runtime directional         : " + totalTimeDirectional+ "ms");
		System.out.println("Argument count directiona   : " + totalArgsDirectional);
		System.out.println("Queries returning accepted  : " + totalTrue + "/" + repetitions);
	}

//	private static void printAF(DungTheory af) {
//		for (Argument a: af) {
//			System.out.println("arg: " + a);
//		}
//		for (Attack a: af.getAttacks()) {
//			System.out.println("att: " + a);
//		}
//	}
	
  public static <T> T runWithTimeout(Callable<T> callable, long timeout, TimeUnit timeUnit) throws Exception {
    final ExecutorService executor = Executors.newSingleThreadExecutor();
    final Future<T> future = executor.submit(callable);
    executor.shutdown(); // This does not cancel the already-scheduled task.
    try {
      return future.get(timeout, timeUnit);
    }
    catch (TimeoutException e) {
      //remove this if you do not want to cancel the job in progress
      //or set the argument to 'false' if you do not want to interrupt the thread
      future.cancel(true);
      throw e;
    }
    catch (ExecutionException e) {
      //unwrap the root cause
      Throwable t = e.getCause();
      if (t instanceof Error) {
        throw (Error) t;
      } else if (t instanceof Exception) {
        throw (Exception) t;
      } else {
        throw new IllegalStateException(t);
      }
    }
  }

	
}
