package net.sf.tweety.arg.dung.test;

import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.dung.SccCompleteReasoner;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.Attack;
import net.sf.tweety.logics.pl.sat.Sat4jSolver;
import net.sf.tweety.logics.pl.sat.SatSolver;

public class SccCompleteReasonerTest {

	public static void main(String[] args){
		SatSolver.setDefaultSolver(new Sat4jSolver());
		DungTheory theory = new DungTheory();
		Argument a = new Argument("a");
		Argument b = new Argument("b");
		Argument c = new Argument("c");
		Argument d = new Argument("d");
		Argument e = new Argument("e");
		Argument f = new Argument("f");
		Argument g = new Argument("g");
		Argument h = new Argument("h");
		Argument i = new Argument("i");
		theory.add(a);
		theory.add(b);
		theory.add(c);
		theory.add(d);
		theory.add(e);
		theory.add(f);
		theory.add(g);
		theory.add(h);
		theory.add(i);
		
		theory.add(new Attack(a,b));
		theory.add(new Attack(b,a));
		theory.add(new Attack(b,c));
		theory.add(new Attack(c,d));
		theory.add(new Attack(d,c));
		theory.add(new Attack(d,e));
		theory.add(new Attack(e,f));
		theory.add(new Attack(f,g));
		theory.add(new Attack(g,e));
		theory.add(new Attack(c,h));
		theory.add(new Attack(i,h));
		theory.add(new Attack(h,i));
		
		
		SccCompleteReasoner reasoner = new SccCompleteReasoner(theory);
		
		System.out.println(reasoner.getExtensions());
		
	}
}
