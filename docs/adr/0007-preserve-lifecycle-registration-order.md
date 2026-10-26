# Preserve lifecycle registration order across overrides

An override controls participation in each lifecycle phase, while the first ancestor annotation for that phase fixes its invocation position. This keeps ancestor sequencing stable while allowing children to disable callbacks, replace their implementation, and append new phase registrations. Moving an overriding method within a child class therefore cannot reorder inherited callbacks; see the [lifecycle callback contract](../specs/lifecycle-callbacks.md).
