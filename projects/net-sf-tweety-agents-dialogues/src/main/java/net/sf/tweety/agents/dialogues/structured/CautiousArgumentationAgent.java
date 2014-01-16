package net.sf.tweety.agents.dialogues.structured;

import java.util.*;

import net.sf.tweety.agents.*;
import net.sf.tweety.agents.dialogues.ExecutableExtension;
import net.sf.tweety.arg.dung.*;
import net.sf.tweety.arg.dung.syntax.*;
import net.sf.tweety.arg.saf.*;
import net.sf.tweety.arg.saf.syntax.*;
import net.sf.tweety.logics.pl.syntax.*;


/**
 * This class models a cautious argumentation agent, i.e. 
 * an agent that only brings forward arguments that cannot be harmful
 * to this agent's goal of proving some given proposition but are inherently
 * necessary in order to prove the agent's focal element.
 * 
 * @author Matthias Thimm
 */
public class CautiousArgumentationAgent extends OvercautiousArgumentationAgent {

	/**
	 * Creates a new (non-single-step) agent with the given (local) view and utility function. 
	 * @param view the view of the agent on the argumentation.
	 * @param utility a utility function.
	 * @param focalElement the focal element of this agent.
	 */
	public CautiousArgumentationAgent(StructuredArgumentationFramework view, UtilityFunction utility, Proposition focalElement) {
		this(view, utility,false,focalElement);
	}
	
	/**
	 * Creates a new agent with the given (local) view and utility function. 
	 * @param view the view of the agent on the argumentation.
	 * @param utility a utility function.
	 * @param isSingleStep indicates whether this agent is a single-step argumentation agent,
	 * i.e. whether he may bring forward only one argument at a time or multiple.
	 * @param focalElement the focal element of this agent.
	 */
	public CautiousArgumentationAgent(StructuredArgumentationFramework view, UtilityFunction utility, boolean isSingleStep, Proposition focalElement) {
		super(view, utility, isSingleStep, focalElement);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.agents.sas.SasAgent#next(java.util.Collection)
	 */
	@Override
	public Executable next(Collection<? extends Perceivable> percepts) {
		super.next(percepts);
		Set<Argument> possibleArguments = this.getPossibleArguments();
		possibleArguments.removeAll(this.attackSet());
		possibleArguments.addAll(this.necessaryArguments());
		if(possibleArguments.isEmpty())
			return Executable.NO_OPERATION;
		if(this.isSingleStep()){
			//get the first argument and return it
			Set<Argument> result = new HashSet<Argument>();
			result.add(possibleArguments.iterator().next());
			return new ExecutableExtension(result);
		}else{
			return new ExecutableExtension(possibleArguments);
		}
	}
	
	/**
	 * Computes the set of necessary arguments for this agent's focal
	 * element, i.e. the intersection of all argument structures
	 * claiming this agent's focal element.
	 * @return the set of necessary arguments for this agent's focal
	 * element.
	 */
	protected Set<Argument> necessaryArguments(){
		DungTheory commonView = this.getCommonView().toDungTheory();
		Set<Argument> necessaryArguments = new HashSet<Argument>(commonView);
		for(Argument a : commonView){
			ArgumentStructure arg1 = (ArgumentStructure) a;
			necessaryArguments.retainAll(arg1);
		}
		return necessaryArguments;
	}
}
