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
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.math.util;

import org.ojalgo.access.Access2D;
import org.ojalgo.matrix.PrimitiveMatrix;


/**
 * Provides some utility functions for solving quadratic problems with ojAlgo.
 * 
 * @author NicoPotyka
 *
 */
public class OjAlgoMathUtils {

	
	/**
	 * Create (m,n)-matrix containing only ones.
	 * @param m
	 * @param n
	 * @return
	 */
	public static PrimitiveMatrix getOnes(int m, int n) {

		Access2D.Builder<PrimitiveMatrix> aBuilder = PrimitiveMatrix.FACTORY.getBuilder(m, n);
		for(int i=0; i<m; i++) {
			for(int j=0; j<n; j++) {
				aBuilder.set(i, j, 1);
			}
		}
		
		return aBuilder.build();
	}
	

	/**
	 * Create unity matrix multiplied by scalar.
	 * @param m
	 * @param n
	 * @return
	 */
	public static PrimitiveMatrix getUnityMultiple(int n, double scalar) {

		Access2D.Builder<PrimitiveMatrix> aBuilder = PrimitiveMatrix.FACTORY.getBuilder(n, n);
		
		for(int i=0; i<n; i++) {
			aBuilder.set(i, i, scalar);
		}
		
		return aBuilder.build();
	}


	

	
}
