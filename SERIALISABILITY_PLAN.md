# Plan: Generic serialisability module

Replace the AF-only serialisation code in `arg-dung` with a formalism-independent
module. A formalism plugs in through a `SerialisationScheme` (initial models,
reduct, union of models); selection functions, termination functions, the
state search, graphs and sequences are written once. This plan covers the core
module, the AF schemes (extension- and labeling-based) and the migration of all
existing callers. Further formalisms and base notions are listed as follow-up steps
in Sec. 9.

## Decisions

- New module `org-tweetyproject-serialisability`, groupId `org.tweetyproject`,
  package `org.tweetyproject.serialisability`. Depends only on `commons` and `graphs`.
- Schemes live in the module of their formalism (AF schemes in `arg-dung`).
- A scheme class fixes the formalism and model representation (`empty`, `union`,
  ⊑); the base notion τ with its reduct and classification hooks is a
  constructor parameter (`AfSerialisationBasis` for AFs). Combinations are the
  user's responsibility and are not enforced (Sec. 4.1, 5.1).
- The old classes in `org.tweetyproject.arg.dung.serialisability.{semantics,syntax}`
  are deleted; all callers are migrated in the same change, with no deprecated shims.
  The web service and `arg-explanations` are ported minimally and do not drive the
  core design; the web service gets a separate rework for multiple formalisms.
- The generic α/β factories have structural names. Paper names and semantic
  mappings belong in Javadoc and formalism-specific reasoners.
- The AF labeling scheme labels an initial set S as `in = S`, `out = S⁺` (standard
  Caminada decisions), *not* BT26 Eq. 5 (`out = S⁻`). Internally, partial labelings
  contain only IN/OUT assignments; total labelings are reconstructed when needed.
- Keep Tweety's mutable framework and model types. Scheme operations do not
  mutate their inputs; retained state contents must remain unchanged (Sec. 4.1).
  Unmodifiable collections protect structure, not their mutable elements.
- `SerialisationGraph` implements `Graph` directly, with targeted storage and
  operations; it does not extend `DefaultGraph`.
- Include graph-local checks of BT26 closure, maximality, reinstatement,
  cautiousness and strict cautiousness as optional analyses (Sec. 4.11).
  They characterise a system, not implementation correctness.
- Reachability defines a partial order, not necessarily a lattice. A separate
  order view and the remaining BT26 property checks are follow-ups F8/F9.
- One `SerialisationScheme` interface. Framework-specific operations (reducts)
  stay methods of the framework classes; schemes delegate to them. No bound on
  the framework type `T` (Sec. 4.1).
- States, steps and sequences are plain data without a scheme. The engine stores
  the initial models of each state in the graph. Concepts are modelled first;
  derived data is computed, not stored, unless the saving is substantial.
- Terminology (Sec. 4): framework, model, initial model, union; terminal states
  carry the models of the induced semantics (`getModels`).
- The scheme contract is documented, not enforced, except for four cheap
  invariants checked by the engine (Sec. 4.6). A contract checker supports tests.
- Java 15: no records or sealed types; core data classes are final classes.

## Sources

- [T22] M. Thimm. *Revisiting initial sets in abstract argumentation*. Argument &
  Computation, 2022. (`../AgonProject/literature/T22.pdf`): Def. 1–7, Thm. 1–10.
- [BT26] L. Bengel, M. Thimm. *Serialisability in Formal Argumentation*. Preprint,
  2026. (`../AgonProject/literature/BT26.pdf`): Def. 3.3–3.8 (AF), Sec. 5 (ADF).
- [BT22b] L. Blümel, M. Thimm. *A Ranking Semantics for Abstract Argumentation
  based on Serialisability*. COMMA 2022: serialisation sequences.
- [BBU22] R. Baumann, G. Brewka, M. Ulbricht. *Shedding new light on the foundations
  of abstract argumentation: Modularization and weak admissibility*. AIJ 2022:
  modularisation (follow-up F5 only).
- [BKU24] L. Blümel, M. König, M. Ulbricht. *Weak Admissibility for ABA via
  Abstract Set-Attacks*. KR 2024: SETAF reduct (follow-up F3 only).
- [DTB26] T. Doukas, M. Thimm, L. Bengel. *Explanatory Serialisability in Abstract
  Argumentation*. Submitted to Argument & Computation, 2026
  (`../AgonProject/literature/DTB26.pdf`): construction schemes
  (Def. 24–31), weak initial sets (Def. 43, Prop. 16, Thm. 3), reduct-witness
  schemes (Def. 32–38, Thm. 2, 4), explanatory criteria (Sec. 6).

## 1. Problems with the current implementation

For reference; each item is addressed below.

1. `getSequences` enumerates every path of the transition system; `getModels`,
   `getModel` and `SerialisationGraph` are derived from it. Paths can outnumber
   states factorially (k unattacked arguments: k! grounded sequences, 2^k states).
2. Initial sets are recomputed for the same state by α (via
   `partitionInitialSets`) and again by `TerminationFunction.PREFERRED`/`UNCHALLENGED`.
3. `SerialisationSequence` is a mutable `List` that breaks the `List` contract
   (`add` silently drops sets, `remove` always returns false, `isEmpty` vs `size`,
   shallow copies share inner sets, hash-based `compareTo`).
4. `SerialisationState.equals` ignores the theory, `hashCode` does not.
5. `SerialisationGraph` has incomplete graph operations (7 unsupported operations).
   Retain direct `Graph` implementation, with an explicit operation contract.
6. `Semantics.diverse` is used as a placeholder for custom α/β.
7. Unchecked `Collection`→`Set` casts in `getSelection`; α/β sit in a `syntax` package.

## 2. Module layout

```
commons, graphs
    ↑
serialisability            org.tweetyproject.serialisability(.util)
    ↑
arg-dung                   AfExtensionScheme, AfLabelingScheme, SerialisedExtensionReasoner
    ↑
arg-adf, arg-setaf, …      (follow-ups)        web, arg-explanations
```

- `org-tweetyproject-serialisability/pom.xml`: parent `parent-pom` 1.31-SNAPSHOT,
  dependencies `commons`, `graphs`, `junit-jupiter` (test).
- Root `pom.xml`: add `<module>org-tweetyproject-serialisability</module>`.
- `arg-dung/pom.xml`: add dependency on `serialisability`.
- `org-tweetyproject/pom.xml` (aggregate): add dependency on `serialisability`.
- No dependency changes for `web` and `arg-explanations` (transitive via `dung`).

Packages:

| Package | Contents |
|---|---|
| `org.tweetyproject.serialisability` | `SerialisationScheme`, `InitialModels`, `SelectionFunction`, `TerminationFunction`, `SerialisationSystem`, `SerialisationState`, `SerialisationStep`, `SerialisationGraph`, `SerialisationSequence`, `SequenceIterator` (package-private) |
| `org.tweetyproject.serialisability.util` | `SchemeContractChecker` |
| `org.tweetyproject.serialisability.analysis` | `SerialisationProperty`, `SerialisationPropertyAnalysis`, `SerialisationPropertyReport`, `SerialisationPropertyViolation` |
| `org.tweetyproject.arg.dung.serialisability` | `AfSerialisationBasis`, `AfExtensionScheme`, `AfLabelingScheme` |
| `org.tweetyproject.arg.dung.serialisability.util` | existing plotters (ported) |

Core construction classes share one package so that graph construction can use
package-private mutators. Analyses use only the public read-only API.

## 3. Architecture overview

Type parameters throughout: `T` is the framework type (e.g. `DungTheory`), `S` the
model type (e.g. `Extension<DungTheory>`, `Labeling`, `ThreeValuedInterpretation`).

```
SerialisationSystem<T,S>  = (SerialisationScheme<T,S>, SelectionFunction<T,S>, TerminationFunction<T,S>)
        │ getGraph(F)
        ▼
SerialisationGraph<T,S>   nodes: SerialisationState<T,S> (framework, model)
        │                 edges: SerialisationStep<T,S>  (selected initial model)
        │                 per state: InitialModels<S> (unattacked, unchallenged, challenged)
        │ getSequences()
        ▼
SerialisationSequence<S>  (S1, …, Sn)
```

- A **scheme** defines the formalism-specific part: initial models and their
  classification, reduct, empty model, union.
- A **state** is the paper's (F, E) [BT26 Def. 3.3], identified by its model.
- α selects initial models of a state [BT26 Def. 3.4]; β decides whether a
  state is terminal [BT26 Def. 3.5]. Both are given (F, E) and the classified
  initial models of F; the standard α only use the latter.
- A **system** fixes (scheme, α, β), i.e. the αβ-serialisation system [BT26 Sec. 3].
  It explores the transition system, merging states by model, computes the
  initial models of each state once, and returns the `SerialisationGraph`.
- **Models** are the models of the terminal states. **Sequences** are
  root-to-terminal paths of the graph, enumerated on request (eagerly or lazily).
- Formalism reasoners (e.g. `SerialisedExtensionReasoner extends
  AbstractExtensionReasoner`) hold a `SerialisationSystem` and delegate to it.
  The engine is used by delegation because each reasoner must stay in its
  formalism's class hierarchy.

## 4. Core API (`org.tweetyproject.serialisability`)

Terminology: `T` is the framework type, `S` the model type. Initial models
(initial sets for AFs [T22], initial models for ADFs [BT26 Def. 5.1]) are the
⊑-minimal non-empty models of a base notion τ (e.g. admissibility). Every
state holds a τ-model; terminal states hold the models of the semantics
induced by α and β [BT26 Def. 3.6].

### 4.1 `SerialisationScheme<T, S>` (interface)

```java
public interface SerialisationScheme<T, S> {
    S empty(T framework);
    Collection<S> getBaseModels(T framework);
    default Collection<S> getInitialModels(T framework) { ... }
    boolean isUnattacked(T framework, S initialModel);
    boolean isConflicting(T framework, S initialModel, S other);
    T getReduct(T framework, S model);
    S union(S model, S other);
    boolean isLessOrEqual(S model, S other);
    default InitialModels<S> partition(T framework) { ... }
}
```

| Method | Meaning |
|---|---|
| `empty(F)` | the model of the root state (∅, υ_u, or the empty partial labeling) |
| `getBaseModels(F)` | the models of the base notion τ, i.e. τ(F) |
| `getInitialModels(F)` | the initial models of `F` (default below; may be overridden) |
| `isUnattacked(F, m)` | `m` is an unattacked initial model of `F` [T22 Def. 2, BT26 Def. 5.2] |
| `isConflicting(F, m, o)` | initial models `m` and `o` conflict; symmetric [BT26 Def. 5.2]. For AFs: one attacks the other |
| `getReduct(F, m)` | the m-reduct of `F` |
| `union(m, o)` | the union ⊔ of two compatible models (∪ for extensions) [BT26 Sec. 5] |
| `isLessOrEqual(m, o)` | the information order m ⊑ o (⊆ for extensions, ≤i for ADFs) |
| `partition(F)` | the three groups (default below; may be overridden) |

Default `getInitialModels(F)`: the ⊑-minimal base models m with
`!isLessOrEqual(m, empty(F))`, so the first part of C5 holds by construction.
The initial models are the moves of the scheme; a scheme may override
`getInitialModels`, either with an equivalent direct computation (e.g.
SAT-based or per-SCC, F2) or with a different notion of move, e.g. the
Pareto-minimal moves of reduct-witness schemes [DTB26 Def. 38], whose
comparison order differs from ⊑ (F11). In the latter case the first part of C5
does not hold, at the scheme's responsibility. Every scheme provides τ, even if
it overrides `getInitialModels`.

Default `partition(F)`: for each `m ∈ getInitialModels(F)`: unattacked if
`isUnattacked(F, m)`; otherwise challenged if some `o ∈ getInitialModels(F)` has
`isConflicting(F, m, o)`; otherwise unchallenged. Overrides must return the
same groups.

There is no emptiness test in the scheme. `TerminationFunction.emptyFramework()`
uses `Collection.isEmpty` via a bound on that factory only (Sec. 4.4). A global
bound on `T` (e.g. `BeliefSet`) is deferred until several framework operations
are needed generically; note that `AbaTheory` is only a `BeliefBase`.

Contract (Javadoc on the interface, cited by every implementation). Each scheme
documents its model representation, its information order ⊑, and which models
are compatible.

- **C1 Ownership and determinism.** Scheme operations and α/β are deterministic
  and do not modify their arguments. Frameworks and models have consistent
  `equals`/`hashCode` and must not be modified while retained by a graph or
  sequence. Reducts and unions are fresh objects. No copies or immutable
  wrappers are introduced.
- **C2 Union.** On compatible models, `union` is the least upper bound under ⊑;
  incompatible operands are rejected with `IllegalArgumentException`.
  `empty(F)` is neutral.
- **C3 Progress.** For each reachable state (F', E) and each initial model `s`
  of F', `s` only decides what E leaves undecided (for AFs: `s` is disjoint from
  E and E⁺; for ADFs: `s` assigns t/f only to arguments of the reduct). Hence
  `s` is compatible with E, E ⊏ E ⊔ s, distinct initial models lead to distinct
  successors, and every transition sequence is finite.
- **C4 Reduct composition.** `getReduct(F, empty(F))` equals F, and
  `getReduct(getReduct(F, e), s)` equals `getReduct(F, union(e, s))`. Hence
  within the serialisation of one framework a state is determined by its model.
- **C5 τ-modularisation.** `empty(F)` ∈ τ(F); the initial models are the
  ⊑-minimal non-empty members of τ(F); for e ∈ τ(F) and r ∈ τ(F^e),
  `union(e, r)` ∈ τ(F).
- **C6 τ-decomposition.** Every e ∈ τ(F) other than `empty(F)` is `union(s, r)`
  for some initial model s of F and r ∈ τ(F^s).

Under C1–C6, `SelectionFunction.all()` and `TerminationFunction.always()` yield
exactly τ(F). Implementations state whether C5/C6 are proven (with a citation)
or conjectured.

C1–C4 are preconditions of the engine: in particular, merging states by model
is only sound under C4; a reduct violating it makes the graph misrepresent the
transition system. C5/C6 only concern the correspondence with τ; without them
the system still defines a semantics. Which combinations of τ, reduct and
classification are well-behaved is the user's responsibility; they are not
enforced. `SchemeContractChecker` (Sec. 4.10) tests C2–C6 on finite examples.

### 4.2 `InitialModels<S>` (final, structurally unmodifiable)

- `InitialModels(Collection<S> unattacked, Collection<S> unchallenged, Collection<S> challenged)`:
  stores unmodifiable copies of the three collections (not of their elements).
- `getUnattacked()`, `getUnchallenged()`, `getChallenged()`: `Set<S>`.
- `getAll()`: the union, computed on each call (derived, not stored);
  `isEmpty()`: all three groups are empty.
- `equals`/`hashCode` over the three sets; `toString` like `{ua=…, uc=…, c=…}`.

### 4.3 `SelectionFunction<T, S>` (functional interface)

```java
@FunctionalInterface
public interface SelectionFunction<T, S> {
    Collection<S> select(T framework, S model, InitialModels<S> initialModels);

    static <T, S> SelectionFunction<T, S> all()                       // α_ad
    static <T, S> SelectionFunction<T, S> unattacked()                // α_gr
    static <T, S> SelectionFunction<T, S> unattackedOrUnchallenged()  // α_uc
}
```

α(ua, uc, c) [BT26 Def. 3.4], additionally given the state (F, E) so that
domain-specific selection functions can use the framework. The provided
factories depend only on the initial models, as in the literature; an α using
F or E may violate properties that assume this (e.g. monotonicity, BT26
Property 2). The result must be a subset of `initialModels.getAll()`
(engine-checked).

### 4.4 `TerminationFunction<T, S>` (functional interface)

```java
@FunctionalInterface
public interface TerminationFunction<T, S> {
    boolean isTerminal(T framework, S model, InitialModels<S> initialModels);

    static <T, S> TerminationFunction<T, S> always()                                    // β_ad
    static <T, S> TerminationFunction<T, S> noUnattackedInitialModels()                 // β_co
    static <T, S> TerminationFunction<T, S> noInitialModels()                           // β_pr
    static <T extends Collection<?>, S> TerminationFunction<T, S> emptyFramework()     // β_st
    static <T, S> TerminationFunction<T, S> noUnattackedOrUnchallengedInitialModels()   // β_uc
}
```

β(F, E) [BT26 Def. 3.5], additionally given the initial models of F so that
β_co, β_pr and β_uc do not recompute them. `emptyFramework()` tests
`framework.isEmpty()`; `DungTheory`, `AbstractDialecticalFramework` and `SetAf`
are `BeliefSet`s. For ADFs it yields two-valued models, not stable models
[BT26 Thm. 5.7]. A terminal state may still have successors.

### 4.5 `SerialisationState<T, S>` (final, implements `graphs.Node`)

- The plain pair (F, E) [BT26 Def. 3.3]: `getFramework()` (the residual
  framework), `getModel()`. No scheme, no cached initial models, no emptiness.
- Package-private constructor: states are created by the engine only.
- `equals`/`hashCode` over the model only. Within the serialisation of one
  framework a state is determined by its model (C4). Comparing states of
  different graphs is not meaningful.
- `toString()`: the model.

### 4.6 `SerialisationSystem<T, S>` (final): the engine

- `SerialisationSystem(SerialisationScheme<T,S> scheme, SelectionFunction<T,S> alpha, TerminationFunction<T,S> beta)`
- `getScheme()`, `getSelectionFunction()`, `getTerminationFunction()`.
- `getGraph(T framework)`: full exploration (below).
- `getModels(T framework)`: `getGraph(framework).getModels()`.
- `getSequences(T framework)`: `getGraph(framework).getSequences()`.

There is no first-model method; reasoners return some element of `getModels`.
An early-exit search can be added later if a reasoner needs it.

Exploration: worklist from the root (F, `scheme.empty(F)`); states are merged
by model. For each state:

1. `initialModels = scheme.partition(state.getFramework())`, computed once per
   state and stored in the graph.
2. Mark the state terminal if `beta.isTerminal(F', E, initialModels)`.
3. `selected = alpha.select(F', E, initialModels)`. Throw `IllegalStateException`
   unless `initialModels.getAll().containsAll(selected)`.
4. For each `s ∈ selected`: `next = scheme.union(E, s)`. Throw if
   `next.equals(E)`. Reuse the state with model `next`, or create it with
   framework `scheme.getReduct(F', s)` (incremental reduct; correct by C4).
   Add the step; throw if the two states are already connected (C3).

After exploration, throw `IllegalStateException` if the graph contains a cycle
(impossible under C3; guards sequence enumeration against broken schemes).

### 4.7 `SerialisationStep<T, S>` (extends `graphs.DirectedEdge<SerialisationState<T,S>>`)

- `SerialisationStep(SerialisationState<T,S> from, SerialisationState<T,S> to, S initialModel)`,
  label `initialModel.toString()`.
- `getInitialModel()`: the selected initial model (of the framework of `from`).
- `equals`/`hashCode` over endpoints and initial model. By C3 there is at most
  one step between two states.

### 4.8 `SerialisationGraph<T, S>` (final, implements `graphs.Graph<SerialisationState<T,S>>`)

Storage: the root and one map from model to a private `Entry` per state (in
insertion order), holding the state, its initial models, its outgoing and
incoming steps and a terminal flag. No separate step list or terminal set.
Built only by `SerialisationSystem` through package-private methods. Public
collections are unmodifiable.

- Full read-only `Graph` surface, as in `DefaultGraph` (directed adjacency;
  `getChildren`/`getParents`/`getNeighbors` throw `IllegalArgumentException`
  for states not in the graph). Connected and strongly connected components and
  subgraphs reuse the static `DefaultGraph` methods; restriction and complement
  return ordinary `DefaultGraph`s. Public `add(...)` throws
  `UnsupportedOperationException`: the graph is the computed reachable part of
  one system, and external nodes or edges would invalidate root, terminal
  states, initial models and sequences.
- `getRoot()`, `isTerminal(state)`, `getTerminalStates()`, `getModels()`
  (models of the terminal states).
- `getInitialModels(state)`: the classified initial models of the state's framework.
- `getState(S model)`: the state with that model, or `null`.
- `getOutgoingSteps(state)`.
- `getSequences()`: all paths from the root to a terminal state, including the
  empty sequence if the root is terminal and paths through terminal states.
  Exponential in general.
- `getSequences(state)`: all paths from the root to the given state; only
  ancestors of the state are visited.
- `iterateSequences()`: lazy `Iterable` over the paths of `getSequences()`.

Enumeration is a package-private class `SequenceIterator` (depth-first, using
only the public graph API). It needs no cycle check (the graph is acyclic).

### 4.9 `SerialisationSequence<S>` (final, extends `AbstractList<S>`)

- A sequence is its steps (S1, …, Sn) [BT26 Def. 3.8]; the induced model Ŝ is
  derived and not stored. No scheme, no framework.
- `SerialisationSequence(List<S> steps)` (copied), `extend(S initialModel)`
  (new sequence), `get(int)`, `size()`. Read-only: inherited mutators throw.
- List equality over the steps; every empty sequence is equal.
- `toString()`: `({a},{b})`.
- Ŝ is obtained from the graph (`getSequences(state)` groups sequences by end
  state) or by folding the steps with `scheme.union`.
- No `Comparable`, no interpretation superclass.

### 4.10 `util.SchemeContractChecker` (final, static methods)

For tests of schemes, in `main` so that other modules' tests can use it without a
test-jar. Each method returns the list of violations (empty = passed), with
messages naming the framework and models involved.

- `checkPartition(scheme, framework)`: groups disjoint, union = `getInitialModels`,
  override agrees with a reference partition computed directly from the hooks.
- `checkTransitions(scheme, framework)`: with `all()`/`always()`, check root reduct
  identity and each outgoing transition's non-changing-step test and composition:
  `getReduct(state.getFramework(), s)` equals
  `getReduct(framework, union(state.getModel(), s))`.
  Compute and compare these before destination deduplication, including edges
  into already-visited models. Report a violation and stop that exploration
  on a mismatch; do not silently discard an inconsistent alternate reduct.
  This check requires structural framework `equals` to express C4; if it does
  not, use a scheme-specific test instead of a misleading generic check.
- `checkOrder(scheme, framework)`: on the initial models and reachable models,
  `union` is an upper bound of its operands under `isLessOrEqual` (C2) and every
  step strictly increases the model (C3).
- `checkBase(scheme, framework, Collection<S> tauModels)`: compare the models
  of `all()`/`always()` with supplied reference models; an overload uses
  `getBaseModels(framework)`. Report missing and unexpected models. This checks
  the result correspondence for that input, not C5/C6 as universal statements.
- `checkInitialModels(scheme, framework)`: an overridden `getInitialModels`
  agrees with the default derivation from `getBaseModels`.

Keep checks small and concrete. No generic exhaustive algebra checker or inferred
compatibility test; test identity, commutativity and associativity on explicitly
compatible examples in each scheme's own tests. No checker proves termination;
use finite small inputs and return immediately on detected transition violations.

### 4.11 Graph-local serialisation property analysis

Optional analysis of a fully explored `SerialisationGraph`, separate from
`SchemeContractChecker` and from extension-based principles. Implement the five
conditions from [BT26 Sec. 4.1, Properties 5–9] that can be inspected directly on
the reachable graph:

| `SerialisationProperty` | Condition at every reachable state `v` |
|---|---|
| `CLOSURE` | No outgoing steps implies `graph.isTerminal(v)` |
| `MAXIMALITY` | `graph.isTerminal(v)` implies no outgoing steps |
| `REINSTATEMENT` | `graph.isTerminal(v)` implies `graph.getInitialModels(v).getUnattacked().isEmpty()` |
| `CAUTIOUSNESS` | No outgoing step selects a member of `graph.getInitialModels(v).getChallenged()` |
| `STRICT_CAUTIOUSNESS` | Every outgoing step selects a member of `graph.getInitialModels(v).getUnattacked()` |

- `SerialisationPropertyAnalysis.check(graph, property)` returns a
  `SerialisationPropertyReport<T,S>`. A convenience `checkAll(graph)` evaluates
  the five properties on the same graph and its stored initial models.
- The report identifies the property and inspected graph (hence its input and
  computed system behavior), has the explicit scope `REACHABLE_STATES`, and
  provides `isSatisfiedOnGraph()` plus a structurally unmodifiable list of
  `SerialisationPropertyViolation<T,S>`.
- A violation contains the state, an explanatory message and, when relevant,
  the offending outgoing step or unattacked initial model. Witnesses are actual
  objects rather than only formatted strings; no sequence enumeration is needed.
- The input must be a complete exploration from the root, including successors
  of terminal states. A restricted graph cannot establish these conditions;
  ordinary graphs returned by restriction are not accepted by this API.
- A successful check establishes the condition only on that graph. BT26
  properties are universally quantified; several concern arbitrary `(F,E)`
  states, not just states reachable from a particular input. Do not report
  graph-local success as a proof that α/β satisfies a property universally.
- A violation refutes the condition for the tested system. It does not establish
  that the induced semantics has no other α/β representation satisfying it:
  BT26's corresponding semantics-level definitions are existential.
- These checks are never mandatory engine validation or optimization flags.
  Stable serialisation can violate closure; admissible serialisation can violate
  maximality. Such outcomes are meaningful semantic characteristics.
- The implementation is generic because it uses only transitions, terminal
  membership and initial-model classifications. For non-AF schemes these are
  structural analogues; AF-specific semantic implications from BT26 are not
  automatically claimed for another formalism.

## 5. AF schemes (`arg-dung`, `org.tweetyproject.arg.dung.serialisability`)

### 5.1 `AfSerialisationBasis`

Holds the τ-dependent operations of the AF schemes; the schemes delegate to it.

```java
public class AfSerialisationBasis {
    public static final AfSerialisationBasis ADMISSIBLE = new AfSerialisationBasis(new SimpleAdmissibleReasoner());
    public AfSerialisationBasis(AbstractExtensionReasoner reasoner);
    public Collection<Extension<DungTheory>> getModels(DungTheory af);          // τ(F): reasoner.getModels(af)
    public DungTheory getReduct(DungTheory af, Extension<DungTheory> ext);     // af.getReduct(ext), removes S ∪ S⁺
    public boolean isUnattacked(DungTheory af, Extension<DungTheory> ext);     // af.getAttackers(ext).isEmpty()
    public boolean isConflicting(DungTheory af, Extension<DungTheory> ext, Extension<DungTheory> other);
                                                                               // one attacks the other
}
```

- The constructor argument chooses an implementation of τ (e.g. a SAT-based
  admissible reasoner). Other notions are subclasses overriding any of the four
  methods, e.g. with a different reduct or conflict relation. Combinations are
  not checked (Sec. 4.1); the Javadoc points to C4 and `SchemeContractChecker`.
- Only `ADMISSIBLE` is provided now: C5/C6 by [T22 Thm. 1, Cor. 1]; C3/C4 by the
  reduct definition (initial sets of F^E are disjoint from E ∪ E⁺). Further
  bases are F5.
- For admissible initial sets, "one attacks the other" coincides with "the
  other attacks it" [BT26 Def. 3.2], since admissible sets counter-attack.

### 5.2 AF schemes

`AfExtensionScheme implements SerialisationScheme<DungTheory, Extension<DungTheory>>`:
`AfExtensionScheme()` (with `ADMISSIBLE`), `AfExtensionScheme(AfSerialisationBasis basis)`.

| Method | Implementation |
|---|---|
| `empty` | `new Extension<>()` |
| `getBaseModels` | `basis.getModels(af)` |
| `isUnattacked`, `isConflicting`, `getReduct` | delegate to `basis` |
| `union` | new `Extension` with the union |
| `isLessOrEqual` | `other.containsAll(model)` |

- `SimpleInitialReasoner` stays an independent reasoner for initial sets (an AF
  notion in its own right [Xu, Cayrol 2016]) and does not depend on the schemes.
  `Initial`, `partitionInitialSets` and `isUnattacked`/`isUnchallenged`/`isChallenged`
  are removed; callers use `AfExtensionScheme.partition`. `getModel` returns some
  initial set or `null`.

`AfLabelingScheme implements SerialisationScheme<DungTheory, Labeling>`:
`AfLabelingScheme()` (with `ADMISSIBLE`), `AfLabelingScheme(AfSerialisationBasis basis)`.

| Method | Implementation |
|---|---|
| `empty` | `new Labeling()` (no recorded decisions) |
| `getBaseModels` | `basis.getModels(af)`, each mapped with `toLabeling(af, e)` |
| `toLabeling(af, e)` | protected, overridable: the partial labeling IN = E, OUT = E⁺; no UNDECIDED entries |
| `isUnattacked`, `isConflicting`, `getReduct` | delegate to `basis` on the IN parts |
| `union` | new `Labeling` containing the union of both operands' recorded assignments; identical assignments agree, opposite IN/OUT assignments throw `IllegalArgumentException` |
| `isLessOrEqual` | every recorded assignment of `model` is recorded in `other` |

- `toLabeling` must label exactly the arguments the basis's reduct removes
  (C3/C4: the model records what the reduct removed). The default fits reducts
  removing E ∪ E⁺, such as `ADMISSIBLE`; for other reducts users override it.
  Documented, not checked.
- The representation is a finite partial map containing only IN/OUT decisions.
  Missing entries mean "no decision recorded", not that the argument is outside
  the original framework. `union` preserves the union of both recorded
  domains; `{}` is its identity. Reject explicit UNDECIDED entries at scheme
  union boundaries; conversion from a total labeling drops them first.
  Do not change the global equality or other behavior of Tweety's `Labeling`.
- A step records decisions about arguments in the current reduct. Accumulated
  models record decisions about the original framework, without retaining
  undecided entries. A helper `toTotalLabeling(DungTheory original, Labeling partial)`
  copies decisions and fills absent arguments with UNDECIDED; reject recorded
  arguments outside `original`.
- Javadoc: with `ADMISSIBLE`, τ is the image of admissible extensions under the
  canonical partial labeling IN = E, OUT = E⁺. This is not a claim about every
  possible definition of admissible labelings. `Labeling` remains a mutable
  `Map`, subject to C1; the scheme does not modify its inputs.
- After totalization, models equal `new Labeling(original, e)` for extension
  models `e`. Thus `all()`/`noUnattackedInitialModels()` yields complete labelings
  after totalization. Sequence equality uses only partial decision maps, so
  adding undecided arguments to an originating framework does not itself change
  a sequence.
- No dedicated labeling reasoner (Tweety has no labeling reasoner base class); use
  `new SerialisationSystem<>(new AfLabelingScheme(), α, β)` directly.

### 5.3 `SerialisedExtensionReasoner` (stays in `org.tweetyproject.arg.dung.reasoner`)

`extends AbstractExtensionReasoner`, holds a
`SerialisationSystem<DungTheory, Extension<DungTheory>>` over `AfExtensionScheme`.

- `SerialisedExtensionReasoner(Semantics semantics)`, with the same mapping as
  today:

  | Semantics | α | β |
  |---|---|---|
  | ADM | `all()` | `always()` |
  | CO | `all()` | `noUnattackedInitialModels()` |
  | PR | `all()` | `noInitialModels()` |
  | ST | `all()` | `emptyFramework()` |
  | GR | `unattacked()` | `noUnattackedInitialModels()` |
  | SAD | `unattacked()` | `always()` |
  | UC | `unattackedOrUnchallenged()` | `noUnattackedOrUnchallengedInitialModels()` |

  Other semantics: `IllegalArgumentException("Semantics is not serialisable: …")`.
- `SerialisedExtensionReasoner(SelectionFunction<DungTheory, Extension<DungTheory>> alpha, TerminationFunction<DungTheory, Extension<DungTheory>> beta)`.
  The `(α, β, Semantics)` constructor and the `Semantics.diverse` placeholder go
  away (fixes problem 6); `getSemantics()` returns `null` for custom α/β.
  `Semantics.diverse` itself stays (used by `ProboProblem` and the web service).
- `getModels(af)`: `system.getModels(af)`; `getModel(af)`: some element of
  `getModels(af)`, or `null` if there is none.
- `getSequences(af)`: `Set<SerialisationSequence<Extension<DungTheory>>>`.
- `getSerialisationGraph(af)`: `SerialisationGraph<DungTheory, Extension<DungTheory>>`.
- `getSystem()`. `getSelection` and `isTerminal` are removed; callers use the system.
- `AbstractExtensionReasoner` (UC → `SerialisedExtensionReasoner(Semantics.UC)`)
  is unchanged.

## 6. Migration of callers

`arg-dung`:

- Delete `serialisability/semantics/{SerialisationGraph,SerialisationSequence,SerialisationState}`
  and `serialisability/syntax/{SelectionFunction,TerminationFunction}`.
- `SerialisationGraphPlotter` moves to the core module
  (`org.tweetyproject.serialisability.util`) and becomes generic in `T`, `S`;
  terminal states are highlighted via `graph.isTerminal(state)`.
- `SerialisationAnalysisPlotter` (stays in `arg-dung`): takes `List`/`Map` instead of
  arrays/`HashMap`; fixes the overwrite bug in `plotAnalyses(framework, graphs, …)`;
  the `Semantics` overloads add `semantics.description()` to the title.
- `AigSerialisationPlotter` (stays in `arg-dung`): final, not instantiable; uses
  `graph.getRoot()`; terminal states are colored instead of marked with `*`.
- `equivalence/SerialisationEquivalence`: unchanged logic (compares the sets of
  sequences using ordered-step equality, independent of their originating frameworks).
- Examples: `SerialisedExtensionReasonerExample`, `GraphPlotterExample`,
  `EquivalenceExample`: new types and factories.
- Tests: move `serialisability/sequence/SerialisationSequenceTest` to the core
  module (rewritten for the new `SerialisationSequence`); update
  `equivalence/StandardEquivalenceTest`.

`web`:

- `SerialisationFactory`: retain external names, mapping to the structural
  factories in Sec. 4.3/4.4 (e.g. `SelectionFunction.all()` for α_adm).
- `SerialisationGetSelectionCallee`: `alpha.select(bbase, extension, new AfExtensionScheme().partition(bbase))`.
- `SerialisationIsTerminalCallee`:
  `beta.isTerminal(bbase, extension, new AfExtensionScheme().partition(bbase))`.
- `SerialisationGetModelsCallee`, `SerialisationGetSequencesCallee`,
  `AbstractExtensionReasonerFactory` ("SER"), `RequestController`: new types.
- Answers are `toString()` of collections. `RequestControllerSerialisationTest`
  compares exact strings (e.g. `"[({4},{1}), ({1})]"`), whose order depends on
  hash iteration. If the order changes, the callees sort sequences by `toString()`
  and the expected strings are updated once.

`arg-explanations` (port types only; rework is F6):

- `SequenceExplanation`: holds `SerialisationSequence<Extension<DungTheory>>`. Keeps
  implementing `List<Collection<? extends Argument>>` by delegating to the sequence
  (mutators throw). `getSetExplanation()` and `getArgumentsOfStatus(IN)` use the
  union of the steps.
- `SequenceExplanationReasoner`: starts with `new SerialisationSequence<>(List.of())`,
  builds sequences with `extend(set)` and gets the groups from `AfExtensionScheme.partition`.
- `DialecticalSequenceExplanationReasoner`: the prefix union is computed from
  `subList(0, i)` directly.
- Check `MinimalSequenceExplanationReasoner` and other users of
  `SequenceExplanation.getSequence()`.

## 7. Tests

Small inputs only (≤ 8 arguments, fixed seeds), no long runs.

Core module (junit-jupiter), with a toy scheme over finite sets of integers:
- merging: diamond-shaped transition systems yield one state per model;
- `getModels` of system and graph agree; initial models stored per state;
- sequences: counts on a known example, including the empty sequence for a terminal root;
- engine invariants: an α returning a foreign model, a non-changing step, a
  second step between the same states, and a cycle each throw
  `IllegalStateException`;
- `SerialisationSequence`: `extend`, list behaviour (read-only), ordered-step
  equality, and `toString`;
- `SerialisationGraph`: exercise its `Graph` interface on a small diamond,
  including adjacency, parents/neighbors, paths, restriction and components;
  public mutation is rejected and collections/iterators cannot alter storage;
  `getSequences(state)` visits only ancestors;
- `SchemeContractChecker` reports a planted C4 violation on an alternate incoming
  path to an already-seen model;
- eager and lazy sequence traversal agree, including paths through terminal states.
- property analysis: small graphs with satisfying and violating instances for
  each of the five conditions; verify the scope and concrete witnesses, including
  a terminal state with successors and a nonterminal dead end.

`arg-dung`:
- `SerialisedExtensionReasoner` vs the existing reasoners
  (`SimpleAdmissibleReasoner`, `SimpleCompleteReasoner`, `SimplePreferredReasoner`,
  `SimpleStableReasoner`, `SimpleGroundedReasoner`, `StronglyAdmissibleReasoner`) on
  random AFs from `DefaultDungTheoryGenerator`;
- UC on [T22] Ex. 9 (AF₂: {b}, {b, e}) and Ex. 10 (AF₇: {d, f}), plus the web
  test's example;
- totalized `AfLabelingScheme` models = `new Labeling(af, e)` for the extension
  scheme's models; partial models contain no UNDECIDED entries;
- a few explicit compatible partial labelings verify domain-preserving union,
  identity, commutativity and associativity; opposite decisions are rejected;
  equal decisions from frameworks with different undecided arguments yield
  equal steps/sequences;
- `SchemeContractChecker` (partition, transitions and reference results) on both
  AF schemes; use `SimpleAdmissibleReasoner` directly for extensions and map its
  models to canonical partial labelings for the labeling scheme;
- regression: 10 isolated arguments under GR finish quickly (old code: 10! paths).
- property analysis: a few small AFs exercise the positive guarantees in
  [BT26 Table 2] for Properties 5–9 and representative negative examples.
  In particular, a self-attacking singleton under ST violates closure, an
  unattacked singleton under ADM violates maximality and reinstatement, mutual
  attack under ADM violates cautiousness, and GR uses only unattacked steps.
  Do not expect a universally failing property to fail on every input.

`web`: `RequestControllerSerialisationTest` passes (Sec. 6).

## 8. Implementation order

1. Module skeleton and poms; core classes (Sec. 4.1–4.9) with core tests (done).
1a. `isLessOrEqual`, `getBaseModels` and the default `getInitialModels` (Sec. 4.1).
1b. `SchemeContractChecker` and graph-local property analysis (Sec. 4.10–4.11)
   (deferred; see the roadmap in Sec. 9).
2. `AfSerialisationBasis`, `AfExtensionScheme`, `AfLabelingScheme`, `SerialisedExtensionReasoner`,
   `partitionInitialSets`; `arg-dung` tests.
3. Delete the old classes; port plotters, examples, equivalence, `arg-dung` tests;
   reduce `SimpleInitialReasoner` to a reasoner for initial sets; core Javadoc:
   initial models are the scheme's moves (Sec. 4.1) (done).
4. Port `web` and `arg-explanations`; run their tests (done).
5. Full build (`mvn -q install` of the affected modules) (done).

## 9. Follow-up steps (after the rework)

Roadmap (decided 2026-10-20; the PR of the rework waits):

1. Minimal `SchemeContractChecker`: only the checks the next schemes need
   (C1–C4 on given frameworks; C5/C6 against τ where a reference reasoner
   exists), as test support. The full design of Sec. 4.10 stays deferred.
2. F11 reduct-witness scheme.
3. F5 further AF bases: weakly admissible first, then conflict-free.
4. F1 ADF scheme.
5. F3 SetAF, then F4 ABA (coordinate with `ABA_WEAK_STRONG_PLAN.md`).
6. Analysis: property analysis (Sec. 4.11), F9, F8.
7. Consumers: F6 explanations, web service rework (separate), F2 performance.

Parked: F7 BAF, F10 general partitions. Independent and small, at any time:
`AigGraphPlotter` (graphs) — `getResource` fails inside jars
(`FileSystemNotFoundException` from `Paths.get(uri)`), and pages are written to
`index.html` in the working directory.

Each is a separate change. "Experimental" schemes have conjectured C5/C6 and
say so in their Javadoc. They are validated with `SchemeContractChecker`, and
their reasoners make no claims about equalling native semantics.

- **F1 ADF scheme** (`arg-adf`), backed by [BT26 Sec. 5]:
  `AdfScheme implements SerialisationScheme<AbstractDialecticalFramework, ThreeValuedInterpretation>`.
  - Initial models from `SimpleInitialAdfReasoner`/`SatInitialAdfReasoner`.
  - Serialisation reduct [BT26 Def. 5.3] as a new ADF method with its own name;
    the existing `reduct(ThreeValuedInterpretation)` is the two-valued reduct for
    stable semantics and stays as is.
  - Union ⊔ [BT26 Eq. 6] on `ThreeValuedInterpretation`, extending the domain
    (initial models of a reduct cover fewer arguments). Specify how absent and
    undecided assignments are normalized for model/step equality and the
    C2 domain convention; do not silently inherit domain-sensitive equality
    that distinguishes otherwise identical decision steps.
  - Hooks per [BT26 Def. 5.2] (unattacked: all decided arguments have tautological or
    unsatisfiable conditions; conflicting: t/f disagreement).
  - `SerialisedAdfReasoner extends AbstractAdfReasoner` for ADM, CO, PR, GR, SAD
    [BT26 Thm. 5.2–5.8]; `emptyFramework()` yields val₂ (Thm. 5.7; check against `MOD2`). ADF-stable
    is open.
  - Tests: a few small examples against native ADF reasoners, plus targeted
    initial-model/classification correspondence via υ_S (out = S⁻, BT26 Eq. 5)
    [BT26 Thm. 5.1, Prop. 5.2]. If testing reduct correspondence, compare through
    `toAF` as in Prop. 5.3. Do not require all ADF results to equal the Eq. 5 image
    of AF extensions or the results of `AfLabelingScheme`. Include `a → b`:
    the ADF has admissible models `(u,u)`, `(t,u)`, `(t,f)`, whereas Eq. 5 maps
    `{a}` to `(t,u)` and the complete model is `(t,f)`. No separate generic
    correspondence-testing infrastructure is needed.
- **F2 Initial-set performance**: per-SCC computation of AF initial sets
  [T22 Prop. 1/2]; SAT-based `partition` overrides where a reasoner can compute the
  groups directly.
- **F3 SetAF scheme** (`arg-setaf`, experimental): needs `SetAf.getReduct`
  (see `ABA_WEAK_STRONG_PLAN.md` Sec. 1, [BKU24] Def. 22); initial sets as minimal
  non-empty sets of `SimpleAdmissibleSetAfReasoner`; hooks via set attacks.
- **F4 ABA scheme** (`arg-aba`, experimental, flat ABA): serialise the SETAF
  instantiation (`SetafReductionReasoner`) and map assumption sets back.
- **F5 Further serialisation bases for AFs** (`arg-dung`, experimental): further
  `AfSerialisationBasis` instances, used with the existing AF schemes:
  - conflict-free: τ = conflict-free sets (initial sets: singletons of
    non-self-attacking arguments); reduct removes S ∪ S⁺ ∪ S⁻ (the standard
    reduct violates C5 for conflict-freeness); the labeling scheme needs an
    overridden `toLabeling` or is not used with it.
  - weakly admissible: τ from `WeaklyAdmissibleReasoner`; standard reduct;
    initial models are the weak initial sets [DTB26 Def. 43]. With α = all and
    β = always it yields wadm: C5/C6 by strong modularisation and the
    decomposition into weak initial sets [DTB26 Prop. 16, Thm. 3].
  - Decide the conflict relation per basis; test each on small examples.
- **F6 Explanations rework** (`arg-explanations`): rebuild
  `SequenceExplanationReasoner` (sequences up to the first step containing the
  argument), `MinimalSequenceExplanationReasoner` and the dialectical variant as
  queries on `SerialisationGraph`, reusing its states' reducts.
- **F7 BAF** (`arg-bipolar`): deferred; depends on the chosen reading of support.
- **F8 Order-theoretic graph analysis** (prefer a reusable DAG order view in
  `graphs`, with serialisation examples):
  - Define `x ≤ y` iff `x = y` or a directed path leads from `x` to `y`.
    Valid serialisation graphs are finite DAGs with the root as least element.
    They are not generally lattices: for `a ↔ b` with unrestricted admissible
    selection, `{a}` and `{b}` have no common upper bound.
  - Keep reachability order distinct from the scheme's information order `⊑`.
    Reachability implies information growth; the converse requires a separate
    justification for the scheme and selection function.
  - Expose `isLessOrEqual`, minimal upper bounds, maximal lower bounds, optional
    least upper/greatest lower bounds, and `isLattice`. Bounds are computed
    within the view's node set and reachability order; missing or non-unique
    bounds must not be replaced by a fabricated state.
  - A view derives its order from the existing graph, rather than duplicating
    storage in `comparator.Order`. `LatticePartialOrder` is a ranking abstraction
    with formula/belief-base bounds and no meet/join API; do not make the
    serialisation graph inherit from it or add a comparator dependency merely
    for naming conformity.
  - Preserve the labeled transition graph and its sequences. A Hasse diagram or
    transitive reduction is an optional derived view, not a replacement for the
    actual allowed transitions.
  - Illustrate Boolean lattices for isolated arguments with unrestricted selection.
    For the full admissible AF family under inclusion, compatible extensions
    have union as join, and a pair's meet is the greatest admissible subset of
    their intersection (the union of all admissible subsets of that intersection).
    These facts do not automatically transfer to arbitrary selection-restricted
    reachability orders.
  - Test basic bounds on small DAGs, including missing joins and multiple minimal
    upper bounds; applications include common continuations, common predecessors
    and incomparable partial constructions.
- **F9 Remaining BT26 serialisation properties** (`arg-dung` first):
  - Add explicit-instance checks for abstraction (renaming), monotonicity (the
    same initial set and classification across frameworks), composability
    (disjoint composition), Markovianity (β independent of accumulated model
    for a fixed residual framework), and confluence [BT26 Properties 1–4, 10].
  - Supply the required renamings, framework pairs and states explicitly.
    Report the checked scope and concrete counterexamples; finite samples are
    not universal proofs. Graph-only checks cannot replace quantification over
    arbitrary states or pairs of frameworks.
  - Implement BT26 confluence exactly: for a selectable set `S`, subsequent
    states must have accepted it, accepted an attacker of it, or still permit a
    continuation to the accumulated model extended by `S`. That continuation
    may use multiple smaller initial sets. This is neither ordinary branch
    joinability nor an immediate diamond condition, and does not imply that the
    serialisation graph is a lattice.
  - Markovianity is not implied by the state-based API: β can inspect the
    accumulated model. Preserve this freedom in the engine.
  - Reuse generic graph analyses where possible, but keep attacks, renamings and
    framework composition in AF-specific code. Do not enlarge the scheme
    interface solely to force every property into a generic checker.
  - Provide AF-specific adapters to the existing `commons.postulates`
    infrastructure where appropriate, without imposing `Formula` bounds on
    the generic engine. Keep serialisation properties distinct from
    extension-based `Principle` classes.
  - Use a few paper examples and custom α/β counterexamples for tests. Identify
    the tested scheme and α/β explicitly and retain the distinction between a
    property of that system and existence of a suitable representation of a
    semantics. Extend to other formalisms only with stated definitions.
- **F10 General partitions of initial models**: the three groups (unattacked,
  unchallenged, challenged) are fixed in `InitialModels`, the scheme hooks, the
  factory names and the property analysis. Domain-specific α/β can already
  compute finer classifications from F (Sec. 4.3). Generalise only once an
  alternative partition is meant to be a first-class concept that generic
  factories or analyses reason about, e.g. via a classification type `K`
  chosen by the scheme (`classify(F, m) → K`). Weigh this against `K`
  appearing in all generic signatures; consider keeping the standard three
  groups and adding finer classes only to the optional parts.
- **F11 Reduct-witness scheme** (`arg-dung`) [DTB26 Sec. 4]. Terminology: the
  paper's γ (semantics of the added arguments) and τ (vacuity semantics) are the
  scheme parameters `baseReasoner` and `vacuityReasoner`; the paper's τ is not
  our base notion τ. The scheme is independent of α and β; only the reasoner
  fixes them.
  - Framework `T` = `WitnessedFramework` (G, W): the residual framework G = F^P
    and the witness W, i.e. the local view λ_rw [DTB26 Sec. 7.4, Prop. 26]. W
    is part of the framework because the initial models and β depend on it.
  - Model `S` = `Labeling`: IN = P, OUT = P⁺, UNDECIDED = W (the witness
    arguments); unrecorded arguments are open. A witness argument is never
    labeled IN or OUT later (W must survive every later reduct), so recorded
    UNDECIDED is a fixed decision. `empty` = no recorded labels; `union` =
    union of the recorded labels (never conflicting, by C3); ⊑ = recorded
    labels included. The convention "recorded = decided, unrecorded = open"
    also covers `AfLabelingScheme`, which never decides UNDECIDED.
  - Reduct ((G, W), step) ↦ (G minus the step's IN and OUT, W ∪ its UNDECIDED);
    C4 by [DTB26 Lemma 1], C3 by the transition conditions [DTB26 Def. 32]
    (steps add only new arguments, and every step is non-empty).
  - Initial models of (G, W): candidates (ΔP, ΔW) with ΔP ∈ γ(G) ∪ {∅},
    ΔW ⊆ args(G^ΔP) \ W, W ⊆ args(G^ΔP) and τ(G↓(W ∪ ΔW)) ⊆ {∅}, as labelings
    (IN = ΔP, OUT = ΔP⁺ in G, UNDECIDED = ΔW). Extension steps (ΔP ≠ ∅) must
    contain the residual attackers of ΔP in ΔW and are Pareto-minimal;
    witness steps (ΔP = ∅) are inclusion-minimal [DTB26 Def. 34–38], computed
    from the definitions. `getInitialModels` is overridden: minimality only
    compares steps of the same kind (≺rw), unlike ⊑ (see Sec. 4.1).
  - Base models (`getBaseModels`): the structural basis B_rw (all candidate
    steps of both kinds before minimisation) [DTB26 Sec. 7.4]. No base notion
    serves both roles of τ here: the paper characterises only the terminal
    results, not all reachable models. C5/C6 do not hold; `checkBaseModels`
    does not apply.
  - Classification (own definition, not from [DTB26]): the AF definitions
    applied literally to ΔP. Unattacked = ΔP has no attackers in G; conflicting
    = the ΔP of one step attacks that of the other. Witness steps (ΔP = ∅) are
    thus unattacked and never conflicting, without a special case; hence
    `unattacked()` always admits them, and `noUnattackedInitialModels()`
    terminates only when no witness step is available. Open point: whether
    "conflicting" should also cover steps blocking each other through the
    witness (one step's ΔW contains arguments of the other's ΔP), see F10.
  - Termination: `AfReductWitnessScheme.noOpenArguments()` (W = args(G),
    [DTB26 Def. 32]), a `TerminationFunction<WitnessedFramework, Labeling>`.
    The generic factories (`always()`, `noInitialModels()`, …) also apply.
    Only under `noOpenArguments()` are terminal models total; they then equal
    `new Labeling(F, P)` (UNDECIDED = A \ (P ∪ P⁺)). With other β, terminal
    models may contain open arguments; results are not completed, so open and
    witness arguments stay distinct.
  - Results: with α = all and `noOpenArguments()` this serialises γ^τ under
    [DTB26 Thm. 2], in particular ud = cf^adm and wpr = cf^wadm [DTB26 Cor. 4,
    5]; P is conflict-free when γ ⊆ cf. No moves at terminal states [DTB26
    Prop. 7]. Non-terminal states without initial models (dead ends) exist,
    e.g. w → w, x → w (cf, adm) at (∅, {w}); more with restrictive α. What
    other α/β yield is not established; the Javadoc says so.
  - `SerialisedReductWitnessReasoner extends AbstractExtensionReasoner`:
    `(baseReasoner, vacuityReasoner)` with α = all and `noOpenArguments()`, and
    `(baseReasoner, vacuityReasoner, α, β)`. `getModels` returns the IN parts
    (with other β, distinct terminal labelings may share their IN part);
    labelings via `getSystem()`, `getSequences`, `getSerialisationGraph`.
  - Tests: [DTB26] Ex. 19–23; the default against `UndisputedReasoner`,
    `WeaklyPreferredReasoner` and `VacuousReductReasoner` on small seeded AFs;
    the dead-end example; one restrictive α; one other β with partial
    results; `checkContract`.
  - Remark (revisit): the distinction between open and fixed undecided
    arguments rests only on "recorded vs. unrecorded" in `Labeling`. Consider
    again a representation that makes it explicit, e.g. a model type with an
    explicit set of open arguments, a dedicated status, or a partial-labeling
    class shared with `AfLabelingScheme` and the ADF scheme (F1, where
    ThreeValuedInterpretation has the same question).
  - The dialectical record (S^ρ, T^ρ, U^ρ) [DTB26 Def. 42] (OUT and UNDECIDED
    parts of each extension step) can be derived from sequences; optional.
