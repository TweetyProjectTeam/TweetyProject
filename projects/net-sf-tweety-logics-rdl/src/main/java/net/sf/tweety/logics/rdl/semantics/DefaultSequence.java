/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
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

/**
 * sequence of defaults
 * @author Nils Geilen
 *
 */

public class DefaultSequence {

	private List<DefaultRule> defaults = new ArrayList<>();
	private Set<FolFormula> out = new HashSet<>();
	private FolBeliefSet in = new FolBeliefSet();
	private ClassicalInference reasoner = new ClassicalInference(in);
	boolean process = true;

	public DefaultSequence(DefaultTheory dt) {
		in.addAll(dt.getFacts());
	}

	public DefaultSequence(DefaultSequence ds, DefaultRule d) {
		defaults.addAll(ds.defaults);
		in.addAll(ds.in);
		process = ds.isApplicable(d);
		for(DefaultRule r: defaults)
			if(d.equals(r))
				process = false;
		in.add(d.getConc());
		defaults.add(d);
		out.addAll(ds.out);
		for(FolFormula f: d.getJus())
			out.add(new Negation(f));
	}
	
	/**
	 * Constructs a new DefaultSequence
	 * @param d
	 * @return new Sequence adding d to this
	 */
	public DefaultSequence app(DefaultRule d){
		return new DefaultSequence(this,d);
	}
	
	/**
	 * applicable ^= pre in In and (not jus_i) not in In forall i 
	 * @param d
	 * @return true iff d is applicable to In
	 */
	public boolean isApplicable(DefaultRule d){
		for(FolFormula f: d.getJus())
			if(reasoner.query(new Negation(f)).getAnswerBoolean())
				return false;
		return reasoner.query(d.getPre()).getAnswerBoolean();
		
	}
	
	/**
	 * 	@return the sequence's in set
	 */
	public Collection<FolFormula> getIn() {
		return in;
	}

	
	/**
	 * @return the sequence's out set
	 */
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
	 * successfull <=> there is no x: x in In and x in Out
	 * @return true iff successfull
	 */
	public boolean isSuccessful() {
			for(FolFormula g: out)
				if(reasoner.query(g).getAnswerBoolean())
					return false;
		return true;
	}

	/**
	 * Tests wether all applicble defaukts from t have been applied
	 * @return true iff every possible default is applied
	 */
	public boolean isClosed(DefaultTheory t) {
		for(DefaultRule d: t.getDefaults())
			if(this.app(d).isProcess())
				return false;
		return true;
	}

	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "DefaultSequence"
				+ (isProcess() ? " is process":"")
				+ (isSuccessful()?" is successfull":"")
				+" [\n\tdefaults = " + defaults + ", \n\tout = " + out + ", \n\tin = " + in + "\n]";
	}
	

}
