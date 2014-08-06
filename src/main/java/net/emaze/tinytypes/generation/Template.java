package net.emaze.tinytypes.generation;

import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @author rferranti
 */
public class Template {

    private final Stream<String> vs;

    public Template(Stream<String> vs) {
        this.vs = vs;
    }

    public static Template of(String... vs) {
        return new Template(Stream.of(vs));
    }

    public String format(Object... args) {
        return String.format(vs.collect(Collectors.joining()), args);
    }
}
