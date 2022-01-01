package org.tweetyproject.sat.picosat;

import org.tweetyproject.sat.SatSolver;

public final class PicosatSatSolver implements SatSolver {

	private final long handle;
	
	private int currentVar = 1;

	public PicosatSatSolver() {
		this.handle = Binding.init();
	}

	@Override
	public boolean satisfiable(int[] assumptions) {
		assume(handle, assumptions);
		return Binding.sat(handle, Binding.NO_DECISION_LIMIT);
	}

	@Override
	public boolean[] witness(int[] literals, int[] assumptions) {
		if (satisfiable(assumptions)) {
			boolean[] witness = new boolean[literals.length];
			for (int i = 0; i < literals.length; i++) {
				witness[i] = Binding.deref(handle, literals[i]);
			}
			return witness;
		}
		return new boolean[0];
	}

	@Override
	public void add(int[] clause) {
		for (int i = 0; i < clause.length; i++) {
			Binding.add(handle, clause[i]);
		}
		Binding.add(handle, 0); // 0 indicates end of clause
	}
	
	@Override
	public int newVar() {
		return currentVar++;
	}

	@Override
	public void close() {
		Binding.reset(handle);
	}

	private static void assume(long handle, int[] assumptions) {
		for (int i = 0; i < assumptions.length; i++) {
			Binding.assume(handle, assumptions[i]);
		}
	}

}
