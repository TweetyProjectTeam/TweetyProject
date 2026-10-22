# Plan: Weak and strong admissibility for ABA via SETAFs

Flat ABA only. Reasoners are implemented on SETAFs; ABA support comes through
`SetafReductionReasoner`. A native ABA reasoner is deferred; it would only be
needed for non-flat theories (BSAF semantics) or if attack enumeration becomes
the bottleneck.

## Sources

- [BKU24] L. Blümel, M. König, M. Ulbricht. *Weak Admissibility for ABA via
  Abstract Set-Attacks*. KR 2024, 178–188.
  (`~/Downloads/Papers/kr2024-0017-blumel-et-al.pdf`)
- [BBR25] M. Berthold, L. Blümel, A. Rapberger. *On Strong and Weak
  Admissibility in Non-Flat Assumption-Based Argumentation*. arXiv:2508.11182, 2025.
  (`~/Downloads/Papers/2508.11182v1.pdf`)

Note: [BKU24] Sec. 3 shows that weak admissibility on the AF instantiation of
an ABAF gives wrong results. Do not use `AfReductionReasoner` + the Dung `Weakly*`
reasoners for ABA.

## 1. `SetAf.getReduct(Collection<Argument> e)`

[BKU24] Def. 22 (originally Dvořák et al. 2024):

- arguments: A \ (E ∪ E⁺)
- attacks: (T \ E, h) for each (T, h) ∈ R with T ∩ E⁺ = ∅, T ⊄ E, h in the reduct

Needed by all weak semantics and by weak defense. `SetAf` currently only has
`getRestriction`.

## 2. `SimpleStronglyAdmissibleSetAfReasoner` (SAD)

- Definition: [BBR25] Def. 5.2/5.3 with no supports. E is conflict-free and every
  a ∈ E is defended by a strongly admissible subset of E \ {a}.
- Candidates: subsets of the grounded extension (`IncreasingSubsetIterator`).
  Sound because strongly admissible sets lie inside every complete extension
  ([BBR25] Prop. 5.6). Same approach as the Dung `StronglyAdmissibleReasoner`.
- Check ([BBR25] Prop. 5.4): iterate S ↦ `faf(S)` ∩ E from ∅ until a fixpoint;
  E is strongly admissible iff the fixpoint equals E. Conflict-freeness holds
  because candidates lie inside the grounded extension.
- Out of scope: strongly complete/preferred ([BBR25] Def. 5.9); for flat theories
  the maximum strongly admissible set is unique ([BBR25] Thm. 5.12).

## 3. `SimpleWeaklyAdmissibleSetAfReasoner` (WAD)

- Preprocessing: remove arguments a with ({a}, a) ∈ R ([BKU24] Thm. 33).
- Candidates: conflict-free sets, filtered by `isWeaklyAdmissible`.
- `isWeaklyAdmissible(SetAf, ext)` ([BKU24] Def. 24), recursive:
  - ∅ always passes.
  - For each (T, h) ∈ R with h ∈ E and T ∩ E⁺ = ∅: E fails if some weakly
    admissible set of `getReduct(E)` contains T ∩ A′ (A′ = arguments of the reduct).
  - Searching for a ⊆-minimal such set is enough.
- Not included initially: [BKU24] Prop. 32 (removing attacks with conflicting
  tails) as an optional pruning step. Keep the code close to the definition first.

## 4. `SimpleWeaklyCompleteSetAfReasoner`, `SimpleWeaklyPreferredSetAfReasoner`, `SimpleWeaklyGroundedSetAfReasoner` (WCO, WPR, WGR)

- Weak defense ([BKU24] Def. 38): E w-defends X iff for each (T, h) ∈ R with h ∈ X:
  - E attacks T, or
  - no E′ ∈ adm_w(F^E) with T ⊆ E ∪ E′, **and** some X′ ⊇ X is in adm_w(F).
- WCO: E ∈ adm_w such that no proper superset of E is w-defended by E ([BKU24] Def. 41).
- WPR: ⊆-maximal in adm_w ([BKU24] Def. 41; equals maximal in com_w by Prop. 45).
- WGR: ⊆-minimal in com_w. Not necessarily unique.
- Mirror the Dung `Weakly*Reasoner` classes, but compute adm_w(F) and
  adm_w(F^E) once per call rather than inside each superset check.

## 5. Wiring

- `AbstractSetAfExtensionReasoner.getSimpleReasonerForSemantics`: add SAD, WAD,
  WCO, WPR, WGR.
- `SetafReductionReasoner.NATIVE`: add the same five so they no longer fall
  through to `ReductionBasedSetAfReasoner` (flat ABA support for free).
- Open question: should `ReductionBasedSetAfReasoner` refuse SAD and the weak
  semantics? The metalevel AF construction is not known to preserve them. At
  least document this in its Javadoc.

## 6. Tests (small, fast)

SETAF module:
- [BKU24] Ex. 23/25/26, 27, 34 (adm_w = {∅, {a}, {b}}),
  37/42 (com_w = grd_w = {{c, e}}; pref_w = {{a, c, e}, {b, c, e}}).
- AF-shaped SETAF (singleton tails only), compared against the Dung `Weakly*`
  and `StronglyAdmissibleReasoner` ([BKU24] Prop. 29/43).

`AbaTest`, via `SetafReductionReasoner`:
- Climate example [BKU24] Ex. 15/48/51: adm_w = {∅, {stud}, {cc}, {stud, cc}},
  pref_w = {{stud, cc}}.
- Ex. 16: adm_w = {∅}.
- Ex. 17/52: {c} ∈ adm_w.
- Ex. 18/53: adm_w = {∅, {e}}.

## Cost

Weak semantics are PSPACE-complete already for AFs; the implementation follows the
definitions, so runtimes are exponential. State this in the Javadoc and keep test
instances at about 6 arguments or fewer. Strong admissibility is cheap apart from
enumerating subsets of the grounded extension.

## Files

- New (`org-tweetyproject-arg-setaf/.../reasoners/`): the five `Simple*SetAfReasoner` classes.
- Edited: `SetAf.java` (reduct), `AbstractSetAfExtensionReasoner.java` (factory),
  `SetafReductionReasoner.java` (`NATIVE`), SETAF and ABA tests.

## Order

1. Reduct
2. SAD
3. WAD
4. WCO / WPR / WGR
5. Wiring and tests
