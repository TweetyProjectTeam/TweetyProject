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
package org.tweetyproject.commons.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Cygwin shell.
 * @author Nils Geilen, Matthias Thimm
 *
 */
public class CygwinShell extends Shell{
	
	String binaryLocation;

	CygwinShell(String binaryLocation) {
		super();
		this.binaryLocation = binaryLocation;
	}

	@Override
	public String run(String cmd)throws InterruptedException, IOException {
		Runtime runtime = Runtime.getRuntime();			
		Process proc = runtime.exec(new String[] {binaryLocation , "-c",cmd },new String[] {});
		proc.waitFor();
		BufferedReader br = new BufferedReader(new InputStreamReader(proc.getInputStream()));
		String result = "";
		while (br.ready())
			result += br.readLine()+"\n";
		return result;
	}
	
}
