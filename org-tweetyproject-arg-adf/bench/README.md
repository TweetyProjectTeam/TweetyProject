# ADF evaluation harness

Old-vs-new comparison for the ADF rework (see `../docs/REWORK_PLAN.md`, section 4). This is a standalone Maven project, not part of the TweetyProject reactor.

| Module | Classpath | Role |
|---|---|---|
| `common` | – | runner protocol, JSON, canonical answer hash |
| `runner-old` | `org.tweetyproject.arg:adf:1.31-baseline` or `1.31-baseline-fixed` | frozen old implementation, native MiniSat (`--impls old` / `old-fixed`) |
| `runner-new` | `org.tweetyproject.arg:adf:1.31-SNAPSHOT` | reworked implementation (`Simple*` reasoners until phase 4) |
| `driver` | – | instance generation, supervised runs, independent brute-force oracle, reports |

## Setup

The baseline artifact is built from the tag `adf-baseline`, with the root-pom fix applied, and installed locally:

```sh
git worktree add /tmp/adf-baseline adf-baseline
cd /tmp/adf-baseline && git cherry-pick --no-commit 1f0c941d
mvn -o -pl org-tweetyproject-arg-adf package -DskipTests -Dgpg.skip -Dmaven.javadoc.skip=true
mvn install:install-file -Dfile=org-tweetyproject-arg-adf/testBuild/org.tweetyproject.arg.adf-1.31-SNAPSHOT.jar \
    -DgroupId=org.tweetyproject.arg -DartifactId=adf -Dversion=1.31-baseline -Dpackaging=jar -DgeneratePom=true
```

The hot-fixed baseline, the performance reference, is built the same way from the branch `adf-baseline-fixed` (which already contains the root-pom fix) and installed with `-Dversion=1.31-baseline-fixed`. `runner-old` copies both jars to `target/adf/`, and the driver puts the one of the selected implementation on the classpath.

Then install the current module (`mvn -pl org-tweetyproject-arg-adf install -DskipTests -Dgpg.skip`) and build the harness with `mvn -f org-tweetyproject-arg-adf/bench/pom.xml package`.

## Usage

```sh
./driver.sh generate --out instances/NAME --seed 2026 --sizes 4,6,8 --per 5
./driver.sh run --instances instances/NAME --results results/NAME/results.jsonl --impls old,old-fixed,new --sem CO --task EE --timeout-s 60 --cores 0,1,2,3,4,5
./driver.sh report --instances instances/NAME --results results/NAME/results.jsonl --out results/NAME/report.md --timeout-s 60
./driver.sh check --file instances/NAME/x.kpp --sem CO --task DS --arg a3 --impls old-fixed,new
```

Tasks (`--task`): `EE` enumerate all models, `CE` count them, `SE` find one model, `DC` / `DS` credulous / skeptical acceptance, `VE` verify an interpretation. Each `run` covers one semantics and one task; results of several runs can share one JSONL file, and the report has a section per (semantics, task). The queries are frozen per instance (`Queries`): one per instance for EE/CE/SE, three arguments for DC/DS, and for VE the all-undecided and grounded interpretations, three random interpretations and up to two oracle models. The old runner answers SE with the first enumerated model, DC/DS with `where(a).exists()` / `where(a).forAll()`, and VE by membership in the enumeration (recorded as `method`); it reports 3MOD as `unsupported`. The new runner uses `getModel`, `query` and `isModel`.

- Every run is a fresh JVM (`-Xms512m -Xmx4g -XX:+UseSerialGC -Xss64m`) pinned with `taskset` to one physical core (both SMT threads). The timeout applies to each solve. Parsing is untimed and repeated before every solve, so no solver state or cached link types carry over between repetitions.
- Runs are grouped into chains, one per implementation and family, and processed in increasing size. Once every instance of a size has timed out, the chain records larger sizes as `skipped`. Chains run in parallel on the given cores.
- `run` is resumable: runs already in the JSONL file are skipped.
- The report compares `new` with `old-fixed` (or with `old` if `old-fixed` was not run) and lists crashes of every implementation.
- Before the runners, `run` answers every query of an instance with at most `--oracle-max` (default 10) arguments with the oracle (`Oracle`, `KppAdf`), which shares no code with either implementation: its own k++ADF parser, Kleene evaluation and Γ over completions, written from the definitions in `../docs/ARCHITECTURE.md`. Its answers are recorded as implementation `oracle`. VE candidates of larger instances are checked with the oracle's verifier (`independent-check`), except PR/NA with more than 12 undecided arguments.
- The report's validation section classifies every solved run as oracle-verified, independently verified (VE answers; SE witnesses of any size), wrong, in agreement or disagreement with the other side of the reference/new pair, or unchecked. Answers are compared per task: EE by count and hash, CE by count, SE by the existence of a model (witnesses may differ), DC/DS/VE by the Boolean answer. Wrong answers and disagreements also go to `mismatches.jsonl` next to the report, each with a `check` command that reproduces it.

## Instance sets

`co-scaling` (first measurement, n = 4–20), `dev` (correctness, n = 4–12, all families) and `dev-large` (performance, af/bipolar, n = 50–200); all frozen, see `../docs/REWORK_PLAN.md`, section 4.3.

## Instance families

`af` (D_F of random AFs), `bipolar` (each parent occurs once with fixed polarity), `general` (random formulas with ∧ ∨ ¬ → ↔ ⊕). Seeds are in `manifest.tsv`. The old parser rejects syntactically equal operands such as `or(a1,a1)`, so the generator never emits them.

## Not yet implemented (compared with plan section 4)

Capped enumeration (waits for D8), cold mode, peak memory, SAT statistics and plots.
