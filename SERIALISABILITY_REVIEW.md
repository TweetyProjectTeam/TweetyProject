# Review notes: serialisability rework, batch 1

Branch `serialisability-rework` (from `main`). Batch 1 (core module) is written
and tested but not committed. These are open review remarks to resolve in a
redesign once the review is complete.

## Remarks

1. `SerialisationState` should not hold the classified initial sets
   (`InitialSets` cache, `getInitialSets()`).
2. `SerialisationState` should not hold the `SerialisationScheme`.
3. `SerialisationState.isEmpty()` is questionable.
   - Context: it exists only for `TerminationFunction.emptyFramework()` (β_st).
     Discussed options: keep `scheme.isEmpty`, bound `T extends BeliefSet<?,?>`
     and use `theory.isEmpty()`, a new framework interface, or a
     formalism-specific β_st. Not decided.

4. `SerialisationSequence` should not need the scheme (currently used only for
   `empty(theory)` and `combine` in `extend`).
   - Proposed: steps plus a supplied result; the graph passes the state
     solutions (root solution for the empty sequence, target solution in
     `extend`). Consistency of result and steps is then guaranteed by the
     graph, not the sequence. Alternatives: steps only, or a fold helper
     outside the sequence.
   - Immutability only preserves the result given at construction. The
     guarantee holds if construction is package-private (graph only);
     `SequenceExplanationReasoner` builds sequences by hand and would need a
     public route until F6. Step elements remain mutable objects (C1) either way.
   - Defensive copies of steps: no generic copy for `S` (would need
     `scheme.copy`, reintroducing the scheme), and copies on input alone do not
     help since `get`/`getSteps`/`getResult` hand the objects out again.
     Proposed: keep the C1 convention, no copies. Undecided.

5. Naming: an αβ-system induces a semantics E^{α,β} [BT26 Def. 3.6], so its
   terminal solutions are models. Rename `SerialisationSystem.getResults/getResult`
   to `getModels/getModel` and `SerialisationGraph.getResults` to `getModels`.
   Keep `SerialisationSequence.getResult()` (induced solution Ŝ, not
   necessarily a model). No `ModelProvider` implementation in the core.

6. `InitialSets` should not store `all`: it is derived data. Compute the union
   in `getAll()` and check the three groups in `isEmpty()`. Modelling the
   concept comes first, efficiency second.
   - Keep the unmodifiable wrappers (and input copies) for the three stored
     groups: the partition is shared by α, β and analyses.

7. `getResult` (first model) is not conceptually important; one typically wants
   all models. Proposed: drop it from `SerialisationSystem`; the reasoner's
   `getModel` returns some element of `getModels` (or null). `getModels(T)`
   derives from `getGraph(T)`, so `explore` only builds the graph and loses
   its three-mode parameters (`graph`, `results`, `first`). Cost: steps are
   stored even when only models are needed (acceptable, efficiency secondary).

8. Terminology (proposal, to decide), based on BT26 Sec. 5 and Tweety usage:
   - "solution" → "model" (`S`, `getSolution` → `getModel`): initial models are
     τ-models, every state's accumulated value is a τ-model (C5), Ŝ is a
     τ-model, terminal states carry the σ-models returned by `getModels`.
     Initial models are the building blocks of the models of the semantics.
     Javadoc must distinguish τ-models (states) from σ-models (terminal):
     `state.getModel()` need not be in `getModels(F)`.
   - "initial set" → "initial model" (`InitialSets`, `getInitialSets`,
     `getInitialSet`, factory names): BT26 Def. 5.1; Tweety already calls
     extensions models. Javadoc: "initial sets for AFs [T22]".
   - "theory" → "framework" (`getTheory`, parameters): Javadoc already says
     framework.
   - `combine` → `union`: BT26 calls ⊔ the union operator.
   - Keep: unattacked/unchallenged/challenged, `isConflicting` (fix Javadoc:
     symmetric, BT26 Def. 5.2; symmetric for AF initial sets too), reduct,
     `partition`, selection/termination function, system/state/step/sequence/
     graph, `SerialisationScheme`, structural factory names, `empty`, `T`/`S`.

9. `SerialisationGraph` storage holds derived data and model-keyed indexes.
   - Drop the explicit `steps` list (= union of outgoing lists).
   - Keep `incoming` (efficiency worth it): without it `getParents` is O(E) and
     per-node users (`getAncestors`, connected components, ancestors in
     `getSequences(model)`) become O(V·E), e.g. ~2·10^9 for 14 isolated
     arguments under α = all.
   - Model-keyed maps exist only to avoid hashing frameworks and for
     `getState(model)`. Framework hashing is O(|A|+|R|) of the residual
     framework, negligible next to computing initial sets (2^|A| subsets per
     state). Proposed: key by state directly with full (F, E) equality
     (option A); `getState(model)` scans. Alternative B: state equality over
     the model only (sound within a graph by C4, surprising outside).
   - Merging in the exploration stays keyed by model (definition, not
     optimisation).

10. Parallel steps (same endpoints, different initial models) cannot occur for
    AF extensions/labelings or ADFs: initial models of the reduct only decide
    arguments left undecided by the current model, so E ⊔ S₁ = E ⊔ S₂ implies
    S₁ = S₂. Agreed: add this disjointness to the scheme contract (C3); the
    graph is then simple, `getEdge(a, b)` exact, the parallel-step caveats
    and the hand-built `parallelSteps` test go (replace with a contract
    check). Keep storing the step label.
    - Engine check (agreed): adding a step between two states that are
      already connected throws `IllegalStateException` (third engine check,
      next to "α selects only initial models" and "the model changes").

11. `SequenceIterator`: keep lazy enumeration (`iterateSequences`), but move
    the iterator out of `SerialisationGraph` into its own package-private
    class using only the graph's public API. `getSequences()` and
    `getSequences(model)` collect from it (ancestor pruning for the target
    variant).
    - Cycles are impossible under C3 (strict growth), but the engine only
      checks "model changes"; a broken scheme can give E → E′ → E, making
      enumeration non-terminating. Agreed direction: check acyclicity once
      when the graph is built (O(V+E), or `DefaultGraph.containsCycle`) and
      throw `IllegalStateException`; the iterator then drops its on-path set.

## Proposed revision (covers remarks 1–11 and the open questions)

### Core API after the revision

```java
interface SerialisationScheme<T, S> {
    S empty(T framework);
    Collection<S> getInitialModels(T framework);
    boolean isUnattacked(T framework, S initialModel);
    boolean isConflicting(T framework, S initialModel, S other);   // symmetric
    T getReduct(T framework, S model);
    S union(S model, S other);
    default InitialModels<S> partition(T framework) { ... }
}                                                       // isEmpty removed

final class InitialModels<S>        // ua, uc, c stored; getAll()/isEmpty() derived
interface SelectionFunction<S>      { Collection<S> select(InitialModels<S> im); }
interface TerminationFunction<T, S> { boolean isTerminal(T framework, S model, InitialModels<S> im); }
    // factories: always(), noUnattackedInitialModels(), noInitialModels(),
    // noUnattackedOrUnchallengedInitialModels(),
    // <T extends Collection<?>, S> emptyFramework()   (method-level bound only)

final class SerialisationState<T, S> implements Node        // plain (F, E)
    getFramework(), getModel(); equals/hashCode over both; package-private constructor
final class SerialisationStep<T, S> extends DirectedEdge<...> // + getInitialModel()
final class SerialisationSequence<S> extends AbstractList<S>  // steps only
    SerialisationSequence(List<S> steps), extend(S), get, size, toString "(..)"
final class SerialisationSystem<T, S>
    getScheme/getSelectionFunction/getTerminationFunction,
    getGraph(F), getModels(F), getSequences(F)
final class SerialisationGraph<T, S> implements Graph<SerialisationState<T, S>>
    getRoot, isTerminal(state), getTerminalStates, getModels,
    getInitialModels(state), getState(model) [scan], getOutgoingSteps(state),
    getSequences(), getSequences(state), iterateSequences(), Graph surface
class SequenceIterator<T, S>  (package-private, public graph API only)
```

### Decisions per remark

- R1/R2: the state is the plain pair (F, E). The partition of each state is
  computed once by the engine and stored in the graph (`getInitialModels(state)`),
  which is where the property analysis reads it. Not computing it twice is
  worth it: it is the dominant cost (exponential per state).
- R1/R2: α keeps the paper signature α(ua, uc, c) (as one `InitialModels`); β
  becomes β(F, E, is(F)): the paper's β(F, E) plus the initial models of F,
  which β_co/β_pr/β_uc are defined over anyway. Single-(F, E) callers (web)
  call `scheme.partition(F)` and α/β directly; no public state constructor.
- R3: `isEmpty` leaves scheme and state. `emptyFramework()` stays generic with a
  bound on the factory only (`T extends Collection<?>`): DungTheory, ADF and
  SetAf are BeliefSets. Javadoc: two-valued models (not stable) for ADFs.
- R4: the sequence holds only its steps (BT26 Def. 3.8 defines a sequence as
  (S1, …, Sn); Ŝ is derived). No scheme, no stored result, nothing to keep
  consistent; anyone may construct sequences. Callers needing Ŝ use the graph
  (`getSequences(state)` groups by end state) or fold with `scheme.union`
  (explanations: AF union of steps). No defensive copies (C1 convention).
- Open questions: as steps-only value, the sequence is naturally a read-only
  `List<S>` (`AbstractList`: `get`/`size`, mutators throw, list equality),
  `toString` "({a},{b})". No `Comparable`, no interpretation superclass.
- R5/R7: `getModels` on system and graph; no `getModel`/`getResult` in the
  core (reasoners return some element of `getModels`). `explore` only builds
  the graph.
- R6: `all` derived.
- R8: renames as listed (solution → model, initial set → initial model,
  theory → framework, combine → union); `isConflicting` documented symmetric.
- R9: graph keyed by state (equality over the model, see follow-up remarks);
  `outgoing` and `incoming` kept, explicit step list dropped; terminal states
  and per-state partitions stored by state.
- R10: disjointness in C3; engine throws on a second step between the same
  states.
- R11: iterator as own package-private class; graph acyclicity checked once
  after construction (`DefaultGraph.containsCycle`), iterator has no on-path set.

### Follow-up remarks on the revision

- Web service and `SequenceExplanation` do not drive the design (the web
  service gets a major rework for multiple formalisms anyway).
- R3 alternative: global bound `T extends BeliefSet<?, ?>` (isEmpty for free,
  ties to Tweety's knowledge-base abstraction) vs. method-level bound on
  `emptyFramework()` only. Against the global bound: `AbaTheory` is only a
  `BeliefBase` (direct ABA scheme excluded; F4 via SetAf unaffected), bound
  serves one method, generics noise, BeliefSet test fixture needed.
  Decided: method-level bound now, global bound once several framework
  operations are needed.
- Decided: steps-only sequence (R4), as read-only `List<S>` (`AbstractList`).
- Decided: no first-model method in the core; keep in mind as a later
  optimisation if a reasoner needs early exit.
- R9 decided (option B): state `equals`/`hashCode` over the model only; within
  one serialisation, E determines F (C4), and states only exist inside graphs
  (package-private constructor). Graph maps keyed by state; `getState(model)`
  via a model → state map. Javadoc: states are identified by their model
  within the serialisation of one framework; comparing states of different
  graphs is not meaningful.

### Engine checks after the revision

1. α selects only initial models of the state.
2. Every step changes the model.
3. At most one step between two states.
4. The graph is acyclic.

## Open questions

- `SerialisationSequence`: dropping the interpretation superclass loses nothing
  in use (only `getArgumentsOfStatus(IN)` via `SequenceExplanation`, replaced
  by `getResult()`).
- `SerialisationSequence` as read-only `List<S>` (via `AbstractList`)?
  Proposed: not needed, `getSteps()` suffices. Undecided.
- `Comparable`: proposed to drop (no natural order for generic `S`; use a
  `Comparator` where deterministic output is needed).

## Consequences to work out in the redesign

- The state becomes the plain pair (F, E); α/β need the initial sets and
  emptiness from somewhere else (engine/graph-level cache, or a different
  α/β signature).
- Each state's partition must still be computed at most once (plan Sec. 4.5/4.6).
- Callers that evaluate α/β on a single (F, E) (web service:
  `SerialisationIsTerminalCallee`, `SerialisationGetSelectionCallee`) need a
  replacement for `new SerialisationState<>(scheme, theory, solution)`.
- The property analysis (batch 2) reads classifications per state.
