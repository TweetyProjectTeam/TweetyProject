package org.tweetyproject.arg.adf.bench.driver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UncheckedIOException;
import java.lang.management.ManagementFactory;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.Instant;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.tweetyproject.arg.adf.bench.common.Json;

/**
 * Orchestrates the old-vs-new evaluation (plan, section 4).
 *
 * <pre>
 * generate --out DIR [--seed 2026] [--families af,bipolar,general] [--sizes 4,6,..,20] [--per 5]
 * run      --instances DIR --results FILE [--impls old,old-fixed,new] [--sem CO] [--task EE] [--timeout-s 60]
 *          [--heap 4g] [--warmup 1] [--reps 3] [--budget-ms 10000] [--cores 0,1,2,3,4,5] [--oracle-max 10]
 * report   --instances DIR --results FILE --out FILE [--timeout-s 60]
 * check    --file FILE [--sem CO] [--task EE] [--arg A | --interpretation "t:{..} f:{..}"] [--impls old-fixed,new]
 *          [--timeout-s 60] [--heap 4g]
 * </pre>
 *
 * {@code run} starts every run in its own JVM, pinned with taskset to one
 * physical core (both hardware threads). Runs are grouped into chains per
 * (implementation, family), processed in increasing size; once every instance
 * of a size has timed out, larger sizes of that chain are recorded as skipped.
 * Chains run in parallel on disjoint cores. Results are appended to a JSONL
 * file, and runs already present are not repeated. Before the runners, the
 * driver builds the frozen queries of every instance ({@link Queries}: e.g.
 * the arguments for DC/DS, the candidates for VE) and records the expected
 * answers of the independent {@link Oracle} as implementation {@code oracle}:
 * for instances with at most {@code --oracle-max} arguments, and for VE also
 * beyond as an independent check.
 * <p>
 * {@code check} runs the queries of one instance (all frozen ones, or the given
 * {@code --arg} / {@code --interpretation}) once per implementation and prints
 * the answers next to the oracle's; it is the reproduction command for
 * mismatches listed by {@code report}.
 * <p>
 * Implementations: {@code old} (adf:1.31-baseline as tagged), {@code old-fixed}
 * (adf:1.31-baseline-fixed, the performance reference) and {@code new}.
 */
public final class Driver {

	private static final Path BENCH = Path.of(System.getProperty("bench.home", "."));

	/** kills a runner that shows no protocol progress outside a solve for this long */
	private static final long WATCHDOG_NS = 120_000_000_000L;

	/** baseline artifact version of each old implementation, copied to runner-old/target/adf */
	private static final Map<String, String> BASELINES = Map.of("old", "1.31-baseline", "old-fixed", "1.31-baseline-fixed");

	public static void main(String[] args) throws Exception {
		if (args.length == 0) {
			throw new IllegalArgumentException("Usage: generate|run|report|check [options]");
		}
		Map<String, String> opts = new HashMap<>();
		for (int i = 1; i + 1 < args.length; i += 2) {
			opts.put(args[i].substring(2), args[i + 1]);
		}
		switch (args[0]) {
			case "generate" -> InstanceGenerator.generate(Path.of(opts.get("out")), Long.parseLong(opts.getOrDefault("seed", "2026")),
					List.of(opts.getOrDefault("families", String.join(",", InstanceGenerator.FAMILIES)).split(",")),
					ints(opts.getOrDefault("sizes", "4,6,8,10,12,14,16,18,20")), Integer.parseInt(opts.getOrDefault("per", "5")));
			case "run" -> run(opts);
			case "report" -> Report.write(Path.of(opts.get("instances")), Path.of(opts.get("results")), Path.of(opts.get("out")),
					Double.parseDouble(opts.getOrDefault("timeout-s", "60")));
			case "check" -> check(opts);
			default -> throw new IllegalArgumentException("Unknown command " + args[0]);
		}
	}

	private static List<Integer> ints(String csv) {
		return Arrays.stream(csv.split(",")).map(String::trim).map(Integer::valueOf).toList();
	}

	// ------------------------------------------------------------------
	// run
	// ------------------------------------------------------------------

	private record Instance(String file, String family, int n, int index) {}

	/** one runner invocation: an instance with one of its frozen queries */
	private record Unit(Instance instance, Queries.Query query) {}

	private record Chain(String impl, String family, List<Unit> units) {}

	/** the key under which a result line is recorded; lines without task or query are EE results */
	private static String key(String impl, String instance, String sem, String task, String query) {
		return impl + "|" + instance + "|" + sem + "|" + task + "|" + query;
	}

	private static void run(Map<String, String> opts) throws Exception {
		Path instances = Path.of(opts.get("instances"));
		Path results = Path.of(opts.get("results"));
		List<String> impls = List.of(opts.getOrDefault("impls", "old,old-fixed,new").split(","));
		for (String impl : impls) {
			if (!impl.equals("new") && !BASELINES.containsKey(impl)) {
				throw new IllegalArgumentException("Unknown implementation " + impl);
			}
		}
		String sem = opts.getOrDefault("sem", "CO");
		String task = opts.getOrDefault("task", "EE");
		int oracleMax = Integer.parseInt(opts.getOrDefault("oracle-max", "10"));
		double timeoutS = Double.parseDouble(opts.getOrDefault("timeout-s", "60"));
		List<Integer> cores = ints(opts.getOrDefault("cores", "0,1,2,3,4,5"));
		List<String> runnerFlags = List.of("--task", task, "--sem", sem, "--warmup", opts.getOrDefault("warmup", "1"),
				"--reps", opts.getOrDefault("reps", "3"), "--budget-ms", opts.getOrDefault("budget-ms", "10000"));
		String heap = opts.getOrDefault("heap", "4g");

		Set<String> done = new HashSet<>();
		if (Files.exists(results)) {
			for (String line : Files.readAllLines(results)) {
				Map<String, Object> r = Json.parseObject(line);
				if (r.containsKey("impl") && r.containsKey("instance")) {
					done.add(key((String) r.get("impl"), (String) r.get("instance"), (String) r.get("sem"),
							(String) r.getOrDefault("task", "EE"), (String) r.getOrDefault("query", "")));
				}
			}
		} else {
			Map<String, Object> header = new LinkedHashMap<>();
			header.put("header", true);
			header.put("time", Instant.now().toString());
			header.put("java", System.getProperty("java.vm.name") + " " + System.getProperty("java.version"));
			header.put("os", System.getProperty("os.name") + " " + System.getProperty("os.version"));
			header.put("cpu", cpuModel());
			header.put("commit", git("rev-parse", "HEAD"));
			header.put("dirty", !git("status", "--porcelain", "--untracked-files=no", "--", "..").isEmpty());
			header.put("jvmFlags", jvmFlags(heap));
			header.put("runnerFlags", runnerFlags);
			header.put("timeoutS", timeoutS);
			header.put("cores", cores);
			Files.writeString(results, Json.write(header) + "\n", StandardOpenOption.CREATE, StandardOpenOption.APPEND);
		}

		List<Instance> all = new ArrayList<>();
		List<String> manifest = Files.readAllLines(instances.resolve("manifest.tsv"));
		for (String line : manifest.subList(1, manifest.size())) {
			String[] f = line.split("\t");
			all.add(new Instance(f[0], f[1], Integer.parseInt(f[2]), Integer.parseInt(f[3])));
		}
		Map<String, List<Queries.Query>> queries = prepare(all, instances, results, sem, task, oracleMax, cores.size(), done);
		ConcurrentLinkedQueue<Chain> chains = new ConcurrentLinkedQueue<>();
		for (String impl : impls) {
			for (String family : InstanceGenerator.FAMILIES) {
				List<Unit> units = new ArrayList<>();
				all.stream().filter(i -> i.family().equals(family))
						.sorted((a, b) -> a.n() != b.n() ? Integer.compare(a.n(), b.n()) : Integer.compare(a.index(), b.index()))
						.forEach(i -> queries.get(i.file()).forEach(q -> units.add(new Unit(i, q))));
				if (!units.isEmpty()) {
					chains.add(new Chain(impl, family, units));
				}
			}
		}
		List<Thread> workers = new ArrayList<>();
		for (int core : cores) {
			Thread worker = new Thread(() -> {
				Chain chain;
				while ((chain = chains.poll()) != null) {
					try {
						runChain(chain, core, instances, results, sem, task, runnerFlags, heap, timeoutS, done);
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
			worker.start();
			workers.add(worker);
		}
		for (Thread worker : workers) {
			worker.join();
		}
	}

	/**
	 * Builds the frozen queries of every instance and records the oracle's
	 * expected answers (implementation {@code oracle}) that are not yet in the
	 * results.
	 *
	 * @return the queries per instance file
	 */
	private static Map<String, List<Queries.Query>> prepare(List<Instance> all, Path instances, Path results, String sem, String task,
			int oracleMax, int threads, Set<String> done) throws Exception {
		Map<String, List<Queries.Query>> queries = new ConcurrentHashMap<>();
		ExecutorService pool = Executors.newFixedThreadPool(threads);
		List<Future<?>> futures = new ArrayList<>();
		for (Instance instance : all) {
			futures.add(pool.submit(() -> {
				KppAdf adf = KppAdf.parse(instances.resolve(instance.file()));
				List<Queries.Query> list = Queries.of(adf, instance.file(), task, sem, oracleMax);
				queries.put(instance.file(), list);
				for (Queries.Query query : list) {
					if (done.contains(key(ORACLE, instance.file(), sem, task, query.label()))) {
						continue;
					}
					Map<String, Object> expected = Queries.expectation(adf, query, task, sem, oracleMax);
					if (expected == null) {
						continue;
					}
					Map<String, Object> line = new LinkedHashMap<>();
					line.put("impl", ORACLE);
					line.put("instance", instance.file());
					line.put("family", instance.family());
					line.put("n", instance.n());
					line.put("sem", sem);
					line.put("task", task);
					line.put("query", query.label());
					line.putAll(expected);
					synchronized (Driver.class) {
						Files.writeString(results, Json.write(line) + "\n", StandardOpenOption.APPEND);
					}
				}
				return null;
			}));
		}
		pool.shutdown();
		for (Future<?> future : futures) {
			// rethrows parse errors: every instance must be readable by the oracle's parser
			future.get();
		}
		return queries;
	}

	private static final String ORACLE = "oracle";

	private static void check(Map<String, String> opts) throws Exception {
		Path file = Path.of(opts.get("file"));
		String sem = opts.getOrDefault("sem", "CO");
		String task = opts.getOrDefault("task", "EE");
		int oracleMax = Integer.parseInt(opts.getOrDefault("oracle-max", "10"));
		KppAdf adf = KppAdf.parse(file);
		List<Queries.Query> queries;
		if (opts.containsKey("arg")) {
			queries = List.of(new Queries.Query("arg=" + opts.get("arg"), List.of("--arg", opts.get("arg"))));
		} else if (opts.containsKey("interpretation")) {
			queries = List.of(new Queries.Query("interpretation=" + opts.get("interpretation"),
					List.of("--interpretation", opts.get("interpretation"))));
		} else {
			queries = Queries.of(adf, file.getFileName().toString(), task, sem, oracleMax);
		}
		for (Queries.Query query : queries) {
			System.out.println(task + " " + sem + (query.label().isEmpty() ? "" : " " + query.label()));
			List<String> flags = new ArrayList<>(List.of("--task", task, "--sem", sem, "--warmup", "0", "--reps", "1"));
			flags.addAll(query.flags());
			for (String impl : opts.getOrDefault("impls", "old-fixed,new").split(",")) {
				Map<String, Object> r = runOne(impl, file, 0, flags, opts.getOrDefault("heap", "4g"),
						Double.parseDouble(opts.getOrDefault("timeout-s", "60")));
				System.out.println("  " + describe(impl, r));
			}
			Map<String, Object> expected = Queries.expectation(adf, query, task, sem, oracleMax);
			System.out.println("  " + (expected == null ? "oracle    (no answer: instance too large)" : describe(ORACLE, expected)));
		}
		if (adf.size() <= oracleMax) {
			List<String> models = new Oracle(adf).models(sem);
			System.out.println("oracle models (" + models.size() + "):");
			models.stream().limit(100).forEach(m -> System.out.println("  " + m));
			if (models.size() > 100) {
				System.out.println("  … " + (models.size() - 100) + " more");
			}
		}
	}

	private static String describe(String impl, Map<String, Object> r) {
		StringBuilder s = new StringBuilder(String.format("%-9s %-8s", impl, r.get("status")));
		for (String field : List.of("count", "answerHash", "answer", "validation", "method", "error")) {
			if (r.containsKey(field)) {
				s.append(' ').append(field).append('=').append(r.get(field));
			}
		}
		return s.toString();
	}

	private static void runChain(Chain chain, int core, Path instances, Path results, String sem, String task, List<String> runnerFlags,
			String heap, double timeoutS, Set<String> done) throws Exception {
		Map<Integer, List<Unit>> bySize = new TreeMap<>();
		for (Unit u : chain.units()) {
			bySize.computeIfAbsent(u.instance().n(), k -> new ArrayList<>()).add(u);
		}
		boolean skipRest = false;
		for (Map.Entry<Integer, List<Unit>> size : bySize.entrySet()) {
			int timeouts = 0;
			for (Unit unit : size.getValue()) {
				Instance instance = unit.instance();
				if (done.contains(key(chain.impl(), instance.file(), sem, task, unit.query().label()))) {
					continue;
				}
				Map<String, Object> result;
				if (skipRest) {
					result = new LinkedHashMap<>();
					result.put("status", "skipped");
				} else {
					List<String> flags = new ArrayList<>(runnerFlags);
					flags.addAll(unit.query().flags());
					result = runOne(chain.impl(), instances.resolve(instance.file()), core, flags, heap, timeoutS);
				}
				if ("timeout".equals(result.get("status")) || "memout".equals(result.get("status"))) {
					timeouts++;
				}
				Map<String, Object> line = new LinkedHashMap<>();
				line.put("impl", chain.impl());
				line.put("instance", instance.file());
				line.put("family", instance.family());
				line.put("n", instance.n());
				line.put("sem", sem);
				line.put("task", task);
				line.put("query", unit.query().label());
				line.put("core", core);
				line.putAll(result);
				line.remove("file");
				line.put("impl", chain.impl());
				synchronized (Driver.class) {
					Files.writeString(results, Json.write(line) + "\n", StandardOpenOption.APPEND);
					System.out.printf("%-9s %-8s %-22s %-8s %s %s%n", chain.impl(), chain.family(), instance.file(), line.get("status"),
							unit.query().label(), line.get("timesMs"));
				}
			}
			if (timeouts == size.getValue().size()) {
				skipRest = true;
			}
		}
	}

	private static Map<String, Object> runOne(String impl, Path file, int core, List<String> runnerFlags, String heap, double timeoutS)
			throws IOException, InterruptedException {
		boolean old = BASELINES.containsKey(impl);
		String runner = old ? "runner-old" : "runner-new";
		String main = old ? "org.tweetyproject.arg.adf.bench.old.OldAdfRunner" : "org.tweetyproject.arg.adf.bench.current.NewAdfRunner";
		String classpath = BENCH.resolve(runner).resolve("target/classes") + ":"
				+ Files.readString(BENCH.resolve(runner).resolve("target/classpath.txt")).trim();
		if (old) {
			classpath += ":" + BENCH.resolve(runner).resolve("target/adf/adf-" + BASELINES.get(impl) + ".jar");
		}
		List<String> cmd = new ArrayList<>(List.of("taskset", "-c", core + "," + (core + 8), "java"));
		cmd.addAll(jvmFlags(heap));
		cmd.addAll(List.of("-cp", classpath, main, "--file", file.toAbsolutePath().toString()));
		cmd.addAll(runnerFlags);
		Process process = new ProcessBuilder(cmd).start();

		Deque<String> stderrTail = new ArrayDeque<>();
		// solveStart: start of the running solve or -1; lastEvent: last protocol event (or process start)
		long startedAt = System.nanoTime();
		long[] solveStart = { -1 };
		long[] lastEvent = { startedAt };
		Thread errReader = new Thread(() -> {
			try (BufferedReader r = new BufferedReader(new InputStreamReader(process.getErrorStream(), StandardCharsets.UTF_8))) {
				String line;
				while ((line = r.readLine()) != null) {
					synchronized (solveStart) {
						if (line.startsWith("SOLVE_START")) {
							solveStart[0] = System.nanoTime();
							lastEvent[0] = solveStart[0];
						} else if (line.startsWith("SOLVE_END")) {
							solveStart[0] = -1;
							lastEvent[0] = System.nanoTime();
						} else {
							stderrTail.addLast(line);
							if (stderrTail.size() > 20) {
								stderrTail.removeFirst();
							}
						}
					}
				}
			} catch (IOException ignored) {
				// process killed
			}
		});
		StringBuilder stdout = new StringBuilder();
		Thread outReader = new Thread(() -> {
			try (BufferedReader r = new BufferedReader(new InputStreamReader(process.getInputStream(), StandardCharsets.UTF_8))) {
				String line;
				while ((line = r.readLine()) != null) {
					synchronized (stdout) {
						stdout.append(line).append('\n');
					}
				}
			} catch (IOException ignored) {
				// process killed
			}
		});
		errReader.start();
		outReader.start();

		String killedFor = null;
		long timeoutNs = (long) (timeoutS * 1e9);
		while (!process.waitFor(20, TimeUnit.MILLISECONDS)) {
			long now = System.nanoTime();
			synchronized (solveStart) {
				if (solveStart[0] >= 0 && now - solveStart[0] > timeoutNs) {
					killedFor = "timeout";
				} else if (solveStart[0] < 0 && now - lastEvent[0] > WATCHDOG_NS) {
					// parsing, answer hashing or protocol output hangs
					killedFor = "watchdog";
				}
			}
			if (killedFor != null) {
				process.destroyForcibly();
				process.waitFor();
				break;
			}
		}
		errReader.join(2000);
		outReader.join(2000);
		long wallMs = (System.nanoTime() - startedAt) / 1_000_000;
		Map<String, Object> result;
		String out;
		synchronized (stdout) {
			out = stdout.toString().trim();
		}
		if (killedFor != null) {
			result = new LinkedHashMap<>();
			result.put("status", killedFor.equals("timeout") ? "timeout" : "error");
			if (!killedFor.equals("timeout")) {
				result.put("error", "watchdog killed the runner");
			}
		} else if (out.isEmpty()) {
			result = new LinkedHashMap<>();
			result.put("status", "crash");
			result.put("error", "exit " + process.exitValue() + ": " + String.join(" / ", stderrTail));
		} else {
			String last = out.substring(out.lastIndexOf('\n') + 1);
			result = Json.parseObject(last);
		}
		result.put("processWallMs", wallMs);
		return result;
	}

	private static List<String> jvmFlags(String heap) {
		return List.of("-Xms512m", "-Xmx" + heap, "-XX:+UseSerialGC", "-Xss64m");
	}

	private static String cpuModel() {
		try {
			for (String line : Files.readAllLines(Path.of("/proc/cpuinfo"))) {
				if (line.startsWith("model name")) {
					return line.substring(line.indexOf(':') + 1).trim();
				}
			}
		} catch (IOException ignored) {
			// not on Linux
		}
		return ManagementFactory.getOperatingSystemMXBean().getArch();
	}

	private static String git(String... args) {
		try {
			List<String> cmd = new ArrayList<>(List.of("git"));
			cmd.addAll(List.of(args));
			Process p = new ProcessBuilder(cmd).directory(BENCH.toFile()).redirectErrorStream(true).start();
			String out = new String(p.getInputStream().readAllBytes(), StandardCharsets.UTF_8).trim();
			p.waitFor();
			return out;
		} catch (IOException | InterruptedException e) {
			return "unknown";
		}
	}

}
