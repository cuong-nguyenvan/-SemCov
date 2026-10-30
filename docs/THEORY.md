# Coverage criterion, propositions and proofs

This document states the definitions of the coverage criterion and the observation contexts and the results that the
experiments check mechanically. Script references are relative to the repository root.

## 1. Decision domain and observability

The XACML 3.0 decision of a rule, policy or policy set ranges over the extended domain

    𝒟 = {P, D, NA, Ind{D}, Ind{P}, Ind{DP}}

A PDP response only reveals vis(d) ∈ {P, D, NA, Ind}, where vis maps the three Indeterminate
subtypes to Ind. Rules never produce Ind{DP}, so the rule-level domain is
𝒟_R = {P, D, NA, Ind{D}, Ind{P}}.

## 2. Semantic coverage goals

A goal is one entry of one of the decision tables of the reference semantics
(`xpdp/coverage.py`, recorded by `xpdp/oracle.py`):

| Family | Goal | Count |
|---|---|---|
| rule table | (effect, τ, c), with τ the target value and c the condition value (none, T, F, Ind); for τ ∈ {NoMatch, Ind} the condition is irrelevant | 2 × (4 + 1 + 1) = 12 |
| order-independent combining (deny-/permit-overrides, their ordered variants, deny-unless-permit, permit-unless-deny) | (level, alg, τ, σ), with σ ⊆ 𝒟_level the set of child decisions; τ = NoMatch collapses to a single goal | per algorithm: 2·2^\|𝒟_level\| + 1, i.e. 65 (rule) and 129 (policy) |
| first-applicable | (level, τ, (NA seen before the deciding child, deciding decision)) plus "empty" and "all NA" | 2·(2·(\|𝒟_level\|−1) + 2) + 1, i.e. 21 (rule) and 25 (policy) |
| only-one-applicable (policy level) | (τ, outcome class) with classes empty, none, multi, target-Ind, one:d for d ∈ 𝒟 | 2·10 + 1 = 21 |
| Indeterminate sources | (source, location); sources: missing attribute, `*-one-and-only` on an empty or multi-valued bag, function error; locations: rule target/condition, policy target, policy-set target (one-and-only cannot occur in a Match) | 4 + 3·2 = 10 |
| target tables | (level, set of outcomes) for AllOf, AnyOf and Target over {Match, NoMatch, Ind} | 3 × 7 = 21 |

**Theorem 1 (finite, policy-independent goal space).** The number of goals is

    12 + 6·65 + 21 + 6·129 + 25 + 21 + 10 + 21 = 1,274,

independent of the size of any policy. Without the reductions (τ = NoMatch collapsed, first-
applicable reduced to its deciding prefix) the space has 8,659 goals; the reduction is 85.3%.
*Proof:* direct counting from the table above. `scripts/check_propositions.py` (P3) checks the
closed forms against the enumeration in `xpdp/coverage.py`.

A **depth-2 goal** (`xpdp/depth2.py`) records, for every child of a
policy-level node, (parent algorithm, child type, child algorithm, child target, child decision);
there are 868 feasible depth-2 goals.

## 3. Observation contexts

A goal node X is emitted in four contexts:

| Context | Policy set around X |
|---|---|
| a | X is the root |
| b | permit-overrides(X, deny-all) |
| c | deny-overrides(X, permit-all) |
| d | deny-overrides(X) |

Visible signatures (`results/paper/propositions.json`, P1):

| X | a | b | c |
|---|---|---|---|
| P | P | P | P |
| D | D | D | D |
| NA | NA | D | P |
| Ind{D} | Ind | D | Ind |
| Ind{P} | Ind | Ind | P |
| Ind{DP} | Ind | Ind | Ind |

**Proposition 1.** The context pair {b, c} distinguishes all six extended decisions, and it is
the only minimal subset of {a, b, c, d} that does.
*Proof:* the (b, c) columns above are pairwise distinct; every other subset of size ≤ 2 merges two
rows. Checked exhaustively by `scripts/check_propositions.py` (P1).

**Proposition 2 (subtype-robust propagation).** Suppose an implementation returns a
*subtype-less* Indeterminate that a parent deny-/permit-overrides node ignores (Balana defect B5).
For every reading of the missing subtype, only context d reveals the fault; {a, b, c} detects it
only under some readings.
*Proof:* exhaustive check (P2, P2b). This is why context d was added after the seed-0 pilot.

## 4. Fault detection

**Decision-table hypothesis H.** The implementation under test computes every node with the same
decision tables as the reference semantics, abstracted exactly as in Section 2 (the uniformity
hypothesis of Bernot, Gaudel and Marre), except for a single table entry e, where it outputs
v ≠ R(e).

**Theorem 2.** Under H, every suite that covers all 1,274 goals and exercises each goal node in
contexts a, b and c detects the fault: some test's visible decision differs from the oracle's.

*Proof.* Let X be the node of the test that realises goal e; under H, X outputs v instead of R(e).

1. *vis(v) ≠ vis(R(e)).* In context a, X is the root. For combining goals and Table-7 goals the
   root returns X's value directly. For rule and target-table goals, X is wrapped in a single-rule
   deny-overrides policy, whose output is a bijective image of the rule value (target values
   Match / NoMatch / Ind give effect / NA / Ind{effect}). The visible decision therefore differs.
2. *vis(v) = vis(R(e)) = Ind.* Then {v, R(e)} is a pair of distinct Indeterminate subtypes. By the
   signature table, context b separates the pairs (Ind{D}, Ind{P}) and (Ind{D}, Ind{DP}), and
   context c separates (Ind{D}, Ind{P}) and (Ind{P}, Ind{DP}). The parent of b uses the
   policy-level permit-overrides table and the parent of c the policy-level deny-overrides table.
   A parent can reach the faulty entry only if e lies in its own table and class(e) = {v, sibling}.
   - For b, with sibling D: class {v, D} has reference output D (v = Ind{D}), Ind{DP} (v = Ind{P})
     or Ind{DP} (v = Ind{DP}). The first contradicts R(e) ∈ Ind, the third contradicts v ≠ R(e).
     The remaining case (R(e), v) = (Ind{DP}, Ind{P}) is separated by c, whose parent uses a
     different table and is therefore fault-free.
   - For c, symmetrically, the only case is (R(e), v) = (Ind{DP}, Ind{D}), which b separates.
   - The deny-all and permit-all siblings use the rule-table entries (Deny, Match, none) and
     (Permit, Match, none) and the class {D} or {P} of rule-level deny-overrides. If e is one of
     these, then vis(v) ≠ vis(R(e)) for rule entries with a non-Indeterminate reference output,
     and case 1 applies.

   In every case at least one of b and c shows different visible decisions. ∎

**Mechanised check (`scripts/run_dtmut.py`).** All 3,202 single-entry faults (every entry of
every table replaced by every other output) are killed by the semantics-coverage suite in all 5
seeds (100%). The same faults are killed at 45.8% by random suites, 21.0% by rule-coverage suites
and 12.0% by the OASIS conformance suite.

**Scope.** Theorem 2 covers depth-1, single-entry faults. Faults that depend on the provenance of
a child's decision (for example Balana B6) are outside H; the depth-2 goals target them.
Faults that depend on the order of children (OpenAZ O6) are covered by the permutation extension.
