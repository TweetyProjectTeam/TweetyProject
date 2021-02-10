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
 *  Copyright 2019 The TweetyProject Team <http://tweetyproject.org/contact/>
 */
package org.tweetyproject.arg.adf.reasoner.sat.pipeline;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import org.tweetyproject.arg.adf.reasoner.sat.generator.CandidateGenerator;
import org.tweetyproject.arg.adf.reasoner.sat.generator.ConflictFreeGenerator;
import org.tweetyproject.arg.adf.reasoner.sat.generator.GroundGenerator;
import org.tweetyproject.arg.adf.reasoner.sat.generator.ModelGenerator;
import org.tweetyproject.arg.adf.reasoner.sat.processor.InterpretationProcessor;
import org.tweetyproject.arg.adf.reasoner.sat.processor.KBipolarStateProcessor;
import org.tweetyproject.arg.adf.reasoner.sat.processor.MaximizeInterpretationProcessor;
import org.tweetyproject.arg.adf.reasoner.sat.processor.StateProcessor;
import org.tweetyproject.arg.adf.reasoner.sat.verifier.AdmissibleVerifier;
import org.tweetyproject.arg.adf.reasoner.sat.verifier.CompleteVerifier;
import org.tweetyproject.arg.adf.reasoner.sat.verifier.GrounderStableVerifier;
import org.tweetyproject.arg.adf.reasoner.sat.verifier.Verifier;

/**
 * Represents the computational building blocks of SAT based ADF semantics.
 * 
 * @author Mathias Hofer
 *
 */
public final class Semantics {
	
	private final List<StateProcessor> stateProcessors;

	private final CandidateGenerator candidateGenerator;

	private final Verifier verifier;

	private final List<InterpretationProcessor> modelProcessors;

	private Semantics(Builder builder) {
		this.candidateGenerator = builder.candidateGenerator;
		this.stateProcessors = List.copyOf(builder.stateProcessors);
		this.verifier = builder.verifier;
		this.modelProcessors = List.copyOf(builder.modelProcessors);
	}

	public static Builder builder(CandidateGenerator candidateGenerator) {
		return new Builder(candidateGenerator);
	}

	/**
	 * @return the candidateGenerator
	 */
	public CandidateGenerator getCandidateGenerator() {
		return candidateGenerator;
	}

	/**
	 * @return the stateProcessors
	 */
	public List<StateProcessor> getStateProcessors() {
		return stateProcessors;
	}

	/**
	 * @return the verifier
	 */
	public Verifier getVerifier() {
		return verifier;
	}

	/**
	 * @return the modelProcessors
	 */
	public List<InterpretationProcessor> getModelProcessors() {
		return modelProcessors;
	}

	public boolean hasVerifier() {
		return verifier != null;
	}
	
	public boolean hasStateProcessors() {
		return !stateProcessors.isEmpty();
	}
	
	public boolean hasModelProcessors() {
		return !modelProcessors.isEmpty();
	}
		
	public static final class Builder {

		private final CandidateGenerator candidateGenerator;

		private final List<StateProcessor> stateProcessors = new LinkedList<>();

		private Verifier verifier;

		private final List<InterpretationProcessor> modelProcessors = new LinkedList<>();

		/**
		 * @param candidateGenerator
		 */
		Builder(CandidateGenerator candidateGenerator) {
			this.candidateGenerator = Objects.requireNonNull(candidateGenerator);
		}

		public Builder addStateProcessor(StateProcessor stateProcessor) {
			this.stateProcessors.add(Objects.requireNonNull(stateProcessor));
			return this;
		}

		public Builder addModelProcessor(InterpretationProcessor modelProcessor) {
			this.modelProcessors.add(Objects.requireNonNull(modelProcessor));
			return this;
		}

		public Builder setVerifier(Verifier verifier) {
			this.verifier = Objects.requireNonNull(verifier);
			return this;
		}

		public Semantics build() {
			return new Semantics(this);
		}
	}
	
	/*
	 * Default configurations:
	 */
	
	public static Semantics conflictFree() {
		return Semantics.builder(new ConflictFreeGenerator()).build();
	}

	public static Semantics naive() {
		return Semantics.builder(new ConflictFreeGenerator())
				.addModelProcessor(new MaximizeInterpretationProcessor())
				.build();
	}

	public static Semantics admissible() {
		return Semantics.builder(new ConflictFreeGenerator())
				.addStateProcessor(new KBipolarStateProcessor())
				.setVerifier(new AdmissibleVerifier())
				.build();
	}
	
	public static Semantics preferred() {
		Verifier admissibleVerifier = new AdmissibleVerifier();
		return Semantics.builder(new ConflictFreeGenerator())
				.addStateProcessor(new KBipolarStateProcessor())
				.setVerifier(admissibleVerifier)
				.addModelProcessor(new MaximizeInterpretationProcessor(admissibleVerifier))
				.build();
	}
	
	public static Semantics stable() {
		return Semantics.builder(new ModelGenerator())
				.setVerifier(new GrounderStableVerifier(new GroundGenerator()))
				.build();
	}

	public static Semantics complete() {
		return Semantics.builder(new ConflictFreeGenerator())
				.addStateProcessor(new KBipolarStateProcessor())
				.setVerifier(new CompleteVerifier())
				.build();
	}

	public static Semantics model() {
		return Semantics.builder(new ModelGenerator()).build();
	}

	public static Semantics ground() {
		return Semantics.builder(new GroundGenerator()).build();
	}

}
