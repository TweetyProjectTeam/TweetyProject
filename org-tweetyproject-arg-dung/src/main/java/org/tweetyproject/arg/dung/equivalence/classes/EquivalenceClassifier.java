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
 *  Copyright 2023 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.equivalence.classes;

import java.util.HashSet;

import org.tweetyproject.arg.dung.equivalence.Equivalence;
import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * This class is responsible for classifying abstract argumentation frameworks due to their equivalence
 * @author Julian Sander
 * @version TweetyProject 1.23
 */
public class EquivalenceClassifier {

	private HashSet<DungTheory> classes;
	private Equivalence<DungTheory> equivalence;

	public EquivalenceClassifier(Equivalence<DungTheory> equivalence) {
		super();
		this.classes = new HashSet<DungTheory>();
		this.equivalence = equivalence;
	}
	
	/**
	 * Examines a specified argumentation framework wrt a possible new class
	 * @param framework Argumentation framework, which is a candidate for a new equivalence class
	 * @return TRUE iff a new class was added to the classifier
	 */
	public boolean examineNewTheory(DungTheory framework) {
		for (DungTheory dungTheory : classes) {
			if(equivalence.isEquivalent(dungTheory, framework)) return false;
		}
		
		return this.classes.add(framework);
	}
	
	/**
	 * @param framework Framework, to which an equivalence class shall be found
	 * @return A abstract argumentation framework, representing the equivalence class of the specified framework
	 * @throws ClassNotFoundException Thrown if no suitable equivalence class was found
	 */
	public DungTheory getEquivalenceClass(DungTheory framework) throws ClassNotFoundException {
		for (DungTheory dungTheory : classes) {
			if(equivalence.isEquivalent(dungTheory, framework)) return dungTheory;
		}
		
		throw new ClassNotFoundException();
	}
	
	/**
	 * @return Array of abstract argumentation frameworks. Each represents one equivalence class.
	 */
	public DungTheory[] getClasses() {
		return classes.toArray(new DungTheory[0]);
	}
	
	
}
