package net.sf.tweety.arg.saf.util;

import java.util.*;

import net.sf.tweety.*;
import net.sf.tweety.arg.dung.syntax.*;
import net.sf.tweety.arg.saf.*;
import net.sf.tweety.arg.saf.syntax.*;


/**
 * This class implements a belief base sampler for structured argumentation
 * frameworks.
 * 
 * @author Matthias Thimm
 *
 */
public class SimpleSafSampler extends BeliefBaseSampler {

	/**
	 * Creates a new SimpleSafSampler for the given signature. 
	 * @param signature a signature.
	 */
	public SimpleSafSampler(Signature signature) {
		super(signature);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.kr.BeliefBaseSampler#randomSample(int)
	 */
	@Override
	public BeliefBase randomSample(int minLength, int maxLength) {
		//bbLength is interpreted as the maximum number of basic arguments
		Random random = new Random();
		int thisLength = random.nextInt(maxLength-minLength+1) + minLength;
		BasicArgumentSampler argSampler = new BasicArgumentSampler(this.getSignature());
		StructuredArgumentationFramework saf = new StructuredArgumentationFramework();
		for(int i = 0; i < thisLength; i++)
			saf.add(argSampler.randomSample());
		//determine attacks with a probability of 1/5
		// - but do not allow self-attacks (these are useless in SAFs)
		// - avoid with a probability of 3/4 attacks between arguments with same claim
		// - avoid with a probability of 4/5 attacks between arguments where one
		//   argument might support the other
		for(Formula f1 : saf)
			for(Formula f2: saf)
				if(f1 != f2)
					if(random.nextInt(5) == 0){
						BasicArgument arg1 = (BasicArgument) f1;
						BasicArgument arg2 = (BasicArgument) f2;
						if(arg1.getConclusion().equals(arg2.getConclusion())){
							if(random.nextInt(4) == 0)
								saf.add(new Attack(arg1,arg2));
						}else if(arg1.getPremise().contains(arg2.getConclusion()) || arg2.getPremise().contains(arg1.getConclusion())){
							if(random.nextInt(5) == 0)
								saf.add(new Attack(arg1,arg2));
						}else{
							saf.add(new Attack(arg1,arg2));
						}
					}
		return saf;
	}

}
