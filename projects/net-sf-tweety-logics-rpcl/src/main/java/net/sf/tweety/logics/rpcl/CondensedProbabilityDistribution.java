package net.sf.tweety.logics.rpcl;

import java.util.*;

import net.sf.tweety.commons.*;
import net.sf.tweety.logics.commons.syntax.Constant;
import net.sf.tweety.logics.fol.syntax.*;
import net.sf.tweety.logics.pcl.semantics.*;
import net.sf.tweety.logics.rpcl.semantics.*;
import net.sf.tweety.logics.rpcl.syntax.*;
import net.sf.tweety.math.probability.Probability;


/**
 * Instances of this class represent condensed probability distributions, rf. [PhD thesis, Thimm].
 * 
 * @author Matthias Thimm
 *
 */
public class CondensedProbabilityDistribution extends ProbabilityDistribution<ReferenceWorld> {

	/**
	 * The semantics used for this probability distribution.
	 */
	private RpclSemantics semantics;
	
	/**
	 * Creates a new condensed probability distribution for the given signature.
	 * @param semantics the semantics used for this distribution.
	 * @param signature a fol signature.
	 */
	public CondensedProbabilityDistribution(RpclSemantics semantics, FolSignature signature){
		super(signature);
		this.semantics = semantics;		
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.kr.Interpretation#satisfies(net.sf.tweety.kr.Formula)
	 */
	@Override
	public boolean satisfies(Formula formula) throws IllegalArgumentException {
		if(!(formula instanceof RelationalProbabilisticConditional))
			throw new IllegalArgumentException("Relational probabilistic conditional expected.");
		return semantics.satisfies(this, (RelationalProbabilisticConditional)formula);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.kr.Interpretation#satisfies(net.sf.tweety.kr.BeliefBase)
	 */
	@Override
	public boolean satisfies(BeliefBase beliefBase)	throws IllegalArgumentException {
		if(!(beliefBase instanceof RpclBeliefSet))
			throw new IllegalArgumentException("Relational probabilistic conditional knowledge base expected.");
		RpclBeliefSet kb = (RpclBeliefSet) beliefBase;
		for(Formula f: kb)
			if(!this.satisfies(f)) return false;
		return true;
	}
	
	/**
	 * Returns the semantics of this distribution.
	 * @return the semantics of this distribution.
	 */
	public RpclSemantics getSemantics(){
		return this.semantics;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.relationalprobabilisticconditionallogic.semantics.RpclProbabilityDistribution#entropy()
	 */
	@Override
	public double entropy(){
		double entropy = 0;
		for(Interpretation i : this.keySet())
			if(this.probability(i).getValue() != 0)
				entropy -= ((ReferenceWorld)i).spanNumber() * this.probability(i).getValue() * Math.log(this.probability(i).getValue());
		return entropy;
	}
	
	/**
	 * Returns the condensed entropy of this distribution (neglecting multiplicators of
	 * reference worlds.
	 * @return the condensed entropy of this distribution
	 */
	public double condensedEntropy(){
		return super.entropy();
	}
		
	/**
	 * Returns the uniform distribution on the given signature.
	 * @param semantics the semantics used for the distribution
	 * @param signature a fol signature
	 * @return the uniform distribution on the given signature.
	 */
	public static CondensedProbabilityDistribution getUniformDistribution(RpclSemantics semantics, FolSignature signature, Set<Set<Constant>> equivalenceClasses){
		CondensedProbabilityDistribution p = new CondensedProbabilityDistribution(semantics,signature);
		Set<ReferenceWorld> interpretations = ReferenceWorld.enumerateReferenceWorlds(signature.getPredicates(), equivalenceClasses); 
		double size = 0;
		for(ReferenceWorld i: interpretations)
			size += i.spanNumber();
		for(ReferenceWorld i: interpretations)
			p.put(i, new Probability(i.spanNumber()*(1/size)));
		return p;
	}
	
	/**
	 * Returns a random distribution on the given signature.
	 * @param semantics the semantics used for the distribution
	 * @param signature a fol signature
	 * @return a random distribution on the given signature.
	 */
	public static CondensedProbabilityDistribution getRandomDistribution(RpclSemantics semantics, FolSignature signature, Set<Set<Constant>> equivalenceClasses){
		CondensedProbabilityDistribution p = new CondensedProbabilityDistribution(semantics,signature);
		Set<ReferenceWorld> interpretations = ReferenceWorld.enumerateReferenceWorlds(signature.getPredicates(), equivalenceClasses); 
		Random rand = new Random();
		List<Double> probs = new ArrayList<Double>();
		for(int i = 0; i < interpretations.size(); i++)
			probs.add(rand.nextDouble());
		ProbabilityDistribution.normalize(probs);
		Iterator<Double> itProbs = probs.iterator();
		for(ReferenceWorld i: interpretations)
			p.put(i, new Probability(itProbs.next()/i.spanNumber()));
		return p;
	}
	
	/**
	 * Converts this condensed probability distribution into an ordinary
	 * probability distribution.
	 * @return a probability distribution.
	 */
	public RpclProbabilityDistribution toProbabilityDistribution(){
		//TODO implement me
		return null;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.relationalprobabilisticconditionallogic.semantics.RpclProbabilityDistribution#probability(net.sf.tweety.logics.firstorderlogic.syntax.FolFormula)
	 */
	public Probability probability(FolFormula f){
		Probability p = new Probability(0d);
		for(Interpretation w: this.keySet()){
			ReferenceWorld rw = (ReferenceWorld) w;
			p = p.add(this.probability(rw).mult(rw.getMultiplicator(f)));
		}
		return p;
	}
}
