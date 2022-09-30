/**
 * This file is part of jArgSemSAT
 * <p>
 * Copyright (c) 2015 Federico Cerutti <federico.cerutti@acm.org>
 * <p>
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * <p>
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 * <p>
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package org.tweetyproject.arg.peaf.inducers.jargsemsat.jargsemsat.alg;

import org.tweetyproject.arg.peaf.inducers.jargsemsat.jargsemsat.datastructures.DungAF;
import org.tweetyproject.arg.peaf.inducers.jargsemsat.jargsemsat.datastructures.Encoding;
import org.tweetyproject.arg.peaf.inducers.jargsemsat.jargsemsat.datastructures.Labelling;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Iterator;
import java.util.Vector;

public class ProboMain {

	public final static String sw = "jArgSemSAT";

	public final static String rev = "0.1.7";

	public final static int PARSE_CONTINUE = 0;

	public final static int PARSE_EXIT = 10;

	public final static int PARSE_ERROR = -10;

	public final static int PARSE_UNABLE = -20;

	public final static String[] acceptedformats = {"apx"};

	public final static String[] acceptedproblems = {"DC-CO", "DC-GR", "DC-PR",
			"DC-ST", "DS-CO", "DS-GR", "DS-PR", "DS-ST", "EE-CO", "EE-GR",
			"EE-PR", "EE-ST", "SE-CO", "SE-GR", "SE-PR", "SE-ST"};

	public final static String complete_string_const = "CO";

	public final static String preferred_string_const = "PR";

	public final static String grounded_string_const = "GR";

	public final static String stable_string_const = "ST";

	public final static String credulous = "DC";

	public final static String skeptical = "DS";

	public final static String enumerateall = "EE";

	public final static String enumeratesome = "SE";

	public static String inputfile = null;

	public static String problem = null;

	public static String semantics = null;

	public static Encoding global_enc = Encoding.defaultEncoding();

	public static String sat = null;

	public static String argumentDecision = null;

	static void printArray(String[] arr) {
		int i = 0;
		System.out.print("[");
		for (i = 0; i < arr.length; i++) {
			System.out.print(arr[i]);
			if (i != arr.length - 1)
				System.out.print(",");
		}
		System.out.println("]");
	}

	static boolean isInArray(String el, String[] arr) {
		int i = 0;
		for (i = 0; i < arr.length; i++) {
			if (arr[i].compareTo(el) == 0)
				return true;
		}
		return false;
	}

	static void showHelp() {
		System.out.println(rev);

		System.out.println(sw + " Copyright (C) 2015");
		System.out.println("Federico Cerutti <federico.cerutti@org>");
		System.out.println("Mauro Vallati <m.vallati@hud.ac.uk>");
		System.out.println(
				"Massimiliano Giacomin <massimiliano.giacomin@unibs.it>");

		System.out.println("This program comes with ABSOLUTELY NO WARRANTY");
		System.out.println("This is free software, under the MIT license");

		System.out.println("*************************");
		System.out.println(
				"jArgSemSAT has been designed for providing an efficient, self-contained, Java library,");
		System.out.println(
				"therefore it is not suitable to be used for evaluating the performance of the ArgSemSAT approach.");
		System.out.println(
				"On this regard, the C++ version, available at http://sourceforge.net/projects/argsemsat/ , must be used.");
		System.out.println("************************* ");

		System.out.println("#### Running");
		System.out.println("./jPrefSAT <param1> ... <paramN>");
		System.out.println("--help\t\t\t this help");

		System.out.println("-f <filename>\t\t input file name for a problem");
		System.out.println("-fo <format>\t\t format of the input file");
		System.out.println("-p <problem>\t\t problem to be solved");

		System.out.println("--formats\t\t list of supported file types");
		System.out.println("--problems\t\t list of supported problems");
		System.out.println(
				"--ExtEnc <CIr><CIl><COr><COl><CUr><CUl> sequence of 6 booleans without spaces: by default 101010");
		System.out.println("--sat  SAT solver full path");

		return;
	}

	static void authorInfo() {
		System.out.println(sw + " " + rev);
		System.out.println("Federico Cerutti <federico.cerutti@org>");
		System.out.println("Mauro Vallati <m.vallati@hud.ac.uk>");
		System.out.println(
				"Massimiliano Giacomin <massimiliano.giacomin@unibs.it>");

	}

	static int parseParameters(String[] args) {
		if (args.length == 0) {
			authorInfo();
			return PARSE_EXIT;
		}

		for (int k = 0; k < args.length; k++) {
			if ("--formats".compareTo(args[k]) == 0) {
				printArray(acceptedformats);
				return PARSE_EXIT;
			} else if ("--problems".compareTo(args[k]) == 0) {
				printArray(acceptedproblems);
				return PARSE_EXIT;
			} else if ("--help".compareTo(args[k]) == 0) {
				showHelp();
				return PARSE_EXIT;
			} else if ("-f".compareTo(args[k]) == 0) {
				inputfile = args[++k];

			} else if ("-a".compareTo(args[k]) == 0) {
				argumentDecision = args[++k];
			} else if ("-fo".compareTo(args[k]) == 0) {
				if (!isInArray(args[++k], acceptedformats)) {
					return PARSE_UNABLE;
				}
			} else if ("-p".compareTo(args[k]) == 0) {
				String p = args[++k];
				if (!isInArray(p, acceptedproblems)) {
					return PARSE_UNABLE;
				}

				int dash = p.indexOf("-");
				if (dash == -1) {
					return PARSE_ERROR;
				}
				problem = p.substring(0, dash);
				semantics = p.substring(dash + 1);
			} else if ("--ExtEnc".compareTo(args[k]) == 0) {
				try {
					global_enc = new Encoding(args[++k]);
				} catch (Exception e) {
					e.printStackTrace();
					return PARSE_ERROR;
				}
			} else if ("--sat".compareTo(args[k]) == 0) {
				sat = args[++k];
				if (sat.charAt(0) == '"') {
					sat = sat.substring(1);
					while (!sat.contains("\"")) {
						sat = sat.concat(args[++k]);
					}
					sat = sat.substring(0, sat.indexOf('"'));
				}
			} else {
				System.out.println("Unrecognised parameter: " + args[k]);
				return PARSE_ERROR;
			}
		}
		return 1;
	}

	public static void printbooleanprobo(boolean res) {
		if (res)
			System.out.println("YES");
		else
			System.out.println("NO");
	}

	public static void printvectorlabellings(Vector<Labelling> res) {
		System.out.print("[");

		Iterator<Labelling> it = res.iterator();
		while (it.hasNext()) {
			System.out.print(it.next().toString());

			if (it.hasNext())
				System.out.print(",");
		}

		System.out.println("]");
	}

	public static void main(String[] args) {
		int p = parseParameters(args);

		if (p == PARSE_EXIT) {
			System.exit(0);
		}
		if (p == PARSE_ERROR || p == PARSE_UNABLE) {
			showHelp();
			System.exit(-127);
		}

		DungAF framework = new DungAF();
		try {
			if (!framework.readFile(inputfile)) {
				showHelp();
				System.exit(-1);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			System.exit(-1);
		} catch (IOException e) {
			e.printStackTrace();
			System.exit(-1);
		}

		Vector<Labelling> res = new Vector<Labelling>();

		if (semantics.compareToIgnoreCase(complete_string_const) == 0) {
			if (problem.compareToIgnoreCase(enumerateall) == 0) {
				CompleteSemantics.extensions(res, framework, global_enc, null,
						false);
				printvectorlabellings(res);
			} else if (problem.compareToIgnoreCase(credulous) == 0) {
				printbooleanprobo(CompleteSemantics.credulousAcceptance(
						argumentDecision, framework, global_enc));
			} else if (problem.compareToIgnoreCase(skeptical) == 0) {
				printbooleanprobo(CompleteSemantics.skepticalAcceptance(
						argumentDecision, framework, global_enc));
			} else if (problem.compareToIgnoreCase(enumeratesome) == 0) {
				Labelling ret = new Labelling();
				CompleteSemantics.someExtension(ret, framework, global_enc);
				System.out.println(ret);
			}

		} else if (semantics.compareToIgnoreCase(preferred_string_const) == 0) {
			if (problem.compareToIgnoreCase(enumerateall) == 0) {
				PreferredSemantics.extensions(res, framework, global_enc, null,
						false);
				printvectorlabellings(res);
			} else if (problem.compareToIgnoreCase(credulous) == 0) {
				printbooleanprobo(PreferredSemantics.credulousAcceptance(
						argumentDecision, framework, global_enc));
			} else if (problem.compareToIgnoreCase(skeptical) == 0) {
				printbooleanprobo(PreferredSemantics.skepticalAcceptance(
						argumentDecision, framework, global_enc));
			} else if (problem.compareToIgnoreCase(enumeratesome) == 0) {
				Labelling ret = new Labelling();
				PreferredSemantics.someExtension(ret, framework, global_enc);
				System.out.println(ret);
			}
		} else if (semantics.compareToIgnoreCase(grounded_string_const) == 0) {
			if (problem.compareToIgnoreCase(enumerateall) == 0) {
				GroundedSemantics.extensions(res, framework, global_enc, null,
						false);
				printvectorlabellings(res);
			} else if (problem.compareToIgnoreCase(credulous) == 0) {
				printbooleanprobo(GroundedSemantics.credulousAcceptance(
						argumentDecision, framework, global_enc));
			} else if (problem.compareToIgnoreCase(skeptical) == 0) {
				printbooleanprobo(GroundedSemantics.skepticalAcceptance(
						argumentDecision, framework, global_enc));
			} else if (problem.compareToIgnoreCase(enumeratesome) == 0) {
				Labelling ret = new Labelling();
				GroundedSemantics.someExtension(ret, framework, global_enc);
				System.out.println(ret);
			}
		} else if (semantics.compareToIgnoreCase(stable_string_const) == 0) {
			if (problem.compareToIgnoreCase(enumerateall) == 0) {
				StableSemantics.extensions(res, framework, global_enc, null,
						false);
				printvectorlabellings(res);
			} else if (problem.compareToIgnoreCase(credulous) == 0) {
				printbooleanprobo(StableSemantics.credulousAcceptance(
						argumentDecision, framework, global_enc));
			} else if (problem.compareToIgnoreCase(skeptical) == 0) {
				printbooleanprobo(StableSemantics.skepticalAcceptance(
						argumentDecision, framework, global_enc));
			} else if (problem.compareToIgnoreCase(enumeratesome) == 0) {
				Labelling ret = new Labelling();
				StableSemantics.someExtension(ret, framework, global_enc);

				if (ret.empty())
					printbooleanprobo(false);
				else
					System.out.println(ret);
			}
		}

		// this does not consider the encoding
		/*
		 * HashSet<HashSet<String>> exts = framework.getPreferredExts();
		 *
		 * System.out.print("["); Iterator<HashSet<String>> it =
		 * exts.iterator(); while (it.hasNext()) { System.out.print("[");
		 * Iterator<String>ar = it.next().iterator(); while (ar.hasNext()) {
		 * System.out.print(ar.next()); if (ar.hasNext()) {
		 * System.out.print(","); } } System.out.print("]");
		 *
		 * if(it.hasNext()) System.out.print(",");
		 *
		 * } System.out.print("]");
		 */
	}

}
