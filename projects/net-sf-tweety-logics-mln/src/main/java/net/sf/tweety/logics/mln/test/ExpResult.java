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
package net.sf.tweety.logics.mln.test;

import java.io.Serializable;

import net.sf.tweety.logics.mln.MarkovLogicNetwork;
import net.sf.tweety.logics.mln.analysis.AbstractCoherenceMeasure;

public class ExpResult implements Serializable{

	private static final long serialVersionUID = -3397914383589483136L;

	public AbstractCoherenceMeasure coherenceMeasure;
	public MarkovLogicNetwork mln;
	public double[][] domain2Coherence;
	
}
