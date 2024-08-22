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

import java.util.Collection;

import org.tweetyproject.commons.Signature;

/**
 * @author Benedikt Knopp
 */
public class BpmnSignature implements Signature{

	@Override
	public boolean isSubSignature(Signature other) {
		return false;
	}

	@Override
	public boolean isOverlappingSignature(Signature other) {
		return false;
	}

	@Override
	public void addSignature(Signature other) {

	}

	@Override
	public void add(Object obj) {

	}

	@Override
	public void addAll(Collection<?> c) {

	}

	@Override
	public boolean isEmpty() {
		return false;
	}

	@Override
	public void remove(Object obj) {

	}

	@Override
	public void removeAll(Collection<?> c) {

	}

	@Override
	public void clear() {

	}

	@Override
	public Signature clone() {
		return null;
	}

	@Override
	public void add(Object... objects) {
		
	}


    /** Default Constructor */
    public BpmnSignature(){}
}
