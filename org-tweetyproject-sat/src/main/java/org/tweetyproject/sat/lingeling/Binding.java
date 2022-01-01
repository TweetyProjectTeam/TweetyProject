package org.tweetyproject.sat.lingeling;

final class Binding {
	
	static {
		String osName = System.getProperty("os.name").toLowerCase();
		if (osName.contains("win")) {
			System.load(Binding.class.getResource("/lingeling.dll").getPath());			
		} else if (osName.contains("nux")) {
			System.load(Binding.class.getResource("/lingeling.so").getPath());			
		}
	}
	
	static native long init();

	static native void release(long handle);

	static native void add(long handle, int lit);

	static native void assume(long handle, int lit);

	static native boolean sat(long handle);

	static native boolean deref(long handle, int lit);

	static native boolean fixed(long handle, int lit);

	static native boolean failed(long handle, int lit);

	static native boolean inconsistent(long handle);

	static native boolean changed(long handle);

	static native void reduceCache(long handle);

	static native void flushCache(long handle);

	static native void freeze(long handle, int lit);

	static native boolean frozen(long handle, int lit);

	static native void melt(long handle, int lit);

	static native void meltAll(long handle);

	static native boolean usable(long handle, int lit);

	static native boolean reusable(long handle, int lit);

	static native void reuse(long handle, int lit);

}
