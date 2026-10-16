package org.tweetyproject.arg.adf.bench.driver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.tweetyproject.arg.adf.bench.common.Answers;
import org.tweetyproject.arg.adf.bench.common.Json;

/**
 * Writes a Markdown report, a per-run CSV and {@code mismatches.jsonl} from a
 * JSONL result file, with one section per (semantics, task). A unit is an
 * instance with one of its frozen queries.
 * <p>
 * Per (family, size, implementation): solved units, the geometric mean of the
 * per-unit median solve times over the solved units, and the mean PAR-2 score
 * (every unsolved unit counts as twice the timeout). For the units solved by
 * both the reference ({@code old-fixed} if present, otherwise {@code old}) and
 * the candidate ({@code new-sat} if present, otherwise {@code new}): the
 * geometric mean of the per-unit speedups reference/candidate, and how many
 * answers agree.
 * <p>
 * Validation (plan, section 4.2), per solved run:
 * <ul>
 * <li>with an oracle answer: <em>oracle ok</em> (or <em>independent ok</em>
 * for VE beyond the oracle's enumeration limit) or <em>wrong</em>;</li>
 * <li>otherwise an SE witness is verified with {@link Oracle#verify}
 * (<em>independent ok</em> or <em>wrong</em>);</li>
 * <li>otherwise the reference is compared with the candidate and every other
 * implementation with the reference: <em>agreement</em> is differential
 * evidence only, <em>disagreement</em> is unresolved;</li>
 * <li>otherwise <em>unchecked</em>.</li>
 * </ul>
 * Answers are compared per task: EE by count and hash, CE by count, SE by the
 * existence of a model (witnesses may differ), DC/DS/VE by the Boolean answer.
 * Every wrong answer and disagreement is written to {@code mismatches.jsonl}
 * next to the report, with a {@code check} command that reproduces it.
 */
final class Report {

	private static final String ORACLE = "oracle";

	private Report() {}

	private record Run(String impl, String instance, String family, int n, String sem, String task, String query, String status,
			Double medianMs, Long count, String hash, Object answer, String validation, String error) {
		boolean solved() {
			return "ok".equals(status);
		}

		String unit() {
			return query.isEmpty() ? instance : instance + " " + query;
		}
	}

	static void write(Path instances, Path results, Path out, double timeoutS) throws IOException {
		Map<String, Object> header = null;
		List<Run> runs = new ArrayList<>();
		for (String line : Files.readAllLines(results)) {
			if (line.isBlank()) {
				continue;
			}
			Map<String, Object> r = Json.parseObject(line);
			if (Boolean.TRUE.equals(r.get("header"))) {
				header = r;
				continue;
			}
			runs.add(new Run((String) r.get("impl"), (String) r.get("instance"), (String) r.get("family"), ((Number) r.get("n")).intValue(),
					(String) r.get("sem"), (String) r.getOrDefault("task", "EE"), (String) r.getOrDefault("query", ""),
					(String) r.get("status"), median(r.get("timesMs")), r.get("count") == null ? null : ((Number) r.get("count")).longValue(),
					(String) r.get("answerHash"), r.get("answer"), (String) r.get("validation"), (String) r.get("error")));
		}
		Map<String, List<Run>> groups = new TreeMap<>();
		for (Run run : runs) {
			groups.computeIfAbsent(run.sem() + " " + run.task(), k -> new ArrayList<>()).add(run);
		}

		StringBuilder md = new StringBuilder("# ADF evaluation\n\n");
		if (header != null) {
			md.append("- Machine: ").append(header.get("cpu")).append(", ").append(header.get("os")).append(", ").append(header.get("java")).append('\n');
			md.append("- Commit: `").append(header.get("commit")).append("`").append(Boolean.TRUE.equals(header.get("dirty")) ? " (with uncommitted changes)" : "").append('\n');
			md.append("- JVM flags: `").append(String.join(" ", strings(header.get("jvmFlags")))).append("`, one JVM per run, pinned to one physical core\n");
			md.append("- Runner flags of the first run: `").append(String.join(" ", strings(header.get("runnerFlags")))).append("`\n");
		}
		md.append("- Per-solve timeout: ").append(fmt(timeoutS)).append(" s; parsing is excluded from solve times\n\n");
		md.append("Tasks: EE enumerate all models, CE count them, SE find one, DC/DS credulous/skeptical acceptance of an argument, ")
				.append("VE verify an interpretation. A unit is an instance with one frozen query. ")
				.append("Times are geometric means over the solved units of each row of the per-unit median solve time (ms). ")
				.append("PAR-2 is the mean over all units of the row in seconds, with unsolved units counted as 2 × timeout.\n\n");

		List<String> problems = new ArrayList<>();
		List<Map<String, Object>> mismatches = new ArrayList<>();
		Map<String, KppAdf> parsed = new HashMap<>();
		for (Map.Entry<String, List<Run>> group : groups.entrySet()) {
			md.append(section(group.getKey(), group.getValue(), instances, timeoutS, problems, mismatches, parsed));
		}

		md.append("## Crashes, errors and disagreements\n\n");
		if (problems.isEmpty()) {
			md.append("None.\n");
		} else {
			for (String p : new TreeSet<>(problems)) {
				md.append("- ").append(p.length() > 400 ? p.substring(0, 400) + "…" : p).append('\n');
			}
		}
		Files.writeString(out, md.toString());

		StringBuilder csv = new StringBuilder("impl,instance,family,n,sem,task,query,status,median_ms,count,hash,answer\n");
		for (Run r : runs) {
			csv.append(String.join(",", r.impl(), r.instance(), r.family(), String.valueOf(r.n()), r.sem(), r.task(), csvField(r.query()),
					r.status(), r.medianMs() == null ? "" : String.format(Locale.ROOT, "%.3f", r.medianMs()), Objects.toString(r.count(), ""),
					Objects.toString(r.hash(), ""), csvField(Objects.toString(r.answer(), "")))).append('\n');
		}
		Files.writeString(out.resolveSibling(out.getFileName().toString().replaceAll("\\.md$", "") + ".csv"), csv.toString());

		StringBuilder jsonl = new StringBuilder();
		for (Map<String, Object> m : mismatches) {
			jsonl.append(Json.write(m)).append('\n');
		}
		Files.writeString(out.resolveSibling("mismatches.jsonl"), jsonl.toString());
	}

	private static String section(String title, List<Run> runs, Path instances, double timeoutS, List<String> problems,
			List<Map<String, Object>> mismatches, Map<String, KppAdf> parsed) throws IOException {
		String task = runs.get(0).task(), sem = runs.get(0).sem();
		Map<String, Run> byKey = new LinkedHashMap<>();
		for (Run run : runs) {
			byKey.put(run.impl() + "|" + run.unit(), run);
		}
		List<String> impls = runs.stream().map(Run::impl).filter(i -> !i.equals(ORACLE)).distinct().sorted().toList();
		String reference = impls.contains("old-fixed") ? "old-fixed" : "old";
		String candidate = impls.contains("new-sat") ? "new-sat" : "new";
		Map<String, Map<Integer, List<Run>>> unitsByFamily = new TreeMap<>();
		Set<String> seen = new java.util.HashSet<>();
		for (Run run : runs) {
			if (seen.add(run.unit())) {
				unitsByFamily.computeIfAbsent(run.family(), k -> new TreeMap<>()).computeIfAbsent(run.n(), k -> new ArrayList<>()).add(run);
			}
		}
		boolean counts = task.equals("EE") || task.equals("CE");

		StringBuilder md = new StringBuilder("## ").append(sem).append(" · ").append(task).append("\n\n");
		md.append("Speedup = ").append(reference).append(" / ").append(candidate).append(" on units solved by both (> 1: ")
				.append(candidate).append(" is faster). ")
				.append("Agree = units solved by both with the same answer").append(task.equals("SE") ? " (existence of a model)" : "")
				.append(".\n\n");
		for (Map.Entry<String, Map<Integer, List<Run>>> family : unitsByFamily.entrySet()) {
			md.append("### Family `").append(family.getKey()).append("`\n\n");
			md.append("| n | units |");
			for (String impl : impls) {
				md.append(' ').append(impl).append(" solved | ").append(impl).append(" time ms | ").append(impl).append(" PAR-2 s |");
			}
			md.append(" speedup | agree |").append(counts ? " models (max) |" : "").append("\n|---:|---:|");
			md.append("---:|".repeat(3 * impls.size())).append("---:|---:|").append(counts ? "---:|" : "").append('\n');
			for (Map.Entry<Integer, List<Run>> size : family.getValue().entrySet()) {
				List<Run> units = size.getValue();
				md.append("| ").append(size.getKey()).append(" | ").append(units.size()).append(" |");
				for (String impl : impls) {
					List<Double> times = new ArrayList<>();
					double par2 = 0;
					int solved = 0;
					for (Run unit : units) {
						Run run = byKey.get(impl + "|" + unit.unit());
						if (run != null && run.solved()) {
							solved++;
							times.add(run.medianMs());
							par2 += run.medianMs() / 1000;
						} else {
							par2 += 2 * timeoutS;
						}
					}
					md.append(' ').append(solved).append('/').append(units.size()).append(" | ")
							.append(times.isEmpty() ? "–" : fmt(geomean(times))).append(" | ").append(fmt(par2 / units.size())).append(" |");
				}
				List<Double> speedups = new ArrayList<>();
				int both = 0, agree = 0;
				long maxModels = 0;
				for (Run unit : units) {
					for (String impl : impls) {
						Run r = byKey.get(impl + "|" + unit.unit());
						if (r != null && r.count() != null) {
							maxModels = Math.max(maxModels, r.count());
						}
					}
					Run old = byKey.get(reference + "|" + unit.unit()), neu = byKey.get(candidate + "|" + unit.unit());
					if (old != null && neu != null && old.solved() && neu.solved()) {
						both++;
						speedups.add(old.medianMs() / neu.medianMs());
						// differences are reported by the validation section
						if (sameAnswer(old, neu)) {
							agree++;
						}
					}
				}
				md.append(' ').append(speedups.isEmpty() ? "–" : fmt(geomean(speedups)) + "×").append(" | ").append(agree).append('/')
						.append(both).append(" |").append(counts ? " " + maxModels + " |" : "").append('\n');
			}
			md.append('\n');
		}

		md.append("### Validation\n\n");
		md.append("Solved runs by validation strength: *oracle ok* / *wrong* against the brute-force oracle; *independent ok*: ")
				.append("checked with the oracle's verifier beyond its enumeration limit (VE answers, SE witnesses); *agreement* / ")
				.append("*disagreement* with the other side of the ").append(reference).append("/").append(candidate)
				.append(" pair (every other implementation with ").append(reference).append("), differential evidence only; *unchecked*: nothing to compare with. ")
				.append("*unsupported*: the implementation does not offer the semantics or task.\n\n");
		md.append("| family | implementation | solved | oracle ok | independent ok | wrong | agreement | disagreement | unchecked | unsupported |\n");
		md.append("|---|---|---:|---:|---:|---:|---:|---:|---:|---:|\n");
		for (Map.Entry<String, Map<Integer, List<Run>>> family : unitsByFamily.entrySet()) {
			for (String impl : impls) {
				Map<String, Integer> c = new HashMap<>();
				for (List<Run> units : family.getValue().values()) {
					for (Run unit : units) {
						Run run = byKey.get(impl + "|" + unit.unit());
						if (run == null) {
							continue;
						}
						if (!run.solved()) {
							if ("unsupported".equals(run.status())) {
								c.merge("unsupported", 1, Integer::sum);
							} else if (!"timeout".equals(run.status()) && !"skipped".equals(run.status())) {
								problems.add("`" + run.unit() + "` " + sem + " " + task + " " + impl + ": **" + run.status() + "** "
										+ Objects.toString(run.error(), ""));
							}
							continue;
						}
						c.merge("solved", 1, Integer::sum);
						String otherImpl = impl.equals(reference) ? candidate : reference;
						String verdict = validate(run, byKey.get(ORACLE + "|" + unit.unit()), otherImpl.equals(impl) ? null
								: byKey.get(otherImpl + "|" + unit.unit()), instances, parsed);
						c.merge(verdict, 1, Integer::sum);
						if (verdict.equals("wrong") || verdict.equals("disagreement")) {
							mismatches.add(mismatch(verdict, run, byKey.get(ORACLE + "|" + unit.unit()), otherImpl, instances));
							problems.add("`" + run.unit() + "` " + sem + " " + task + " " + impl + ": **" + verdict + "** (" + answerText(run)
									+ ")");
						}
					}
				}
				md.append("| ").append(family.getKey()).append(" | ").append(impl);
				for (String k : List.of("solved", "oracle ok", "independent ok", "wrong", "agreement", "disagreement", "unchecked",
						"unsupported")) {
					md.append(" | ").append(c.getOrDefault(k, 0));
				}
				md.append(" |\n");
			}
		}
		return md.append('\n').toString();
	}

	/** @return the validation verdict of a solved run */
	private static String validate(Run run, Run oracle, Run other, Path instances, Map<String, KppAdf> parsed) throws IOException {
		if (oracle != null && oracle.solved()) {
			boolean ok = sameAnswer(run, oracle);
			if (ok && run.task().equals("SE") && run.answer() != null) {
				ok = Boolean.TRUE.equals(verifyWitness(run, instances, parsed));
			}
			if (!ok) {
				return "wrong";
			}
			return "independent-check".equals(oracle.validation()) ? "independent ok" : "oracle ok";
		}
		if (run.task().equals("SE") && run.answer() != null) {
			Boolean verified = verifyWitness(run, instances, parsed);
			if (verified != null) {
				return verified ? "independent ok" : "wrong";
			}
		}
		if (other != null && other.solved()) {
			return sameAnswer(run, other) ? "agreement" : "disagreement";
		}
		return "unchecked";
	}

	private static Boolean verifyWitness(Run run, Path instances, Map<String, KppAdf> parsed) throws IOException {
		KppAdf adf = parsed.get(run.instance());
		if (adf == null) {
			adf = KppAdf.parse(instances.resolve(run.instance()));
			parsed.put(run.instance(), adf);
		}
		Oracle oracle = new Oracle(adf);
		List<Set<String>> sets = Answers.parseCanonical((String) run.answer());
		return oracle.verify(oracle.interpretation(sets.get(0), sets.get(1)), run.sem());
	}

	private static boolean sameAnswer(Run a, Run b) {
		return switch (a.task()) {
			case "EE" -> Objects.equals(a.count(), b.count()) && Objects.equals(a.hash(), b.hash());
			case "CE" -> Objects.equals(a.count(), b.count());
			case "SE" -> (a.answer() == null) == (b.answer() == null);
			default -> Objects.equals(a.answer(), b.answer());
		};
	}

	private static String answerText(Run r) {
		return switch (r.task()) {
			case "EE" -> r.count() + " models, " + r.hash();
			case "CE" -> r.count() + " models";
			case "SE" -> r.answer() == null ? "no model" : "witness " + r.answer();
			default -> String.valueOf(r.answer());
		};
	}

	private static Map<String, Object> mismatch(String kind, Run run, Run oracle, String otherImpl, Path instances) {
		boolean againstOracle = kind.equals("wrong") && oracle != null && oracle.solved();
		Map<String, Object> m = new LinkedHashMap<>();
		m.put("kind", kind);
		m.put("instance", run.instance());
		m.put("sem", run.sem());
		m.put("task", run.task());
		m.put("query", run.query());
		m.put("impl", run.impl());
		m.put("answer", answerText(run));
		m.put("against", againstOracle ? ORACLE : kind.equals("wrong") ? "verifier" : otherImpl);
		if (againstOracle) {
			m.put("expected", answerText(oracle));
		}
		StringBuilder repro = new StringBuilder("./driver.sh check --file ").append(instances.resolve(run.instance())).append(" --sem ")
				.append(run.sem()).append(" --task ").append(run.task());
		if (run.query().startsWith("arg=")) {
			repro.append(" --arg ").append(run.query().substring(4));
		} else if (run.query().startsWith("interpretation=")) {
			repro.append(" --interpretation '").append(run.query().substring(15)).append('\'');
		}
		repro.append(" --impls ").append(run.impl()).append(kind.equals("disagreement") ? "," + otherImpl : "");
		m.put("repro", repro.toString());
		return m;
	}

	private static String csvField(String s) {
		return s.contains(",") || s.contains("\"") ? '"' + s.replace("\"", "\"\"") + '"' : s;
	}

	private static Double median(Object times) {
		if (!(times instanceof List<?> list) || list.isEmpty()) {
			return null;
		}
		List<Double> values = new ArrayList<>();
		for (Object o : list) {
			values.add(((Number) o).doubleValue());
		}
		Collections.sort(values);
		int m = values.size() / 2;
		return values.size() % 2 == 1 ? values.get(m) : (values.get(m - 1) + values.get(m)) / 2;
	}

	private static double geomean(List<Double> values) {
		double log = 0;
		for (double v : values) {
			log += Math.log(Math.max(v, 1e-3));
		}
		return Math.exp(log / values.size());
	}

	private static String fmt(double v) {
		if (v >= 100) {
			return String.format(Locale.ROOT, "%.0f", v);
		} else if (v >= 10) {
			return String.format(Locale.ROOT, "%.1f", v);
		}
		return String.format(Locale.ROOT, "%.2f", v);
	}

	private static List<String> strings(Object list) {
		List<String> result = new ArrayList<>();
		for (Object o : (List<?>) list) {
			result.add(String.valueOf(o));
		}
		return result;
	}

}
