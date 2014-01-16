package net.sf.tweety.agents;

/**
 * An executable is an action an agent performs within an environment.
 * 
 * @author Matthias Thimm
 */
public interface Executable {

	/**
	 * This constant represents the default null operation.
	 */
	public static final Executable NO_OPERATION = new Executable(){
		public String toString(){ return "NO_OPERATION";}
		@Override
		public boolean isNoOperation() {
			return true;
		}		
	};
	
	/**
	 * Indicates whether this operation can be regarded
	 * as no operation at all.
	 * @return "true" if this operation is a noop.
	 */
	public boolean isNoOperation();
}
