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
 package net.sf.tweety.arg.dung.parser;

import java.util.Collection;
import java.util.HashSet;
import java.util.StringTokenizer;

/**
 * This enum lists all supported file formats.
 * @author Matthias Thimm
 */
public enum FileFormat {
	TGF ("tgf", "Trivial Graph format"),
	APX ("apx", "Aspartix format"),
	CNF ("cnf", "Conjunctive Normal Form format");
	//LPG ("lpg", "Logic Programming format"),
	//AIF ("aif", "Argument Interchange format"),	
	//UAI ("uai", "UAI 2010 format");
	
	/** The extension of the file format */
	private String extension;	
	/** The description of the file format. */
	private String description;
	
	/**
	 * Creates a new FileFormat
	 * @param extension the extension of the file format
	 * @param description some description
	 */
	private FileFormat(String extension, String description){
		this.extension = extension;
		this.description = description;
	}
	
	/**
	 * Returns the extension of the file format.
	 * @return the extension of the file format.
	 */
	public String extension(){
		return this.extension;
	}
	
	/**
	 * Returns the description of the file format.
	 * @return the description of the file format.
	 */
	public String description(){
		return this.description;
	}	
	
	/* (non-Javadoc)
	 * @see java.lang.Enum#toString()
	 */
	public String toString(){
		return this.extension;
	}
	
	/**
	 * Returns the file format instance that corresponds to the given abbrevation
	 * @param s some string describing a file format
	 * @return the file format instance corresponding to the string.
	 */
	public static FileFormat getFileFormat(String s){
		s = s.trim();
		for(FileFormat f: FileFormat.values())
			if(s.toLowerCase().equals(f.extension()))
				return f;
		if(s.toLowerCase().equals("aspartix"))
			return APX;
		if(s.toLowerCase().equals("trivial"))
			return TGF;
		return null;
	}
	
	/**
	 * Returns a collection of file formats parsed from the given string which
	 * has to be in the format "[format1,...,formatn]".
	 * @param s some string
	 * @return a collection of file formats
	 */
	public static Collection<FileFormat> getFileFormats(String s){
		s = s.trim();
		if(!s.startsWith("[") || !s.endsWith("]"))
			throw new IllegalArgumentException("Malformed file format specification: " + s);
		s = s.substring(1, s.length()-1);
		StringTokenizer tokenizer = new StringTokenizer(s, ",");
		Collection<FileFormat> formats = new HashSet<FileFormat>();
		while(tokenizer.hasMoreTokens()){
			formats.add(FileFormat.getFileFormat(tokenizer.nextToken()));
		}
		return formats;
	}
}
