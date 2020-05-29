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
 *  Copyright 2019 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.arg.adf.reasoner.sat.encodings;

import java.util.Collection;
import java.util.LinkedList;
import java.util.function.Consumer;

import net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework;
import net.sf.tweety.logics.pl.syntax.Disjunction;

/**
 * 
 * @author Mathias Hofer
 *
 */
public interface SatEncoding {

	void encode(Consumer<Disjunction> consumer, PropositionalMapping mapping, AbstractDialecticalFramework adf);
	
	default Collection<Disjunction> encode(PropositionalMapping mapping, AbstractDialecticalFramework adf) {
		Collection<Disjunction> clauses = new LinkedList<>();
		encode(clauses::add, mapping, adf);
		return clauses;
	}
	
	default void encode(Collection<Disjunction> collection, PropositionalMapping mapping, AbstractDialecticalFramework adf) {
		encode(collection::add, mapping, adf);
	}

}
