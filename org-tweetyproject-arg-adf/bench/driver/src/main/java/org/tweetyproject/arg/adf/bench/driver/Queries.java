package org.tweetyproject.arg.adf.bench.driver;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

import org.tweetyproject.arg.adf.bench.common.Answers;

/**
 * The frozen queries of an instance per task (plan, section 4.4) and the
 * oracle's expected answers.
 * <ul>
 * <li>EE, CE, SE: one query without parameters.</li>
 * <li>DC, DS: up to {@value #ARGUMENTS} arguments, the same for both tasks.</li>
 * <li>VE: valid and invalid candidates: the all-undecided and the grounded
 * interpretation, {@value #RANDOM_CANDIDATES} random three-valued and one
 * random two-valued interpretation, and, if the oracle enumerates the
 * instance, up to {@value #MODEL_CANDIDATES} of its models.</li>
 * </ul>
 * Queries depend only on the instance (its file name seeds the choice), the
 * semantics and the oracle limit, so every implementation and every rerun gets
 * the same ones.
 */
final class Queries {

	static final int ARGUMENTS = 3, RANDOM_CANDIDATES = 2, MODEL_CANDIDATES = 2;

	private static final long SEED = 2026;

	/** a query: a label for results and reports, e.g. {@code arg=a3}, and the runner flags */
	record Query(String label, List<String> flags) {}

	private Queries() {}

	static List<Query> of(KppAdf adf, String file, String task, String sem, int oracleMax) {
		switch (task) {
		case "EE", "CE", "SE":
			return List.of(new Query("", List.of()));
		case "DC", "DS": {
			List<String> names = new ArrayList<>(adf.names);
			Collections.shuffle(names, new Random(SEED * 31 + file.hashCode()));
			List<Query> queries = new ArrayList<>();
			for (String name : names.subList(0, Math.min(ARGUMENTS, names.size()))) {
				queries.add(new Query("arg=" + name, List.of("--arg", name)));
			}
			return queries;
		}
		case "VE": {
			Oracle oracle = new Oracle(adf);
			Random rng = new Random((SEED * 31 + file.hashCode()) * 31 + sem.hashCode());
			int n = adf.size();
			Set<String> candidates = new LinkedHashSet<>();
			byte[] v = new byte[n];
			Arrays.fill(v, Oracle.U);
			candidates.add(oracle.canonical(v));
			try {
				candidates.add(oracle.canonical(oracle.grounded()));
			} catch (UnsupportedOperationException e) {
				// too many undecided parents for Γ
			}
			for (int i = 0; i < RANDOM_CANDIDATES + 1; i++) {
				boolean twoValued = i == RANDOM_CANDIDATES;
				for (int a = 0; a < n; a++) {
					v[a] = (byte) rng.nextInt(twoValued ? 2 : 3);
				}
				candidates.add(oracle.canonical(v));
			}
			if (n <= oracleMax) {
				List<String> models = new ArrayList<>(oracle.models(sem));
				Collections.shuffle(models, rng);
				candidates.addAll(models.subList(0, Math.min(MODEL_CANDIDATES, models.size())));
			}
			List<Query> queries = new ArrayList<>();
			for (String c : candidates) {
				queries.add(new Query("interpretation=" + c, List.of("--interpretation", c)));
			}
			return queries;
		}
		default:
			throw new IllegalArgumentException("Unknown task " + task);
		}
	}

	/**
	 * @return the oracle's answer in runner format plus {@code validation}
	 *         ({@code oracle}, or {@code independent-check} for VE beyond the
	 *         oracle limit), or null if the oracle cannot answer the query
	 */
	static Map<String, Object> expectation(KppAdf adf, Query query, String task, String sem, int oracleMax) {
		Oracle oracle = new Oracle(adf);
		boolean enumerable = adf.size() <= oracleMax;
		Map<String, Object> result = new LinkedHashMap<>();
		long t0 = System.nanoTime();
		switch (task) {
		case "EE", "CE", "SE": {
			if (!enumerable) {
				return null;
			}
			List<String> models = oracle.models(sem);
			if (task.equals("SE")) {
				result.put("answer", models.isEmpty() ? null : models.get(0));
			} else {
				result.put("count", models.size());
			}
			if (task.equals("EE")) {
				result.put("answerHash", Answers.hash(models));
			}
			break;
		}
		case "DC", "DS":
			if (!enumerable) {
				return null;
			}
			result.put("answer", oracle.accepted(sem, query.flags().get(1), task.equals("DC")));
			break;
		case "VE": {
			List<Set<String>> sets = Answers.parseCanonical(query.flags().get(1));
			Boolean answer = oracle.verify(oracle.interpretation(sets.get(0), sets.get(1)), sem);
			if (answer == null) {
				return null;
			}
			result.put("answer", answer);
			break;
		}
		default:
			throw new IllegalArgumentException("Unknown task " + task);
		}
		Map<String, Object> line = new LinkedHashMap<>();
		line.put("status", "ok");
		line.putAll(result);
		line.put("validation", enumerable ? "oracle" : "independent-check");
		line.put("timesMs", List.of((System.nanoTime() - t0) / 1e6));
		return line;
	}

}
