# Experiments

All numbers come from `./run_all.sh` (see [RESULTS.md](RESULTS.md), generated from
`results/paper/*.csv`). This page describes what each stage does.

## Common setup

| Item | Value |
|---|---|
| Goal space | 1,274 depth-1 semantic goals ([THEORY.md](THEORY.md)); 868 depth-2 goals |
| Our suite | one generated policy + request per goal, emitted in 4 observation contexts: 5,096 tests per seed |
| Baselines | **rule coverage** (X-PTG-style: random realistic policies, one Z3-solved request per feasible rule path, one value per attribute); **random** (same policy generator, random requests, values biased towards policy thresholds); **OASIS** conformance suite (XACML 3.0 port of the 2.0 tests, 455 mandatory tests); all generated suites are budget-matched (5,096 tests); **oracle-free differential testing** (B4: a test is flagged when the engines disagree), on every suite and on random suites of 1x, 2x, 5x and 10x the budget |
| Oracle | executable reference semantics `xpdp/oracle.py` |
| Engines | Balana `4748417` and OpenAZ `d85056d`, built from source by `setup/build_engines.sh`, driven by batch runners in `engines/harness` through each engine's standard root-policy path; optional third engine AuthzForce CE 21.2.0 (`setup/build_authzforce.sh`, Maven), configured as in AuthzForce's own conformance tests |
| Seeds | coverage and PDP mutants: 30; engine runs, decision-table mutants and depth-2: 5 (seed 0 was the pilot that motivated context d; seeds 1-4 are held out); generation cost: 3 per depth |
| Statistics | paired Wilcoxon signed-rank test, Vargha-Delaney Â12, Spearman ρ |
| Determinism | every generator is seeded; `run_all.sh` sets `PYTHONHASHSEED=0`; every Z3 solving episode starts from a fresh context (`xpdp/z3util.py`), so a test does not depend on what the process solved before; real-policy queries use Z3's deterministic resource limit (`rlimit` 30,000,000) instead of a wall-clock timeout. Re-running reproduces every table except wall-clock timings |

## Stages of `run_all.sh`

Tables are written to `results/paper/<name>.csv` (and Markdown in `results/paper/tables_*.md`);
`docs/RESULTS.md` collects all of them.

| Stage | Script | What it measures | Output (results/ and results/paper/) |
|---|---|---|---|
| 1 | `validate_oracle.py` | does the oracle agree with the expected responses of the OASIS conformance tests? | `oracle_ct_validation.json`, `oracle_vs_conformance_tests` |
| 2 | `spec_check.py` | does the oracle agree with the normative pseudo-code? (OASIS ACAL 1.0 Annex E and Tables 4-5, transcribed literally; all child vectors up to length 5) | `oracle_vs_pseudocode.json`, `oracle_vs_normative_pseudocode` |
| 3 | `run_coverage_and_mutants.py` | semantic coverage of each suite; kills of 25 hand-written PDP mutants (F1-F4); 30 seeds | `semantic_coverage_mutants.json`, `goal_space`, `semantic_coverage_by_suite`, `mutation_score_by_suite`, `statistical_tests`, `kills_per_mutant` |
| 4 | `run_saturation.py` | do the baselines catch up with 1x-10x the budget? | `saturation.json`, `baseline_budget_saturation`, `fig_budget_saturation` |
| 5 | `run_generation_cost.py` | generation cost vs nesting depth 1-5 (wall-clock timings: run it on an idle machine) | `generation_cost_vs_depth.json`, `generation_cost_vs_depth`, `fig_generation_cost_vs_depth` |
| 6 | `run_ct_engines.py` | OASIS conformance tests on both engines | `{balana,openaz}_ct.tsv`, `engines_on_conformance_tests` |
| 7-8 | `run_balana_suites.py`, `run_openaz_suites.py` | engine decisions on the generated suites: 3 suites x 5 seeds x 2 engines | `{balana,openaz}_<suite>_s<seed>.tsv`, `balana_runs.json` |
| 9-10 | `attribute_bugs.py`, `ooa_sensitivity.py`, `analyze_balana.py`, `explain_engine.py` | root-cause attribution of every discrepancy by bug-model replay; only-one-applicable subtype sensitivity; outcome of each failure | `attribution.json`, `explain_*.json`, `balana_defects`, `balana_defect_impact`, `defects_exposed_per_suite`, `defect_impact_per_engine` |
| 11 | `extract_witnesses.py` | smallest unique witness per defect | `paper/witnesses.json` |
| 12 | `run_ablation.py`, `ablation_engines.py` | which design elements matter (contexts, goal families, Z3, minimisation) | `ablation` |
| 13 | `run_permutation_ext.py` | permutation closure of the target-table goals (order-dependent defects) | `permutation_ext.json`, `permutation_closure` |
| 14 | `build_corpus.py`, `run_realpolicies.py`, `analyze_realpolicies.py` | real-policy mode on the public XACML 3.0 policies | `realpolicies.json`, `real_*.tsv`, `public_policies_coverage`, `public_policies_engine_runs` |
| 15 | `check_propositions.py` | mechanised checks of the propositions and of the goal count | `paper/propositions.json` |
| 16 | `run_dtmut.py` | all 3,202 single-entry decision-table faults | `decision_table_mutants.json`, `decision_table_mutants` |
| 17 | `run_depth2.py`, `d2_b5_sensitivity.py` | depth-2 (parent-child) coverage and the defects it reveals | `depth2_coverage.json`, `d2_*.tsv`, `depth2_coverage`, `depth2_defects` |
| 18 | `run_authzforce.py` | third engine on the conformance tests, the generated suites and the depth-2 suites; skipped when AuthzForce is not built | `authzforce*.json`, `authzforce_vs_oracle`, `paper/authzforce_triage.json` |
| 19 | `run_differential.py` | oracle-free differential testing vs oracle-based testing; common-mode failures; three-engine majority when AuthzForce ran | `differential.json`, `diff/`, `differential_vs_oracle`, `common_mode_failures` (`three_engine_majority`) |
| 20 | `build_benchmarks.py`, `run_realpolicies.py --tag benchmark ...`, `analyze_realpolicies.py benchmark` | literature policy benchmarks (XPA, Margrave), XACML 1.0 to 3.0 translation and its equivalence check | `benchmark_translation.json`, `benchmarkpolicies.json`, `benchmark_*.tsv`, `benchmark_policies_coverage`, `benchmark_policies_engine_runs`, `xacml1_to_xacml3_translation_equivalence`, `skip_covered_ablation` |
| 21 | `tables_*.py`, `make_results_md.py` | tables and figures | `results/paper/`, `docs/RESULTS.md` |

## PDP mutants

`xpdp/mutants.py` lists the 25 faults injected into the evaluator, grouped by fault class:
F1 combining functions (12), F2 target evaluation (3), F3 Indeterminate sources (5), F4 propagation
(5). Because they were written by the authors, stage 16 complements them with every possible
single-entry fault of the decision tables.

## Root-cause attribution

A discrepancy (engine decision ≠ oracle decision) is attributed to the smallest set of defect
switches (up to three) whose engine model reproduces the observed decision. A run is accepted only
if the full model matches the engine on **every** test and the defect-free model matches the
oracle on every test; both hold for all suites reported. A defect counts as *detected by a suite in
a seed* if at least one failing test of that suite needs it.

## Real policies

`build_corpus.py` collects every self-contained XACML 3.0 policy in the pinned Balana, OpenAZ,
AuthzForce and ONAP repositories (conformance tests excluded, duplicates removed, policies with
references or unsupported features rejected). In real-policy mode (`xpdp/realmode.py`,
`xpdp/symbolic.py`), the policy is fixed: its semantic goals are enumerated from its own nodes and
each one is reached by an SMT query over the policy's attributes. String values are either policy
constants or fresh tokens. When every string function of the policy only compares values for
equality (51 of the 67 corpus policies and every benchmark policy), the fresh tokens come from a
finite set with one token per string variable. This keeps every equality pattern between variables
and constants, so no goal is lost, and it avoids the regular-expression theory, which made some
queries run into the solver limit. Other policies use the general token shape.

## Oracle-free differential testing

Differential testing needs no oracle: run the same input on several implementations and inspect
the inputs on which they disagree. `run_differential.py` evaluates it on the same inputs as
oracle-based testing. A defect counts as found when some flagged test is explained by it (bug-model
replay, as above). This assumes an ideal triager who always knows which engine is wrong, so the
numbers are an upper bound for differential testing; with two engines every flagged test needs
such a judgement. The variant "differential + crash" also flags tests on which an engine produced
no response, which is visible without an oracle. Tests on which both engines return the same wrong
decision (*common-mode failures*) cannot be flagged at all. When AuthzForce results exist, the
script adds majority voting over three engines.

## Policy benchmarks

`build_benchmarks.py` collects the policies used in the XACML testing literature:

* XPA experiments (Xu et al.), XACML 3.0: conference (4 variants), fedora, iTrust (64, 320, 640,
  1,280 and 2,560 rules), kmarket (3), pluto, HL7, Simpson (2), obligation; gpms is rejected
  (AttributeSelector);
* Margrave test policies (Fisler et al., ICSE 2005), XACML 1.0: CONTINUE (code versions A and B,
  the conference manager used by Martin and Xie) and college (code versions A-D), whose role and
  permission policy sets refer to each other by id, plus two small examples.

The XACML 1.0 policies are translated by `xpdp/xacml2to3.py`: references are inlined, then targets,
designators, conditions, identifiers and the legacy combining algorithms are mapped to XACML 3.0.
Every output validates against the XACML 3.0 schema. Each translation is checked for semantic
equivalence: the inlined 1.0 original and the 3.0 translation are evaluated on Balana (which
implements both versions) with the same 300 random requests, and the translation with the oracle
(`xacml1_to_xacml3_translation_equivalence`). Real-policy mode then runs as for the public policies, except that a goal instance whose abstract goal is
already covered is skipped: large policies repeat each rule-level goal once per rule. On the
policies with at most 64 rules (`skip_covered_ablation`), skipping needs 620 instead of 3,404 tests and reaches 720 of
the 738 goals reached without skipping; the 18 others were reached only incidentally by requests
generated for goal instances that skipping leaves out. Rule coverage needs one SMT query
per rule and is run up to 400 rules.

## Third engine (AuthzForce)

`setup/build_authzforce.sh` builds `engines/authzforce` with Maven: AuthzForce CE 21.2.0 (the last
release before the pinned source commit), or the pinned commit itself with
`AUTHZFORCE_FROM_SOURCE=1`. The runner has the same input/output format as the other two and
configures each policy as the single policy of a `StaticPolicyProvider`, like AuthzForce's own
conformance tests. Once it is built, stages 14 and 20 (real policies, benchmarks) and stage 19
(differential, majority voting) include it automatically; stage 18 compares it with the oracle and
groups the discrepancies (goal family, context, expected and observed decision) with the smallest
witness per group, for the same manual root-cause analysis as B1-B8 and O1-O8. AuthzForce has no
bug model, so its discrepancies are not attributed automatically.

## Known limitations

* AuthzForce, the most actively maintained engine, is supported by the pipeline but could not be
  built in the experiment sandbox (Maven Central unreachable); the published tables therefore cover
  Balana and OpenAZ.
* The rule-coverage baseline re-implements the X-PTG criterion; it is not the original tool.
* The differential-testing numbers assume an ideal triager (upper bound).
* Theorem 2 assumes the decision-table hypothesis (depth 1, one faulty entry); the depth-2 goals address depth 2.
* The LMS, VMS and ASMS policies (Mouelhi et al.) are not publicly available and are not included.
