package org.tweetyproject.arg.adf.reasoner.sat.execution;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import org.tweetyproject.arg.adf.reasoner.sat.decomposer.Decomposer;
import org.tweetyproject.arg.adf.reasoner.sat.decomposer.RandomDecomposer;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.PropositionalMapping;
import org.tweetyproject.arg.adf.reasoner.sat.generator.CandidateGenerator;
import org.tweetyproject.arg.adf.reasoner.sat.generator.ConflictFreeGenerator;
import org.tweetyproject.arg.adf.reasoner.sat.generator.GroundGenerator;
import org.tweetyproject.arg.adf.reasoner.sat.generator.ModelGenerator;
import org.tweetyproject.arg.adf.reasoner.sat.processor.InterpretationProcessor;
import org.tweetyproject.arg.adf.reasoner.sat.processor.KBipolarStateProcessor;
import org.tweetyproject.arg.adf.reasoner.sat.processor.StateProcessor;
import org.tweetyproject.arg.adf.reasoner.sat.processor.ConflictFreeMaximizer;
import org.tweetyproject.arg.adf.reasoner.sat.processor.AdmissibleMaximizer;
import org.tweetyproject.arg.adf.reasoner.sat.verifier.AdmissibleVerifier;
import org.tweetyproject.arg.adf.reasoner.sat.verifier.CompleteVerifier;
import org.tweetyproject.arg.adf.reasoner.sat.verifier.GrounderStableVerifier;
import org.tweetyproject.arg.adf.reasoner.sat.verifier.Verifier;
import org.tweetyproject.arg.adf.sat.SatSolverState;
import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;

abstract class DefaultSemantics implements Semantics {
	
	final AbstractDialecticalFramework adf;
	
	final PropositionalMapping mapping;

	private DefaultSemantics(AbstractDialecticalFramework adf) {
		this.adf = Objects.requireNonNull(adf);
		this.mapping = new PropositionalMapping(adf);
	}
	
	@Override
	public Decomposer createDecomposer() {
		return new RandomDecomposer();
	}
	
	static final class ConflictFreeSemantics extends DefaultSemantics {

		public ConflictFreeSemantics(AbstractDialecticalFramework adf) {
			super(adf);
		}

		@Override
		public CandidateGenerator createCandidateGenerator() {
			return ConflictFreeGenerator.withoutPrefix(adf, mapping);
		}

		@Override
		public List<StateProcessor> createStateProcessors() {
			return List.of();
		}
		
		@Override
		public List<InterpretationProcessor> createCandidateProcessor(Supplier<SatSolverState> stateSupplier) {
			return List.of();
		}

		@Override
		public Optional<Verifier> createVerifier(Supplier<SatSolverState> stateSupplier) {
			return Optional.empty();
		}

		@Override
		public List<InterpretationProcessor> createModelProcessors(Supplier<SatSolverState> stateSupplier) {
			return List.of();
		}

		@Override
		public Semantics withPrefix(Interpretation prefix) {
			return new PrefixSemantics.ConflictFreeSemantics(adf, mapping, prefix);
		}		
	
	}
	
	static final class NaiveSemantics extends DefaultSemantics {
		
		public NaiveSemantics(AbstractDialecticalFramework adf) {
			super(adf);
		}
		
		@Override
		public CandidateGenerator createCandidateGenerator() {
			return ConflictFreeGenerator.withoutPrefix(adf, mapping);
		}

		@Override
		public List<StateProcessor> createStateProcessors() {
			return List.of();
		}
		
		@Override
		public List<InterpretationProcessor> createCandidateProcessor(Supplier<SatSolverState> stateSupplier) {
			return List.of();
		}

		@Override
		public Optional<Verifier> createVerifier(Supplier<SatSolverState> stateSupplier) {
			return Optional.empty();
		}

		@Override
		public List<InterpretationProcessor> createModelProcessors(Supplier<SatSolverState> stateSupplier) {
			return List.of(ConflictFreeMaximizer.withoutPrefix(stateSupplier, adf, mapping));
		}
		
		@Override
		public Semantics withPrefix(Interpretation prefix) {
			return new PrefixSemantics.NaiveSemantics(adf, mapping, prefix);
		}
		
	}
	
	static final class AdmissibleSemantics extends DefaultSemantics {
		
		public AdmissibleSemantics(AbstractDialecticalFramework adf) {
			super(adf);
		}

		@Override
		public CandidateGenerator createCandidateGenerator() {
			return ConflictFreeGenerator.withoutPrefix(adf, mapping);
		}

		@Override
		public List<StateProcessor> createStateProcessors() {
			return List.of(new KBipolarStateProcessor(adf, mapping));
		}
		
		@Override
		public List<InterpretationProcessor> createCandidateProcessor(Supplier<SatSolverState> stateSupplier) {
			return List.of();
		}

		@Override
		public Optional<Verifier> createVerifier(Supplier<SatSolverState> stateSupplier) {
			return Optional.of(new AdmissibleVerifier(stateSupplier, adf, mapping));
		}

		@Override
		public List<InterpretationProcessor> createModelProcessors(Supplier<SatSolverState> stateSupplier) {
			return List.of();
		}
		
		@Override
		public Semantics withPrefix(Interpretation prefix) {
			return new PrefixSemantics.AdmissibleSemantics(adf, mapping, prefix);
		}
	
	}
	
	static final class PreferredSemantics extends DefaultSemantics {
		
		public PreferredSemantics(AbstractDialecticalFramework adf) {
			super(adf);
		}

		@Override
		public CandidateGenerator createCandidateGenerator() {
			return ConflictFreeGenerator.withoutPrefix(adf, mapping);
		}

		@Override
		public List<StateProcessor> createStateProcessors() {
			return List.of(new KBipolarStateProcessor(adf, mapping));
		}
		
		@Override
		public List<InterpretationProcessor> createCandidateProcessor(Supplier<SatSolverState> stateSupplier) {
			return List.of();
		}

		@Override
		public Optional<Verifier> createVerifier(Supplier<SatSolverState> stateSupplier) {
			return Optional.of(new AdmissibleVerifier(stateSupplier, adf, mapping));
		}

		@Override
		public List<InterpretationProcessor> createModelProcessors(Supplier<SatSolverState> stateSupplier) {
			return List.of(AdmissibleMaximizer.withoutPrefix(stateSupplier, adf, mapping));
		}
		
		@Override
		public Semantics withPrefix(Interpretation prefix) {
			return new PrefixSemantics.PreferredSemantics(adf, mapping, prefix);
		}
		
	}
	
	static final class StableSemantics extends DefaultSemantics {
		
		public StableSemantics(AbstractDialecticalFramework adf) {
			super(adf);
		}
		
		@Override
		public Decomposer createDecomposer() {
			return new RandomDecomposer().asTwoValued();
		}

		@Override
		public CandidateGenerator createCandidateGenerator() {
			return ModelGenerator.withoutPrefix(adf, mapping);
		}

		@Override
		public List<StateProcessor> createStateProcessors() {
			return List.of();
		}
		
		@Override
		public List<InterpretationProcessor> createCandidateProcessor(Supplier<SatSolverState> stateSupplier) {
			return List.of();
		}

		@Override
		public Optional<Verifier> createVerifier(Supplier<SatSolverState> stateSupplier) {
			return Optional.of(new GrounderStableVerifier(stateSupplier, adf, mapping));
		}

		@Override
		public List<InterpretationProcessor> createModelProcessors(Supplier<SatSolverState> stateSupplier) {
			return List.of();
		}
		
		@Override
		public Semantics withPrefix(Interpretation prefix) {
			return new PrefixSemantics.StableSemantics(adf, mapping, prefix);
		}
		
	}
	
	static final class CompleteSemantics extends DefaultSemantics {
		
		public CompleteSemantics(AbstractDialecticalFramework adf) {
			super(adf);
		}

		@Override
		public CandidateGenerator createCandidateGenerator() {
			return ConflictFreeGenerator.withoutPrefix(adf, mapping);
		}

		@Override
		public List<StateProcessor> createStateProcessors() {
			return List.of(new KBipolarStateProcessor(adf, mapping));
		}
		
		@Override
		public List<InterpretationProcessor> createCandidateProcessor(Supplier<SatSolverState> stateSupplier) {
			return List.of();
		}

		@Override
		public Optional<Verifier> createVerifier(Supplier<SatSolverState> stateSupplier) {
			return Optional.of(new CompleteVerifier(stateSupplier, adf, mapping));
		}

		@Override
		public List<InterpretationProcessor> createModelProcessors(Supplier<SatSolverState> stateSupplier) {
			return List.of();
		}
		
		@Override
		public Semantics withPrefix(Interpretation prefix) {
			return new PrefixSemantics.CompleteSemantics(adf, mapping, prefix);
		}
		
	}

	static final class ModelSemantics extends DefaultSemantics {

		public ModelSemantics(AbstractDialecticalFramework adf) {
			super(adf);
		}
		
		@Override
		public Decomposer createDecomposer() {
			return new RandomDecomposer().asTwoValued();
		}

		@Override
		public CandidateGenerator createCandidateGenerator() {
			return ModelGenerator.withoutPrefix(adf, mapping);
		}

		@Override
		public List<StateProcessor> createStateProcessors() {
			return List.of();
		}
		
		@Override
		public List<InterpretationProcessor> createCandidateProcessor(Supplier<SatSolverState> stateSupplier) {
			return List.of();
		}

		@Override
		public Optional<Verifier> createVerifier(Supplier<SatSolverState> stateSupplier) {
			return Optional.empty();
		}

		@Override
		public List<InterpretationProcessor> createModelProcessors(Supplier<SatSolverState> stateSupplier) {
			return List.of();
		}
		
		@Override
		public Semantics withPrefix(Interpretation prefix) {
			return new PrefixSemantics.ModelSemantics(adf, mapping, prefix);
		}
	
	}
	
	static final class GroundSemantics extends DefaultSemantics {

		public GroundSemantics(AbstractDialecticalFramework adf) {
			super(adf);
		}
		
		@Override
		public CandidateGenerator createCandidateGenerator() {
			return GroundGenerator.withoutPrefix(adf, mapping);
		}

		@Override
		public List<StateProcessor> createStateProcessors() {
			return List.of();
		}
		
		@Override
		public List<InterpretationProcessor> createCandidateProcessor(Supplier<SatSolverState> stateSupplier) {
			return List.of();
		}

		@Override
		public Optional<Verifier> createVerifier(Supplier<SatSolverState> stateSupplier) {
			return Optional.empty();
		}

		@Override
		public List<InterpretationProcessor> createModelProcessors(Supplier<SatSolverState> stateSupplier) {
			return List.of();
		}
		
		@Override
		public Semantics withPrefix(Interpretation prefix) {
			return new PrefixSemantics.GroundSemantics(adf, mapping, prefix);
		}

	}
}
