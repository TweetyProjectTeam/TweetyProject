/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2016 The Tweety Project Team <http://tweetyproject.org/contact/>
 */
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
