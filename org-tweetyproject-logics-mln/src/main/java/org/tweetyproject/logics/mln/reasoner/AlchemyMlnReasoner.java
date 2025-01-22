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
package org.tweetyproject.logics.mln.reasoner;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringTokenizer;

import org.tweetyproject.logics.commons.syntax.Constant;
import org.tweetyproject.logics.commons.syntax.Predicate;
import org.tweetyproject.logics.commons.syntax.Sort;
import org.tweetyproject.logics.commons.syntax.Variable;
import org.tweetyproject.logics.commons.syntax.interfaces.Term;
import org.tweetyproject.logics.fol.syntax.Conjunction;
import org.tweetyproject.logics.fol.syntax.Disjunction;
import org.tweetyproject.logics.fol.syntax.ExclusiveDisjunction;
import org.tweetyproject.logics.fol.syntax.ExistsQuantifiedFormula;
import org.tweetyproject.logics.fol.syntax.FolAtom;
import org.tweetyproject.logics.fol.syntax.FolFormula;
import org.tweetyproject.logics.fol.syntax.FolSignature;
import org.tweetyproject.logics.fol.syntax.ForallQuantifiedFormula;
import org.tweetyproject.logics.fol.syntax.Negation;
import org.tweetyproject.logics.mln.syntax.MarkovLogicNetwork;
import org.tweetyproject.logics.mln.syntax.MlnFormula;
import org.tweetyproject.logics.commons.syntax.RelationalFormula;

/**
 * This class implements a wrapper for Alchemy in order to
 * reason with MLNs. Note: implementation inspired by
 * AlchemyWrapper of KReator (http://kreator-ide.sourceforge.net)
 *
 * @author Matthias Thimm
 */
public class AlchemyMlnReasoner extends AbstractMlnReasoner {
	/** Default */
	public AlchemyMlnReasoner(){
		super();
	}

	/** The console command for Alchemy inference. */
	private String inferCmd = "infer";

	/** Sets the console command for Alchemy inference (default is 'infer').
	 * @param inferCmd the console command for Alchemy inference.
	 */
	public void setAlchemyInferenceCommand(String inferCmd){
		this.inferCmd = inferCmd;
	}

	/* (non-Javadoc)
	 * @see org.tweetyproject.logics.mln.AbstractMlnReasoner#doQuery(org.tweetyproject.logics.mln.MarkovLogicNetwork, org.tweetyproject.logics.fol.syntax.FolFormula, org.tweetyproject.logics.fol.syntax.FolSignature)
	 */
	@Override
	public double doQuery(MarkovLogicNetwork mln, FolFormula query, FolSignature signature) {
		// NOTE: as the query formula might be an arbitrary formula
		// and Alchemy only supports querying the probabilities
		// of atoms, we need to encode the query in the MLN
		// by stating it to be equivalent to some new atom
		try{
			File mlnFile = this.writeAlchemyMlnFile(mln, signature, query);
			//empty evidence file needed
			File evidenceFile = File.createTempFile("alchemy_ev",null);
			evidenceFile.deleteOnExit();
			//result file
			File resultFile = File.createTempFile("alchemy_res",null);
			resultFile.deleteOnExit();
			//execute Alchemy inference on problem and retrieve console output
			//TODO parametrize parameters
			List<String> processCommandsList = new ArrayList<String>();
			processCommandsList.add(this.inferCmd);
	        processCommandsList.add("-i");
	        processCommandsList.add(mlnFile.getAbsolutePath());
	        processCommandsList.add("-e");
	        processCommandsList.add(evidenceFile.getAbsolutePath());
	        processCommandsList.add("-r");
	        processCommandsList.add(resultFile.getAbsolutePath() );
            processCommandsList.add("-q");
            processCommandsList.add("tweetyQueryFormula(TWEETYQUERYCONSTANT)");
            processCommandsList.add("-p");
            processCommandsList.add("true");
            processCommandsList.add("-burnMinSteps");
            processCommandsList.add("1000");
            processCommandsList.add("-burnMaxSteps");
            processCommandsList.add("-1");
            processCommandsList.add("-maxSteps");
            processCommandsList.add("100000");
            processCommandsList.add("-lifted");
            processCommandsList.add("true");
            processCommandsList.add("-numChains");
            processCommandsList.add("50");
            processCommandsList.add("-epsilonError");
            processCommandsList.add("0.0001");
            processCommandsList.add("-fracConverged");
            processCommandsList.add("0.999");
            processCommandsList.add("-delta");
            processCommandsList.add("0.0001");

	        ProcessBuilder processBuilder = new ProcessBuilder(processCommandsList);
	        processBuilder.redirectErrorStream(true);
	        final Process process;
            process = processBuilder.start();
	        BufferedReader br = new BufferedReader(new InputStreamReader(process.getInputStream()));
	        String line = "";
	        while (true) {
                line = br.readLine();
                if (line == null) {
                    break;
	            }
	        }
        	process.waitFor();
	        String resultString = "";
            BufferedReader resultReader = new BufferedReader(new FileReader(resultFile));
            line = "";
            while(line != null) {
                line = resultReader.readLine();
                resultString += line != null ? line + "\n" : "";
            }
            resultReader.close();
	        StringTokenizer tokenizer = new StringTokenizer(resultString);
	        String token = null;
	        while(tokenizer.hasMoreTokens())
	        	token = tokenizer.nextToken();
	        if(token == null)
	        	throw new RuntimeException();
			return Double.parseDouble(token);
		}catch(Exception e) {
			System.err.println("Could not find or missing rights to execute Alchemy binary 'infer'. "
					+ "If 'infer' is not in your PATH please specify its location using the "
					+ "'setAlchemyInferenceCommand(String)' method of 'AlchemyMlnReasoner'. "
					+ "Installation instructions for Alchemy can be found at "
					+ "http://alchemy.cs.washington.edu. If you do not wish to use Alchemy "
					+ "you can choose e.g. 'SimpleSamplingMlnReasoner' as an alternative that is less "
					+ "accurate but does not depend on third-party projects.\n\nThe application will "
					+ "now terminate.");
			System.exit(1);
			return -1;
		}
	}

	/** Writes the given MLN wrt. the given signature to a temporary file
	 * in Alchemy syntax.
	 * @param mln some MLN.
	 * @param signature some fol signature.
	 * @param formula the query formula that has to be encoded as well.
	 * @return the file object of the Alchemy MLN file.
	 * @throws IOException if file writing fails.
	 */
	private File writeAlchemyMlnFile(MarkovLogicNetwork mln, FolSignature signature, FolFormula formula) throws IOException{
		File mlnFile = File.createTempFile("alchemy_mln",null);
		mlnFile.deleteOnExit();
		FileWriter fstream = new FileWriter(mlnFile.getAbsoluteFile());
		BufferedWriter out = new BufferedWriter(fstream);
		// write sorts
		for(Sort s: signature.getSorts()){
			out.append(s.getName().toLowerCase() + " = {");
			boolean isFirst = true;
			for(Constant c: s.getTerms(Constant.class)){
				if(isFirst){
					out.append(c.get().toUpperCase());
					isFirst = false;
				}else{
					out.append("," + c.get().toUpperCase());
				}
			}
			out.append("}\n");
		}
		// write query sort
		out.append("tweetyqueryconstant = {TWEETYQUERYCONSTANT}\n");
		out.append("\n");
		// write predicates
		for(Predicate p: signature.getPredicates()){
			out.append(p.getName().toLowerCase());
			if(p.getArgumentTypes().size()>0)
				out.append("(");
			boolean isFirst = true;
			for(Sort s: p.getArgumentTypes()){
				if(isFirst){
					out.append(s.getName().toLowerCase());
					isFirst = false;
				}else{
					out.append("," + s.getName().toLowerCase());
				}
			}
			if(p.getArgumentTypes().size()>0)
				out.append(")\n");
		}
		// write query predicate
		out.append("tweetyQueryFormula(tweetyqueryconstant)");
		out.append("\n");
		// write query formula
		out.append("tweetyQueryFormula(TWEETYQUERYCONSTANT) <=> " + this.alchemyStringForFormula(formula) + " .\n\n");
		// write formulas
		for(MlnFormula f: mln){
			if(f.isStrict())
				out.append(this.alchemyStringForFormula(f.getFormula()) + " .\n");
			else
				out.append(f.getWeight() + " " + this.alchemyStringForFormula(f.getFormula()) + "\n");
		}
		out.close();
		return mlnFile;
	}

	/**
	 * Returns the string in Alchemy syntax representing the given formula.
	 * @param formula some FOL formula
	 * @return the string in Alchemy syntax representing the given formula.
	 */
	private String alchemyStringForFormula(RelationalFormula formula){
		if(formula instanceof Conjunction){
			String result = "";
			boolean isFirst = true;
			for(RelationalFormula f: ((Conjunction)formula)){
				if(isFirst){
					result += "(" + this.alchemyStringForFormula((FolFormula)f) + ")";
					isFirst = false;
				}else{
					result += " ^ (" + this.alchemyStringForFormula((FolFormula)f) + ")";
				}
			}
			return result;
		}
		if(formula instanceof Disjunction){
			String result = "";
			boolean isFirst = true;
			for(RelationalFormula f: ((Disjunction)formula)){
				if(isFirst){
					result += "(" + this.alchemyStringForFormula((FolFormula)f) + ")";
					isFirst = false;
				}else{
					result += " v (" + this.alchemyStringForFormula((FolFormula)f) + ")";
				}
			}
			return result;
		}
		if(formula instanceof Negation){
			return "!(" + this.alchemyStringForFormula(((Negation)formula).getFormula()) + ")";
		}
		if(formula instanceof ForallQuantifiedFormula){
			Collection<Variable> vars = ((ForallQuantifiedFormula)formula).getQuantifierVariables();
			String result = "FORALL ";
			boolean isFirst = true;
			for(Variable v: vars){
				if(isFirst){
					result += v.toString().toLowerCase();
					isFirst = false;
				}else{
					result += "," + v.toString().toLowerCase();
				}
			}
			return result + " (" + this.alchemyStringForFormula(((ForallQuantifiedFormula)formula).getFormula()) + ")";
		}
		if(formula instanceof ExistsQuantifiedFormula){
			Collection<Variable> vars = ((ExistsQuantifiedFormula)formula).getQuantifierVariables();
			String result = "EXIST ";
			boolean isFirst = true;
			for(Variable v: vars){
				if(isFirst){
					result += v.toString().toLowerCase();
					isFirst = false;
				}else{
					result += "," + v.toString().toLowerCase();
				}
			}
			return result + " (" + this.alchemyStringForFormula(((ExistsQuantifiedFormula)formula).getFormula()) + ")";
		}
		if(formula instanceof FolAtom){
			FolAtom a = (FolAtom) formula;
			String result = a.getPredicate().getName().toLowerCase();
			if(a.getArguments().size()>0)
				result += "(";
			boolean isFirst = true;
			for(Term<?> t: a.getArguments()){
				if(isFirst){
					result += this.alchemyStringForTerm(t);
					isFirst = false;
				}else{
					result += "," + this.alchemyStringForTerm(t);
				}
			}
			if(a.getArguments().size()>0)
				result += ")";
			return result;
		}
		if (formula instanceof ExclusiveDisjunction) {
			return alchemyStringForFormula((Disjunction) ((ExclusiveDisjunction) formula).toDnf());
		}
		throw new IllegalArgumentException("Representation of tautologies and contradictions not supported in Alchemy.");
	}

	/**
	 * Returns the string in Alchemy syntax representing the given term.
	 * @param t some FOL tern
	 * @return the string in Alchemy syntax representing the given term.
	 */
	private String alchemyStringForTerm(Term<?> t){
		if(t instanceof Constant)
			return t.toString().toUpperCase();
		if(t instanceof Variable)
			return t.toString().toLowerCase();
		throw new IllegalArgumentException("Functional expressions not supported by Alchemy.");
	}

	@Override
	public boolean isInstalled() {
		return true;
	}
}
