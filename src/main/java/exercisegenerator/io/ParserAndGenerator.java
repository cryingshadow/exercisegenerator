package exercisegenerator.io;

import java.io.*;
import java.util.*;
import java.util.function.*;

import exercisegenerator.*;
import exercisegenerator.util.*;

public class ParserAndGenerator<T> {

    private final Supplier<T> generator;

    private final CheckedFunction<BufferedReader, T, IOException> parser;

    public ParserAndGenerator(
        final CheckedFunction<BufferedReader, T, IOException> parser,
        final Supplier<T> generator
    ) {
        this.parser = parser;
        this.generator = generator;
    }

    public T getResult(final Map<Flag, String> options) throws IOException {
        if (options.containsKey(Flag.SOURCE)) {
            try (BufferedReader reader = new BufferedReader(new FileReader(options.get(Flag.SOURCE)))) {
                return this.parser.apply(reader);
            }
        } else if (Main.STUDENT_MODE) {
            return this.generator.get();
        } else {
            try (BufferedReader reader = new BufferedReader(new StringReader(options.get(Flag.INPUT)))) {
                return this.parser.apply(reader);
            }
        }
    }

}
