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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * An inconsistency listener that is apt for doing evaluations on the
 * performance of an inconsistency measure. Stores runtime and further information
 * in a csv file.
 * 
 * @author Matthias Thimm
 *
 */
public class EvaluationInconsistencyListener implements InconsistencyListener{

	/** The file where the results are stored. */
	private File file;
	/** The maximum number of events this listener
	 * listens to an inconsistency measure. Afterwards this stream processing
	 * is aborted and the file is written to disk.*/
	private int maxEvents;
	/**The current number of events. */
	private int numberOfEvents;
	/** The previous timestamp. */
	private long lastMillis;
	/** Sum of all steps. */
	private long cumulativeTime;
	

	
	// TEMPORARY BEGIN
	public static double INCDEFAULTVALUE = -1;
	public static double TOLERANCE = -1;
	// TEMPORATY END
	
	
	
	public EvaluationInconsistencyListener(String file, int maxEvents){
		this.file = new File(file);
		this.maxEvents = maxEvents;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.streams.InconsistencyListener#inconsistencyUpdateOccured(net.sf.tweety.logics.commons.analysis.streams.InconsistencyUpdateEvent)
	 */
	@Override
	public void inconsistencyUpdateOccured(InconsistencyUpdateEvent evt) {
		this.numberOfEvents++;
		String log = this.numberOfEvents + ";";
		log += (System.currentTimeMillis() - this.lastMillis) + ";";
		this.cumulativeTime += (System.currentTimeMillis() - this.lastMillis);
		this.lastMillis = System.currentTimeMillis();
		log += this.cumulativeTime + ";";
		log += evt.measure.toString() + ";";
		log += evt.process.toString() + ";";
		log += evt.f.toString() + ";";
		log += evt.inconsistencyValue + "\n";
		//TEMPORARY BEGIN
		if(EvaluationInconsistencyListener.TOLERANCE != -1){		
			if(evt.inconsistencyValue >= EvaluationInconsistencyListener.INCDEFAULTVALUE - EvaluationInconsistencyListener.TOLERANCE && 
					evt.inconsistencyValue <= EvaluationInconsistencyListener.INCDEFAULTVALUE + EvaluationInconsistencyListener.TOLERANCE){
				evt.process.abort();
				log += "END\n";
			}
		}
		//TEMPORARY END
		if(this.numberOfEvents+1 > this.maxEvents){
			
			// abort
			evt.process.abort();
			log += "END\n";							
		}
		this.writeToDisk(log);
	}

	/**
	 * Writes the given log to disk.
	 * @param log some string.
	 */
	private void writeToDisk(String log){
		try {
			FileWriter writer = new FileWriter(this.file, true);
			writer.append(log);
			writer.close();
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.streams.InconsistencyListener#inconsistencyMeasurementStarted(net.sf.tweety.logics.commons.analysis.streams.InconsistencyUpdateEvent)
	 */
	@Override
	public void inconsistencyMeasurementStarted(InconsistencyUpdateEvent evt) {
		this.numberOfEvents = 0;
		this.writeToDisk("BEGIN\n");
		this.lastMillis = System.currentTimeMillis();
		this.cumulativeTime = 0;
	}	
}
