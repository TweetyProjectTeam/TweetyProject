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
package net.sf.tweety.logics.pl.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.StringTokenizer;

import net.sf.tweety.commons.BeliefSetIterator;
import net.sf.tweety.logics.pl.parser.PlParser;
import net.sf.tweety.logics.pl.syntax.PlBeliefSet;
import net.sf.tweety.logics.pl.syntax.PlFormula;

/**
 * Enumerates all belief bases from a text file; the file contains one
 * belief base per line.
 * 
 * @author Matthias Thimm
 *
 */
public class TextfileIterator implements BeliefSetIterator<PlFormula,PlBeliefSet>{
	
	/** For reading the file */
	private BufferedReader file_reader;
	
	/** the last read line */
	private String currentLine;
	
	/** for parsing belief bases */
	private PlParser parser;
	
	/** Creates a new iterator based on the given file.
	 * @param pathToFile path to a text file containing belief bases (one per line)
	 * 
	 */
	public TextfileIterator(String pathToFile) {
		this.parser = new PlParser();
		try {
			this.file_reader = new BufferedReader(new FileReader(pathToFile));
			this.currentLine = this.file_reader.readLine();
		} catch (IOException e) {
			this.file_reader = null;
		}
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.BeliefSetIterator#hasNext()
	 */
	@Override
	public boolean hasNext() {
		return this.file_reader != null && this.currentLine != null;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.commons.BeliefSetIterator#next()
	 */
	@Override
	public PlBeliefSet next() {
		if(!this.hasNext())
			throw new NoSuchElementException("All belief sets already read.");
		PlBeliefSet bs;
		try {
			String s = this.currentLine.substring(this.currentLine.indexOf("{") + 1, this.currentLine.lastIndexOf("}"));
	    	StringTokenizer tokenizer = new StringTokenizer(s,",");
	    	bs = new PlBeliefSet();
	    	while(tokenizer.hasMoreTokens()) {
	    		String token = tokenizer.nextToken();
	    		if(token.trim().equals(""))
	    			continue;
	    		bs.add((PlFormula) this.parser.parseFormula(token));
	    	}
			this.currentLine = this.file_reader.readLine();			
		} catch (IOException e) {
			throw new NoSuchElementException("All belief sets already read.");
		}
		if(this.currentLine == null) {
			try {
				this.file_reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			this.file_reader = null;
		}
		return bs;
	}
	
	/**
	 * Close file reader (in case no more belief bases should be read)
	 * 
	 * @throws IOException
	 */
	public void close() throws IOException {
		this.file_reader.close();
	}

}
