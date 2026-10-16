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
import java.util.Set;

/**
 * The runner protocol shared by the old and the new implementation (plan,
 * section 4.2):
 *
 * <pre>
 * Runner --task EE --sem CO --file x.kpp [--warmup W] [--reps R] [--budget-ms B]
 * </pre>
 *
 * Each repetition parses the file (untimed, so no state is reused between
 * repetitions) and then solves (timed: wall clock and thread CPU time). The
 * runner writes {@code SOLVE_START i} and {@code SOLVE_END i} to stderr so the
 * driver can enforce a per-solve timeout, and prints exactly one JSON line to
 * stdout. Measured repetitions stop early once their total time exceeds the
 * budget, so slow instances are measured once instead of R times.
 * <p>
 * Answers are reported as the model count and an order-independent SHA-256
 * over the canonical models {@code t:{..} f:{..}} (sorted names); converting
 * models to canonical form happens outside the timed section (see {@link Answers}).
 */
public final class RunnerSupport {

	/** An implementation under test. */
	public interface Implementation<A, M> {
		/** @return a short name, e.g. "old" */
		String name();

		/** @return the parsed framework (untimed) */
		A parse(Path file) throws Exception;

		/** @return all models of the semantics, enumerated with an explicit iterator (timed) */
		List<M> enumerate(A adf, String semantics) throws Exception;

		/** @return the names of the arguments mapped to true */
		Set<String> satisfied(M model);

		/** @return the names of the arguments mapped to false */
		Set<String> unsatisfied(M model);
	}

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
		out.put("impl", impl.name());
		out.put("task", opts.getOrDefault("task", "EE"));
		out.put("sem", opts.get("sem"));
		out.put("file", opts.get("file"));
		List<Double> times = new ArrayList<>(), cpu = new ArrayList<>();
		try {
			if (!"EE".equals(out.get("task"))) {
				out.put("status", "unsupported");
				System.out.println(Json.write(out));
				return;
			}
			int warmup = Integer.parseInt(opts.getOrDefault("warmup", "1"));
			int reps = Integer.parseInt(opts.getOrDefault("reps", "3"));
			double budgetMs = Double.parseDouble(opts.getOrDefault("budget-ms", "10000"));
			Path file = Path.of(opts.get("file"));
			ThreadMXBean bean = ManagementFactory.getThreadMXBean();
			String hash = null;
			int count = -1;
			double parseMs = 0, measured = 0;
			for (int i = 0; i < warmup + reps; i++) {
				long p0 = System.nanoTime();
				A adf = impl.parse(file);
				parseMs = (System.nanoTime() - p0) / 1e6;
				System.err.println("SOLVE_START " + i);
				System.err.flush();
				long c0 = bean.getCurrentThreadCpuTime();
				long t0 = System.nanoTime();
				List<M> models = impl.enumerate(adf, opts.get("sem"));
				long t1 = System.nanoTime();
				long c1 = bean.getCurrentThreadCpuTime();
				System.err.println("SOLVE_END " + i);
				System.err.flush();
				String h = hash(impl, models);
				if (hash != null && !hash.equals(h)) {
					throw new IllegalStateException("Answer differs between repetitions.");
				}
				hash = h;
				count = models.size();
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
			out.put("count", count);
			out.put("answerHash", hash);
			out.put("parseMs", parseMs);
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

	private static <A, M> String hash(Implementation<A, M> impl, List<M> models) {
		List<String> canonical = new ArrayList<>();
		for (M m : models) {
			canonical.add(Answers.canonical(impl.satisfied(m), impl.unsatisfied(m)));
		}
		return Answers.hash(canonical);
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
