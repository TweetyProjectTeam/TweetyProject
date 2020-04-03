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
 *  Copyright 2019 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package net.sf.tweety.arg.adf.parser;

import java.io.IOException;
import java.io.Reader;
import java.nio.CharBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Stack;

import net.sf.tweety.arg.adf.semantics.LinkStrategy;
import net.sf.tweety.arg.adf.syntax.Argument;
import net.sf.tweety.arg.adf.syntax.acc.AcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.acc.ConjunctionAcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.acc.DisjunctionAcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.acc.EquivalenceAcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.acc.ExclusiveDisjunctionAcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.acc.ImplicationAcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.acc.NegationAcceptanceCondition;
import net.sf.tweety.arg.adf.syntax.adf.AbstractDialecticalFramework;
import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.Parser;
import net.sf.tweety.commons.ParserException;

/**
 * A parser for the input format described at
 * https://www.cs.helsinki.fi/group/coreo/k++adf/
 * 
 * Since the input format is only described informally, we assume that each
 * argument has to be defined before it can be used in some way, e.g. 's(a).
 * ac(a,c(v)).' is a valid input but 'ac(a,c(v)). s(a).' is not. This assumption
 * makes error handling and reporting much easier. However, this also means that
 * the order of the input statements matter and we are thus not fully
 * declarative.
 * 
 * @author Mathias Hofer
 *
 */
public class KppADFFormatParser extends Parser<AbstractDialecticalFramework, Formula> {

	private static final int BUFFER_CAPACITY = 8192;
	
	private final boolean lazy;
	
	private final LinkStrategy linkStrategy;

	/**
	 * 
	 * @param linkStrategy
	 * @param lazy
	 */
	public KppADFFormatParser(LinkStrategy linkStrategy, boolean lazy) {
		this.linkStrategy = linkStrategy;
		this.lazy = lazy;
	}

	@Override
	public AbstractDialecticalFramework parseBeliefBase(Reader reader) throws IOException, ParserException {
		Map<String, Argument> arguments = new HashMap<String, Argument>();
		Map<Argument, AcceptanceCondition> accByArgument = new HashMap<Argument, AcceptanceCondition>();
		Stack<Node> nodes = new Stack<Node>();
		CharBuffer buf = CharBuffer.allocate(BUFFER_CAPACITY);
		StringBuilder token = new StringBuilder();
		while (reader.read(buf) > 0) {
			buf.flip();
			while (buf.hasRemaining()) {
				char c = buf.get();
				if (c == '(') {
					String parsed = token.toString();
					token = new StringBuilder(); // consume token
					switch (parsed) {
					case "s":
						nodes.push(new SNode(arguments));
						break;
					case "ac":
						nodes.push(new ACNode(accByArgument));
						break;
					case "c":
						nodes.push(new CNode());
						break;
					case "neg":
						nodes.push(new NegNode());
						break;
					case "and":
						nodes.push(new AndNode());
						break;
					case "or":
						nodes.push(new OrNode());
						break;
					case "imp":
						nodes.push(new ImpNode());
						break;
					case "xor":
						nodes.push(new XorNode());
						break;
					case "iff":
						nodes.push(new IffNode());
						break;
					default:
						throw new ParserException("Unknown expression: " + parsed);
					}
				} else if (c == ',') {
					// we only have to deal with identifiers
					if (token.length() > 0) {
						Node top = nodes.peek();
						top.addNode(new IdentifierNode(token.toString(), arguments));
						// consume token
						token = new StringBuilder();
					}
				} else if (c == ')') {
					// check if we have to process an identifier
					if (token.length() > 0) {
						Node top = nodes.peek();
						top.addNode(new IdentifierNode(token.toString(), arguments));
						// consume token
						token = new StringBuilder();
					}

					// keep the last node for the '.' case
					if (nodes.size() > 1) {
						Node child = nodes.pop();
						Node parent = nodes.peek();
						parent.addNode(child);
					}
				} else if (c == '.') {
					Node root = nodes.pop();
					if (nodes.isEmpty()) {
						root.parseStatement();
					} else {
						throw new ParserException("Unprocessed node(s) on the stack!");
					}
				} else if (!Character.isWhitespace(c)) {
					// build token until there is something to do
					token.append(c);
				}
			}
			buf.clear();
		}

		if (!nodes.isEmpty()) {
			throw new ParserException("Unprocessed node(s) on the stack!");
		}
		
		AbstractDialecticalFramework.Builder builder = AbstractDialecticalFramework.fromMap(accByArgument);
		if (lazy) {
			builder = builder.lazy(linkStrategy);
		} else {
			builder = builder.eager(linkStrategy);
		}
		return builder.build();
	}

	@Override
	public Formula parseFormula(Reader reader) throws IOException, ParserException {
		throw new UnsupportedOperationException();
	}

	/**
	 * Represents a node of the parse-tree.
	 * 
	 * Provides all necessary methods and throws ParserExceptions if they are
	 * called on wrong nodes. This basically outsources error-handling to the
	 * nodes and keeps the parser logic clean.
	 */
	private abstract class Node {

		private String name;

		public Node(String name) {
			this.name = name;
		}

		abstract void addNode(Node node) throws ParserException;

		void parseStatement() throws ParserException {
			throw new ParserException(toString() + " neither 's' nor 'ac'!");
		}

		AcceptanceCondition parseFormula() throws ParserException {
			throw new ParserException(toString() + " not a formula!");
		}

		AcceptanceCondition parseSpecialFormula() throws ParserException {
			throw new ParserException(toString() + " neither 'v' (verum) nor 'f' (falsum)!");
		}

		Argument parseArgument() throws ParserException {
			throw new ParserException(toString() + " not a proposition!");
		}

		public String getName() {
			return name;
		}

		@Override
		public String toString() {
			return name;
		}
	}

	private class SNode extends UnaryNode {
		private Map<String, Argument> statements;

		public SNode(Map<String, Argument> statements) {
			super("s");
			this.statements = statements;
		}

		@Override
		public void parseStatement() throws ParserException {
			// call parseArgument() just for error-handling purposes
			// it returns null -> is absent in statements -> create new one
			// it does not return null -> is in statements -> do nothing
			getChild().parseArgument();
			String argName = getChild().getName();
			statements.putIfAbsent(argName, new Argument(argName));
		}
	}

	private class ACNode extends BinaryNode {

		private Map<Argument, AcceptanceCondition> accByArgument;
		
		public ACNode(Map<Argument, AcceptanceCondition> accByArgument) {
			super("ac");
			this.accByArgument = accByArgument;
		}

		@Override
		public void parseStatement() throws ParserException {
			Argument argument = getFirst().parseArgument();
			AcceptanceCondition acceptanceCondition = getSecond().parseFormula();
			accByArgument.put(argument, acceptanceCondition);
		}
	}

	private abstract class UnaryNode extends Node {
		private Node child;

		public UnaryNode(String name) {
			super(name);
		}

		@Override
		public void addNode(Node node) throws ParserException {
			if (child == null) {
				child = node;
			} else {
				throw new ParserException("Cannot add node '" + node + "' to " + this);
			}
		}

		public Node getChild() {
			return child;
		}

		@Override
		public String toString() {
			return getName() + "(" + child + ")";
		}
	}

	private abstract class BinaryNode extends Node {
		private Node first;
		private Node second;

		public BinaryNode(String name) {
			super(name);
		}

		@Override
		public void addNode(Node node) {
			if (first == null) {
				first = node;
			} else if (second == null) {
				second = node;
			} else {
				throw new ParserException("Cannot add node '" + node + "' to " + this);
			}
		}

		public Node getFirst() {
			return first;
		}

		public Node getSecond() {
			return second;
		}

		@Override
		public String toString() {
			return getName() + "(" + first + "," + second + ")";
		}
	}

	private class IdentifierNode extends Node {
		private Map<String, Argument> statements;

		public IdentifierNode(String name, Map<String, Argument> statements) {
			super(name);
			this.statements = statements;
		}

		@Override
		public void addNode(Node node) throws ParserException {
			throw new ParserException("Cannot add node '" + node + "' to " + this);
		}

		@Override
		public Argument parseArgument() throws ParserException {
			return statements.get(getName());
		}

		@Override
		AcceptanceCondition parseFormula() throws ParserException {
			return parseArgument();
		}

		@Override
		AcceptanceCondition parseSpecialFormula() throws ParserException {
			if ("v".equals(getName())) {
				return AcceptanceCondition.TAUTOLOGY;
			} else if ("f".equals(getName())) {
				return AcceptanceCondition.CONTRADICTION;
			}
			return super.parseSpecialFormula();
		}
	}

	private class CNode extends UnaryNode {

		public CNode() {
			super("c");
		}

		@Override
		public AcceptanceCondition parseFormula() {
			return getChild().parseSpecialFormula();
		}
	}

	private class NegNode extends UnaryNode {

		public NegNode() {
			super("neg");
		}

		@Override
		public AcceptanceCondition parseFormula() {
			return new NegationAcceptanceCondition(getChild().parseFormula());
		}
	}

	private class AndNode extends BinaryNode {

		public AndNode() {
			super("and");
		}

		@Override
		public AcceptanceCondition parseFormula() {
			return new ConjunctionAcceptanceCondition(getFirst().parseFormula(), getSecond().parseFormula());
		}
	}

	private class OrNode extends BinaryNode {

		public OrNode() {
			super("or");
		}

		@Override
		public AcceptanceCondition parseFormula() throws ParserException {
			return new DisjunctionAcceptanceCondition(getFirst().parseFormula(), getSecond().parseFormula());
		}
	}

	private class ImpNode extends BinaryNode {

		public ImpNode() {
			super("imp");
		}

		@Override
		public AcceptanceCondition parseFormula() throws ParserException {
			return new ImplicationAcceptanceCondition(getFirst().parseFormula(), getSecond().parseFormula());
		}
	}

	private class XorNode extends BinaryNode {

		public XorNode() {
			super("xor");
		}

		@Override
		public AcceptanceCondition parseFormula() throws ParserException {
			return new ExclusiveDisjunctionAcceptanceCondition(getFirst().parseFormula(), getSecond().parseFormula());
		}
	}

	private class IffNode extends BinaryNode {

		public IffNode() {
			super("iff");
		}

		@Override
		public AcceptanceCondition parseFormula() throws ParserException {
			return new EquivalenceAcceptanceCondition(getFirst().parseFormula(), getSecond().parseFormula());
		}
	}
}
