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
package net.sf.tweety.arg.deductive.reasoner;

import net.sf.tweety.arg.deductive.accumulator.Accumulator;
import net.sf.tweety.arg.deductive.categorizer.Categorizer;
import net.sf.tweety.arg.deductive.semantics.ArgumentTree;
import net.sf.tweety.arg.deductive.semantics.Compilation;
import net.sf.tweety.arg.deductive.semantics.DeductiveArgument;
import net.sf.tweety.arg.deductive.syntax.DeductiveKnowledgeBase;


/**
 * This class implements the approach of knowledge base compilation as proposed in<br>
 * <br>
 * Philippe Besnard and Anthony Hunter. Knowledgebase Compilation for Efficient Logical Argumentation.
 * In Proceedings of the 10th International Conference on Knowledge Representation (KR'06), pages
 * 123-133, AAAI Press, 2006.<br>
 * <br>
 * 
 * It performs deductive argumentation on a set of propositional formulas. 
 * 
 * @author Matthias Thimm
 */
public class CompilationReasoner extends AbstractDeductiveArgumentationReasoner {

	/** Creates a new compilation reasoner for the given belief base,
	 * categorizer, and accumulator.
	 * @param categorizer some categorizer.
	 * @param accumulator some accumulator.
	 */
	public CompilationReasoner(Categorizer categorizer, Accumulator accumulator) {
		super(categorizer, accumulator);		
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.deductive.AbstractDeductiveArgumentationReasoner#getArgumentTree(net.sf.tweety.arg.deductive.DeductiveKnowledgeBase, net.sf.tweety.arg.deductive.semantics.DeductiveArgument)
	 */
	protected  ArgumentTree getArgumentTree(DeductiveKnowledgeBase kb, DeductiveArgument arg){
		return new Compilation(kb).getArgumentTree(arg);
	}	
}
