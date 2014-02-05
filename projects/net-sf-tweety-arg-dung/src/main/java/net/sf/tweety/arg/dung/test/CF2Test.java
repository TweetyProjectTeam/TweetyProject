package net.sf.tweety.arg.dung.test;

import net.sf.tweety.arg.dung.CF2Reasoner;
import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.dung.semantics.Extension;
import net.sf.tweety.arg.dung.semantics.Semantics;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.Attack;

public class CF2Test {
	public static void main(String[] args){
		// create some Dung theory
		DungTheory theory = new DungTheory();
		Argument a = new Argument("a");
		Argument b = new Argument("b");
		Argument c = new Argument("c");
		Argument d = new Argument("d");
		Argument e = new Argument("e");
		Argument f = new Argument("f");
		theory.add(a);
		theory.add(b);
		theory.add(c);
		theory.add(d);
		theory.add(e);
		theory.add(f);
		theory.add(new Attack(a,b));
		theory.add(new Attack(b,c));
		theory.add(new Attack(c,d));
		theory.add(new Attack(d,e));
		theory.add(new Attack(e,a));
		theory.add(new Attack(e,f));
		
		CF2Reasoner reasoner = new CF2Reasoner(theory,Semantics.CREDULOUS_INFERENCE);
		
		for(Extension ext: reasoner.getExtensions()){
			System.out.println(ext);			
		}
	}
}
