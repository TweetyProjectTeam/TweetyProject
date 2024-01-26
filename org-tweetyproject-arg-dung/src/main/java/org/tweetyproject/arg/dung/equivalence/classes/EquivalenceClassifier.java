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

import java.io.File;
import java.io.FileReader;
import java.io.IOException;

import org.tweetyproject.arg.dung.equivalence.Equivalence;
import org.tweetyproject.arg.dung.examples.EquivalenceCompExFinderExample;
import org.tweetyproject.arg.dung.parser.ApxFilenameFilter;
import org.tweetyproject.arg.dung.parser.ApxParser;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.writer.ApxWriter;

/**
 * This class is responsible for classifying abstract argumentation frameworks due to their equivalence
 * @author Julian Sander
 * @version TweetyProject 1.23
 */
public class EquivalenceClassifier {

	private String pathToFolderClasses;
	private Equivalence<DungTheory> equivalence;

	/**
	 * *description missing*
	 * @param equivalence *description missing*
	 * @param path *description missing*
	 */
	public EquivalenceClassifier(Equivalence<DungTheory> equivalence, String path) {
		super();
		this.pathToFolderClasses = path;
		createFolderForClasses(path);
		this.equivalence = equivalence;
	}
	
	/**
	 * Examines a specified argumentation framework wrt a possible new class
	 * @param framework Argumentation framework, which is a candidate for a new equivalence class
	 * @return TRUE iff a new class was added to the classifier
	 */
	public boolean examineNewTheory(DungTheory framework) {
		var classes = loadClasses();
		for (DungTheory dungTheory : classes) {
			if(equivalence.isEquivalent(dungTheory, framework)) return false;
		}
		
		saveClass(framework, classes.length);
		return true;
	}
	
	/**
	 * @param framework Framework, to which an equivalence class shall be found
	 * @return A abstract argumentation framework, representing the equivalence class of the specified framework
	 * @throws ClassNotFoundException Thrown if no suitable equivalence class was found
	 */
	public DungTheory getEquivalenceClass(DungTheory framework) throws ClassNotFoundException {
		for (DungTheory dungTheory : loadClasses()) {
			if(equivalence.isEquivalent(dungTheory, framework)) return dungTheory;
		}
		
		throw new ClassNotFoundException();
	}
	
	/**
	 * @return Array of abstract argumentation frameworks. Each represents one equivalence class.
	 */
	public DungTheory[] getClasses() {
		return loadClasses();
	}
	
	/**
	 * @return The equivalence definition used by this classifier
	 */
	public Equivalence<DungTheory> getEquivalence() {
		return this.equivalence;
	}
	
	private void createFolderForClasses(String path) {
		var folder = new File(path);
		folder.mkdirs();
		//System.out.println("Created Folder in: " + path );
	}
	
	private boolean saveClass(DungTheory eqClass, int index) {
		var writer = new ApxWriter();
		var addInfo = new String[1];
		addInfo[0] = "Equivalence:" + this.getEquivalence().getDescription();

		var file = new File(this.pathToFolderClasses + File.separator + "Class_" + index + ".apx");
		try {
			writer.write(eqClass, file);
			EquivalenceCompExFinderExample.writeComment(file, addInfo);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	private DungTheory[] loadClasses() {
		var apxFiles = new File(this.pathToFolderClasses).listFiles(new ApxFilenameFilter());
		var output = new DungTheory[apxFiles.length];
		var parser = new ApxParser();
		for (int i = 0; i < apxFiles.length; i++) {
			try {
				output[i] = parser.parseIgnoreComments(new FileReader(apxFiles[i]), false);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return output;
	}
}
