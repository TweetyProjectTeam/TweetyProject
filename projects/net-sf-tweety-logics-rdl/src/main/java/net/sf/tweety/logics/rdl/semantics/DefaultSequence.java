package net.sf.tweety.logics.rdl.semantics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.tweety.logics.fol.ClassicalInference;
import net.sf.tweety.logics.fol.FolBeliefSet;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.syntax.Negation;
import net.sf.tweety.logics.rdl.DefaultTheory;
import net.sf.tweety.logics.rdl.syntax.DefaultRule;

public class DefaultSequence {

	private List<DefaultRule> defaults = new ArrayList();
	private Set<FolFormula> out = new HashSet();
	private FolBeliefSet in = new FolBeliefSet();
	private ClassicalInference reasoner = new ClassicalInference(in);
	boolean process = true;

	public DefaultSequence(DefaultTheory dt) {
		in.addAll(dt.getFacts());
	}

	public DefaultSequence(DefaultSequence ds, DefaultRule d) {
		defaults.addAll(ds.defaults);
		defaults.add(d);
		in.addAll(ds.in);
		process = false;
		for(FolFormula f:in)
			if(eq(f, (FolFormula)d.getPre())) 
				process = true;
		for(DefaultRule r: defaults)
			if(d==r)
				process = false;
		in.add(d.getConc());
		out.addAll(ds.out);
		for(FolFormula f: d.getJus())
			out.add(new Negation(f));
	}
	
	public DefaultSequence app(DefaultRule d){
		return new DefaultSequence(this,d);
	}
	
	/**
	 * helper
	 * @param a
	 * @param b
	 * @return
	 */
 	private boolean eq(FolFormula a, FolFormula b){
		return ClassicalInference.equivalent(a, b);
	}

	public Collection<FolFormula> getIn() {
		return in;
	}

	public Collection<FolFormula> getOut() {
		return out;
	}

	/**
	 * process <=> all defaults are unique and applicable in sequence
	 * @return true iff is process
	 */
	public boolean isProcess() {
		return process;
	}

	/**
	 * successfull <=> no elem in In and Out
	 * @return true iff successfull
	 */
	public boolean isSuccessful() {
		for(FolFormula f: in)
			for(FolFormula g: out)
				if(eq(f,g))
					return false;
		return true;
	}

	/**
	 * @return true iff every possible default is applied
	 */
	public boolean isClosed() {
		// TODO implement me
		return false;
	}

	@Override
	public String toString() {
		return "DefaultSequence [\n\tdefaults = " + defaults + ", \n\tout = " + out + ", \n\tin = " + in + "\n\tprocess = "+ process +"\n]";
	}
	

}
