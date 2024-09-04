package org.tweetyproject.arg.adf.reasoner.sat.processor;

import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

import org.tweetyproject.arg.adf.reasoner.sat.encodings.ConflictFreeInterpretationSatEncoding;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.LargerInterpretationSatEncoding;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.PropositionalMapping;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.RefineLargerSatEncoding;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.RelativeSatEncoding;
import org.tweetyproject.arg.adf.reasoner.sat.encodings.SatEncoding;
import org.tweetyproject.arg.adf.sat.SatSolverState;
import org.tweetyproject.arg.adf.semantics.interpretation.Interpretation;
import org.tweetyproject.arg.adf.syntax.adf.AbstractDialecticalFramework;
import org.tweetyproject.arg.adf.syntax.pl.Literal;

/**
* The {@code ConflictFreeMaximizer} is an abstract class responsible for maximizing conflict-free
* interpretations in an abstract dialectical framework (ADF). It processes interpretations and ensures
* that they are conflict-free by utilizing SAT solver states and encoding mechanisms. The maximizer has
* two main modes: restricted and unrestricted, determined by the concrete subclasses.
*
* @author Mathias Hofer
*/
public abstract class ConflictFreeMaximizer implements InterpretationProcessor {

   /** The propositional mapping of arguments. */
   private final PropositionalMapping mapping;

   /** The SAT encoding to generate larger interpretations. */
   private final RelativeSatEncoding larger;

   /** The SAT encoding to refine larger interpretations. */
   private final RelativeSatEncoding refineLarger;

   /**
	* Constructs a {@code ConflictFreeMaximizer} with the given propositional mapping.
	* Initializes the larger and refineLarger encodings for use in maximizing interpretations.
	*
	* @param mapping the propositional mapping used for encoding, must not be null
	* @throws NullPointerException if {@code mapping} is null
	*/
   private ConflictFreeMaximizer(PropositionalMapping mapping) {
	   this.mapping = Objects.requireNonNull(mapping);
	   this.larger = new LargerInterpretationSatEncoding(mapping);
	   this.refineLarger = new RefineLargerSatEncoding(mapping);
   }

   /**
	* Creates and returns a restricted {@code InterpretationProcessor} that maximizes interpretations
	* based on a given partial interpretation (prefix). This processor restricts conflict-free maximization
	* to interpretations that are consistent with the given partial interpretation.
	*
	* @param stateSupplier a supplier that provides new instances of {@code SatSolverState}
	* @param adf the abstract dialectical framework to be processed
	* @param mapping the propositional mapping used for encoding
	* @param prefix the partial interpretation to restrict the maximization
	* @return an {@code InterpretationProcessor} for restricted conflict-free maximization
	*/
   public static InterpretationProcessor restricted(Supplier<SatSolverState> stateSupplier, AbstractDialecticalFramework adf, PropositionalMapping mapping, Interpretation prefix) {
	   return new RestrictedConflictFreeMaximizer(stateSupplier, adf, mapping, prefix);
   }

   /**
	* Creates and returns an unrestricted {@code InterpretationProcessor} that maximizes interpretations
	* without any restrictions on the partial interpretation.
	*
	* @param stateSupplier a supplier that provides new instances of {@code SatSolverState}
	* @param adf the abstract dialectical framework to be processed
	* @param mapping the propositional mapping used for encoding
	* @return an {@code InterpretationProcessor} for unrestricted conflict-free maximization
	*/
   public static InterpretationProcessor unrestricted(Supplier<SatSolverState> stateSupplier, AbstractDialecticalFramework adf, PropositionalMapping mapping) {
	   return new UnrestrictedConflictFreeMaximizer(stateSupplier, adf, mapping);
   }

   /**
	* Creates a new SAT solver state for processing interpretations.
	* This method is implemented by the concrete subclasses to either restrict or not restrict
	* the interpretations based on the type of conflict-free maximization.
	*
	* @return a new {@code SatSolverState} for SAT solving
	*/
   protected abstract SatSolverState createState();

   /**
	* Processes the given interpretation by maximizing it into a conflict-free interpretation.
	* This method uses a SAT solver to find the largest conflict-free interpretation that satisfies
	* the given propositional mapping.
	*
	* @param interpretation the interpretation to be maximized
	* @return the maximized conflict-free interpretation
	*/
   @Override
   public Interpretation process(Interpretation interpretation) {
	   try (SatSolverState state = createState()) {
		   Interpretation maximal = interpretation;
		   larger.encode(state::add, maximal);
		   Set<Literal> witness = null;
		   while ((witness = state.witness(mapping.getArgumentLiterals())) != null) {
			   maximal = Interpretation.fromWitness(witness, mapping);
			   larger.encode(state::add, maximal);
		   }
		   return maximal;
	   }
   }

   /**
	* Updates the SAT solver state to prevent smaller interpretations from being computed in the future.
	* This method is called after a maximal conflict-free interpretation is found, to ensure that
	* smaller interpretations are not revisited.
	*
	* @param state the SAT solver state to be updated
	* @param maximal the maximal interpretation that has been found
	*/
   @Override
   public void updateState(SatSolverState state, Interpretation maximal) {
	   refineLarger.encode(state::add, maximal);
   }

   @Override
   public void close() {}

   /**
	* The {@code UnrestrictedConflictFreeMaximizer} class extends {@code ConflictFreeMaximizer} to perform
	* unrestricted conflict-free maximization. It allows any interpretation to be maximized without any
	* restrictions based on a partial interpretation.
	*/
   private static final class UnrestrictedConflictFreeMaximizer extends ConflictFreeMaximizer {

	   /** A supplier that provides new instances of {@code SatSolverState}. */
	   private final Supplier<SatSolverState> stateSupplier;

	   /** The SAT encoding for conflict-free interpretations. */
	   private final SatEncoding conflictFree;

	   /**
		* Constructs an {@code UnrestrictedConflictFreeMaximizer} with the given state supplier, ADF, and propositional mapping.
		*
		* @param stateSupplier a supplier that provides new instances of {@code SatSolverState}, must not be null
		* @param adf the abstract dialectical framework to be processed, must not be null
		* @param mapping the propositional mapping used for encoding, must not be null
		* @throws NullPointerException if {@code stateSupplier}, {@code adf}, or {@code mapping} is null
		*/
	   public UnrestrictedConflictFreeMaximizer(Supplier<SatSolverState> stateSupplier, AbstractDialecticalFramework adf, PropositionalMapping mapping) {
		   super(mapping);
		   this.stateSupplier = Objects.requireNonNull(stateSupplier);
		   this.conflictFree = new ConflictFreeInterpretationSatEncoding(adf, mapping);
	   }

	   @Override
	   protected SatSolverState createState() {
		   SatSolverState state = stateSupplier.get();
		   conflictFree.encode(state::add);
		   return state;
	   }

   }

   /**
	* The {@code RestrictedConflictFreeMaximizer} class extends {@code ConflictFreeMaximizer} to perform
	* restricted conflict-free maximization. It restricts the maximization to interpretations that
	* are consistent with a given partial interpretation.
	*/
   private static final class RestrictedConflictFreeMaximizer extends ConflictFreeMaximizer {

	   /** A supplier that provides new instances of {@code SatSolverState}. */
	   private final Supplier<SatSolverState> stateSupplier;

	   /** The SAT encoding for conflict-free interpretations. */
	   private final RelativeSatEncoding conflictFree;

	   /** The partial interpretation that restricts the maximization. */
	   private final Interpretation partial;

	   /**
		* Constructs a {@code RestrictedConflictFreeMaximizer} with the given state supplier, ADF, mapping, and partial interpretation.
		*
		* @param stateSupplier a supplier that provides new instances of {@code SatSolverState}, must not be null
		* @param adf the abstract dialectical framework to be processed, must not be null
		* @param mapping the propositional mapping used for encoding, must not be null
		* @param partial the partial interpretation to restrict the maximization, must not be null
		* @throws NullPointerException if {@code stateSupplier}, {@code adf}, {@code mapping}, or {@code partial} is null
		*/
	   public RestrictedConflictFreeMaximizer(Supplier<SatSolverState> stateSupplier, AbstractDialecticalFramework adf, PropositionalMapping mapping, Interpretation partial) {
		   super(mapping);
		   this.stateSupplier = Objects.requireNonNull(stateSupplier);
		   this.conflictFree = new ConflictFreeInterpretationSatEncoding(adf, mapping);
		   this.partial = Objects.requireNonNull(partial);
	   }

	   @Override
	   protected SatSolverState createState() {
		   SatSolverState state = stateSupplier.get();
		   conflictFree.encode(state::add, partial);
		   return state;
	   }

   }

}
