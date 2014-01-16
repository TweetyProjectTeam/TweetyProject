package net.sf.tweety.agents.dialogues;

import java.util.*;

import net.sf.tweety.agents.*;
import net.sf.tweety.arg.dung.semantics.*;
import net.sf.tweety.arg.dung.syntax.*;

/**
 * This class packs an extension into an executable object.
 * 
 * @author Matthias Thimm
 */
public class ExecutableExtension extends Extension implements Executable {

	/**
	 * Creates a new empty extension.
	 */
	public ExecutableExtension() {
		super();
	}
	
	/**
	 * Creates a new extension for the given arguments.
	 * @param arguments a collection of arguments.
	 */
	public ExecutableExtension(Collection<? extends Argument> arguments) {
		super(arguments);
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.agents.Executable#isNoOperation()
	 */
	@Override
	public boolean isNoOperation() {
		return this.isEmpty();
	}
}
