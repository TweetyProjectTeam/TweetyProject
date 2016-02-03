/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
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
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.agents;

/**
 * A protocol gives instructions in which order agents have to be asked
 * for actions in a multi-agent system. 
 * @author Matthias Thimm
 */
public interface Protocol {

	/**
	 * Adds the given listener to this protocol.
	 * @param listener a protocol listener.
	 */
	public void addProtocolListener(ProtocolListener listener);
	
	/**
	 * Removes the given protocol listener from this protocol.
	 * @param listener a protocol listener.
	 * @return "true" if the listener has been removed.
	 */
	public boolean removeProtocolListener(ProtocolListener listener);
}
