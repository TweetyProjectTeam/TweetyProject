package net.sf.tweety;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

/**
 * This class models a belief set, i.e. a set of formulae
 * of some formalism.
 * 
 * @author Matthias Thimm
 * @author Tim Janus
 * 
 * @param <T> The type of the beliefs in this belief set.
 */
public abstract class BeliefSet<T extends Formula> implements BeliefBase, Collection<T> {

	/**
	 * The set of formulas of this belief base.
	 */
	private Set<T> formulas;
	
	/**
	 * Creates a new (empty) belief set.
	 */
	public BeliefSet(){
		this(new HashSet<T>());
	}
	
	/**
	 * Creates a new belief set with the given collection of
	 * formulae.
	 * @param c a collection of formulae.
	 */
	public BeliefSet(Collection<? extends T> c){
		this.formulas = instantiateSet();
		this.formulas.addAll(c);
	}
	
	/**
	 * instantiates the set which is used as data holder for the belief set.
	 * Subclasses might override this method if the do not want to use HashSet
	 * as container implementation
	 */
	protected Set<T> instantiateSet() {
		return new HashSet<T>();
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.kr.BeliefBase#getSignature()
	 */
	@Override
	public abstract Signature getSignature();
	
	/* (non-Javadoc)
	 * @see java.util.Collection#add(java.lang.Object)
	 */
	@Override
	public boolean add(T f){
		return this.formulas.add(f);
	}
	
	/* (non-Javadoc)
	 * @see java.util.Collection#addAll(java.util.Collection)
	 */
	@Override
	public boolean addAll(Collection<? extends T> c){
		return this.formulas.addAll(c);
	}
	
	/* (non-Javadoc)
	 * @see java.util.Collection#clear()
	 */
	@Override
	public void clear(){
		this.formulas.clear();
	}
	
	/* (non-Javadoc)
	 * @see java.util.Collection#contains(java.lang.Object)
	 */
	@Override
	public boolean contains(Object o){
		return this.formulas.contains(o);
	}
	
	/* (non-Javadoc)
	 * @see java.util.Collection#containsAll(java.util.Collection)
	 */
	@Override
	public boolean containsAll(Collection<?> c){
		return this.formulas.containsAll(c);
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((formulas == null) ? 0 : formulas.hashCode());
		return result;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		BeliefSet<?> other = (BeliefSet<?>) obj;
		if (formulas == null) {
			if (other.formulas != null)
				return false;
		} else if (!formulas.equals(other.formulas))
			return false;
		return true;
	}
	
	/* (non-Javadoc)
	 * @see java.util.Collection#isEmpty()
	 */
	@Override
	public boolean isEmpty(){
		return this.formulas.isEmpty();
	}
	
	/* (non-Javadoc)
	 * @see java.util.Collection#iterator()
	 */
	@Override
	public Iterator<T> iterator(){
		return this.formulas.iterator();
	}

	/* (non-Javadoc)
	 * @see java.util.Collection#remove(java.lang.Object)
	 */
	@Override
	public boolean remove(Object o){
		return this.formulas.remove(o);
	}
	
	/* (non-Javadoc)
	 * @see java.util.Collection#removeAll(java.util.Collection)
	 */
	@Override
	public boolean removeAll(Collection<?> c){
		return this.formulas.removeAll(c);
	}
	
	/* (non-Javadoc)
	 * @see java.util.Collection#retainAll(java.util.Collection)
	 */
	@Override
	public boolean retainAll(Collection<?> c){
		return this.formulas.retainAll(c);
	}
	
	/* (non-Javadoc)
	 * @see java.util.Collection#size()
	 */
	@Override
	public int size(){
		return this.formulas.size();
	}
	
	/* (non-Javadoc)
	 * @see java.util.Collection#toArray()
	 */
	@Override
	public Object[] toArray(){
		return this.formulas.toArray();
	}
	
	/* (non-Javadoc)
	 * @see java.util.Collection#toArray(T[])
	 */
	@Override
	public <S> S[] toArray(S[] a) {
		return this.formulas.toArray(a);
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.kr.BeliefBase#toString()
	 */
	@Override
	public String toString() {
		String s = "{ ";
		Iterator<T> it = this.iterator();
		if(it.hasNext())
			s += it.next();
		while(it.hasNext())
			s += ", " + it.next();
		s += " }";
		return s;
	}
}
