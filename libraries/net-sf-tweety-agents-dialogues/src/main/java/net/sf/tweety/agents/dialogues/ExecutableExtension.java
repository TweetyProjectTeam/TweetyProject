/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
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
