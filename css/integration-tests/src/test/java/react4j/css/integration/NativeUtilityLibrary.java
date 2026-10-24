package react4j.css.integration;

import react4j.css.CssImport;
import react4j.css.UtilityCssLibrary;

@CssImport(value = SharedPulse.class, name = "external-pulse")
@CssImport(value = SharedDefaults.class, property = "accent", name = "theme")
@UtilityCssLibrary(file = "native.utilities.css")
final class NativeUtilityLibrary {}
