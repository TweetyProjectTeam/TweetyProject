package net.sf.tweety.plugin;

/**
 * This class provides the output for each plugin to be used in the CLI.
 * Only meant for command line output, not for writing into output files (those are handled within each project
 * as well as parsing input files)
 * 
 * @author Bastian Wolf
 *
 */

public class PluginOutput {
	
	// TODO: implementation of solid representation (e.g. Output-Strings, error-messages, help-text)
	private String out;
	
	public PluginOutput(String output) {
		this.out = output;
	}
	
	public void appendToOutput(String ap){
		out += ap;
	}
	
	public String getOutput(){
		return out;
	}

}
