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
 *  Copyright 2019 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.arg.adf.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class TestUtil {

	public static final String DEFAULT_SOLUTION_EXTENSION = "solutions";

	/**
	 * Merges the solution files from solvers like diamond or k++adf into a
	 * single one. Allows us to keep the number of files per instance much lower
	 * since we must not store the models in a separate file for each semantics.
	 * 
	 * @param dir
	 *            the path
	 * @param semantics
	 * @param outputExtension
	 * @throws IOException
	 */
	public static void mergeSolutionFiles(String dir, String[] semantics, String outputExtension) throws IOException {
		File[] instances = new File(dir).listFiles((File f, String name) -> name.endsWith(".adf"));
		for (File instance : instances) {
			// scan for solution files per instance and semantic
			PrintStream out = new PrintStream(new File(instance.getPath() + "." + outputExtension));
			for (String semantic : semantics) {
				File solutionFile = new File(instance.getPath() + "." + semantic);
				if (solutionFile.exists()) {
					// group the models by their semantics
					out.println("[" + semantic + "]");
					BufferedReader reader = new BufferedReader(
							new InputStreamReader(new FileInputStream(solutionFile)));
					String line = null;
					Set<String> last = new HashSet<String>();
					while ((line = reader.readLine()) != null) {
						// filter out all non-model output
						if (line.matches("(\\s*(t|f|u)\\([a-zA-Z0-9]*\\)\\s*)+")) {
							// only print distinct interpretations
							if (!last.contains(line)) {
								out.println(line);
								last.add(line);
							}
						}
					}
					reader.close();
				}
			}
			out.close();
		}
	}

	/**
	 * Reads all models from the given solution file and groups them by their
	 * semantics.
	 * 
	 * @param file
	 * @return a mapping of all the models per semantic
	 * @throws IOException
	 */
	public static Map<String, Set<Map<String, Boolean>>> readSolutionFile(File file) throws IOException {
		Map<String, Set<Map<String, Boolean>>> modelsPerSemantic = new HashMap<>();
		BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
		String line = null;
		String currentSemantics = null;
		while ((line = reader.readLine()) != null) {
			line = line.trim();
			// semantics line
			if (line.matches("\\[[a-zA-Z]+\\]")) {
				currentSemantics = line.substring(1, line.length() - 1);
				modelsPerSemantic.put(currentSemantics, new HashSet<Map<String, Boolean>>());
			}
			// model line
			else if (line.matches("(\\s*(t|f|u)\\([a-zA-Z0-9]+\\)\\s*)+")) {
				Map<String, Boolean> assignment = new HashMap<String, Boolean>();
				String[] splitted = line.split("\\s+");
				for (String s : splitted) {
					Boolean label = charToBool(s.charAt(0));
					String argument = s.substring(2, s.length() - 1);
					assignment.put(argument, label);
				}
				modelsPerSemantic.get(currentSemantics).add(assignment);
			}
		}
		reader.close();
		return modelsPerSemantic;
	}

	private static Boolean charToBool(char ch) {
		switch (ch) {
		case 't':
			return true;
		case 'f':
			return false;
		case 'u':
			return null;
		default:
			throw new IllegalArgumentException(ch + " must be 'u', 'f' or 't'");
		}
	}

}
