package net.sf.tweety.logics.rpcl.plugin;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import org.slf4j.LoggerFactory;
import org.slf4j.Logger;

import net.sf.tweety.commons.Answer;
import net.sf.tweety.commons.ParserException;
import net.sf.tweety.commons.TweetyLogging;
import net.sf.tweety.commons.Writer;
import net.sf.tweety.logics.fol.parser.FolParser;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.pcl.semantics.ProbabilityDistribution;
import net.sf.tweety.logics.rpcl.CondensedProbabilityDistribution;
import net.sf.tweety.logics.rpcl.RpclBeliefSet;
import net.sf.tweety.logics.rpcl.RpclMeReasoner;
import net.sf.tweety.logics.rpcl.parser.RpclParser;
import net.sf.tweety.logics.rpcl.parser.rpclcondensedprobabilitydistributionparser.RpclCondensedProbabilityDistributionParser;
import net.sf.tweety.logics.rpcl.parser.rpclprobabilitydistributionparser.RpclProbabilityDistributionParser;
import net.sf.tweety.logics.rpcl.semantics.AggregatingSemantics;
import net.sf.tweety.logics.rpcl.semantics.AveragingSemantics;
import net.sf.tweety.logics.rpcl.semantics.RpclSemantics;
import net.sf.tweety.logics.rpcl.writers.DefaultCondensedProbabilityDistributionWriter;
import net.sf.tweety.logics.rpcl.writers.DefaultProbabilityDistributionWriter;
import net.sf.tweety.math.probability.Probability;
import net.sf.tweety.plugin.AbstractTweetyPlugin;
import net.sf.tweety.plugin.PluginOutput;
import net.sf.tweety.plugin.parameter.CommandParameter;
import net.sf.tweety.plugin.parameter.FileListCommandParameter;
import net.sf.tweety.plugin.parameter.SelectionCommandParameter;
import net.sf.tweety.plugin.parameter.StringListCommandParameter;
import net.xeoh.plugins.base.annotations.PluginImplementation;

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
	
	/*
	 * Used Parameters:
	 * Fixed: Input-File
	 * Necessary: -rpclogic, -semantics, -inference
	 * Optional: -o (-output), -query (necessary?)
	 * to be checked:
	 * -inputreader, -outputwriter, -parser
	 * 
	 */
	/**
	 * Logger.
	 */
	private static Logger log = LoggerFactory.getLogger(RPCLPlugin.class);
	
	// necessary
	private static final String RPCLOGIC__CALL_PARAMETER = "rpclogic";

//	private static final String RPCLOGIC__CALL_DESCRIPTION = "";

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
 	private static Answer queryResult = null;
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
	 * 
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
	 * 
	 * @param args
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
		/*
		- parse knowledge base
		-> kb 
		- parse semantics
		- parse inference
		- parse query
		knowledge base: ".rpclkb"
		probability function ".rpclfct"
		extra output parameters? (P'function...) 
		*/
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
				} else {
					log.error("More or less than one plugin output file given.");
				}
			}
		}
		
		// </collect parameter>
		
		// logging
		TweetyLogging.initLogging();
		
		log.info("Start logging.");
		
		// set all collected parameters
		// parser
		parser = new RpclParser();
		// semantics
		RpclSemantics sem = null;
		if(semantics == 0){
			sem = new AveragingSemantics();
		} else if (semantics == 1){
			sem = new AggregatingSemantics();
		} else {
			log.error("Invalid semantics argument");
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
				
				RpclMeReasoner reasoner = new RpclMeReasoner(kb,sem, parser.getSignature(), inference);
				ProbabilityDistribution<?> p = reasoner.getMeDistribution();
				
				// write probability function into prob output file
				if(probOutFile != null){	
					probOutWriter.setObject(p);
					probOutWriter.writeToFile(probOutFile);
				}
				
				// if query is given
				if(query != null){
					
					pout.addField( "Query", query);
					queryResult = reasoner.query(folParser.parseFormula(query));
					pout.addField( "Query Result", queryResult.toString());
					
					// test output
					if(outputFile == null){
						System.out.println("query: " + query + queryResult);
					} else {
						log.info("Output written to file " + outputFile);
						// TODO: Write output to file
						probOutWriter.setObject(p);
						probOutWriter.writeToFile(outputFile);
						System.out.println("Write plugin output into file " + outputFile);
					}
				}else{
					log.info("No query given with knowledge base");
				}
				
				
			// input probability distribution file and standard inference	
			} else if(probParser instanceof RpclProbabilityDistributionParser){
				// init standard parser for prob input file
				((RpclProbabilityDistributionParser) probParser).setSemantics(sem);
				((RpclProbabilityDistributionParser) probParser).setSignature(parser.getSignature());
				
//				if(probInputFiles.length == 1){
					ProbabilityDistribution<?> p = ((RpclProbabilityDistributionParser) probParser).parseProbabilityDistribution(new InputStreamReader(new java.io.FileInputStream(probInputFiles[0])));
//				} else log.error("Invalid probability input files");
				
				if (query != null){
					pout.addField("Query", query);
					Probability res = p.probability(folParser.parseFormula(query));
					pout.addField( "Probability (Standard)", res.toString());
					// TODO: res and p to file?
				} else {
					log.info("No query given with standard probability distribution");
				}
					
				if(probOutFile != null){	
					probOutWriter.setObject(p);
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
				}else {
					log.info("No query given with lifted probability distribution");
				}
				
				if(probOutFile != null){	
					probOutWriter.setObject(p);
					probOutWriter.writeToFile(probOutFile);
				}
						
			}
			
		} catch (FileNotFoundException e) {
			log.error(e.getMessage());
			
		} catch (ParserException e) {
			
			log.error(e.getMessage());
		} catch (IOException e) {
			
			log.error(e.getMessage());
		}
		
		// returns plugin output
		pout.mergeFields();
		System.out.println(pout.getOutput());
		return pout;
	}
}