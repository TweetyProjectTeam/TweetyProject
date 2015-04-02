/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.tweety.agents.dialogues;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import net.sf.tweety.agents.Agent;
import net.sf.tweety.agents.Environment;
import net.sf.tweety.agents.Executable;
import net.sf.tweety.agents.Perceivable;
import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.dung.semantics.Extension;

/**
 * This class models the environment for agents in argumentation games with lotteries.
 * It consists of the universal Dung theory used
 * for argumentation (but not completely revealed to all agents) and
 * the current trace of disclosed Dung theories.
 * 
 * @author Matthias Thimm
 */
public class LotteryArgumentationEnvironment implements Environment, Perceivable {

	/** The current dialogue trace. */
	private DialogueTrace<DungTheory,Collection<DungTheory>> trace;
	/** The universal Dung theory used for argumentation. */
	private DungTheory universalTheory;
	
	/**
	 * Creates a new grounded environment.
	 * @param universalTheory the universal Dung theory used for argumentation.
	 */
	public LotteryArgumentationEnvironment(DungTheory universalTheory){
		this.trace = new DialogueTrace<DungTheory,Collection<DungTheory>>();
		this.universalTheory = universalTheory;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.agents.Environment#execute(net.sf.tweety.agents.Executable)
	 */
	@Override
	public Set<Perceivable> execute(Executable action) {
		if(!(action instanceof ExecutableDungTheory))
			throw new IllegalArgumentException("Object of type ExecutableDungTheory expected");
		Collection<DungTheory> th = new HashSet<DungTheory>();
		th.add((ExecutableDungTheory)action);
		this.trace.add(th);
		return this.getPercepts(null);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.agents.Environment#execute(java.util.Collection)
	 */
	@Override
	public Set<Perceivable> execute(Collection<? extends Executable> actions) {
		for(Executable exec: actions){
			if(!(exec instanceof ExecutableDungTheory))
				throw new IllegalArgumentException("Object of type ExecutableDungTheory expected");
			Collection<DungTheory> th = new HashSet<DungTheory>();
			th.add((ExecutableDungTheory)exec);
			this.trace.add(th);
		}
		return this.getPercepts(null);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.agents.Environment#getPercepts(net.sf.tweety.agents.Agent)
	 */
	@Override
	public Set<Perceivable> getPercepts(Agent agent) {
		//this environment is added as percept so that
		//the agent can inquire the necessary information
		//himself.
		Set<Perceivable> percepts = new HashSet<Perceivable>();
		percepts.add(this);
		return percepts;
	}
	
	/**
	 * Returns the current dialogue trace.
	 * @return the current dialogue trace.
	 */
	public DialogueTrace<DungTheory,Collection<DungTheory>> getDialogueTrace(){
		return this.trace;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.agents.Environment#reset()
	 */
	public boolean reset(){
		this.trace = new DialogueTrace<DungTheory,Collection<DungTheory>>();
		return true;
	}
	
	/**
	 * Returns the view of the universal Dung theory restricted to
	 * the given set of arguments.
	 * @param arguments a set of arguments.
	 * @return the projection of the universal theory.
	 */
	public DungTheory getPerceivedDungTheory(Extension arguments){
		return new DungTheory(this.universalTheory.getRestriction(arguments));
	}
}
