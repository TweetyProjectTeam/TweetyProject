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
 * This class provides all tptp roles used within the tptp format.
 * Currently only "axiom" and "conjecture" are used.
 * @author Bastian Wolf
 *
 */

public enum TptpFofFormulaRole {
	// used
	axiom , conjecture ,
	// currently unused
	hypothesis , definition , assumption ,
    lemma , theorem ,  negated_conjecture ,
    plain , fi_domain , fi_functors , fi_predicates ,
    type , unknown
}
