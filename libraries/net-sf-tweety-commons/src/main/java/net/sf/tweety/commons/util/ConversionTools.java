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
 package net.sf.tweety.commons.util;

import java.math.BigInteger;
import java.util.BitSet;

/**
 * This class provides some utility methods for converting different
 * data types.
 * 
 * @author Matthias Thimm
 *
 */
public class ConversionTools {
	
	/**
	 * Provides a string representation of the bits in the given BigInteger. 
	 * @param value some BigInteger
	 * @return a string representation of the bits in the given BigInteger.
	 */
	public static String bigInteger2BinaryString(BigInteger value){
		String str = "";
		for(int j = value.bitLength()-1; j >=0; j--)
			str += value.testBit(j) ? "1" : "0";
		return str;
	}
	
	/**Creates a bit set from the given BigInteger. 
	 * @param value value some BigInteger
	 * @return a bitset representing the BigInteger.
	 */
	public static BitSet bigInteger2BitSet(BigInteger value){
		return ConversionTools.binaryString2BitSet(ConversionTools.bigInteger2BinaryString(value));
	}
	
	/**
	 * Provides a string representation of the bits in the given BitSet. 
	 * @param s some BitSet
	 * @return a string representation of the bits in the given BitSet.
	 */
	public static String bitSet2BinaryString(BitSet s){
		String str = "";
		for(int i = 0; i < s.size(); i++)
			str += s.get(i) ? "1" : "0";		
		return str;
	}
	
	/**
	 * Creates a bit set from the given string of zeros and
	 * ones. Additional zeros are added to the prefix in, so
	 * that the string is aligned on the right side of the bitset
	 * @param s some string of zeros and ones
	 * @return a bitset representing the bitvector encoded by the string.
	 */
	public static BitSet binaryString2BitSet(String s){
		BitSet bs = new BitSet(s.length());
		for(int i = 1; i <= s.length(); i++){
			if(s.charAt(s.length()-i) == '1')
				bs.set(bs.size()-i);
		}
		return bs;
	}
}
