/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
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
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.logics.pl.plugin;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import net.sf.tweety.commons.ParserException;
import net.sf.tweety.commons.Reasoner;
import net.sf.tweety.logics.pl.ClassicalEntailment;
import net.sf.tweety.logics.pl.ClassicalInference;
import net.sf.tweety.logics.pl.PlBeliefSet;
import net.sf.tweety.logics.pl.SatReasoner;
import net.sf.tweety.logics.pl.parser.PlParser;
import net.sf.tweety.logics.pl.sat.Sat4jSolver;
import net.sf.tweety.logics.pl.sat.SatSolver;
import net.sf.tweety.logics.pl.syntax.PropositionalFormula;
import net.sf.tweety.plugin.*;
import net.sf.tweety.plugin.parameter.CommandParameter;
import net.sf.tweety.plugin.parameter.SelectionCommandParameter;
import net.sf.tweety.plugin.parameter.StringListCommandParameter;
import net.xeoh.plugins.base.annotations.Capabilities;
import net.xeoh.plugins.base.annotations.PluginImplementation;

/**
 * This class provides the JSPF-Plugin for the propositional logic library
 * Currently plugin is loadable but not executed
 * @author Bastian Wolf
 *	
 */

@PluginImplementation
public class PropositionalLogicPlugin extends AbstractTweetyPlugin {

	// <---- STATIC DELCARATION ----->	
	
	
	
	// this plugins call parameter
	private static final String PROPLOGIC__CALL_PARAMETER = "pl";
	// this plugins description
	// private static final String PROPLOGIC__PLUGIN_DESCRIPTION = "";

	
	// option capabilities
//	private static final OptionCapabilities PROPLOGIC__OPTIONS = new OptionCapabilities("Tweety Plugin", PROPLOGIC__CALL_PARAMETER);
	
	@Capabilities
	public String[] capabilities() { return new String[] {"Tweety Plugin", PROPLOGIC__CALL_PARAMETER}; }
	
	
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
	public PropositionalLogicPlugin(String[] args) {
		this();
	}
	
	/**
	 * actually used constructor, initializing start parameters for this plugin
	 */
	public PropositionalLogicPlugin() {
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
		Reasoner reasoner = null;
		// queries
		PropositionalFormula[] queries = new PropositionalFormula[1];
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
					ClassicalEntailment naiveEntail = new ClassicalEntailment();
					reasoner = new ClassicalInference(plbs, naiveEntail);
				} else if (tmp.getValue().equalsIgnoreCase("sat4j")) {
					SatSolver.setDefaultSolver(new Sat4jSolver());
					reasoner = new SatReasoner(plbs);
				} else if(tmp.getValue().equalsIgnoreCase("lingeling")){
				// TODO: implement lingeling call
				
				} else {
					throw new IllegalArgumentException("Illegal argument: "+ tempComParam.getIdentifier());
				}

			}
			// if parameter identifier is for a query
			if (tempComParam.getIdentifier().equals("-query")) {

				StringListCommandParameter tmp = (StringListCommandParameter) tempComParam;
				queries = new PropositionalFormula[tmp.getValue().length];
				for(int i = 0; i < tmp.getValue().length; i++){
					try {
						queries[i] = (PropositionalFormula) parser.parseFormula(tmp.getValue()[i]);
					} catch (ParserException e) {
						
						e.printStackTrace();
					} catch (IOException e) {
						
						e.printStackTrace();
					}
				}
				
			}
		}

		for(PropositionalFormula pf : queries){
			System.out.println(reasoner.query(pf));
		}
		
		// TODO: handle output and return appropriate representation
		PluginOutput out = new PluginOutput();
		return out;
	}

}
