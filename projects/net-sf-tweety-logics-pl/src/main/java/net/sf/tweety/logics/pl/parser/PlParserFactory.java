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
package net.sf.tweety.logics.pl.parser;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import net.sf.tweety.commons.Parser;
import net.sf.tweety.logics.pl.PlBeliefSet;

/**
 * Manages and creates different parsers for propositional logic.
 * @author Matthias Thimm
 */
public class PlParserFactory {
	
	/** An enumeration of all available knowledge base formats. */
	public enum Format{
		TWEETY ("tweety", "Tweety Propositional Beliefset Format", "/format/TweetyPlFormat.html");
		
		public String id;
		public String label;
		public String description;
		
		Format(String id, String label, String description){
			this.id = id;
			this.label = label;
			// for some reason this is the only way it works for
			// both running the thing in Eclipse and from a JAR
			boolean worked = true;
			try {
				InputStream stream = getClass().getResourceAsStream(description);				
				BufferedReader br = new BufferedReader(new InputStreamReader(stream));
				String line;
				this.description = "";
				while((line = br.readLine()) != null) {
					this.description += line + " ";
				}
				br.close();
			} catch (Exception e) {
				worked = false;
			}	
			if(!worked){
				try {
					InputStream stream = getClass().getResourceAsStream("/resources" + description);				
					BufferedReader br = new BufferedReader(new InputStreamReader(stream));
					String line;
					this.description = "";
					while((line = br.readLine()) != null) {
						this.description += line + " ";
					}
					br.close();
				} catch (Exception e) {
					this.description = "<Description not found>";
				}
			}
		}
		
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
	public static Parser<PlBeliefSet> getParserForFormat(Format f){
		if(f.equals(Format.TWEETY))
			return new PlParser();
		return null;
	}
}
