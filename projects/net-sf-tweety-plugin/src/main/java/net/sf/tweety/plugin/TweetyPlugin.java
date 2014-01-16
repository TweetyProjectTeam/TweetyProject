package net.sf.tweety.plugin;

import java.io.File;

import java.util.List;

import net.sf.tweety.plugin.parameter.CommandParameter;
import net.xeoh.plugins.base.Plugin;

/**
 * This class provides the base for each plugin's functionality
 * 
 * @author Bastian Wolf
 *
 */

public interface TweetyPlugin extends Plugin {
	
	/**
	 * returns the keyword used in the cli to call this plugin
	 * @return the keyword used in the cli to call this plugin
	 */
	public String getCommand();	
	
	/**
	 * passes by the arguments given with the call to the called plugin
	 * 
	 * @param input files to be used within the plugin
	 * @param params parameter handled in the plugin (e.g. desired output file, iterations...)
	 * @return the output resulted after the execution
	 */
	public PluginOutput execute(File[] input, CommandParameter[] params); 
		
	/**
	 * returns parameters allowed with plugin calls
	 * @return parameters allowed with plugin calls
	 */
	public List<CommandParameter> getParameters();
	
	
	
	// future work:
	// getDependencies()-Methode

}
