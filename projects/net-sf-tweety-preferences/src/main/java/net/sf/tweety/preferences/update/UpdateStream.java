package net.sf.tweety.preferences.update;

import java.util.LinkedList;

/**
 * Konzeption:
 * 
 * Der Update-Stream ist eine Datenstruktur, die benutzt wird, um eine Reihe von Updates für dynamische POs
 * einzuspeisen und zu verwalten. In dem Stream stehen Update-Elemente vom Typ Update, die wie folgt aufgebaut sind:
 * Update(Präferenzordnung, Operation, Anzahl der Operationen, Element)
 * (Update<PreferenceOrder<T>, Operation, Integer, T>)
 * 
 * Pro Aggregator-Instanz wird ein UpdateStream sowie Reader/Writer von dessen Konstruktor erzeugt.
 * 
 * Fragen/Probleme:
 * - Writer exklusiv zugänglich für die Listener eines Aggregators?
 *  
 * @author Bastian Wolf
 *
 * @param <T>
 */

public class UpdateStream<T> {
	
	public LinkedList<Update<T>> stream;
	
	// Konstruktor
	public UpdateStream(){
		this.stream = new LinkedList<Update<T>>();
	}
	
	// Methoden....
	public void add(Update<T> update){
		this.stream.addLast(update);
	}

	public Update<T> next(){
		Update<T> temp = this.stream.getFirst();
		this.stream.removeFirst();
		return temp;
	}
	
	public boolean isEmpty(){
		return this.stream.isEmpty();
	}
}
 