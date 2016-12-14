package net.sf.tweety.arg.aba.syntax;

import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.util.rules.Rule;

/**
 * @author Nils Geilen <geilenn@uni-koblenz.de>
 *	An interface that combines assumptions and inference rules
 * @param <T>	is the type of the language that the ABA theory's rules range over 
 */
public interface ABARule<T extends Formula> extends Rule<T, T> {

	boolean isAssumption();

}
