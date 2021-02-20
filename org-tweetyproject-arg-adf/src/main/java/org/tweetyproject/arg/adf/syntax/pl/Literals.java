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
package org.tweetyproject.arg.adf.syntax.pl;

import java.util.Objects;

/**
 * @author Mathias Hofer
 *
 */
class Literals {
	
	static final class TransientAtom implements Literal {

		@Override
		public String getName() {
			return null;
		}
		
		@Override
		public boolean isTransient() {
			return true;
		}

		@Override
		public boolean isPositive() {
			return true;
		}

		@Override
		public Literal getAtom() {
			return this;
		}

		@Override
		public Literal neg() {
			return new Negation(this);
		}
		
	}
	
	static final class NamedAtom implements Literal {

		private final String name;
		
		NamedAtom(String name) {
			this.name = Objects.requireNonNull(name);
		}
		
		@Override
		public boolean isTransient() {
			return false;
		}
		
		@Override
		public boolean isPositive() {
			return true;
		}
		
		@Override
		public String getName() {
			return name;
		}
		
		@Override
		public Literal getAtom() {
			return this;
		}

		@Override
		public String toString() {
			return getName();
		}
		
		@Override
		public Literal neg() {
			return new Negation(this);
		}
		
	}
	
	static final class UnnamedAtom implements Literal {
		
		@Override
		public String getName() {
			return null;
		}
		
		@Override
		public boolean isTransient() {
			return false;
		}

		@Override
		public boolean isPositive() {
			return true;
		}

		@Override
		public Literal getAtom() {
			return this;
		}

		@Override
		public Literal neg() {
			return new Negation(this);
		}
		
	}
	
	static final class Negation implements Literal {
		
		private final Literal atom;
		
		Negation(Literal atom) {
			this.atom = atom;
		}

		@Override
		public boolean isPositive() {
			return !atom.isPositive();
		}

		@Override
		public Literal getAtom() {
			return atom;
		}

		@Override
		public String getName() {
			return "-" + atom.getName();
		}
		
		@Override
		public boolean isTransient() {
			return atom.isTransient();
		}

		@Override
		public Literal neg() {
			return atom;
		}
		
	}

}
