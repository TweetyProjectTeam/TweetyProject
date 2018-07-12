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
package net.sf.tweety.plugin;

import java.util.ArrayList;

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
	
	// list of all fields this output contains
	private ArrayList<OutputField> fields;
	// merged output string from all fields
	private String output;
	
	public PluginOutput(){
		fields = new ArrayList<OutputField>();
		output = new String();
	}
	
	public PluginOutput(ArrayList<OutputField> fields){
		this.fields = fields;
	}
	
	public void addField(OutputField field){
		fields.add(field);
	}
	
	public void addField(String description, String value){
		fields.add(new OutputField(description, value));
	}
	
	public void mergeFields(){
		output = "";
		for(OutputField f : fields){
			output += f.merge()+"\n";
		}
	}
	
	public String getOutput(){
		mergeFields();
		return output;
	}

}
