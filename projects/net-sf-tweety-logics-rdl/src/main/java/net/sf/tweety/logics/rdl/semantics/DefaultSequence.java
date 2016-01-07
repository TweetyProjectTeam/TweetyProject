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
	boolean process = false;

	public DefaultSequence(DefaultTheory dt) {
		in.addAll(dt.getFacts());
	}

	public DefaultSequence(DefaultSequence ds, DefaultRule d) {
		defaults.addAll(ds.defaults);
		defaults.add(d);
		in.addAll(ds.in);
		for(FolFormula f:in)
			if(eq(f, d.getPre()))
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
	
	private boolean eq(FolFormula a, FolFormula b){
		return false;
	}

	public Collection<FolFormula> getIn() {
		return in;
	}

	public Collection<FolFormula> getOut() {
		return out;
	}

	public boolean isProcess() {
		return process;
	}

	public boolean isSuccessful() {
		for(FolFormula f: in)
			for(FolFormula g: out)
				if(eq(f,g))
					return false;
		return true;
	}

	public boolean isClosed() {
		// TODO implement me
		return false;
	}
}
