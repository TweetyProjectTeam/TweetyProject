package net.sf.tweety.math.opt.solver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import net.sf.tweety.math.GeneralMathException;
import net.sf.tweety.math.opt.OptimizationProblem;
import net.sf.tweety.math.term.Term;
import net.sf.tweety.math.term.Variable;

/**
 * Uses a web service implementation of the OpenOpt-library.
 * 
 * @author Matthias Thimm
 */
public class OpenOptWebSolver extends OpenOptSolver {

	// TODO: better configuration for the two parameters
	/** The URL of the web service. */
	public static String openopt_webservice_url = null;
	/** The API key for using the service. */
	public static String openopt_webservice_apikey = null;
	
	/**
	 * Logger.
	 */
	private Logger log = LoggerFactory.getLogger(OpenOptWebSolver.class);
	
	/**
	 * Creates a new solver for the given problem.
	 * @param problem a csp.
	 */
	public OpenOptWebSolver(OptimizationProblem problem) {
		this(problem,null);
	}
	
	/**
	 * Creates a new solver for the given problem.
	 * @param problem a optimization problem.
	 * @param startingPoint a starting point.
	 */
	public OpenOptWebSolver(OptimizationProblem problem, Map<Variable,Term> startingPoint) {
		super(problem);
	}
		
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.opt.solver.OpenOptSolver#solve()
	 */
	@Override
	public Map<Variable, Term> solve() throws GeneralMathException {
		// check for service parameters
		if(OpenOptWebSolver.openopt_webservice_apikey == null || OpenOptWebSolver.openopt_webservice_url == null)
			throw new RuntimeException("OpenOpt web service not configured, you have to supply an url and an API key.");
		String output = "";
		try{
			// call web service to solve problem
			this.log.info("Calling OpenOpt web service.");
			// prepare parameters for service
		    String data = URLEncoder.encode("apikey", "UTF-8") + "=" + URLEncoder.encode(OpenOptWebSolver.openopt_webservice_apikey, "UTF-8");
		    data += "&" + URLEncoder.encode("script", "UTF-8") + "=" + URLEncoder.encode(this.getOpenOptCode(), "UTF-8");
	        // send data
		    URL url = new URL(OpenOptWebSolver.openopt_webservice_url);
		    URLConnection conn = url.openConnection();
		    conn.setDoOutput(true);
		    OutputStreamWriter wr = new OutputStreamWriter(conn.getOutputStream());
		    wr.write(data);
		    wr.flush();
		    // Parse response 
		    BufferedReader rd = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		    // omit the last line, that one is duplicated for whatever reason
		    String line, previousLine = null;
		    while ((line = rd.readLine()) != null) {
		    	if(previousLine != null)
		    		output += previousLine + "\n";
		    	previousLine = line;
		    }
		    wr.close();
		    rd.close();
		}catch(IOException e){
			throw new RuntimeException("Something wrong with the OpenOpt web service at " + OpenOptWebSolver.openopt_webservice_url);
		}			
		// TODO check error appropriately
		if(output.contains("NO FEASIBLE SOLUTION") && !this.ignoreNotFeasibleError){
			this.log.info("The optimization problem seems to be unfeasible.");
			throw new GeneralMathException("The optimization problem seems to be unfeasible.");
		}
		// parser output
		this.log.info("Parsing solution from OpenOpt.");
		try{
			return this.parseOutput(output);
		}catch(Exception e){
			this.log.error(e.getMessage());
			throw new GeneralMathException(e.getMessage());
		}
	}
}
