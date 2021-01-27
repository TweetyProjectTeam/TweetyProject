/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.preferences;

import java.util.*;

import org.tweetyproject.commons.util.Triple;
import org.tweetyproject.preferences.ranking.LevelingFunction;
import org.tweetyproject.preferences.ranking.RankingFunction;

/**
 * This class extends the BinaryRelation-class with a check for totality and
 * transitivity
 * 
 * @author Bastian Wolf
 * 
 * @param <T>
 *            the generic type of objects/pairs in this preference order
 */

// TODO: Equals-Methode schreiben

public class PreferenceOrder<T> implements BinaryRelation<T> {

	/**
	 * a given set of Triples
	 */
	private Set<Triple<T, T, Relation>> relations;

	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
	// ------- Constructor ------------------------------------------------

	/**
	 * Creates an empty HashSet of preference order.
	 */
	public PreferenceOrder() {
		this(new HashSet<Triple<T, T, Relation>>());
	}

	/**
	 * generates a preference order with a given set of elements
	 * 
	 * @param relations
	 *            the set of given element pairs
	 */
	public PreferenceOrder(
			Collection<? extends Triple<T, T, Relation>> relations) {
		this.relations = new HashSet<Triple<T, T, Relation>>(relations);
	}

	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
	// ------- Getter & Setter --------------------------------------------

	/**
	 * returns the ranking function for this preference order
	 * 
	 * @return the ranking function for this preference order
	 */
	public LevelingFunction<T> getLevelingFunction() {
		return new LevelingFunction<T>(this);
	}

	// %%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%%
	// ------- Methods ----------------------------------------------------

	/**
	 * adds a given pair of generic elements to the set.
	 * 
	 * @param t the given set
	 * @return true if successful, false if not
	 */
	@Override
	public boolean add(Triple<T, T, Relation> t) {
		return this.relations.add(t);
	}

	/**
	 * adds two given (single) elements as pair into the set
	 * 
	 * @param f
	 *            first element of the new pair
	 * @param s
	 *            second element of the new pair
	 * @param relation the relation
	 * @return true if successful, false if not
	 */
	public boolean addPair(T f, T s, Relation relation) {
		return this.add(new Triple<T, T, Relation>(f, s, relation));
	}

	/**
	 * (re-)computes a set of single elements in this preference order
	 */
	public Set<T> getDomainElements() {
		Set<T> domainElements = new HashSet<T>();

		for (Triple<T, T, Relation> t : relations) {
			domainElements.add(t.getFirst());
			domainElements.add(t.getSecond());
		}

		return domainElements;
	}

	/**
	 * removes specific pair of the set
	 * 
	 * @param o
	 *            the pair to be removed
	 * @return true if successful, false if not
	 */
	@Override
	public boolean remove(Object o) {
		if (this.contains(o))
			return relations.remove(o);
		else
			return false;
	}

	/**
	 * returns whether the set is empty or not
	 * 
	 * @return true if empty, false if not
	 */
	public boolean isEmpty() {
		return (this.relations.isEmpty());
	}

	/**
	 * returns whether the elements a and b are related
	 * 
	 * @param a
	 *            the first element to be checked
	 * @param b
	 *            the second element to be checked
	 * @return true if related, false if not.
	 */

	public boolean isRelated(T a, T b) {
		for (Triple<T, T, Relation> t : relations) {
			if (t.getFirst() == a) {
				if (t.getSecond() == b) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * returns an iterator over a set of triples
	 * @param s a set of triples
	 * 
	 * @return an iterator over a set of triples
	 */

	public Iterator<Triple<T, T, Relation>> iterator(
			Set<Triple<T, T, Relation>> s) {
		return s.iterator();
	}

	/**
	 * checks existence and returns a demanded pair
	 * 
	 * @param e
	 *            the demanded pair
	 * @return a pair if it exists, null otherwise
	 */
	public Triple<T, T, Relation> get(Triple<T, T, Relation> e) {
		for (Triple<T, T, Relation> t : relations) {
			if (t.getFirst().toString().equals(e.getFirst().toString())) {
				if (t.getSecond().toString().equals(e.getSecond().toString())) {
					if (t.getThird().toString().equals(e.getThird().toString()))
						return t;
				}
			}
		}
		return null;
	}

	/**
	 * returns a pair if it consists of of two given elements
	 * 
	 * @param a
	 *            the first element
	 * @param b
	 *            the second element
	 * @return a pair if found, null if not
	 */
	public Triple<T, T, Relation> getTriple(T a, T b) {
		for (Triple<T, T, Relation> t : relations) {
			if (t.getFirst() == a) {
				if (t.getSecond() == b) {
					return t;
				}
			}
		}
		return null;
	}

	/**
	 * checks whether this preference order contains a pair of given elements
	 * 
	 * @param a
	 *            the first element
	 * @param b
	 *            the second element
	 * @return true if pair is in this preference order, false if not
	 */
	public boolean containsRelation(T a, T b) {
		for (Triple<T, T, Relation> t : relations) {
			if (t.getFirst() == a) {
				if (t.getSecond() == b) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * checks whether this preference order contains a given pair
	 * 
	 * @param o
	 *            the given pair
	 * @return true if pair is in this preference order, false if not
	 */
	@Override
	public boolean contains(Object o) {
		return (relations.contains(o));
	}

	/**
	 * checks whether the given triple is contained
	 * @param firstElement the first element
	 * @param secondElement the second element
	 * @param relation the relation
	 * @return true iff  the given triple is contained
	 */
	public boolean containsTriple(T firstElement, T secondElement,
			Relation relation) {
		for (Triple<T, T, Relation> e : relations)
			if (e.getFirst().toString().equals(firstElement.toString())
					&& e.getSecond().toString()
							.equals(secondElement.toString())
					&& e.getThird().toString().equals(relation.toString())) {
				return true;
			}
		return false;
	}

	/**
	 * returns the size of the set
	 * 
	 * @return the size of the set
	 */
	public int size() {
		return (this.relations.size());
	}

	/**
	 * returns a String with the elements of this set
	 * 
	 * @return a String with the elements of this set
	 */
	@Override
	public String toString() {
		String s = "{";
		Iterator<Triple<T, T, Relation>> it = iterator();
		while (it.hasNext()) {
			Triple<T, T, Relation> t = it.next();
			if (it.hasNext()) {
				s += "(" + t.getFirst().toString() + ","
						+ t.getSecond().toString() + "), ";
			} else {
				s += "(" + t.getFirst().toString() + ","
						+ t.getSecond().toString() + ")";
			}
		}
		s += "}";
		return s;

	}

	/**
	 * returns an array containing all objects
	 * 
	 * @return the Object[]-array
	 */
	public Object[] toArray() {
		Set<Triple<T, T, Relation>> elements = new HashSet<Triple<T, T, Relation>>();
		elements.addAll(relations);
		return elements.toArray();
	}

	/**
	 * returns all elements in an array
	 * 
	 * @param a
	 *            is a given array
	 * @return an array
	 */
	@SuppressWarnings("hiding")
	@Override
	public <T> T[] toArray(T[] a) {
		return this.getDomainElements().toArray(a);
	}

	/**
	 * checks whether the set is total or not
	 * 
	 * @return true if total, false otherwise
	 */
	public boolean isTotal() {
		for (final T f : getDomainElements()) {
			for (final T s : getDomainElements()) {
				if (f != s && !isRelated(f, s) && !isRelated(s, f))
					return false;
			}
		}
		return true;
	}

	/**
	 * checks whether the given set is transitive or not
	 * 
	 * @return true if transitive, false otherwise
	 */
	public boolean isTransitive() {
		for (final T a : getDomainElements()) {
			for (final T b : getDomainElements()) {
				for (final T c : getDomainElements()) {
					if (a != b && b != c && a != c && isRelated(a, b)
							&& isRelated(b, c) && !isRelated(a, c)) {
						return false;
					}
				}
			}
		}
		return true;
	}

	/**
	 * checks whether the given set represents a valid preference order
	 * 
	 * @return true if valid, false if not
	 */
	public boolean isValid() {
		return (isTotal() && isTransitive());
	}

	/**
	 * clears the current preference order element set
	 */
	@Override
	public void clear() {
		relations.clear();
	}

	/**
	 * checks, whether all of the given elements are contained in the preference
	 * order
	 * 
	 * @return true iff all elements are contained, false otherwise
	 */
	@Override
	public boolean containsAll(Collection<?> c) {
		Iterator<?> it = c.iterator();
		while (it.hasNext()) {
			Object e = it.next();
			if (!this.contains(e)) {
				return false;
			}
		}
		return true;
	}

	/**
	 * removes all given elements from the preference order
	 * 
	 * @return true if elements-set has changed, false if not
	 */
	@Override
	public boolean removeAll(Collection<?> c) {
		throw new RuntimeException("Method not correctly implemented, please check source code");
		//Check the following code! In the while loop the element "e" from the iterator is not used.
		/*
		Iterator<?> it = c.iterator();
		Set<Triple<T, T, Relation>> tempRel = new HashSet<Triple<T, T, Relation>>();
		while (it.hasNext()) {
			Object e = it.next();
			for (Triple<T, T, Relation> a : relations) {
				if (!c.contains(a))
					tempRel.add(a);
			}
		}
		if (tempRel.equals(this.relations)) {
			return false;
		}
		this.relations = tempRel;
		return true;*/
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Set#retainAll(java.util.Collection)
	 */
	// TODO equality-check testen
	@Override
	public boolean retainAll(Collection<?> c) {
		Iterator<?> it = c.iterator();
		Set<Triple<T, T, Relation>> tempRel = new HashSet<Triple<T, T, Relation>>();
		while (it.hasNext()) {
			Object e = it.next();
			for (Triple<T, T, Relation> a : relations) {
				if (a.toString().equals(e.toString())) {
					tempRel.add(a);
				}
			}
		}
		if (tempRel.equals(c)) {
			return false;
		}
		relations = tempRel;
		return true;
	}

	/**
	 * adds all given elements to the preference order
	 * 
	 * @return true if element-set changed, false if not
	 */
	@Override
	public boolean addAll(Collection<? extends Triple<T, T, Relation>> c) {
		boolean changed = false;
		Set<Triple<T, T, Relation>> tempRel = this.relations;
		for (Triple<T, T, Relation> t : c) {
			if (!tempRel.contains(t)) {
				tempRel.add(t);
				changed = true;
			}
		}
		return changed;
	}

	/* (non-Javadoc)
	 * @see java.util.Set#iterator()
	 */
	@Override
	public Iterator<Triple<T, T, Relation>> iterator() {
		return relations.iterator();
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof PreferenceOrder))
			return false;
		PreferenceOrder<?> po = (PreferenceOrder<?>) obj;
		if (!this.getClass().equals(po.getClass())) {
			return false;
		}
		if (!this.getDomainElements().equals(po.getDomainElements())) {
			return false;
		}
		if (!this.getLevelingFunction().equals(po.getLevelingFunction())) {
			return false;
		}
		return true;
	}

	/**
	 * compares this preference order to another given one whether each relation
	 * is contained in both
	 * 
	 * @param po
	 *            the preference order to compare with
	 * @return true if both are equal, false if not
	 */
	public boolean compareEqualityWith(PreferenceOrder<T> po) {

		for (Triple<T, T, Relation> f : po) {
			if (!(this
					.containsTriple(f.getFirst(), f.getSecond(), f.getThird()))) {
				return false;
			}
		}
		return true;
	}

	/**
	 * weakens the given element in this preference order in its leveling
	 * function
	 * 
	 * @param element
	 *            the element to be weaken
	 */
	public void weakenElementInLF(T element) {
		LevelingFunction<T> tempLF = getLevelingFunction();
		tempLF.weakenElement(element);
		relations = tempLF.generatePreferenceOrder();
	}

	/**
	 * strengthens the given element in this preference order in its leveling
	 * function
	 * 
	 * @param element
	 *            the element to be strengthen
	 */
	public void strengthenElementInLF(T element) {
		LevelingFunction<T> tempLF = getLevelingFunction();
		tempLF.strengthenElement(element);
		relations = tempLF.generatePreferenceOrder();
	}

	/**
	 * weakens the given element in this preference order in its ranking
	 * function
	 * 
	 * @param element
	 *            the element to be weaken
	 */
	public void weakenElementInRF(T element) {
		RankingFunction<T> tempRF = getLevelingFunction().getRankingFunction();
		tempRF.weakenElement(element);
		relations = tempRF.generatePreferenceOrder();
	}

	/**
	 * strengthens the given element in this preference order in its ranking
	 * function
	 * 
	 * @param element
	 *            the element to be strengthen
	 */
	public void strengthenElementInRF(T element) {
		RankingFunction<T> tempRF = getLevelingFunction().getRankingFunction();
		tempRF.strengthenElement(element);
		relations = tempRF.generatePreferenceOrder();
	}
}