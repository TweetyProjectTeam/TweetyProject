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
package net.sf.tweety.arg.adf.reasoner.processor;

import net.sf.tweety.arg.adf.reasoner.SatReasonerContext;
import net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework;

/**
 * Decides if the given ADF becomes K-bipolar if we fix L arguments.
 * 
 * @author Mathias Hofer
 *
 */
public final class SatLKBipolarStateProcessor implements StateProcessor<SatReasonerContext>{
	
	private final int l;
	
	private final int k;

	/**
	 * @param l
	 * @param k
	 */
	public SatLKBipolarStateProcessor(int l, int k) {
		this.l = l;
		this.k = k;
	}
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.arg.adf.reasoner.processor.StateProcessor#process(java.lang.Object, net.sf.tweety.arg.adf.syntax.AbstractDialecticalFramework)
	 */
	@Override
	public void process(SatReasonerContext state, AbstractDialecticalFramework adf) {
		// TODO Auto-generated method stub
		
	}
	

	
}