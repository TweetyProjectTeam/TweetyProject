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
