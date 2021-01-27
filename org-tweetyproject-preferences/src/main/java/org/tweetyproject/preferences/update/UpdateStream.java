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
package org.tweetyproject.preferences.update;

import java.util.LinkedList;

/**
 * Konzeption:
 * 
 * Der Update-Stream ist eine Datenstruktur, die benutzt wird, um eine Reihe von Updates fuer dynamische POs
 * einzuspeisen und zu verwalten. In dem Stream stehen Update-Elemente vom Typ Update, die wie folgt aufgebaut sind:
 * Update(Praeferenzordnung, Operation, Anzahl der Operationen, Element)
 * (Update&lt;PreferenceOrder&lt;T&gt;, Operation, Integer, T&gt;)
 * 
 * Pro Aggregator-Instanz wird ein UpdateStream sowie Reader/Writer von dessen Konstruktor erzeugt.
 * 
 * Fragen/Probleme:
 * - Writer exklusiv zugaenglich fuer die Listener eines Aggregators?
 *  
 * @author Bastian Wolf
 *
 * @param <T> generic preference order type
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
 