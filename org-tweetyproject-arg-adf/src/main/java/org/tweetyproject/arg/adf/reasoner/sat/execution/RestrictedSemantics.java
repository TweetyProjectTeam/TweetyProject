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
import org.tweetyproject.arg.adf.reasoner.sat.verifier.NaiveVerifier;
import org.tweetyproject.arg.adf.reasoner.sat.verifier.PreferredVerifier;
import org.tweetyproject.arg.adf.reasoner.sat.verifier.Verifier;
import org.tweetyproject.arg.adf.sat.SatSolverState;
import org.tweetyproject.arg.adf.sat.solver.NativeMinisatSolver;
import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.semantics.link.SatLinkStrategy;
import org.tweetyproject.arg.adf.syntax.Argument;
import org.tweetyproject.arg.adf.syntax.acc.AcceptanceCondition;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework.Builder;
import org.tweetyproject.arg.adf.transform.FixPartialTransformer;
import org.tweetyproject.arg.adf.transform.Transformer;

/**
 * 
 * Only computes interpretations that extend the provided partial interpretation.
 * 
 * @author Mathias
 * 
 */
abstract class RestrictedSemantics implements Semantics {

	final AbstractDialecticalFramework adf;
	
	final AbstractDialecticalFramework reduct;
	
	final PropositionalMapping mapping;
	
	final Interpretation partial;

	RestrictedSemantics(AbstractDialecticalFramework adf, PropositionalMapping mapping, Interpretation partial) {
		this.adf = Objects.requireNonNull(adf);
		this.reduct = reduct(adf, partial);
		this.mapping = Objects.requireNonNull(mapping);
		this.partial = Objects.requireNonNull(partial);
	}
	
	@Override
	public Decomposer createDecomposer() {
		return new MostBipolarParentsDecomposer();
	}
	
	private static AbstractDialecticalFramework reduct(AbstractDialecticalFramework adf, Interpretation interpretation) {
		Transformer<AcceptanceCondition> fixPartials = new FixPartialTransformer(interpretation);		
		Builder builder = AbstractDialecticalFramework.builder().eager(new SatLinkStrategy(new NativeMinisatSolver())); // TODO fix
		for (Argument arg : adf.getArguments()) {
			builder.add(arg, fixPartials.transform(adf.getAcceptanceCondition(arg)));
		}
		return builder.build();
	}
	
	static final class ConflictFreeSemantics extends RestrictedSemantics {

		public ConflictFreeSemantics(AbstractDialecticalFramework adf, PropositionalMapping mapping, Interpretation partial) {
			super(adf, mapping, partial);
		}

		@Override
		public CandidateGenerator createCandidateGenerator(Supplier<SatSolverState> stateSupplier) {
			return ConflictFreeGenerator.restricted(reduct, mapping, partial, stateSupplier);
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
		public Semantics restrict(Interpretation partial) {
			return new ConflictFreeSemantics(adf, mapping, Interpretation.union(this.partial, partial));			
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
	
	static final class NaiveSemantics extends RestrictedSemantics {
		
		public NaiveSemantics(AbstractDialecticalFramework adf, PropositionalMapping mapping, Interpretation partial) {
			super(adf, mapping, partial);
		}
		
		@Override
		public CandidateGenerator createCandidateGenerator(Supplier<SatSolverState> stateSupplier) {
			return ConflictFreeGenerator.restricted(reduct, mapping, partial, stateSupplier);
		}

		@Override
		public List<StateProcessor> createStateProcessors() {
			return List.of();
		}
		
		@Override
		public Optional<InterpretationProcessor> createUnverifiedProcessor(Supplier<SatSolverState> stateSupplier) {
			return Optional.of(ConflictFreeMaximizer.restricted(stateSupplier, reduct, mapping, partial));
		}

		@Override
		public Optional<Verifier> createVerifier(Supplier<SatSolverState> stateSupplier) {
			return Optional.of(new NaiveVerifier(stateSupplier, adf, mapping));
		}

		@Override
		public Optional<InterpretationProcessor> createVerifiedProcessor(Supplier<SatSolverState> stateSupplier) {
			return Optional.empty();
		}
		
		@Override
		public Semantics restrict(Interpretation partial) {
			return new NaiveSemantics(adf, mapping, Interpretation.union(this.partial, partial));			
		}

		@Override
		public boolean hasStateProcessors() {
			return false;
		}

		@Override
		public boolean hasUnverifiedProcessor() {
			return true;
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
	
	static final class AdmissibleSemantics extends RestrictedSemantics {

		public AdmissibleSemantics(AbstractDialecticalFramework adf, PropositionalMapping mapping, Interpretation partial) {
			super(adf, mapping, partial);
		}

		@Override
		public CandidateGenerator createCandidateGenerator(Supplier<SatSolverState> stateSupplier) {
			return ConflictFreeGenerator.restricted(reduct, mapping, partial, stateSupplier);
		}

		@Override
		public List<StateProcessor> createStateProcessors() {
			return List.of(new KBipolarStateProcessor(reduct, mapping));
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
		public Semantics restrict(Interpretation partial) {
			return new AdmissibleSemantics(adf, mapping, Interpretation.union(this.partial, partial));			
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
	
	static final class PreferredSemantics extends RestrictedSemantics {
		
		public PreferredSemantics(AbstractDialecticalFramework adf, PropositionalMapping mapping, Interpretation partial) {
			super(adf, mapping, partial);
		}

		@Override
		public CandidateGenerator createCandidateGenerator(Supplier<SatSolverState> stateSupplier) {
			return ConflictFreeGenerator.restricted(reduct, mapping, partial, stateSupplier);
		}

		@Override
		public List<StateProcessor> createStateProcessors() {
			return List.of(new KBipolarStateProcessor(reduct, mapping));
		}
		
		@Override
		public Optional<InterpretationProcessor> createUnverifiedProcessor(Supplier<SatSolverState> stateSupplier) {
			return Optional.of(AdmissibleMaximizer.restricted(stateSupplier, reduct, mapping, partial));
		}

		@Override
		public Optional<Verifier> createVerifier(Supplier<SatSolverState> stateSupplier) {
			return Optional.of(new PreferredVerifier(stateSupplier, adf, mapping));
		}

		@Override
		public Optional<InterpretationProcessor> createVerifiedProcessor(Supplier<SatSolverState> stateSupplier) {
			return Optional.empty();
		}
		
		@Override
		public Semantics restrict(Interpretation partial) {
			return new PreferredSemantics(adf, mapping, Interpretation.union(this.partial, partial));
		}

		@Override
		public boolean hasStateProcessors() {
			return true;
		}

		@Override
		public boolean hasUnverifiedProcessor() {
			return true;
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
	
	static final class CompleteSemantics extends RestrictedSemantics {
		
		public CompleteSemantics(AbstractDialecticalFramework adf, PropositionalMapping mapping, Interpretation partial) {
			super(adf, mapping, partial);
		}

		@Override
		public CandidateGenerator createCandidateGenerator(Supplier<SatSolverState> stateSupplier) {
			return ConflictFreeGenerator.restricted(reduct, mapping, partial, stateSupplier);
		}

		@Override
		public List<StateProcessor> createStateProcessors() {
			return List.of(new KBipolarStateProcessor(reduct, mapping));
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
		public Semantics restrict(Interpretation partial) {
			return new CompleteSemantics(adf, mapping, Interpretation.union(this.partial, partial));
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
	
	static final class ModelSemantics extends RestrictedSemantics {
		
		public ModelSemantics(AbstractDialecticalFramework adf, PropositionalMapping mapping, Interpretation partial) {
			super(adf, mapping, partial);
		}
		
		@Override
		public Decomposer createDecomposer() {
			return new RandomDecomposer().asTwoValued();
		}

		@Override
		public CandidateGenerator createCandidateGenerator(Supplier<SatSolverState> stateSupplier) {
			return ModelGenerator.restricted(reduct, mapping, partial, stateSupplier);
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
		public Semantics restrict(Interpretation partial) {
			return new ModelSemantics(adf, mapping, Interpretation.union(this.partial, partial));
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
	
	static final class StableSemantics extends RestrictedSemantics {
		
		public StableSemantics(AbstractDialecticalFramework adf, PropositionalMapping mapping, Interpretation partial) {
			super(adf, mapping, partial);
		}
		
		@Override
		public Decomposer createDecomposer() {
			return new RandomDecomposer().asTwoValued();
		}

		@Override
		public CandidateGenerator createCandidateGenerator(Supplier<SatSolverState> stateSupplier) {
			return ModelGenerator.restricted(reduct, mapping, partial, stateSupplier);
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
		public Semantics restrict(Interpretation partial) {
			return new StableSemantics(adf, mapping, Interpretation.union(this.partial, partial));
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
	
	static final class GroundSemantics extends RestrictedSemantics {

		public GroundSemantics(AbstractDialecticalFramework adf, PropositionalMapping mapping, Interpretation partial) {
			super(adf, mapping, partial);
		}
		
		@Override
		public CandidateGenerator createCandidateGenerator(Supplier<SatSolverState> stateSupplier) {
			return GroundGenerator.restricted(adf, mapping, partial, stateSupplier);
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
		public Semantics restrict(Interpretation partial) {
			return new GroundSemantics(adf, mapping, Interpretation.union(this.partial, partial));
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
