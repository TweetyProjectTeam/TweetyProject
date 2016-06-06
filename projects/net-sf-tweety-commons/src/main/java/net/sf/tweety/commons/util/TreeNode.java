package net.sf.tweety.commons.util;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;

/**
 * 
 * @author Nils Geilen
 * generic node to build up tree structures
 * @param <T> data type of the nodes
 */

public class TreeNode<T> implements Iterable<T>{
	
	private T value;
	private TreeNode<T> parent;
	private Collection<TreeNode<T>> children = new ArrayList<>();
	
	public TreeNode(T data, TreeNode<T> parent) {
		super();
		this.value = data;
		this.parent = parent;
	}
	public T getData() {
		return value;
	}
	public void setData(T data) {
		this.value = data;
	}
	public TreeNode<T> getParent() {
		return parent;
	}
	public void setParent(TreeNode<T> parent) {
		this.parent = parent;
	}
	public Collection<TreeNode<T>> getChildren() {
		return children;
	}
	
	public Collection<T> flatten(){
		Collection<T> result = new ArrayList<>();
		result.add(value);
		for(TreeNode<T> n:children)
			result.addAll(n.flatten());
		return result;
	}
	
	@Override
	public Iterator<T> iterator() {
		return flatten().iterator();
	}

}
