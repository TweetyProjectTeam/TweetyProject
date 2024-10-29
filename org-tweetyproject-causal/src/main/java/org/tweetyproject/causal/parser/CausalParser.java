package org.tweetyproject.causal.parser;

import org.tweetyproject.causal.syntax.CausalKnowledgeBase;
import org.tweetyproject.commons.Parser;
import org.tweetyproject.commons.ParserException;
import org.tweetyproject.logics.pl.syntax.PlFormula;

import java.io.IOException;
import java.io.Reader;

public class CausalParser extends Parser<CausalKnowledgeBase, PlFormula> {
    @Override
    public CausalKnowledgeBase parseBeliefBase(Reader reader) throws IOException, ParserException {
        return null;
    }

    @Override
    public PlFormula parseFormula(Reader reader) throws IOException, ParserException {
        return null;
    }
}
