package net.sf.tweety.arg.aba.semantics;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import net.sf.tweety.arg.aba.ABATheory;
import net.sf.tweety.arg.aba.syntax.Assumption;
import net.sf.tweety.commons.Formula;

public class AttackRelation <T extends Formula> {
	
	Map<Assumption<T>, Set<Assumption<T>>> atters = new HashMap<>(), atteds = new HashMap<>();
	

	public AttackRelation(ABATheory<T> abat) {
		
	}

	
}
