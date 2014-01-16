package net.sf.tweety;

import java.util.Collection;

/**
 * An abstract interpretation for some logical language.
 * @author Matthias Thimm
 */
public abstract class AbstractInterpretation implements Interpretation {
	
	/**
	 * Checks whether this interpretation satisfies all given formulas.
	 * @param formulas a collection of formulas.
	 * @return "true" if this interpretation satisfies all given formulas. 
	 * @throws IllegalArgumentException if at least one formula does not correspond
	 * 		to the expected language.
	 */
	@Override
	public boolean satisfies(Collection<? extends Formula> formulas) throws IllegalArgumentException{
		for(Formula f: formulas)
			if(!this.satisfies(f))
				return false;
		return true;
	}
}
