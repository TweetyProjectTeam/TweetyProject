package net.sf.tweety.arg.dung.test;

import java.io.FileNotFoundException;
import java.io.IOException;

import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.dung.StratifiedLabelingReasoner;
import net.sf.tweety.arg.dung.parser.*;
import net.sf.tweety.arg.dung.semantics.Semantics;
import net.sf.tweety.arg.dung.semantics.StratifiedLabeling;
import net.sf.tweety.commons.ParserException;

/**
 * A small CLI for testing stratified labelings
 * @author Matthias Thimm
 */
public class StratifiedLabelingsCli {
	
	public static int getSemantics(String sem){
		if(sem.equals("stable")) return Semantics.STABLE_SEMANTICS;
		if(sem.equals("grounded")) return Semantics.GROUNDED_SEMANTICS;
		if(sem.equals("preferred")) return Semantics.PREFERRED_SEMANTICS;
		if(sem.equals("complete")) return Semantics.COMPLETE_SEMANTICS;
		throw new IllegalArgumentException("Unrecognized semantics, use one of {stable, grounded, preferred, complete}");
	}
	
	public static void main(String[] args) throws FileNotFoundException, ParserException, IOException{
		DungParser parser = new DungParser();
		DungTheory theory = (DungTheory) parser.parseBeliefBaseFromFile(args[0]);
		
		StratifiedLabelingReasoner reasoner = new StratifiedLabelingReasoner(theory,StratifiedLabelingsCli.getSemantics(args[1]), Semantics.CREDULOUS_INFERENCE);
		
		
		System.out.println("Argumentation framework:\n" + theory);
		
		System.out.println("\nStratified " + args[1] + " labelings:");
		
		for(StratifiedLabeling labeling: reasoner.getLabelings()){
			System.out.println(labeling);			
		}
		if(reasoner.getLabelings().isEmpty())
			System.out.println("no labelings found.");
		
		
	
	}
}
