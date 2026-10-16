package org.tweetyproject.arg.adf.bench.driver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
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
import java.util.Set;
import java.util.TreeMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.TimeUnit;

import org.tweetyproject.arg.adf.bench.common.Json;

/**
 * Orchestrates the old-vs-new evaluation (plan, section 4).
 *
 * <pre>
 * generate --out DIR [--seed 2026] [--sizes 4,6,..,20] [--per 5]
 * run      --instances DIR --results FILE [--impls old,old-fixed,new] [--sem CO] [--timeout-s 60]
 *          [--heap 4g] [--warmup 1] [--reps 3] [--budget-ms 10000] [--cores 0,1,2,3,4,5]
 * report   --instances DIR --results FILE --out FILE [--timeout-s 60]
 * </pre>
 *
 * {@code run} starts every run in its own JVM, pinned with taskset to one
 * physical core (both hardware threads). Runs are grouped into chains per
 * (implementation, family), processed in increasing size; once every instance
 * of a size has timed out, larger sizes of that chain are recorded as skipped.
 * Chains run in parallel on disjoint cores. Results are appended to a JSONL
 * file, and runs already present are not repeated.
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
			throw new IllegalArgumentException("Usage: generate|run|report [options]");
		}
		Map<String, String> opts = new HashMap<>();
		for (int i = 1; i + 1 < args.length; i += 2) {
			opts.put(args[i].substring(2), args[i + 1]);
		}
		switch (args[0]) {
			case "generate" -> InstanceGenerator.generate(Path.of(opts.get("out")), Long.parseLong(opts.getOrDefault("seed", "2026")),
					ints(opts.getOrDefault("sizes", "4,6,8,10,12,14,16,18,20")), Integer.parseInt(opts.getOrDefault("per", "5")));
			case "run" -> run(opts);
			case "report" -> Report.write(Path.of(opts.get("instances")), Path.of(opts.get("results")), Path.of(opts.get("out")),
					Double.parseDouble(opts.getOrDefault("timeout-s", "60")));
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

	private record Chain(String impl, String family, List<Instance> instances) {}

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
		double timeoutS = Double.parseDouble(opts.getOrDefault("timeout-s", "60"));
		List<Integer> cores = ints(opts.getOrDefault("cores", "0,1,2,3,4,5"));
		List<String> runnerFlags = List.of("--task", "EE", "--sem", sem, "--warmup", opts.getOrDefault("warmup", "1"),
				"--reps", opts.getOrDefault("reps", "3"), "--budget-ms", opts.getOrDefault("budget-ms", "10000"));
		String heap = opts.getOrDefault("heap", "4g");

		Set<String> done = new HashSet<>();
		if (Files.exists(results)) {
			for (String line : Files.readAllLines(results)) {
				Map<String, Object> r = Json.parseObject(line);
				if (r.containsKey("impl") && r.containsKey("instance")) {
					done.add(r.get("impl") + "|" + r.get("instance") + "|" + r.get("sem"));
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
		ConcurrentLinkedQueue<Chain> chains = new ConcurrentLinkedQueue<>();
		for (String impl : impls) {
			for (String family : InstanceGenerator.FAMILIES) {
				List<Instance> list = all.stream().filter(i -> i.family().equals(family))
						.sorted((a, b) -> a.n() != b.n() ? Integer.compare(a.n(), b.n()) : Integer.compare(a.index(), b.index())).toList();
				if (!list.isEmpty()) {
					chains.add(new Chain(impl, family, list));
				}
			}
		}
		List<Thread> workers = new ArrayList<>();
		for (int core : cores) {
			Thread worker = new Thread(() -> {
				Chain chain;
				while ((chain = chains.poll()) != null) {
					try {
						runChain(chain, core, instances, results, sem, runnerFlags, heap, timeoutS, done);
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

	private static void runChain(Chain chain, int core, Path instances, Path results, String sem, List<String> runnerFlags,
			String heap, double timeoutS, Set<String> done) throws Exception {
		Map<Integer, List<Instance>> bySize = new TreeMap<>();
		for (Instance i : chain.instances()) {
			bySize.computeIfAbsent(i.n(), k -> new ArrayList<>()).add(i);
		}
		boolean skipRest = false;
		for (Map.Entry<Integer, List<Instance>> size : bySize.entrySet()) {
			int timeouts = 0;
			for (Instance instance : size.getValue()) {
				if (done.contains(chain.impl() + "|" + instance.file() + "|" + sem)) {
					continue;
				}
				Map<String, Object> result;
				if (skipRest) {
					result = new LinkedHashMap<>();
					result.put("status", "skipped");
				} else {
					result = runOne(chain.impl(), instances.resolve(instance.file()), core, runnerFlags, heap, timeoutS);
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
				line.put("core", core);
				line.putAll(result);
				line.remove("file");
				line.put("impl", chain.impl());
				synchronized (Driver.class) {
					Files.writeString(results, Json.write(line) + "\n", StandardOpenOption.APPEND);
					System.out.printf("%-4s %-8s %-22s %-8s %s%n", chain.impl(), chain.family(), instance.file(), line.get("status"),
							line.get("timesMs"));
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
