### goal_space

| family | level | algorithm | raw goals | reduced goals | reduction |
|---|---|---|---|---|---|
| rule | - | - | 24 | 12 | 50.0% |
| comb | rule | DO | 96 | 65 | 32.3% |
| comb | rule | PO | 96 | 65 | 32.3% |
| comb | rule | ODO | 96 | 65 | 32.3% |
| comb | rule | OPO | 96 | 65 | 32.3% |
| comb | rule | DUP | 96 | 65 | 32.3% |
| comb | rule | PUD | 96 | 65 | 32.3% |
| comb | rule | FA | 978 | 21 | 97.9% |
| comb | policy | DO | 192 | 129 | 32.8% |
| comb | policy | PO | 192 | 129 | 32.8% |
| comb | policy | ODO | 192 | 129 | 32.8% |
| comb | policy | OPO | 192 | 129 | 32.8% |
| comb | policy | DUP | 192 | 129 | 32.8% |
| comb | policy | PUD | 192 | 129 | 32.8% |
| comb | policy | FA | 5871 | 25 | 99.6% |
| comb | policy | OOA | 21 | 21 | 0.0% |
| src | - | - | 16 | 10 | 37.5% |
| tgt | - | - | 21 | 21 | 0.0% |
| total |  |  | 8659 | 1274 | 85.3% |

### oracle_vs_conformance_tests

Reference oracle vs expected responses of the 455 mandatory XACML 3.0 conformance tests.

| conformance tests | supported | pass | fail | excluded |
|---|---|---|---|---|
| 455 | 445 | 445 | 0 | 10 |

### semantic_coverage_by_suite

Mean over 30 seeds (budget-matched); the conformance suite is fixed. Family sizes: rule=12, comb-rule=411, comb-policy=820, src=10, tgt=21

| suite | tests | rule (%) | comb-rule (%) | comb-policy (%) | src (%) | tgt (%) | overall (%) |
|---|---|---|---|---|---|---|---|
| Semantics coverage | 5096 | 100.0 | 100.0 | 100.0 | 100.0 | 100.0 | 100.0 ± 0.0 |
| Random | 5096 | 100.0 | 42.8 | 29.2 | 50.0 | 66.7 | 35.0 ± 1.1 |
| Rule coverage (X-PTG style) | 5096 | 66.7 | 13.1 | 11.2 | 0.0 | 38.1 | 12.7 ± 0.3 |
| OASIS conformance suite | 445 | 83.3 | 10.9 | 3.8 | 40.0 | 66.7 | 8.2 |

### mutation_score_by_suite

25 PDP mutants (F1-F4), 30 seeds.

| suite | mutation score (mean ± sd) | range |
|---|---|---|
| Semantics coverage | 0.999 ± 0.007 | 0.96-1.00 |
| Random | 0.868 ± 0.026 | 0.80-0.92 |
| Rule coverage (X-PTG style) | 0.231 ± 0.023 | 0.16-0.24 |
| OASIS conformance suite | 0.480 | - |

### statistical_tests

Paired Wilcoxon signed-rank test over 30 seeds; Vargha-Delaney A12.

| metric | comparison | Wilcoxon p | A12 | effect size |
|---|---|---|---|---|
| coverage | semantics coverage vs Random | 1.72e-06 | 1.000 | large |
| coverage | semantics coverage vs Rule coverage (X-PTG style) | 1.67e-06 | 1.000 | large |
| mutation_score | semantics coverage vs Random | 6.87e-07 | 1.000 | large |
| mutation_score | semantics coverage vs Rule coverage (X-PTG style) | 3.34e-07 | 1.000 | large |

### kills_per_mutant

| mutant | class | description | semantics coverage (/30) | random (/30) | rule cov (/30) | OASIS CT |
|---|---|---|---|---|---|---|
| do_errD_permit | F1 | deny-overrides: Ind{D} with Permit/Ind{P} returns Ind{D} instead of Ind{DP} | 30 | 26 | 0 | no |
| do_permit_first | F1 | deny-overrides: Permit checked before pending Indeterminate flags | 30 | 30 | 29 | no |
| do_dp_as_d | F1 | deny-overrides: Ind{DP} child treated as Ind{D} | 30 | 3 | 0 | no |
| po_errP_deny | F1 | permit-overrides: Ind{P} with Deny/Ind{D} returns Ind{P} instead of Ind{DP} | 30 | 24 | 0 | no |
| po_ignore_ip | F1 | permit-overrides: Ind{P} child ignored (treated as NotApplicable) | 30 | 30 | 0 | yes |
| dup_ind | F1 | deny-unless-permit: Indeterminate child propagated instead of Deny | 30 | 30 | 28 | yes |
| pud_ind | F1 | permit-unless-deny: Indeterminate child propagated instead of Permit | 30 | 30 | 29 | yes |
| fa_continue_ind | F1 | first-applicable: continues after an Indeterminate child | 30 | 30 | 28 | yes |
| fa_ind_dp | F1 | first-applicable: returns Ind{DP} instead of the child's Indeterminate | 30 | 30 | 0 | no |
| ooa_ind_ignored | F1 | only-one-applicable: Indeterminate child target treated as NoMatch | 29 | 30 | 0 | no |
| ooa_multi_first | F1 | only-one-applicable: >1 applicable returns first applicable | 30 | 30 | 30 | yes |
| odo_legacy | F1 | ordered-deny-overrides implemented with XACML 1.0 semantics | 30 | 30 | 29 | yes |
| anyof_ind_nm | F2 | AnyOf: Indeterminate AllOf treated as NoMatch | 30 | 30 | 0 | yes |
| allof_ind_nm | F2 | AllOf: Indeterminate Match treated as NoMatch | 30 | 30 | 0 | yes |
| target_ind_over_nm | F2 | Target: Indeterminate AnyOf dominates NoMatch | 30 | 30 | 0 | no |
| mbp_ignored | F3 | MustBePresent ignored (missing attribute gives empty bag) | 30 | 30 | 0 | yes |
| oao_multi_first | F3 | *-one-and-only on a multi-valued bag returns the first value | 30 | 30 | 0 | yes |
| oao_empty_false | F3 | *-one-and-only on an empty bag makes the condition false | 30 | 30 | 0 | yes |
| div0_zero | F3 | division by zero returns 0 | 30 | 0 | 0 | no |
| regex_false | F3 | invalid regular expression evaluates to false | 30 | 0 | 0 | no |
| pol_tind_dp | F4 | Policy target Indeterminate always gives Ind{DP} (XACML 2.0 behaviour) | 30 | 30 | 0 | no |
| pol_tind_na | F4 | Policy target Indeterminate gives NotApplicable | 30 | 30 | 0 | no |
| ps_tind_dp | F4 | PolicySet target Indeterminate always gives Ind{DP} | 30 | 28 | 0 | no |
| rule_cind_dp | F4 | Rule condition Indeterminate gives Ind{DP} instead of Ind{effect} | 30 | 30 | 0 | no |
| rule_tind_na | F4 | Rule target Indeterminate gives NotApplicable | 30 | 30 | 0 | yes |

Spearman correlation between semantic coverage and mutation score over 90 suite instances: rho = 0.944, p = 2.90e-44

### baseline_budget_saturation

5 seeds per cell; the semantics-coverage suite reaches 100% coverage and MS 0.999 at 1x.

| baseline | budget (x semantics-coverage suite) | tests | coverage (%) | mutation score |
|---|---|---|---|---|
| random | 1 | 5096 | 34.7 | 0.888 |
| random | 2 | 10192 | 42.7 | 0.888 |
| random | 5 | 25480 | 53.8 | 0.896 |
| random | 10 | 50960 | 61.6 | 0.920 |
| rulecov | 1 | 5096 | 12.5 | 0.232 |
| rulecov | 2 | 10192 | 13.3 | 0.240 |
| rulecov | 5 | 25480 | 13.8 | 0.240 |
| rulecov | 10 | 50960 | 13.8 | 0.240 |

### generation_cost_vs_depth

Mean of 3 seeds; 1,274 goals per run.

| depth k | generation time (s) | ms per goal | Z3 calls | UNSAT (%) | goals not reached | coverage (%) | oracle ms/test |
|---|---|---|---|---|---|---|---|
| 1 | 34.4 | 27.0 | 2270 | 43.9 | 0.0 | 100.00 | 0.121 |
| 2 | 43.4 | 34.1 | 3342 | 61.9 | 0.0 | 100.00 | 0.144 |
| 3 | 63.3 | 49.7 | 5343 | 76.1 | 0.3 | 99.97 | 0.203 |
| 4 | 92.2 | 72.4 | 8542 | 85.1 | 5.7 | 99.56 | 0.205 |
| 5 | 146.3 | 114.8 | 13506 | 90.7 | 24.3 | 98.09 | 0.324 |
