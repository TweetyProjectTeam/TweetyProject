package net.sf.tweety.arg.aspic.syntax;

import java.io.StringWriter;
import java.util.Collection;
import java.util.Iterator;

public class InferenceRule {
	
	boolean defeasible;
	Word conclusion;
	Collection<Word> prerequisites;
	
	public InferenceRule(boolean defeasible, Word conclusion, Collection<Word> prerequisites) {
		super();
		this.defeasible = defeasible;
		this.conclusion = conclusion;
		this.prerequisites = prerequisites;
	}
	public boolean isDefeasible() {
		return defeasible;
	}
	public Word getConclusion() {
		return conclusion;
	}
	public Collection<Word> getPrerequisites() {
		return prerequisites;
	}
	@Override
	public String toString() {
		StringWriter sw =  new StringWriter();
		Iterator<Word> i = prerequisites.iterator();
		if(i.hasNext())
			sw.write(i.next().toString());
		while(i.hasNext())
			sw.write(", "+i.next());
		if(defeasible)
			sw.write(" => ");
		else
			sw.write(" -> ");
		sw.write(conclusion.toString());
		return sw.toString();
		
	}
	
	
}
