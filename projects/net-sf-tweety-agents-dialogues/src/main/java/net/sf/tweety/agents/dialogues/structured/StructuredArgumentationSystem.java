package net.sf.tweety.agents.dialogues.structured;

import java.util.*;

import net.sf.tweety.agents.*;
import net.sf.tweety.arg.dung.semantics.*;
import net.sf.tweety.arg.dung.syntax.*;

/**
 * This class represents a structured argumentation system, i.e. a set of agents
 * that argue within some given protocol based on structured argumentation frameworks.
 * 
 * @author Matthias Thimm
 */
public class StructuredArgumentationSystem extends MultiAgentSystem<SasAgent> {

	/**
	 * This class models an environment for structured argumentation systems.
	 * @author Matthias Thimm
	 */
	private static class SasEnvironment implements Environment {

		/**
		 * The common view of all agents.
		 */
		private PerceivableStructuredArgumentationFramework commonView;
		
		/**
		 * The underlying structured argumentation framework.
		 */
		private PerceivableStructuredArgumentationFramework saf;
		
		/**
		 * Creates a new SasEnvironment based on the given SAF.
		 * @param saf a perceivable structured argumentation framework.
		 */
		public SasEnvironment(PerceivableStructuredArgumentationFramework saf){
			this.saf = saf;
			this.commonView = new PerceivableStructuredArgumentationFramework();
		}
		
		/* (non-Javadoc)
		 * @see net.sf.tweety.agents.Environment#execute(net.sf.tweety.agents.Executable)
		 */
		@Override
		public Set<Perceivable> execute(Executable action) {
			if(!(action instanceof Extension))
				throw new IllegalArgumentException("SasEnvironment expects action of type 'Extension'.");
			Extension e = (Extension) action;
			if(!(this.saf.containsAll(e)))
				throw new IllegalArgumentException("Action contains unknown arguments.");
			this.commonView.addAll(e);
			for(Argument a: this.commonView){
				for(Argument b: this.commonView)
					if(saf.isAttackedBy(a, b))
						this.commonView.add(new Attack(b,a));
			}				
			return this.getPercepts(null);
		}

	
		/* (non-Javadoc)
		 * @see net.sf.tweety.agents.Environment#execute(java.util.Collection)
		 */
		@Override
		public Set<Perceivable> execute(Collection<? extends Executable> actions) {
			for(Executable action: actions)
				this.execute(action);
			return this.getPercepts(null);
		}

		/* (non-Javadoc)
		 * @see net.sf.tweety.agents.Environment#getPercepts(net.sf.tweety.agents.Agent)
		 */
		@Override
		public Set<Perceivable> getPercepts(Agent agent) {
			Set<Perceivable> percepts = new HashSet<Perceivable>();
			percepts.add(this.commonView);
			return percepts;
		}

		/* (non-Javadoc)
		 * @see net.sf.tweety.agents.Environment#reset()
		 */
		@Override
		public boolean reset() {
			this.commonView = new PerceivableStructuredArgumentationFramework();
			return true;
		}
		
	}
	
	/**
	 * Creates a new StructuredArgumentationSystem
	 * @param saf the underlying perceivable structured argumentation framework
	 */
	public StructuredArgumentationSystem(PerceivableStructuredArgumentationFramework saf) {
		this(saf,new HashSet<SasAgent>());
	}
	
	/**
	 * Creates a new StructuredArgumentationSystem with the given collection of agents.
	 * @param saf the underlying perceivable structured argumentation framework
	 * @param agents a collection of agents.
	 */
	public StructuredArgumentationSystem(PerceivableStructuredArgumentationFramework saf, Collection<? extends SasAgent> agents){
		super(new SasEnvironment(saf),agents);
	}
}
