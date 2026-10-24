package react4j.css.utility.language;

import com.helger.css.parser.CSSParseHelper;
import com.helger.css.parser.ParserCSS30Constants;
import com.helger.css.utils.ECSSColor;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;
import react4j.css.internal.CssLexical;

/** The supported static CSS value grammars; variables retain browser substitution semantics. */
final class CompoundValues {
    private CompoundValues() {}

    private static final Set<String> COLOR_FUNCTIONS =
            Set.of("rgb", "rgba", "hsl", "hsla", "hwb", "lab", "lch", "oklab", "oklch", "color");
    private static final Set<String> COLOR_SPACES = Set.of(
            "srgb",
            "srgb-linear",
            "display-p3",
            "display-p3-linear",
            "a98-rgb",
            "prophoto-rgb",
            "rec2020",
            "xyz",
            "xyz-d50",
            "xyz-d65");
    private static final Set<String> POSITIONS = Set.of("left", "right", "top", "bottom", "center");

    static boolean variable(String value) {
        var function = RuleSyntax.function(value);
        return null != function && "var".equals(function.name());
    }

    static boolean color(String value) {
        String name = CSSParseHelper.unescapeURL(value).toLowerCase(Locale.ROOT);
        if (ECSSColor.isDefaultColorName(name)
                || Set.of("transparent", "currentcolor", "rebeccapurple").contains(name)
                || value.matches("#(?:[0-9a-fA-F]{3,4}|[0-9a-fA-F]{6}|[0-9a-fA-F]{8})")
                || variable(value)) {
            return true;
        }
        var function = RuleSyntax.function(value);
        if (null == function || !COLOR_FUNCTIONS.contains(function.name())) {
            return false;
        }
        var commas = RuleSyntax.split(function.arguments(), ',');
        boolean legacy = commas.size() > 1;
        if (legacy && !Set.of("rgb", "rgba", "hsl", "hsla").contains(function.name())) {
            return false;
        }
        var slash = RuleSyntax.split(function.arguments(), '/');
        if (legacy && slash.size() > 1 || slash.size() > 2) {
            return false;
        }
        var channels = new ArrayList<>(
                legacy
                        ? commas.stream().map(RuleSyntax.Part::text).toList()
                        : RuleSyntax.components(slash.get(0).text()));
        boolean dynamic = channels.stream().anyMatch(CompoundValues::variable);
        if (!legacy && 2 == slash.size() && !channel(slash.get(1).text(), !legacy)) {
            return false;
        }
        if ("color".equals(function.name())) {
            if (channels.isEmpty()) {
                return false;
            }
            String profile = channels.remove(0);
            if (!variable(profile) && !COLOR_SPACES.contains(profile.toLowerCase(Locale.ROOT))) {
                return false;
            }
        }
        if (!dynamic && (legacy ? channels.size() < 3 || channels.size() > 4 : 3 != channels.size())) {
            return false;
        }
        String legacyRgbType = "";
        for (int i = 0; i < channels.size(); i++) {
            String part = channels.get(i);
            if (variable(part)) {
                continue;
            }
            boolean hue = (Set.of("hsl", "hsla", "hwb").contains(function.name()) && 0 == i)
                    || Set.of("lch", "oklch").contains(function.name()) && 2 == i;
            if (hue) {
                if (!(matches("number", part) || matches("angle", part) || !legacy && "none".equalsIgnoreCase(part))) {
                    return false;
                }
            } else if (legacy && Set.of("hsl", "hsla").contains(function.name()) && i > 0 && i < 3 && !dynamic) {
                if (!matches("percentage", part)) {
                    return false;
                }
            } else if (!channel(part, !legacy)) {
                return false;
            }
            if (legacy && !dynamic && i < 3 && Set.of("rgb", "rgba").contains(function.name())) {
                String type = matches("percentage", part) ? "percentage" : "number";
                if (!legacyRgbType.isEmpty() && !type.equals(legacyRgbType)) {
                    return false;
                }
                legacyRgbType = type;
            }
        }
        return true;
    }

    private static boolean channel(String value, boolean modern) {
        return modern && "none".equalsIgnoreCase(value) || matches("number", value) || matches("percentage", value);
    }

    static boolean shadow(String value) {
        if ("none".equalsIgnoreCase(value) || variable(value)) {
            return true;
        }
        for (var shadow : RuleSyntax.split(value, ',')) {
            var parts = RuleSyntax.components(shadow.text());
            int colors = 0;
            int inset = 0;
            int lengths = 0;
            boolean dynamic = false;
            boolean endedLengths = false;
            for (var part : parts) {
                if (variable(part)) {
                    dynamic = true;
                } else if ("inset".equalsIgnoreCase(part)) {
                    inset++;
                    endedLengths |= lengths > 0;
                } else if (color(part)) {
                    colors++;
                    endedLengths |= lengths > 0;
                } else if (matches("length", part)) {
                    if (endedLengths) {
                        return false;
                    }
                    lengths++;
                    if (3 == lengths && negative(part)) {
                        return false;
                    }
                } else {
                    return false;
                }
            }
            if (colors > 1 || inset > 1 || lengths > 4 || !dynamic && lengths < 2) {
                return false;
            }
        }
        return true;
    }

    static boolean position(String value) {
        var parts = RuleSyntax.components(value);
        if (parts.isEmpty() || parts.size() > 4) {
            return false;
        }
        if (parts.stream().anyMatch(CompoundValues::variable)) {
            return parts.stream()
                    .allMatch(part -> variable(part)
                            || POSITIONS.contains(part.toLowerCase(Locale.ROOT))
                            || matches("length-percentage", part));
        }
        if (1 == parts.size()) {
            return POSITIONS.contains(parts.get(0).toLowerCase(Locale.ROOT))
                    || matches("length-percentage", parts.get(0));
        }
        if (2 == parts.size()) {
            String a = parts.get(0).toLowerCase(Locale.ROOT);
            String b = parts.get(1).toLowerCase(Locale.ROOT);
            return horizontal(a) && vertical(b)
                    || Set.of("top", "bottom").contains(a)
                            && Set.of("left", "right", "center").contains(b);
        }
        var axes = new HashSet<String>();
        for (int i = 0; i < parts.size(); i++) {
            String part = parts.get(i).toLowerCase(Locale.ROOT);
            if (!POSITIONS.contains(part)) {
                return false;
            }
            if (!"center".equals(part) && !axes.add(Set.of("left", "right").contains(part) ? "x" : "y")) {
                return false;
            }
            if (i + 1 < parts.size() && matches("length-percentage", parts.get(i + 1))) {
                if ("center".equals(part)) {
                    return false;
                }
                i++;
            }
        }
        return axes.size() >= 1
                && parts.stream()
                                .filter(part -> POSITIONS.contains(part.toLowerCase(Locale.ROOT)))
                                .count()
                        == 2;
    }

    private static boolean horizontal(String value) {
        return Set.of("left", "right", "center").contains(value) || matches("length-percentage", value);
    }

    private static boolean vertical(String value) {
        return Set.of("top", "bottom", "center").contains(value) || matches("length-percentage", value);
    }

    static boolean image(String value, boolean nestedSet) {
        if (variable(value)) {
            return true;
        }
        var function = RuleSyntax.function(value);
        if (null == function) {
            return false;
        }
        if ("url".equals(function.name())) {
            return true;
        }
        if ("image-set".equals(function.name())) {
            if (nestedSet) {
                return false;
            }
            for (var option : RuleSyntax.split(function.arguments(), ',')) {
                var parts = RuleSyntax.components(option.text());
                if (parts.isEmpty() || !(matches("string", parts.get(0)) || image(parts.get(0), true))) {
                    return false;
                }
                boolean resolution = false;
                boolean type = false;
                for (var part : parts.subList(1, parts.size())) {
                    var descriptor = RuleSyntax.function(part);
                    if (null != descriptor && "type".equals(descriptor.name())) {
                        if (type || !matches("string", descriptor.arguments())) {
                            return false;
                        }
                        type = true;
                    } else {
                        if (resolution || !matches("resolution", part) || negative(part)) {
                            return false;
                        }
                        resolution = true;
                    }
                }
            }
            return true;
        }
        String kind = function.name().replace("repeating-", "");
        if (!Set.of("linear-gradient", "radial-gradient", "conic-gradient").contains(kind)) {
            return false;
        }
        var parts = new ArrayList<>(RuleSyntax.split(function.arguments(), ',').stream()
                .map(RuleSyntax.Part::text)
                .toList());
        if (parts.isEmpty()) {
            return false;
        }
        if (!stop(parts.get(0), "conic-gradient".equals(kind))) {
            if (!gradientHeader(kind, parts.remove(0))) {
                return false;
            }
        }
        int stops = 0;
        boolean dynamic = false;
        boolean previousHint = false;
        for (int i = 0; i < parts.size(); i++) {
            String part = parts.get(i);
            if (stop(part, "conic-gradient".equals(kind))) {
                stops++;
                previousHint = false;
                dynamic |= variable(part);
            } else {
                if (0 == i
                        || i + 1 == parts.size()
                        || previousHint
                        || !gradientPosition(part, "conic-gradient".equals(kind))) {
                    return false;
                }
                previousHint = true;
            }
        }
        return dynamic || stops >= 2;
    }

    private static boolean stop(String text, boolean conic) {
        var parts = RuleSyntax.components(text);
        if (parts.isEmpty() || !color(parts.get(0))) {
            return false;
        }
        return parts.size() <= 3
                && parts.subList(1, parts.size()).stream().allMatch(part -> gradientPosition(part, conic));
    }

    private static boolean gradientPosition(String text, boolean conic) {
        return matches(conic ? "angle-percentage" : "length-percentage", text) || conic && "0".equals(text);
    }

    private static boolean gradientHeader(String kind, String text) {
        var parts = new ArrayList<>(RuleSyntax.components(text.toLowerCase(Locale.ROOT)));
        int in = parts.indexOf("in");
        if (in >= 0) {
            if (in + 1 >= parts.size()) {
                return false;
            }
            String space = parts.get(in + 1);
            boolean polar = Set.of("lch", "oklch", "hsl", "hwb").contains(space);
            if (!variable(space)
                    && !polar
                    && !COLOR_SPACES.contains(space)
                    && !Set.of("lab", "oklab").contains(space)) {
                return false;
            }
            int end = in + 2;
            if (end < parts.size()
                    && Set.of("shorter", "longer", "increasing", "decreasing").contains(parts.get(end))) {
                if ((!polar && !variable(space)) || end + 1 >= parts.size() || !"hue".equals(parts.get(end + 1))) {
                    return false;
                }
                end += 2;
            }
            parts.subList(in, end).clear();
        }
        if (parts.isEmpty()) {
            return in >= 0;
        }
        if ("linear-gradient".equals(kind)) {
            if (1 == parts.size() && matches("angle", parts.get(0))) {
                return true;
            }
            return "to".equals(parts.get(0))
                    && parts.size() >= 2
                    && parts.size() <= 3
                    && parts.subList(1, parts.size()).stream()
                            .allMatch(part ->
                                    Set.of("left", "right", "top", "bottom").contains(part))
                    && parts.subList(1, parts.size()).stream()
                                    .map(part -> Set.of("left", "right").contains(part) ? "x" : "y")
                                    .distinct()
                                    .count()
                            == parts.size() - 1;
        }
        int at = parts.indexOf("at");
        if (at >= 0) {
            if (!position(String.join(" ", parts.subList(at + 1, parts.size())))) {
                return false;
            }
            parts.subList(at, parts.size()).clear();
        }
        if ("conic-gradient".equals(kind)) {
            return parts.isEmpty()
                    || 2 == parts.size() && "from".equals(parts.get(0)) && matches("angle", parts.get(1));
        }
        if (parts.isEmpty()) {
            return true;
        }
        int shapes = 0;
        int sizes = 0;
        int lengths = 0;
        boolean circle = false;
        for (var part : parts) {
            if (Set.of("circle", "ellipse").contains(part)) {
                shapes++;
                circle = "circle".equals(part);
            } else if (Set.of("closest-side", "closest-corner", "farthest-side", "farthest-corner")
                    .contains(part)) {
                sizes++;
            } else if (matches("length-percentage", part) && !negative(part)) {
                lengths++;
            } else {
                return false;
            }
        }
        return shapes <= 1
                && sizes <= 1
                && (0 == lengths
                        || 0 == sizes
                                && (circle || 0 == shapes && 1 == lengths
                                        ? 1 == lengths
                                                && parts.stream()
                                                        .filter(part -> matches("length-percentage", part))
                                                        .allMatch(part -> matches("length", part))
                                        : 2 == lengths));
    }

    static boolean tracks(String value, boolean nestedRepeat) {
        if ("none".equalsIgnoreCase(value) || variable(value)) {
            return true;
        }
        var parts = RuleSyntax.components(value);
        if (parts.isEmpty()) {
            return false;
        }
        boolean subgrid = "subgrid".equalsIgnoreCase(parts.get(0));
        boolean track = false;
        int automatic = 0;
        boolean allFixed = true;
        for (var part : parts.subList(subgrid ? 1 : 0, parts.size())) {
            if (lineNames(part)) {
                continue;
            }
            var function = RuleSyntax.function(part);
            if (null != function && "repeat".equals(function.name())) {
                if (nestedRepeat) {
                    return false;
                }
                var args = RuleSyntax.split(function.arguments(), ',');
                if (2 != args.size()) {
                    return false;
                }
                String count = args.get(0).text().toLowerCase(Locale.ROOT);
                boolean auto = Set.of("auto-fit", "auto-fill").contains(count);
                boolean dynamicCount =
                        variable(count) || null != RuleSyntax.function(count) && matches("integer", count);
                if (!auto && !dynamicCount && !count.matches("[+]?[0-9]+")
                        || !auto && !dynamicCount && new BigInteger(count).signum() <= 0
                        || subgrid && "auto-fit".equals(count)) {
                    return false;
                }
                if (subgrid) {
                    var names = RuleSyntax.components(args.get(1).text());
                    if (names.isEmpty() || names.stream().anyMatch(name -> !lineNames(name))) {
                        return false;
                    }
                } else if (!tracks(args.get(1).text(), true)
                        || "none".equalsIgnoreCase(args.get(1).text())
                        || args.get(1).text().toLowerCase(Locale.ROOT).startsWith("subgrid")) {
                    return false;
                }
                boolean fixed = RuleSyntax.components(args.get(1).text()).stream()
                        .allMatch(item -> lineNames(item) || fixedTrack(item));
                if (auto && !subgrid && !fixed) {
                    return false;
                }
                automatic += auto ? 1 : 0;
                allFixed &= fixed;
                track = true;
            } else {
                if (subgrid || !track(part)) {
                    return false;
                }
                track = true;
                allFixed &= fixedTrack(part);
            }
        }
        return automatic <= 1 && (subgrid || track) && (subgrid || 0 == automatic || allFixed);
    }

    private static boolean lineNames(String text) {
        if (!text.startsWith("[") || !text.endsWith("]")) {
            return false;
        }
        for (var token : CssLexical.tokens(text, 1, text.length() - 1)) {
            if (token.value().isBlank()) {
                continue;
            }
            if (ParserCSS30Constants.IDENT != token.kind() && ParserCSS30Constants.DASHED_IDENT != token.kind()
                    || Set.of("auto", "span", "initial", "inherit", "unset", "revert", "revert-layer", "default")
                            .contains(token.name().toLowerCase(Locale.ROOT))) {
                return false;
            }
        }
        return true;
    }

    private static boolean track(String value) {
        if (variable(value) || Set.of("auto", "min-content", "max-content").contains(value.toLowerCase(Locale.ROOT))) {
            return true;
        }
        if (matches("length-percentage", value)) {
            return !negative(value);
        }
        if (value.matches("(?i)[+]?(?:[0-9]+(?:\\.[0-9]+)?|\\.[0-9]+)(?:[eE][+-]?[0-9]+)?fr")) {
            return true;
        }
        var function = RuleSyntax.function(value);
        if (null == function) {
            return false;
        }
        if ("fit-content".equals(function.name())) {
            return matches("length-percentage", function.arguments()) && !negative(function.arguments());
        }
        if ("minmax".equals(function.name())) {
            var parts = RuleSyntax.split(function.arguments(), ',');
            return 2 == parts.size()
                    && breadth(parts.get(0).text(), false)
                    && breadth(parts.get(1).text(), true);
        }
        return false;
    }

    private static boolean breadth(String value, boolean flex) {
        return variable(value)
                || Set.of("auto", "min-content", "max-content").contains(value.toLowerCase(Locale.ROOT))
                || matches("length-percentage", value) && !negative(value)
                || flex && value.matches("(?i)[+]?(?:[0-9]+(?:\\.[0-9]+)?|\\.[0-9]+)(?:[eE][+-]?[0-9]+)?fr");
    }

    private static boolean fixedTrack(String value) {
        if (matches("length-percentage", value) || variable(value)) {
            return true;
        }
        var function = RuleSyntax.function(value);
        if (null == function || !"minmax".equals(function.name())) {
            return false;
        }
        var parts = RuleSyntax.split(function.arguments(), ',');
        return 2 == parts.size()
                && (matches("length-percentage", parts.get(0).text())
                        || matches("length-percentage", parts.get(1).text()));
    }

    private static boolean negative(String value) {
        try {
            return RuleValues.scalar(value).number().signum() < 0;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static boolean matches(String type, String value) {
        try {
            if (Set.of("angle-percentage", "resolution").contains(type)) {
                NumericValues.check(type, value);
            } else {
                RuleValues.check(type, value);
            }
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }
}
