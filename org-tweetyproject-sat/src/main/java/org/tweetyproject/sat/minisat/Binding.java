package org.tweetyproject.sat.minisat;

final class Binding {

	static {
		String osName = System.getProperty("os.name").toLowerCase();
		if (osName.contains("win")) {
			System.load(Binding.class.getResource("/minisat.dll").getPath());			
		} else if (osName.contains("nux")) {
			System.load(Binding.class.getResource("/minisat.so").getPath());			
		}
	}
	
	static native long init();

	static native void delete(long handle);

	static native int newVar(long handle);

	static native void add(long handle, int lit);

	static native void add(long handle, int lit1, int lit2);

	static native void add(long handle, int lit1, int lit2, int lit3);

	static native void add(long handle, int[] clause, int size);

	static native void simplify(long handle);

	static native boolean solve(long handle);

	static native boolean solve(long handle, int assumption);

	static native boolean solve(long handle, int assumption1, int assumption2);

	static native boolean solve(long handle, int assumption1, int assumption2, int assumption3);

	static native boolean solve(long handle, int[] assumptions, int size);

	static native int[] witness(long handle, int[] filter);

	/**
	 * @param handle
	 * @param var
	 * @return 0 = true, 1 = false, 2 = undef
	 */
	static native int value(long handle, int var);

}
