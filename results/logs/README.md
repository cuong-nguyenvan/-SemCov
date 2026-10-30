# Logs of the published run

`run_all.txt` and the per-stage logs in this folder come from the run that produced every file in
`results/`: a complete `./run_all.sh` (stages 0-21) from empty `results/` and `data/` directories,
started on 2026-10-11 at 11:44 and finished at 18:03 (2 vCPUs, 8 GB RAM; AuthzForce not built, so
stage 18 was skipped). It was run after all result files, scripts and tables had been given
descriptive names (see the mapping below).

* Stage 5 (generation cost) was re-run alone right after the pipeline (`5-generation-cost-rerun.log`,
  18:04-18:23) because its wall-clock timings are sensitive to load; `results/generation_cost_vs_depth.json`
  holds the re-run, and stage 21 regenerated the tables. All non-timing values of the two runs are
  identical.
* Wall-clock timings depend on the cloud host: the same idle stage-5 run took 12.8-72.9 s for
  depth 1-5 on 2026-10-10 and 34.4-146.3 s on 2026-10-11. Only timings differ between the runs.
* Every non-timing result is identical to the earlier runs in `earlier_runs/` (checked file by
  file; the only other differences are the suite label `ours` -> `semcov` and absolute paths in
  `*.err` files and `ct_list.tsv`).

## Earlier runs

* `earlier_runs/2026-10-10_first/`: first complete run from a clean checkout (stages 0-17 and
  tables). Stage 7 first failed because `data/suites` did not exist (fixed in
  `run_balana_suites.py`) and was resumed; stage 5 was re-run alone; witnesses were re-extracted
  after stage 17 (now automatic).
* `earlier_runs/2026-10-11_extension/`: stages re-run after adding the AuthzForce runner, the
  differential-testing stage and the policy benchmarks. The string encoding of real-policy mode
  changed (finite token set for equality-only policies; public-policy coverage 93.1% -> 95.0%, no
  solver-limit cases, same defects), and the OpenAZ model gained O8 (found on the HL7 benchmark).
  The first stage-20 attempts were stopped after two performance changes and restarted.

## Names used in the earlier logs

Before the published run, result files, scripts, stages and tables were renamed so that each name describes
its content (no references to the structure of a paper); the contents of the result files were
not changed apart from the suite label `ours`, which became `semcov` (semantics-coverage suite),
in file names, JSON keys and test ids. The logs keep the names that were current when they were
written. Mapping of the old names that appear in them:

| Old | New |
|---|---|
| stage `rq12`, `scripts/run_rq12.py`, `results/rq12.json` | `coverage-mutants`, `scripts/run_coverage_and_mutants.py`, `results/semantic_coverage_mutants.json` |
| stage `rq5`, `scripts/run_rq5.py`, `results/rq5.json` | `generation-cost`, `scripts/run_generation_cost.py`, `results/generation_cost_vs_depth.json` |
| `results/dtmut.json`, `results/spec_check.json`, `results/depth2.json` | `decision_table_mutants.json`, `oracle_vs_pseudocode.json`, `depth2_coverage.json` |
| `scripts/analyze.py`, `analyze2.py`, `analyze3.py`, `analyze4.py` | `tables_generated_suites.py`, `tables_engines_and_public_policies.py`, `tables_oracle_dtmutants_depth2.py`, `tables_differential_and_benchmarks.py` |
| `results/paper/T<n>_...` tables, `fig_rq5_scalability` | descriptive names (see docs/EXPERIMENTS.md), `fig_generation_cost_vs_depth` |
| stage-20 tag `bench` (`bench_*.tsv`, `benchpolicies.json`, `bench_translation.json`) | `benchmark` (`benchmark_*.tsv`, `benchmarkpolicies.json`, `benchmark_translation.json`) |
| suite label `ours` (`*_ours_*`, `real-ours-*`) | `semcov` |
| `18-tables.log` (stage 18 of the first run) | `18-tables-first-run.log` (tables are now stage 21) |
