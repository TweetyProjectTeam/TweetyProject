package org.tweetyproject.arg.dung.serialisibility.syntax;

import java.util.LinkedList;

import org.tweetyproject.arg.dung.syntax.DungTheory;

public class ReducedFrameworksSequence extends LinkedList<DungTheory>{

	private static final long serialVersionUID = 1L;
	
	public ReducedFrameworksSequence(DungTheory origin, SerialisationSequence sequence) {
		add(origin.clone());
		var tempAF = origin;
		for(var ext : sequence) {
			tempAF = tempAF.getReduct(ext);
			add(tempAF);
		}
	}
	
	@Override
	public boolean add(DungTheory element) {
		if(this.contains(element)) {
			return false;
		}
		
		return super.add(element);
	}
	
	@Override
	public void add(int index, DungTheory element) {
		if(this.contains(element)) {
			return;
		}
		
		super.add(index, element);
		return;
	}

}
