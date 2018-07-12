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
import net.sf.tweety.math.opt.ConstraintSatisfactionProblem;
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
	/** Whether to use a local installation of OpenOpt. */
	public static boolean openopt_use_local = false;
	
	/**
	 * Logger.
	 */
	private Logger log = LoggerFactory.getLogger(OpenOptWebSolver.class);

	/* (non-Javadoc)
	 * @see net.sf.tweety.math.opt.solver.OpenOptSolver#solve()
	 */
	@Override
	public Map<Variable, Term> solve(ConstraintSatisfactionProblem problem) throws GeneralMathException {
		// use local installation?
		if(OpenOptWebSolver.openopt_use_local){
			OpenOptSolver solver = new OpenOptSolver();
			return solver.solve(problem);
		}
		// check for service parameters		
		if(OpenOptWebSolver.openopt_webservice_apikey == null || OpenOptWebSolver.openopt_webservice_url == null)
			throw new RuntimeException("OpenOpt web service not configured, you have to supply an url and an API key.");
		String output = "";
		try{
			// call web service to solve problem
			this.log.info("Calling OpenOpt web service.");
			// prepare parameters for service
		    String data = URLEncoder.encode("apikey", "UTF-8") + "=" + URLEncoder.encode(OpenOptWebSolver.openopt_webservice_apikey, "UTF-8");
		    data += "&" + URLEncoder.encode("script", "UTF-8") + "=" + URLEncoder.encode(this.getOpenOptCode((OptimizationProblem)problem), "UTF-8");
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
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.math.opt.Solver#isInstalled()
	 */
	public static boolean isInstalled() throws UnsupportedOperationException{
		return OpenOptWebSolver.openopt_webservice_apikey != null && OpenOptWebSolver.openopt_webservice_url != null;
	}
}
