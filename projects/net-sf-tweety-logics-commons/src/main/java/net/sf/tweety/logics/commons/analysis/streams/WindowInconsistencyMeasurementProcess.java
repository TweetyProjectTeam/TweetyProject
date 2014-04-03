package net.sf.tweety.logics.commons.analysis.streams;

import java.util.LinkedList;
import java.util.Map;
import java.util.Queue;

import net.sf.tweety.Formula;
import net.sf.tweety.logics.commons.analysis.BeliefSetInconsistencyMeasure;

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
	/** Whether the inconsistency value should be smoothed: if X1 is the previous
	 * inconsistency value, X2 is the new inconsistency value on the new window, then
	 * the actual new inconsistency value X is determined by X=X1*smoothingFactor + X2*(1-smoothingFactor).
	 * This value should be between 0 and 1. If it is -1 no smoothing is done. */
	private double smoothingFactor;
	/** The name of this process. */
	private String name;
	
	/** Key for the configuration map that points to the inconsistency measure to be used. */
	public static final String CONFIG_MEASURE = "config_measure";
	/** Key for the configuration map that points to the window size to be used. */
	public static final String CONFIG_WINDOWSIZE = "config_windowsize";
	/** Key for the configuration map that points to the smoothing factor to be used. if X1 is the previous
	 * inconsistency value, X2 is the new inconsistency value on the new window, then
	 * the actual new inconsistency value X is determined by X=X1*smoothingFactor + X2*(1-smoothingFactor).
	 * This value should be between 0 and 1. If it is -1 no smoothing is done (the same as setting
	 * the smoothing factor to 0. */
	public static final String CONFIG_SMOOTHINGFACTOR = "config_smoothingfactor";
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
		if(config.containsKey(WindowInconsistencyMeasurementProcess.CONFIG_SMOOTHINGFACTOR))
			this.smoothingFactor = (double) config.get(WindowInconsistencyMeasurementProcess.CONFIG_SMOOTHINGFACTOR);
		else this.smoothingFactor = -1;
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
		this.formulas.add(formula);
		double oldVal = this.previousValue;
		double newVal = this.measure.inconsistencyMeasure(this.formulas);
		if(this.smoothingFactor != -1)
			this.previousValue = oldVal * this.smoothingFactor + newVal * (1-this.smoothingFactor);
		else this.previousValue = newVal;
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
