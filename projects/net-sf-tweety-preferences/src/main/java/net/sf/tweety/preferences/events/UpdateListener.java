package net.sf.tweety.preferences.events;

import java.util.EventListener;
//Event-Listener

/**
 * The interface for UpdateListener used for dynamic preference aggregation
 * 
 * @author Bastian Wolf
 *
 * @param <T> the generic element's type
 */

public interface UpdateListener<T> extends EventListener {
	
	/**
	 * UpdateListener is informed as soon as an update occurs
	 * @param e the update event observed by the listener
	 */
	void eventOccurred(UpdateEvent<T> e);
	
}
