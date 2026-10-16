package org.tweetyproject.arg.adf.bench.driver;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.TreeMap;
import java.util.TreeSet;

import org.tweetyproject.arg.adf.bench.common.Json;

/**
 * Writes a Markdown report and a per-instance CSV from a JSONL result file.
 * <p>
 * Per (family, size, implementation): solved instances, the geometric mean of
 * the per-instance median solve times over the solved instances, and the mean
 * PAR-2 score (every unsolved instance counts as twice the timeout). For the
 * instances solved by both the reference ({@code old-fixed} if present,
 * otherwise {@code old}) and {@code new}: the geometric mean of the
 * per-instance speedups reference/new, and whether model count and answer hash
 * agree. Crashes and errors are listed for every implementation.
 * <p>
 * Validation (plan, section 4.2): a solved run is {@code oracle}-checked if the
 * oracle solved the instance, and then either verified or wrong. Otherwise it
 * is compared with the other side of the reference/new pair ({@code old} with
 * the reference): agreement is only differential evidence, a difference is an
 * unresolved disagreement. Every wrong answer and disagreement is written to
 * {@code mismatches.jsonl} next to the report, with a reproduction command.
 */
final class Report {

	private Report() {}

	private record Run(String impl, String instance, String family, int n, String status, Double medianMs, Long count, String hash,
			String error) {
		boolean solved() {
			return "ok".equals(status);
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
					(String) r.get("status"), median(r.get("timesMs")), r.get("count") == null ? null : ((Number) r.get("count")).longValue(),
					(String) r.get("answerHash"), (String) r.get("error")));
		}
		Map<String, Run> byKey = new LinkedHashMap<>();
		for (Run run : runs) {
			byKey.put(run.impl() + "|" + run.instance(), run);
		}
		List<String> impls = runs.stream().map(Run::impl).filter(i -> !i.equals(ORACLE)).distinct().sorted().toList();
		String reference = impls.contains("old-fixed") ? "old-fixed" : "old";
		String sem = header == null ? "?" : String.valueOf(((List<?>) header.get("runnerFlags")).get(3));

		StringBuilder md = new StringBuilder();
		md.append("# ADF evaluation: old implementation vs. new simple reasoners\n\n");
		if (header != null) {
			md.append("- Semantics / task: **").append(sem).append("**, enumerate all models (EE)\n");
			md.append("- Machine: ").append(header.get("cpu")).append(", ").append(header.get("os")).append(", ").append(header.get("java")).append('\n');
			md.append("- Commit: `").append(header.get("commit")).append("`").append(Boolean.TRUE.equals(header.get("dirty")) ? " (with uncommitted changes)" : "").append('\n');
			md.append("- JVM flags: `").append(String.join(" ", strings(header.get("jvmFlags")))).append("`, one JVM per run, pinned to one physical core\n");
			md.append("- Runner flags: `").append(String.join(" ", strings(header.get("runnerFlags")))).append("`\n");
			md.append("- Per-solve timeout: ").append(fmt(timeoutS)).append(" s; parsing is excluded from solve times\n\n");
		}
		md.append("Times are geometric means over the solved instances of each row of the per-instance median solve time (ms). ")
				.append("PAR-2 is the mean over all instances of the row in seconds, with unsolved instances counted as 2 × timeout. ")
				.append("Speedup = ").append(reference).append(" / new on instances solved by both (> 1: new is faster). ")
				.append("Agree = instances solved by both with equal model count and answer hash.\n\n");

		Map<String, Map<Integer, List<String>>> instancesByFamily = new TreeMap<>();
		for (Run run : runs) {
			instancesByFamily.computeIfAbsent(run.family(), k -> new TreeMap<>()).computeIfAbsent(run.n(), k -> new ArrayList<>());
			List<String> list = instancesByFamily.get(run.family()).get(run.n());
			if (!list.contains(run.instance())) {
				list.add(run.instance());
			}
		}
		List<String> problems = new ArrayList<>();
		for (Map.Entry<String, Map<Integer, List<String>>> family : instancesByFamily.entrySet()) {
			md.append("## Family `").append(family.getKey()).append("`\n\n");
			md.append("| n |");
			for (String impl : impls) {
				md.append(' ').append(impl).append(" solved | ").append(impl).append(" time ms | ").append(impl).append(" PAR-2 s |");
			}
			md.append(" speedup | agree | models (max) |\n|---:|");
			md.append("---:|".repeat(3 * impls.size())).append("---:|---:|---:|\n");
			for (Map.Entry<Integer, List<String>> size : family.getValue().entrySet()) {
				md.append("| ").append(size.getKey()).append(" |");
				for (String impl : impls) {
					List<Double> times = new ArrayList<>();
					double par2 = 0;
					int solved = 0;
					for (String instance : size.getValue()) {
						Run run = byKey.get(impl + "|" + instance);
						if (run != null && run.solved()) {
							solved++;
							times.add(run.medianMs());
							par2 += run.medianMs() / 1000;
						} else {
							par2 += 2 * timeoutS;
						}
					}
					md.append(' ').append(solved).append('/').append(size.getValue().size()).append(" | ")
							.append(times.isEmpty() ? "–" : fmt(geomean(times))).append(" | ").append(fmt(par2 / size.getValue().size())).append(" |");
				}
				List<Double> speedups = new ArrayList<>();
				int both = 0, agree = 0;
				long maxModels = 0;
				for (String instance : size.getValue()) {
					for (String impl : impls) {
						Run r = byKey.get(impl + "|" + instance);
						if (r != null && r.count() != null) {
							maxModels = Math.max(maxModels, r.count());
						}
						if (r != null && !r.solved() && !"timeout".equals(r.status()) && !"skipped".equals(r.status())) {
							problems.add("`" + instance + "` " + r.impl() + ": **" + r.status() + "** " + Objects.toString(r.error(), ""));
						}
					}
					Run old = byKey.get(reference + "|" + instance), neu = byKey.get("new|" + instance);
					if (old != null && neu != null && old.solved() && neu.solved()) {
						both++;
						speedups.add(old.medianMs() / neu.medianMs());
						// differences are reported by the validation section
						if (Objects.equals(old.count(), neu.count()) && Objects.equals(old.hash(), neu.hash())) {
							agree++;
						}
					}
				}
				md.append(' ').append(speedups.isEmpty() ? "–" : fmt(geomean(speedups)) + "×").append(" | ").append(agree).append('/')
						.append(both).append(" | ").append(maxModels).append(" |\n");
			}
			md.append('\n');
		}
		List<Map<String, Object>> mismatches = new ArrayList<>();
		md.append(validation(instancesByFamily, byKey, impls, reference, instances, sem, problems, mismatches));

		md.append("## Crashes, errors and disagreements\n\n");
		if (problems.isEmpty()) {
			md.append("None.\n");
		} else {
			for (String p : new TreeSet<>(problems)) {
				md.append("- ").append(p.length() > 400 ? p.substring(0, 400) + "…" : p).append('\n');
			}
		}
		Files.writeString(out, md.toString());

		StringBuilder csv = new StringBuilder("impl,instance,family,n,status,median_ms,count,hash\n");
		for (Run r : runs) {
			csv.append(String.join(",", r.impl(), r.instance(), r.family(), String.valueOf(r.n()), r.status(),
					r.medianMs() == null ? "" : String.format(Locale.ROOT, "%.3f", r.medianMs()), Objects.toString(r.count(), ""),
					Objects.toString(r.hash(), ""))).append('\n');
		}
		Files.writeString(out.resolveSibling(out.getFileName().toString().replaceAll("\\.md$", "") + ".csv"), csv.toString());

		StringBuilder jsonl = new StringBuilder();
		for (Map<String, Object> m : mismatches) {
			jsonl.append(Json.write(m)).append('\n');
		}
		Files.writeString(out.resolveSibling("mismatches.jsonl"), jsonl.toString());
	}

	private static final String ORACLE = "oracle";

	private static String validation(Map<String, Map<Integer, List<String>>> instancesByFamily, Map<String, Run> byKey, List<String> impls,
			String reference, Path instances, String sem, List<String> problems, List<Map<String, Object>> mismatches) {
		StringBuilder md = new StringBuilder("## Validation\n\n");
		md.append("Solved runs by validation strength. *oracle ok* / *wrong*: compared with the independent brute-force oracle. ")
				.append("Without an oracle answer, runs are compared with the other side of the ").append(reference)
				.append("/new pair (old with ").append(reference).append("): *agreement* is differential evidence only, ")
				.append("*disagreement* is unresolved. *unchecked*: nothing to compare with.\n\n");
		md.append("| family | implementation | solved | oracle ok | wrong | agreement | disagreement | unchecked |\n");
		md.append("|---|---|---:|---:|---:|---:|---:|---:|\n");
		for (Map.Entry<String, Map<Integer, List<String>>> family : instancesByFamily.entrySet()) {
			for (String impl : impls) {
				int solved = 0, verified = 0, wrong = 0, agreement = 0, disagreement = 0, unchecked = 0;
				for (List<String> size : family.getValue().values()) {
					for (String instance : size) {
						Run run = byKey.get(impl + "|" + instance);
						if (run == null || !run.solved()) {
							continue;
						}
						solved++;
						Run oracle = byKey.get(ORACLE + "|" + instance);
						String otherImpl = impl.equals("new") ? reference : impl.equals(reference) ? "new" : reference;
						Run other = byKey.get(otherImpl + "|" + instance);
						Run against;
						if (oracle != null && oracle.solved()) {
							against = oracle;
						} else if (other != null && other.solved() && !otherImpl.equals(impl)) {
							against = other;
						} else {
							unchecked++;
							continue;
						}
						boolean same = Objects.equals(run.count(), against.count()) && Objects.equals(run.hash(), against.hash());
						if (against == oracle) {
							if (same) {
								verified++;
							} else {
								wrong++;
							}
						} else if (same) {
							agreement++;
						} else {
							disagreement++;
						}
						if (!same) {
							String kind = against == oracle ? "wrong" : "disagreement";
							Map<String, Object> m = new LinkedHashMap<>();
							m.put("kind", kind);
							m.put("instance", instance);
							m.put("sem", sem);
							m.put("impl", impl);
							m.put("count", run.count());
							m.put("answerHash", run.hash());
							m.put("against", against.impl());
							m.put("expectedCount", against.count());
							m.put("expectedHash", against.hash());
							m.put("repro", "./driver.sh check --file " + instances.resolve(instance) + " --sem " + sem + " --impls " + impl
									+ (against == oracle ? "" : "," + against.impl()));
							mismatches.add(m);
							problems.add("`" + instance + "` " + impl + ": **" + kind + "** (" + run.count() + " models, " + run.hash() + "; "
									+ against.impl() + " " + against.count() + " models, " + against.hash() + ")");
						}
					}
				}
				md.append("| ").append(family.getKey()).append(" | ").append(impl).append(" | ").append(solved).append(" | ").append(verified)
						.append(" | ").append(wrong).append(" | ").append(agreement).append(" | ").append(disagreement).append(" | ")
						.append(unchecked).append(" |\n");
			}
		}
		return md.append('\n').toString();
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
