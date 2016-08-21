package net.sf.tweety.arg.aspic.order;

import java.util.Collection;
import java.util.Comparator;

public class SetComparator<T> implements Comparator<Collection<T>> {

	private Comparator<T> comp;

	private boolean elitist;

	public SetComparator(Comparator<T> comp, boolean elitist) {
		super();
		this.comp = comp;
		this.elitist = elitist;
	}

	private boolean is_smaller(Collection<T> gamma1, Collection<T> gamma2) {
		if (elitist) {
			for (T t : gamma1) {
				int i = 0;
				for (T u : gamma2) {
					if (comp.compare(t, u) <= 0)
						i++;
				}
				if (i == gamma2.size())
					return true;
			}
			return false;
		} else {
			int i = 0;
			for (T t : gamma1) {
				for (T u : gamma2) {
					if (comp.compare(t, u) <= 0) {
						i++;
						break;
					}
				}
			}
			if (i == gamma1.size())
				return true;
			else
				return false;
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.util.Comparator#compare(java.lang.Object, java.lang.Object)
	 */
	@Override
	public int compare(Collection<T> gamma1, Collection<T> gamma2) {
		if (gamma1.isEmpty() && gamma2.isEmpty())
			return 0;
		if (gamma1.isEmpty() && !gamma2.isEmpty())
			return -1;
		if (gamma2.isEmpty() && !gamma1.isEmpty())
			return 1;
		
		if(is_smaller(gamma1, gamma2))
			return -1;
		if(is_smaller(gamma2, gamma1))
			return 1;
		
		return 0;
	}

}
