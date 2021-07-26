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
package org.tweetyproject.logics.pl.plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.tweetyproject.commons.ParserException;
import org.tweetyproject.logics.pl.parser.PlParser;
import org.tweetyproject.logics.pl.reasoner.AbstractPlReasoner;
import org.tweetyproject.logics.pl.reasoner.SimplePlReasoner;
import org.tweetyproject.logics.pl.reasoner.SatReasoner;
import org.tweetyproject.logics.pl.sat.Sat4jSolver;
import org.tweetyproject.logics.pl.sat.SatSolver;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.PlFormula;
import org.tweetyproject.plugin.*;
import org.tweetyproject.plugin.parameter.CommandParameter;
import org.tweetyproject.plugin.parameter.SelectionCommandParameter;
import org.tweetyproject.plugin.parameter.StringListCommandParameter;
import net.xeoh.plugins.base.annotations.Capabilities;
import net.xeoh.plugins.base.annotations.PluginImplementation;

/**
 * This class provides the JSPF-Plugin for the propositional logic library
 * Currently plugin is loadable but not executed
 * @author Bastian Wolf
 *	
 */

@PluginImplementation
public class PlPlugin extends AbstractTweetyPlugin {

	// <---- STATIC DELCARATION ----->	
	
	
	
	// this plugins call parameter
	private static final String PROPLOGIC__CALL_PARAMETER = "pl";
	// this plugins description
	// private static final String PROPLOGIC__PLUGIN_DESCRIPTION = "";

	
	// option capabilities
//	private static final OptionCapabilities PROPLOGIC__OPTIONS = new OptionCapabilities("TweetyProject Plugin", PROPLOGIC__CALL_PARAMETER);
	/**
	 * 
	 * @return capabilities representation
	 */
	@Capabilities
	public String[] capabilities() { return new String[] {"TweetyProject Plugin", PROPLOGIC__CALL_PARAMETER}; }
	
	
	// parameter for the used solver
	private static final String PROPLOGIC__REASONER_IDENTIFIER = "-reasoner";

	private static final String PROPLOGIC__REASONER_DESCRIPTION = "-reasoner <solver>, use given solver (as String)";

	private static final String[] PROPLOGIC__REASONER_SOLVERENUM = new String[] {
			"sat4j", "naive", "lingeling" };

	// parameter for query formula
	private static final String PROPLOGIC__QUERY_IDENTIFIER = "-query";

	private static final String PROPLOGIC__QUERY_DESCRIPTION = "-query <formula>, " +
			"check whether result satisfies query. Please note: " +
			"with multiple queries EACH MUST be surrounded by \" \" and separated with a single blank between.";

// <---- STATIC DELCARATION ----->	
	
	/**
	 * returns the parameter used to call this plugin from the tweety cli
	 */
	@Override
	public String getCommand() {
		return PROPLOGIC__CALL_PARAMETER;
	}

	/**
	 * non-empty constructor in case of problems concerning jspf
	 * @param args never observed
	 */
	public PlPlugin(String[] args) {
		this();
	}
	
	/**
	 * actually used constructor, initializing start parameters for this plugin
	 */
	public PlPlugin() {
		super();
		this.addParameter(new SelectionCommandParameter(
				PROPLOGIC__REASONER_IDENTIFIER, PROPLOGIC__REASONER_DESCRIPTION,
				PROPLOGIC__REASONER_SOLVERENUM));
		this.addParameter(new StringListCommandParameter(
				PROPLOGIC__QUERY_IDENTIFIER, PROPLOGIC__QUERY_DESCRIPTION));
	}

	/**
	 * Method to be executed with input files and parameters from tweety cli
	 * @return an output written into a file or the console
	 */	
	@Override
	public PluginOutput execute(File[] input, CommandParameter[] params) {
		// new belief set
		PlBeliefSet plbs = new PlBeliefSet();
		// new parser
		PlParser parser = new PlParser();
		// reasoner
		AbstractPlReasoner reasoner = null;
		// queries
		PlFormula[] queries = new PlFormula[1];
		// try to parse all given input files
		
		// TODO: check for multiple input files (kb is overwritten)!
		for (int i = 0; i < input.length; i++) {
			if (input[i].getAbsolutePath().endsWith(".proplogic")) {
				try {
					FileReader fr = new FileReader(input[i].getAbsolutePath());
					plbs = parser.parseBeliefBase(fr);

				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
			}
		}
		// iterate over all given command parameter
		for (CommandParameter tempComParam : params) {
			// if parameter identifier is for a solver
			if (tempComParam.getIdentifier().equals("-reasoner")) {
				SelectionCommandParameter tmp = (SelectionCommandParameter) tempComParam;
				if (tmp.getValue().equalsIgnoreCase("naive")) {
					reasoner = new SimplePlReasoner();
				} else if (tmp.getValue().equalsIgnoreCase("sat4j")) {
					SatSolver.setDefaultSolver(new Sat4jSolver());
					reasoner = new SatReasoner();
				} else if(tmp.getValue().equalsIgnoreCase("lingeling")){
				// TODO: implement lingeling call
				
				} else {
					throw new IllegalArgumentException("Illegal argument: "+ tempComParam.getIdentifier());
				}

			}
			// if parameter identifier is for a query
			if (tempComParam.getIdentifier().equals("-query")) {

				StringListCommandParameter tmp = (StringListCommandParameter) tempComParam;
				queries = new PlFormula[tmp.getValue().length];
				for(int i = 0; i < tmp.getValue().length; i++){
					try {
						queries[i] = (PlFormula) parser.parseFormula(tmp.getValue()[i]);
					} catch (ParserException e) {
						
						e.printStackTrace();
					} catch (IOException e) {
						
						e.printStackTrace();
					}
				}
				
			}
		}

		for(PlFormula pf : queries){
			System.out.println(reasoner.query(plbs,pf));
		}
		
		// TODO: handle output and return appropriate representation
		PluginOutput out = new PluginOutput();
		return out;
	}

}
