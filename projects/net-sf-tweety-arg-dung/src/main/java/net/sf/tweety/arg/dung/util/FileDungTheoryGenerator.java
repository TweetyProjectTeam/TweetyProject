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
package net.sf.tweety.arg.dung.util;

import java.io.File;
import java.io.FileReader;

import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.commons.Parser;

/**
 * This generator receives a list of files containing Dung theories
 * and returns those step by step.
 * 
 * @author Matthias Thimm
 */
public class FileDungTheoryGenerator implements DungTheoryGenerator {

	/** The files containing Dung theories. */
	private File[] files;
	/** A parser for parsing the files. */
	private Parser<DungTheory> parser;
	/** The index of the next theory to be returned. */
	private int idx;
	
	/**
	 * Creates a new theory generator for the given files, which
	 * can be parsed by the given parser.
	 * @param files an array of files.
	 * @param parser a parser for the files.
	 */
	public FileDungTheoryGenerator(File[] files, Parser<DungTheory> parser){
		this.files = files;
		this.parser = parser;
		this.idx = 0;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.dung.util.DungTheoryGenerator#generate()
	 */
	@Override
	public DungTheory generate() {
		if(this.idx >= this.files.length)
			this.idx = 0;
		try {
			return this.parser.parseBeliefBase(new FileReader(this.files[idx++]));
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.dung.util.DungTheoryGenerator#generate(net.sf.tweety.arg.dung.syntax.Argument)
	 */
	@Override
	public DungTheory generate(Argument arg) {
		// not supported
		throw new UnsupportedOperationException();
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.dung.util.DungTheoryGenerator#setSeed(long)
	 */
	@Override
	public void setSeed(long seed) {
		// Nothing to do
	}

}
