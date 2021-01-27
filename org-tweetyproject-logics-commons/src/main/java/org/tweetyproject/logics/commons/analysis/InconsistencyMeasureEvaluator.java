/*
 *  This file is part of "TweetyProject", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  TweetyProject is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU Lesser General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU Lesser General Public License for more details.
 *
 *  You should have received a copy of the GNU Lesser General Public License
 *  along with this program. If not, see <http://www.gnu.org/licenses/>.
 *
 *  Copyright 2020 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.logics.commons.analysis;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import org.tweetyproject.commons.BeliefSet;
import org.tweetyproject.commons.Formula;
import org.tweetyproject.commons.Parser;
import org.tweetyproject.commons.ParserException;
import org.tweetyproject.commons.util.Pair;

/**
 * This class provides functionality to quickly load or generate sample datasets
 * and to use them to compare the results and performances of different
 * inconsistency measure implementations.
 * 
 * @author Anna Gessler
 *
 * @param <T> The type of formulas belief sets are made of
 * @param <U> The type of belief sets that are evaluated
 */
public class InconsistencyMeasureEvaluator<T extends Formula, U extends BeliefSet<T, ?>> {

	/**
	 * The dataset used for testing. Consists of a collection of knowledge bases.
	 */
	private List<U> dataset = new ArrayList<U>();
	
	/**
	 * Stores (file-)name of the datasat entries.
	 */
	private List<String> dataset_names = new ArrayList<String>();

	/**
	 * The set of inconsistency measures that will be evaluated.
	 */
	private List<BeliefSetInconsistencyMeasure<T>> inconsistency_measures = new ArrayList<BeliefSetInconsistencyMeasure<T>>();

	/**
	 * Timeout for inconsistency value computation, in ms.
	 */
	private long TIMEOUT = 60000;

	/**
	 * Time function used to measure time.
	 */
	private Supplier<Long> time = () -> System.nanoTime() / 1000000;

	/**
	 * Create a new empty InconsistencyMeasureEvaluator.
	 * 
	 * @param measure an inconsistency measure
	 */
	public InconsistencyMeasureEvaluator() {
	}

	/**
	 * Create a new InconsistencyMeasureEvaluator with the given inconsistency
	 * measure.
	 * 
	 * @param measure an inconsistency measure
	 */
	public InconsistencyMeasureEvaluator(BeliefSetInconsistencyMeasure<T> i) {
		this.inconsistency_measures.add(i);
	}
	
	/**
	 * Create a new InconsistencyMeasureEvaluator with the given inconsistency
	 * measure.
	 * 
	 * @param measure an inconsistency measure
	 */
	public InconsistencyMeasureEvaluator(Collection<BeliefSetInconsistencyMeasure<T>> measures) {
		this.inconsistency_measures.addAll(measures);
	}

	/**
	 * Create a new InconsistencyMeasureEvaluator with the given dataset and the
	 * given inconsistency measure.
	 * 
	 * @param dataset collection of belief bases
	 * @param measure an inconsistency measure
	 */
	public InconsistencyMeasureEvaluator(Collection<U> dataset, BeliefSetInconsistencyMeasure<T> i) {
		this.dataset = (List<U>) dataset;
		this.inconsistency_measures.add(i);
	}
	
	/**
	 * Create a new InconsistencyMeasureEvaluator with the given dataset and the
	 * given inconsistency measures.
	 * 
	 * @param dataset collection of belief bases
	 * @param measure an inconsistency measure
	 */
	public InconsistencyMeasureEvaluator(Collection<U> dataset, Collection<BeliefSetInconsistencyMeasure<T>> measures) {
		this.dataset = (List<U>) dataset;
		this.inconsistency_measures.addAll(measures);
	}
	
	/**
	 * Adds the given inconsistency measure.
	 * 
	 * @param i BeliefSetInconsistencyMeasure
	 */
	public void addInconsistencyMeasure(BeliefSetInconsistencyMeasure<T> i) {
		this.inconsistency_measures.add(i);
	}
	
	/**
	 * Sets the function used to measure time in ms.
	 * 
	 * @param timeFunction
	 */
	public void setTimeFunction(Supplier<Long> timeFunction) {
		this.time = timeFunction;
	}

	/**
	 * Sets the timeout.
	 * 
	 * @param timeout in ms, must be non-negative
	 */
	public void setTimeout(long timeout) {
		if (timeout >= 0)
			this.TIMEOUT = timeout;
	}
	
	/**
	 * Adds the given knowledge base to the testing dataset.
	 * 
	 * @param kb knowledge base
	 */
	public void addKnowledgeBase(U kb) {
		this.dataset.add(kb);
		this.dataset_names.add("TweetyKB"); 
	}

	/**
	 * Adds the given knowledge bases to the testing dataset.
	 * 
	 * @param kb knowledge bases
	 */
	public void addKnowledgeBases(List<U> kbs) {
		this.dataset.addAll(kbs);
		for (int i = 0; i < kbs.size(); i++)
			this.dataset_names.add("TweetyKB"); 
	}

	/**
	 * Parse all knowledge bases from files in the given folder and add them to the
	 * testing dataset.
	 * 
	 * @param path        that contains knowledge bases (one knowledge base per
	 *                    file)
	 * @param p			  appropriate parser for the format the knowledge bases are saved in
	 *                    (i.e. DimacsParser for files in Dimacs format, PlParser
	 *                    for files in TweetyProject pl Syntax)
	 * @param n           maximum number of knowledge bases to parse
	 * @throws IOException, ParserException, FileNotFoundException
	 */
	public void parseDatasetFromPath(String path, Parser<U, T> p, int n) throws FileNotFoundException, ParserException, IOException {
		File dir = new File(path);
		if (dir.isFile()) {
			U kb = p.parseBeliefBaseFromFile(dir.getAbsolutePath());
			this.dataset.add(kb);
			this.dataset_names.add(dir.getName());
			return;
		}
		File[] directoryListing = dir.listFiles();
		if (directoryListing != null) {
			int i = 0;
			for (File child : directoryListing) {
				if (i++ >= n)
					break;
				if (child.isFile()) {
				U kb = p.parseBeliefBaseFromFile(child.getAbsolutePath());
				this.dataset.add(kb);
				this.dataset_names.add(child.getName()); }
			}
		}
	}

	/**
	 * Add n knowledge bases from the given iterator (e.g., a random sampler) to the
	 * testing dataset.
	 * 
	 * @param sampler
	 * @param n                  how many knowledge bases will be added at most
	 */
	public void addFromSampler(Iterator<U> sampler, int n) {
		for (int i = 0; (sampler.hasNext() && i < n); i++) {
			this.dataset.add(sampler.next());
			this.dataset_names.add(sampler.getClass().getSimpleName());
		}
	}

	/**
	 * Computes inconsistency values for all belief bases in the dataset using all
	 * inconsistency measures and returns a report of the results.
	 * 
	 * @return InconsistencyMeasureReport 
	 */
	public InconsistencyMeasureReport<T,U> compareMeasures() {
		if (dataset.isEmpty())
			throw new IllegalArgumentException("Test dataset is empty.");

		Map<String, List<InconsistencyMeasureResult>> list_of_results = new HashMap<String, List<InconsistencyMeasureResult>>();
		for (BeliefSetInconsistencyMeasure<T> i : inconsistency_measures) {
			List<InconsistencyMeasureResult> results = new ArrayList<InconsistencyMeasureResult>();
			list_of_results.put(i.toString(), results);
		}

		for (int j = 0; j < this.dataset.size(); j++) {
			U kb = this.dataset.get(j);
			for (BeliefSetInconsistencyMeasure<T> i : inconsistency_measures) {
				ExecutorService executorService = Executors.newSingleThreadExecutor();
				InconsistencyMeasureResult result = null;
				long startTime = time.get();
				Future<InconsistencyMeasureResult> task = executorService.submit(() -> InconsistencyMeasureResult.ok(i.inconsistencyMeasure(kb)));
				try {
					result = task.get(this.TIMEOUT, TimeUnit.MILLISECONDS);
				} catch (TimeoutException e) {
					result = InconsistencyMeasureResult.timeout();
					task.cancel(true);
				}  catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
					task.cancel(true);
				}
				long elapsedTime = time.get() - startTime;
				result.setElapsedTime(elapsedTime);
				List<InconsistencyMeasureResult> results = list_of_results.get(i.toString());
				results.add(result);
				list_of_results.put(i.toString(), results);
				executorService.shutdownNow();
			}
			
		}
		
		List<String> measureNames = inconsistency_measures.stream().map( e -> e.toString() ).collect( Collectors.toList() );
		return new InconsistencyMeasureReport<T,U>(measureNames, this.dataset, this.dataset_names, list_of_results);
	}
	
	/**
	 * @return the dataset of this evaluator
	 */
	public List<U> getDataset() {
		return this.dataset;
	}
	
	/**
	 * @return the dataset of this evaluator, where each entry is the (file-)name and the corresponding kb
	 */
	public List<Pair<String,U>> getDatasetWithNames() {
		List<Pair<String,U>> result = new ArrayList<Pair<String,U>>();
		for (int i = 0; i < this.dataset.size(); i++) 
			result.add(new Pair<String,U>(this.dataset_names.get(i), this.dataset.get(i)));
		return result;
	}
	
}
