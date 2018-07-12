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
 package net.sf.tweety.arg.dung.writer;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.arg.dung.syntax.Attack;

/**
 * Writes an abstract argumentation framework into a file of 
 * the TGF format.
 * 
 * @author Matthias Thimm
 */
public class TgfWriter extends AbstractDungWriter {

	/* (non-Javadoc)
	 * @see argc.writer.Writer#write(net.sf.tweety.arg.dung.DungTheory, java.io.File)
	 */
	@Override
	public void write(DungTheory aaf, File f) throws IOException {
		PrintWriter writer = new PrintWriter(f, "UTF-8");
		for(Argument a: aaf)
			writer.println(a.getName());
		writer.println("#");
		for(Attack att: aaf.getAttacks())
			writer.println(att.getAttacker().getName() + " " + att.getAttacked().getName());		
		writer.close();		
	}

}
