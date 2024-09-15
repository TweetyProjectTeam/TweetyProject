package org.tweetyproject.arg.dung.examples;

import org.tweetyproject.arg.dung.syntax.DungTheory;
import org.tweetyproject.arg.dung.util.DungTheoryGenerator;
import org.tweetyproject.arg.dung.util.EnumeratingDungTheoryGenerator;
import org.tweetyproject.arg.dung.writer.AbstractDungWriter;
import org.tweetyproject.arg.dung.writer.TgfWriter;

import java.io.File;
import java.io.IOException;

public class GenerateTestData {
    public static void main(String[] args) throws IOException {
        DungTheoryGenerator generator = new EnumeratingDungTheoryGenerator();
        AbstractDungWriter writer = new TgfWriter();

        int counter = 1;
        while (true) {
            DungTheory theory = generator.next();
            if (theory.size() < 4) continue;
            if (theory.size()==5) break;

            writer.write(theory, new File(String.format("/home/lars/GitHub/algorithms_for_acceptable_arguments/evaluation/benchmark/instance_%s.tgf", counter++)));
        }
    }
}
