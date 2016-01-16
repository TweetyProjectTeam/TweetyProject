package net.sf.tweety.logics.rdl.semantics;

import java.util.ArrayList;
import java.util.List;

import net.sf.tweety.logics.rdl.DefaultTheory;
import net.sf.tweety.logics.rdl.syntax.DefaultRule;

public class DefaultProcessTree {
	
	List<DefaultSequence> extensions = new ArrayList<>();
	
	public DefaultProcessTree(DefaultTheory t) {
		t = t.extend();
		List<DefaultSequence> seqs_old  = new ArrayList();
		List<DefaultSequence> seqs_new  = new ArrayList();
		seqs_old.add(new DefaultSequence(t)) ;
		System.out.println(t.getDefaults().size());
		for(int i=0;i<t.getDefaults().size();i++) {
			for(DefaultSequence seq_old: seqs_old) {
				for(DefaultRule d: t.getDefaults()){
					DefaultSequence seq_new = seq_old.app(d);
					if(seq_new.isProcess())
					if(seq_new.isSuccessful())
						if(seq_new.isClosed(t)) 
							extensions.add(seq_new);
						else
							seqs_new.add(seq_new);
				}
			}
			seqs_old = seqs_new;
			seqs_new  = new ArrayList();
		}
		
	}

	public List<DefaultSequence> getExtensions() {
		return extensions;
	}


}
