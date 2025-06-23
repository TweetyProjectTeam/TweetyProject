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


public abstract class AigSerialisationPlotter extends AigGraphPlotter<DungTheory,Argument> {

    public AigSerialisationPlotter(DungTheory graph) {
        super(graph);
    }

    public static void showSerialisation(DungTheory theory, Semantics semantics) {
        SerialisedExtensionReasoner reasoner = new SerialisedExtensionReasoner(semantics);

        SerialisationGraph serialisation = reasoner.getSerialisationGraph(theory);
        SerialisationState root = new SerialisationState(theory, new Extension<>(), reasoner.isTerminal(theory, new Extension<>()));

        AigGraphPlotter<DungTheory,Argument> plotter1 = new AigGraphPlotter<>(theory);
        AigGraphPlotter<SerialisationGraph,SerialisationState> plotter2 = new AigGraphPlotter<>(serialisation);
        plotter2.makeLeveled(root);

        Path outputPath = Paths.get("index.html");
        try {
            String template = Files.readString(Paths.get(getResource("aiggraph/serialisation.template")));
            String output = String.format(template,
                    plotter1.write(), plotter2.write(),
                    getResource("aiggraph/favicon.ico"), getResource("aiggraph/style.css"),
                    getResource("aiggraph/load-mathjax.js"), getResource("aiggraph/graph-component.js")
            );

            Files.writeString(outputPath, output);

            // show graph in web browser
            File htmlFile = new File("index.html");
            Desktop.getDesktop().browse(htmlFile.toURI());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
