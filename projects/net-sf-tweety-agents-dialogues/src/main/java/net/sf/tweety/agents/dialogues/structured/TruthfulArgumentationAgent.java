package net.sf.tweety.agents.dialogues.structured;

import java.util.*;

import net.sf.tweety.agents.*;
import net.sf.tweety.agents.dialogues.ExecutableExtension;
import net.sf.tweety.arg.dung.syntax.*;
import net.sf.tweety.arg.saf.*;

/**
 * This class models a truthful argumentation agent, i.e.
 * an agent that always brings forward all arguments he knows of.
 * 
 * @author Matthias Thimm
 *
 */
public class TruthfulArgumentationAgent extends SasAgent {

	/**
	 * Creates a new (non-single-step) agent with the given (local) view and utility function. 
	 * @param view the view of the agent on the argumentation.
	 * @param utility a utility function.
	 */
	public TruthfulArgumentationAgent(StructuredArgumentationFramework view, UtilityFunction utility) {
		this(view, utility,false);
	}
	
	/**
	 * Creates a new agent with the given (local) view and utility function. 
	 * @param view the view of the agent on the argumentation.
	 * @param utility a utility function.
	 * @param isSingleStep indicates whether this agent is a single-step argumentation agent,
	 * i.e. whether he may bring forward only one argument at a time or multiple.
	 */
	public TruthfulArgumentationAgent(StructuredArgumentationFramework view, UtilityFunction utility, boolean isSingleStep){
		super(view,utility,isSingleStep);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.agents.sas.SasAgent#next(java.util.Collection)
	 */
	@Override
	public Executable next(Collection<? extends Perceivable> percepts) {
		super.next(percepts);
		Set<Argument> possibleArguments = this.getPossibleArguments();
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

}
