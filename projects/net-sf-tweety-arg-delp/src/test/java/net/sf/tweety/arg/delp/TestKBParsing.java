package net.sf.tweety.arg.delp;

import net.sf.tweety.arg.delp.parser.DelpParser;
import net.sf.tweety.arg.delp.parser.TokenMgrError;
import net.sf.tweety.commons.ParserException;
import org.junit.Test;

import java.io.IOException;

/**
 * Parsing DeLPs.
 *
 * @author Linda.Briesemeister
 */
public final class TestKBParsing {

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
    public void parseKnownKBs() throws IOException {
        for (String KB : new String[]{
                "/birds.txt",
                "/counterarg.txt",
                "/dtree.txt",
                "/hobbes.txt",
                "/nixon.txt",
                "/stocks.txt"})
            new DelpParser().parseBeliefBase(Utilities.getKB(KB));
    }

    // TODO: very long string (> 4096 chars?)
    // TODO: input encoding??
}
