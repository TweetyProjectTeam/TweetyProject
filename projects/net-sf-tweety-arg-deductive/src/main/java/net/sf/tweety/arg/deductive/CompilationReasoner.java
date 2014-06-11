package net.sf.tweety.arg.deductive;

import net.sf.tweety.arg.deductive.accumulator.Accumulator;
import net.sf.tweety.arg.deductive.categorizer.Categorizer;
import net.sf.tweety.arg.deductive.semantics.ArgumentTree;
import net.sf.tweety.arg.deductive.semantics.Compilation;
import net.sf.tweety.arg.deductive.semantics.DeductiveArgument;
import net.sf.tweety.commons.BeliefBase;


/**
 * This class implements the approach of knowledge base compilation as proposed in<br/>
 * <br/>
 * Philippe Besnard and Anthony Hunter. Knowledgebase Compilation for Efficient Logical Argumentation.
 * In Proceedings of the 10th International Conference on Knowledge Representation (KR'06), pages
 * 123-133, AAAI Press, 2006.<br/>
 * <br/>
 * 
 * It performs deductive argumentation on a set of propositional formulas. 
 * 
 * @author Matthias Thimm
 */
public class CompilationReasoner extends AbstractDeductiveArgumentationReasoner {

	/** The compilation of the knowledge base. */
	private Compilation compilation;
	
	/** Creates a new compilation reasoner for the given belief base,
	 * categorizer, and accumulator.
	 * @param beliefBase some belief base (must be of class DeductiveKnowledgebase).
	 * @param categorizer some categorizer.
	 * @param accumulator some accumulator.
	 */
	public CompilationReasoner(BeliefBase beliefBase, Categorizer categorizer, Accumulator accumulator) {
		super(beliefBase, categorizer, accumulator);		
		this.compilation = new Compilation((DeductiveKnowledgeBase)beliefBase);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.argumentation.deductive.AbstractDeductiveArgumentationReasoner#getArgumentTree(net.sf.tweety.argumentation.deductive.semantics.DeductiveArgument)
	 */
	protected  ArgumentTree getArgumentTree(DeductiveArgument arg){
		return this.compilation.getArgumentTree(arg);
	}
	
}
