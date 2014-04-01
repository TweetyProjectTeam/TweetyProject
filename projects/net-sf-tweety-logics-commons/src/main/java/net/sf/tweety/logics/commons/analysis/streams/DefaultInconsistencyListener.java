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
