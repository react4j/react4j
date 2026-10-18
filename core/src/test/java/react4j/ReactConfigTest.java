package react4j;

import static org.testng.Assert.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

@NullMarked
public final class ReactConfigTest {
    private static final Map<String, String> SETTINGS = Map.of(
            "enable_view_names", "enableViewNames",
            "minimize_input_keys", "shouldMinimizeInputKeys",
            "validate_input_values", "shouldValidateInputValues",
            "store_debug_data_as_state", "shouldStoreDebugDataAsState",
            "check_invariants", "shouldCheckInvariants");

    @DataProvider
    public Object[][] environments() {
        return new Object[][] {{"production", true}, {"development", false}, {null, true}, {"other", false}};
    }

    @Test(dataProvider = "environments")
    public void defaultsAndExplicitOverrides(@Nullable String environment, boolean production) throws Exception {
        var saved = new HashMap<String, @Nullable String>();
        var keys = new ArrayList<>(SETTINGS.keySet());
        keys.add("environment");
        keys.forEach(key -> saved.put(key, System.getProperty("react4j." + key)));
        try {
            keys.forEach(key -> System.clearProperty("react4j." + key));
            if (null != environment) {
                System.setProperty("react4j.environment", environment);
            }
            var defaults = configuration();
            assertEquals(flag(defaults, "isProductionEnvironment"), production);
            SETTINGS.forEach((key, method) ->
                    assertEquals(flag(defaults, method), "minimize_input_keys".equals(key) == production, key));
            for (String value : new String[] {"true", "false", "invalid"}) {
                SETTINGS.keySet().forEach(key -> System.setProperty("react4j." + key, value));
                var overridden = configuration();
                SETTINGS.forEach((key, method) -> assertEquals(flag(overridden, method), "true".equals(value), key));
            }
        } finally {
            saved.forEach((key, value) -> {
                if (null == value) {
                    System.clearProperty("react4j." + key);
                } else {
                    System.setProperty("react4j." + key, value);
                }
            });
        }
    }

    private static boolean flag(Class<?> type, String name) {
        try {
            var method = type.getDeclaredMethod(name);
            method.setAccessible(true);
            return (boolean) method.invoke(null);
        } catch (ReflectiveOperationException e) {
            throw new AssertionError(e);
        }
    }

    private static Class<?> configuration() throws ClassNotFoundException {
        // Read the tested classpath's bytes, preserving Bazel's actual coverage instrumentation.
        return Class.forName("react4j.ReactConfig", true, new ClassLoader(ReactConfigTest.class.getClassLoader()) {
            @Override
            protected Class<?> loadClass(String name, boolean resolve) throws ClassNotFoundException {
                if (!"react4j.ReactConfig".equals(name) && !name.startsWith("react4j.ReactConfig$")) {
                    return super.loadClass(name, resolve);
                }
                var existing = findLoadedClass(name);
                if (null != existing) {
                    return existing;
                }
                String resource = "/" + name.replace('.', '/') + ".class";
                try (var input = ReactConfigTest.class.getResourceAsStream(resource)) {
                    if (null == input) {
                        throw new ClassNotFoundException(name);
                    }
                    byte[] bytes = input.readAllBytes();
                    var type = defineClass(name, bytes, 0, bytes.length);
                    if (resolve) {
                        resolveClass(type);
                    }
                    return type;
                } catch (IOException e) {
                    throw new ClassNotFoundException(name, e);
                }
            }
        });
    }
}
