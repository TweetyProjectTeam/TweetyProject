package net.sf.tweety.beliefdynamics.gui;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import net.sf.tweety.beliefdynamics.BaseRevisionOperator;
import net.sf.tweety.commons.Formula;

/**
 * The data-model used to compare two different revision approaches. Two approaches
 * can be selected using a list of approaches. The data-model also contains an ordered list
 * of belief bases which define the ordering of the revision process. Observers are notified using
 * the PropertyChangeListener.
 * 
 * @author Tim Janus
 */
public class RevisionCompareModel {
	/** delegator for the implementation of the PropertyChange */
	private PropertyChangeSupport change = new PropertyChangeSupport(this);
	
	/** the left revision operator */
	protected BaseRevisionOperator<?> leftOperator;
	
	/** the right revision operator */
	protected BaseRevisionOperator<?> rightOperator;
	
	/** use interactive revision for the left method */
	protected boolean leftIterative;
	
	/** use interactive revision for the right method */
	protected boolean rightIterative;
	
	/** a set of selectable revison operators */
	protected Set<BaseRevisionOperator<?>> selectableOperators = new HashSet<BaseRevisionOperator<?>>();
	
	/** an ordered list of belief bases which are revised for the comparsation of the two selected revision methods */
	protected List<Collection<?>> beliefBases = new LinkedList<Collection<?>>();
	
	/**
	 * Adds a property change listener
	 * @param listener	Reference to the listener
	 */
	public void addListener(PropertyChangeListener listener) {
		change.addPropertyChangeListener(listener);
	}
	
	/**
	 * Removes a property change listener
	 * @param listener	Reference to the listener
	 */
	public void removeListener(PropertyChangeListener listener) {
		change.removePropertyChangeListener(listener);
	}
	
	/**
	 * Select the left revision method.
	 * @param left	The revision method as an operator reference
	 */
	public void setLeftOperator(BaseRevisionOperator<?> left) {
		if(selectableOperators.contains(left)) {
			BaseRevisionOperator<?> old = this.leftOperator;
			this.leftOperator = left;
			change.firePropertyChange("leftOperator", old, left);
		}
	}
	
	/**
	 * Select the right revision method.
	 * @param right	The revision method as an operator reference
	 */
	public void setRightOperator(BaseRevisionOperator<?> right) {
		if(selectableOperators.contains(right)) {
			BaseRevisionOperator<?> old = this.rightOperator;
			this.rightOperator = right;
			change.firePropertyChange("rightOperator", old, right);
		}
	}
	
	/**
	 * Adds a belief base to the end of the ordered list of belief bases.
	 * @param beliefBase	Reference to the new belief base
	 */
	public void addBeliefbase(Collection<? extends Formula> beliefBase) {
		beliefBases.add(beliefBase);
		change.fireIndexedPropertyChange("beliefBases", beliefBases.size()-1, null, beliefBase);
	}
	
	/**
	 * Removes a belief base from the ordered list of belief bases.
	 * @param beliefBase Reference to the belief base which shall be removed.
	 */
	public void removeBeliefbase(Collection<?> beliefBase) {
		int index = beliefBases.indexOf(beliefBase);
		if(index != -1) {
			beliefBases.remove(index);
			change.fireIndexedPropertyChange("beliefBases", index, beliefBase, null);
		}
	}
	
	/**
	 * Moves the given belief base in the given direction.
	 * @param beliefBase	A belief base which is already in the ordered list of belief bases
	 * @param dir			Either -1 if the belief base shall move one index to the front of the list or
	 * 						1 if the belief base shall move one unit index to the end of the list.
	 */
	public void moveBeliefbase(Collection<?> beliefBase, int dir) {
		if(dir != -1 && dir != 1)
			throw new IllegalArgumentException("dir must not be 1 for upwards or -1 for downwards");
		
		int index = beliefBases.indexOf(beliefBase);
		if(index != -1) {
			int newIndex = index + dir;
			if(newIndex >= 0 && newIndex < beliefBases.size()) {
				Collection<?> other = beliefBases.get(newIndex);
				beliefBases.set(newIndex, beliefBase);
				beliefBases.set(index, other);
				change.fireIndexedPropertyChange("beliefBases", index, beliefBase, other);
				change.fireIndexedPropertyChange("beliefBases", newIndex, other, beliefBase);
			}
		}
	}
	
	/**
	 * Adds the given revision operator to the set of selectable revision methods.
	 * @param op	Reference to the operator, if the operator is in the set nothing happens
	 */
	public void addOperator(BaseRevisionOperator<?> op) {
		if(this.selectableOperators.add(op)) {
			change.firePropertyChange("selectableOperators", null, op);
		}
	}
	
	/**
	 * Removes the given revision operator from the set of selectable revision methods.
	 * @param op Reference to the operator which is removed if it is member of the set of revision methods.
	 */
	public void removeOperator(BaseRevisionOperator<?> op) {
		if(this.selectableOperators.remove(op)) {
			change.firePropertyChange("selectableOperators", op, null);
		}
	}
	
	/**
	 * switches the flag indicating if the left method shall use iterative algorithms
	 * @param b	true means iterative, false means in one step.
	 */
	public void setLeftIterative(boolean b) {
		if(leftIterative != b) {
			leftIterative = b;
			change.firePropertyChange("leftIterative", !leftIterative, leftIterative);
		}
	}
	
	/**
	 * switches the flag indicating if the right method shall use iterative algorithms
	 * @param b	true means iterative, false means in one step.
	 */
	public void setRightIterative(boolean b) {
		if(rightIterative != b) {
			rightIterative = b;
			change.firePropertyChange("rightIterative", !rightIterative, rightIterative);
		}
	}
}
