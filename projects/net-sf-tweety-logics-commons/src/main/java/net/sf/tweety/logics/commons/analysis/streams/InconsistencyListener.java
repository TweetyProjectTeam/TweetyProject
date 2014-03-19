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
}
