package net.sf.tweety.arg.dung.test;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileReader;

import org.junit.Test;

import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.dung.parser.AbstractDungParser;
import net.sf.tweety.arg.dung.prover.ProboSolver;
import net.sf.tweety.arg.dung.prover.constants.FileFormat;
import net.sf.tweety.arg.dung.prover.constants.Problem;
import net.sf.tweety.arg.dung.writer.DungWriter;
import net.sf.tweety.commons.util.Shell;

public class ProboTest {

	static Shell shell = Shell.getCygwinShell("C:\\Windows\\System32\\bash.exe");
	

	@Test
	public void ParserTest() throws Exception {
		AbstractDungParser parser = AbstractDungParser.getParser(FileFormat.TGF);
		DungTheory af = parser.parse(new FileReader(new File("../../examples/dung/ex1.tgf")));

		assertTrue(af.getAttacks().size() == 6);
		assertTrue(af.getNodes().size() == 4);

		parser = AbstractDungParser.getParser(FileFormat.APX);
		af = parser.parseBeliefBase(new FileReader(new File("../../examples/dung/ex1.apx")));

		assertTrue(af.getAttacks().size() == 6);
		assertTrue(af.getNodes().size() == 4);

	}

	@Test
	public void WriterTest() throws Exception {
		AbstractDungParser parser = AbstractDungParser.getParser(FileFormat.TGF);
		DungTheory af = parser.parse(new FileReader(new File("../../examples/dung/ex1.tgf")));

		DungWriter writer = DungWriter.getWriter(FileFormat.APX);
		// System.out.println(writer.writeArguments(af));

	}

	@Test
	public void HeurekaTest() throws Exception {
		ProboSolver solver = new ProboSolver("/mnt/c/Users/nils/git/MA/bin/heureka", shell);
		assertTrue(solver.versionInfo().charAt(0) == 'h');
		assertTrue(solver.supportedFormats().size() == 2);
		assertTrue(solver.supportedProblems().size() == 14);
		System.out.println(
				solver.solve(Problem.EE_ST, new File("/mnt/c/Users/nils/svn/tweety/trunk/examples/dung/ex3.tgf"), FileFormat.TGF, ""));
		
		AbstractDungParser parser = AbstractDungParser.getParser(FileFormat.TGF);
		DungTheory aaf = parser.parseBeliefBaseFromFile("C:/Users/nils/svn/tweety/trunk/examples/dung/ex3.tgf");
		System.out.println(aaf);
		System.out.println(
				solver.solve(Problem.EE_ST, aaf, FileFormat.TGF, ""));
	}

}
