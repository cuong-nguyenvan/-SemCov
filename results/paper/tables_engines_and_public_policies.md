### engines_on_conformance_tests

| engine | runnable OASIS tests | pass | fail |
|---|---|---|---|
| balana | 447 | 407 | 40 |
| openaz | 447 | 447 | 0 |

### defects_exposed_per_suite
Seeds (of 5) in which a suite exposes the defect. OpenAZ O4 (NullPointerException in PermitOverrides) is latent and found by code reading only.

| engine | id | class | defect | semcov | random | rule cov. |
|---|---|---|---|---|---|---|
| balana | B1 | F4 | DO policy alg: child with Indeterminate target -> Deny | 5/5 | 5/5 | 0/5 |
| balana | B2 | F1 | DO policy alg: && instead of || | 5/5 | 0/5 | 0/5 |
| balana | B3 | F1 | PO rule alg: no Ind{D} branch | 5/5 | 5/5 | 0/5 |
| balana | B4 | F4 | PO policy alg: child with Indeterminate target skipped | 5/5 | 5/5 | 0/5 |
| balana | B5 | F4 | plain Indeterminate ignored by 3.0 parents | 5/5 | 5/5 | 5/5 |
| balana | B6 | F4 | FA policy alg: target Ind -> Ind even if NA | 3/5 | 3/5 | 0/5 |
| balana | B7 | F4 | root target Ind -> Ind even if NA | 5/5 | 4/5 | 0/5 |
| balana | B8 | F3 | invalid regex: uncaught exception | 5/5 | 0/5 | 0/5 |
| openaz | O1 | F4 | root target NoMatch -> Indeterminate | 5/5 | 5/5 | 0/5 |
| openaz | O2 | F4 | policy/policy-set target Ind -> Ind{DP}, children ignored | 5/5 | 5/5 | 0/5 |
| openaz | O3 | F4 | rule target/condition Ind -> Ind{DP} (no Ind{P}/Ind{D}) | 5/5 | 5/5 | 0/5 |
| openaz | O5 | F3 | invalid regex: uncaught exception | 5/5 | 0/5 | 0/5 |
| openaz | O6 | F2 | Target returns first non-Match AnyOf (order dependent) | 3/5 | 5/5 | 0/5 |
| openaz | O7 | F3 | regexp-match anchored (String.matches) | 0/5 | 0/5 | 0/5 |
| openaz | O8 | F3 | any-of-any: empty bag -> processing error | 0/5 | 0/5 | 0/5 |

### defect_impact_per_engine
Failing tests over the 15 generated suites (5 seeds x 3 suites).

| engine | id | unauthorized Permit | Permit lost | other wrong decision | no response |
|---|---|---|---|---|---|
| balana | B1 | 0 | 1339 | 5507 | 0 |
| balana | B2 | 0 | 0 | 43 | 0 |
| balana | B3 | 63 | 1 | 117 | 0 |
| balana | B4 | 23 | 0 | 1834 | 0 |
| balana | B5 | 156 | 0 | 383 | 0 |
| balana | B6 | 4 | 2 | 15 | 0 |
| balana | B7 | 0 | 0 | 134 | 0 |
| balana | B8 | 0 | 0 | 0 | 1488 |
| openaz | O1 | 0 | 0 | 7976 | 0 |
| openaz | O2 | 2 | 1345 | 1640 | 0 |
| openaz | O3 | 4 | 316 | 286 | 0 |
| openaz | O5 | 0 | 0 | 0 | 1532 |
| openaz | O6 | 0 | 22 | 58 | 0 |
| openaz | O7 | 0 | 0 | 0 | 0 |
| openaz | O8 | 0 | 0 | 0 | 0 |

### ablation
Coverage and mutation score: mean of 30 seeds. Random values instead of Z3 missed 58.4 goals on average with 6.3x more skeleton draws. Denominators: defects reachable by synthetic suites (Balana B1-B8; OpenAZ O1-O3, O5, O6; O7 needs a regular expression and O8 a higher-order bag function; both appear only in real policies). For a, b, c the B5 witnesses depend on the only-one-applicable subtype reading.

| variant | tests | coverage (%) | mutation score | Balana defects (all 5 seeds) | OpenAZ defects (all 5 seeds) |
|---|---|---|---|---|---|
| Full suite (4 contexts) | 5096 | 100.0 | 0.999 | 7/8 | 4/5 |
| Context a only | 1274 | 100.0 | 0.939 | 5/8 | 4/5 |
| Contexts a, b, c | 3822 | 100.0 | 0.999 | 7/8 | 4/5 |
| Without policy-combining goals | 1816 | 37.1 | 0.880 | 5/8 | 4/5 |
| Without rule-combining goals | 3452 | 71.4 | 0.999 | 7/8 | 4/5 |
| Without target-table goals | 5012 | 99.1 | 0.959 | 7/8 | 4/5 |
| Without Indeterminate-source goals | 5056 | 99.8 | 0.999 | 7/8 | 4/5 |
| Without rule-table goals | 5048 | 100.0 | 0.999 | 7/8 | 4/5 |
| Greedy-minimised (coverage only) | 1181 | 100.0 | 0.937 | 5/8 | 4/5 |
| Random values instead of Z3 | 4862 | 95.4 | 0.999 | n/a | n/a |

### permutation_closure
All permutations of the target-table goals (900 tests).

| engine | seeds with a discrepancy (of 5) | model mismatches |
|---|---|---|
| balana | 0 | 0 |
| openaz | 5 | 0 |

### public_policies_coverage
64 public policies (3 skipped: non-standard functions). Goal instances are counted per policy; the union is over all suites.

| suite | tests | goal instances covered | share of union (%) |
|---|---|---|---|
| Semantics coverage | 2456 | 1531 | 95.0 |
| Rule coverage | 131 | 453 | 28.1 |
| Random (same budget) | 2456 | 901.6 | 55.9 |

### public_policies_engine_runs

| engine | suite | tests | discrepancies | unexplained | defects (policies affected) |
|---|---|---|---|---|---|
| balana | semcov | 2456 | 224 | 0 | B1: 10 pol.; B4: 10 pol.; B7: 1 pol.; load: Error parsing required attribute RuleId: 1 pol.; load: Unknown FunctionId: 1 pol. |
| openaz | semcov | 2456 | 279 | 0 | O1: 33 pol.; O2: 11 pol.; O3: 7 pol.; O7: 6 pol. |
| balana | rulecov | 131 | 4 | 0 | load: Error parsing required attribute RuleId: 1 pol.; load: Unknown FunctionId: 1 pol. |
| openaz | rulecov | 131 | 32 | 0 | O1: 11 pol.; O7: 6 pol. |
| balana | random | 2456 | 96 | 0 | B7: 1 pol.; load: Error parsing required attribute RuleId: 1 pol.; load: Unknown FunctionId: 1 pol. |
| openaz | random | 2456 | 790 | 0 | O1: 33 pol.; O2: 1 pol.; O7: 6 pol. |

### Propositions (mechanised)

- minimal context sets distinguishing all six extended decisions: ['bc']
- subtype-robust detection of a parent that ignores a plain Indeterminate: minimal ['d'], a+b+c robust = False
- closed-form goal counts match enumeration: True
- deny/permit-overrides duality and order independence: {'cases': 1555, 'violations': 0}