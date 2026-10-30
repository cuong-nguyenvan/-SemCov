# Defects found in the PDPs under test

Engines and versions:

* **WSO2 Balana**, commit `4748417` (8 Oct 2025, latest at the time of the experiments).
* **Apache OpenAZ** (the AT&T XACML 3.0 engine donated to Apache), commit `d85056d`, the last
  commit before the project was retired (2016). Derived code still ships in other products
  (ONAP uses the AT&T engine).

Every defect below is attributed by *bug-model replay*. Each defect is a switch in an executable
model of the engine (`xpdp/balana_model.py`, `xpdp/openaz_model.py`). A failing test is explained
by the smallest set of switches that reproduces the engine's decision. With all switches on, the
models reproduce the engines' decisions on every generated test (76,440 per engine for the main
suites). With all switches off, they coincide with the reference oracle. Line numbers refer to the
pinned commits.

Minimal witnesses (policy, request, oracle decision, engine decision) for every defect are in
[`results/paper/witnesses.json`](../results/paper/witnesses.json).

## Balana

| ID | Location | Defect | Spec rule violated | Class | Impact observed |
|---|---|---|---|---|---|
| B1 | `combine/xacml3/DenyOverridesPolicyAlg.java:112` | a child policy whose target is Indeterminate makes the policy set return **Deny** immediately (the line carries `//TODO do we really want this?`) | Indeterminate target → value from the child's rules (XACML 3.0, "Policy set value for Indeterminate target"); Appendix C deny-overrides | F4 | legitimate requests denied; Indeterminate/NA becomes Deny |
| B2 | `combine/xacml3/DenyOverridesPolicyAlg.java:172` | `atLeastOneErrorD && (atLeastOneErrorP && atLeastOnePermit)` instead of `\|\|` | Appendix C deny-overrides: Ind{D} with Permit or Ind{P} gives Ind{DP} | F1 | wrong Indeterminate subtype; Deny instead of Indeterminate in a permit-overrides parent |
| B3 | `combine/xacml3/PermitOverridesRuleAlg.java:159` (missing branch) | no `Ind{D}` branch: an erroring Deny rule is reported as NotApplicable | Appendix C permit-overrides | F1 | **unauthorized Permit**: the Deny branch's error is lost and a sibling Permit wins |
| B4 | `combine/xacml3/PermitOverridesPolicyAlg.java:111` | a child policy with Indeterminate target is skipped | as B1 | F4 | **unauthorized Permit**; errors hidden as NotApplicable |
| B5 | `combine/xacml2/FirstApplicablePolicyAlg.java:111`, `combine/xacml2/OnlyOneApplicablePolicyAlg.java:118` and the XACML 3.0 parents (e.g. `DenyOverridesPolicyAlg.java:139-162`: no case for `DECISION_INDETERMINATE`) | the XACML 2.0 algorithms return a plain `DECISION_INDETERMINATE`, which the 3.0 deny-/permit-overrides algorithms recognise as none of Ind{D}/Ind{P}/Ind{DP} and silently ignore | Indeterminate must propagate (Appendix C) | F4 | **unauthorized Permit**; errors hidden as NotApplicable |
| B6 | `combine/xacml2/FirstApplicablePolicyAlg.java:111` | child target Indeterminate returns Indeterminate even when the child's rules combine to NotApplicable (the algorithm should continue) | Indeterminate target table; Appendix C first-applicable | F4 | **unauthorized Permit** (with B5); spurious Indeterminate |
| B7 | `finder/impl/FileBasedPolicyFinderModule.java:110` | root policy with Indeterminate target returns Indeterminate even when its rules give NotApplicable | Indeterminate target table | F4 | spurious Indeterminate |
| B8 | `cond/MatchFunction.java:415` | invalid regular expression throws an uncaught `PatternSyntaxException`; no Response is produced | functions must return Indeterminate on error | F3 | no response (PEP gets an exception) |

The OASIS conformance suite reveals none of B1-B8. It does reveal 40 failures in Balana's function
library (table `engines_on_conformance_tests`): unsupported XACML 3.0 identifiers for duration functions, date arithmetic and
`map`; argument-signature errors for `anyURI-*` and `string-substring`; `ipAddress` attributes;
and one wrong `any-of-any` result (IIC166).

## OpenAZ

| ID | Location (`openaz-xacml-pdp/.../pdp/`) | Defect | Class | Impact observed |
|---|---|---|---|---|
| O1 | `std/StdPolicyFinder.java:74` | root target NoMatch gives Indeterminate ("No matching root policy found") instead of NotApplicable | F4 | spurious Indeterminate |
| O2 | `policy/Policy.java:291`, `policy/PolicySet.java:213` | target Indeterminate gives Indeterminate{DP} without evaluating the children (Indeterminate-target table not applied) | F4 | Permit lost; unauthorized Permit in nested contexts |
| O3 | `policy/Rule.java:197, 221` | rule target/condition Indeterminate gives Indeterminate{DP}, not Ind{P}/Ind{D} by effect | F4 | as O2 |
| O4 | `std/combiners/PermitOverrides.java:115` | `firstIndeterminateD.getStatus()` when only Ind{P} and Deny occurred: NullPointerException. *Latent*: unreachable while O3 holds; found by code reading only | F1 | - |
| O5 | `std/functions/FunctionDefinitionRegexpMatch.java:120` | invalid regular expression: uncaught `PatternSyntaxException` | F3 | no response |
| O6 | `policy/Target.java:128-129` | returns the first non-Match AnyOf result, so [Indeterminate, NoMatch] gives Indeterminate (NoMatch must dominate); order dependent | F2 | wrong decision |
| O7 | `std/functions/FunctionDefinitionRegexpMatch.java:120` | `String.matches` (whole-string) instead of the substring semantics of `xf:matches` | F3 | wrong decision on real policies |
| O8 | `std/functions/FunctionDefinitionHigherOrderBag.java:309-316` | `any-of-any` with an empty bag argument returns a processing error ("Bag is empty at index i"), deliberately and checked by OpenAZ's own unit test, instead of False (XACML 3.0 A.3.12: True iff some combination satisfies the predicate) | F3 | Deny/Permit becomes Indeterminate; found on the HL7 benchmark policy (real-policy mode) |

OpenAZ passes all 447 runnable OASIS conformance tests.

## Shared failure modes

Some defects of the two engines fail in the same way on the same input, so that the engines agree
on a wrong decision and cross-checking them cannot reveal the defect (common-mode failures,
table `common_mode_failures`): B7 and O2 both return Indeterminate for a root policy whose target is Indeterminate and whose
rules give NotApplicable, and B8 and O5 both crash on an invalid regular expression. B7 is never
found by oracle-free differential testing in any seed, even with 10 times the test budget of the semantics-coverage suite (table `differential_vs_oracle`).

## Spec interpretation

The only-one-applicable pseudo-code (Appendix C) says "Indeterminate" without a subtype. The
oracle uses Ind{DP}, as AuthzForce does. A B5 witness is counted only if the engine's decision is
wrong under **every** subtype reading (`scripts/ooa_sensitivity.py`,
`scripts/d2_b5_sensitivity.py`).

## Disclosure

B3, B4, B5, B6, O2 and O3 can turn a request that must not be permitted into Permit. Before making
this repository public, report the Balana defects to WSO2 through its security reporting process
and wait for an acknowledgement or a fix. OpenAZ is retired; consider notifying the maintainers of
maintained forks. Record the disclosure status here:

| Defect | Reported to | Date | Status |
|---|---|---|---|
| B1-B8 | WSO2 | _todo_ | _todo_ |
| O1-O8 | forks of the AT&T engine (e.g. ONAP) | _todo_ | _todo_ |
