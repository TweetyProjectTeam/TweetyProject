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
package org.tweetyproject.logics.commons.analysis.streams;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.tweetyproject.commons.Formula;
import org.tweetyproject.commons.streams.FormulaStream;

/**
 * Implements a stream-based inconsistency measure on a given class of
 * inconsistency measurement processes.
 * @author Matthias Thimm
 *
 * @param <S> The type of formulas
 */
public class DefaultStreamBasedInconsistencyMeasure<S extends Formula> extends StreamBasedInconsistencyMeasure<S> {

	/** The class of inconsistency measurement processes. */
	private Class<? extends InconsistencyMeasurementProcess<S>> clazz;
	/** The listeners of this measure. */
	private List<InconsistencyListener> listeners = new LinkedList<InconsistencyListener>();
	/** Configuration options for to be given to the inconsistency measurement process. */
	private Map<String,Object> config;
	
	/**
	 * Creates a new inconsistency measure based on the given process class.
	 * @param clazz some inconsistency measurement process class. 
	 */
	public DefaultStreamBasedInconsistencyMeasure(Class<? extends InconsistencyMeasurementProcess<S>> clazz){
		this(clazz, new HashMap<String,Object>());
	}
	
	/**
	 * Creates a new inconsistency measure based on the given process class and the given configuration options for that process.
	 * @param clazz some inconsistency measurement process class.
	 * @param config configuration options for that class.
	 */
	public DefaultStreamBasedInconsistencyMeasure(Class<? extends InconsistencyMeasurementProcess<S>> clazz, Map<String,Object> config){
		this.clazz = clazz;
		this.config = config;
	}
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.commons.analysis.streams.StreamBasedInconsistencyMeasure#inconsistencyMeasure(java.util.Collection)
	 */
	@Override
	public Double inconsistencyMeasure(Collection<S> formulas) {
		// TODO
		return null;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.commons.analysis.stream.StreamBasedInconsistencyMeasure#getInconsistencyMeasureProcess(org.tweetyproject.streams.FormulaStream)
	 */
	@Override
	public InconsistencyMeasurementProcess<S> getInconsistencyMeasureProcess(FormulaStream<S> stream) {		
		try {
			@SuppressWarnings("deprecation")
			InconsistencyMeasurementProcess<S> imp = this.clazz.newInstance();
			imp.init(stream, this, config);			
			return imp;			
		} catch (InstantiationException | IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.commons.analysis.stream.StreamBasedInconsistencyMeasure#addInconsistencyListener(org.tweetyproject.logics.commons.analysis.stream.InconsistencyListener)
	 */
	@Override
	public void addInconsistencyListener(InconsistencyListener listener) {
		this.listeners.add(listener);		
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.commons.analysis.stream.StreamBasedInconsistencyMeasure#removeInconsistencyListener(org.tweetyproject.logics.commons.analysis.stream.InconsistencyListener)
	 */
	@Override
	public void removeInconsistencyListener(InconsistencyListener listener) {
		this.listeners.remove(listener);
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.commons.analysis.stream.StreamBasedInconsistencyMeasure#fireInconsistencyUpdateEvent(org.tweetyproject.logics.commons.analysis.stream.InconsistencyUpdateEvent)
	 */
	@Override
	public void fireInconsistencyUpdateEvent(InconsistencyUpdateEvent evt) {
		for(InconsistencyListener listener: this.listeners)
			listener.inconsistencyUpdateOccured(evt);		
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return "DSBIM";
	}

	@Override
	void fireInconsistencyMeasurementStartedEvent(InconsistencyUpdateEvent evt) {
		for(InconsistencyListener listener: this.listeners)
			listener.inconsistencyMeasurementStarted(evt);
	}
}
