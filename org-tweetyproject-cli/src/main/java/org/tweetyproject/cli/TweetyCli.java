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
package org.tweetyproject.cli;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.tweetyproject.commons.ParserException;
import org.tweetyproject.commons.Writer;
import org.tweetyproject.logics.fol.parser.FolParser;
import org.tweetyproject.logics.fol.syntax.FolFormula;
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
import org.tweetyproject.math.opt.ProblemInconsistentException;
import org.tweetyproject.math.probability.Probability;


/**
 * This class implements a simple command line interface for accessing
 * the functionalities provided by the TweetyProject libraries.
 * 
 * @author Matthias Thimm
 */
public class TweetyCli {

	
	/** The argument name for the input file(s) */
	public static final String ARG__INPUT_FILES = "--input";
	/** The argument name for the input file(s) (short) */
	public static final String ARG__INPUT_FILES_SHORT = "-i";
	/** The argument name for the parser(s) used for reading the input file(s) */
	public static final String ARG__INPUT_PARSER = "--parser";
	/** The argument name for the parser(s) used for reading the input file(s) (short) */
	public static final String ARG__INPUT_PARSER_SHORT = "-p";
	/** The argument name for the output file */
	public static final String ARG__OUTPUT_FILE = "--output";
	/** The argument name for the output file (short) */
	public static final String ARG__OUTPUT_FILE_SHORT = "-o";
	/** The argument name for the writer used for writing the output file. */
	public static final String ARG__OUTPUT_WRITER = "--writer";
	/** The argument name for the writer used for writing the output file (short) */
	public static final String ARG__OUTPUT_WRITER_SHORT = "-w";
	/** The argument name for the log level (The possible values are described
	 *  by <code>TweetyConfiguration.LogLevel</code>, default
	 *  is <code>TweetyConfiguration.LogLevel.INFO</code>) */
	public static final String ARG__LOG_LEVEL = "--log";
	/** The argument name for the log level (The possible values are described
	 *  by <code>TweetyConfiguration.LogLevel</code>, default
	 *  is <code>TweetyConfiguration.LogLevel.INFO</code>) (short) */
	public static final String ARG__LOG_LEVEL_SHORT = "-l";
	/** The argument name for the file used for logging (if this parameter is not set,
	 *  logging is performed on the standard output) */
	public static final String ARG__LOG_FILE = "--logfile";
	/** The argument name for the file used for logging (if this parameter is not set,
	 *  logging is performed on the standard output) (short) */
	public static final String ARG__LOG_FILE_SHORT = "-lf";
	/** The argument name for advanced options */
	public static final String ARG__OPTIONS = "--options";
	/** The argument name for advanced options (short) */
	public static final String ARG__OPTIONS_SHORT = "-op";
	/** The argument name for a query */
	public static final String ARG__QUERY = "--query";
	/** The argument name for a query */
	public static final String ARG__QUERY_SHORT = "-q";
	
	
	/** The input file */
	private static String[] inputFiles = new String[1];
	/** The parser used for reading the input file */
	private static Object[] inputParser = new Object[1];
	/** The output file */
	private static String outputFile = null;
	/** The writer used for writing the output file. */
	private static Writer outputWriter = null;
	/** Advanced options */
	private static String options = null;
	/** The query */
	private static String query = null;
	

// TODO: Plugin fuer RPCL erstellen mit dieser Funktionalitaet, danach hier streichen:	
	/**
	 * Program entry.<br>
	 * <br>
	 * Current program call:<br>
	 * - java -jar TweetyCLI.jar --input RPCLKBFILE --parser rpclme --output RPCLPROBFILE --writer X
	 * 		--options [rpcl.semantics=Y,rpcl.inference=Z]<br>	 * 
	 * - java -jar TweetyCLI.jar --input RPCLKBFILE RPCLPROBFILE --parser rpclme X --query=SOMEQUERY
	 * 		--options [rpcl.semantics=Y,rpcl.inference=Z] 
	 * 		
	 * with X\in{rpclmeProb, rpclmeCondProb}, Y\in {averaging,aggregating}, Z\in{standard,lifted}<br>
	 * @param args command line arguments.
	 */
	public static void main(String[] args){
		// TODO the following has to be generalized
		// (at the moment this cli just supports reasoning with RPCL)
		
		// read arguments		
		for(int i = 0; i < args.length; i++){
			if(args[i].equals(ARG__INPUT_FILES) || args[i].equals(ARG__INPUT_FILES_SHORT)){
				List<String> files = new ArrayList<String>();
				while(!args[i+1].startsWith("-"))
					files.add(args[++i]);
				inputFiles = files.toArray(inputFiles);
			}else if(args[i].equals(ARG__INPUT_PARSER) || args[i].equals(ARG__INPUT_PARSER_SHORT)){
				//TODO generalize the following
				List<Object> parser = new ArrayList<Object>();
				while(!args[i+1].startsWith("-")){
					i++;
					if(args[i].equals("rpclme"))
						parser.add(new RpclParser());
					else if(args[i].equals("rpclmeProb"))
						parser.add(new RpclProbabilityDistributionParser());
					else if(args[i].equals("rpclmeCondProb"))
						parser.add(new RpclCondensedProbabilityDistributionParser());
					else{					
						System.err.println("At the moment TweetyCLI only supports reasoning with RPCL.");
						System.exit(1);
					}					
				}
				inputParser = parser.toArray(inputParser);
			}else if(args[i].equals(ARG__OUTPUT_FILE) || args[i].equals(ARG__OUTPUT_FILE_SHORT))
				outputFile = args[++i];
			else if(args[i].equals(ARG__OUTPUT_WRITER) || args[i].equals(ARG__OUTPUT_WRITER)){
				//TODO generalize the following
				i++;
				if(args[i].equals("rpclmeProb")){
					outputWriter = new DefaultProbabilityDistributionWriter();
				}else if(args[i].equals("rpclmeCondProb"))
					outputWriter = new DefaultCondensedProbabilityDistributionWriter();
				else{					
					System.err.println("At the moment TweetyCLI only supports reasoning with RPCL.");
					System.exit(1);
				}
			}
			else if(args[i].equals(ARG__OPTIONS) || args[i].equals(ARG__OPTIONS_SHORT))
				options = args[++i];
			if(args[i].equals(ARG__QUERY) || args[i].equals(ARG__QUERY_SHORT))
				query = args[++i];
		}
		
		
		// parse options
		// TODO generalize this (at the moment, only "rpcl.semantics={averaging,aggregating}"
		// and "rpcl.inference={standard,lifted} are valid options)
		RpclSemantics semantics = null;
		if(options.toLowerCase().indexOf("averaging") != -1)
			semantics = new AveragingSemantics();
		else semantics = new AggregatingSemantics();
		int inferenceType = RpclMeReasoner.STANDARD_INFERENCE;
		if(options.toLowerCase().indexOf("lifted") != -1)
			inferenceType = RpclMeReasoner.LIFTED_INFERENCE;		
				
		// perform inference
		try{
			RpclBeliefSet kb = (RpclBeliefSet)((RpclParser) inputParser[0]).parseBeliefBaseFromFile(inputFiles[0]);
			if(inputFiles.length == 1){				
				RpclMeReasoner reasoner = new RpclMeReasoner(semantics,inferenceType);
				RpclProbabilityDistribution<?> p = reasoner.getModel(kb,((RpclParser) inputParser[0]).getSignature());
				outputWriter.setObjectToBePrinted(p);
				outputWriter.writeToFile(outputFile);
				System.exit(0);
			}else if(inputParser[1] instanceof RpclProbabilityDistributionParser) {
				((RpclProbabilityDistributionParser)inputParser[1]).setSemantics(semantics);
				((RpclProbabilityDistributionParser)inputParser[1]).setSignature(((RpclParser) inputParser[0]).getSignature());
				RpclProbabilityDistribution<?> p = ((RpclProbabilityDistributionParser)inputParser[1]).parseProbabilityDistribution(new InputStreamReader(new java.io.FileInputStream(inputFiles[1])));
				FolParser folParser = new FolParser();
				folParser.setSignature(((RpclParser) inputParser[0]).getSignature());
				Probability result = p.probability((FolFormula) folParser.parseFormula(query));
				System.out.println(result.getValue());
				System.exit(0);
			}else if(inputParser[1] instanceof RpclCondensedProbabilityDistributionParser) {
				((RpclCondensedProbabilityDistributionParser)inputParser[1]).setSemantics(semantics);
				((RpclCondensedProbabilityDistributionParser)inputParser[1]).setSignature(((RpclParser) inputParser[0]).getSignature());
				CondensedProbabilityDistribution p = ((RpclCondensedProbabilityDistributionParser)inputParser[1]).parseCondensedProbabilityDistribution(new InputStreamReader(new java.io.FileInputStream(inputFiles[1])));
				FolParser folParser = new FolParser();
				folParser.setSignature(((RpclParser) inputParser[0]).getSignature());
				Probability result = p.probability((FolFormula)folParser.parseFormula(query));
				System.out.println(result.getValue());
				System.exit(0);
			}else {}
		} catch (FileNotFoundException e) {
			
		} catch (ParserException e) {

		} catch (IOException e) {

		} catch (ProblemInconsistentException e){

		}

		System.err.println("Error: see log for details.");
		System.exit(1);
	}
}
