package net.sf.tweety.logics.fol.writer;

import java.io.IOException;

import net.sf.tweety.logics.fol.FolBeliefSet;
import net.sf.tweety.logics.fol.syntax.FolFormula;

/**
 * Prints out single fol formulas and full knowledge bases
 * 
 * @author Nils Geilen
 *
 */

public interface FolWriter {
	
	/**
	 * Prints formatted representation of a Query.
	 * @param query
	 *            the formula to be queried
	 */
	public void printQuery(FolFormula query) throws IOException;
	
	/**
	 * Prints an Equivalence
	 * @param a formula on one side of the equation
	 * @param b formula on one side of the equation
	 * @throws IOException
	 */
	public void printEquivalence( FolFormula a, FolFormula b) throws IOException ;

	/**
	 * Prints formatted representation of a knowledge base.
	 * @param b
	 *            a knowledge base
	 */
	public void printBase(FolBeliefSet b) throws IOException ;
	
	/**
	 * Closes the Writer
	 * @throws IOException
	 */
	public void close() throws IOException;

}
