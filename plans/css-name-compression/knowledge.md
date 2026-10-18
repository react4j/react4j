# Knowledge ownership

Candidate classification completed before drafting specifications. Existing owners checked:
`docs/specs/css-liveness.md`, `docs/adr/0001-css-symbol-liveness.md`,
`docs/glossary/README.md`, their git history, and the active compression deferral.
No prior removed requirement conflicts with the accepted changes.

| Accepted conclusion | Canonical owner | Qualification |
| --- | --- | --- |
| Short shared names, deterministic allocation, modes, switch, prefix, reservations, reverse lookup | `docs/specs/css-names.md` | Observable contracts required of any replacement; no delivery dependencies |
| Stable owned names, authored stable globals, imported canonical identity | `docs/specs/css-names.md` | Observable names and identity, independent of implementation |
| Effective-declaration annotation semantics, selected ID imports, checked global IDs | `docs/specs/css-bindings.md` | Public source/binary authoring contracts, independently verifiable |
| ID getters activate providers; references do not establish DOM presence; equal stable spelling | Existing liveness specification | Extends its existing reachability contract |
| CSS and JS mapping guarantees and complete reverse-name inventory | `docs/specs/css-names.md` | Debugger-observable conformance, independent of map producer |
| Allocate once at the symbolic link boundary, reuse across variants | Existing symbolic-compilation ADR | Same boundary and alternatives; extend its rationale rather than duplicate it |
| Meaning of managed and stable symbols and shared assignments | Existing glossary | Domain language only |
| Exact token attribution; JS-to-authored-CSS mapping | Two requested deferred issues | Independent nonblocking questions with user-approved triggers |
| Parser edits, schema, define consumption, J2CL source discovery | Plan/code | Replaceable implementation choices fail the specification replacement test |
| Test sizes, runtime investigation, coverage thresholds, review and delivery evidence | Tasks/code/config | Verification and delivery ownership |
| Existing name-compression deferral | Delete after promotion | User selected its implementation and resolved the policy |

No new essential use case is needed: these contracts extend the existing style-authoring and application-linking goals without a distinct user journey.
