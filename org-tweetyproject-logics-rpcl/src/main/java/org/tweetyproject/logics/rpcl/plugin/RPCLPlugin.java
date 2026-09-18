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
 * Tweety CLI plugin for relational probabilistic conditional logic.
 *
 * @author Bastian Wolf
 *
 */
@SuppressWarnings("unused")
@PluginImplementation
public class RPCLPlugin extends AbstractTweetyPlugin {


		/** Command-line identifier for the RPCL plugin. */
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

		/** Command-line identifier for selecting a probability parser. */
		private static final String RPCLOGIC__PARSER_IDENTIFIER = "-parser";

		/** Description of the parser selection parameter. */
		private static final String RPCLOGIC__PARSER_DESCRIPTION = "-parser <parser>, parser used to parse input files";

		/** Supported probability parser identifiers. */
		private static final String[] RPCLOGIC__PARSER_ENUM = {"rpclmeProb","rpclmeCondProb"};


		/** Command-line identifier for selecting RPCL semantics. */
		private static final String RPCLOGIC__SEMANTICS_IDENTIFIER = "-semantics";

//	private static final String RPCLOGIC__SEMANTICS_IDENTIFIER_SHORT = "-sem";

		/** Description of the semantics selection parameter. */
		private static final String RPCLOGIC__SEMANTICS_DESCRIPTION = "";

		/** Supported RPCL semantics identifiers. */
		private static final String[] RPCLOGIC__SEMANTICS_ENUM = {"averaging","aggregating"};


		/** Command-line identifier for selecting an inference method. */
		private static final String RPCLOGIC__INFERENCE_IDENTIFIER = "-inference";

//	private static final String RPCLOGIC__INFERENCE_IDENTIFIER_SHORT = "-inf";

		/** Description of the inference selection parameter. */
		private static final String RPCLOGIC__INFERENCE_DESCRIPTION = "";

		/** Supported inference method identifiers. */
		private static final String[] RPCLOGIC__INFERENCE_ENUM = {"standard", "lifted"};


	// optional
	// output
		/** Command-line identifier for the general output file. */
		private static final String RPCLOGIC__OUTPUT_IDENTIFIER = "-o";

		/** Description of the general output parameter. */
		private static final String RPCLOGIC__OUTPUT_DESCRIPTION = "";

		/** Storage for the general output parameter value. */
		private static final String[] RPCLOGIC__OUTPUT_VALUE = new String[1];

	// query
		/** Command-line identifier for the query parameter. */
		private static final String RPCLOGIC__QUERY_IDENTIFIER = "-query";

		/** Description of the query parameter. */
		private static final String RPCLOGIC__QUERY_DESCRIPTION = "";

		/** Storage for the query parameter value. */
		private static final String[] RPCLOGIC__QUERY_VALUE = new String[1];

	// input probability files
		/** Command-line identifier for probability distribution input files. */
		private static final String RPCLOGIC__INPROB_IDENTIFIER = "-ip";

		/** Description of the probability input parameter. */
		private static final String RPCLOGIC__INPROB_DESCRIPTION = "";

		/** Storage for probability distribution input files. */
		private static final File[] RPCLOGIC__INPROB_FILES = new File[1];

	// probability output file
		/** Command-line identifier for the probability distribution output file. */
		private static final String RPCLOGIC__PROBOUT_IDENTIFIER = "-po";

		/** Description of the probability output parameter. */
		private static final String RPCLOGIC__PROBOUT_DESCRIPTION = "";

		/** Storage for the probability distribution output file. */
		private static final File[] RPCLOGIC__PROBOUT_FILES = new File[1];

	// static parameter
		/** Paths of the probability distribution input files. */
		private static String[] probInputFiles = null;
		/** Parser used for the RPCL belief set. */
		private static RpclParser parser = null;
		/** Parser used for the probability distribution. */
		private static Object probParser = null;
		/** Path of the probability distribution output file. */
		private static String probOutFile = null;
		/** Writer used for the probability distribution output. */
		private static Writer probOutWriter = null;
		/** Indicates whether plugin output is written to a file. */
		private static boolean writeToFile = false;
		/** Path of the general plugin output file. */
		private static String outputFile = null;
		/** Query formula supplied to the plugin. */
		private static String query = null;
		/** Probability calculated for the query. */
		private static Double queryResult = null;
		/** Index of the selected RPCL semantics. */
		private static int semantics = -1;
		/** Index of the selected inference method. */
		private static int inference = -1;
		/** Indicates whether lifted inference is selected. */
		private static boolean lifted = false;
		/** Parser used to parse first-order queries. */
		private static FolParser folParser = new FolParser();

	/**
	 * Returns the command identifier used to invoke this plugin.
	 *
	 * @return the plugin command
	 */
	@Override
	public String getCommand() {
		return RPCLOGIC__CALL_PARAMETER;
	}

	/**
	 * Creates a new RPCL plugin.
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
	 * Creates a new RPCL plugin instance from command-line arguments.
	 *
	 * @param args command-line arguments
	 */
	public RPCLPlugin(String[] args) {
		this();
	}

	/**
	 * Executes this plugin with the given input files and parameters.
	 *
	 * @param input files to be parsed; the first entry is treated as the knowledge base
	 * @param params additional parameters such as query, parser, or semantics settings
	 * @return the output calculated from the input files and parameters
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
