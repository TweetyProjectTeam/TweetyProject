package net.sf.tweety.logics.rdl.semantics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import net.sf.tweety.logics.fol.syntax.FolFormula;
import net.sf.tweety.logics.rdl.DefaultTheory;
import net.sf.tweety.logics.rdl.syntax.DefaultRule;

/**
 * Computes the extensions of a default theory
 * 
 * @author Nils Geilen
 *
 */
public class DefaultProcessTree {
	
	Collection<DefaultSequence> processes = new ArrayList<>();
	Collection<Collection<FolFormula>> extensions = new HashSet<>();
	
	public DefaultProcessTree(DefaultTheory t) {
		t = t.ground();
		List<DefaultSequence> seqs_old  = new ArrayList<>();
		List<DefaultSequence> seqs_new  = new ArrayList<>();
		seqs_old.add(new DefaultSequence(t)) ;
		while(!seqs_old.isEmpty()){
			for(DefaultSequence seq_old: seqs_old) {
				for(DefaultRule d: t.getDefaults()){
					DefaultSequence seq_new = seq_old.app(d);
					if(seq_new.isProcess())
					if(seq_new.isSuccessful())
						if(seq_new.isClosed(t)) 
							processes.add(seq_new);
						else
							seqs_new.add(seq_new);
				}
			}
			seqs_old = seqs_new;
			seqs_new  = new ArrayList<>();
		}
		for(DefaultSequence seq: processes)
			extensions.add(seq.getIn());
		
	}

	public Collection<DefaultSequence> getProcesses() {
		return processes;
	}

	public Collection<Collection<FolFormula>> getExtensions() {
		return extensions;
	}

	


}
