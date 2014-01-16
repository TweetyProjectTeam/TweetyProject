package net.sf.tweety.lp.asp.solver;

import java.io.StringReader;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.sf.tweety.lp.asp.parser.ASPParser;
import net.sf.tweety.lp.asp.parser.InstantiateVisitor;
import net.sf.tweety.lp.asp.syntax.Program;
import net.sf.tweety.lp.asp.util.AnswerSetList;


public class Clingo extends SolverBase {

	protected String path2clingo = null;
	
	public Clingo(String path2clingo) {
		this.path2clingo = path2clingo;
	}
	
	
	private AnswerSetList parseAnswerSets(String s) {		
		// use parser:
		try {
			ASPParser ep = new ASPParser( new StringReader( s ));
			InstantiateVisitor visitior = new InstantiateVisitor();
			return visitior.visit(ep.AnswerSetList(), null);
		} catch (Exception e) {
			System.err.println("clingo: error parsing answer set!");
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public AnswerSetList computeModels(Program p, int maxModels) throws SolverException {
		checkSolver(path2clingo);
		try {
			ai.executeProgram( path2clingo+" "+maxModels, p.toStringFlat());
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		this.checkErrors();
		return this.buildASL(ai.getOutput());
	}

	
	@Override
	public AnswerSetList computeModels(String s, int maxModels) throws SolverException {
		checkSolver(path2clingo);
		try {
			ai.executeProgram( path2clingo, s );
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		this.checkErrors();
		return this.buildASL(ai.getOutput());
	}

	
	@Override
	public AnswerSetList computeModels(List<String> files, int maxModels) throws SolverException {
		checkSolver(path2clingo);
		try {			
			LinkedList<String> f2 = new LinkedList<String>(files);
			f2.addFirst(path2clingo);
			ai.executeProgram(f2, null);
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		this.checkErrors();
		return this.buildASL(ai.getOutput());				
	}
	
	
	@Override
	protected void checkErrors() throws SolverException {
		// process possible errors and throw exception
		if (ai.getError().size() > 0) {
			// skip any warning, anything else is critical!
			Iterator<String> iter = ai.getError().iterator();
			while (iter.hasNext()) {
				String l = iter.next();
				
				if (l.startsWith("% warning"))
					; // TODO: Find a warning policy like logging it at least.
				
				if (l.startsWith("ERROR:")) {
					if (iter.hasNext())
						l += iter.next();
					
					throw new SolverException( l, SolverException.SE_ERROR );
				}
					
			}
		}
	}
	
	
	protected AnswerSetList buildASL(List<String> output) {
		// process output and return answer set
		// First convert in dlv format:
		String toParse = "";
		for(String line : output) {
			String as = line.replace('.', ',');
			as = "{" + as + "}";
			as = as.replace(",}", "}");
			toParse += as;
		}
		return parseAnswerSets(toParse);
	}
}
