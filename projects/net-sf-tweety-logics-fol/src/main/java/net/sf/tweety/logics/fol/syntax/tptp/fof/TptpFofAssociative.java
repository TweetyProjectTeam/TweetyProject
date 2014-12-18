/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.tweety.logics.fol.syntax.tptp.fof;

/**
 * This class models the connection between two formulae
 * @author Bastian Wolf
 */
public class TptpFofAssociative {

    public static final String AND = TptpFofLogicalSymbols.CONJUNCTION();

    public static final String OR = TptpFofLogicalSymbols.DISJUNCTION();


	/**
	 *	the Associative
	 */
    private static String assoc;

    /**
     * Constructor using given associative symbol
     * @param sym given associative symbol
     */
    public TptpFofAssociative(String sym){
        assoc = sym;
    }

    /*
     * Getter 
     */
	public String getAssoc() {
		return assoc;
	}
    
    
}
