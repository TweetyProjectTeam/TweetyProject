package net.sf.tweety.logics.fol.test;

import java.io.StringWriter;

import org.junit.Test;

import net.sf.tweety.logics.fol.FolBeliefSet;
import net.sf.tweety.logics.fol.parser.FolParser;
import net.sf.tweety.logics.fol.writer.Prover9Writer;

public class Prover9Test {

	@Test
	public void test() throws Exception{
		Prover9Writer p9w = new Prover9Writer();
		StringWriter strw = new StringWriter();
		FolParser parser = new FolParser();	
		String source = "Animal = {horse, cow, lion} \n"
				+ "type(Tame(Animal)) \n"
				+ "type(Ridable(Animal)) \n"
				+ "Tame(cow) \n"
				+ "!Tame(lion) \n"
				+ "Ridable(horse) \n"
				+ "forall X: (!Ridable(X) || Tame(X)) \n";
		FolBeliefSet b = parser.parseBeliefBase(source);
		System.out.println(p9w.toTPTP(b));
	}

}
