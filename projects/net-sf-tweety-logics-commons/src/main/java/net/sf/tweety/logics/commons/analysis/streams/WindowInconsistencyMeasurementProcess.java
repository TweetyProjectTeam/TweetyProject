/*
 *  This file is part of "Tweety", a collection of Java libraries for
 *  logical aspects of artificial intelligence and knowledge representation.
 *
 *  Tweety is free software: you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License version 3 as
 *  published by the Free Software Foundation.
 *
 *  This program is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package net.sf.tweety.logics.commons.analysis.streams;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import net.sf.tweety.commons.Formula;
import net.sf.tweety.logics.commons.analysis.BeliefSetInconsistencyMeasure;
import net.sf.tweety.math.func.BinaryFunction;
import net.sf.tweety.math.func.MaxFunction;

/**
 * This inconsistency measurement process keeps a window of a number of previous 
 * formulas in memory and computes the inconsistency value from this window using
 * an ordinary inconsistency measure.
 * 
 * @author Matthias Thimm
 *
 * @param <S> The type of formulas
 * @param <T> The type of belief bases
 */
public abstract class WindowInconsistencyMeasurementProcess<S extends Formula> extends InconsistencyMeasurementProcess<S>{

	/** The inconsistency measure used */
	private BeliefSetInconsistencyMeasure<S> measure;
	/** The window size. */	
	private int windowsize;
	/** the current window of formulas */
	private Queue<S> formulas;	
	/** The previous value of the measure. */
	private double previousValue;
	/** If X1 is the previous
	 * inconsistency value, X2 is the new inconsistency value on the new window, then
	 * the actual new inconsistency value X is determined by aggregating X1 and X2 with this function.
	 * If none is given the maximum function is assumed. */
	private BinaryFunction<Double,Double,Double> agg = null;
	/** The name of this process. */
	private String name;
	
	/** Key for the configuration map that points to the inconsistency measure to be used. */
	public static final String CONFIG_MEASURE = "config_measure";
	/** Key for the configuration map that points to the window size to be used. */
	public static final String CONFIG_WINDOWSIZE = "config_windowsize";
	/** Key for the configuration map that points to the aggregation function used. If X1 is the previous
	 * inconsistency value, X2 is the new inconsistency value on the new window, then
	 * the actual new inconsistency value X is determined by aggregating X1 and X2 with this function. If none
	 * is given the maximum function is assumed. */
	public static final String CONFIG_AGGREGATIONFUNCTION = "config_aggregationfunction";
	/** Key for the configuration map that points to the name to be used. */
	public static final String CONFIG_NAME = "config_name";
	
	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.streams.InconsistencyMeasurementProcess#init()
	 */
	@SuppressWarnings("unchecked")
	@Override
	protected void init(Map<String,Object> config) {
		this.formulas = new LinkedList<S>();
		if(!config.containsKey(WindowInconsistencyMeasurementProcess.CONFIG_MEASURE))
			throw new RuntimeException("Key \"CONFIG_MEASURE\" expected for configuration of WindowInconsistencyMeasurementProcess");
		this.measure = (BeliefSetInconsistencyMeasure<S>) config.get(WindowInconsistencyMeasurementProcess.CONFIG_MEASURE);
		if(config.containsKey(WindowInconsistencyMeasurementProcess.CONFIG_WINDOWSIZE))
			this.windowsize = (int) config.get(WindowInconsistencyMeasurementProcess.CONFIG_WINDOWSIZE);
		else this.windowsize = -1;
		if(config.containsKey(WindowInconsistencyMeasurementProcess.CONFIG_AGGREGATIONFUNCTION))
			this.agg = (BinaryFunction<Double,Double,Double>) config.get(WindowInconsistencyMeasurementProcess.CONFIG_AGGREGATIONFUNCTION);
		else this.agg = new MaxFunction();
		if(config.containsKey(WindowInconsistencyMeasurementProcess.CONFIG_NAME))
			this.name = (String) config.get(WindowInconsistencyMeasurementProcess.CONFIG_NAME);
		else this.name = "";		
		this.previousValue = 0;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.streams.InconsistencyMeasurementProcess#update(net.sf.tweety.Formula)
	 */
	@Override
	protected double update(S formula) {
		if((this.windowsize != -1) && (this.formulas.size() >= this.windowsize))
			this.formulas.poll();
		// remove formula from the queue if it appereared already
		this.formulas.remove(formula);
		this.formulas.add(formula);
		double oldVal = this.previousValue;
		double newVal = this.measure.inconsistencyMeasure(this.formulas);
		this.previousValue = this.agg.eval(oldVal, newVal); 
		return this.previousValue;
	}

	/* (non-Javadoc)
	 * @see net.sf.tweety.logics.commons.analysis.streams.InconsistencyMeasurementProcess#toString()
	 */
	@Override
	public String toString() {
		return "WIMP" + this.name + "-" + this.measure.toString();
	}
}
