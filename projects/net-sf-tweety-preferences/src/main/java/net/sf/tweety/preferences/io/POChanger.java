package net.sf.tweety.preferences.io;

import net.sf.tweety.commons.util.Triple;
import net.sf.tweety.preferences.PreferenceOrder;

/**
 * This class is meant to provide a structure to change preference orders and compare the results between changes
 * 
 * @author Bastian Wolf
 * @param <T> generic type of elements
 *
 */

//TODO Struktur fuer dymanische Aggregation definieren (Signaturen, Attribute...)

//TODO zu Stream aendern
public class POChanger<T> {

	/**
	 * The Triple t(x,y,z) contains the element y whose value in the po x' ranking function should be overwritten with parameter z
	 */
	Triple<PreferenceOrder<T>,T, Integer> operation;
	
	public POChanger(Triple<PreferenceOrder<T>,T, Integer> op){
		this.operation = op;
	}
	
	/**
	 * 
	 */
	public void changeOrder(){
		
	}
}
