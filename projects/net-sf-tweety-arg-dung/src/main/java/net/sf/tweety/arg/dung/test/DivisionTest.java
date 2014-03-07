package net.sf.tweety.arg.dung.test;

import java.util.Collection;

import net.sf.tweety.arg.dung.AbstractExtensionReasoner;
import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.dung.GroundReasoner;
import net.sf.tweety.arg.dung.divisions.Division;
import net.sf.tweety.arg.dung.semantics.Extension;
import net.sf.tweety.arg.dung.semantics.Semantics;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.Attack;

public class DivisionTest {

	public static void main(String[] args){
		// create some Dung theory
		DungTheory theory = new DungTheory();
		Argument a = new Argument("a");
		Argument b = new Argument("b");
		Argument c = new Argument("c");
		theory.add(a);
		theory.add(b);
		theory.add(c);		
		theory.add(new Attack(a,b));
		theory.add(new Attack(b,a));
		theory.add(new Attack(c,b));
		
		// Instantiate reasoner
		AbstractExtensionReasoner r = new GroundReasoner(theory);
		Collection<Extension> exts = r.getExtensions();
		
		// print theory
		System.out.println("AAF: " + theory);
		
		// print extensions
		System.out.println();
		System.out.println("Extensions: ");
		for(Extension e: exts)
			System.out.println(e);
		
		// print divisions
		System.out.println();
		System.out.println("Divisions: ");
		for(Division div: Division.getDivisions(exts, theory)){
			System.out.println(div);
			System.out.println("\tDividers:");
			for(DungTheory d: div.getDividers(theory, Semantics.GROUNDED_SEMANTICS))
				System.out.println("\t" +d);			
		}
		
		
	}
}
