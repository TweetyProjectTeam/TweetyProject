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
 *  Copyright 2025 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.dung.serialisability.util;

import org.tweetyproject.arg.dung.reasoner.SerialisedExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.graphs.util.AigGraphPlotter;
import org.tweetyproject.serialisability.SerialisationGraph;
import org.tweetyproject.serialisability.SerialisationState;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Visualizes an argumentation framework next to its serialisation graph via
 * the aig-graph-component. States are arranged in levels by their distance to
 * the root; terminal states are highlighted.
 * <p>
 * This class only extends {@link AigGraphPlotter} to access its resources and
 * cannot be instantiated.
 *
 * @author Lars Bengel
 */
public final class AigSerialisationPlotter extends AigGraphPlotter<DungTheory, Argument> {
    /** color of terminal states */
    private static final String COLOR_TERMINAL = "lightgreen";

    /** Hidden constructor */
    private AigSerialisationPlotter() {
        super(new DungTheory());
    }

    /**
     * Renders the framework and its serialisation graph wrt. the semantics in the web browser.
     * The page is written to {@code index.html} in the working directory.
     *
     * @param theory    some argumentation framework
     * @param semantics some serialisable semantics
     * @throws RuntimeException if an I/O error occurs
     */
    public static void showSerialisation(DungTheory theory, Semantics semantics) {
        SerialisationGraph<DungTheory, Extension<DungTheory>> graph = new SerialisedExtensionReasoner(semantics)
                .getSerialisationGraph(theory);

        AigGraphPlotter<DungTheory, Argument> plotter1 = new AigGraphPlotter<>(theory);
        AigGraphPlotter<SerialisationGraph<DungTheory, Extension<DungTheory>>, SerialisationState<DungTheory, Extension<DungTheory>>> plotter2 =
                new AigGraphPlotter<>(graph);
        plotter2.makeLeveled(graph.getRoot());
        for (SerialisationState<DungTheory, Extension<DungTheory>> state : graph.getTerminalStates())
            plotter2.setColor(state, COLOR_TERMINAL);

        Path outputPath = Paths.get("index.html");
        try {
            String template = Files.readString(Paths.get(getResource("aiggraph/serialisation.template")));
            String output = String.format(template,
                    plotter1.write(), plotter2.write(),
                    getResource("aiggraph/favicon.ico"), getResource("aiggraph/style.css"),
                    getResource("aiggraph/load-mathjax.js"), getResource("aiggraph/graph-component.js"));

            Files.writeString(outputPath, output);

            // show graph in web browser
            File htmlFile = new File("index.html");
            Desktop.getDesktop().browse(htmlFile.toURI());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
