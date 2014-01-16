package net.sf.tweety.lp.asp.syntax;

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import net.sf.tweety.logics.commons.syntax.Variable;
import net.sf.tweety.logics.commons.syntax.interfaces.Term;

/**
 * Encapsulates the possible values for a dlv complex
 * list. A dlv complex list can be represented either as
 * (head|tail) where head is a single term and tail is a
 * Variable or a List or it can be represented as [v1,...,vn]
 * describing an orderered sequence of n values.
 * The method usesHeadTailsSyntax() can be used to determine the
 * representation type of the ListTermValue.
 * 
 * @author Tim Janus
 */
public class ListTermValue {
	private Term<?> head;
	private Term<?> tail;
	private List<Term<?>> list;
	
	public ListTermValue() {
		list = new LinkedList<Term<?>>();
	}
	
	public ListTermValue(ListTermValue other) {
		if(other.usesHeadTailSyntax()) {
			this.head = other.head.clone();
			this.tail = other.tail.clone();
		} else {
			list = new LinkedList<Term<?>>();
			for(Term<?> t : other.list) {
				list.add(t.clone());
			}
		}
	}
	
	public ListTermValue(Term<?> head, Term<?> tail) {
		this.head = head;
		if( !(tail instanceof Variable) &&
			!(tail instanceof ListTerm)) {
			throw new IllegalArgumentException();
		}
		this.tail = tail;
	}
	
	public ListTermValue(Collection<? extends Term<?>> terms) {
		list = new LinkedList<Term<?>>(terms);
	}
	
	public Term<?> head() {
		return usesHeadTailSyntax() ? head : (list.size() > 0 ? list.get(0) : null);
	}
	
	/**
	 * @return A Variable or a ListTerm representing the tail.
	 */
	public Term<?> tail() {
		return tail;
	}
	
	public List<Term<?>> list() {
		return list;
	}
	
	public boolean usesHeadTailSyntax() {
		return head != null;
	}
	
	@Override
	public int hashCode() {
		if(head != null) {
			return (head.hashCode() + tail.hashCode()) * 13;
		} else {
			return list.hashCode() * 13;
		}
	}
	
	@Override
	public boolean equals(Object other) {
		if(!(other instanceof ListTermValue))
			return false;
		ListTermValue ltv = (ListTermValue)other;
		if(ltv.usesHeadTailSyntax() != usesHeadTailSyntax())
			return false;
		if(usesHeadTailSyntax()) {
			return ltv.head.equals(head) && ltv.tail.equals(tail);
		} else {
			return ltv.list.equals(list);
		}
	}
	
	@Override
	public ListTermValue clone() {
		return new ListTermValue(this);
	}
	
	@Override
	public String toString() {
		if(head != null) {
			return "[" + head.toString() + "|" + tail.toString() + "]";
		} else {
			return list.toString();
		}
	}
}
