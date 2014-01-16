package net.sf.tweety.preferences.events;

/**
 * This exemplary class implements a simple printer for update events writing its result into the console
 * 
 * @author Bastian Wolf
 *
 * @param <T> the generic type
 */

public class UpdatePrinter<T> implements UpdateListener<T> {

	
	/**
	 * This method is called every time an update occurs
	 */
	public void eventOccurred(UpdateEvent<T> e) {
	
	System.out.println("Updated aggregation result: "+ e.getResult());
	System.out.println(e.getResult().getLevelingFunction());
	System.out.println(e.getResult().getLevelingFunction().getRankingFunction());	
	}

}
