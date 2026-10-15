package isolated;

import java.util.List;
import org.jspecify.annotations.NullMarked;
import org.jspecify.annotations.Nullable;
import react4j.annotations.Input;

@NullMarked
public abstract class Parent<T extends @Nullable Object> {
    @Input
    abstract T value();

    @Input
    abstract List<@Nullable String> nested();
}
