package org.tweetyproject.sat.picosat;

final class Binding {
	
	static {
		String osName = System.getProperty("os.name").toLowerCase();
		if (osName.contains("win")) {
			System.load(Binding.class.getResource("/picosat.dll").getPath());			
		} else if (osName.contains("nux")) {
			System.load(Binding.class.getResource("/picosat.so").getPath());			
		}
	}
	
	static final int NO_DECISION_LIMIT = -1;
	
	static native void addClause(long handle, int lit);

	static native void addClause(long handle, int lit1, int lit2);

	static native void addClause(long handle, int lit1, int lit2, int lit3);
	
	static native void addClause(long handle, int[] clause);
		
	/*
	 * The following methods directly correspond to picosat calls as defined
	 * in picosat.h
	 */
	static native long init();

	static native void reset(long handle);
	
	static native void add(long handle, int lit);
	
	static native void addAdoLit(long handle, int lit);

	static native void assume(long handle, int lit);
	
	static native void setPropagationLimit(long handle, long limit);
	
	static native boolean sat(long handle, long limit);
	
	static native boolean deref(long handle, int lit);
	
	static native boolean derefPartial(long handle, int lit);
	
	static native boolean inconsistent(long handle);
	
}
