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
 *  Copyright 2021 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.logics.petri.syntax;

/**
 * A class to describe places in a Petri net
 * @author Benedikt Knopp
 */
public class Place extends PetriNetNode implements Comparable<Place> {

	/**
	* the current number of tokens at this place,
	* at a certain time point in an instance of the Petri net
	*/
	private int tokens = 0;
	/**
	* a static constant to describe infinity
	*/
	private final int INFINITY = -1;
	/**
	* the maximal number of tokens that this place can hold
	*/
	private int maxtokens = INFINITY;
	/**
	 * (optional) some designated purpose, e.g. as initial or final place
	 */
	private PlaceType placeType;

	/**
	 * Create a new instance
	 * @param id a unique identifier
	 */
	public Place(String id) {
		super(id);
	}

	/**
	 * Create a new instance
	 * @param id a unique identifier
	 * @param name a pretty name
	 */
	public Place(String id, String name) {
		super(id, name);
	}

	/**
	 *
	 * Return the id
	 * @return the id
	 */
	public String getId() {
		return id;
	}

	/**
	 * setter id
	 * @param id the id to set
	 */
	public void setId(String id) {
		this.id = id;
	}

	/**
	* Return the number of tokens currently at this place
	* @return the number of tokens currently at this place
	*/
	public int getTokens() {
		return tokens;
	}

	/**
	 * setter tokens
	 * @param tokens the number of tokens currently at this place
	 */
	public void setTokens(int tokens) {
		this.tokens = tokens;
	}

	/**
	 *
	* Return the maximal number of tokens that this place can hold
	* @return the maximal number of tokens that this place can hold
	*/
	public int getMaxtokens() {
		return maxtokens;
	}

	/**
	 *Setter maxtokens
	* @param maxtokens the maximal number of tokens that this place can hold
	*/
	public void setMaxtokens(int maxtokens) {
		this.maxtokens = maxtokens;
	}

	/**
	 * Add tokens
    * @param tokens the number of tokens to be added to this place
    * @throws IllegalArgumentException if this number of tokens can not be added
	*/
	public void addTokens(int tokens) {
		if(!this.canAddTokens(tokens)) {
			throw new IllegalArgumentException("An amount of " + String.valueOf(tokens)
					+ "can not be added to this place");
		}
		this.tokens += tokens;
	}

	/**
	 * Remove tokens
	* @param tokens the number of tokens to be removed from this place
	* @throws IllegalArgumentException if this number of tokens can not be removed
	*/
	public void removeTokens(int tokens) {
		if(!this.canRemoveTokens(tokens)) {
			throw new IllegalArgumentException("An amount of " + String.valueOf(tokens)
			+ "can not be removed from this place");
		}
		this.tokens -= tokens;
	}

	/**
	 * Check if tokens can be added
	 * @param tokens the number of tokens to be added to this place
	 * @return true if this number can be added with respect to the token limit and the current number of tokens
	 */
	public boolean canAddTokens(int tokens) {
		return this.maxtokens == this.INFINITY|| this.tokens + tokens <= this.maxtokens;
	}

	/**
	 * Check if tokens are removeable
	 * @param tokens the number of tokens to be removed from this place
	 * @return true if this number can be removed with respect to current number of tokens
	 */
	public boolean canRemoveTokens(int tokens) {
		return this.tokens >= tokens;
	}

	@Override
	public int compareTo(Place that) {
		// Make places comparable to
		// facilitate linear algebra operations
		return this.hashCode() - that.hashCode();
	}

	/**
	 * check if this place is a designated initial place
	 * @return true iff this place is a designated initial place
	 */
	public boolean isInitial() {
		return this.placeType == PlaceType.INITIAL;
	}

	/**
	 * specify that this place is a designated initial place
	 */
	public void setInitial() {
		this.placeType = PlaceType.INITIAL;
	}

	/**
	 * check if this place is a designated final place
	 * @return true iff this place is a designated final place
	 */
	public boolean isFinal() {
		return this.placeType == PlaceType.FINAL;
	}

	/**
	 * specify that this place is a designated final place
	 */
	public void setFinal() {
		this.placeType = PlaceType.FINAL;
	}

	/**
	 * Some special types of places
	 * @author Benedikt Knopp
	 */
	private enum PlaceType {
		/** initial */
		INITIAL,
		/** final */
		FINAL
	}
}
