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
 *  Copyright 2020 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.logics.bpm.syntax;

/**
 * A class to represent elements of a BPMN Model
 * @author Benedikt Knopp
 */
public abstract class BpmnElement extends BpmnFormula {
	
	/**
	 * the unique identifier of the BPMN element
	 */
	private String id;
	
	/**
	 * the name of the BPMN element
	 */
	private String name;
	
	/**
	 * Create a new BPMN element
	 */
	public BpmnElement() {}
	
	/**
	 * set the element's ID
	 * @param id the unique element identifier
	 */
	public void setId(String id) {
		this.id = id;
	}
	
	/**
	 * set the element's name
	 * @param name the element's name
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * retrieve the element's ID
	 * @return the unique element identifier
	 */
	public String getId() {
		return id;
	}
	
	/**
	 * retrieve the element's name
	 * @return the element's name
	 */
	public String getName() {
		return name;
	}
	
}
