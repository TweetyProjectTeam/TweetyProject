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

import java.util.EventListener;

/**
 * Listener interface for listeners of stream-based inconsistency measures.
 * @author Matthias Thimm
 */
public interface InconsistencyListener extends EventListener {

	/**
	 * This method is called by a stream-based inconsistency measure when
	 * an update of an inconsistency value occurs.
	 * @param evt some event.
	 */
	public void inconsistencyUpdateOccured(InconsistencyUpdateEvent evt);
	
	/**
	 * This method is called by a stream-based inconsistency measure when
	 * an inconsistency measurement has started.
	 * @param evt some event.
	 */
	public void inconsistencyMeasurementStarted(InconsistencyUpdateEvent evt);
}
