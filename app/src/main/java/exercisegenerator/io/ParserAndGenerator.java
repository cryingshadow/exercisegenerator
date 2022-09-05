package exercisegenerator.io;

import java.io.*;
import java.util.function.*;

import exercisegenerator.*;
import exercisegenerator.util.*;

public class ParserAndGenerator<T> {

    private final Function<Parameters, T> generator;

    private final CheckedBiFunction<BufferedReader, Parameters, T, IOException> parser;

    public ParserAndGenerator(
        final CheckedBiFunction<BufferedReader, Parameters, T, IOException> parser,
        final Function<Parameters, T> generator
    ) {
        this.parser = parser;
        this.generator = generator;
    }

    public T getResult(final Parameters options) throws IOException {
        if (options.containsKey(Flag.SOURCE)) {
            try (BufferedReader reader = new BufferedReader(new FileReader(options.get(Flag.SOURCE)))) {
                return this.parser.apply(reader, options);
            }
        } else if (Main.STUDENT_MODE) {
            return this.generator.apply(options);
        } else {
            try (BufferedReader reader = new BufferedReader(new StringReader(options.get(Flag.INPUT)))) {
                return this.parser.apply(reader, options);
            }
        }
    }

}
