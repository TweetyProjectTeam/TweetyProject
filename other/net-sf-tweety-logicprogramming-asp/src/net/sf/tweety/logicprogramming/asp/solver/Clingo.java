/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.tweety.logicprogramming.asp.solver;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.sf.tweety.logicprogramming.asp.syntax.*;

/**
 * wrapper for the clingo asp solver. clingo
 * is clasp with gringo in one binary.
 * 
 * @author Thomas Vengels
 *
 */
public class Clingo {
	
	protected String path2clingo;
	
	/**
	 * instantiates a new clingo wrapper.
	 * 
	 * @param path2clingo path to clingo binary
	 */
	public Clingo(String path2clingo) {
		this.path2clingo = path2clingo;
	}
	
	/**
	 * this method computes models for a given program
	 * 
	 * @param program logical program input
	 * @param maxmodels number of models to compute
	 * @return 0 to maxmodels answer sets of input program
	 */
	public List<AnswerSet> computeModels(ELP program, int maxmodels) {
		
		SourceList sl = new SourceList();
		sl.add(program);
		return computeModels(sl,"",maxmodels );	
	}
	
	/**
	 * this method computes models for a given program
	 * 
	 * @param input logical program input source list
	 * @param options command line options
	 * @param maxmodels number of models to compute
	 * @return 0 to maxmodels answer sets of input program
	 */
	public List<AnswerSet> computeModels(SourceList input, String options, int models) {
		ArrayList<AnswerSet> ret = new ArrayList<AnswerSet>();
		List<String> claspout = null;
		
		// call solver
		claspout = runClingo(options, models, input);
		
		// check output and create answer sets
		Iterator<String> i = claspout.iterator();
		while (i.hasNext()) {
			String line = i.next();
			if (line.startsWith("Answer:")) {
				// next line is an answer set, parse that
				if (i.hasNext()) {
					line = i.next();
					line += "}";
					Set<ELPLiteral> model = parseModel(line);					
					ret.add( new AnswerSet( model, 0, 0 ) );
				}
			}
		}
		return ret;
	}
	
	/**
	 * auxiliary class to read clingos stderr, stdout
	 * streams in parallel.
	 * 
	 * @author Thomas
	 *
	 */
	class ReadThread extends Thread {
		BufferedReader br;
		List<String> output;
		public ReadThread(BufferedReader br) {
			this.br = br;
			this.output = new LinkedList<String>();
		}
		
		@Override
		public void run() {
			try {
				
				while (!br.ready()) {
					Thread.sleep(10);
				}
				
				while(br.ready()) {
					output.add( br.readLine() );
				}
			} catch (Exception e) {

			}
		}
		
		public List<String> getOutput() {
			return output;
		}
	}

	/**
	 * this method executes clingo
	 * 
	 * @param cmdln command line options
	 * @param models number of models
	 * @param programs program sources
	 * @return
	 */
	protected List<String> runClingo(String cmdln, int models, SourceList programs) {
		
		List<String> output = new LinkedList<String>();
		String text ="";
		StringBuffer errors = new StringBuffer();
		
		// step 1: let gringo ground input		
		// only use piped input		
		boolean pipeInput = true;
		
		ReadThread isr = null;
		
		try {
			// setup call to clasp
			Process process = Runtime.getRuntime().exec(path2clingo);
			
			BufferedReader inputStream = new BufferedReader(
					new InputStreamReader(process.getInputStream()));			
			BufferedReader errorStream = new BufferedReader(
					new InputStreamReader(process.getErrorStream()));

			isr = new ReadThread(inputStream);
			isr.start();
			
			ReadThread esr = new ReadThread(errorStream);
			esr.start();

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
			
			
			errorStream.close();
			isr.join();			
			inputStream.close();			
			
			process = null;
		} catch (IOException e) {
			System.err.println("clingo error!");
			System.err.println(text);
			System.err.println(e);
		}
		catch (InterruptedException e) {
			System.err.println("clingo error!");
			System.err.println(text);
			System.err.println(e);
		} finally {
			output = isr.getOutput();
		}
			
		return output;
	}
	
	/**
	 * simple parser function, extracts all literals from
	 * an answer set provided by clasp. if the empty answer
	 * set is found, an empty Set<ELPLiteral> is returned.
	 *  
	 * @param s		answer set from clasp
	 * @return		set of literals
	 *
	 */ 
	protected Set<ELPLiteral> parseModel(String s) {		
		// parse result
		Set<ELPLiteral> ret = new LinkedHashSet<ELPLiteral>();
		// buffer to hold symbols during literal parsing.
		// can hold atoms up to arity 99, first token
		// is always the predicate name.
		List<String> tokens = new LinkedList<String>();
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
			
			// whitespace separates literals
			if ((c == ' ')) {
				
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
				
				// unlike dlv, do not skip next char because
				// literals are only separated by a comma,
				// not a comma and space.
				tokenStart = cursor;
				isneg = false;
				
				tokens.clear();					

				continue;
			}
			
			// found opening parenthesis of a term list
			if (c == '(') {
				
				tokenEnd = cursor-1;
				tokens.add(s.substring(tokenStart, tokenEnd));
				
				tokenStart = cursor;
				// scan term list
				while (c != ')') {
					c = s.charAt(cursor++);
					if (c == ',') {
						tokenEnd = cursor-1;
						tokens.add(s.substring(tokenStart, tokenEnd));						
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
					tokens.add(s.substring(tokenStart, tokenEnd));
					
					ELPAtom a = new ELPAtom(tokens);
					if (isneg)
						ret.add(new NegLiteral(a));
					else
						ret.add(a);
				}
				
				tokens.clear();
			}									
		}
		
		return ret;
	}
}
