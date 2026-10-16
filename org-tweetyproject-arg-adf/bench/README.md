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
./driver.sh run --instances instances/NAME --results results/NAME/results.jsonl --impls old,old-fixed,new --sem CO --timeout-s 60 --cores 0,1,2,3,4,5
./driver.sh report --instances instances/NAME --results results/NAME/results.jsonl --out results/NAME/report.md --timeout-s 60
./driver.sh check --file instances/NAME/x.kpp --sem CO --impls old-fixed,new
```

- Every run is a fresh JVM (`-Xms512m -Xmx4g -XX:+UseSerialGC -Xss64m`) pinned with `taskset` to one physical core (both SMT threads). The timeout applies to each solve. Parsing is untimed and repeated before every solve, so no solver state or cached link types carry over between repetitions.
- Runs are grouped into chains, one per implementation and family, and processed in increasing size. Once every instance of a size has timed out, the chain records larger sizes as `skipped`. Chains run in parallel on the given cores.
- `run` is resumable: runs already in the JSONL file are skipped.
- The report compares `new` with `old-fixed` (or with `old` if `old-fixed` was not run) and lists crashes of every implementation.
- Before the runners, `run` solves every instance with at most `--oracle-max` (default 10) arguments with the oracle (`Oracle`, `KppAdf`), which shares no code with either implementation: its own k++ADF parser, Kleene evaluation and Γ over completions, written from the definitions in `../docs/ARCHITECTURE.md`. Its answers are recorded as implementation `oracle`.
- The report's validation section classifies every solved run as oracle-verified, wrong, in agreement or disagreement with the other side of the reference/new pair, or unchecked. Wrong answers and disagreements also go to `mismatches.jsonl` next to the report, each with a `check` command that reproduces it.

## Instance families

`af` (D_F of random AFs), `bipolar` (each parent occurs once with fixed polarity), `general` (random formulas with ∧ ∨ ¬ → ↔ ⊕). Seeds are in `manifest.tsv`. The old parser rejects syntactically equal operands such as `or(a1,a1)`, so the generator never emits them.

## Not yet implemented (compared with plan section 4)

Tasks other than EE, cold mode, peak memory, SAT statistics and plots.
