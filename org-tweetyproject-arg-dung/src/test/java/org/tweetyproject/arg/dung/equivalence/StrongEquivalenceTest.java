package org.tweetyproject.arg.dung.equivalence;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.tweetyproject.arg.dung.equivalence.kernel.EquivalenceKernel;
import org.tweetyproject.arg.dung.syntax.*;

class StrongEquivalenceTest {

	@Test
	void testIsEquivalentDungTheoryDungTheory() {
		//Arrange
		var af1 = new DungTheory();
		var a1 = new Argument("a1");
		var a2 = new Argument("a2");
		var a3 = new Argument("a3");
		af1.add(a1);
		af1.add(a2);
		af1.add(a3);
		af1.add(new Attack(a2, a2));
		af1.add(new Attack(a3, a2));
		
		var af2 = new DungTheory();
		var a4 = new Argument("a1");
		var a5 = new Argument("a2");
		var a6 = new Argument("a3");
		af2.add(a4);
		af2.add(a5);
		af2.add(a6);
		af2.add(new Attack(a5, a5));
		af2.add(new Attack(a5, a6));
		af2.add(new Attack(a6, a5));
		
		var kernel = EquivalenceKernel.ADMISSIBLE;
		var strongEQ = new StrongEquivalence(EquivalenceKernel.ADMISSIBLE);
		
		//Act
		var k1 = kernel.getKernel(af1);
		var k2 = kernel.getKernel(af2);
		
		//Assert
		Assert.assertTrue(k1.equals(k2));
		Assert.assertTrue(strongEQ.isEquivalent(af1, af2));
		
	}

}
