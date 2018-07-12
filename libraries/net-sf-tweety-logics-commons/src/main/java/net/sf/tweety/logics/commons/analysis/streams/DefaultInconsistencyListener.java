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

/**
 * A simple implementation of an inconsistency listener that simply
 * prints out each event to standard output.
 * 
 * @author Matthias Thimm
 *
 */
public class DefaultInconsistencyListener implements InconsistencyListener{

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.stream.InconsistencyListener#inconsistencyUpdateOccured(net.sf.tweety.logics.commons.analysis.stream.InconsistencyUpdateEvent)
	 */
	@Override
	public void inconsistencyUpdateOccured(InconsistencyUpdateEvent evt) {
		System.out.println(evt);		
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.streams.InconsistencyListener#inconsistencyMeasurementStarted(net.sf.tweety.logics.commons.analysis.streams.InconsistencyUpdateEvent)
	 */
	@Override
	public void inconsistencyMeasurementStarted(InconsistencyUpdateEvent evt) {
		// Nothing to do		
	}

}
