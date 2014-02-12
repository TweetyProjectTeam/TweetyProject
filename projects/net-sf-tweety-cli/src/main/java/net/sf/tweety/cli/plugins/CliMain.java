package net.sf.tweety.cli.plugins;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import net.sf.tweety.plugin.*;
import net.sf.tweety.plugin.parameter.CommandParameter;
import net.xeoh.plugins.base.PluginManager;
import net.xeoh.plugins.base.impl.PluginManagerFactory;
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

	/** the called plugin */
	private static String plugin;
	/** the list of input files */
	private static File[] inputFiles = new File[1];
	/** the output file */
	//private static String outputFile = null;
	/** the optional plugin parameters */
	//private static CommandParameter[] pluginParams = null;

	/**
	 * prints help text if cli is called with parameter "--help" or empty argument array
	 */
	public static void printHelpText() {
		File help = new File(HELPTEXT).getAbsoluteFile();
		try {
			BufferedReader bfrd = new BufferedReader(new FileReader(help));

			try {
				String line;
				while ((line = bfrd.readLine()) != null) {
//					if (line.length() >= 1) {
						System.out.println(line);
//					}
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
		// TODO : exception handling for empty/erroneous configuration
		Map<String, String> loadablePlugins = new HashMap<String, String>();

		XMLConfiguration tweetyXmlConfig = new XMLConfiguration();
		File in = new File(TWEETY_CLI_DEFAULT_CONFIG);
		try {
			String inPath = in.getAbsolutePath();
			tweetyXmlConfig.setBasePath(inPath.substring(0, inPath.length()
					- TWEETY_CLI_DEFAULT_CONFIG.length() - 1));
			tweetyXmlConfig.load(in);
		} catch (NullPointerException e) {
			e.printStackTrace();
		}

		// map über "plugins.plugin" mit keys ()
		// TODO: Verhalten bei leeren Feldern prüfen
		Iterator<String> it = tweetyXmlConfig.getKeys("plugin");

		
		//TODO fix the casts!
		@SuppressWarnings("unchecked")
		ArrayList<String> pluginPath = (ArrayList<String>) tweetyXmlConfig.getProperty(it.next());
		@SuppressWarnings("unchecked")
		ArrayList<String> pluginName = (ArrayList<String>) tweetyXmlConfig.getProperty(it.next());

		for (int i = 0; i < pluginPath.size(); i++) {
			System.out.println(pluginName.get(i) + pluginPath.get(i));
			loadablePlugins.put(pluginName.get(i), pluginPath.get(i));
		}
		return loadablePlugins;
	}

	/**
	 * TODO: own method for plugin loading
	 * 
	 * @param plugin
	 */
	public static void loadPlugin(String plugin) {
		// move plugin loading in here
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
		// System.out.println(args.length);
		if (args.length == 0) {
			System.out.println("Tweety CLI: Obtain help with command --help");
			System.exit(0);
		} else if ((args.length == 1 && args[0].equals("--help"))) {
			printHelpText();
			System.exit(0);
		} else if (args.length == 1 && !args[0].contains("--help")){
			System.out.println("No valid input, call with --help for helptext");
			System.exit(0);
		}

		// Unused!
		//TweetyPlugin tweetyPlugin;
		PluginManager pm = PluginManagerFactory.createPluginManager();
		PluginManagerUtil pmu = new PluginManagerUtil(pm);
		System.out.println(pmu.getPlugins());
		ArrayList<ArrayList<String>> collectedparams = new ArrayList<ArrayList<String>>();
		// Unused!
		//List<CommandParameter> inParams = new ArrayList<CommandParameter>();

		Map<String, String> availablePlugins = new HashMap<String, String>();

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

		// TODO implement the main CLI
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
				//outputFile not used!
				//outputFile = args[++i];
			}

			// collecting given command parameters
			else if (args[i].startsWith("-")) {
				ArrayList<String> temp = new ArrayList<String>();
				temp.add(args[i]);
				while ((i + 1) < args.length && !args[i + 1].startsWith("-")) {
					temp.add(args[++i]);

				}
				collectedparams.add(temp);
			}
		}

		// check whether the called plugin is present
		boolean pluginPresent = false;
		for (TweetyPlugin tp : pmu.getPlugins(TweetyPlugin.class)) {
			if (tp.getCommand().equalsIgnoreCase(plugin)) {
				pluginPresent = true;
			}
		}
		// TODO: move loading into own method
		// trying to load plugin if not present
		if (!pluginPresent) {
			if (availablePlugins.containsKey(plugin)) {
				pm.addPluginsFrom(new File(availablePlugins.get(plugin))
						.toURI());
			} else {
				System.out.println("No such plugin available.");

			}
		}

		// Testausgabe aller Plugins
		 System.out.println(pm.getPlugin(TweetyPlugin.class));
		 System.out.println(pmu.getPlugins());

		//
		for (TweetyPlugin tp : pmu.getPlugins(TweetyPlugin.class)) {
			if (tp.getCommand().equalsIgnoreCase(plugin)) {
				// each input parameter is checked against the called plugin
				// whether it is valid
				ArrayList<CommandParameter> ip = new ArrayList<CommandParameter>();
				//Unused!
				//tweetyPlugin = tp;
				try {
					ip.addAll(instantiateParameters(tp, collectedparams));
				} catch (CloneNotSupportedException e) {
					e.printStackTrace();
				}
				PluginOutput out = new PluginOutput();
//				tp.execute(inputFiles,ip.toArray(new CommandParameter[ip.size()]));
				out = tp.execute(inputFiles,
						ip.toArray(new CommandParameter[ip.size()]));
				System.out.println("Output: \n" + out.getOutput());

			}

		}

	}

}
