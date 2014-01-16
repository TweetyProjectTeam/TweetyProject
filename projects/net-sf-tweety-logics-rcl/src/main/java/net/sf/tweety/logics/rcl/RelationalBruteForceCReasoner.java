package net.sf.tweety.logics.rcl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.tweety.Answer;
import net.sf.tweety.BeliefBase;
import net.sf.tweety.Formula;
import net.sf.tweety.Reasoner;
import net.sf.tweety.logics.fol.semantics.HerbrandInterpretation;
import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.fol.syntax.FolSignature;
import net.sf.tweety.logics.rcl.semantics.RelationalRankingFunction;
import net.sf.tweety.logics.rcl.syntax.RelationalConditional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This class models a relational brute force c-reasoner for relational conditional logic. Reasoning is performed
 * by computing a minimal c-representation for the given knowledge base.<br>
 * 
 * A c-representation for a conditional knowledge base R={r1,...,rn} is a ranking function k such that
 * k accepts every conditional in R (k |= R) and if there are numbers k0,k1+,k1-,...,kn+,kn- with<br>
 * 
 * k(w)=k0 + \sum_{w verifies ri} ki+ + \sum_{w falsifies ri} kj-
 * 
 * for every w. A c-representation is minimal if k0+...+kn- is minimal.<br>
 * 
 * The c-representation is computed using a brute force approach.
 * 
 * <br><br>See Gabriele Kern-Isberner. Conditionals in nonmonotonic reasoning and belief revision.
 * Lecture Notes in Computer Science, Volume 2087. 2001.
 * 
 * <br><br>See also [Kern-Isberner,Thimm, "A Ranking Semantics for Relational Defaults", in preparation].
 * 
 * @author Matthias Thimm
 */
public class RelationalBruteForceCReasoner extends Reasoner {

	/** Logger. */
	static private Logger log = LoggerFactory.getLogger(RelationalBruteForceCReasoner.class);
	
	/**
	 * The relational c-representation for the given knowledge base. Once this
	 * ranking function has been computed it is used for
	 * subsequent queries in order to avoid unnecessary
	 * computations.
	 */
	private RelationalRankingFunction crepresentation = null;
	
	/**
	 * The current vectors of kappa values.
	 */
	private List<Integer[]> kappa;
	
	/**
	 * The number of conditionals in the given knowledge base.
	 */
	private int numConditionals;
	
	/**
	 * The signature used for building the c-representation. 
	 */
	private FolSignature signature;
	
	/**
	 * Maps the indices of the kappa vector to their corresponding
	 * conditionals. 
	 */
	private Map<Integer,RelationalConditional> indexToConditional;
	
	/**
	 * indicates whether the computed c-representation is simple.
	 */
	private boolean simple = false;
	
	/**
	 * Creates a new relational c-representation reasoner for the given knowledge base.
	 * @param beliefBase a knowledge base.
	 * @param signature the signature used for building the c-representation.	
	 * @param simple whether the computed c-representation is simple. 
	 */
	public RelationalBruteForceCReasoner(BeliefBase beliefBase, FolSignature signature, boolean simple){
		super(beliefBase);		
		if(!(beliefBase instanceof RclBeliefSet))
			throw new IllegalArgumentException("Knowledge base of class RclBeliefSet expected.");
		this.signature = signature;
		this.simple = simple;
	}
	
	/**
	 * Creates a new simple c-representation reasoner for the given knowledge base.
	 * @param beliefBase  a knowledge base.
	 * @param signature the signature used for building the c-representation.	
	 */
	public RelationalBruteForceCReasoner(BeliefBase beliefBase, FolSignature signature){
		this(beliefBase,signature, false);
	}
	
	/**
	 * Returns the c-representation this reasoner bases on.
	 * @return the c-representation this reasoner bases on.
	 */
	public RelationalRankingFunction getCRepresentation(){
		if(this.crepresentation == null)
			this.crepresentation = this.computeCRepresentation();
		return this.crepresentation;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.Reasoner#query(net.sf.tweety.Formula)
	 */
	@Override
	public Answer query(Formula query) {
		if(!(query instanceof RelationalConditional) && !(query instanceof FolFormula))
			throw new IllegalArgumentException("Reasoning in relational conditional logic is only defined for relational conditional and first-order queries.");
		RelationalRankingFunction crepresentation = this.getCRepresentation();
		if(query instanceof RelationalConditional){
			Answer answer = new Answer(this.getKnowledgBase(),query);
			boolean bAnswer = crepresentation.satisfies(query);
			answer.setAnswer(bAnswer);
			answer.appendText("The answer is: " + bAnswer);
			return answer;			
		}
		if(query instanceof FolFormula){
			if(((FolFormula)query).isClosed())
				throw new IllegalArgumentException("Reasoning in relational conditional logic is not defined for open first-order queries.");
			int rank = crepresentation.rank((FolFormula)query);
			Answer answer = new Answer(this.getKnowledgBase(),query);			
			answer.setAnswer(new Double(rank));
			answer.appendText("The rank of the query is " + rank + " (the query is " + ((rank==0)?(""):("not ")) + "believed)");
			return answer;
		}			
		return null;
	}
	
	/**
	 * Computes a minimal c-representation for this reasoner's knowledge base. 
	 * @return a minimal c-representation for this reasoner's knowledge base.
	 */
	private RelationalRankingFunction computeCRepresentation(){		
		this.numConditionals = ((RclBeliefSet)this.getKnowledgBase()).size();
		int i = 0;
		this.indexToConditional = new HashMap<Integer,RelationalConditional>();
		for(Formula f: ((RclBeliefSet)this.getKnowledgBase())){
			this.indexToConditional.put(i++, (RelationalConditional) f);
			if(!this.simple)
				this.indexToConditional.put(i++, (RelationalConditional) f);
		}
		Integer[] kappa = null;		
		RelationalRankingFunction candidate = this.constructRankingFunction(kappa);
		while(!candidate.satisfies(this.getKnowledgBase())){
			kappa = this.increment(kappa);			
			candidate = this.constructRankingFunction(kappa);
			String debugMessage = "";
			if(this.simple){
				debugMessage = "[ KMINUS "+ this.indexToConditional.get(0) + " : " + kappa[0];
				for(int j=1; j< kappa.length;j++)
					debugMessage += ", KMINUS "+ this.indexToConditional.get(j) + " : " + kappa[j];
				debugMessage += "]";
			}else{
				debugMessage = "[ KPLUS/KMINUS "+ this.indexToConditional.get(0) + " : " + kappa[0] + "/" + kappa[1];
				for(int j=2; j< kappa.length;j+=2){
					debugMessage += ", KPLUS/KMINUS "+ this.indexToConditional.get(j/2) + " : " + kappa[j] + "/" + kappa[j+1];
				}
				debugMessage += "]";
			}				
			log.debug(debugMessage);
		}		
		candidate.normalize();
		return candidate;
	}
	
	/**
	 * Constructs a ranking function with the given kappa values [k1+,k1-,...,kn+,kn-], i.e.
	 * for every possible world w set<br>
	 * k(w)=k0 + \sum_{w verifies ri} ki+ + \sum_{w falsifies ri} kj-
	 * @param kappa
	 */
	private RelationalRankingFunction constructRankingFunction(Integer[] kappa){
		RelationalRankingFunction candidate = new RelationalRankingFunction(this.signature);
		if(kappa == null) 
			return candidate;
		// following [Kern-Isberner,Thimm, "A Ranking Semantics for Relational Defaults", in preparation]
		// the rank of an interpretation is determined by the number of verified/falsified instances
		for(HerbrandInterpretation w: candidate.getPossibleInterpretations()){
			int sum = 0;
			if(this.simple){
				for(int idx = 0; idx < kappa.length; idx++)
					sum += candidate.numFalsifiedInstances(w, this.indexToConditional.get(idx)) * kappa[idx];					
			}else{
				for(int idx = 0; idx < kappa.length; idx+=2)
					sum += candidate.numVerifiedInstances(w, this.indexToConditional.get(idx)) * kappa[idx];
				for(int idx = 1; idx < kappa.length; idx+=2)
					sum += candidate.numFalsifiedInstances(w, this.indexToConditional.get(idx)) * kappa[idx];
			}
			candidate.setRank(w, sum);
		}
		return candidate;
	}
	
	/**
	 * This method increments the given array by one value.
	 * @param kappa an array of integers.
	 * @return an array of integers.
	 */
	private Integer[] increment(Integer[] kappa){
		if(this.kappa == null){
			Integer[] first;
			if(this.simple)
				first = new Integer[this.numConditionals];
			else
				first = new Integer[2*this.numConditionals];
			first[0] = 1;
			for(int i = 1; i < first.length; i++)
				first[i] = 0;
			this.kappa = new ArrayList<Integer[]>();
			this.kappa.add(first);			
		}else{
			boolean overflow = true;
			int j = 0;
			while(overflow && j < this.kappa.size()){
				overflow = this.incrementStep(this.kappa.get(j));
				j++;
			}
			if(overflow){
				//add new vector
				Integer[] newVec;
				if(this.simple)
					newVec= new Integer[this.numConditionals];
				else
					newVec= new Integer[2*this.numConditionals];
				newVec[0] = 1;
				for(int i = 1; i < newVec.length; i++)
					newVec[i] = 0;
				this.kappa.add(newVec);	
			}
		}
		//compute the actual kappa values
		Integer[] newKappa;
		if(this.simple)
			newKappa = new Integer[this.numConditionals];
		else
			newKappa = new Integer[2*this.numConditionals];
		for(int i = 0; i < newKappa.length; i++){
			newKappa[i] = 0;
			for(Integer[] v: this.kappa)
				newKappa[i] += v[i];
		}
		return newKappa;
	}
	
	/**
	 * This method increments the given vector (which composes of exactly
	 * one "1" entry and zeros otherwise), e.g. [0,0,1,0] -> [0,0,0,1]
	 * and [0,0,0,1] -> [1,0,0,0] 
	 * @param kappaRow a vector of zeros and one "1"
	 * @return "true" if there is an overflow, i.e. when [0,0,0,1] -> [1,0,0,0], otherwise
	 *  "false". 
	 */
	private boolean incrementStep(Integer[] kappaRow){
		int length = kappaRow.length;
		if(kappaRow[length-1] == 1){
			kappaRow[length-1] = 0;
			kappaRow[0] = 1;
			return true;
		}else{
			for(int i = 0; i< length-1; i++){
				if(kappaRow[i] == 1){
					kappaRow[i] = 0;
					kappaRow[i+1] = 1;
					return false;
				}
			}
		}
		throw new IllegalArgumentException("Argument must contain at least one value \"1\"");
	}
}
