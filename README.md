# Semantics-Coverage-Driven Conformance Testing of XACML 3.0 PDPs

Replication package for the paper *"Semantics-Coverage-Driven Conformance Testing of XACML 3.0
Policy Decision Points"* (under review).

Existing XACML test generators measure coverage over **policies** and assume the Policy Decision
Point (PDP) is correct. This artifact measures and generates tests over the **decision semantics
of the evaluator** instead: Indeterminate{D,P,DP} propagation, combining algorithms, missing and
multi-valued attributes. It contains:

* a semantic coverage criterion: 1,274 goals, one per entry of the XACML 3.0 decision
  tables, independent of policy size ([docs/THEORY.md](docs/THEORY.md));
* an SMT-based generator (Z3) that co-generates a policy and a request for each goal and
  emits it in four observation contexts that make all six extended decisions visible;
* an executable reference semantics used as test oracle;
* differential runs on two open-source PDPs (WSO2 Balana and Apache OpenAZ) with root-cause
  attribution of every discrepancy ([docs/DEFECTS.md](docs/DEFECTS.md));
* a comparison with oracle-free differential testing (cross-checking the engines, up to 10x the
  test budget), and real-policy runs on the policy benchmarks of the XACML testing literature
  (XPA: conference, fedora, iTrust up to 2,560 rules, kmarket, pluto, HL7; Margrave: CONTINUE and
  college, translated from XACML 1.0 to 3.0 and checked for equivalence);
* a batch runner for a third engine, AuthzForce CE, which the pipeline uses when it is built.

## Headline results

<!-- RESULTS:BEGIN -->
Oracle: 445/445 supported OASIS conformance tests pass (10 excluded).

| Measure | Semantics coverage | Random | Rule coverage (X-PTG style) | OASIS conformance suite |
|---|---|---|---|---|
| Semantic coverage, 1,274 goals (%) | 100.0 ± 0.0 | 35.0 ± 1.1 | 12.7 ± 0.3 | 8.2 |
| Mutation score, 25 PDP mutants | 0.999 ± 0.007 | 0.868 ± 0.026 | 0.231 ± 0.023 | 0.480 |
| Decision-table faults killed, 3,202 (%) | 100.0 (100.0-100.0) | 45.8 (43.5-47.6) | 21.0 (20.5-21.8) | 12.0 |
| Balana defects exposed (of 8) | 8 | 6 | 1 | 0 (fails 40 function-library tests) |
| OpenAZ defects exposed (of 7; O7 and O8 show only on real policies) | 5 | 4 | 0 | 0 (passes all 447) |

Semantics coverage = depth-1 suite plus the depth-2 suite; defects count if exposed in at least one of 5 seeds.

Oracle-free differential testing (Balana vs OpenAZ) as a baseline, assuming an ideal triager:

| Suite | Verdict | Tests | Tests to judge by hand | Defects found (of 13, any seed) |
|---|---|---|---|---|
| Semantics coverage | oracle | 5096 | 0 (oracle decides) | 13 |
| Semantics coverage | differential | 5096 | 2127 | 11 |
| Random | oracle | 5096 | 0 (oracle decides) | 10 |
| Random 1x | differential | 5096 | 1709 | 9 |
| Random 10x | differential | 50960 | 17333 | 10 |

Full tables: [docs/RESULTS.md](docs/RESULTS.md).
<!-- RESULTS:END -->

## Repository layout

| Path | Content |
|---|---|
| `xpdp/` | Python package: data model, XACML parser/serializer (`xmlio`), function library, reference oracle (`oracle`), coverage criterion (`coverage`, `depth2`), generator (`generator`), real-policy mode (`realmode`, `symbolic`), baselines, mutants, executable engine models (`balana_model`, `openaz_model`), XACML 1.0/2.0 to 3.0 translator (`xacml2to3`) |
| `engines/` | batch runners for Balana and OpenAZ (built with javac) and AuthzForce CE (Maven project in `engines/authzforce`), logging stub, run wrappers |
| `setup/` | `fetch_deps.sh` (pinned third-party sources and jars), `build_engines.sh`, `build_authzforce.sh` (optional third engine, needs Maven) |
| `scripts/` | one script per experiment stage (see [docs/EXPERIMENTS.md](docs/EXPERIMENTS.md)) |
| `results/` | raw results of the published run (`*.json`, engine decisions `*.tsv`) |
| `results/paper/` | every table (CSV + Markdown) and figure (PNG + PDF) used in the paper |
| `docs/` | [EXPERIMENTS.md](docs/EXPERIMENTS.md), [RESULTS.md](docs/RESULTS.md), [DEFECTS.md](docs/DEFECTS.md), [THEORY.md](docs/THEORY.md) |
| `run_all.sh` | re-runs every experiment and regenerates every table |

## Reproducing the results

Requirements: Linux or macOS, Python ≥ 3.11, JDK ≥ 17, git, about 15 GB of free disk (most of it
the 255,000 test files of the 10x random suites in `data/diff_suites`, which can be deleted after stage 19).

```bash
pip install -r requirements.txt
setup/fetch_deps.sh      # clones Balana, OpenAZ, AuthzForce, ONAP, the XPA and Margrave policy
                         # benchmarks and the XACML 3.0 schema at pinned commits
setup/build_engines.sh   # compiles Balana and OpenAZ from source with javac (no Maven needed)
setup/build_authzforce.sh       # optional: third engine, AuthzForce CE (needs Maven + Maven Central)
python3 scripts/smoke_test.py   # 1-minute installation check against the published results
./run_all.sh             # all 22 stages; about 6-7 h on 2 vCPUs
./run_all.sh 6 11        # or only some stages (see docs/EXPERIMENTS.md)
```

Every generator is seeded, `run_all.sh` sets `PYTHONHASHSEED=0` and each Z3 query runs in a fresh
context with a deterministic resource limit, so a re-run reproduces every table exactly (except
wall-clock timings). This was checked by re-running the whole pipeline from a clean checkout. The published numbers were produced with Python 3.13.16, z3-solver 5.1.0.0,
OpenJDK 21.0.12 on 2 vCPUs (Xeon 2.1 GHz, 8 GB RAM). Logs of each stage go to `logs/`.

Third-party code and data are **fetched, not redistributed**: the PDP sources, the XACML 3.0
port of the OASIS conformance tests (AuthzForce repository) and the public policies are cloned at
pinned commits; the 14 OpenAZ runtime jars come from Maven Central, or, if it is unreachable, from
the pyspark 3.5.1 source distribution on PyPI, with SHA-256 checks
(`setup/openaz-lib.sha256`).

## Using the generator on its own

```python
from xpdp.generator import generate_suite
from xpdp.xmlio import write_test

tests, stats = generate_suite(seed=0)          # 1,274 goals x 4 contexts = 5,096 tests
for t in tests[:3]:
    print(t["goal"], t["ctx"], t["expected"])    # oracle decision: P | D | NA | IND
    write_test(f"out/{t['id']}", t["root"], t["request"])   # Policy.xml + Request.xml
```

To test another PDP, write a runner that reads lines `id<TAB>policy.xml<TAB>request.xml` and
writes `id<TAB>P|D|NA|IND|ERR`, as `engines/harness/BalanaRunner.java` does.

## Defects

The generated suites expose eight defects in Balana and seven in OpenAZ (two of them, O7 and O8,
only on real policies), plus one latent OpenAZ defect found by code reading. Several of them can turn a request that must be denied into **Permit**.
Details, source locations and minimal witnesses: [docs/DEFECTS.md](docs/DEFECTS.md).

> **Responsible disclosure.** Report the defects to the maintainers before making this repository
> public; record the status in `docs/DEFECTS.md`.

## Limitations

The published results cover two engines: the AuthzForce runner is included and runs automatically
once built, but it could not be built in the experiment sandbox (no access to Maven Central), so
no AuthzForce results are reported yet. The rule-coverage baseline re-implements the X-PTG
criterion rather than running the original tool; Theorem 2 is proved for depth-1 single-entry
faults. See [docs/EXPERIMENTS.md](docs/EXPERIMENTS.md#known-limitations).

## Citation

See [CITATION.cff](CITATION.cff).

## License

Code in this repository: MIT ([LICENSE](LICENSE)). Fetched third-party code and data keep their
own license (Balana, OpenAZ, AuthzForce, ONAP and xacml4j: Apache-2.0; the XPA and Margrave
benchmark policies are used as published by their authors and are not redistributed).
