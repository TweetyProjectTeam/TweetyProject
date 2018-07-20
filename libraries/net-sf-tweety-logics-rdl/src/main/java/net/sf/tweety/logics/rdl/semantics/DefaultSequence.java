/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
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
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.logics.rdl.semantics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.sf.tweety.logics.fol.reasoner.FolReasoner;
import net.sf.tweety.logics.fol.syntax.FolBeliefSet;
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

	/**
	 * the sequence of defaults
	 */
	private List<DefaultRule> defaults = new ArrayList<>();
	
	/**
	 * the out set
	 */
	private Set<FolFormula> out = new HashSet<>();
	
	/**
	 * the in set
	 */
	private FolBeliefSet in = new FolBeliefSet();
	
	/**
	 * true if the sequence is a process
	 */
	boolean process = true;

	/**
	 * constructs an empty sequence of defaults of the default theory dt
	 * @param dt a default theory, from which defaults will be added to the sequence
	 */
	public DefaultSequence(DefaultTheory dt) {
		in.addAll(dt.getFacts());
	}

	/**
	 * constructs a sequence by appending d to ds 
	 * @param ds
	 * @param d
	 */
	public DefaultSequence(DefaultSequence ds, DefaultRule d) {
		defaults.addAll(ds.defaults);
		in.addAll(ds.in);
		process = ds.isApplicable(d);
		for(DefaultRule r: defaults)
			if(d.equals(r))
				process = false;
		in.add(d.getConclusion());
		defaults.add(d);
		out.addAll(ds.out);
		for(FolFormula f: d.getJustification())
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
		FolReasoner prover = FolReasoner.getDefaultReasoner();
		for(FolFormula f: d.getJustification())
			if(prover.query(in, new Negation(f)))
				return false;
		return prover.query(in, d.getPrerequisite());
		
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
		FolReasoner prover = FolReasoner.getDefaultReasoner();
			for(FolFormula g: out)
				if(prover.query(in,g))
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
