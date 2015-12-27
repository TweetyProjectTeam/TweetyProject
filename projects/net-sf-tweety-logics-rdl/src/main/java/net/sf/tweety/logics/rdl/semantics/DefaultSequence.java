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

	public DefaultSequence(DefaultTheory dt) {
		in.addAll(dt.getFacts());
	}

	public DefaultSequence(DefaultSequence ds, DefaultRule d) {
		defaults.addAll(ds.defaults);
		defaults.add(d);
		in.addAll(ds.in);
		in.add(d.getConc());
		out.addAll(ds.out);
		for(FolFormula f: d.getJus())
			out.add(new Negation(f));
	}
	
	public DefaultSequence app(DefaultRule d){
		return new DefaultSequence(this,d);
	}

	public Collection<FolFormula> getIn() {
		// TODO implement me
		return in;
	}

	public Collection<FolFormula> getOut() {
		// TODO implement me
		return out;
	}

	public boolean isProcess() {
		for(FolFormula f : out)
			if(reasoner.query(f).getAnswerBoolean())
				return false;
		return true;
	}

	public boolean isSuccessful() {
		// TODO implement me
		return false;
	}

	public boolean isClosed() {
		// TODO implement me
		return false;
	}
}
