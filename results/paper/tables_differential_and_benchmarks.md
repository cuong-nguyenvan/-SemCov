### differential_vs_oracle

Two engines (Balana, OpenAZ), 5 seeds. 'oracle': a test fails when an engine differs from the reference oracle. 'differential': a test is flagged when the engines disagree; no oracle. 'differential + crash' also flags tests on which an engine gives no response (an implicit oracle). For differential testing a defect counts as found if a flagged test is explained by it, i.e. assuming an ideal triager who always identifies the faulty engine; with two engines every flagged test has to be judged by hand. 13 defects are reachable on generated policies (O4 is latent; O7 and O8 show only on real policies).

| suite | verdict | tests | tests needing manual judgement (mean ± sd) | defects found per seed (mean ± sd) | defects found in ≥1 seed (of 13) | never found |
|---|---|---|---|---|---|---|
| Semantics coverage | oracle | 5096 | 0 (oracle decides) | 12.2 ± 1.1 | 13 | - |
| Semantics coverage | differential | 5096 | 2127 ± 15 | 10.0 ± 1.0 | 11 | B7, B8 |
| Semantics coverage | differential + crash | 5096 | 2425 ± 17 | 11.2 ± 1.1 | 12 | B7 |
| Rule coverage | oracle | 5096 | 0 (oracle decides) | 1.0 ± 0.0 | 1 | B1, B2, B3, B4, B6, B7, B8, O1, O2, O3, O5, O6 |
| Rule coverage | differential | 5096 | 61 ± 10 | 1.0 ± 0.0 | 1 | B1, B2, B3, B4, B6, B7, B8, O1, O2, O3, O5, O6 |
| Random | oracle | 5096 | 0 (oracle decides) | 9.4 ± 0.9 | 10 | B2, B8, O5 |
| Random 1x | differential | 5096 | 1709 ± 61 | 8.4 ± 0.5 | 9 | B2, B7, B8, O5 |
| Random 2x | differential | 10192 | 3525 ± 143 | 9.2 ± 0.4 | 10 | B7, B8, O5 |
| Random 5x | differential | 25480 | 8653 ± 312 | 9.4 ± 0.5 | 10 | B7, B8, O5 |
| Random 10x | differential | 50960 | 17333 ± 381 | 9.6 ± 0.5 | 10 | B7, B8, O5 |
| Random 10x | differential + crash | 50960 | 17333 ± 381 | 9.6 ± 0.5 | 10 | B7, B8, O5 |

### common_mode_failures

Tests on which Balana and OpenAZ return the same wrong decision; invisible to differential testing. For example, B7 (Balana) and O2 (OpenAZ) both return Indeterminate for a root policy whose target is Indeterminate and whose rules give NotApplicable, and B8 and O5 both crash on an invalid regular expression (visible as a crash, but not as a disagreement).

| suite | common-mode failing tests per seed | defects involved (seeds) |
|---|---|---|
| semcov | 321 ± 10 | B3 (2/5), B7 (5/5), B8 (5/5), O2 (5/5), O3 (2/5), O5 (5/5) |
| random 1x | 5 ± 4 | B6 (3/5), B7 (4/5), O2 (4/5) |
| random 10x | 62 ± 15 | B1 (4/5), B6 (5/5), B7 (5/5), O2 (5/5), O3 (1/5) |

### benchmark_policies_coverage

Real-policy mode (goal instances whose abstract goal is already covered are skipped) on the policy benchmarks of the XACML testing literature; random is budget-matched to the semantics-coverage suite. Over the 24 policies where rule coverage was run (≤ 400 rules), the semantics-coverage suite reaches 97.7% of the goals reached by any suite, rule coverage 38.7%, random 57.7%; it is ≥ both baselines on 24/24 policies.

| policy | family | rules | nodes | tests semantics cov. | tests rule cov. | goals semantics cov. | goals rule cov. | goals random (mean of 5) | goals reached by any suite | time semantics cov. (s) | time rule cov. (s) |
|---|---|---|---|---|---|---|---|---|---|---|---|
| HL7 | XPA | 19 | 11 | 52 | 19 | 36 | 26 | 23.0 | 36 | 1.4 | 0.4 |
| Simpson1 | XPA | 4 | 1 | 40 | 4 | 29 | 13 | 21.4 | 30 | 0.3 | 0.0 |
| Simpson2 | XPA | 4 | 1 | 72 | 4 | 43 | 15 | 29.2 | 43 | 0.3 | 0.0 |
| conference3-dup | XPA | 15 | 1 | 16 | 15 | 27 | 12 | 18.4 | 28 | 0.7 | 0.1 |
| conference3-fa-wdeny | XPA | 15 | 1 | 24 | 15 | 37 | 14 | 22.0 | 38 | 0.4 | 0.2 |
| conference3-pud | XPA | 15 | 1 | 16 | 15 | 25 | 12 | 18.4 | 26 | 0.8 | 0.2 |
| conference3 | XPA | 15 | 1 | 16 | 15 | 30 | 12 | 18.4 | 31 | 0.8 | 0.2 |
| fedora-rule3-po | XPA | 2 | 1 | 24 | 2 | 39 | 8 | 25.4 | 40 | 0.2 | 0.0 |
| fedora-rule3 | XPA | 12 | 1 | 72 | 12 | 61 | 17 | 36.8 | 62 | 1.4 | 0.3 |
| itrust3-10 | XPA | 640 | 1 | 20 | - | 32 | - | 12.2 | 32 | 211.2 | - |
| itrust3-20 | XPA | 1280 | 1 | 20 | - | 32 | - | 12.2 | 32 | 875.0 | - |
| itrust3-40 | XPA | 2560 | 1 | 16 | - | 31 | - | 11.0 | 32 | 3525.1 | - |
| itrust3-5 | XPA | 320 | 1 | 20 | 320 | 32 | 11 | 13.2 | 32 | 56.1 | 28.2 |
| itrust3 | XPA | 64 | 1 | 20 | 64 | 32 | 11 | 13.0 | 32 | 3.3 | 1.7 |
| kmarket-blue-policy | XPA | 4 | 1 | 60 | 4 | 55 | 14 | 29.6 | 55 | 0.4 | 0.0 |
| kmarket-gold-policy | XPA | 3 | 1 | 56 | 3 | 51 | 12 | 29.4 | 52 | 0.2 | 0.1 |
| kmarket-sliver-policy | XPA | 5 | 1 | 52 | 5 | 51 | 13 | 28.4 | 52 | 0.3 | 0.0 |
| obligation3 | XPA | 2 | 1 | 28 | 2 | 34 | 4 | 22.8 | 39 | 29.3 | 1.5 |
| pluto3 | XPA | 21 | 1 | 16 | 21 | 32 | 13 | 17.6 | 33 | 4.1 | 1.6 |
| continue-a | Margrave/CONTINUE | 298 | 377 | 20 | 298 | 33 | 22 | 19.2 | 33 | 85.2 | 27.0 |
| continue-b | Margrave/CONTINUE | 306 | 377 | 20 | 306 | 34 | 23 | 19.2 | 34 | 81.2 | 26.3 |
| college-a | Margrave/college | 2 | 7 | 8 | 2 | 23 | 12 | 12.4 | 24 | 0.4 | 0.1 |
| college-b | Margrave/college | 3 | 10 | 8 | 3 | 23 | 12 | 15.0 | 24 | 0.6 | 0.1 |
| college-c | Margrave/college | 4 | 12 | 4 | 4 | 21 | 16 | 12.8 | 21 | 1.0 | 0.1 |
| college-d | Margrave/college | 5 | 16 | 4 | 5 | 21 | 16 | 11.0 | 21 | 1.5 | 0.1 |
| mg1-simple-policy | Margrave/examples | 2 | 3 | 12 | 2 | 23 | 11 | 13.0 | 24 | 0.2 | 0.0 |
| sun-cond-1 | Margrave/examples | 1 | 1 | 20 | 1 | 27 | 5 | 13.6 | 28 | 0.1 | 0.0 |

### skip_covered_ablation

Skipping goal instances whose goal is already covered, on the benchmark policies with ≤ 64 rules.

| policy | tests (skip) | tests (all instances) | goals (skip) | goals (all instances) | goals lost by skipping | time skip (s) | time all (s) |
|---|---|---|---|---|---|---|---|
| HL7 | 52 | 316 | 36 | 36 | 0 | 1.4 | 1.8 |
| Simpson1 | 40 | 72 | 29 | 29 | 0 | 0.3 | 0.3 |
| Simpson2 | 72 | 96 | 43 | 43 | 0 | 0.3 | 0.4 |
| conference3-dup | 16 | 168 | 27 | 28 | 1 | 0.7 | 1.2 |
| conference3-fa-wdeny | 24 | 172 | 37 | 38 | 1 | 0.4 | 0.7 |
| conference3-pud | 16 | 168 | 25 | 26 | 1 | 0.8 | 1.0 |
| conference3 | 16 | 168 | 30 | 31 | 1 | 0.8 | 1.3 |
| fedora-rule3-po | 24 | 56 | 39 | 40 | 1 | 0.2 | 0.4 |
| fedora-rule3 | 72 | 212 | 61 | 62 | 1 | 1.4 | 2.3 |
| itrust3 | 20 | 756 | 32 | 32 | 0 | 3.3 | 8.9 |
| kmarket-blue-policy | 60 | 104 | 55 | 55 | 0 | 0.4 | 0.4 |
| kmarket-gold-policy | 56 | 88 | 51 | 51 | 0 | 0.2 | 0.3 |
| kmarket-sliver-policy | 52 | 112 | 51 | 51 | 0 | 0.3 | 0.4 |
| obligation3 | 28 | 40 | 34 | 35 | 1 | 29.3 | 41.2 |
| pluto3 | 16 | 260 | 32 | 34 | 2 | 4.1 | 8.0 |
| college-a | 8 | 84 | 23 | 24 | 1 | 0.4 | 0.5 |
| college-b | 8 | 116 | 23 | 24 | 1 | 0.6 | 1.1 |
| college-c | 4 | 144 | 21 | 24 | 3 | 1.0 | 1.7 |
| college-d | 4 | 188 | 21 | 24 | 3 | 1.5 | 1.6 |
| mg1-simple-policy | 12 | 52 | 23 | 24 | 1 | 0.2 | 0.5 |
| sun-cond-1 | 20 | 32 | 27 | 27 | 0 | 0.1 | 0.1 |
| total | 620 | 3404 | 720 | 738 | 18 | 47.6 | 74.1 |

### benchmark_policies_engine_runs

Differential runs of the benchmark suites, attributed by bug-model replay (minimal defect subset whose engine model reproduces the decision).

| engine | suite | tests | discrepancies | defects (policies affected) | unexplained |
|---|---|---|---|---|---|
| balana | random | 736 | 5 | B3 (1) | 0 |
| balana | rulecov | 1141 | 0 | - | 0 |
| balana | semcov | 736 | 61 | B1 (4), B3 (1), B4 (4) | 0 |
| openaz | random | 736 | 86 | O1 (5), O3 (1), O8 (1) | 0 |
| openaz | rulecov | 1141 | 6 | O1 (1), O8 (1) | 0 |
| openaz | semcov | 736 | 72 | O1 (5), O2 (4), O3 (10), O8 (1) | 0 |

### xacml1_to_xacml3_translation_equivalence

Each translated policy and its inlined XACML 1.0 original evaluated on Balana with the same 300 random requests. Requests with several resource-id values are Multiple Resource Profile requests in XACML 2.0 and are not compared. Rejected sources: xpa__gpms: AttributeSelector (XPath); margrave__generated: ordered-permit-overrides is not defined in XACML 1.0 (introduced in 1.1)

| policy (XACML 1.0 source) | requests compared | not comparable | Balana 1.0 = Balana 3.0 | Balana 1.0 = oracle on 3.0 | decision mix |
|---|---|---|---|---|---|
| continue-a | 300 | 0 | 300 | 300 | D=149, NA=128, P=23 |
| continue-b | 300 | 0 | 300 | 300 | D=149, NA=128, P=23 |
| college-a | 300 | 0 | 300 | 300 | NA=280, P=20 |
| college-b | 300 | 0 | 300 | 300 | NA=285, P=15 |
| college-c | 300 | 0 | 300 | 300 | NA=290, P=10 |
| college-d | 300 | 0 | 300 | 300 | NA=282, P=18 |
| mg1-simple-policy | 300 | 0 | 300 | 300 | NA=280, P=20 |
| sun-cond-1 | 272 | 28 | 272 | 272 | IND=17, NA=255 |
