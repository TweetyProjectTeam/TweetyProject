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
package net.sf.tweety.cli.plugins;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sf.tweety.plugin.*;
import net.sf.tweety.plugin.parameter.CommandParameter;
import net.xeoh.plugins.base.PluginManager;
import net.xeoh.plugins.base.impl.PluginManagerFactory;
import net.xeoh.plugins.base.options.getplugin.OptionCapabilities;
import net.xeoh.plugins.base.util.PluginManagerUtil;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

/**
 * skeleton of the new main method of this CLI using plugins.
 * 
 * @author Bastian Wolf
 * 
 */

public class CliMain {

	public static final String HELPTEXT = "help.txt";

	// TODO: Create external configuration file and Setter
	public static final String TWEETY_CLI_DEFAULT_CONFIG = "tweety_config.xml";

	/** The argument name for the called plugin */
	public static final String ARG__CALLED_PLUGIN = "--plugin";
	/** The argument name for the called plugin (short) */
	public static final String ARG__CALLED_PLUGIN_SHORT = "-p";
	/** The argument name for the input file(s) */
	public static final String ARG__INPUT_FILES = "--input";
	/** The argument name for the input file(s) (short) */
	public static final String ARG__INPUT_FILES_SHORT = "-i";
	/** The argument name for the output file */
	public static final String ARG__OUTPUT_FILE = "--output";
	/** The argument name for the output file (short) */
	public static final String ARG__OUTPUT_FILE_SHORT = "-o";
	/** The argument name for debugging output */
	public static final String ARG__DEBUG_FLAG = "--debug";
	/** The argument name for debugging output (short) */
	public static final String ARG__DEBUG_FLAG_SHORT = "-d";

	/** the called plugin */
	private static String plugin;
	/** the list of input files */
	private static File[] inputFiles = new File[1];
	/** the output file */
	private static String outputFile = null;

	/** the optional plugin parameters */
	// private static CommandParameter[] pluginParams = null;
	// debug flag, false as default
	// private boolean debug = false;

	/**
	 * prints help text if cli is called with parameter "--help" or empty
	 * argument array
	 */
	public static void printHelpText() {
		File help = new File(HELPTEXT).getAbsoluteFile();
		try {
			BufferedReader bfrd = new BufferedReader(new FileReader(help));

			try {
				String line;
				while ((line = bfrd.readLine()) != null) {
					// if (line.length() >= 1) {
					System.out.println(line);
					// }
				}
				bfrd.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (FileNotFoundException f) {
			f.printStackTrace();
		}

	}

	/**
	 * This method is meant to load the tweety plugin pathes on startup
	 * 
	 * @return an object with one or more pluginpathes
	 * @throws ConfigurationException
	 */
	public static Map<String, String> configCLI()
			throws ConfigurationException, FileNotFoundException {

		System.out.println("Initialize CLI...");

		// TODO : exception handling for empty/erroneous configuration
		Map<String, String> loadablePlugins = new HashMap<String, String>();

		XMLConfiguration tweetyXmlConfig = new XMLConfiguration();
		File in = new File(TWEETY_CLI_DEFAULT_CONFIG);
		try {
			System.out.print("Loading Configuration...");
			String inPath = in.getAbsolutePath();
			tweetyXmlConfig.setBasePath(inPath.substring(0, inPath.length()
					- TWEETY_CLI_DEFAULT_CONFIG.length() - 1));
			tweetyXmlConfig.load(in);
			System.out.print("success.\n");
		} catch (NullPointerException e) {
			e.printStackTrace();
		}

		// map ueber "plugins.plugin" mit keys ()
		// TODO: Verhalten bei leeren Feldern pruefen
		// TODO: Verhalten bei einem einzelnen Eintrag prüfen
		Iterator<String> it = tweetyXmlConfig.getKeys("plugin");

		// // TODO fix the casts!
		// if (it.hasNext()) {
		//
		// String pluginPath = (String) tweetyXmlConfig.getProperty(it.next()
		// .toString());
		//
		// String pluginName = (String) tweetyXmlConfig.getProperty(it.next()
		// .toString());
		//
		// // for (int i = 0; i < pluginPath.size(); i++) {
		// // System.out.println(pluginName.get(i) + pluginPath.get(i));
		// loadablePlugins.put(pluginName, pluginPath);
		// }
		// }
		System.out.print("Getting Plugins...");
		// TODO fix the casts!
		if (it.hasNext()) {
			@SuppressWarnings("unchecked")
			ArrayList<String> pluginPath = (ArrayList<String>) tweetyXmlConfig
					.getProperty(it.next());
			@SuppressWarnings("unchecked")
			ArrayList<String> pluginName = (ArrayList<String>) tweetyXmlConfig
					.getProperty(it.next());

			for (int i = 0; i < pluginPath.size(); i++) {
				// System.out.println(pluginName.get(i) + pluginPath.get(i));
				loadablePlugins.put(pluginName.get(i), pluginPath.get(i));
			}
		}
		System.out.print("done.\n");
		System.out.println("CLI initialized");
		return loadablePlugins;
	}

	/**
	 * TODO: own method for plugin loading
	 * 
	 * @param plugin
	 */
	// public static boolean loadPlugin(Map<String, String> availablePlugins,
	// PluginManager plugman, String plugin) {
	// // move plugin loading in here
	// if (availablePlugins.containsKey(plugin)) {
	// plugman.addPluginsFrom(new File(availablePlugins.get(plugin))
	// .toURI(), );
	// } else {
	// System.out.println("No such plugin available.");
	//
	// }
	// }

	/**
	 * This function allows to print the content of the current configuration
	 * file. It consists of pairs of each available plugin an its path.
	 */
	public void printConfiguration() {

	}

	/**
	 * 
	 * @param path
	 */
	public void setConfigurationFilePath(String path) {

	}

	/**
	 * 
	 * @param path
	 */
	public void setHelptextPath(String path) {

	}

	/**
	 * instantiates each given input parameter within the called plugin - if
	 * possible
	 * 
	 * @param tp
	 *            the called Tweety-Plugin Implementation
	 * @param inparams
	 *            the parameter given as input
	 * @return an ArrayList of instantiated CommandParameter
	 * @throws CloneNotSupportedException
	 *             if the CommandParameter does not implement Cloneable
	 */
	public static ArrayList<CommandParameter> instantiateParameters(
			TweetyPlugin tp, ArrayList<ArrayList<String>> inparams)
			throws CloneNotSupportedException {

		// new array list for the instantiated command parameter
		ArrayList<CommandParameter> tmp = new ArrayList<CommandParameter>(
				inparams.size());

		for (int i = 0; i < inparams.size(); i++) {
			// get each inparams entry first element (the identifier for the)
			String cmdIdentifier = inparams.get(i).get(0);
			inparams.get(i).remove(0);
			// checks, if the first element starts with an "-", e.g. "-aggr"
			// instead of "aggr"
			// if(!cmdIdentifier.startsWith("-")){
			// cmdIdentifier = "-" + cmdIdentifier;
			// }

			for (CommandParameter cp : tp.getParameters()) {
				if (cp.getIdentifier().equalsIgnoreCase(cmdIdentifier)) {
					for (int j = 0; j < inparams.get(i).size(); j++) {
						tmp.add(cp.instantiate(inparams.get(i).get(j)));
					}
				}
			}
		}
		return tmp;
	}

	public static void main(String[] args) {

		// check, if first call parameter is for the helptext
		if (args.length == 0) {
			System.out.println("Welcome to the Tweety command line interface.");
			System.out.println("Obtain help with command --help");
			System.exit(0);
		} else if ((args.length == 1 && args[0].equals("--help"))) {
			printHelpText();
			System.exit(0);
		} else if (args.length == 1 && !args[0].contains("--help")) {
			System.out.println("No valid input, call with --help for helptext");
			System.exit(0);
		}

		// create new plugin manager
		PluginManager pm = PluginManagerFactory.createPluginManager();
		// create plugin manager util
		PluginManagerUtil pmu = new PluginManagerUtil(pm);

		// System.out.println(pmu.getPlugins());

		// collected parameter
		ArrayList<ArrayList<String>> collectedparams = new ArrayList<ArrayList<String>>();

		// list of available plugins
		Map<String, String> availablePlugins = new HashMap<String, String>();

		// try to configure CLI
		try {
			availablePlugins = configCLI();
		} catch (ConfigurationException e) {
			System.out
					.println("Something went wrong with your Configuration: ");
			e.printStackTrace();
		} catch (FileNotFoundException e) {
			System.out.println("No such configuration file: ");
			e.printStackTrace();
		}

		// handle all input parameter
		for (int i = 0; i < args.length; i++) {
			// The called plugin
			if (args[i].equals(ARG__CALLED_PLUGIN)
					|| args[i].equals(ARG__CALLED_PLUGIN_SHORT)) {
				String calledPlugin = "";
				while ((i + 1) < args.length && !args[i + 1].startsWith("-")) {
					calledPlugin += args[++i];
				}
				plugin = calledPlugin;
			}

			// the input files
			else if (args[i].equals(ARG__INPUT_FILES)
					|| args[i].equals(ARG__INPUT_FILES_SHORT)) {
				ArrayList<String> inFiles = new ArrayList<String>();
				while ((i + 1) < args.length && !args[i + 1].startsWith("-")) {
					inFiles.add(args[++i]);
				}

				String[] files = new String[inFiles.size()];
				inFiles.toArray(files);

				File[] inf = new File[inFiles.size()];

				for (int k = 0; k < inf.length; k++) {
					inf[k] = new File(files[k]).getAbsoluteFile();
				}

				inputFiles = inf;
			}

			// output file
			else if (args[i].equals(ARG__OUTPUT_FILE)
					|| args[i].equals(ARG__OUTPUT_FILE_SHORT)) {
				// outputFile not used!
				outputFile = args[++i];
			}

			// collecting given command parameters
			else if (args[i].startsWith("-")) {
				ArrayList<String> temp = new ArrayList<String>();
				temp.add(args[i]);
				while ((i + 1) < args.length && !args[i + 1].startsWith("-")) {
					temp.add(args[++i]);

				}
				collectedparams.add(temp);
			} // else if (args[i].equals(ARG__DEBUG_FLAG)
				// ||args[i].equals(ARG__DEBUG_FLAG_SHORT)){
			// debug = true;
			// }
		}

		// check whether the called plugin is present
		boolean pluginPresent = false;
		for (TweetyPlugin tp : pmu.getPlugins(TweetyPlugin.class)) {
			if (tp.getCommand().equalsIgnoreCase(plugin)) {
				pluginPresent = true;
				System.out.println("Called plugin present");
			}
		}
		// TODO: move loading into own method
		// trying to load plugin if not present
		// old method for loading plugins
		if (!pluginPresent) {
			System.out.print("Trying to find plugin...");
			if (availablePlugins.containsKey(plugin)) {
				pm.addPluginsFrom(new File(availablePlugins.get(plugin))
						.toURI());
				System.out.print("success.\n");
			} else {
				System.out.print("no such plugin available.\n");
			}
		}

		// Test: print all plugins
		// System.out.println("Plugin loaded due to call parameter: " +
		// pm.getPlugin(TweetyPlugin.class, new
		// OptionCapabilities("Tweety Plugin", plugin) ));
		// System.out.println("Print all plugins: " + pmu.getPlugins());
		// System.out.println("Given plugin call parameter: " + plugin);

		// each plugin MUST implement the capabilites "Tweety Plugin" and the
		// variable "call parameter" to select called plugin from plugin pool
		TweetyPlugin tp = pm.getPlugin(TweetyPlugin.class,
				new OptionCapabilities("Tweety Plugin", plugin));

		// for (TweetyPlugin tp : pmu.getPlugins(TweetyPlugin.class)) {
		if (tp.getCommand().equalsIgnoreCase(plugin)) {
			System.out.println("Valid plugin found.");
			// each input parameter is checked against the called plugin
			// whether it is valid
			ArrayList<CommandParameter> ip = new ArrayList<CommandParameter>();

			System.out.print("Trying to instantiate parameters...");
			try {
				ip.addAll(instantiateParameters(tp, collectedparams));
				System.out.print("done.\n");
			} catch (CloneNotSupportedException e) {
				e.printStackTrace();
			}
			PluginOutput out = new PluginOutput();

			System.out.println("Execute Plugin...");
			out = tp.execute(inputFiles,
					ip.toArray(new CommandParameter[ip.size()]));

			if (outputFile != null) {

				try {
					FileWriter fw = new FileWriter(outputFile);
					fw.write(out.getOutput());
					fw.close();
					System.out.println("Output written to file: " + outputFile);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			} else {
				System.out
						.println("No output file given, writing to console...");
				System.out.println("Output: \n" + out.getOutput());
			}
		} else {
			System.out.println("Faulty parameters. Please check input.");

		}

	}

}
