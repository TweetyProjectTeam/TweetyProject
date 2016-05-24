package net.sf.tweety.arg.aspic;

import org.junit.Test;

import net.sf.tweety.arg.aspic.parser.AspicParser;

public class AspicTest {

	@Test
	public void test() throws Exception {

		AspicParser parser = new AspicParser();
		String input = "=>s\n=>u \n=>x\n ->p\n->x \n p => q\n p->v \ns=>t\n u =>v\nu,x=>not t\n s=> not  p\np,q->r\nv->not\ts";
		AspicTheory at = parser.parseBeliefBase(input);
		System.out.println(at);
		
	}

}
