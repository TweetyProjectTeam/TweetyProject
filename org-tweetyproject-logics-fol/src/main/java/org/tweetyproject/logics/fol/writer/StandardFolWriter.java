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
package org.tweetyproject.logics.fol.writer;

import java.io.IOException;
import java.io.StringWriter;
import java.io.Writer;

import org.tweetyproject.logics.commons.syntax.Constant;
import org.tweetyproject.logics.commons.syntax.Functor;
import org.tweetyproject.logics.commons.syntax.Predicate;
import org.tweetyproject.logics.commons.syntax.Sort;
import org.tweetyproject.logics.commons.syntax.interfaces.Term;
import org.tweetyproject.logics.fol.syntax.Equivalence;
import org.tweetyproject.logics.fol.syntax.FolBeliefSet;
import org.tweetyproject.logics.fol.syntax.FolFormula;
import org.tweetyproject.logics.fol.syntax.FolSignature;

/**
 * Writes FOL formulas and knowledge bases in the standard TweetyProject
 * format, see parser.FolParser.
 * 
 * @author Matthias Thimm
 *
 */
public class StandardFolWriter implements FolWriter {
	
	/**
	 * Output is redirected to this writer
	 */
	final Writer writer;
	
	/**
	 * Creates new writer
	 * @param writer output is redirected to this writer
	 */
	public StandardFolWriter(Writer writer) {
		super();
		this.writer = writer;
	}
	
	/**
	 * Creates new writer
	 */
	public StandardFolWriter() {
		super();
		this.writer = new StringWriter();
	}
	
	@Override
	public void printQuery(FolFormula query) throws IOException {
		this.writer.write(query.toString() + "\n");
		
	}

	@Override
	public void printEquivalence(FolFormula a, FolFormula b) throws IOException {
		this.writer.write(new Equivalence(a,b).toString() + "\n");
		
	}

	@Override
	public void printBase(FolBeliefSet b) throws IOException {
		FolSignature sig = b.getSignature();
		for(Sort s: sig.getSorts()) {			
			writer.write(s.getName() + " = {");
			boolean isFirst = true;
			for(Term<?> c: s.getTerms()) {
				if(!(c instanceof Constant))
					continue;
				if(isFirst) isFirst = false;
				else writer.write(",");
				writer.write(c.toString());
			}
			writer.write("}\n");
		}		
		for(Functor f: sig.getFunctors())
			writer.write("type(" + f.toString() + ")\n");
		for(Predicate p: sig.getPredicates())
			writer.write("type(" + p.toString() + ")\n");
			
		for (FolFormula f: b)
			this.writer.write(f.toString() + "\n");
		
	}

	@Override
	public void close() throws IOException {
		this.writer.close();		
	}

}
