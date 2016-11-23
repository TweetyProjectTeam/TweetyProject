package net.sf.tweety.arg.aba.syntax;

import net.sf.tweety.commons.util.rules.Rule;
import net.sf.tweety.logics.commons.syntax.interfaces.Invertable;

/**
 * @author Nils Geilen <geilenn@uni-koblenz.de>
 *	An interface that combines assumptions and inference rules
 * @param <T>	is the type of the language that the ABA theory's rules range over 
 */
public interface ABARule<T extends Invertable> extends Rule<T, T> {

	boolean isAssumption();
}
