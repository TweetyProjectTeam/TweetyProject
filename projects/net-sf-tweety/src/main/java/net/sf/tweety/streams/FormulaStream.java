package net.sf.tweety.streams;

import java.util.Iterator;

import net.sf.tweety.Formula;

/**
 * This interface models a stream on formulas.
 * 
 * @author Matthias Thimm
 *
 * @param <S> The type of formulas
 */
public interface FormulaStream<S extends Formula> extends Iterator<S> {

	/* (non-Javadoc)
	 * @see java.util.Iterator#hasNext()
	 */
	@Override
	public boolean hasNext();

	/* (non-Javadoc)
	 * @see java.util.Iterator#next()
	 */
	@Override
	public S next();

	/* (non-Javadoc)
	 * @see java.util.Iterator#remove()
	 */
	@Override
	public void remove();

}
