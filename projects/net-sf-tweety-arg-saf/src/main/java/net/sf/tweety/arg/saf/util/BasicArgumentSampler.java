package net.sf.tweety.arg.saf.util;

import java.util.*;

import net.sf.tweety.arg.saf.syntax.*;
import net.sf.tweety.commons.*;
import net.sf.tweety.logics.pl.syntax.*;

/**
 * This class implements a formula sampler for structured argumentation
 * frameworks.
 * 
 * @author Matthias Thimm
 *
 */
public class BasicArgumentSampler extends FormulaSampler<BasicArgument> {

	/**
	 * Creates a new basic argument sampler for the given signature.
	 * @param signature a signature
	 */
	public BasicArgumentSampler(Signature signature) {
		super(signature);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.kr.FormulaSampler#randomSample(net.sf.tweety.kr.Signature, int)
	 */
	@Override
	public BasicArgument randomSample(int formula_length) {
		if(!(this.getSignature() instanceof PropositionalSignature))
			throw new IllegalArgumentException("Signature must be a propositional signature.");
		// randomly choose conclusion
		Random random = new Random();
		List<Formula> propositions = new ArrayList<Formula>((PropositionalSignature)this.getSignature()); 
		Proposition claim = (Proposition) propositions.get(random.nextInt(propositions.size()));
		propositions.remove(claim);
		// the int formula_length is interpreted as length of support.
		int thisLength = Math.min(random.nextInt(formula_length+1), propositions.size());
		Set<Proposition> support = new HashSet<Proposition>();
		for(int i = 0; i < thisLength; i++){
			int nextInt = random.nextInt(propositions.size());
			support.add((Proposition)propositions.get(nextInt));
			propositions.remove(nextInt);
		}
		return new BasicArgument(claim,support);
	}

}
