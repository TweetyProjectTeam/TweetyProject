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
package net.sf.tweety.logics.el.writer;

import net.sf.tweety.commons.Writer;
import net.sf.tweety.logics.el.ModalBeliefSet;
import net.sf.tweety.logics.fol.syntax.RelationalFormula;

public class ModalWriter extends Writer {

	public ModalWriter(RelationalFormula formula) {
		super(formula);
	}
	
	public ModalWriter(ModalBeliefSet beliefSet) {
		super(beliefSet);
	}

	@Override
	public String writeToString() {
		return this.getObject().toString();
	}

}
