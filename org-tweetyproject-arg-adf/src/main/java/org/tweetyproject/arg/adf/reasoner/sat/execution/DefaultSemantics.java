package org.tweetyproject.arg.adf.reasoner.sat.execution;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import org.tweetyproject.arg.adf.reasoner.sat.decomposer.Decomposer;
import org.tweetyproject.arg.adf.reasoner.sat.decomposer.MostBipolarParentsDecomposer;
import org.tweetyproject.arg.adf.reasoner.sat.decomposer.RandomDecomposer;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.PropositionalMapping;
import org.tweetyproject.arg.adf.reasoner.sat.generator.CandidateGenerator;
import org.tweetyproject.arg.adf.reasoner.sat.generator.ConflictFreeGenerator;
import org.tweetyproject.arg.adf.reasoner.sat.generator.GroundGenerator;
import org.tweetyproject.arg.adf.reasoner.sat.generator.ModelGenerator;
import org.tweetyproject.arg.adf.reasoner.sat.processor.AdmissibleMaximizer;
import org.tweetyproject.arg.adf.reasoner.sat.processor.ConflictFreeMaximizer;
import org.tweetyproject.arg.adf.reasoner.sat.processor.InterpretationProcessor;
import org.tweetyproject.arg.adf.reasoner.sat.processor.KBipolarStateProcessor;
import org.tweetyproject.arg.adf.reasoner.sat.processor.StateProcessor;
import org.tweetyproject.arg.adf.reasoner.sat.verifier.AdmissibleVerifier;
import org.tweetyproject.arg.adf.reasoner.sat.verifier.CompleteVerifier;
import org.tweetyproject.arg.adf.reasoner.sat.verifier.StableVerifier;
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
		return new MostBipolarParentsDecomposer(adf);
	}
	
	static final class ConflictFreeSemantics extends DefaultSemantics {

		public ConflictFreeSemantics(AbstractDialecticalFramework adf) {
			super(adf);
		}

		@Override
		public CandidateGenerator createCandidateGenerator(Supplier<SatSolverState> stateSupplier) {
			return ConflictFreeGenerator.unrestricted(adf, mapping, stateSupplier);
		}

		@Override
		public List<StateProcessor> createStateProcessors() {
			return List.of();
		}
		
		@Override
		public Optional<InterpretationProcessor> createUnverifiedProcessor(Supplier<SatSolverState> stateSupplier) {
			return Optional.empty();
		}

		@Override
		public Optional<Verifier> createVerifier(Supplier<SatSolverState> stateSupplier) {
			return Optional.empty();
		}

		@Override
		public Optional<InterpretationProcessor> createVerifiedProcessor(Supplier<SatSolverState> stateSupplier) {
			return Optional.empty();
		}

		@Override
		public Semantics restrict(Interpretation prefix) {
			return new RestrictedSemantics.ConflictFreeSemantics(adf, mapping, prefix);
		}

		@Override
		public boolean hasStateProcessors() {
			return false;
		}

		@Override
		public boolean hasUnverifiedProcessor() {
			return false;
		}

		@Override
		public boolean hasVerifier() {
			return false;
		}

		@Override
		public boolean hasVerifiedProcessor() {
			return false;
		}		
	
	}
	
	static final class NaiveSemantics extends DefaultSemantics {
		
		public NaiveSemantics(AbstractDialecticalFramework adf) {
			super(adf);
		}
		
		@Override
		public CandidateGenerator createCandidateGenerator(Supplier<SatSolverState> stateSupplier) {
			return ConflictFreeGenerator.unrestricted(adf, mapping, stateSupplier);
		}

		@Override
		public List<StateProcessor> createStateProcessors() {
			return List.of();
		}
		
		@Override
		public Optional<InterpretationProcessor> createUnverifiedProcessor(Supplier<SatSolverState> stateSupplier) {
			return Optional.empty();
		}

		@Override
		public Optional<Verifier> createVerifier(Supplier<SatSolverState> stateSupplier) {
			return Optional.empty();
		}

		@Override
		public Optional<InterpretationProcessor> createVerifiedProcessor(Supplier<SatSolverState> stateSupplier) {
			return Optional.of(ConflictFreeMaximizer.unrestricted(stateSupplier, adf, mapping));
		}
		
		@Override
		public Semantics restrict(Interpretation prefix) {
			return new RestrictedSemantics.NaiveSemantics(adf, mapping, prefix);
		}

		@Override
		public boolean hasStateProcessors() {
			return false;
		}

		@Override
		public boolean hasUnverifiedProcessor() {
			return false;
		}

		@Override
		public boolean hasVerifier() {
			return false;
		}

		@Override
		public boolean hasVerifiedProcessor() {
			return true;
		}
		
	}
	
	static final class AdmissibleSemantics extends DefaultSemantics {
		
		public AdmissibleSemantics(AbstractDialecticalFramework adf) {
			super(adf);
		}

		@Override
		public CandidateGenerator createCandidateGenerator(Supplier<SatSolverState> stateSupplier) {
			return ConflictFreeGenerator.unrestricted(adf, mapping, stateSupplier);
		}

		@Override
		public List<StateProcessor> createStateProcessors() {
			return List.of(new KBipolarStateProcessor(adf, mapping));
		}
		
		@Override
		public Optional<InterpretationProcessor> createUnverifiedProcessor(Supplier<SatSolverState> stateSupplier) {
			return Optional.empty();
		}

		@Override
		public Optional<Verifier> createVerifier(Supplier<SatSolverState> stateSupplier) {
			return Optional.of(new AdmissibleVerifier(stateSupplier, adf, mapping));
		}

		@Override
		public Optional<InterpretationProcessor> createVerifiedProcessor(Supplier<SatSolverState> stateSupplier) {
			return Optional.empty();
		}
		
		@Override
		public Semantics restrict(Interpretation prefix) {
			return new RestrictedSemantics.AdmissibleSemantics(adf, mapping, prefix);
		}

		@Override
		public boolean hasStateProcessors() {
			return true;
		}

		@Override
		public boolean hasUnverifiedProcessor() {
			return false;
		}

		@Override
		public boolean hasVerifier() {
			return true;
		}

		@Override
		public boolean hasVerifiedProcessor() {
			return false;
		}
	
	}
	
	static final class PreferredSemantics extends DefaultSemantics {
		
		public PreferredSemantics(AbstractDialecticalFramework adf) {
			super(adf);
		}

		@Override
		public CandidateGenerator createCandidateGenerator(Supplier<SatSolverState> stateSupplier) {
			return ConflictFreeGenerator.unrestricted(adf, mapping, stateSupplier);
		}

		@Override
		public List<StateProcessor> createStateProcessors() {
			return List.of(new KBipolarStateProcessor(adf, mapping));
		}
		
		@Override
		public Optional<InterpretationProcessor> createUnverifiedProcessor(Supplier<SatSolverState> stateSupplier) {
			return Optional.empty();
		}

		@Override
		public Optional<Verifier> createVerifier(Supplier<SatSolverState> stateSupplier) {
			return Optional.of(new AdmissibleVerifier(stateSupplier, adf, mapping));
		}

		@Override
		public Optional<InterpretationProcessor> createVerifiedProcessor(Supplier<SatSolverState> stateSupplier) {
			return Optional.of(AdmissibleMaximizer.unrestricted(stateSupplier, adf, mapping));
		}
		
		@Override
		public Semantics restrict(Interpretation prefix) {
			return new RestrictedSemantics.PreferredSemantics(adf, mapping, prefix);
		}

		@Override
		public boolean hasStateProcessors() {
			return true;
		}

		@Override
		public boolean hasUnverifiedProcessor() {
			return false;
		}

		@Override
		public boolean hasVerifier() {
			return true;
		}

		@Override
		public boolean hasVerifiedProcessor() {
			return true;
		}
		
	}
	
	static final class StableSemantics extends DefaultSemantics {
		
		public StableSemantics(AbstractDialecticalFramework adf) {
			super(adf);
		}
		
		@Override
		public Decomposer createDecomposer() {
			return new RandomDecomposer(adf).asTwoValued();
		}

		@Override
		public CandidateGenerator createCandidateGenerator(Supplier<SatSolverState> stateSupplier) {
			return ModelGenerator.unrestricted(adf, mapping, stateSupplier);
		}

		@Override
		public List<StateProcessor> createStateProcessors() {
			return List.of();
		}
		
		@Override
		public Optional<InterpretationProcessor> createUnverifiedProcessor(Supplier<SatSolverState> stateSupplier) {
			return Optional.empty();
		}

		@Override
		public Optional<Verifier> createVerifier(Supplier<SatSolverState> stateSupplier) {
			return Optional.of(new StableVerifier(stateSupplier, adf, mapping));
		}

		@Override
		public Optional<InterpretationProcessor> createVerifiedProcessor(Supplier<SatSolverState> stateSupplier) {
			return Optional.empty();
		}
		
		@Override
		public Semantics restrict(Interpretation prefix) {
			return new RestrictedSemantics.StableSemantics(adf, mapping, prefix);
		}

		@Override
		public boolean hasStateProcessors() {
			return false;
		}

		@Override
		public boolean hasUnverifiedProcessor() {
			return false;
		}

		@Override
		public boolean hasVerifier() {
			return true;
		}

		@Override
		public boolean hasVerifiedProcessor() {
			return false;
		}
		
	}
	
	static final class CompleteSemantics extends DefaultSemantics {
		
		public CompleteSemantics(AbstractDialecticalFramework adf) {
			super(adf);
		}

		@Override
		public CandidateGenerator createCandidateGenerator(Supplier<SatSolverState> stateSupplier) {
			return ConflictFreeGenerator.unrestricted(adf, mapping, stateSupplier);
		}

		@Override
		public List<StateProcessor> createStateProcessors() {
			return List.of(new KBipolarStateProcessor(adf, mapping));
		}
		
		@Override
		public Optional<InterpretationProcessor> createUnverifiedProcessor(Supplier<SatSolverState> stateSupplier) {
			return Optional.empty();
		}

		@Override
		public Optional<Verifier> createVerifier(Supplier<SatSolverState> stateSupplier) {
			return Optional.of(new CompleteVerifier(stateSupplier, adf, mapping));
		}

		@Override
		public Optional<InterpretationProcessor> createVerifiedProcessor(Supplier<SatSolverState> stateSupplier) {
			return Optional.empty();
		}
		
		@Override
		public Semantics restrict(Interpretation prefix) {
			return new RestrictedSemantics.CompleteSemantics(adf, mapping, prefix);
		}

		@Override
		public boolean hasStateProcessors() {
			return true;
		}

		@Override
		public boolean hasUnverifiedProcessor() {
			return false;
		}

		@Override
		public boolean hasVerifier() {
			return true;
		}

		@Override
		public boolean hasVerifiedProcessor() {
			return false;
		}
		
	}

	static final class ModelSemantics extends DefaultSemantics {

		public ModelSemantics(AbstractDialecticalFramework adf) {
			super(adf);
		}
		
		@Override
		public Decomposer createDecomposer() {
			return new RandomDecomposer(adf).asTwoValued();
		}

		@Override
		public CandidateGenerator createCandidateGenerator(Supplier<SatSolverState> stateSupplier) {
			return ModelGenerator.unrestricted(adf, mapping, stateSupplier);
		}

		@Override
		public List<StateProcessor> createStateProcessors() {
			return List.of();
		}
		
		@Override
		public Optional<InterpretationProcessor> createUnverifiedProcessor(Supplier<SatSolverState> stateSupplier) {
			return Optional.empty();
		}

		@Override
		public Optional<Verifier> createVerifier(Supplier<SatSolverState> stateSupplier) {
			return Optional.empty();
		}

		@Override
		public Optional<InterpretationProcessor> createVerifiedProcessor(Supplier<SatSolverState> stateSupplier) {
			return Optional.empty();
		}
		
		@Override
		public Semantics restrict(Interpretation prefix) {
			return new RestrictedSemantics.ModelSemantics(adf, mapping, prefix);
		}

		@Override
		public boolean hasStateProcessors() {
			return false;
		}

		@Override
		public boolean hasUnverifiedProcessor() {
			return false;
		}

		@Override
		public boolean hasVerifier() {
			return false;
		}

		@Override
		public boolean hasVerifiedProcessor() {
			return false;
		}
	
	}
	
	static final class GroundSemantics extends DefaultSemantics {

		public GroundSemantics(AbstractDialecticalFramework adf) {
			super(adf);
		}
		
		@Override
		public CandidateGenerator createCandidateGenerator(Supplier<SatSolverState> stateSupplier) {
			return GroundGenerator.unrestricted(adf, mapping, stateSupplier);
		}

		@Override
		public List<StateProcessor> createStateProcessors() {
			return List.of();
		}
		
		@Override
		public Optional<InterpretationProcessor> createUnverifiedProcessor(Supplier<SatSolverState> stateSupplier) {
			return Optional.empty();
		}

		@Override
		public Optional<Verifier> createVerifier(Supplier<SatSolverState> stateSupplier) {
			return Optional.empty();
		}

		@Override
		public Optional<InterpretationProcessor> createVerifiedProcessor(Supplier<SatSolverState> stateSupplier) {
			return Optional.empty();
		}
		
		@Override
		public Semantics restrict(Interpretation prefix) {
			return new RestrictedSemantics.GroundSemantics(adf, mapping, prefix);
		}

		@Override
		public boolean hasStateProcessors() {
			return false;
		}

		@Override
		public boolean hasUnverifiedProcessor() {
			return false;
		}

		@Override
		public boolean hasVerifier() {
			return false;
		}

		@Override
		public boolean hasVerifiedProcessor() {
			return false;
		}

	}
}
