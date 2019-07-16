/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
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
 *  Copyright 2019 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.arg.adf.reasoner.test;

/**
 * A wrapper for the information we collect during benchmark calls.
 * 
 * @author Mathias Hofer
 */
public class BenchmarkResult {
	
	/**
	 * the thrown exception or null
	 */
	private Throwable exception;

	/**
	 * the number of models returned by the reasoner
	 */
	private int modelCount;

	/**
	 * negative if we found less models than in the solution file, positive if
	 * we found more and 0 otherwise
	 */
	private int modelDifference;
	
	private int correctModels;

	/**
	 * true if the found models are also in the solution files. is trivially
	 * true if the reasoner found no models at all!
	 */
	private boolean correct;

	/**
	 * The starting time of the reasoner
	 */
	private long startTimeInMillis;

	/**
	 * The ending time of the reasoner
	 */
	private long endTimeInMillis;
	
	public BenchmarkResult(Throwable exception) {
		super();
		this.exception = exception;
	}
	
	public BenchmarkResult(int modelCount, int modelDifference, int correctModels, boolean correct,
			long startTimeInMillis, long endTimeInMillis) {
		super();
		this.modelCount = modelCount;
		this.modelDifference = modelDifference;
		this.correctModels = correctModels;
		this.correct = correct;
		this.startTimeInMillis = startTimeInMillis;
		this.endTimeInMillis = endTimeInMillis;
	}



	/**
	 * @return the exception
	 */
	public Throwable getException() {
		return exception;
	}

	/**
	 * @return the modelCount
	 */
	public int getModelCount() {
		return modelCount;
	}

	/**
	 * @return the modelDifference
	 */
	public int getModelDifference() {
		return modelDifference;
	}
	
	public int getCorrectModels() {
		return correctModels;
	}

	/**
	 * @return the correct
	 */
	public boolean isCorrect() {
		return correct;
	}

	/**
	 * @return the startTimeInMillis
	 */
	public long getStartTimeInMillis() {
		return startTimeInMillis;
	}

	/**
	 * @return the endTimeInMillis
	 */
	public long getEndTimeInMillis() {
		return endTimeInMillis;
	}

}