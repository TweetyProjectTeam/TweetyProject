package net.sf.tweety.arg.aba.syntax;

import net.sf.tweety.commons.util.rules.Rule;
import net.sf.tweety.logics.commons.syntax.interfaces.Invertable;

public interface ABARule<T extends Invertable> extends Rule<T, T> {
	boolean isAssumption();
}
