package net.sf.tweety.beliefdynamics;

import java.util.Collection;
import java.util.List;

import net.sf.tweety.commons.Formula;

/**
 * Implements the list based method of the Revision interface by iterative calling
 * the revision method which revise two belief bases. Acts as a base class for iterative
 * revision processes.
 * 
 * @author Tim Janus
 *
 * @param <T>	The type of the belief bases
 */
public abstract class CredibilityRevisionIterative<T extends Formula> 
	extends CredibilityRevision<T> {

	@Override
	public Collection<T> revise(List<Collection<T>> ordererList) {
		if(ordererList == null || ordererList.size() == 0)
			throw new IllegalArgumentException("The parameter 'orderList' must not be empty.");
		
		Collection<T> p1 = ordererList.get(0);
		for(int i=1; i<ordererList.size(); ++i) {
			Collection<T> p2 = ordererList.get(i);
			p1 = revise(p1,p2);
		}
		return p1;
	}
	
}
