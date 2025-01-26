/*
 * This file is part of "TweetyProject", a collection of Java libraries for
 * logical aspects of artificial intelligence and knowledge representation.
 *
 * TweetyProject is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License version 3 as
 * published by the Free Software Foundation.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 * Copyright 2025 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.examples;

import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.serialisability.util.AigSerialisationPlotter;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.util.DefaultDungTheoryGenerator;
import org.tweetyproject.graphs.util.AigGraphPlotter;

import java.io.IOException;
import java.net.URISyntaxException;

public class GraphPlotterExample {
    public static void main(String[] args) throws URISyntaxException, IOException {
        DungTheory theory = new DefaultDungTheoryGenerator(9, 0.2).next();

        AigGraphPlotter<DungTheory, Argument> plotter = new AigGraphPlotter<>(theory);
        //plotter.show();

        AigSerialisationPlotter.showSerialisation(theory, Semantics.CO);
    }
}
