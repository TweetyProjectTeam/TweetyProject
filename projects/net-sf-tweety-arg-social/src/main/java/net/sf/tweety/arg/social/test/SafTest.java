package net.sf.tweety.arg.social.test;

import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.Attack;
import net.sf.tweety.arg.social.IssReasoner;
import net.sf.tweety.arg.social.SocialAbstractArgumentationFramework;
import net.sf.tweety.arg.social.semantics.SimpleProductSemantics;

public class SafTest {
	public static void main(String[] args){
		SocialAbstractArgumentationFramework saf = new SocialAbstractArgumentationFramework();
		Argument a = new Argument("A");
		Argument b = new Argument("B");
		Argument c = new Argument("C");
		Argument d = new Argument("D");
		saf.add(a);
		saf.add(b);
		saf.add(c);
		saf.add(d);
		saf.add(new Attack(a,b));
		saf.add(new Attack(b,c));
		saf.add(new Attack(c,b));
		saf.add(new Attack(c,d));
		
		saf.voteUp(a,3);
		saf.voteUp(b,2);
		saf.voteUp(c,2);
		saf.voteUp(d,2);
		saf.voteDown(a);
		saf.voteDown(c,5);
		saf.voteDown(d,1);
		
		System.out.println(saf);
		
		IssReasoner reasoner = new IssReasoner(saf,new SimpleProductSemantics(0.01),0.001);
		
		System.out.println(reasoner.getSocialModel());
	}	
}
