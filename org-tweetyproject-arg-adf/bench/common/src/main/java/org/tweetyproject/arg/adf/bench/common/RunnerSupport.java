package org.tweetyproject.arg.adf.bench.common;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * The runner protocol shared by the old and the new implementation (plan,
 * section 4.2):
 *
 * <pre>
 * Runner --task EE|CE|SE|DC|DS|VE --sem CO --file x.kpp [--arg a] [--interpretation "t:{a} f:{b}"]
 *        [--warmup W] [--reps R] [--budget-ms B]
 * </pre>
 *
 * Tasks: enumerate all models (EE), count them exactly (CE), find one model
 * (SE), credulous (DC) or skeptical (DS) acceptance of {@code --arg}, and
 * verification of {@code --interpretation} (VE).
 * <p>
 * Each repetition parses the file (untimed, so no state is reused between
 * repetitions) and then solves (timed: wall clock and thread CPU time). The
 * runner writes {@code SOLVE_START i} and {@code SOLVE_END i} to stderr so the
 * driver can enforce a per-solve timeout, and prints exactly one JSON line to
 * stdout. Measured repetitions stop early once their total time exceeds the
 * budget, so slow instances are measured once instead of R times.
 * <p>
 * Answers: EE reports the model count and an order-independent hash over the
 * canonical models (see {@link Answers}); CE the count; SE the canonical
 * witness as {@code answer}, or null if there is no model; DC, DS and VE a
 * Boolean {@code answer}. Converting models to canonical form happens outside
 * the timed section. An implementation that does not support a semantics or
 * task throws {@link UnsupportedOperationException} and is reported as
 * {@code unsupported}.
 */
public final class RunnerSupport {

	/** An implementation under test. */
	public interface Implementation<A, M> {
		/** @return a short name, e.g. "old" */
		String name();

		/** @return the parsed framework (untimed) */
		A parse(Path file) throws Exception;

		/** @return all models of the semantics, enumerated with an explicit iterator (EE, CE) */
		List<M> enumerate(A adf, String semantics) throws Exception;

		/** @return some model of the semantics, or null if there is none (SE) */
		M witness(A adf, String semantics) throws Exception;

		/** @return whether the argument is credulously (DC) or skeptically (DS) accepted */
		boolean accepted(A adf, String semantics, String argument, boolean credulous) throws Exception;

		/** @return the interpretation, built untimed, for VE */
		Object interpretation(A adf, Set<String> satisfied, Set<String> unsatisfied) throws Exception;

		/** @return whether the interpretation from {@link #interpretation} is a model (VE) */
		boolean verify(A adf, String semantics, Object interpretation) throws Exception;

		/** @return how VE is answered, e.g. "isModel" or "membership in the enumeration" */
		String verificationMethod();

		/** @return the names of the arguments mapped to true */
		Set<String> satisfied(M model);

		/** @return the names of the arguments mapped to false */
		Set<String> unsatisfied(M model);
	}

	static final Set<String> TASKS = Set.of("EE", "CE", "SE", "DC", "DS", "VE");

	private RunnerSupport() {}

	/**
	 * Runs the protocol and prints the JSON result line.
	 *
	 * @param impl the implementation
	 * @param args the command line
	 */
	public static <A, M> void run(Implementation<A, M> impl, String[] args) {
		Map<String, String> opts = options(args);
		Map<String, Object> out = new LinkedHashMap<>();
		String task = opts.getOrDefault("task", "EE");
		String sem = opts.get("sem");
		out.put("impl", impl.name());
		out.put("task", task);
		out.put("sem", sem);
		out.put("file", opts.get("file"));
		List<Double> times = new ArrayList<>(), cpu = new ArrayList<>();
		try {
			if (!TASKS.contains(task)) {
				throw new IllegalArgumentException("Unknown task " + task);
			}
			if (task.equals("DC") || task.equals("DS")) {
				Objects.requireNonNull(opts.get("arg"), "--arg is required for " + task);
			}
			if (task.equals("VE")) {
				out.put("method", impl.verificationMethod());
			}
			int warmup = Integer.parseInt(opts.getOrDefault("warmup", "1"));
			int reps = Integer.parseInt(opts.getOrDefault("reps", "3"));
			double budgetMs = Double.parseDouble(opts.getOrDefault("budget-ms", "10000"));
			Path file = Path.of(opts.get("file"));
			ThreadMXBean bean = ManagementFactory.getThreadMXBean();
			Map<String, Object> answer = null;
			double parseMs = 0, measured = 0;
			for (int i = 0; i < warmup + reps; i++) {
				long p0 = System.nanoTime();
				A adf = impl.parse(file);
				Object interpretation = null;
				if (task.equals("VE")) {
					List<Set<String>> sets = Answers.parseCanonical(Objects.requireNonNull(opts.get("interpretation"),
							"--interpretation is required for VE"));
					interpretation = impl.interpretation(adf, sets.get(0), sets.get(1));
				}
				parseMs = (System.nanoTime() - p0) / 1e6;
				System.err.println("SOLVE_START " + i);
				System.err.flush();
				long c0 = bean.getCurrentThreadCpuTime();
				long t0 = System.nanoTime();
				Object raw = switch (task) {
					case "EE", "CE" -> impl.enumerate(adf, sem);
					case "SE" -> impl.witness(adf, sem);
					case "DC", "DS" -> impl.accepted(adf, sem, opts.get("arg"), task.equals("DC"));
					default -> impl.verify(adf, sem, interpretation);
				};
				long t1 = System.nanoTime();
				long c1 = bean.getCurrentThreadCpuTime();
				System.err.println("SOLVE_END " + i);
				System.err.flush();
				Map<String, Object> a = describe(impl, task, raw);
				// SE witnesses may legitimately differ between repetitions, their existence may not
				boolean differs = answer != null && (task.equals("SE") ? (answer.get("answer") == null) != (a.get("answer") == null)
						: !answer.equals(a));
				if (differs) {
					throw new IllegalStateException("Answer differs between repetitions.");
				}
				if (answer == null) {
					answer = a;
				}
				double ms = (t1 - t0) / 1e6;
				// a slow first solve counts as measurement: warming up would only repeat it
				boolean isWarmup = i < warmup && ms < budgetMs / 10;
				if (!isWarmup) {
					times.add(ms);
					cpu.add((c1 - c0) / 1e6);
					measured += ms;
					if (measured > budgetMs) {
						break;
					}
				}
			}
			out.put("status", "ok");
			out.putAll(answer);
			out.put("parseMs", parseMs);
		} catch (UnsupportedOperationException e) {
			out.put("status", "unsupported");
			out.put("error", String.valueOf(e.getMessage()));
		} catch (Throwable t) {
			out.put("status", t instanceof OutOfMemoryError ? "memout" : "error");
			StringWriter trace = new StringWriter();
			t.printStackTrace(new PrintWriter(trace));
			out.put("error", t + " | " + firstLines(trace.toString(), 6));
		}
		out.put("timesMs", times);
		out.put("cpuMs", cpu);
		System.out.println(Json.write(out));
		System.out.flush();
	}

	@SuppressWarnings("unchecked")
	private static <A, M> Map<String, Object> describe(Implementation<A, M> impl, String task, Object raw) {
		Map<String, Object> a = new LinkedHashMap<>();
		switch (task) {
		case "EE": {
			List<M> models = (List<M>) raw;
			List<String> canonical = new ArrayList<>();
			for (M m : models) {
				canonical.add(canonical(impl, m));
			}
			a.put("count", models.size());
			a.put("answerHash", Answers.hash(canonical));
			break;
		}
		case "CE":
			a.put("count", ((List<M>) raw).size());
			break;
		case "SE":
			a.put("answer", raw == null ? null : canonical(impl, (M) raw));
			break;
		default:
			a.put("answer", raw);
		}
		return a;
	}

	private static <A, M> String canonical(Implementation<A, M> impl, M model) {
		return Answers.canonical(impl.satisfied(model), impl.unsatisfied(model));
	}

	private static String firstLines(String s, int n) {
		String[] lines = s.split("\n");
		StringBuilder b = new StringBuilder();
		for (int i = 0; i < Math.min(n, lines.length); i++) {
			b.append(lines[i].trim()).append(i + 1 < Math.min(n, lines.length) ? " / " : "");
		}
		return b.toString();
	}

	private static Map<String, String> options(String[] args) {
		Map<String, String> opts = new HashMap<>();
		for (int i = 0; i + 1 < args.length; i += 2) {
			if (!args[i].startsWith("--")) {
				throw new IllegalArgumentException("Unexpected argument " + args[i]);
			}
			opts.put(args[i].substring(2), args[i + 1]);
		}
		return opts;
	}

}
