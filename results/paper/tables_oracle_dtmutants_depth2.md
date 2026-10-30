### oracle_vs_normative_pseudocode

Oracle vs the literally transcribed pseudo-code / truth tables of OASIS ACAL 1.0 (acal-core-v1.0.md, Annex E, Tables 4-5); all child-decision vectors up to length 5.

| normative artefact | input vectors | mismatches |
|---|---|---|
| deny-overrides | 9331 | 0 |
| permit-overrides | 9331 | 0 |
| deny-unless-permit | 9331 | 0 |
| permit-unless-deny | 9331 | 0 |
| first-applicable | 9331 | 0 |
| table5-target-indeterminate | 6 | 0 |
| table4-rule | 6 | 0 |

### decision_table_mutants

3202 mutants: every entry of every decision table replaced by every other output (sizes: comb-rule=1010, comb-policy=2030, rule=60, tmap=60, tgt=42). Mean over 5 seeds, budget-matched suites.

| suite | comb-rule (%) | comb-policy (%) | rule (%) | tmap (%) | tgt (%) | overall (%) |
|---|---|---|---|---|---|---|
| Semantics coverage | 100.0 | 100.0 | 100.0 | 100.0 | 100.0 | 100.0 (100.0-100.0) |
| Random | 48.5 | 42.0 | 99.7 | 63.3 | 66.7 | 45.8 (43.5-47.6) |
| Rule coverage | 22.4 | 19.1 | 66.7 | 0.0 | 38.1 | 21.0 (20.5-21.8) |
| OASIS conformance suite | 17.7 | 6.9 | 66.7 | 0.0 | 57.1 | 12.0 |

### depth2_coverage

868 depth-2 goals (parent algorithm x child type/algorithm x child target x child decision); 5 seeds.

| suite | tests | depth-2 coverage (%) |
|---|---|---|
| Depth-2 suite | 3472 | 100.0 ± 0.0 |
| Depth-1 semantics-coverage suite | 5096 | 42.2 ± 0.2 |
| Random | 5096 | 52.4 ± 1.1 |
| Rule coverage | 5096 | 31.3 ± 0.6 |

### depth2_defects

B5 counts only witnesses that are wrong under every reading of the only-one-applicable subtype. Root-level defects (B7, O1) and target-order defects (O6) are outside the depth-2 goal space.

| engine | defect | depth-1 suite (seeds) | depth-2 suite (seeds) | failing depth-2 tests per seed |
|---|---|---|---|---|
| balana | B1 | 5/5 | 5/5 | 408 |
| balana | B2 | 5/5 | 5/5 | 3 |
| balana | B3 | 5/5 | 5/5 | 67 |
| balana | B4 | 5/5 | 5/5 | 272 |
| balana | B5 | 5/5 | 5/5 | 108 |
| balana | B6 | 3/5 | 5/5 | 105 |
| balana | B7 | 5/5 | 0/5 | 0 |
| balana | B8 | 5/5 | 5/5 | 165 |
| openaz | O1 | 5/5 | 0/5 | 0 |
| openaz | O2 | 5/5 | 5/5 | 391 |
| openaz | O3 | 5/5 | 5/5 | 116 |
| openaz | O5 | 5/5 | 5/5 | 167 |
| openaz | O6 | 3/5 | 0/5 | 0 |
| openaz | O7 | 0/5 | 0/5 | 0 |
| openaz | O8 | 0/5 | 0/5 | 0 |

Depth-2 suite on the 25 hand-written mutants: 23.8 killed on average; missed (seeds of 5): target_ind_over_nm (5), ooa_multi_first (1)
