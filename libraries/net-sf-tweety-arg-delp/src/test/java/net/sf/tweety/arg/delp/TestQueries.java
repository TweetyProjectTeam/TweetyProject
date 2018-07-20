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
import net.sf.tweety.arg.delp.reasoner.DelpReasoner;
import net.sf.tweety.arg.delp.semantics.DelpAnswer;
import net.sf.tweety.arg.delp.semantics.GeneralizedSpecificity;
import net.sf.tweety.arg.delp.syntax.DefeasibleLogicProgram;
import net.sf.tweety.logics.fol.syntax.FolFormula;

import org.junit.Test;

import java.io.IOException;
import java.util.logging.Logger;

import static org.junit.Assert.assertEquals;

/**
 * Testing some example KBs with various queries.
 *
 * @author Linda.Briesemeister
 */
public final class TestQueries {

    private final static Logger LOGGER = Logger.getLogger(TestQueries.class.getName());

    private static DelpReasoner REASONER = new DelpReasoner(new GeneralizedSpecificity());

    private DelpAnswer.Type query(String filepath, String query) throws IOException {
        // perform query
    	DelpParser parser = new DelpParser();
    	DefeasibleLogicProgram delp = parser.parseBeliefBaseFromFile(TestQueries.class.getResource(filepath).getFile());
        FolFormula formula = (FolFormula) parser.parseFormula(query);
        DelpAnswer.Type answer = REASONER.query(delp,formula);
        LOGGER.info("DeLP answer to query '"+formula+"' = "+answer);
        return answer;
    }

    private DelpAnswer.Type query(DefeasibleLogicProgram delp, FolFormula formula) throws IOException {
        // perform query
        DelpAnswer.Type answer = REASONER.query(delp,formula);
        LOGGER.info("DeLP answer to query '"+formula+"' = "+answer);
        return answer;
    }
    
    @Test
    public void birds() throws IOException {
        DelpAnswer.Type answer;
        // tina
        answer = query("/birds.txt", "Flies(tina)");
        assertEquals("Tina should fly", DelpAnswer.Type.YES, answer);
        answer = query("/birds.txt","~Flies(tina)");
        assertEquals("Tina should fly", DelpAnswer.Type.NO, answer);
        // tweety
        answer = query("/birds.txt", "Flies(tweety)");
        assertEquals("Tweety does not fly", DelpAnswer.Type.NO, answer);
        answer = query("/birds.txt", "~Flies(tweety)");
        assertEquals("Tweety does not fly", DelpAnswer.Type.YES, answer);
    }

    @Test
    public void nixon() throws IOException {
        DelpAnswer.Type answer;
        answer = query("/nixon.txt", "~pacifist(nixon)"); // UNDECIDED
        assertEquals(DelpAnswer.Type.UNDECIDED, answer);
        answer = query("/nixon.txt", "pacifist(nixon)"); // UNDECIDED
        assertEquals(DelpAnswer.Type.UNDECIDED, answer);
        answer = query("/nixon.txt", "has_a_gun(nixon)"); // YES
        assertEquals(DelpAnswer.Type.YES, answer);
    }

    @Test
    public void stocks() throws IOException {
        DelpAnswer.Type ans = query("/stocks.txt", "buy_stock(acme)");
        assertEquals("Buying stock ACME should be supported", DelpAnswer.Type.YES, ans);
    }

    @Test
    public void counterarguments() throws IOException {
        DelpAnswer.Type answer;
        answer = query("/counterarg.txt", "a");
        assertEquals(DelpAnswer.Type.UNDECIDED, answer);
        answer = query("/counterarg.txt", "c");
        assertEquals(DelpAnswer.Type.UNDECIDED, answer);
    }

    @Test
    public void hobbes() throws IOException {
        DelpAnswer.Type answer;
        answer = query("/hobbes.txt", "~dangerous(hobbes)");
        assertEquals(DelpAnswer.Type.UNDECIDED, answer);
    }

    @Test
    public void dtree() throws IOException {
        DelpAnswer.Type answer;
        answer = query("/dtree.txt", "a"); // UNDECIDED
        assertEquals(DelpAnswer.Type.UNDECIDED, answer);
        answer = query("/dtree.txt", "~b"); // YES
        assertEquals(DelpAnswer.Type.YES, answer);
        answer = query("/dtree.txt", "b"); // NO
        assertEquals(DelpAnswer.Type.NO, answer);
    }

    @Test
    public void quoted() throws IOException {
        DelpAnswer.Type answer;
        DelpParser parser = new DelpParser();
        DefeasibleLogicProgram delp = parser.parseBeliefBase(
                "saw(\"1.2.3.4\",\"foo.png\").\n"+
                "visited(IP) -< saw(IP,STR).\n"+
                "src(\"1.2.3.5\").");
        answer = query(delp, (FolFormula)parser.parseFormula("visited(\"1.2.3.4\")")); // YES
        assertEquals(DelpAnswer.Type.YES, answer);
        answer = query(delp, (FolFormula)parser.parseFormula("visited(\"1.2.3.5\")")); // UNDECIDED
        assertEquals(DelpAnswer.Type.UNDECIDED, answer);
    }

    @Test // currently too slow: state-space explosion!
    public void moreQuoted() throws IOException {
        DelpAnswer.Type answer;
        DelpParser parser = new DelpParser();
        DefeasibleLogicProgram delp = parser.parseBeliefBase("% modeling web defacement\n" +
                "web_defaced(STR,IP1) -< cmd_injection(IP1),\n" +
                "   saw(STR,IP2),\n" +
                "   same_realm(IP1,IP2).\n" +
                "~web_defaced(STR,IP1) -< ~cmd_injection(IP1),\n" +
                "   visited(IP2),\n" +
                "   same_realm(IP1,IP2).\n" +
                "\n" +
                "cmd_injection(IP) -< HTTP_POST(IP),\n" +
                "   POST_contains(IP).\n" +
                "~cmd_injection(IP) -< HTTP_GET(IP).\n"+
                "~cmd_injection(IP) -< ~POST_contains(IP).\n" +
                "~cmd_injection(IP) -< ~POST_contains(IP).\n"+
                "\n"+
                "same_realm(IP1,IP2) <- slash24(IP1,PRE),slash24(IP2,PRE).\n"+
                "\n"+
                "% operative presumptions:\n" +
                "visited(\"1.2.3.4\") -< true.\n" +
                "saw(\"fr.jpg\",\"1.2.3.4\") -< true.\n" +
                "HTTP_POST(\"1.2.3.9\") -< true.\n" +
                "POST_contains(\"1.2.3.9\") -< true.\n" +
                "\n" +
                "% operative facts:\n"+
                "slash24(\"1.2.3.4\",\"1.2.3\").\n" +
                "slash24(\"1.2.3.9\",\"1.2.3\").\n" +
                "true.");
     
        answer = query(delp, (FolFormula)parser.parseFormula("same_realm(\"1.2.3.4\",\"1.2.3.9\")")); // YES
        assertEquals(DelpAnswer.Type.YES, answer);
        answer = query(delp, (FolFormula)parser.parseFormula("web_defaced(\"fr.jpg\",\"1.2.3.9\")")); // YES
        assertEquals(DelpAnswer.Type.YES, answer);
    }
}