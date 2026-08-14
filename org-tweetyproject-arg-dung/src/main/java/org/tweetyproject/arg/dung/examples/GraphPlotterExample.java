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

import org.tweetyproject.arg.dung.equivalence.SerialisationEquivalence;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.serialisability.util.AigSerialisationPlotter;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.util.DefaultDungTheoryGenerator;
import org.tweetyproject.graphs.util.AigGraphPlotter;

import java.io.IOException;
import java.net.URISyntaxException;

/**
 * Example usage of the {@link AigGraphPlotter} for Dung theories.
 * <p>
 * It renders a sample argumentation framework in a browser window.
 *
 * @author Lars Bengel
 */
public class GraphPlotterExample {
    /**
     * Creates a new graph plotter example.
     */
    public GraphPlotterExample() {
        super();
    }

    /**
     * Entry point of the program demonstrating the visualization of an
     * argumentation framework using the {@code AigGraphPlotter}.
     *
     * <p>
     * This method generates a random Dung argumentation framework with a specified
     * number of arguments and a given attack probability. The framework is then
     * visualized using the {@code AigGraphPlotter}.
     * </p>
     *
     * @param args Command-line arguments (not used in this application).
     * @throws URISyntaxException if a URI error occurs while loading resources.
     * @throws IOException        if an I/O error occurs during resource handling.
     */
    public static void main(String[] args) throws URISyntaxException, IOException {
        DungTheory theory = new DungTheory();
        Argument a = new Argument("a");
        Argument b = new Argument("b");
        Argument c = new Argument("c");
        Argument d = new Argument("d");
        Argument e = new Argument("e");
        Argument f = new Argument("f");
        Argument g = new Argument("g");
        Argument h = new Argument("h");
        theory.add(a,b,c,d,e,f,h);
        theory.addAttack(a,b);
        //theory.addAttack(a,g);
        //theory.addAttack(g,b);

        theory.addAttack(b,c);
        theory.addAttack(b,d);
        theory.addAttack(c,e);
        theory.addAttack(d,e);
        theory.addAttack(e,f);
        theory.addAttack(c,h);
        theory.addAttack(h,f);
        theory.addAttack(f,h);

        DungTheory theory1 = new DungTheory();
        theory1.add(a,b,c,d,e,f,h);
        theory1.addAttack(a,b);
        theory1.addAttack(a,h);
        theory1.addAttack(h,b);

        theory1.addAttack(b,c);
        theory1.addAttack(b,d);
        theory1.addAttack(c,e);
        theory1.addAttack(d,e);
        theory1.addAttack(e,f);
        //theory1.addAttack(c,h);
        //theory1.addAttack(h,f);
        //theory1.addAttack(f,h);

        System.out.println(new SerialisationEquivalence(Semantics.GR).isEquivalent(theory1,theory));

        // Initialize plotter for argumentation framework
        AigGraphPlotter<DungTheory, Argument> plotter = new AigGraphPlotter<>(theory);
        plotter.setToggleNodePhysics(false);

        // Set options for rendering of the graph, eg
        plotter.setLinkDeletable(false);

        // Render graph
        //plotter.show();

        // Plot an argumentation framework and its serialisation wrt. complete semantics
        AigSerialisationPlotter.showSerialisation(theory, Semantics.PR);

    }
}
