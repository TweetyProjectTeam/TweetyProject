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
 *  Copyright 2016 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.logics.commons.analysis.streams;

import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import org.tweetyproject.commons.Formula;
import org.tweetyproject.commons.streams.FormulaStream;

/**
 * The actual process of an inconsistency measure on streams.
 * @author Matthias Thimm
 *
 * @param <S> The type of formulas.
 */
public abstract class InconsistencyMeasurementProcess<S extends Formula> extends Thread {
	
	/** Key for the configuration map that gives a time out (given in seconds) 
	 * for a single update operation Default value is -1 which means no time out. */
	public static final String CONFIG_TIMEOUT = "config_timeout";
	
	/** The stream.*/
	private FormulaStream<S> stream;
	/** Whether execution should be aborted. */
	private boolean abort; 
	/** The current inconsistency value.*/
	private Double iValue;
	/** The measure from where this process has been dispatched. */
	private StreamBasedInconsistencyMeasure<S> parent;
	/** Time out for the update operation (in seconds). */
	private long timeout;
	
	/**
	 * For handling timeouts.
	 */
	private class UpdateCallee implements Callable<Double>{
		InconsistencyMeasurementProcess<S> imp;
		S formula;
		public UpdateCallee(InconsistencyMeasurementProcess<S> imp, S formula){
			this.imp = imp;
			this.formula = formula;
		}
		@Override
		public Double call() throws Exception {
			return this.imp.update(this.formula);
		}		
	}
	
	/**
	 * Creates a new process for the given stream.
	 */
	public InconsistencyMeasurementProcess(){ }
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		this.parent.fireInconsistencyMeasurementStartedEvent(new InconsistencyUpdateEvent(this.parent, this, -1d, null));
		ExecutorService executor = Executors.newSingleThreadExecutor();
		while(!this.abort && this.stream.hasNext()){
			S f = this.stream.next();
			if(this.timeout == -1)
				this.iValue = this.update(f);
			else{				
			    try {
			    	// handle timeout				
				    Future<Double> future = executor.submit(new UpdateCallee(this,f));
			    	this.iValue = future.get(this.timeout, TimeUnit.SECONDS);	            
			    } catch (Exception e) {
			        //abort the complete process
			    	e.printStackTrace();
			    	this.abort = true;
			    	this.iValue = -1d;
			    }			    
			}
			this.parent.fireInconsistencyUpdateEvent(new InconsistencyUpdateEvent(this.parent, this, this.iValue, f));
		}
		executor.shutdownNow();
	}
	
	/**
	 * Initialization statements.
	 * @param stream some formula stream.
	 * @param parent the  measure from where this process has been dispatched.
	 * @param config configuration options for the specific process.
	 */
	protected void init(FormulaStream<S> stream, StreamBasedInconsistencyMeasure<S> parent, Map<String,Object> config){
		this.abort = false;
		this.stream = stream;
		this.iValue = 0d;
		this.parent = parent;
		if(config.containsKey(InconsistencyMeasurementProcess.CONFIG_TIMEOUT))
			this.timeout = (long) config.get(InconsistencyMeasurementProcess.CONFIG_TIMEOUT);
		else this.timeout = -1;
		this.init(config);
	}
	
	/**
	 * Additional initialization statements are put here.
	 * @param config the configuration
	 */
	protected abstract void init(Map<String,Object> config);
	
	/**
	 * Updates the inconsistency value with the new formula.
	 * @param formula some formula.
	 * @return the current inconsistency value.
	 */
	protected abstract double update(S formula);
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#toString()
	 */
	public abstract String toString();
	
	/**
	 * Aborts the measurement of a stream.
	 */
	public void abort(){
		this.abort = true;
	}
	
	/**
	 * Returns the current inconsistency value of this stream processing or 
	 * the last value if the stream processing has finalized.
	 * @return the current inconsistency value.
	 */
	public Double getInconsistencyValue(){
		return this.iValue;
	}
}
