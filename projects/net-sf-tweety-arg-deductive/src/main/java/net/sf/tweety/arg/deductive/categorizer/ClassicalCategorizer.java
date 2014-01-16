package net.sf.tweety.arg.deductive.categorizer;

import java.util.Collection;

import net.sf.tweety.arg.deductive.semantics.ArgumentTree;
import net.sf.tweety.arg.deductive.semantics.DeductiveArgumentNode;

/**
 * This class implements a simple categorizer that does
 * an bottom-up marking of the tree; leafs are marked
 * undefeated and an inner node is marked defeated
 * if at least one child is marked undefeated and
 * defeated otherwise.<br/>
 * <br/>
 * The method "categorize" returns "1" if the root node is
 * undefeated and "0" if it is defeated.
 * 
 * @author Matthias Thimm
 */
public class ClassicalCategorizer implements Categorizer {

	/* (non-Javadoc)
	 * @see net.sf.tweety.argumentation.deductive.categorizer.Categorizer#categorize(net.sf.tweety.argumentation.deductive.semantics.ArgumentTree)
	 */
	@Override
	public double categorize(ArgumentTree argumentTree) {
		return this.categorize(argumentTree, null, argumentTree.getRoot());
	}
	
	/**
	 * Performs the bottom-up search.
	 * @param argumentTree some argument tree.
	 * @param parent the parent of the current node.
	 * @param node the current node.
	 * @return "1" if node is undefeated, "0" otherwise.
	 */
	private double categorize(ArgumentTree argumentTree, DeductiveArgumentNode parent, DeductiveArgumentNode node){
		Collection<DeductiveArgumentNode> children = argumentTree.getNeighbors(node);
		if(parent != null) children.remove(parent);
		if(children.isEmpty())
			return 1;
		for(DeductiveArgumentNode child: children)
			if(this.categorize(argumentTree, node, child) != 0)
				return 0;
		return 1;
	}

}
