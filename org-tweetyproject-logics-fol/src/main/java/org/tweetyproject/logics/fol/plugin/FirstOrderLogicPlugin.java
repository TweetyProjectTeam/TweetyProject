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
package org.tweetyproject.logics.fol.plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.tweetyproject.commons.ParserException;
import org.tweetyproject.logics.fol.parser.FolParser;
import org.tweetyproject.logics.fol.reasoner.SimpleFolReasoner;
import org.tweetyproject.logics.fol.syntax.FolBeliefSet;
import org.tweetyproject.logics.fol.syntax.FolFormula;
import org.tweetyproject.plugin.AbstractTweetyPlugin;
import org.tweetyproject.plugin.PluginOutput;
import org.tweetyproject.plugin.parameter.CommandParameter;
import org.tweetyproject.plugin.parameter.SelectionCommandParameter;
import org.tweetyproject.plugin.parameter.StringListCommandParameter;
import net.xeoh.plugins.base.annotations.Capabilities;
import net.xeoh.plugins.base.annotations.PluginImplementation;

/**
 * This class models the plugin for first order logics used in the tweety cli
 * Note: Currently FOL-files MUST BE named after the pattern "*.fologic"
 *  Very early state, not finished or debugged yet.
 * @author Bastian Wolf
 *
 */
@PluginImplementation
public class FirstOrderLogicPlugin extends AbstractTweetyPlugin {

	// <---- STATIC DELCARATION ----->


	// the static identifier for this plugin
	private static final String FOLOGIC__CALL_PARAMETER = "fol";

//	private static final String FOL__PLUGIN_DESCRIPTION = "";
	/**
	 *
	 * Return String representation of capabilities
	 * @return String representation of capabilities
	 */
	@Capabilities
	public String[] capabilities() { return new String[] {"TweetyProject Plugin", FOLOGIC__CALL_PARAMETER}; }

	// reasoner enum command parameter
	private static final String FOLOGIC__REASONER_IDENTIFIER = "-reasoner";

	private static final String FOLOGIC__REASONER_DESCRIPTION = "-reasoner <solver>, use given solver with query";
	// TODO: check this!
	private static final String[] FOLOGIC__REASONER_SOLVERENUM = {"classic"};

	// query input parameter
	private static final String FOLOGIC__QUERY_IDENTIFIER = "-query";

	private static final String FOLOGIC__QUERY_DESCRIPTION = "-query <formula>, one or more queries to be checked against knowledge base";

	// <---- STATIC DELCARATION ----->

	/**
	 * This class returns the parameter used to call this plugin
	 * @return the parameter used to call this plugin
	 */
	@Override
	public String getCommand() {
		return FOLOGIC__CALL_PARAMETER;
	}


	/**
	 * non-empty constructor in case of problems concerning jspf
	 * @param args never observed
	 */
	public FirstOrderLogicPlugin(String[] args) {
		this();
	}

	/**
	 * actually used constructor, initializes start parameters for this plugin
	 */
	public FirstOrderLogicPlugin() {
		super();
		this.addParameter(new SelectionCommandParameter(FOLOGIC__REASONER_IDENTIFIER, FOLOGIC__REASONER_DESCRIPTION,FOLOGIC__REASONER_SOLVERENUM));
		this.addParameter(new StringListCommandParameter(FOLOGIC__QUERY_IDENTIFIER, FOLOGIC__QUERY_DESCRIPTION));
	}

	/**
	 * Executes this plugin with given input files and other aggregated parameters
	 * @param input files to be parsed (e.g. knowledge base)
	 * @param params other parameter like queries,
	 * @return the output calculated from input files and arguments
	 */
	@Override
	public PluginOutput execute(File[] input, CommandParameter[] params) {

		FolBeliefSet folbs = new FolBeliefSet();

		FolParser parser = new FolParser();

		SimpleFolReasoner reasoner = null;

		FolFormula[] queries = new FolFormula[1];

		// read in all input files (knowledge base)
		for(int i = 0; i < input.length; i++){
			if(input[i].getAbsolutePath().endsWith(".fologic")) {
				try {
					FileReader fr = new FileReader(input[i].getAbsolutePath());
					folbs = parser.parseBeliefBase(fr);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (ParserException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}

		for (CommandParameter tempComParam : params){
			// if parameter identifier is for a solver
			if(tempComParam.getIdentifier().equals("-reasoner")){
				SelectionCommandParameter tmp = (SelectionCommandParameter) tempComParam;
				if(tmp.getValue().equalsIgnoreCase("classic")){
					reasoner = new SimpleFolReasoner();
				}
			}
			// if parameter identifier is for a query
			if(tempComParam.getIdentifier().equals("-query")){
				// cast command parameter to correct subclass
				StringListCommandParameter tmp = (StringListCommandParameter) tempComParam;
				// re-initialize queries with correct length
				queries = new FolFormula[tmp.getValue().length];
				// parse in all queries
				for(int i = 0; i<tmp.getValue().length; i++){
					try {
						queries[i] = (FolFormula) parser.parseFormula(tmp.getValue()[i]);

					} catch (ParserException e) {

						e.printStackTrace();
					} catch (IOException e) {

						e.printStackTrace();
					}
				}
			}

		}

		// Test:
		// apply all queries and print out results
		for(FolFormula folf : queries){
			System.out.println(reasoner.query(folbs, folf));
		}

		// TODO: make up and return plugin output
		PluginOutput out = new PluginOutput();
		out.addField("Belief Base: ", folbs.toString());
		for(FolFormula folf : queries){
			out.addField("Query: ", folf.toString());
		}

		return out;
	}

}
