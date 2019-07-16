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

import net.sf.tweety.commons.Formula;

/**
 * An event that is thrown when an inconsistency value
 * has been updated. 
 * 
 * @author Matthias Thimm
 */
public class InconsistencyUpdateEvent {

	protected StreamBasedInconsistencyMeasure<?> measure;
	protected InconsistencyMeasurementProcess<?> process;
	protected Double inconsistencyValue;
	protected Formula f;
	
	/**
	 * Creates a new event with the given parameters.
	 * @param measure a stream-based inconsistency measure
	 * @param process an inconsistent measurement process
	 * @param inconsistencyValue an inconsistency value
	 * @param f a formula
	 */
	public InconsistencyUpdateEvent(StreamBasedInconsistencyMeasure<?> measure, InconsistencyMeasurementProcess<?> process, Double inconsistencyValue, Formula f){
		this.measure = measure;
		this.process = process;
		this.inconsistencyValue = inconsistencyValue;
		this.f = f;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString(){
		return "InconsistencyUpdateEvent: <" + this.inconsistencyValue + "," + this.process + "," + this.f + "," + this.measure + ">";
	}
}
