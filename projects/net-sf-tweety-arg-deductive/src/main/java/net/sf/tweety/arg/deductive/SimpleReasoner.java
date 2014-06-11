package net.sf.tweety.arg.deductive;

import java.util.HashSet;
import java.util.Set;

import net.sf.tweety.arg.deductive.accumulator.Accumulator;
import net.sf.tweety.arg.deductive.categorizer.Categorizer;
import net.sf.tweety.arg.deductive.semantics.ArgumentTree;
import net.sf.tweety.arg.deductive.semantics.DeductiveArgument;
import net.sf.tweety.arg.deductive.semantics.DeductiveArgumentNode;
import net.sf.tweety.commons.BeliefBase;
import net.sf.tweety.graphs.DirectedEdge;
import net.sf.tweety.graphs.Edge;
import net.sf.tweety.logics.pl.syntax.Conjunction;
import net.sf.tweety.logics.pl.syntax.Negation;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;

/**
 * This class implements a brute force approach to deductive argumentation.
 * 
 * It performs deductive argumentation on a set of propositional formulas. 
 * 
 * @author Matthias Thimm
 */
public class SimpleReasoner extends AbstractDeductiveArgumentationReasoner {

	/** Creates a new reasoner for the given belief base,
	 * categorizer, and accumulator.
	 * @param beliefBase some belief base (must be of class DeductiveKnowledgebase).
	 * @param categorizer some categorizer.
	 * @param accumulator some accumulator.
	 */
	public SimpleReasoner(BeliefBase beliefBase, Categorizer categorizer, Accumulator accumulator) {
		super(beliefBase, categorizer, accumulator);		
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.argumentation.deductive.AbstractDeductiveArgumentationReasoner#getArgumentTree(net.sf.tweety.argumentation.deductive.semantics.DeductiveArgument)
	 */
	@Override
	protected ArgumentTree getArgumentTree(DeductiveArgument arg) {
		return this.getArgumentTree(new DeductiveArgumentNode(arg), new HashSet<PropositionalFormula>());
	}
	
	/**
	 * Computes the argument tree of the given argument.
	 * @param argNode some argument node.
	 * @param support the union of the supports of all previously encountered arguments
	 * @return the argument tree for the argument
	 */
	private ArgumentTree getArgumentTree(DeductiveArgumentNode argNode, Set<PropositionalFormula> support) {
		support.addAll(argNode.getSupport());
		DeductiveKnowledgeBase kb = (DeductiveKnowledgeBase) this.getKnowledgBase();		 
		// 1.) collect all possible undercuts 
		PropositionalFormula claim = new Negation(new Conjunction(argNode.getSupport()));
		Set<DeductiveArgument> possibleUndercuts = kb.getDeductiveArguments(claim);
		// 2.) remove all undercuts that violate non-inclusion in previous supports
		Set<DeductiveArgument> undercuts = new HashSet<DeductiveArgument>();
		for(DeductiveArgument undercut: possibleUndercuts){
			Set<PropositionalFormula> sup = new HashSet<PropositionalFormula>(undercut.getSupport());
			sup.removeAll(support);
			if(!sup.isEmpty())
				undercuts.add(undercut);
		}
		// 3.) create argument tree recursively
		ArgumentTree argTree = new ArgumentTree(argNode);
		argTree.add(argNode);
		for(DeductiveArgument undercut: undercuts){
			DeductiveArgumentNode undercutNode = new DeductiveArgumentNode(undercut);
			ArgumentTree subTree = this.getArgumentTree(undercutNode, new HashSet<PropositionalFormula>(support));
			for(DeductiveArgumentNode node: subTree)
				argTree.add(node);
			for(Edge<DeductiveArgumentNode> edge: subTree.getEdges())
				argTree.add(edge);
			argTree.add(new DirectedEdge<DeductiveArgumentNode>(undercutNode, argNode));
		}
		return argTree;
	}
}
