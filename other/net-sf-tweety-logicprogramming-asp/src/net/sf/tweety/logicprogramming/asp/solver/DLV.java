package net.sf.tweety.logicprogramming.asp.solver;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.Iterator;
import java.io.*;


import net.sf.tweety.logicprogramming.asp.syntax.*;

/**
 * wrapper for the dlv answer set solver.
 * 
 * @author Thomas Vengels
 *
 */
public class DLV {
	
	// path to dlv binary, binary inclusive
	protected String	path2dlv;
	
	// path to log error messages
	protected String	logpath;
	
	
	public DLV(String path) {
		path2dlv = path;
		logpath = "";
	}
	
	public void setLogPath(String path) {
		this.logpath = path;
	}
		
	/**
	 * This method computes answer sets to a given extended
	 * logical program. up to maxmodels (0 for no limitation)
	 * are returned.
	 *  
	 * @param program	extended logical program to process
	 * @param maxmodels	max. number of models (0 for no restriction)
	 * @return	a collection of answer sets
	 */
	public List<AnswerSet> computeModels(ELP program, int maxmodels) {
		
		SourceList sl = new SourceList();
		sl.add(program);
		return computeModels(sl,"",maxmodels );
	}
	
	public List<AnswerSet> computeModels(SourceList input, String options, int models) {
		ArrayList<AnswerSet> ret = new ArrayList<AnswerSet>();
		List<String> dlvout = null;
		
		// build command line
		String cmdln = path2dlv;		
		
		// add options
		cmdln += " "+options;
		
		// create dlv process
		dlvout = runDLVEx(cmdln, models, input);
		
		// check output and create answer sets
		Iterator<String> i = dlvout.iterator();
		while (i.hasNext()) {
			String s1 = i.next();
						
			// check if output is relevant 
			if (s1.startsWith("{") ) {
				AnswerSet as = new AnswerSet();
				this.parseModel(s1, as);
				ret.add(as);
			} else if (s1.startsWith("Best model: {")) {
				
				AnswerSet as = new AnswerSet();
				this.parseModel(s1,as);
				if (i.hasNext()) {	// should never fail..
					String s2 = i.next();
					//asw = parseWeight(s2);
				}
				ret.add( as );
			}
		}
		return ret;
	}
		
	
	/**
	 * main method used for running a answer set solver (external
	 * utility program) and parsing its output.
	 * 
	 * this is a modified version of the computeModels code from
	 * the dlv class found in KiMAS - Knowledge in Multi Agent Systems, 
	 * credits to I. Drobiazko for writing original code.
	 * 
	 * @param cmdln		command to run dlv
	 * @param maxAS		maximum number of answer sets
	 * @param prog		program to pass via stdin (-- option)
	 */
	protected List<String> runDLVEx(String cmdln, int maxAS, SourceList programs) {
		String text = "";
		StringBuffer errors = new StringBuffer();
		List<String> ret = new LinkedList<String>();
		
		if (maxAS > 0)
			cmdln += " -n="+maxAS;
		
		String files = "";
		int nFileSources = 0;
		// process ELP sources
		for (ELPSource s : programs) {
			if (s.getType() == ELPSource.SRC_FILE) {
				files += " "+s.getSrcFile();
				++nFileSources;
			}
		}
		
		if (nFileSources < programs.size())
			cmdln += " --";
		cmdln += files;
		
		boolean pipeInput = nFileSources < programs.size();
		
		// measure dlv time
		long time = System.currentTimeMillis();
		// time required to aquire cpu
		long waitTime = time;
				
		try {
			// setup call to dlv
			Process process = Runtime.getRuntime().exec(cmdln);
			
			BufferedReader inputStream = new BufferedReader(
					new InputStreamReader(process.getInputStream()));
			BufferedReader errorStream = new BufferedReader(
					new InputStreamReader(process.getErrorStream()));
			
			
			boolean useThread = false;
			
			BufferedWriter outputStream = null;
			if (pipeInput) {
				outputStream = new BufferedWriter(new OutputStreamWriter(process.getOutputStream()));
				for (ELPSource elps : programs) {
					if (elps.getType() != ELPSource.SRC_FILE)
						elps.addResource(outputStream);
				}
				outputStream.flush();
				outputStream.close();
				
			}
			
			if (!useThread) {
				// store result
				while ((text = inputStream.readLine()) != null) {
					ret.add(text);
				}
				
				// check for errors
				text = "";
				while ((text = errorStream.readLine()) != null) {
					errors.append(text);
				}
			} 
			
			inputStream.close();
			errorStream.close();
			
			process = null;
		} catch (IOException e) {
			//TODO: improve error handling from cowbots code
			/*
			System.err.println("dlv error!");
			System.err.println(text);
			System.err.println(e);
			String errFile=this.logpath + "dlv_ioerr_" + System.nanoTime() + ".txt";
			try {
			programs.saveAs(errFile);
			} catch (Exception e2) {
				
			}
			*/
		}				
		
		time = System.currentTimeMillis() - time;
		
		if(!"".equals(errors.toString())){
			//TODO: implement better error handling based
			/*
			String errMsg = "dlv error!\n";
			errMsg += errors + "\n";
			
			long errtime = System.nanoTime();
			String errFile = logpath + "dlverror_elp_"+errtime+".txt";
			
			errMsg += "error program saved as: " + errFile;
			System.out.println(errMsg);
			
			try {
				programs.saveAs(errFile);
				
				BufferedWriter bw = new BufferedWriter( new FileWriter( logpath+"dlverror_msg_"+errtime+".txt" ));
				bw.write(errMsg);
				bw.flush();
				bw.close();
				
			} catch (Exception e) {
				System.out.println("dlv error: could not save error log!\n"+
					e.toString());
			}
			*/
		}
				
		return ret;
	}
	
	/**
	 * simple parser function, extracts all literals from
	 * an answer set provided by dlv. if the empty answer
	 * set is found, an empty Set<ELPLiteral> is returned.
	 *  
	 * @param s			answer set from dlv
	 * @param result	collection to store parsed literals in
	 * 
	 */
	protected void parseModel(String s, Collection<ELPLiteral> result) {		
		// parse result
		Collection<ELPLiteral> ret = result;
		// buffer to hold symbols during literal parsing.
		// can hold atoms up to arity 99, first token
		// is always the predicate name.
		//String tokens[] = new String[100];
		List<String> tokens = new LinkedList<String>();
		//int	tokencount = 0;
		int tokenStart = 0;
		int tokenEnd = 0;
		
		// current position in string
		int cursor = 0;
		boolean done = false;
		
		// literal attributes
		boolean isneg = false;
		char c = ' ', lastc = ' ';

		// scan output
		while (!done) {

			// scan input
			lastc = c;
			c = s.charAt(cursor++);
			
			// skip open par
			if (c == '{') {
				tokenStart = cursor;
				continue;
			}					
			
			// is literal negated?
			if (c == '-') {
				isneg = true;
				tokenStart = cursor;
				continue;
			} 
			
			// comma separates literals
			if ((c == ',')) {
				
				// not parsing a term list, tokenStart is valid				
				tokenEnd = cursor-1;
				if (lastc==')')
					--tokenEnd;
				tokens.add( s.substring(tokenStart, tokenEnd).toString() );
								
				ELPAtom a = new ELPAtom(tokens);
				if (isneg)
					ret.add(new NegLiteral(a));
				else
					ret.add(a);
				
				// cursor is always at whitespace at
				// this point, compensate that
				++cursor;	// cursor now at next literal
				tokenStart = cursor;
				isneg = false;
				
				tokens.clear();					

				continue;
			}
			
			// found opening parenthesis of a term list
			if (c == '(') {
				
				tokenEnd = cursor-1;
				tokens.add( s.substring(tokenStart, tokenEnd) );
				
				tokenStart = cursor;
				// scan term list
				while (c != ')') {
					c = s.charAt(cursor++);
					if (c == ',') {
						tokenEnd = cursor-1;
						tokens.add( s.substring(tokenStart, tokenEnd) );						
						tokenStart = cursor;						
					}
				}
				
				continue;
			}
			
			
			// closing bracelet of answer set
			if (c == '}') {
				done = true;
				tokenEnd = cursor-1;
				
				if (lastc==')')
					--tokenEnd;				
				
				// catch last literal, and handle 
				// empty answer set correctly
				if (tokenEnd > tokenStart) {
					tokens.add( s.substring(tokenStart, tokenEnd) );
					
					ELPAtom a = new ELPAtom(tokens);
					if (isneg)
						ret.add(new NegLiteral(a));
					else
						ret.add(a);
				}
				
				tokens.clear();
			}									
		}		
	}	

	
	protected void parseWeight(String s) {
		
	}
}

