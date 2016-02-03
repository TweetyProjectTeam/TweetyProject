/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
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
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
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
