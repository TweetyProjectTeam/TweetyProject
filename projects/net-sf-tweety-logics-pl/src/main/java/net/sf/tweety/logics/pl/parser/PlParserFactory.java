package net.sf.tweety.logics.pl.parser;

import java.io.File;

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
		public File description;
		
		Format(String id, String label, String description){
			this.id = id;
			this.label = label;
			this.description = new File(getClass().getResource(description).getFile());
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
