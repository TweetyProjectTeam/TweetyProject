package net.sf.tweety.logics.fol.writer;

import java.io.IOException;

import net.sf.tweety.logics.fol.FolBeliefSet;
import net.sf.tweety.logics.fol.syntax.FolFormula;

public interface FolWriter {
	
	/**
	 * Creates a TPTP conjecture.
	 * 
	 * @param name
	 *            the identifying name of the conjecture
	 * @param query
	 *            the formula to be queried
	 * @return the query as TPTP
	 */
	public void printQuery(FolFormula query) throws IOException;
	
	public void printEquivalence( FolFormula a, FolFormula b) throws IOException ;

	/**
	 * Prints TPTP representation of a knowledge base to w.
	 * 
	 * @param w
	 *            a writer
	 * @param b
	 *            a knowledge base
	 */
	public void printBase(FolBeliefSet b) throws IOException ;
	
	public void close() throws IOException;

}
