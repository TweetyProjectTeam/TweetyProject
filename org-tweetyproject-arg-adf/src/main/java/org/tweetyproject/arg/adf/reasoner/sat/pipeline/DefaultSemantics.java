package org.tweetyproject.arg.adf.reasoner.sat.pipeline;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Supplier;

import org.tweetyproject.arg.adf.reasoner.sat.encodings.PropositionalMapping;
import org.tweetyproject.arg.adf.reasoner.sat.generator.CandidateGenerator;
import org.tweetyproject.arg.adf.reasoner.sat.generator.ConflictFreeGenerator;
import org.tweetyproject.arg.adf.reasoner.sat.generator.GroundGenerator;
import org.tweetyproject.arg.adf.reasoner.sat.generator.ModelGenerator;
import org.tweetyproject.arg.adf.reasoner.sat.processor.InterpretationProcessor;
import org.tweetyproject.arg.adf.reasoner.sat.processor.KBipolarStateProcessor;
import org.tweetyproject.arg.adf.reasoner.sat.processor.StateProcessor;
import org.tweetyproject.arg.adf.reasoner.sat.processor.UnverifiedMaximizer;
import org.tweetyproject.arg.adf.reasoner.sat.processor.VerifiedMaximizer;
import org.tweetyproject.arg.adf.reasoner.sat.verifier.AdmissibleVerifier;
import org.tweetyproject.arg.adf.reasoner.sat.verifier.CompleteVerifier;
import org.tweetyproject.arg.adf.reasoner.sat.verifier.GrounderStableVerifier;
import org.tweetyproject.arg.adf.reasoner.sat.verifier.Verifier;
import org.tweetyproject.arg.adf.sat.SatSolverState;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;

abstract class DefaultSemantics implements Semantics {
	
	private final AbstractDialecticalFramework adf;
	
	private final PropositionalMapping mapping;

	private DefaultSemantics(AbstractDialecticalFramework adf) {
		this(adf, new PropositionalMapping(adf));
	}
	
	private DefaultSemantics(AbstractDialecticalFramework adf, PropositionalMapping mapping) {
		this.adf = Objects.requireNonNull(adf);
		this.mapping = Objects.requireNonNull(mapping);
	}
	
	abstract CandidateGenerator createCandidateGenerator(AbstractDialecticalFramework adf, PropositionalMapping mapping);

	abstract List<StateProcessor> createStateProcessors(AbstractDialecticalFramework adf, PropositionalMapping mapping);

	abstract Optional<Verifier> createVerifier(Supplier<SatSolverState> stateSupplier, AbstractDialecticalFramework adf, PropositionalMapping mapping);

	abstract List<InterpretationProcessor> createModelProcessors(Supplier<SatSolverState> stateSupplier, AbstractDialecticalFramework adf, PropositionalMapping mapping);
	
	abstract Semantics forReduct(AbstractDialecticalFramework adf, PropositionalMapping mapping);
	
	@Override
	public CandidateGenerator createCandidateGenerator() {
		return createCandidateGenerator(adf, mapping);
	}
	
	@Override
	public List<StateProcessor> createStateProcessors() {
		return createStateProcessors(adf, mapping);
	}
	
	@Override
	public Optional<Verifier> createVerifier(Supplier<SatSolverState> stateSupplier) {
		return createVerifier(stateSupplier, adf, mapping);
	}
	
	@Override
	public List<InterpretationProcessor> createModelProcessors(Supplier<SatSolverState> stateSupplier) {
		return createModelProcessors(stateSupplier, adf, mapping);
	}
	
	@Override
	public PropositionalMapping getPropositionalMapping() {
		return mapping;
	}
	
	@Override
	public Semantics forReduct(AbstractDialecticalFramework adf) {
		return forReduct(adf, mapping);
	}
		
	static final class ConflictFreeSemantics extends DefaultSemantics {

		public ConflictFreeSemantics(AbstractDialecticalFramework adf) {
			super(adf);
		}
		
		private ConflictFreeSemantics(AbstractDialecticalFramework adf, PropositionalMapping mapping) {
			super(adf, mapping);
		}

		@Override
		CandidateGenerator createCandidateGenerator(AbstractDialecticalFramework adf, PropositionalMapping mapping) {
			return new ConflictFreeGenerator(adf, mapping);
		}

		@Override
		List<StateProcessor> createStateProcessors(AbstractDialecticalFramework adf, PropositionalMapping mapping) {
			return List.of();
		}

		@Override
		Optional<Verifier> createVerifier(Supplier<SatSolverState> stateSupplier, AbstractDialecticalFramework adf, PropositionalMapping mapping) {
			return Optional.empty();
		}

		@Override
		List<InterpretationProcessor> createModelProcessors(Supplier<SatSolverState> stateSupplier, AbstractDialecticalFramework adf, PropositionalMapping mapping) {
			return List.of();
		}
		
		@Override
		Semantics forReduct(AbstractDialecticalFramework adf, PropositionalMapping mapping) {
			return new ConflictFreeSemantics(adf, mapping);
		}
	
	}
	
	static final class NaiveSemantics extends DefaultSemantics {
		
		public NaiveSemantics(AbstractDialecticalFramework adf) {
			super(adf);
		}
		
		private NaiveSemantics(AbstractDialecticalFramework adf, PropositionalMapping mapping) {
			super(adf, mapping);
		}

		@Override
		CandidateGenerator createCandidateGenerator(AbstractDialecticalFramework adf, PropositionalMapping mapping) {
			return new ConflictFreeGenerator(adf, mapping);
		}

		@Override
		List<StateProcessor> createStateProcessors(AbstractDialecticalFramework adf, PropositionalMapping mapping) {
			return List.of();
		}

		@Override
		Optional<Verifier> createVerifier(Supplier<SatSolverState> stateSupplier, AbstractDialecticalFramework adf, PropositionalMapping mapping) {
			return Optional.empty();
		}

		@Override
		List<InterpretationProcessor> createModelProcessors(Supplier<SatSolverState> stateSupplier, AbstractDialecticalFramework adf, PropositionalMapping mapping) {
			return List.of(new UnverifiedMaximizer(stateSupplier, mapping, adf));
		}
		
		@Override
		Semantics forReduct(AbstractDialecticalFramework adf, PropositionalMapping mapping) {
			return new NaiveSemantics(adf, mapping);
		}
	}
	
	static final class AdmissibleSemantics extends DefaultSemantics {
		
		public AdmissibleSemantics(AbstractDialecticalFramework adf) {
			super(adf);
		}
		
		private AdmissibleSemantics(AbstractDialecticalFramework adf, PropositionalMapping mapping) {
			super(adf, mapping);
		}

		@Override
		CandidateGenerator createCandidateGenerator(AbstractDialecticalFramework adf, PropositionalMapping mapping) {
			return new ConflictFreeGenerator(adf, mapping);
		}

		@Override
		List<StateProcessor> createStateProcessors(AbstractDialecticalFramework adf, PropositionalMapping mapping) {
			return List.of(new KBipolarStateProcessor(adf, mapping));
		}

		@Override
		Optional<Verifier> createVerifier(Supplier<SatSolverState> stateSupplier, AbstractDialecticalFramework adf, PropositionalMapping mapping) {
			return Optional.of(new AdmissibleVerifier(stateSupplier, adf, mapping));
		}

		@Override
		List<InterpretationProcessor> createModelProcessors(Supplier<SatSolverState> stateSupplier, AbstractDialecticalFramework adf, PropositionalMapping mapping) {
			return List.of();
		}
		
		@Override
		Semantics forReduct(AbstractDialecticalFramework adf, PropositionalMapping mapping) {
			return new AdmissibleSemantics(adf, mapping);
		}
		
	}
	
	static final class PreferredSemantics extends DefaultSemantics {
		
		public PreferredSemantics(AbstractDialecticalFramework adf) {
			super(adf);
		}
		
		private PreferredSemantics(AbstractDialecticalFramework adf, PropositionalMapping mapping) {
			super(adf, mapping);
		}

		@Override
		CandidateGenerator createCandidateGenerator(AbstractDialecticalFramework adf, PropositionalMapping mapping) {
			return new ConflictFreeGenerator(adf, mapping);
		}

		@Override
		List<StateProcessor> createStateProcessors(AbstractDialecticalFramework adf, PropositionalMapping mapping) {
			return List.of(new KBipolarStateProcessor(adf, mapping));
		}

		@Override
		Optional<Verifier> createVerifier(Supplier<SatSolverState> stateSupplier, AbstractDialecticalFramework adf, PropositionalMapping mapping) {
			return Optional.of(new AdmissibleVerifier(stateSupplier, adf, mapping));
		}

		@Override
		List<InterpretationProcessor> createModelProcessors(Supplier<SatSolverState> stateSupplier, AbstractDialecticalFramework adf, PropositionalMapping mapping) {
			return List.of(new VerifiedMaximizer(stateSupplier, mapping, adf, new AdmissibleVerifier(stateSupplier, adf, mapping)));
		}
		
		@Override
		Semantics forReduct(AbstractDialecticalFramework adf, PropositionalMapping mapping) {
			return new PreferredSemantics(adf, mapping);
		}
	}
	
	static final class StableSemantics extends DefaultSemantics {
		
		public StableSemantics(AbstractDialecticalFramework adf) {
			super(adf);
		}
		
		private StableSemantics(AbstractDialecticalFramework adf, PropositionalMapping mapping) {
			super(adf, mapping);
		}

		@Override
		CandidateGenerator createCandidateGenerator(AbstractDialecticalFramework adf, PropositionalMapping mapping) {
			return new ModelGenerator(adf, mapping);
		}

		@Override
		List<StateProcessor> createStateProcessors(AbstractDialecticalFramework adf, PropositionalMapping mapping) {
			return List.of();
		}

		@Override
		Optional<Verifier> createVerifier(Supplier<SatSolverState> stateSupplier, AbstractDialecticalFramework adf, PropositionalMapping mapping) {
			return Optional.of(new GrounderStableVerifier(stateSupplier, adf, mapping));
		}

		@Override
		List<InterpretationProcessor> createModelProcessors(Supplier<SatSolverState> stateSupplier, AbstractDialecticalFramework adf, PropositionalMapping mapping) {
			return List.of();
		}
		
		@Override
		Semantics forReduct(AbstractDialecticalFramework adf, PropositionalMapping mapping) {
			return new StableSemantics(adf, mapping);
		}
		
	}
	
	static final class CompleteSemantics extends DefaultSemantics {
		
		public CompleteSemantics(AbstractDialecticalFramework adf) {
			super(adf);
		}
		
		private CompleteSemantics(AbstractDialecticalFramework adf, PropositionalMapping mapping) {
			super(adf, mapping);
		}

		@Override
		CandidateGenerator createCandidateGenerator(AbstractDialecticalFramework adf, PropositionalMapping mapping) {
			return new ConflictFreeGenerator(adf, mapping);
		}

		@Override
		List<StateProcessor> createStateProcessors(AbstractDialecticalFramework adf, PropositionalMapping mapping) {
			return List.of(new KBipolarStateProcessor(adf, mapping));
		}

		@Override
		Optional<Verifier> createVerifier(Supplier<SatSolverState> stateSupplier, AbstractDialecticalFramework adf, PropositionalMapping mapping) {
			return Optional.of(new CompleteVerifier(stateSupplier, adf, mapping));
		}

		@Override
		List<InterpretationProcessor> createModelProcessors(Supplier<SatSolverState> stateSupplier, AbstractDialecticalFramework adf, PropositionalMapping mapping) {
			return List.of();
		}
		
		@Override
		Semantics forReduct(AbstractDialecticalFramework adf, PropositionalMapping mapping) {
			return new CompleteSemantics(adf, mapping);
		}
		
	}

	static final class ModelSemantics extends DefaultSemantics {

		public ModelSemantics(AbstractDialecticalFramework adf) {
			super(adf);
		}
		
		private ModelSemantics(AbstractDialecticalFramework adf, PropositionalMapping mapping) {
			super(adf, mapping);
		}

		@Override
		CandidateGenerator createCandidateGenerator(AbstractDialecticalFramework adf, PropositionalMapping mapping) {
			return new ModelGenerator(adf, mapping);
		}

		@Override
		List<StateProcessor> createStateProcessors(AbstractDialecticalFramework adf, PropositionalMapping mapping) {
			return List.of();
		}

		@Override
		Optional<Verifier> createVerifier(Supplier<SatSolverState> stateSupplier, AbstractDialecticalFramework adf, PropositionalMapping mapping) {
			return Optional.empty();
		}

		@Override
		List<InterpretationProcessor> createModelProcessors(Supplier<SatSolverState> stateSupplier, AbstractDialecticalFramework adf, PropositionalMapping mapping) {
			return List.of();
		}
		
		@Override
		Semantics forReduct(AbstractDialecticalFramework adf, PropositionalMapping mapping) {
			return new ModelSemantics(adf, mapping);
		}
	
	}
	
	static final class GroundSemantics extends DefaultSemantics {

		public GroundSemantics(AbstractDialecticalFramework adf) {
			super(adf);
		}
		
		private GroundSemantics(AbstractDialecticalFramework adf, PropositionalMapping mapping) {
			super(adf, mapping);
		}

		@Override
		CandidateGenerator createCandidateGenerator(AbstractDialecticalFramework adf, PropositionalMapping mapping) {
			return new GroundGenerator(adf, mapping);
		}

		@Override
		List<StateProcessor> createStateProcessors(AbstractDialecticalFramework adf, PropositionalMapping mapping) {
			return List.of();
		}

		@Override
		Optional<Verifier> createVerifier(Supplier<SatSolverState> stateSupplier, AbstractDialecticalFramework adf, PropositionalMapping mapping) {
			return Optional.empty();
		}

		@Override
		List<InterpretationProcessor> createModelProcessors(Supplier<SatSolverState> stateSupplier, AbstractDialecticalFramework adf, PropositionalMapping mapping) {
			return List.of();
		}
		
		@Override
		Semantics forReduct(AbstractDialecticalFramework adf, PropositionalMapping mapping) {
			return new GroundSemantics(adf, mapping);
		}
	
	}
}
