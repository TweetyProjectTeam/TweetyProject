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
 *  Copyright 2022 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
 package org.tweetyproject.arg.dung.writer;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import org.tweetyproject.arg.dung.syntax.Attack;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.syntax.Argument;;

/**
 * Writes an abstract argumentation framework into a file of the
 * ICCMA23 format, see https://iccma2023.github.io/rules.html#input-format.<br/>
 * <br/>
 * NOTE: this format is a slight variation of the CNF format (see CnfWriter)
 * 
 * @author Matthias Thimm
 */
public class Iccma23Writer extends AbstractDungWriter{

	private Map<Argument,Integer> map;
	private Argument[] rev_map;
	
	/* (non-Javadoc)
	 * @see org.tweetyproject.arg.dung.writer.AbstractDungWriter#write(org.tweetyproject.arg.dung.DungTheory, java.io.File)
	 */
	@Override
	public void write(DungTheory aaf, File f) throws IOException {
		PrintWriter writer = new PrintWriter(f, "UTF-8");
		writer.println("p af " + aaf.size());
		this.map = new HashMap<Argument,Integer>();
		this.rev_map = new Argument[aaf.size()];
		int idx = 1;
		for(Argument arg: aaf) {
			this.rev_map[idx-1] = arg;
			this.map.put(arg, idx++);			
		}
		for(Attack att: aaf.getAttacks())
			writer.println(this.map.get(att.getAttacker()) + " " + this.map.get(att.getAttacked()));		
		writer.close();		
	}
	
	/**
	 * Returns the mapping of the given argument wrt. the last
	 * written file.
	 * @param arg some argument
	 * @return the argument id
	 */
	public int getArgumentId(Argument arg) {
		return this.map.get(arg);			
	}
	
	/**
	 * Returns the argument mapped to the given integer wrt.
	 * the last written file
	 * @param i some index
	 * @return the argument
	 */
	public Argument getArgument(int i) {
		return this.rev_map[i-1];
	}
}
