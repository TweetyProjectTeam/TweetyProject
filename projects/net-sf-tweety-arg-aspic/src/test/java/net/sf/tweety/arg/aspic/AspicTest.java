package net.sf.tweety.arg.aspic;

import org.junit.Test;

import net.sf.tweety.arg.aspic.parser.AspicParser;

public class AspicTest {

	@Test
	public void test() throws Exception {

		AspicParser parser = new AspicParser();
		String input = "=>s\n=>u \n=>x\n ->p\n->x \n d1: p => q\n p->v \ns=>t\n t=> - d1\nu =>v\nu,x=>- t\n s=> -p\np,q->r\nv->-\ts";
		AspicTheory at = parser.parseBeliefBase(input);
		System.out.println(at);
		
	}

}
