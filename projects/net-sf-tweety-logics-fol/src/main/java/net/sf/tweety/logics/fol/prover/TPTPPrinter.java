package net.sf.tweety.logics.fol.prover;

import java.io.StringWriter;
import java.util.Collection;
import java.util.Iterator;

import net.sf.tweety.logics.commons.syntax.Constant;
import net.sf.tweety.logics.fol.FolBeliefSet;
import net.sf.tweety.logics.fol.syntax.AssociativeFOLFormula;
import net.sf.tweety.logics.fol.syntax.Conjunction;
import net.sf.tweety.logics.fol.syntax.ExistsQuantifiedFormula;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.syntax.FolSignature;
import net.sf.tweety.logics.fol.syntax.Negation;
import net.sf.tweety.logics.fol.syntax.QuantifiedFormula;
import net.sf.tweety.logics.fol.syntax.RelationalFormula;

/**
 * Prints single fol formulas and full knowledge bases to TPTP
 * @author Nils Geilen
 *
 */

public class TPTPPrinter {
	
	private String dir;
	
	public String toTPTP(FolBeliefSet b) {
		StringWriter sw = new StringWriter();
		
		// print types
		FolSignature sig = (FolSignature)b.getSignature();
		for(Constant c:sig.getConstants())
			sw.write(makeAxiom(c+"_type", c.getSort()+"("+c+")"));
		
		//print facts
		int axiom_id = 0;
		for(FolFormula f: b)
			sw.write(makeAxiom("axiom_"+ ++axiom_id, printFormula(f)));
		
		return sw.toString();
	}
	
	public String makeQuery(String name, FolFormula query) {
		return "fof("+name+", conjecture, "+printFormula(query)+").\n";
	}
	
	private String makeAxiom(String name, String body){
		return "fof("+name+", axiom, "+body+").\n";
	}
	
	
	
	private String printFormula(RelationalFormula f) {
		if(f instanceof Negation){
			Negation n= (Negation)f;
			return parens("~ "+printFormula(n.getFormula()));
		}
		if(f instanceof QuantifiedFormula){
			QuantifiedFormula fqf = (QuantifiedFormula)f;
			String result = "! [";
			if(f instanceof ExistsQuantifiedFormula)
				result = "? [";
			return parens(result +join(fqf.getQuantifierVariables(),", ") +"]: "+printFormula(fqf.getFormula()));
		}
		if(f instanceof AssociativeFOLFormula){
			AssociativeFOLFormula d= (AssociativeFOLFormula)f;
			Iterator<RelationalFormula> i = d.getFormulas().iterator();
			String result = printFormula(i.next());
			String delimiter = (f instanceof Conjunction) ? " & ":" | ";
			while(i.hasNext())
				result += delimiter + printFormula(i.next());
			return parens(result);
		}
		return f.toString();
	}
	
	private String parens(String str) {
		return "("+str+")";
	}

	
	private <T> String join(Collection<T> c, String delimiter){
		String result="";
		boolean first = true;
		for(T o:c){
			if(first)
				first=false;
			else
				result+=delimiter;
			result+=o;
		}
		return result;
	}

}
