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
package org.tweetyproject.lp.asp.beliefdynamics.revision;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import org.tweetyproject.lp.asp.parser.ASPCore2Parser;
import org.tweetyproject.lp.asp.parser.InstantiateVisitor;
import org.tweetyproject.lp.asp.parser.ParseException;
import org.tweetyproject.lp.asp.reasoner.DLVSolver;
import org.tweetyproject.lp.asp.semantics.AnswerSet;
import org.tweetyproject.lp.asp.syntax.Program;
import org.tweetyproject.lp.asp.syntax.ASPAtom;
import org.tweetyproject.lp.asp.syntax.StrictNegation;
import org.tweetyproject.lp.asp.syntax.ASPRule;
import org.tweetyproject.lp.asp.beliefdynamics.revision.CredibilityRevision;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests the revision method described in Kruempelmann et al. 2008. 
 * The two examples of Kruempelmann et al. 2008 are used. 
 * @author Tim Janus
 */
public class CredibilityTest {

	/** The logger used for output in the angerona Framework */
	static private Logger LOG = LoggerFactory.getLogger(CredibilityTest.class);
	
	private CredibilityRevision revision;
	
	private String pre = "/org.tweetyproject/logicprogramming/asplibrary/examples/";
	
	@Before
	public void initTest() {
		String path = System.getenv().get("DLVCOMPLEX_PATH");
		if(path != null) {
			if(new File(path).exists()) {
				revision = new CredibilityRevision(
						new DLVSolver(path)); //TODO should be DLVComplex
			}
		} else {
			LOG.error("Cannot initalize ASP unit tests, missing environment variable 'DLVCOMPLEX_PATH', skipping tests");
		}
	}
	
	private Program loadFromJar(String filename) {
		String jarPath = pre + filename;
		InputStream stream = getClass().getResourceAsStream(jarPath);
		if(stream == null) {
			LOG.error("Cannot find: '{}', skipping test", jarPath);
			return null;
		}
		
		
		ASPCore2Parser parser = new ASPCore2Parser(stream); //TODO test with new parser
		InstantiateVisitor visitor = new InstantiateVisitor();
		Program reval = new Program();
		try {
			reval = visitor.visit(parser.Program(), null);
		} catch (ParseException e) {
			LOG.error("Cannot parse: '{}' by reason: '{}', skipping test", jarPath, e.getMessage());
		}
		return reval;
	}
	
	@Test
	public void testKrue2008Ex1() {
		if(revision != null) {
			Program p1 = loadFromJar("Krue2008_Ex1_Part1.dl");
			Program p2 = loadFromJar("Krue2008_Ex1_Part2.dl");
			Program p3 = loadFromJar("Krue2008_Ex1_Part3.dl");
			Program res = loadFromJar("Krue2008_Ex1_Res.dl");
			
			if(p1 == null || p2 == null || p3 == null || res == null)
				return; // skip tests cause missing data
			
			List<Collection<ASPRule>> programs = new LinkedList<Collection<ASPRule>>();
			programs.add(p1);
			programs.add(p2);
			programs.add(p3);
			
			Program reval = revision.revise(programs);
			assertEquals(res, reval);
			
			Collection<AnswerSet> asl = revision.getLastProjectedAnswerSet();
			assertEquals(1, asl.size());
			AnswerSet as = asl.iterator().next();
			assertEquals(2, as.size());
			assertEquals(true, as.contains(new ASPAtom("b")));
			assertEquals(true, as.contains(new StrictNegation("a")));
			assertEquals(false, as.contains(new ASPAtom("a")));
		}
	}
	
	@Test
	public void testKrue2008Ex2() {
		if(revision != null) {
			Program p1 = loadFromJar("Krue2008_Ex2_Part1.dl");
			Program p2 = loadFromJar("Krue2008_Ex2_Part2.dl");
			
			if(p1 == null || p2 == null)
				return; // skip tests cause missing data
			
			List<Collection<ASPRule>> programs = new LinkedList<Collection<ASPRule>>();
			programs.add(p1);
			programs.add(p2);
			
			revision.revise(programs);
			
			Collection<AnswerSet> asl = revision.getLastProjectedAnswerSet();
			assertEquals(2, asl.size());
			
			for(AnswerSet as : asl) {
				assertEquals(3, as.size());
				assertEquals(true, as.contains(new ASPAtom("b")));
				assertEquals(true, as.contains(new ASPAtom("c")));
			}
			
			Iterator<AnswerSet> aslit = asl.iterator();
			AnswerSet asl1 = aslit.next();
			AnswerSet asl2 = aslit.next();
			
			if(asl1.contains(new ASPAtom("a"))) {
				assertEquals(true, asl2.contains(new StrictNegation("a")));
			} else {
				assertEquals(true, asl2.contains(new ASPAtom("a")));
			}
		}
	}
}
