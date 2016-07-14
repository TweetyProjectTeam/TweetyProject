package net.sf.tweety.arg.dung.test;

import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.dung.GrossiModgilRankingReasoner;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.Attack;

public class GrossiModgilReasonerTest {

	public static void main(String[] args){
		// See [Grossi, Modgil. On the Graded Acceptability of Arguments. IJCAI 2015]
		// This is Figure 3
		DungTheory theory = new DungTheory();
		Argument a = new Argument("a");
		Argument b = new Argument("b");
		Argument c = new Argument("c");
		Argument d = new Argument("d");
		Argument e = new Argument("e");
		Argument f = new Argument("f");
		Argument g = new Argument("g");
		theory.add(a);
		theory.add(b);
		theory.add(c);
		theory.add(d);
		theory.add(e);
		theory.add(f);
		theory.add(g);
		theory.add(new Attack(d,c));
		theory.add(new Attack(c,a));
		theory.add(new Attack(a,b));
		theory.add(new Attack(b,a));
		theory.add(new Attack(e,b));
		theory.add(new Attack(f,e));
		theory.add(new Attack(g,e));
		
		GrossiModgilRankingReasoner reasoner = new GrossiModgilRankingReasoner(theory);
		
		for(int m = 1; m < theory.size(); m++)
			for(int n = 1; n < theory.size(); n++)
				System.out.println(m + "," + n + " : " + reasoner.getAllMNCompleteExtensions(m, n));
		
		
		
		
	}
}
