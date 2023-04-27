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
 *  Copyright 2023 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.examples;

import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.serialisibility.plotting.SerialisationAnalysisPlotter;
import org.tweetyproject.arg.dung.syntax.DungTheory;

/**
 * This class represents a summary of examples to show the use of {@link SerialisationAnalysisPlotter}.
 *
 * @author Julian Sander
 * @version TweetyProject 1.23
 *
 */
public class SerialisationAnalysisPlotterExample {
	
	public static void main(String[] args) {
		var frameworks = new DungTheory[2];
		frameworks[0] = SerialisableExtensionReasonerExample.buildExample1();
		frameworks[1] = SerialisableExtensionReasonerExample.buildExample2();
		frameworks[2] = SerialisableExtensionReasonerExample.buildExample3();
		
		SerialisationAnalysisPlotter.plotAnalyses(new Semantics[] {Semantics.CO}, frameworks, "Example_", 2000, 1000);
	}

}
