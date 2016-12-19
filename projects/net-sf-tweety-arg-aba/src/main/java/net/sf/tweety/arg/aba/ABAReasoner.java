package net.sf.tweety.arg.aba;

import java.util.Collection;

import net.sf.tweety.arg.aba.syntax.Assumption;
import net.sf.tweety.commons.Formula;

public interface ABAReasoner <T extends Formula> {

	public Collection<Collection<Assumption<T>>> computeExtensions();

}
