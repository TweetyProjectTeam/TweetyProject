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
 *  Copyright 2019 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.arg.adf.reasoner.processor;

import net.sf.tweety.arg.adf.reasoner.Pipeline;
import net.sf.tweety.arg.adf.semantics.Interpretation;
import net.sf.tweety.arg.adf.syntax.AbstractDialecticalFramework;

/**
 * Performs further processing on interpretations, e.g. maximization.
 * <p>
 * Note that sometimes it is necessary for processors to work on isolated
 * states. Just consider a sat based maximization processor, which has to
 * generate clauses to compute larger interpretations until we reach a maximum.
 * Usually we do not want to pollute the shared state with these generated
 * clauses, because it may cut off other solutions. In such cases we can tell
 * the {@link Pipeline} to create a new state on which we perform the processing
 * and which is only used by this processor.
 * <p>
 * As a consequence the implementation of the
 * {@link #process(Object, Interpretation, AbstractDialecticalFramework)
 * process} method must not rely on the fact that its state updates are seen be
 * subsequent processors. Final updates on the shared state must therefore be
 * performed in the
 * {@link #updateState(Object, Interpretation, AbstractDialecticalFramework)
 * updateState} method, which is guaranteed to get the shared state.
 * 
 * @author Mathias Hofer
 *
 */
public interface InterpretationProcessor<S> {

	/**
	 * Performs the processing of the given interpretation on a potential
	 * isolated state, meaning that the updates on this state are not seen by
	 * the subsequent processors. If the given state is isolated or shared by
	 * the other processors depends on the configuration of the {@link Pipeline}
	 * and hence by the user.
	 * <p>
	 * Necessary updates on the shared state have to be performed in the
	 * {@link #updateState(Object, Interpretation, AbstractDialecticalFramework)
	 * updateState} method, which is called by the pipeline after the processing
	 * is done.
	 * 
	 * @param state
	 *            a potentially isolated state or the shared one
	 * @param interpretation
	 *            the result of the previous processor
	 * @param adf
	 *            the corresponding adf
	 * @return
	 */
	public Interpretation process(S state, Interpretation interpretation, AbstractDialecticalFramework adf);

	/**
	 * This method is called by the {@link Pipeline} on the shared state and the
	 * result of the
	 * {@link #process(S, Interpretation, AbstractDialecticalFramework) process}
	 * method, hence after the processing is done.
	 * <p>
	 * Note that if this processor has to update the shared state, it has to
	 * happen in this method. Since
	 * {@link #process(Object, Interpretation, AbstractDialecticalFramework)
	 * process} may only have access to an isolated state to perform its
	 * processing.
	 * 
	 * @param state
	 *            the shared state
	 * @param processed
	 *            the result of the process method
	 * @param adf
	 *            the corresponding adf
	 */
	public void updateState(S state, Interpretation processed, AbstractDialecticalFramework adf);

}
