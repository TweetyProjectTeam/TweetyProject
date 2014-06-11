package net.sf.tweety.arg.prob.test;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.sf.tweety.arg.dung.CompleteReasoner;
import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.dung.GroundReasoner;
import net.sf.tweety.arg.dung.PreferredReasoner;
import net.sf.tweety.arg.dung.StableReasoner;
import net.sf.tweety.arg.dung.semantics.Extension;
import net.sf.tweety.arg.dung.semantics.Labeling;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.Attack;
import net.sf.tweety.arg.prob.semantics.ProbabilisticExtension;
import net.sf.tweety.commons.TweetyConfiguration;
import net.sf.tweety.commons.TweetyLogging;
import net.sf.tweety.commons.util.SetTools;
import net.sf.tweety.math.GeneralMathException;
import net.sf.tweety.math.equation.Equation;
import net.sf.tweety.math.equation.Inequation;
import net.sf.tweety.math.opt.OptimizationProblem;
import net.sf.tweety.math.opt.solver.OpenOptSolver;
import net.sf.tweety.math.term.FloatConstant;
import net.sf.tweety.math.term.FloatVariable;
import net.sf.tweety.math.term.Logarithm;
import net.sf.tweety.math.term.Term;
import net.sf.tweety.math.term.Variable;
import net.sf.tweety.math.probability.*;

/**
 * This class contains some experiments with probabilistic extensions.
 * @author Matthias Thimm
 */
public class ProbDungTest {

	
	public static ProbabilisticExtension centroid(List<ProbabilisticExtension> pes, DungTheory theory){
		String code = "from FuncDesigner import *\n";
		code += "from SpaceFuncs import *\n\n";
		
		List<Extension> dimensions = new ArrayList<Extension>();
		for(Set<Argument> set: new SetTools<Argument>().subsets(theory))
			dimensions.add(new Extension(set));		
		
		int i = 0;
		for(ProbabilisticExtension pe: pes){
			code += "a" + i++ + " = [";
			boolean first = true;
			for(Extension e : dimensions){
				if(first) first = false;
				else code += ",";
				if(pe.containsKey(e))
					code += pe.get(e).doubleValue();
				else code += "0";
			}
			code +="]\n";
		}
		
		code += "P = Polytope(";
		boolean first = true;
		for(int j = 0; j < pes.size();j++){
			if(first) first = false;
			else code += ",";
			code += "a"+j;					
		}
		code += ")\n";
		code += "Centroid = P.centroid\n";
		code += "print Centroid";
		

		
		
		
		
		
		
		
		
		String output = "";
		//String error = "";
		InputStream in = null;
		Process child = null;
		try{
			File ooFile = File.createTempFile("ootmp", null);
			// Delete temp file when program exits.
			ooFile.deleteOnExit();    
			// Write to temp file
			BufferedWriter out = new BufferedWriter(new FileWriter(ooFile));
			out.write(code);			
			out.close();
			child = Runtime.getRuntime().exec("python " + ooFile.getAbsolutePath());
			int c;		
			in = child.getInputStream();
	        while ((c = in.read()) != -1){
	            output += ((char)c);
	        }
			in.close();		        		        
	        in = child.getErrorStream();
	       /*while ((c = in.read()) != -1)
	            error += (char)c;*/	        	        
		}catch(IOException e){
			return null;
		}finally{
			try {
				if(in != null) in.close();
			} catch (IOException e) {
				// ignore
			}
			if(child != null) child.destroy();
		}
		// parser output
		ProbabilisticExtension result = new ProbabilisticExtension();
		try{
			
			int valuesBegin = output.lastIndexOf("[");
			int valuesEnd = output.lastIndexOf("]");
			String values = output.substring(valuesBegin+1, valuesEnd);
			String[] tokens = values.split(" ");
			double[] resultDoubles = new double[dimensions.size()];
			int k = 0;
			for(String token : tokens){
				if(token.trim().equals(""))
					continue;
				resultDoubles[k] = new Double(token.trim());
				k++;
				if(k==dimensions.size()) break;
			}
			k = 0;
			for(Extension e: dimensions){
				result.put(e, new Probability(resultDoubles[k++]));
			}			
			return result;
		}catch(Exception e){
			System.out.println("YYY");
		}
		return null;
	}
	
	public static void main(String[] args){
		// create some Dung theory
		DungTheory theory = new DungTheory();
		Argument a = new Argument("a");
		Argument b = new Argument("b");
		Argument c = new Argument("c");
		Argument d = new Argument("d");
		Argument e = new Argument("e");
		theory.add(a);
		theory.add(b);
		theory.add(c);
		theory.add(d);
		theory.add(e);
		theory.add(new Attack(a,b));
		theory.add(new Attack(b,a));
		theory.add(new Attack(b,c));
		theory.add(new Attack(c,d));
		theory.add(new Attack(d,e));
		theory.add(new Attack(e,d));
		theory.add(new Attack(e,c));
		
//		Argument d1 = new Argument("d1");
//		Argument d2 = new Argument("d2");
//		Argument t1 = new Argument("t1");
//		Argument t2 = new Argument("t2");
//		Argument c = new Argument("c");
//		Argument p1 = new Argument("p1");
//		Argument p2 = new Argument("p2");
//		theory.add(d1);
//		theory.add(d2);
//		theory.add(t1);
//		theory.add(t2);
//		theory.add(c);
//		theory.add(p1);
//		theory.add(p2);
//		theory.add(new Attack(d1,d2));
//		theory.add(new Attack(d2,d1));
//		theory.add(new Attack(t1,t2));
//		theory.add(new Attack(t2,t1));
//		theory.add(new Attack(t1,d1));
//		theory.add(new Attack(t2,d2));
//		theory.add(new Attack(c,d2));
//		theory.add(new Attack(p1,p2));
//		theory.add(new Attack(p2,p1));
//		theory.add(new Attack(p1,c));
			
		
		// standard semantics
		CompleteReasoner completeReasoner = new CompleteReasoner(theory);
		System.out.println("Complete extensions:  " + completeReasoner.getExtensions());
		List<ProbabilisticExtension> allComplete = new LinkedList<ProbabilisticExtension>();
		for(Extension ex: completeReasoner.getExtensions()){
			allComplete.add(ProbabilisticExtension.getCharacteristicProbabilisticExtension(theory, new Labeling(theory,ex)));			
		}
		
		// compute average
		ProbabilisticExtension avg = new ProbabilisticExtension();		
		for(Set<Argument> set: new SetTools<Argument>().subsets(theory)){
			Extension ext = new Extension(set);
			double prob = 0;
			for(ProbabilisticExtension pe: allComplete){
				if(pe.containsKey(ext))
					prob += pe.get(ext).doubleValue();
			}
			avg.put(ext, new Probability(prob/allComplete.size()));
		}
		for(Argument arg: theory)
			System.out.println(arg + " " + avg.probability(arg));
		
		System.out.println();
		
//		// compute centroid
//		ProbabilisticExtension cen = ProbDungTest.centroid(allComplete,theory);
//		for(Argument arg: theory)
//			System.out.println(arg + " " + cen.probability(arg));
		
		
		
		GroundReasoner groundReasoner = new GroundReasoner(theory);
		System.out.println("Ground extensions: " + groundReasoner.getExtensions());
		PreferredReasoner preferredReasoner = new PreferredReasoner(theory);
		System.out.println("Preferred extensions: " + preferredReasoner.getExtensions());
		StableReasoner stableReasoner = new StableReasoner(theory);
		System.out.println("Stable extensions: " + stableReasoner.getExtensions());
		
		System.exit(0);
		
		// compute me-model wrt. probabilistic extensions
		// ==============================================
		// construct optimization problem
		OptimizationProblem problem = new OptimizationProblem(OptimizationProblem.MINIMIZE);
		//ConstraintSatisfactionProblem problem = new ConstraintSatisfactionProblem();
		Set<Set<Argument>> configurations = new SetTools<Argument>().subsets(theory);
		Map<Set<Argument>,Variable> vars = new HashMap<Set<Argument>,Variable>();
		Term normConstraint = null;
		for(Set<Argument> w: configurations){
			Variable var = new FloatVariable("w" + w.toString(),0,1);
			vars.put(w, var);
			if(normConstraint == null)
				normConstraint = var;
			else normConstraint = normConstraint.add(var);
		}
		problem.add(new Equation(normConstraint,new FloatConstant(1)));
		// add constraints imposed by p-justifiability
		for(Argument arg: theory){			
			if(theory.getAttackers(arg).isEmpty()){
				Term leftSide = null;
				Term rightSide = null;
				for(Set<Argument> set: configurations)
					if(set.contains(arg))
						if(leftSide == null)
							leftSide = vars.get(set);
						else leftSide = leftSide.add(vars.get(set));
				rightSide = new FloatConstant(1);
				problem.add(new Equation(leftSide,rightSide));
			}else{
				for(Argument attacker: theory.getAttackers(arg)){
					Term leftSide = null;
					Term rightSide = new FloatConstant(1);
					for(Set<Argument> set: configurations){
						if(set.contains(arg))
							if(leftSide == null)
								leftSide = vars.get(set);
							else leftSide = leftSide.add(vars.get(set));
						if(set.contains(attacker))
							rightSide = rightSide.minus(vars.get(set));
					}						
					problem.add(new Inequation(leftSide,rightSide,Inequation.LESS_EQUAL));
				}
			}			
		}
		// add constraints imposed by p-admissibility (beta version)
		for(Argument arg: theory){			
			if(!theory.getAttackers(arg).isEmpty()){
				Term leftSide = null;
				Term rightSide = new FloatConstant(1);
				for(Set<Argument> set: configurations)
					if(set.contains(arg))
						if(leftSide == null)
							leftSide = vars.get(set);
						else leftSide = leftSide.add(vars.get(set));
				for(Argument attacker: theory.getAttackers(arg))
					for(Set<Argument> set: configurations)
						if(set.contains(attacker))
							rightSide = rightSide.minus(vars.get(set));				
				problem.add(new Inequation(leftSide,rightSide,Inequation.GREATER_EQUAL));
			}			
		}
				
		Term targetFunction = null;
		// target function is the entropy
		for(Set<Argument> w: configurations){
			if(targetFunction == null)
				targetFunction = vars.get(w).mult(new Logarithm(vars.get(w)));
			else targetFunction = targetFunction.add(vars.get(w).mult(new Logarithm(vars.get(w))));			
		}
		
		
		
		problem.setTargetFunction(targetFunction);
	
		System.out.println();
	
		TweetyLogging.logLevel = TweetyConfiguration.LogLevel.FATAL;
		TweetyLogging.initLogging();
	
		try{			
			OpenOptSolver solver = new OpenOptSolver();
			solver.solver = "ralg";
			solver.contol = 0.001;
			Map<Variable,Term> solution = solver.solve(problem);
			// construct probability distribution
			ProbabilisticExtension p = new ProbabilisticExtension();
			for(Set<Argument> w: configurations)
				p.put(new Extension(w), new Probability(solution.get(vars.get(w)).doubleValue()));
		
			System.out.println("ME model:");
			for(Argument arg: theory)
				System.out.println(arg + ": " + p.probability(arg));
			
			System.out.println();
			System.out.println(p);
			
		}catch (GeneralMathException ex){
			// This should not happen as the optimization problem is guaranteed to be feasible (the knowledge base is consistent)
			throw new RuntimeException("Fatal error: Optimization problem to compute the ME-distribution is not feasible although the knowledge base seems to be consistent.");
		}	
		
	}
	
}
