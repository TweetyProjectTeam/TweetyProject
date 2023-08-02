package org.tweetyproject.arg.dung.serialisibility.syntax;

import java.util.HashSet;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;

class TransitionStateSequenceTest {

	@Test
	void test() {
		var a1 = new Argument("a1");
		var A1 = new Argument("a1");
		
		var a2 = new Argument("a2");
		var A2 = new Argument("a2");
		
		var af = new DungTheory();
		af.add(a1);
		af.add(a2);
		
		var Af = new DungTheory();
		Af.add(A1);
		Af.add(A2);
		
		Assert.assertFalse((af == Af));
		Assert.assertTrue(af.equals(Af));
		
		var ext = new Extension<DungTheory>();
		ext.add(a1);
		var Ext = new Extension<DungTheory>();
		Ext.add(A1);
		
		var ext2 = new Extension<DungTheory>();
		ext2.add(a2);
		var Ext2 = new Extension<DungTheory>();
		Ext2.add(A2);
		
		var seq = new SerialisationSequence();
		seq.add(ext);
		var Seq = new SerialisationSequence();
		Seq.add(Ext);
		
		var seq2 = new SerialisationSequence();
		seq2.add(ext2);
		var Seq2 = new SerialisationSequence();
		Seq2.add(Ext2);
		
		var sequences = new HashSet<SerialisationSequence>();
		sequences.add(seq);
		sequences.add(seq2);
		var Sequences = new HashSet<SerialisationSequence>();
		Sequences.add(Seq);
		Sequences.add(Seq2);
		
		Assert.assertFalse(seq == Seq);
		Assert.assertFalse(seq2 == Seq2);
		Assert.assertFalse(sequences == Sequences);
		Assert.assertTrue(seq.equals(Seq));
		Assert.assertTrue(seq2.equals(Seq2));
		Assert.assertTrue(sequences.equals(Sequences));		
		
		var reducedAF = af.getReduct(ext);
		var ReducedAF = Af.getReduct(Ext);
		
		var tstate = new TransitionState(reducedAF, ext);
		var Tstate = new TransitionState(ReducedAF, Ext);
		
		var tseq = new TransitionStateSequence(af, seq);
		var Tseq = new TransitionStateSequence(Af, Seq);
		
		var tseq2 = new TransitionStateSequence(af, seq2);
		var Tseq2 = new TransitionStateSequence(Af, Seq2);
		
		var tsequences = new HashSet<TransitionStateSequence>();
		tsequences.add(tseq);
		tsequences.add(tseq2);
		var Tsequences = new HashSet<TransitionStateSequence>();
		Tsequences.add(Tseq);
		Tsequences.add(Tseq2);
		
		Assert.assertFalse(reducedAF == ReducedAF);
		Assert.assertFalse(tstate == Tstate);
		Assert.assertFalse(tseq == Tseq);
		Assert.assertFalse(tseq2 == Tseq2);
		Assert.assertFalse(tsequences == Tsequences);
		Assert.assertTrue(reducedAF.equals(ReducedAF));
		Assert.assertTrue(tstate.equals(Tstate));
		Assert.assertTrue(tseq.equals(Tseq));
		Assert.assertTrue(tseq2.equals(Tseq2));
		Assert.assertTrue(tsequences.equals(Tsequences));
		
		/*print(af, Af, seq, Seq, seq2, Seq2, sequences, Sequences, reducedAF, ReducedAF, tstate, Tstate, tseq, Tseq,
				tseq2, Tseq2, tsequences, Tsequences);
		*/
	}
	

	@SuppressWarnings("unused")
	private static void print(DungTheory af, DungTheory Af, SerialisationSequence seq, SerialisationSequence Seq,
			SerialisationSequence seq2, SerialisationSequence Seq2, HashSet<SerialisationSequence> sequences,
			HashSet<SerialisationSequence> Sequences, DungTheory reducedAF, DungTheory ReducedAF,
			TransitionState tstate, TransitionState Tstate, TransitionStateSequence tseq, TransitionStateSequence Tseq,
			TransitionStateSequence tseq2, TransitionStateSequence Tseq2, HashSet<TransitionStateSequence> tsequences,
			HashSet<TransitionStateSequence> Tsequences) {
		System.out.println("----------- Argumentation Frameworks ---------------");
		System.out.println("af == Af:		" + (af == Af));
		System.out.println("af equals Af:		" + af.equals(Af));
		
		System.out.println("------------- Serialisation Sequences ----------------");
		System.out.println("seq == Seq:			" + (seq == Seq));
		System.out.println("seq2 == Seq2:			" + (seq2 == Seq2));
		System.out.println("seqs == Seqs:			" + (sequences == Sequences));
		System.out.println("seq equals Seq:			" + seq.equals(Seq));
		System.out.println("seq2 equals Seq2:		" + seq2.equals(Seq2));
		System.out.println("sequences equals Sequences:	" + sequences.equals(Sequences));
		
		System.out.println("------------- Transition State Sequences -----------------");
		System.out.println("reducedAF == ReducedAF			" + (reducedAF == ReducedAF));
		System.out.println("tstate == Tstate			" + (tstate == Tstate));
		System.out.println("tseq == Tseq				" + (tseq == Tseq));
		System.out.println("tseq2 == Tseq2				" + (tseq2 == Tseq2));
		System.out.println("tseqs == Tseqs				" + (tsequences == Tsequences));
		System.out.println("reducedAF equals ReducedAF		" + reducedAF.equals(ReducedAF));
		System.out.println("tstate equals Tstate			" + tstate.equals(Tstate));
		System.out.println("tseq equals Tseq			" + tseq.equals(Tseq));
		System.out.println("tseq2 equals Tseq2			" + tseq2.equals(Tseq2));
		System.out.println("tsequences equals Tsequences		" + tsequences.equals(Tsequences));
		
		System.out.println("---------------- HashValues ---------------------");
		System.out.println("hash(seq) == hash(Seq) :		" + (seq.hashCode() == Seq.hashCode()));
		System.out.println("hash(seq2) == hash(Seq2) :		" + (seq2.hashCode() == Seq2.hashCode()));
		System.out.println("hash(tseq) == hash(Tseq) :		" + (tseq.hashCode() == Tseq.hashCode()));
		System.out.println("hash(tseq2) == hash(Tseq2) :		" + (tseq2.hashCode() == Tseq2.hashCode()));
	}

}
