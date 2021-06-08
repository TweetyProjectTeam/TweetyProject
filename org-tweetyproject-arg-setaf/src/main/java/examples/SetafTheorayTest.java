package examples;

import java.util.HashSet;
import java.util.Set;

import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.setaf.syntax.SetafAttack;
import org.tweetyproject.arg.setaf.syntax.SetafTheory;
import org.tweetyproject.arg.setaf.reasoners.SimpleGroundedReasoner;

public class SetafTheorayTest {
	
	public static void main(String[] args) {
		SetafTheory s = new SetafTheory();
		Argument a = new Argument("a");
		Argument b = new Argument("b");
		Argument c = new Argument("c");
		Argument d = new Argument("d");
		s.add(a);
		s.add(b);
		s.add(c);
		s.add(d);
		
		Set<Argument> a1 = new HashSet<Argument>();
		a1.add(a);
		a1.add(b);
		a1.add(d);
		
		Set<Argument> a2 = new HashSet<Argument>();
		a2.add(c);
		a2.add(a);
		
		
		s.add(new SetafAttack(a1, a));
		s.add(new SetafAttack(a2, c));
		s.remove(a);
		System.out.println(s.toString());
		System.out.println(s.getComplementGraph(0));
		SimpleGroundedReasoner gr = new SimpleGroundedReasoner();
		System.out.println(gr.getModel(s));
	}

}
