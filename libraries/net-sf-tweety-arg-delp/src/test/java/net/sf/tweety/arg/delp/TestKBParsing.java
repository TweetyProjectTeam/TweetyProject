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
 package net.sf.tweety.arg.delp;

import net.sf.tweety.arg.delp.parser.DelpParser;
import net.sf.tweety.arg.delp.parser.TokenMgrError;
import net.sf.tweety.commons.ParserException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

/**
 * Parsing DeLPs.
 *
 * @author Linda.Briesemeister
 */
public final class TestKBParsing {

    @Rule
    public TemporaryFolder tempFolder = new TemporaryFolder();

    @Test(expected = ParserException.class)
    public void parseEmpty() throws IOException {
        new DelpParser().parseBeliefBase("  \t \n\n  \t\r  ");
    }

    @Test(expected = ParserException.class)
    public void parseTooLong() throws IOException {
        new DelpParser().parseBeliefBase("  bla(X) .  foo\n ");
    }

    @Test(expected = ParserException.class)
    public void parseTooShort() throws IOException {
        new DelpParser().parseBeliefBase("  bla(X)\t <-\t foo(Y)");
    }

    @Test(expected = TokenMgrError.class)
    public void parseUnknownToken() throws IOException {
        new DelpParser().parseBeliefBase("  bla(X) > foo(Y).");
    }

    @Test
    public void parseComments() throws IOException {
        new DelpParser().parseBeliefBase("% a comment\r\nbla(X) <- foo(Y).\n  foo(myself).\r\n\n");
    }

    @Test
    public void parseQuotedStrings() throws IOException {
        new DelpParser().parseBeliefBase("% a comment\n bla(X) <- foo(\"1.2.3.4\", X). \n");
    }
    @Test(expected = TokenMgrError.class)
    public void missingEndQuote() throws IOException {
        new DelpParser().parseBeliefBase(" bla(X) <- foo(\"1.2.3.4, X). \n");
    }

    @Test
    public void parseEmptyPreds() throws IOException {
        new DelpParser().parseBeliefBase("% a comment\n bla.  foo. \n");
    }

    @Test
    public void parseKnownKBs() throws IOException {
        for (String KB : new String[]{
                "/birds.txt",
                "/counterarg.txt",
                "/dtree.txt",
                "/hobbes.txt",
                "/nixon.txt",
                "/stocks.txt"}) {
            new DelpParser().parseBeliefBase(Utilities.getKB(KB));
        }
    }

    // very long KB (> 4096 chars?)
    private static String generateString(Random rng, String characters, int length) {
        char[] text = new char[length];
        for (int i = 0; i < length; i++)
            text[i] = characters.charAt(rng.nextInt(characters.length()));
        return new String(text);
    }
    @Test
    public void veryLargeKB() throws IOException {
        // generate file with about 4096 lines of different facts like "abcd."
        Set<String> facts = new HashSet<>();  // use a set to avoid duplicate facts
        Random rng = new Random();
        for (int i = 0; i < 5000; i++)
            facts.add(generateString(rng, "abcdefghijklmnopqrstuvwxyz", 10)+".");
        File tempFile = tempFolder.newFile();
        Files.write(tempFile.toPath(), facts);
        new DelpParser().parseBeliefBase(new FileReader(tempFile));
    }

    // TODO: input encoding??
}
