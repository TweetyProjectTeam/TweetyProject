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
package net.sf.tweety.logics.commons.analysis.streams;

import java.util.Collection;

import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.streams.FormulaStream;
import net.sf.tweety.logics.commons.analysis.BeliefSetInconsistencyMeasure;

/**
 * General interface for inconsistency measures working on streams.
 * @author Matthias Thimm
 *
 * @param <S> The type of formulas
 * @param <T> The type of belief bases 
 */
public abstract class StreamBasedInconsistencyMeasure<S extends Formula> extends BeliefSetInconsistencyMeasure<S>{

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.BeliefSetInconsistencyMeasure#inconsistencyMeasure(java.util.Collection)
	 */
	@Override
	public abstract Double inconsistencyMeasure(Collection<S> formulas);
	
	/**
	 * Processes the formulas in the given stream one after the other.
	 * After each update an event of type "InconsistencyUpdateEvent"
	 * is created and listeners are notified. This method creates and
	 * starts a new object of type "InconsistencyMeasurementProcess"
	 * that processes the stream in a separate thread, and returns it.
	 * The current value of inconsistency can be retrieved by the "getInconsistencyValue()"
	 * of that process and measurement can be aborted by calling its "abort()" method. 
	 * @param stream some formula stream. 
	 * @return an inconsistency measurement process.
	 */
	public abstract InconsistencyMeasurementProcess<S> getInconsistencyMeasureProcess(FormulaStream<S> stream);
	
	/**
	 * Add a listener to this measure.
	 * @param listener the listener to be added.
	 */
	public abstract void addInconsistencyListener(InconsistencyListener listener);
	
	/**
	 * Remove a listener from this measure.
	 * @param listener the listener to be removed.
	 */
	public abstract void removeInconsistencyListener(InconsistencyListener listener);
	
	/**
	 * This method is called by inconsistency measurement processes to dispatch
	 * events to listeners.
	 * @param evt some event to be fired.
	 */
	abstract void fireInconsistencyUpdateEvent(InconsistencyUpdateEvent evt);
	
	/**
	 * This method is called by inconsistency measurement processes to dispatch
	 * an event about inconsistency measurement starts to listeners.
	 * @param evt some event to be fired.
	 */
	abstract void fireInconsistencyMeasurementStartedEvent(InconsistencyUpdateEvent evt);
}
