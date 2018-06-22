/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
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
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.logics.rcl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sf.tweety.commons.Answer;
import net.sf.tweety.commons.BeliefBase;
import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.BeliefBaseReasoner;
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
public class RelationalBruteForceCReasoner implements BeliefBaseReasoner<RclBeliefSet> {

	/** Logger. */
	static private Logger log = LoggerFactory.getLogger(RelationalBruteForceCReasoner.class);
	
	/**
	 * indicates whether the computed c-representation is simple.
	 */
	private boolean simple = false;
	
	/**
	 * Creates a new relational c-representation reasoner.	
	 * @param simple whether the computed c-representation is simple. 
	 */
	public RelationalBruteForceCReasoner(boolean simple){		
		this.simple = simple;
	}
	
	/**
	 * Queries the given belief set wrt. the given signature.
	 * @param bs some belief set
	 * @param query some query
	 * @param signature some signature
	 * @return
	 */
	public Answer query(RclBeliefSet bs, Formula query,FolSignature signature) {
		if(!(query instanceof RelationalConditional) && !(query instanceof FolFormula))
			throw new IllegalArgumentException("Reasoning in relational conditional logic is only defined for relational conditional and first-order queries.");
		RelationalRankingFunction crepresentation = this.getCRepresentation(bs, signature);
		if(query instanceof RelationalConditional){
			Answer answer = new Answer(bs,query);
			boolean bAnswer = crepresentation.satisfies((RelationalConditional) query);
			answer.setAnswer(bAnswer);
			answer.appendText("The answer is: " + bAnswer);
			return answer;			
		}
		if(query instanceof FolFormula){
			if(((FolFormula)query).isClosed())
				throw new IllegalArgumentException("Reasoning in relational conditional logic is not defined for open first-order queries.");
			int rank = crepresentation.rank((FolFormula)query);
			Answer answer = new Answer(bs,query);			
			answer.setAnswer(new Double(rank));
			answer.appendText("The rank of the query is " + rank + " (the query is " + ((rank==0)?(""):("not ")) + "believed)");
			return answer;
		}			
		return null;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.BeliefBaseReasoner#query(net.sf.tweety.commons.BeliefBase, net.sf.tweety.commons.Formula)
	 */
	@Override
	public Answer query(RclBeliefSet bs, Formula query) {
		return query(bs,query,(FolSignature)bs.getSignature());
	}
	
	/**
	 * Computes a minimal c-representation for this reasoner's knowledge base.
	 * @param bs a rcl belief set 
	 * @param signature some signature
	 * @return a minimal c-representation for this reasoner's knowledge base.
	 */
	public RelationalRankingFunction getCRepresentation(RclBeliefSet bs, FolSignature signature){		
		int i = 0;
		Map<Integer,RelationalConditional> indexToConditional = new HashMap<Integer,RelationalConditional>();
		for(Formula f: bs){
			indexToConditional.put(i++, (RelationalConditional) f);
			if(!this.simple)
				indexToConditional.put(i++, (RelationalConditional) f);
		}
		List<Integer[]> kappa_all = new ArrayList<Integer[]>();
		Integer[] kappa = null;		
		RelationalRankingFunction candidate = this.constructRankingFunction(kappa,indexToConditional,signature);
		while(!candidate.satisfies((BeliefBase)bs)){
			kappa = this.increment(kappa_all, bs.size());			
			candidate = this.constructRankingFunction(kappa,indexToConditional,signature);
			String debugMessage = "";
			if(this.simple){
				debugMessage = "[ KMINUS "+ indexToConditional.get(0) + " : " + kappa[0];
				for(int j=1; j< kappa.length;j++)
					debugMessage += ", KMINUS "+ indexToConditional.get(j) + " : " + kappa[j];
				debugMessage += "]";
			}else{
				debugMessage = "[ KPLUS/KMINUS "+ indexToConditional.get(0) + " : " + kappa[0] + "/" + kappa[1];
				for(int j=2; j< kappa.length;j+=2){
					debugMessage += ", KPLUS/KMINUS "+ indexToConditional.get(j/2) + " : " + kappa[j] + "/" + kappa[j+1];
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
	private RelationalRankingFunction constructRankingFunction(Integer[] kappa, Map<Integer,RelationalConditional> indexToConditional,  FolSignature signature){
		RelationalRankingFunction candidate = new RelationalRankingFunction(signature);
		if(kappa == null) 
			return candidate;
		// following [Kern-Isberner,Thimm, "A Ranking Semantics for Relational Defaults", in preparation]
		// the rank of an interpretation is determined by the number of verified/falsified instances
		for(HerbrandInterpretation w: candidate.getPossibleInterpretations()){
			int sum = 0;
			if(this.simple){
				for(int idx = 0; idx < kappa.length; idx++)
					sum += candidate.numFalsifiedInstances(w, indexToConditional.get(idx)) * kappa[idx];					
			}else{
				for(int idx = 0; idx < kappa.length; idx+=2)
					sum += candidate.numVerifiedInstances(w, indexToConditional.get(idx)) * kappa[idx];
				for(int idx = 1; idx < kappa.length; idx+=2)
					sum += candidate.numFalsifiedInstances(w, indexToConditional.get(idx)) * kappa[idx];
			}
			candidate.setRank(w, sum);
		}
		return candidate;
	}
	
	/**
	 * This method increments the given array by one value.
	 * @param kappa_all the values of all kappas
	 * @param numConditionals the number of conditionals
	 * @return an array of integers.
	 */
	private Integer[] increment(List<Integer[]> kappa_all, int numConditionals){
		if(kappa_all.isEmpty()){
			Integer[] first;
			if(this.simple)
				first = new Integer[numConditionals];
			else
				first = new Integer[2*numConditionals];
			first[0] = 1;
			for(int i = 1; i < first.length; i++)
				first[i] = 0;			
			kappa_all.add(first);			
		}else{
			boolean overflow = true;
			int j = 0;
			while(overflow && j < kappa_all.size()){
				overflow = this.incrementStep(kappa_all.get(j));
				j++;
			}
			if(overflow){
				//add new vector
				Integer[] newVec;
				if(this.simple)
					newVec= new Integer[numConditionals];
				else
					newVec= new Integer[2*numConditionals];
				newVec[0] = 1;
				for(int i = 1; i < newVec.length; i++)
					newVec[i] = 0;
				kappa_all.add(newVec);	
			}
		}
		//compute the actual kappa values
		Integer[] newKappa;
		if(this.simple)
			newKappa = new Integer[numConditionals];
		else
			newKappa = new Integer[2*numConditionals];
		for(int i = 0; i < newKappa.length; i++){
			newKappa[i] = 0;
			for(Integer[] v: kappa_all)
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
