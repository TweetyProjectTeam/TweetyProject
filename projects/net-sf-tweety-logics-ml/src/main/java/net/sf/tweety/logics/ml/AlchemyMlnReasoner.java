package net.sf.tweety.logics.ml;

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

import net.sf.tweety.BeliefBase;
import net.sf.tweety.logics.commons.syntax.Constant;
import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.commons.syntax.Sort;
import net.sf.tweety.logics.commons.syntax.Variable;
import net.sf.tweety.logics.commons.syntax.interfaces.Term;
import net.sf.tweety.logics.fol.syntax.Conjunction;
import net.sf.tweety.logics.fol.syntax.Disjunction;
import net.sf.tweety.logics.fol.syntax.ExistsQuantifiedFormula;
import net.sf.tweety.logics.fol.syntax.FOLAtom;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.syntax.FolSignature;
import net.sf.tweety.logics.fol.syntax.ForallQuantifiedFormula;
import net.sf.tweety.logics.fol.syntax.Negation;
import net.sf.tweety.logics.fol.syntax.RelationalFormula;
import net.sf.tweety.logics.ml.syntax.MlnFormula;

/**
 * This class implements a wrapper for Alchemy in order to
 * reason with MLNs. Note: implementation inspired by 
 * AlchemyWrapper of KReator (http://kreator-ide.sourceforge.net)
 * 
 * @author Matthias Thimm
 */
public class AlchemyMlnReasoner extends AbstractMlnReasoner {

	/** The console command for Alchemy inference. */
	private String inferCmd = "infer";
		
	/**
	 * Creates a new AlchemyMlnReasoner for the given Markov logic network.
	 * @param beliefBase a Markov logic network. 
	 */
	public AlchemyMlnReasoner(BeliefBase beliefBase){
		this(beliefBase, (FolSignature) beliefBase.getSignature());
	}
	
	/**
	 * Creates a new AlchemyMlnReasoner for the given Markov logic network.
	 * @param beliefBase a Markov logic network. 
	 * @param signature another signature (if the probability distribution should be defined 
	 * on that one (that one should subsume the signature of the Markov logic network)
	 */
	public AlchemyMlnReasoner(BeliefBase beliefBase, FolSignature signature){
		super(beliefBase, signature);		
	}

	/** Sets the console command for Alchemy inference (default is 'infer').
	 * @param inferCmd the console command for Alchemy inference. 
	 */
	public void setAlchemyInferenceCommand(String inferCmd){
		this.inferCmd = inferCmd;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.markovlogic.AbstractMlnReasoner#doQuery(net.sf.tweety.logics.firstorderlogic.syntax.FolFormula)
	 */
	@Override
	public double doQuery(FolFormula query) {
		// NOTE: as the query formula might be an arbitrary formula
		// and Alchemy only supports querying the probabilities
		// of atoms, we need to encode the query in the MLN
		// by stating it to be equivalent to some new atom
		try{
			File mlnFile = this.writeAlchemyMlnFile((MarkovLogicNetwork)this.getKnowledgBase(), this.getSignature(), query);			
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
			return new Double(token);
		}catch(Exception e) {
			// TODO
			e.printStackTrace();
			return -1;
		}		
	}
	
	/** Writes the given MLN wrt. the given signature to a temporary file
	 * in Alchemy syntax.
	 * @param mln some MLN.
	 * @param signature some fol signature.
	 * @param queryFormula the query formula that has to be encoded as well.
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
		if(formula instanceof FOLAtom){
			FOLAtom a = (FOLAtom) formula;
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
		throw new IllegalArgumentException("Representation of tautologies and contradictions not supported in Alchemy.");
	}
	
	/**
	 * Returns the string in Alchemy syntax representing the given term.
	 * @param tern some FOL tern
	 * @return the string in Alchemy syntax representing the given term.
	 */
	private String alchemyStringForTerm(Term<?> t){
		if(t instanceof Constant)
			return t.toString().toUpperCase();
		if(t instanceof Variable)
			return t.toString().toLowerCase();
		throw new IllegalArgumentException("Functional expressions not supported by Alchemy.");
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.markovlogic.AbstractMlnReasoner#reset()
	 */
	@Override
	public void reset() { }
}
