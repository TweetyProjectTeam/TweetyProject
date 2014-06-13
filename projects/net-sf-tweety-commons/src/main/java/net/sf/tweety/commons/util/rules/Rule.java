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
package net.sf.tweety.commons.util.rules;

import java.util.*;

import net.sf.tweety.commons.*;

/**
 * This interface models a general rule, i.e. a structure with some
 * premise (a set of formulas) and some conclusion (a single formula).
 * 
 * @author Matthias Thimm
 * @author Tim Janus
 */
public interface Rule<C extends Formula, P extends Formula> extends Formula {

	public boolean isFact();
	
	public boolean isConstraint();
	
	public void setConclusion(C conclusion);
	
	public void addPremise(P premise);
	
	public void addPremises(Collection<? extends P> premises);
	
	@Override
	public Signature getSignature();
	
	/**
	 * Returns the premise of this rule.
	 * @return the premise of this rule.
	 */
	public Collection<? extends P> getPremise();
	
	/**
	 * Returns the conclusion of this rule.
	 * @return the conclusion of this rule.
	 */
	public C getConclusion();
		
}
