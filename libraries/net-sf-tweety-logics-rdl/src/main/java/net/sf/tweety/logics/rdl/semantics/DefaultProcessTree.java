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
package net.sf.tweety.logics.rdl.semantics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import net.sf.tweety.logics.rdl.syntax.DefaultRule;
import net.sf.tweety.logics.rdl.syntax.DefaultTheory;

/**
 * Computes the extensions of a default theory
 * 
 * @author Nils Geilen
 * @author Matthias Thimm
 */
public class DefaultProcessTree {
	
	/**
	 * all processes of the process tree
	 */
	Collection<DefaultSequence> processes = new ArrayList<>();
	
	/**
	 * all extensions of the process tree
	 */
	Collection<Extension> extensions = new HashSet<>();
	
	/**
	 * constructs a default process tree out of the default theory t
	 * @param t a default theory
	 */
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
			extensions.add(new Extension(seq.getIn()));
		
	}

	/**
	 * @return all processes (sequences of defaults from root to leaf)
	 */
	public Collection<DefaultSequence> getProcesses() {
		return processes;
	}

	/**
	 * @return all extensions (possible sets of facts)
	 */
	public Collection<Extension> getExtensions() {
		return extensions;
	}

	


}
