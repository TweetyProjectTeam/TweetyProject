package org.tweetyproject.arg.dung.serialisability;

import org.tweetyproject.arg.dung.reasoner.SerialisedExtensionReasoner;
import org.tweetyproject.arg.dung.semantics.Extension;
import org.tweetyproject.arg.dung.semantics.Semantics;
import org.tweetyproject.arg.dung.serialisability.semantics.SerialisationGraph;
import org.tweetyproject.arg.dung.serialisability.semantics.SerialisationState;
import org.tweetyproject.arg.dung.serialisability.util.AigGraphWriter;
import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.util.DefaultDungTheoryGenerator;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;

public class Main {
    public static void main(String[] args) throws URISyntaxException, IOException {
        DungTheory theory = new DefaultDungTheoryGenerator(9, 0.2).next();

        AigGraphWriter writer = new AigGraphWriter();

        writer.showSerialisation(theory, Semantics.CO);

        File htmlFile = new File("index.html");
        //Desktop.getDesktop().browse(htmlFile.toURI());
    }
}
