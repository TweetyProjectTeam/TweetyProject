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
package org.tweetyproject.logics.rpcl.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;


import org.tweetyproject.commons.ParserException;
import org.tweetyproject.commons.Writer;
import org.tweetyproject.logics.fol.parser.FolParser;
import org.tweetyproject.logics.fol.semantics.HerbrandInterpretation;
import org.tweetyproject.logics.fol.syntax.FolFormula;
import org.tweetyproject.logics.pcl.semantics.ProbabilityDistribution;
import org.tweetyproject.logics.rpcl.parser.RpclParser;
import org.tweetyproject.logics.rpcl.parser.rpclcondensedprobabilitydistributionparser.RpclCondensedProbabilityDistributionParser;
import org.tweetyproject.logics.rpcl.parser.rpclprobabilitydistributionparser.RpclProbabilityDistributionParser;
import org.tweetyproject.logics.rpcl.reasoner.RpclMeReasoner;
import org.tweetyproject.logics.rpcl.semantics.AggregatingSemantics;
import org.tweetyproject.logics.rpcl.semantics.AveragingSemantics;
import org.tweetyproject.logics.rpcl.semantics.CondensedProbabilityDistribution;
import org.tweetyproject.logics.rpcl.semantics.RpclProbabilityDistribution;
import org.tweetyproject.logics.rpcl.semantics.RpclSemantics;
import org.tweetyproject.logics.rpcl.syntax.RpclBeliefSet;
import org.tweetyproject.logics.rpcl.writers.DefaultCondensedProbabilityDistributionWriter;
import org.tweetyproject.logics.rpcl.writers.DefaultProbabilityDistributionWriter;
import org.tweetyproject.math.probability.Probability;
import org.tweetyproject.plugin.AbstractTweetyPlugin;
import org.tweetyproject.plugin.PluginOutput;
import org.tweetyproject.plugin.parameter.CommandParameter;
import org.tweetyproject.plugin.parameter.FileListCommandParameter;
import org.tweetyproject.plugin.parameter.SelectionCommandParameter;
import org.tweetyproject.plugin.parameter.StringListCommandParameter;
import net.xeoh.plugins.base.annotations.Capabilities;
import net.xeoh.plugins.base.annotations.PluginImplementation;
import net.xeoh.plugins.base.options.getplugin.OptionCapabilities;

/**
 * relational probabilistic conditional logic plugin for the tweety cli
 * skeleton
 *
 * @author Bastian Wolf
 *
 */
@SuppressWarnings("unused")
@PluginImplementation
public class RPCLPlugin extends AbstractTweetyPlugin {


	// necessary
	private static final String RPCLOGIC__CALL_PARAMETER = "rpcl";

//	private static final String RPCLOGIC__CALL_DESCRIPTION = "";

/**
 *
 * Return capabilities
 * @return capabilities
 */
	@Capabilities
	public String[] capabilities() { return new String[] {"TweetyProject Plugin", RPCLOGIC__CALL_PARAMETER}; }


	// option capabilities
//	private static final OptionCapabilities RPCLOGIC__OPTIONS = new OptionCapabilities("TweetyProject Plugin", RPCLOGIC__CALL_PARAMETER);

	private static final String RPCLOGIC__PARSER_IDENTIFIER = "-parser";

	private static final String RPCLOGIC__PARSER_DESCRIPTION = "-parser <parser>, parser used to parse input files";

	private static final String[] RPCLOGIC__PARSER_ENUM = {"rpclmeProb","rpclmeCondProb"};


	private static final String RPCLOGIC__SEMANTICS_IDENTIFIER = "-semantics";

//	private static final String RPCLOGIC__SEMANTICS_IDENTIFIER_SHORT = "-sem";

	private static final String RPCLOGIC__SEMANTICS_DESCRIPTION = "";

	private static final String[] RPCLOGIC__SEMANTICS_ENUM = {"averaging","aggregating"};


	private static final String RPCLOGIC__INFERENCE_IDENTIFIER = "-inference";

//	private static final String RPCLOGIC__INFERENCE_IDENTIFIER_SHORT = "-inf";

	private static final String RPCLOGIC__INFERENCE_DESCRIPTION = "";

	private static final String[] RPCLOGIC__INFERENCE_ENUM = {"standard", "lifted"};


	// optional
	// output
	private static final String RPCLOGIC__OUTPUT_IDENTIFIER = "-o";

	private static final String RPCLOGIC__OUTPUT_DESCRIPTION = "";

	private static final String[] RPCLOGIC__OUTPUT_VALUE = new String[1];

	// query
	private static final String RPCLOGIC__QUERY_IDENTIFIER = "-query";

	private static final String RPCLOGIC__QUERY_DESCRIPTION = "";

	private static final String[] RPCLOGIC__QUERY_VALUE = new String[1];

	// input probability files
	private static final String RPCLOGIC__INPROB_IDENTIFIER = "-ip";

	private static final String RPCLOGIC__INPROB_DESCRIPTION = "";

	private static final File[] RPCLOGIC__INPROB_FILES = new File[1];

	// probability output file
	private static final String RPCLOGIC__PROBOUT_IDENTIFIER = "-po";

	private static final String RPCLOGIC__PROBOUT_DESCRIPTION = "";

	private static final File[] RPCLOGIC__PROBOUT_FILES = new File[1];

	// static parameter
	// probability input files
	private static String[] probInputFiles = null;
	// all available parsers
 	private static RpclParser parser = null;
	// the used probability parser
 	private static Object probParser = null;
 	// probability output file
 	private static String probOutFile = null;
 	// probability output writer
 	private static Writer probOutWriter = null;
 	// write output to file?
 	private static boolean writeToFile = false;
 	// plugin output file
 	private static String outputFile = null;
 	// query string
 	private static String query = null;
 	// query result
 	private static Double queryResult = null;
 	// semantics
 	private static int semantics = -1;
 	// inference
 	private static int inference = -1;
 	// lifted?
 	private static boolean lifted = false;
 	// folparser
 	private static FolParser folParser = new FolParser();

	/**
	 *
	 */
	@Override
	public String getCommand() {
		return RPCLOGIC__CALL_PARAMETER;
	}

	/**
	 *Constructor
	 */
	public RPCLPlugin() {
		super();
		this.addParameter(new SelectionCommandParameter(RPCLOGIC__PARSER_IDENTIFIER, RPCLOGIC__PARSER_DESCRIPTION, RPCLOGIC__PARSER_ENUM));
		this.addParameter(new SelectionCommandParameter(RPCLOGIC__SEMANTICS_IDENTIFIER, RPCLOGIC__SEMANTICS_DESCRIPTION, RPCLOGIC__SEMANTICS_ENUM));
		this.addParameter(new SelectionCommandParameter(RPCLOGIC__INFERENCE_IDENTIFIER, RPCLOGIC__INFERENCE_DESCRIPTION, RPCLOGIC__INFERENCE_ENUM));
		this.addParameter(new FileListCommandParameter(RPCLOGIC__OUTPUT_IDENTIFIER, RPCLOGIC__OUTPUT_DESCRIPTION));

		this.addParameter(new StringListCommandParameter(RPCLOGIC__QUERY_IDENTIFIER, RPCLOGIC__QUERY_DESCRIPTION));
		this.addParameter(new FileListCommandParameter(RPCLOGIC__INPROB_IDENTIFIER, RPCLOGIC__INPROB_DESCRIPTION));
		this.addParameter(new FileListCommandParameter(RPCLOGIC__PROBOUT_IDENTIFIER, RPCLOGIC__PROBOUT_DESCRIPTION));
	}

	/**
	 * Constructs a new plugin
	 * @param args arguments
	 */
	public RPCLPlugin(String[] args) {
		this();
	}

	/**
	 * Executes this plugin with given input files and other aggregated parameters
	 * @param input files to be parsed (e.g. knowledge base). Input is assumed to contain only one file
	 * at the first position (input[0]).
	 * @param params other parameter like queries, parser or reasoner
	 * @return the output calculated from input files and arguments
	 */
	@Override
	public PluginOutput execute(File[] input, CommandParameter[] params) {

		// init

		// <collect parameter>

		// semantics
		for(CommandParameter tmpComParam : params){
			if(tmpComParam.getIdentifier().equalsIgnoreCase(RPCLOGIC__SEMANTICS_IDENTIFIER)){
				SelectionCommandParameter tmp = (SelectionCommandParameter) tmpComParam;
				if(tmp.getValue().equalsIgnoreCase(RPCLOGIC__SEMANTICS_ENUM[0])){
					semantics = 0;
				} else if(tmp.getValue().equalsIgnoreCase(RPCLOGIC__SEMANTICS_ENUM[1])){
					semantics = 1;
				}
			}
		}

		// inference
		for(CommandParameter tmpComParam : params){
			if(tmpComParam.getIdentifier().equalsIgnoreCase(RPCLOGIC__INFERENCE_IDENTIFIER)){
				SelectionCommandParameter tmp = (SelectionCommandParameter) tmpComParam;
				if(tmp.getValue().equalsIgnoreCase(RPCLOGIC__INFERENCE_ENUM[0])){
					inference = RpclMeReasoner.STANDARD_INFERENCE;
					lifted = false;
					probOutWriter = new DefaultProbabilityDistributionWriter();
				} else if(tmp.getValue().equalsIgnoreCase(RPCLOGIC__INFERENCE_ENUM[1])){
					inference = RpclMeReasoner.LIFTED_INFERENCE;
					lifted = true;
					probOutWriter = new DefaultCondensedProbabilityDistributionWriter();
				}
			}
		}

		// parser
		for(CommandParameter tmpComParam : params){
			if(tmpComParam.getIdentifier().equalsIgnoreCase(RPCLOGIC__PARSER_IDENTIFIER)){
				SelectionCommandParameter tmp = (SelectionCommandParameter) tmpComParam;
				if(tmp.getValue().equalsIgnoreCase(RPCLOGIC__PARSER_ENUM[0])){
					probParser = new RpclProbabilityDistributionParser();
				} else if(tmp.getValue().equalsIgnoreCase(RPCLOGIC__PARSER_ENUM[1])){
					probParser = new RpclCondensedProbabilityDistributionParser();
				}
			}
		}

		// input probability file
		for(CommandParameter tmpComParam : params){
			if(tmpComParam.getIdentifier().equalsIgnoreCase(RPCLOGIC__INPROB_IDENTIFIER)){
				FileListCommandParameter tmp = (FileListCommandParameter) tmpComParam;
				if(tmp.getValue().length == 1){
					probInputFiles = new String[1];
					probInputFiles[0] = tmp.getValue()[0].getAbsolutePath();
				} else {
					System.err.println("Invalid input probability file amount (only one file allowed).");
				}
			}
		}

		// query
		for(CommandParameter tmpComParam : params){
			if(tmpComParam.getIdentifier().equalsIgnoreCase(RPCLOGIC__QUERY_IDENTIFIER)){
				StringListCommandParameter tmp = (StringListCommandParameter) tmpComParam;
				if(tmp.getValue().length == 1){
					query = new String(tmp.getValue()[0]);
				} else {
					System.err.println("Currently only one query per call allowed.");
					System.exit(1);
				}
			}
		}

		// probability output file
		for(CommandParameter tmpComParam : params){
			if(tmpComParam.getIdentifier().equalsIgnoreCase(RPCLOGIC__PROBOUT_IDENTIFIER)){
				FileListCommandParameter tmp = (FileListCommandParameter) tmpComParam;

				if(tmp.getValue().length == 1){
					probOutFile = tmp.getValue()[0].getAbsolutePath();
				}
			}
		}

		// write to file with given filename
		for(CommandParameter tmpComParam : params){
			if(tmpComParam.getIdentifier().equalsIgnoreCase(RPCLOGIC__OUTPUT_IDENTIFIER)){
				FileListCommandParameter tmp = (FileListCommandParameter) tmpComParam;
				if(tmp.getValue().length == 1){
					outputFile = tmp.getValue()[0].getAbsolutePath();
				}
			}
		}

		// </collect parameter>



		// set all collected parameters
		// parser
		parser = new RpclParser();
		// semantics
		RpclSemantics sem = null;
		if(semantics == 0){
			sem = new AveragingSemantics();
		} else if (semantics == 1){
			sem = new AggregatingSemantics();
		}
		if(query != null){
			folParser.setSignature(parser.getSignature());
		}
		PluginOutput pout = new PluginOutput();


		// parse files, apply queries
		try {
			RpclBeliefSet kb = (RpclBeliefSet) parser.parseBeliefBaseFromFile(input[0].getAbsolutePath());
			pout.addField("Knowledge Base", kb.toString() );

			// no input probability distribution file
			if(probInputFiles == null){

				RpclMeReasoner reasoner = new RpclMeReasoner(sem, inference);
				RpclProbabilityDistribution<?> p = reasoner.getModel(kb,parser.getSignature());

				// write probability function into prob output file
				if(probOutFile != null){
					probOutWriter.setObjectToBePrinted(p);
					probOutWriter.writeToFile(probOutFile);
				}

				// if query is given
				if(query != null){

					pout.addField( "Query", query);
					queryResult = reasoner.query(kb,(FolFormula) folParser.parseFormula(query));
					pout.addField( "Query Result", queryResult.toString());

					// test output
					if(outputFile == null){
						System.out.println("query: " + query + queryResult);
					} else {

						// TODO: Write output to file
						probOutWriter.setObjectToBePrinted(p);
						probOutWriter.writeToFile(outputFile);
						System.out.println("Write plugin output into file " + outputFile);
					}
				}


			// input probability distribution file and standard inference
			} else if(probParser instanceof RpclProbabilityDistributionParser){
				// init standard parser for prob input file
				((RpclProbabilityDistributionParser) probParser).setSemantics(sem);
				((RpclProbabilityDistributionParser) probParser).setSignature(parser.getSignature());

//				if(probInputFiles.length == 1){
					RpclProbabilityDistribution<?> p = ((RpclProbabilityDistributionParser) probParser).parseProbabilityDistribution(new InputStreamReader(new java.io.FileInputStream(probInputFiles[0])));
//				} else log.error("Invalid probability input files");

				if (query != null){
					pout.addField("Query", query);
					Probability res = p.probability((FolFormula) folParser.parseFormula(query));
					pout.addField( "Probability (Standard)", res.toString());
					// TODO: res and p to file?
				}

				if(probOutFile != null){
					probOutWriter.setObjectToBePrinted(p);
					probOutWriter.writeToFile(probOutFile);
				}

				// input probability distribution file and lifted inference
			} else if (probParser instanceof RpclCondensedProbabilityDistributionParser){
				// init condensed parser for prob input file (lifted inference)
				((RpclCondensedProbabilityDistributionParser) probParser).setSemantics(sem);
				((RpclCondensedProbabilityDistributionParser) probParser).setSignature(parser.getSignature());

//				if(probInputFiles.length == 1){
				CondensedProbabilityDistribution p = ((RpclCondensedProbabilityDistributionParser) probParser).parseCondensedProbabilityDistribution(new InputStreamReader(new FileInputStream(probInputFiles[0])));

				if (query != null){
					pout.addField( "Query:", query);
					Probability res = p.probability((FolFormula)folParser.parseFormula(query));
					pout.addField( "Probability (Condensed):", res.toString());
				}

				if(probOutFile != null){
					probOutWriter.setObjectToBePrinted(p);
					probOutWriter.writeToFile(probOutFile);
				}

			}

		} catch (FileNotFoundException e) {


		} catch (ParserException e) {

		} catch (IOException e) {


		}

		// returns plugin output
		pout.mergeFields();
		System.out.println(pout.getOutput());
		return pout;
	}
}