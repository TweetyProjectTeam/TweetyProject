package org.tweetyproject.arg.adf.bench.common;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.TreeSet;

/**
 * Canonical answers shared by the runners and the oracle: a model is written as
 * {@code t:{..} f:{..}} with sorted argument names, and a set of models is
 * identified by its size and an order-independent SHA-256 over the sorted
 * canonical models.
 */
public final class Answers {

	private Answers() {}

	/**
	 * @param satisfied   the names of the arguments mapped to true
	 * @param unsatisfied the names of the arguments mapped to false
	 * @return the canonical form of the interpretation
	 */
	public static String canonical(Collection<String> satisfied, Collection<String> unsatisfied) {
		return "t:{" + String.join(",", new TreeSet<>(satisfied)) + "} f:{" + String.join(",", new TreeSet<>(unsatisfied)) + "}";
	}

	/**
	 * @param canonical the canonical models, in any order
	 * @return the first 16 hex digits of the SHA-256 over the sorted models
	 */
	public static String hash(Collection<String> canonical) {
		List<String> sorted = new ArrayList<>(canonical);
		Collections.sort(sorted);
		MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("SHA-256");
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException(e);
		}
		for (String c : sorted) {
			digest.update(c.getBytes(StandardCharsets.UTF_8));
			digest.update((byte) '\n');
		}
		StringBuilder hex = new StringBuilder();
		for (byte b : digest.digest()) {
			hex.append(String.format("%02x", b));
		}
		return hex.substring(0, 16);
	}

}
