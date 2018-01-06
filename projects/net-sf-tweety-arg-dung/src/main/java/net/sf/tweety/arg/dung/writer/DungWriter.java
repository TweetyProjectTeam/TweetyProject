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
 package net.sf.tweety.arg.dung.writer;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.dung.parser.FileFormat;
import net.sf.tweety.arg.dung.semantics.ArgumentStatus;
import net.sf.tweety.arg.dung.semantics.Labeling;
import net.sf.tweety.arg.dung.syntax.Argument;

/**
 * Writes an abstract argumentation framework into a file
 * of a specific format.
 * 
 * @author Matthias Thimm
 */
public abstract class DungWriter {
	
	/**
	 * Retrieves the writer for the given file format.
	 * @param f some file format
	 * @return a writer or null if the format is not supported.
	 */
	public static DungWriter getWriter(FileFormat f){
		if(f.equals(FileFormat.TGF))
			return new TgfWriter();
		if(f.equals(FileFormat.APX))
			return new ApxWriter();
		return null;
	}
	
	/**
	 * Writes the given collection of arguments into a string of the form
	 * [arg1,...,argn].
	 * @param args some collection of arguments
	 * @return a string representation of the collection of arguments
	 */
	public static String writeArguments(Collection<Argument> args){
		String s = "[";
		boolean first = true;
		for(Argument a: args){
			if(first){
				s += a.getName();
				first = false;
			}else{
				s += "," + a.getName();
			}
		}
		s += "]";
		return s;
	}
	
	/**
	 * Writes a labeling in the form [[IN1,...,INM],[OUT1,...,OUTN],[UNDEC1,...,UNDECM]]
	 * @param l some labeling 
	 * @return a string representing the labeling.
	 */
	public static String writeLabeling(Labeling l){
		String result = "[[";
		boolean first = true;
		for(Argument a: l.getArgumentsOfStatus(ArgumentStatus.IN))
			if(first){
				result += a.getName();
				first = false;
			}else result += "," + a.getName();
		result += "],["; 
		first = true;
		for(Argument a: l.getArgumentsOfStatus(ArgumentStatus.OUT))
			if(first){
				result += a.getName();
				first = false;
			}else result += "," + a.getName();
		result += "],[";
		first = true;
		for(Argument a: l.getArgumentsOfStatus(ArgumentStatus.UNDECIDED))
			if(first){
				result += a.getName();
				first = false;
			}else result += "," + a.getName();
		result +="]]";
		return result;
	}
	
	/**
	 * Writes the given file into an abstract argumentation framework
	 * @param aaf an abstract argumentation framework
	 * @param f the file that will be overwritten. 
	 * @throws IOException for all errors concerning file reading/writing.
	 */
	public abstract void write(DungTheory aaf, File f) throws IOException;
}
