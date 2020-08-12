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
 *  Copyright 2020 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.commons.util;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * 
 * Generates an overview of example classes and resources in the workspace with
 * HTML formatting.
 * 
 * @author Anna Gessler
 *
 */
public class ExamplesHTMLGenerator {

	private static String sourceforge_base_path = "https://sourceforge.net/p/tweety/code/HEAD/tree/trunk/";

	/**
	 * Generates SourceForge urls for example classes.
	 */
	private static String generateSFExamplePath(String modulename) {
		return modulename + "/src/main/java/" + modulename.replace("-", "/");
	}

	/**
	 * Generates SourceForge urls for resources.
	 */
	private static String generateSFResourcePath(String modulename) {
		return modulename + "/src/main/";
	}

	// HTML templates for the table of contents
	private static String index_template = "<ul>\n" + "	<li><a href='#sec-general'>General Libraries</a>\n"
			+ "	<ul> $GENERAL_LIBRARIES_LIST </ul>\n" + "	</li>\n"
			+ "	<li><a href='#sec-logic'>Logic Libraries</a>\n" + "	<ul> $LOGIC_LIBRARIES_LIST </ul>\n" + "	</li>\n"
			+ "	<li><a href='#sec-lp'>Logic Programming Libraries</a>\n" + "	<ul> $LP_LIBRARIES_LIST </ul>\n"
			+ "	</li>\n" + "	<li><a href='#sec-arg'>Argumentation Libraries</a>\n"
			+ "	<ul> $ARG_LIBRARIES_LIST </ul>\n" + "	</li>\n" + "	<li><a href='#sec-agent'>Agent Libraries</a>\n"
			+ "	<ul> $AGENT_LIBRARIES_LIST </ul>\n" + "	</li>\n" + "	<li><a href='#sec-other'>Other Libraries</a>\n"
			+ "	<ul> $OTHER_LIBRARIES_LIST </ul>\n" + "	</li>\n" + "</ul>";
	private static String index_item_template = "\n<li><a href='#$MODULELINK'>$MODULENAME</a> (<span style='font-family: Courier'>$MODULEPATH</span>)</li>";

	// HTML template for the library sections
	private static String libraries_template = "<hr noshade='noshade' size='1' />\n"
			+ "<a name='sec-general'></a><h3>General Libraries</h3>\n" + "$GENERAL_LIBRARIES\n" + "\n"
			+ "<hr noshade='noshade' size='1' />\n" + "<a name='#sec-logic'></a><h3>Logic Libraries</h3>\n"
			+ "$LOGIC_LIBRARIES\n" + "\n" + "<hr noshade='noshade' size='1' />\n"
			+ "<a name='sec-lp'></a><h3>Logic Programming Libraries</h3>\n" + "$LP_LIBRARIES\n" + "\n"
			+ "<hr noshade='noshade' size='1' />\n" + "<a name='sec-arg'></a><h3>Argumentation Libraries</h3>\n"
			+ "$ARG_LIBRARIES\n" + "\n" + "<hr noshade='noshade' size='1' />\n"
			+ "<a name='sec-agent'></a><h3>Agent Libraries</h3>\n" + "$AGENT_LIBRARIES\n" + "\n"
			+ "<hr noshade='noshade' size='1' />\n" + "<a name='sec-other'></a><h3>Other Libraries</h3>\n"
			+ "$OTHER_LIBRARIES";

	// HTML templates for the examples and resources of individual libraries
	private static String module_template = "\n<a name='$MODULELINK'></a><h4>$MODULENAME (<span style='font-family: Courier'>$MODULEPATH</span>)</h4>\n"
			+ "Example code: $EXAMPLES \n Resources: $RESOURCES";
	private static String resources_template = "\n" + "<li><a target='_blank' href='" + sourceforge_base_path
			+ "libraries/$MODULE_SF_PATH/resources/$EXAMPLENAME'><tt>resources.$EXAMPLENAME</tt></a>$DESCRIPTION</li>";
	private static String examples_template = "\n<li><a target='_blank' href='" + sourceforge_base_path
			+ "libraries/$MODULE_SF_PATH/examples/$EXAMPLENAME'><tt>examples.$EXAMPLENAME</tt></a>$DESCRIPTION</li>";
	private static String resources_empty = "<p> <i>no resources available</i> </p>";
	private static String examples_empty = "<p> <i>no example code available</i> </p>";

	/**
	 * Generates an overview of example classes and resources in the workspace with
	 * HTML formatting.
	 * 
	 * @param tweety_libraries_dir path of the Tweety 'libraries' folder (can be detected automatically
	 * if left empty)
	 * @return String containing an overview of examples and resources with HTML formatting
	 * @throws IOException
	 */
	private static String generateHTMLOverview(String tweety_libraries_dir) throws IOException {
		if (tweety_libraries_dir.equals(""))
			tweety_libraries_dir = System.getProperty("user.dir");
		tweety_libraries_dir = tweety_libraries_dir.substring(0, tweety_libraries_dir.lastIndexOf("/"));

		// variables that will be used to replace the keywords of the same names
		// in the table of contents HTML template
		String index = index_template;
		String GENERAL_LIBRARIES_LIST = "";
		String LOGIC_LIBRARIES_LIST = "";
		String LP_LIBRARIES_LIST = "";
		String ARG_LIBRARIES_LIST = "";
		String AGENT_LIBRARIES_LIST = "";
		String OTHER_LIBRARIES_LIST = "";

		// variables that will be used to replace the keywords of the same names
		// in the libraries template
		String listings = libraries_template;
		String GENERAL_LIBRARIES = "";
		String LOGIC_LIBRARIES = "";
		String LP_LIBRARIES = "";
		String ARG_LIBRARIES = "";
		String AGENT_LIBRARIES = "";
		String OTHER_LIBRARIES = "";

		File[] tweety_dirs = new File(tweety_libraries_dir).listFiles();
		if (tweety_dirs != null) {
			for (File child : tweety_dirs) {
				if (child.isDirectory() && !child.getName().contains(".settings")) {
					String MODULEPATH = child.getName();
					String MODULENAME = MODULEPATH;

					File[] contents = child.listFiles();

					// Variables that will be used to replace the keywords of the same names
					// in the examples and resources HTML template
					String EXAMPLES = "";
					String RESOURCES = "";

					for (File c : contents) {
						// Get full library name from POM file
						if (c.getName().contains("pom")) {
							Scanner reader = new Scanner(c);
							while (reader.hasNextLine()) {
								String line = reader.nextLine();
								if (line.indexOf("<name>") != -1) {
									MODULENAME = line.substring(line.indexOf("(") + 1, line.indexOf(")"));
									break;
								}
							}
							reader.close();
						}

						// Collect examples and resources file names recursively
						List<Path> examples = new ArrayList<Path>();
						List<Path> resources = new ArrayList<Path>();
						Files.walk(Paths.get(c.getAbsolutePath())).filter(Files::isRegularFile).forEach((f) -> {
							String file = f.toString();
							if (file.contains("resources") && !file.contains(".prefs"))
								resources.add(f);
							else if (file.contains("examples") && !file.contains(".class"))
								examples.add(f);
						});
						
						//sort file names alphabetically
						Collections.sort(examples, (a, b) -> a.getFileName().toString().compareTo(b.getFileName().toString()));
						Collections.sort(resources, (a, b) -> a.getFileName().toString().compareTo(b.getFileName().toString()));

						// Parse example descriptions from javadoc
						for (Path p : examples) {
							String example = examples_template.replace("$EXAMPLENAME", p.getFileName().toString());
							String description = "";
							@SuppressWarnings("resource")
							String doc = new Scanner(p).useDelimiter("\\Z").next();
							if (doc.contains("public class")) {
								doc = doc.substring(doc.indexOf("Copyright"), doc.indexOf("public class")).strip();
								if (doc.contains("/**")) {
									String spl = doc.substring(doc.indexOf("/**") + 1).strip();
									String[] lines = spl.split("\n");

									// Remove comment characters and ignore lines with javadoc tags
									for (String s : lines) {
										if (s.contains("@"))
											continue;
										String line = s.replaceAll("[\\*]", "").replaceAll("\\s{2,}", "");
										line = line.replace("\n", " ").replace("\r", " ").replace(" /", "");
										description += line;
									}
									if (!description.isBlank())
										description = ": " + description;
								}
							}
							EXAMPLES += example.replace("$DESCRIPTION", "");
						}

						for (Path p : resources)
							RESOURCES += resources_template.replace("$EXAMPLENAME", p.getFileName().toString())
									.replace("$DESCRIPTION", "");

						// replace keywords in HTML template
						RESOURCES = RESOURCES.replace("$MODULEPATH", MODULEPATH);
						EXAMPLES = EXAMPLES.replace("$MODULEPATH", MODULEPATH);
						RESOURCES = RESOURCES.replace("$MODULE_SF_PATH", generateSFResourcePath(MODULEPATH));
						EXAMPLES = EXAMPLES.replace("$MODULE_SF_PATH", generateSFExamplePath(MODULEPATH));

					}

					if (EXAMPLES.equals(""))
						EXAMPLES = examples_empty;
					else
						EXAMPLES = "<ul>" + EXAMPLES + "</ul>";
					if (RESOURCES.equals(""))
						RESOURCES = resources_empty;
					else
						RESOURCES = "<ul>" + RESOURCES + "</ul>";

					// replace keywords in HTML template
					String MODULELINK = "lib-" + MODULENAME.toLowerCase().replace(" ", "-");
					String item = index_item_template;
					item = item.replace("$MODULENAME", MODULENAME);
					item = item.replace("$MODULEPATH", MODULEPATH);
					item = item.replace("$MODULELINK", MODULELINK);
					String module_item = module_template;
					module_item = module_item.replace("$MODULENAME", MODULENAME);
					module_item = module_item.replace("$MODULEPATH", MODULEPATH);
					module_item = module_item.replace("$MODULELINK", MODULELINK);
					module_item = module_item.replace("$EXAMPLES", EXAMPLES);
					module_item = module_item.replace("$RESOURCES", RESOURCES);

					// sort modules into categories
					if (MODULEPATH.contains("-logics-")) {
						LOGIC_LIBRARIES_LIST += item;
						LOGIC_LIBRARIES += module_item;
					} else if (MODULEPATH.contains("-arg-")) {
						ARG_LIBRARIES_LIST += item;
						ARG_LIBRARIES += module_item;
					} else if (MODULEPATH.contains("-lp-")) {
						LP_LIBRARIES_LIST += item;
						LP_LIBRARIES += module_item;
					} else if (MODULEPATH.contains("-agents")) {
						AGENT_LIBRARIES_LIST += item;
						AGENT_LIBRARIES += module_item;
					} else if (MODULEPATH.contains("commons") || MODULEPATH.contains("plugin")
							|| MODULEPATH.contains("cli") || MODULEPATH.contains("math")
							|| MODULEPATH.contains("graphs")) {
						GENERAL_LIBRARIES_LIST += item;
						GENERAL_LIBRARIES += module_item;
					} else {
						OTHER_LIBRARIES_LIST += item;
						OTHER_LIBRARIES += module_item;
					}
				}
			}
		} else
			throw new IllegalArgumentException(tweety_libraries_dir + " is not a valid Tweety directory");

		// replace keywords in HTML template
		index = index.replace("$LOGIC_LIBRARIES_LIST", LOGIC_LIBRARIES_LIST);
		index = index.replace("$ARG_LIBRARIES_LIST", ARG_LIBRARIES_LIST);
		index = index.replace("$LP_LIBRARIES_LIST", LP_LIBRARIES_LIST);
		index = index.replace("$AGENT_LIBRARIES_LIST", AGENT_LIBRARIES_LIST);
		index = index.replace("$GENERAL_LIBRARIES_LIST", GENERAL_LIBRARIES_LIST);
		index = index.replace("$OTHER_LIBRARIES_LIST", OTHER_LIBRARIES_LIST);
		listings = listings.replace("$LOGIC_LIBRARIES", LOGIC_LIBRARIES);
		listings = listings.replace("$ARG_LIBRARIES", ARG_LIBRARIES);
		listings = listings.replace("$LP_LIBRARIES", LP_LIBRARIES);
		listings = listings.replace("$AGENT_LIBRARIES", AGENT_LIBRARIES);
		listings = listings.replace("$GENERAL_LIBRARIES", GENERAL_LIBRARIES);
		listings = listings.replace("$OTHER_LIBRARIES", OTHER_LIBRARIES);

		return index + listings;
	}

	/**
	 * Generates an overview of example classes and resources in the workspace with
	 * HTML formatting and writes it to a HTML file.
	 * 
	 * @param path where the generated file will be saved
	 * @throws IOException
	 */
	public static void printExamplesToHtmlFile(String path) throws IOException {
		FileWriter writer = new FileWriter(path + "examples.html");
		writer.write(generateHTMLOverview(""));
		writer.close();
	}

	public static void main(String[] args) throws IOException {
		printExamplesToHtmlFile("/home/anna/");
	}

}
