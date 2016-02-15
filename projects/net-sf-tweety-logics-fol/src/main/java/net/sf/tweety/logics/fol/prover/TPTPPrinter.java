package net.sf.tweety.logics.fol.prover;

import java.io.StringWriter;
import java.util.Collection;
import java.util.Iterator;

import net.sf.tweety.logics.commons.syntax.Constant;
import net.sf.tweety.logics.commons.syntax.Functor;
import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.commons.syntax.Sort;
import net.sf.tweety.logics.commons.syntax.Variable;
import net.sf.tweety.logics.fol.FolBeliefSet;
import net.sf.tweety.logics.fol.syntax.AssociativeFOLFormula;
import net.sf.tweety.logics.fol.syntax.Conjunction;
import net.sf.tweety.logics.fol.syntax.Disjunction;
import net.sf.tweety.logics.fol.syntax.ExistsQuantifiedFormula;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.syntax.FolSignature;
import net.sf.tweety.logics.fol.syntax.ForallQuantifiedFormula;
import net.sf.tweety.logics.fol.syntax.Negation;
import net.sf.tweety.logics.fol.syntax.QuantifiedFormula;
import net.sf.tweety.logics.fol.syntax.RelationalFormula;

public class TPTPPrinter {
	
	private String dir;
	private int axiom_id = 0;
	
	public String toTPTP(FolBeliefSet b) {
		StringWriter sw = new StringWriter();
		
		// print types
		FolSignature sig = (FolSignature)b.getSignature();
		for(Sort sort: sig.getSorts())
			sw.write(toTPTP(sort));
		for(Constant c:sig.getConstants())
			sw.write(toTPTP(c));
		for(Predicate p:sig.getPredicates())
			sw.write(toTPTP(p));
		for(Functor f:sig.getFunctors())
			sw.write(toTPTP(f));
		
		//print facts
		for(FolFormula f: b)
			sw.write(toTPTP(f));
		
		return sw.toString();
	}
	
	public String toTPTP(Sort sort) {
		return "tff("+sort+"_type,type,("+sort+":$tType)).\n";
	}
	
	public String toTPTP(Constant c) {
		return "tff("+c+"_type,type,("+c+":"+c.getSort()+")).\n";
	}
	
	public String toTPTP(Predicate p) {
		return "tff("+p.getName()+"_type,type,("+p.getName()+":"+join(p.getArgumentTypes()," * ","(",")")+" > $o)).\n";
	}
	
	public String toTPTP(Functor f) {
		// TODO ???
		return "";
	}
	
	public String toTPTP(FolFormula f) {
		return "tff(axiom_"+ ++axiom_id+",axiom,("+printFormula(f)+")).\n";
	}
	
	
	
	private String printFormula(RelationalFormula f) {
		if(f instanceof Negation){
			Negation n= (Negation)f;
			return "~ "+printFormula(n.getFormula());
		}
		if(f instanceof QuantifiedFormula){
			QuantifiedFormula fqf = (QuantifiedFormula)f;
			String result = "! [";
			if(f instanceof ExistsQuantifiedFormula)
				result = "? [";
			Iterator<Variable> i = fqf.getQuantifierVariables().iterator();
			result += printVar(i.next());
			while(i.hasNext())
				result+=", "+printVar(i.next());
			return result +"]: "+printFormula(fqf.getFormula());
		}
		if(f instanceof AssociativeFOLFormula){
			AssociativeFOLFormula d= (AssociativeFOLFormula)f;
			Iterator<RelationalFormula> i = d.getFormulas().iterator();
			String result = printFormula(i.next());
			String delimiter = (f instanceof Conjunction) ? " & ":" | ";
			while(i.hasNext())
				result += delimiter + printFormula(i.next());
			return result;
		}
		return f.toString();
	}
	
	
	private String printVar(Variable var){
		return var+": "+var.getSort();
	}
	

	
	private <T> String join(Collection<T> c, String delimiter, String left, String right){
		if(c.size()==1)
			return c.iterator().next().toString();
		String result=left;
		boolean first = true;
		for(T o:c){
			if(first)
				first=false;
			else
				result+=delimiter;
			result+=o;
		}
		return result+right;
	}

}
