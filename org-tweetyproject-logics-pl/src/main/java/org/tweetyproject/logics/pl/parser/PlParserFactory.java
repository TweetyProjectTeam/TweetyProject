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
package org.tweetyproject.logics.pl.parser;

import org.tweetyproject.commons.Parser;
import org.tweetyproject.logics.pl.syntax.PlBeliefSet;
import org.tweetyproject.logics.pl.syntax.PlFormula;

/**
 * Manages and creates different parsers for propositional logic.
 * @author Matthias Thimm
 */
public class PlParserFactory {
	
	/** An enumeration of all available knowledge base formats. */
	public enum Format{
		/**TWEETY*/
		TWEETY ("tweety", "TweetyProject Propositional Beliefset Format");
		/**ID*/
		public String id;
		/**label*/
		public String label;
		
		Format(String id, String label){
			this.id = id;
			this.label = label;
		}
		/**
		 * 
		 * @param id ID
		 * @return format
		 */
		public static Format getFormat(String id){
			for(Format f: Format.values())
				if(f.id.equals(id))
					return f;
			return null;
		}
	}

	/**
	 * Retrieves an appropriate parser for the given format.
	 * @param f some format
	 * @return a parser
	 */
	public static Parser<PlBeliefSet,PlFormula> getParserForFormat(Format f){
		if(f.equals(Format.TWEETY))
			return new PlParser();
		return null;
	}

    /** Default Constructor */
    public PlParserFactory(){}
}
