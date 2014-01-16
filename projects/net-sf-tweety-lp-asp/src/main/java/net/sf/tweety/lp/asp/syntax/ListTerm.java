package net.sf.tweety.lp.asp.syntax;

import java.util.Collection;
import java.util.Iterator;

import net.sf.tweety.logics.commons.syntax.TermAdapter;
import net.sf.tweety.logics.commons.syntax.interfaces.Term;

/**
 * this class models a list term that can be used for
 * dlv complex programs.
 * 
 * @author Tim Janus
 * @author Thomas Vengels
 *
 */
public class ListTerm extends TermAdapter<ListTermValue> {
		
	public ListTerm() {
		super(new ListTermValue());
	}
	
	public ListTerm(ListTerm other) {
		super(other.value.clone());
	}
	
	public ListTerm(ListTermValue value) {
		super(value);
	}
	
	/**
	 * constructor for list elements with given [head|tail].
	 * @param head
	 * @param tail
	 */
	public ListTerm(Term<?> head, Term<?> tail) {
		super(new ListTermValue(head, tail));
	}

	@Override
	public ListTerm clone() {
		return new ListTerm(this);
	}
	
	protected String listPrint(Collection<Term<?>> tl) {
		String ret = "";
		Iterator<Term<?>> iter = tl.iterator();
		if (iter.hasNext())
			ret += iter.next().toString();
		while (iter.hasNext())
			ret += ", " + iter.next().toString();
		return ret;
	}
}
