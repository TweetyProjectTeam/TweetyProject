package net.sf.tweety.arg.dung.writer;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.dung.syntax.Attack;
import net.sf.tweety.arg.dung.syntax.Argument;;

/**
 * Writes an abstract argumentation framework into a file of the
 * CNF format. Note that the order of the arguments may change
 * by using this writer.
 * 
 * @author Matthias Thimm
 */
public class CnfWriter extends DungWriter{

	/* (non-Javadoc)
	 * @see net.sf.probo.writer.Writer#write(net.sf.tweety.arg.dung.DungTheory, java.io.File)
	 */
	@Override
	public void write(DungTheory aaf, File f) throws IOException {
		PrintWriter writer = new PrintWriter(f, "UTF-8");
		writer.println("p af " + aaf.size() + " " + aaf.getAttacks().size());
		Map<Argument,Integer> map = new HashMap<Argument,Integer>();
		int idx = 1;
		for(Argument arg: aaf)
			map.put(arg, idx++);
		for(Attack att: aaf.getAttacks())
			writer.println(map.get(att.getAttacker()) + " -" + map.get(att.getAttacked()) + " 0");		
		writer.close();		
	}
}
