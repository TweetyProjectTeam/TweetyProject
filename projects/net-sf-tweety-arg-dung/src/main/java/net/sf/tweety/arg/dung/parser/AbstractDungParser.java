package net.sf.tweety.arg.dung.parser;

import java.io.IOException;
import java.io.Reader;
import java.util.Collection;
import java.util.HashSet;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.sf.tweety.arg.dung.DungTheory;
import net.sf.tweety.arg.dung.semantics.ArgumentStatus;
import net.sf.tweety.arg.dung.semantics.Extension;
import net.sf.tweety.arg.dung.semantics.Labeling;
import net.sf.tweety.arg.dung.syntax.Argument;
import net.sf.tweety.commons.Formula;
import net.sf.tweety.commons.Parser;
import net.sf.tweety.commons.ParserException;

/**
 * This abstract class gives a template for parsers of files representing
 * abstract argumentation frameworks. The file is parsed into the Tweety format
 * for abstract argumentation frameworks.
 * 
 * @author Matthias Thimm
 */
public abstract class AbstractDungParser extends Parser<DungTheory> {

	/**
	 * Retrieves the parser for the given file format.
	 * 
	 * @param f
	 *            some file format
	 * @return a parser or null if the format is not supported.
	 */
	public static AbstractDungParser getParser(FileFormat f) {
		if (f.equals(FileFormat.TGF))
			return new TgfParser();
		if (f.equals(FileFormat.APX))
			return new ApxParser();
		if (f.equals(FileFormat.CNF))
			return new CnfParser();
		return null;
	}

	@Override
	public DungTheory parseBeliefBase(Reader reader) throws IOException, ParserException {
		return parse(reader);

	}

	@Override
	public Formula parseFormula(Reader reader) throws IOException, ParserException {
		throw new ParserException("Method AbstractDungParser.parseFormula not implemted");
	}

	/**
	 * Returns a collection view of the given set of arguments
	 * 
	 * @param s
	 *            some string in the format "[arg1,...,argn]".
	 * @return a collection view of the given arguments
	 */
	public static Extension parseArgumentList(String s) {
		s = s.trim();
		if (!s.startsWith("[") || !s.endsWith("]"))
			throw new IllegalArgumentException("Expected list of arguments but encountered " + s);
		String s2 = s.substring(1, s.length() - 1);
		StringTokenizer tokenizer = new StringTokenizer(s2, ",");
		Extension args = new Extension();
		while (tokenizer.hasMoreTokens())
			args.add(new Argument(tokenizer.nextToken().trim()));
		return args;
	}

	/**
	 * Returns a collection view of the given set of extensions (=sets of
	 * arguments)
	 * 
	 * @param s
	 *            some string in the format
	 *            "[[arg1,...,argn],...,[arg1,...,argn]]".
	 * @return a collection view of the given set of extensions
	 */
	public static Collection<Collection<Argument>> parseExtensionList(String s) {
		s = s.trim();
		if (!s.startsWith("[") || !s.endsWith("]"))
			throw new IllegalArgumentException("Expected list of arguments but encountered " + s);
		String s2 = s.substring(1, s.length() - 1);
		Pattern pattern = Pattern.compile("\\[(.*?)\\]");
		Matcher matcher = pattern.matcher(s2);
		Collection<Collection<Argument>> result = new HashSet<Collection<Argument>>();
		while (matcher.find()) {
			result.add(AbstractDungParser.parseArgumentList(matcher.group()));
		}
		return result;
	}

	/**
	 * Returns a collection view of the given set of labelings
	 * 
	 * @param s
	 *            some string in the format
	 *            "[[IN1,...,INM],[OUT1,...,OUTN],[UNDEC1,...,UNDECM]]\n...[[IN1,...,INM],[OUT1,...,OUTN],[UNDEC1,...,UNDECM]]".
	 * @return a collection view of the given set of labelings
	 */
	public static Collection<Labeling> parseLabelingList(String s) {
		s = s.trim();
		Collection<Labeling> result = new HashSet<Labeling>();
		StringTokenizer tokenizer = new StringTokenizer(s, "\n");
		while (tokenizer.hasMoreTokens())
			result.add(AbstractDungParser.parseLabeling(tokenizer.nextToken()));
		return result;
	}

	/**
	 * Parses the given string (either "YES" or "NO") to a boolean value.
	 * 
	 * @param s
	 *            some string (either "YES" or "NO")
	 * @return a boolean representing the string.
	 */
	public static boolean parseBoolean(String s) {
		s = s.trim();
		if (s.toLowerCase().equals("yes"))
			return true;
		return false;
	}

	/**
	 * Returns a labeling parsed from the given string
	 * 
	 * @param s
	 *            some string in the format
	 *            [[IN1,...,INM],[OUT1,...,OUTN],[UNDEC1,...,UNDECM]]
	 * @return a labeling.
	 */
	public static Labeling parseLabeling(String s) {
		s = s.trim();
		if (!s.startsWith("[") || !s.endsWith("]"))
			throw new IllegalArgumentException("Expected labeling but encountered " + s);
		String s2 = s.substring(1, s.length() - 1);
		Collection<Argument> in, out, undec;
		int endIn = s2.indexOf("],[");
		int endOut = s2.indexOf("],[", endIn + 1);
		in = AbstractDungParser.parseArgumentList(s2.substring(0, endIn + 1));
		out = AbstractDungParser.parseArgumentList(s2.substring(endIn + 2, endOut + 1));
		undec = AbstractDungParser.parseArgumentList(s2.substring(endOut + 2, s2.length()));
		Labeling lab = new Labeling();
		for (Argument arg : in)
			lab.put(arg, ArgumentStatus.IN);
		for (Argument arg : out)
			lab.put(arg, ArgumentStatus.OUT);
		for (Argument arg : undec)
			lab.put(arg, ArgumentStatus.UNDECIDED);
		return lab;
	}

	/**
	 * Parses the given file into an abstract argumentation framework
	 * 
	 * @param f
	 *            some file
	 * @return an abstract argumentation framework
	 * @throws IOException
	 *             for all errors concerning file reading.
	 */
	public abstract DungTheory parse(Reader reader) throws IOException;
}
