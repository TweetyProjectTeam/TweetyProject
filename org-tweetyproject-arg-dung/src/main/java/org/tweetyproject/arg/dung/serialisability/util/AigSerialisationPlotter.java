package org.tweetyproject.arg.dung.serialisability.util;

import org.tweetyproject.arg.dung.reasoner.SerialisedExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.serialisability.semantics.SerialisationGraph;
import org.tweetyproject.arg.dung.serialisability.semantics.SerialisationState;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.graphs.util.AigGraphPlotter;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Abstract plotter for visualizing the serialisation of Dung argumentation frameworks.
 * <p>
 * This class extends {@link AigGraphPlotter} to provide visualization capabilities
 * specifically tailored for Dung theories and their serialisations under various
 * argumentation semantics.
 * </p>
 *
 * <p>
 * It offers a static utility method to generate and display an HTML visualization
 * of both the original argumentation framework and its serialisation graph,
 * facilitating comparative analysis.
 * </p>
 *
 * @author (your name or team)
 */
public abstract class AigSerialisationPlotter extends AigGraphPlotter<DungTheory, Argument> {

    /**
     * Constructs a new {@code AigSerialisationPlotter} for the given Dung theory.
     *
     * <p>
     * This initializes the plotter with the specified argumentation framework,
     * allowing for the visualization of its structure or serialisation.
     * </p>
     *
     * @param graph the Dung argumentation framework to be visualized.
     */
    public AigSerialisationPlotter(DungTheory graph) {
        super(graph);
    }

    /**
     * Visualizes a Dung argumentation framework along with its serialisation
     * with respect to a given argumentation semantics.
     *
     * <p>
     * This method uses a {@link SerialisedExtensionReasoner} to compute
     * the serialisation graph of the given theory under the specified semantics.
     * It then renders both the original argumentation framework and the
     * serialisation graph side by side in a generated HTML file using
     * {@link AigGraphPlotter}.
     * </p>
     *
     * <p>
     * The resulting visualization is written to {@code index.html}
     * and automatically opened in the system's default web browser.
     * </p>
     *
     * @param theory    the Dung theory to be visualized.
     * @param semantics the argumentation semantics to use for computing the
     *                  serialisation.
     * @throws RuntimeException if an I/O error occurs during file handling.
     */
    public static void showSerialisation(DungTheory theory, Semantics semantics) {
        SerialisedExtensionReasoner reasoner = new SerialisedExtensionReasoner(semantics);

        SerialisationGraph serialisation = reasoner.getSerialisationGraph(theory);
        SerialisationState root = new SerialisationState(theory, new Extension<>(),
                reasoner.isTerminal(theory, new Extension<>()));

        AigGraphPlotter<DungTheory, Argument> plotter1 = new AigGraphPlotter<>(theory);
        AigGraphPlotter<SerialisationGraph, SerialisationState> plotter2 = new AigGraphPlotter<>(serialisation);
        plotter2.makeLeveled(root);

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
