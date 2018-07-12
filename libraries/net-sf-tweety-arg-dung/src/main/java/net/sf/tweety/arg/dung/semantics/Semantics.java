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
 package net.sf.tweety.arg.dung.semantics;

/**
 * This enum lists all semantics.
 * @author Matthias Thimm
 */
public enum Semantics {
	CF ("conflict-free semantics", "CF"),
	ADM ("admissible semantics", "ADM"),
	CO ("complete semantics", "CO"),
	GR ("grounded semantics", "GR"),
	PR ("preferred semantics", "PR"),
	ST ("stable semantics", "ST"),
	STG ("stage semantics", "STG"),
	SST ("semi-stable semantics", "SST"),
	ID ("ideal semantics", "ID"),
	CF2 ("CF2 semantics", "CF2"),
	diverse ("diverse semantics", "div");
	
	public static final Semantics GROUNDED_SEMANTICS = GR,
		STABLE_SEMANTICS = ST,
		PREFERRED_SEMANTICS = PR,
		COMPLETE_SEMANTICS = CO,
		ADMISSIBLE_SEMANTICS = ADM,
		CONFLICTFREE_SEMANTICS = CF,
		SEMISTABLE_SEMANTICS = SST,
		IDEAL_SEMANTICS = ID,
		STAGE_SEMANTICS = STG,
		CF2_SEMANTICS = CF2;
	
	/**
	 * inference types to be used wtih different semantics
	 */
	public static final int SCEPTICAL_INFERENCE = 0x1,
		CREDULOUS_INFERENCE = 0x2;


		
	/** The description of the semantics. */
	private String description;
	/** The abbreviation of the semantics. */
	private String abbreviation;
	
	/**
	 * Creates a new semantics.
	 * @param description some description
	 */
	private Semantics(String description, String abbreviation){
		this.description = description;
		this.abbreviation = abbreviation;
	}
	
	/**
	 * Returns the description of the semantics.
	 * @return the description of the semantics.
	 */
	public String description(){
		return this.description;
	}
	
	/**
	 * Returns the abbreviation of the semantics.
	 * @return the abbreviation of the semantics.
	 */
	public String abbreviation(){
		return this.abbreviation;
	}
}
