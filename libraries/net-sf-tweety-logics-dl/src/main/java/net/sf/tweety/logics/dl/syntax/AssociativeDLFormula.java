package net.sf.tweety.logics.dl.syntax;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Set;

import net.sf.tweety.commons.Signature;
import net.sf.tweety.logics.commons.syntax.AssociativeFormulaSupport.AssociativeSupportBridge;
import net.sf.tweety.logics.commons.syntax.AssociativeFormulaSupport;
import net.sf.tweety.logics.commons.syntax.Predicate;
import net.sf.tweety.logics.commons.syntax.interfaces.AssociativeFormula;
import net.sf.tweety.logics.commons.syntax.interfaces.SimpleLogicalFormula;

/**
 * This class captures the common functionalities of description logic associative formulas 
 * (union and intersection).
 * 
 * @author Anna Gessler
 * @author Matthias Thimm
 * @author Tim Janus
 */
public abstract class AssociativeDLFormula extends DlFormula
		implements AssociativeFormula<DlFormula>, AssociativeSupportBridge {

	/**
	 * This helper class implements most of the common functionality of an associative
	 * formula, so the implementation can delegate the method calls to the support
	 * class. 
	 */
	protected AssociativeFormulaSupport<DlFormula> support;
	
	/**
	 * Creates a new (empty) associative formula.
	 */
	public AssociativeDLFormula(){
		this.support = new AssociativeFormulaSupport<DlFormula>(this);
	}
	
	/**
	 * Creates a new associative formula with the two given formulae
	 * @param first a relational formula.
	 * @param second a relational formula.
	 */
	public AssociativeDLFormula(DlFormula first, DlFormula second){
		this();
		this.add(first);
		this.add(second);
	}
	
	/**
	 * Creates a new associative formula with the given inner formulas. 
	 * @param formulas a collection of formulas.
	 */
	public AssociativeDLFormula(Collection<? extends DlFormula> formulas) {
		this();
		this.addAll(formulas);
	}

	@Override
	public Signature createEmptySignature() {
		return new DlSignature();
	}
	
	@Override
	public DlSignature getSignature() {
		DlSignature sig = new DlSignature();
		sig.addAll(this.getFormulas());
		return sig;
	}

	@Override
	public Set<Predicate> getPredicates() {
		Set<Predicate> ps = new HashSet<Predicate>();
		for (DlFormula d : this.getFormulas())
			ps.addAll(d.getPredicates());
		return ps;
	}

	@Override
	public boolean isLiteral() {
		return false;
	}
	
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((support == null) ? 0 : support.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		AssociativeDLFormula other = (AssociativeDLFormula) obj;
		if (support == null) {
			if (other.support != null)
				return false;
		} else if (!support.equals(other.support))
			return false;
		return true;
	}	
	
	//-------------------------------------------------------------------------
	//	METHODS IMPLEMENTED IN AssociativeFormulaSupport:
	//-------------------------------------------------------------------------
		
	@Override
	public List<DlFormula> getFormulas() {
		return support.getFormulas();
	}
	
	@Override
	public <C extends SimpleLogicalFormula> Set<C> getFormulas(Class<C> cls) {
		Set<C> reval = new HashSet<C>();
		for(DlFormula rf : support) {
			if(rf.getClass().equals(cls)) {
				@SuppressWarnings("unchecked")
				C cast = (C)rf;
				reval.add(cast);
			}
		}
		return reval;
	}
	
	@Override
	public String toString() {
		return support.toString();
	}
	
	@Override
	public boolean add(DlFormula e) {
		return this.support.add(e);
	}

	@Override
	public boolean addAll(Collection<? extends DlFormula> c) {
		return this.support.addAll(c);
	}

	@Override
	public void clear() {
		this.support.clear();
	}

	@Override
	public boolean contains(Object o) {
		return this.support.contains(o);
	}

	@Override
	public boolean containsAll(Collection<?> c) {
		return this.support.containsAll(c);
	}

	@Override
	public boolean isEmpty() {
		return this.support.isEmpty();
	}

	@Override
	public Iterator<DlFormula> iterator() {
		return this.support.iterator();
	}

	@Override
	public boolean remove(Object o) {
		return this.support.remove(o);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return this.support.removeAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return this.support.retainAll(c);
	}

	@Override
	public int size() {
		return this.support.size();
	}

	@Override
	public Object[] toArray() {
		return this.support.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return this.support.toArray(a);
	}
	
	@Override
	public void add(int index, DlFormula element) {
		this.support.add(index, element);
	}

	@Override
	public boolean addAll(int index, Collection<? extends DlFormula> c) {
		return this.support.addAll(index, c);
	}

	@Override
	public DlFormula get(int index) {
		return this.support.get(index);
	}

	@Override
	public int indexOf(Object o) {
		return this.support.indexOf(o);
	}

	@Override
	public int lastIndexOf(Object o) {
		return this.support.lastIndexOf(o);
	}

	@Override
	public ListIterator<DlFormula> listIterator() {
		return this.support.listIterator();
	}

	@Override
	public ListIterator<DlFormula> listIterator(int index) {
		return this.support.listIterator(index);
	}

	@Override
	public DlFormula remove(int index) {
		return this.support.remove(index);
	}

	@Override
	public DlFormula set(int index, DlFormula element) {
		return this.support.set(index, element);
	}

	@Override
	public List<DlFormula> subList(int fromIndex, int toIndex) {
		return this.support.subList(fromIndex, toIndex);
	}
	
}
