package org.tweetyproject.arg.adf.syntax.acc;

import java.util.Objects;
import java.util.Set;

/**
 * Base class for acceptance conditions with two children.
 *
 * @author Sebastian
 *
 */
public abstract class BinaryAcceptanceCondition implements AcceptanceCondition {

	/** the left child acceptance condition */
	private final AcceptanceCondition left;

	/** the right child acceptance condition */
	private final AcceptanceCondition right;

	/**
	 * Creates a new binary acceptance condition.
	 *
	 * @param left the left child acceptance condition
	 * @param right the right child acceptance condition
	 */
	public BinaryAcceptanceCondition(AcceptanceCondition left, AcceptanceCondition right) {
        if (left.equals(Objects.requireNonNull(right))) {
            throw new IllegalArgumentException("Duplicate element: " + left);
        }
		this.left = left;
		this.right = right;
	}

	@Override
	public Set<AcceptanceCondition> getChildren() {
		return Set.of(left, right);
	}
	/**
	 * Returns the left child acceptance condition.
	 *
	 * @return the left child acceptance condition
	 */
	public AcceptanceCondition getLeft() {
		return left;
	}
	/**
	 * Returns the right child acceptance condition.
	 *
	 * @return the right child acceptance condition
	 */
	public AcceptanceCondition getRight() {
		return right;
	}
	/**
	 * Returns the operator name used in {@link #toString()}.
	 *
	 * @return the operator name
	 */
	protected abstract String getName();

	@Override
	public int hashCode() {
		return Objects.hash(left, right);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		BinaryAcceptanceCondition other = (BinaryAcceptanceCondition) obj;
		return Objects.equals(left, other.left) && Objects.equals(right, other.right);
	}

	@Override
	public String toString() {
		return new StringBuilder(getName())
				.append("(")
				.append(left)
				.append(",")
				.append(right)
				.append(")")
				.toString();
	}

}
