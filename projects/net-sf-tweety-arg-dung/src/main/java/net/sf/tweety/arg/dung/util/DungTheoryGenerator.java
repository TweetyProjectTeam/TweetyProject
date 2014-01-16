package net.sf.tweety.arg.dung.util;

import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.dung.syntax.Argument;

/**
 * Class implementing this interface provide the capability
 * to generate Dung theories.
 * @author Matthias Thimm
 */
public interface DungTheoryGenerator {

	/**
	 * Generates a new Dung theory
	 * @return a Dung theory,
	 */
	public DungTheory generate();
	
	/**
	 * Generates a new Dung theory where the given argument
	 * is enforced to be in the grounded extension
	 * @param arg an argument that is enforced
	 *  to be in the grounded extension of the generated theory.
	 * @return a Dung theory,
	 */
	public DungTheory generate(Argument arg);
	
	/**
	 * Set the seed for the generation. Every two
	 * runs of generations with the same seed
	 * are ensured to be identical.
	 * @param seed some seed.
	 */
	public void setSeed(long seed);
}
