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
	/** The log that is written to the file. */
	private String log;
	/** The previous timestamp. */
	private long lastMillis;
	/** Sum of all steps. */
	private long cumulativeTime;
	
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
		this.log += this.numberOfEvents + ";";
		this.log += (System.currentTimeMillis() - this.lastMillis) + ";";
		this.cumulativeTime += (System.currentTimeMillis() - this.lastMillis);
		this.lastMillis = System.currentTimeMillis();
		this.log += this.cumulativeTime + ";";
		this.log += evt.measure.toString() + ";";
		this.log += evt.process.toString() + ";";
		this.log += evt.f.toString() + ";";
		this.log += evt.inconsistencyValue + "\n";
		if(this.numberOfEvents+1 > this.maxEvents){
			// abort
			evt.process.abort();
			this.log += "END\n";
			// write log to file			
			try {
				FileWriter writer = new FileWriter(this.file, true);
				writer.append(this.log);
				writer.close();
			} catch (IOException e) {
				throw new RuntimeException(e);
			}				
		}
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.streams.InconsistencyListener#inconsistencyMeasurementStarted(net.sf.tweety.logics.commons.analysis.streams.InconsistencyUpdateEvent)
	 */
	@Override
	public void inconsistencyMeasurementStarted(InconsistencyUpdateEvent evt) {
		this.numberOfEvents = 0;
		this.log = "BEGIN\n";
		this.lastMillis = System.currentTimeMillis();
		this.cumulativeTime = 0;
	}	
}
