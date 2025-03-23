package utils;

import java.util.function.Consumer;

public class PatchUtil {

    public static <T> void patchIfNotNull(T value, Consumer<T> setter) {
        if (value != null) {
            setter.accept(value);
        }
    }
}
