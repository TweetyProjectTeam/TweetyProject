package net.sf.tweety.lp.asp.beliefdynamics.revision;

import static org.junit.Assert.assertEquals;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import net.sf.tweety.lp.asp.parser.ASPParser;
import net.sf.tweety.lp.asp.parser.InstantiateVisitor;
import net.sf.tweety.lp.asp.parser.ParseException;
import net.sf.tweety.lp.asp.solver.DLVComplex;
import net.sf.tweety.lp.asp.syntax.DLPAtom;
import net.sf.tweety.lp.asp.syntax.DLPNeg;
import net.sf.tweety.lp.asp.syntax.Program;
import net.sf.tweety.lp.asp.syntax.Rule;
import net.sf.tweety.lp.asp.util.AnswerSet;
import net.sf.tweety.lp.asp.util.AnswerSetList;
import net.sf.tweety.lp.asp.beliefdynamics.revision.CredibilityRevision;

import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tests the revision method described in Krümpelmann et al. 2008. 
 * The two examples of Krümpelmann et al. 2008 are used. 
 * @author Tim Janus
 */
public class CredibilityTest {

	/** The logger used for output in the angerona Framework */
	static private Logger LOG = LoggerFactory.getLogger(CredibilityTest.class);
	
	private CredibilityRevision revision;
	
	private String pre = "/net/sf/tweety/logicprogramming/asplibrary/examples/";
	
	@Before
	public void initTest() {
		String path = System.getenv().get("DLVCOMPLEX_PATH");
		if(path != null) {
			if(new File(path).exists()) {
				revision = new CredibilityRevision(
						new DLVComplex(path));
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
		
		
		ASPParser parser = new ASPParser(stream);
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
			
			List<Collection<Rule>> programs = new LinkedList<Collection<Rule>>();
			programs.add(p1);
			programs.add(p2);
			programs.add(p3);
			
			Program reval = revision.revise(programs);
			assertEquals(res, reval);
			
			AnswerSetList asl = revision.getLastProjectedAnswerSet();
			assertEquals(1, asl.size());
			AnswerSet as = asl.get(0);
			assertEquals(2, as.size());
			assertEquals(true, as.contains(new DLPAtom("b")));
			assertEquals(true, as.contains(new DLPNeg("a")));
			assertEquals(false, as.contains(new DLPAtom("a")));
		}
	}
	
	@Test
	public void testKrue2008Ex2() {
		if(revision != null) {
			Program p1 = loadFromJar("Krue2008_Ex2_Part1.dl");
			Program p2 = loadFromJar("Krue2008_Ex2_Part2.dl");
			
			if(p1 == null || p2 == null)
				return; // skip tests cause missing data
			
			List<Collection<Rule>> programs = new LinkedList<Collection<Rule>>();
			programs.add(p1);
			programs.add(p2);
			
			revision.revise(programs);
			
			AnswerSetList asl = revision.getLastProjectedAnswerSet();
			assertEquals(2, asl.size());
			
			for(AnswerSet as : asl) {
				assertEquals(3, as.size());
				assertEquals(true, as.contains(new DLPAtom("b")));
				assertEquals(true, as.contains(new DLPAtom("c")));
			}
			
			if(asl.get(0).contains(new DLPAtom("a"))) {
				assertEquals(true, asl.get(1).contains(new DLPNeg("a")));
			} else {
				assertEquals(true, asl.get(1).contains(new DLPAtom("a")));
			}
		}
	}
}
