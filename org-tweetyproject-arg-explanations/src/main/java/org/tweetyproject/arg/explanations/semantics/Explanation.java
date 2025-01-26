package org.tweetyproject.arg.explanations.semantics;

import org.tweetyproject.arg.dung.semantics.AbstractArgumentationInterpretation;
import org.tweetyproject.arg.dung.syntax.Argument;
import org.tweetyproject.arg.dung.syntax.DungTheory;

import java.util.Collection;

public abstract class Explanation extends AbstractArgumentationInterpretation<DungTheory> implements Collection<Argument>,Comparable<Explanation> {
}
