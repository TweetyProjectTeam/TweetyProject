package net.sf.tweety.lp.asp.solver;

public class SolveTime {

	/** nanoseconds to transfer program to solver */
	public long	write;
	
	/** nanoseconds to perform answer set calculation */
	public long	calculate;
	
	/** nanoseconds to parse back answer sets (accumulated over all answer sets) */
	public long	read;
	
	public SolveTime() {
		this.write = 0;
		this.read = 0;
		this.calculate = 0;
	}
	
	public SolveTime(SolveTime st) {
		this.write = st.write;
		this.read = st.read;
		this.calculate = st.calculate;
	}
	
	public void beginWrite() {
		write = System.nanoTime();
	}
	
	public void endWrite() {
		this.write = System.nanoTime() - write;
	}
	
	public void beginCalculate() {
		this.calculate = System.nanoTime();
	}
	
	public void endCalculate() {
		this.calculate = System.nanoTime() - this.calculate;
	}
	
	public void beginRead() {
		this.read = System.nanoTime();
	}
	
	public void endRead() {
		this.read = System.nanoTime() - this.read;
	}
	
}
