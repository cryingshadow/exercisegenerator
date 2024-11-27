package exercisegenerator.io;

import java.io.*;

import exercisegenerator.util.*;

public class ParserAndGenerator<T> {

    private final CheckedFunction<Parameters, T, IOException> generator;

    private final CheckedBiFunction<BufferedReader, Parameters, T, IOException> parser;

    public ParserAndGenerator(
        final CheckedBiFunction<BufferedReader, Parameters, T, IOException> parser,
        final CheckedFunction<Parameters, T, IOException> generator
    ) {
        this.parser = parser;
        this.generator = generator;
    }

    public T getResult(final Parameters options) throws IOException {
        if (options.containsKey(Flag.SOURCE)) {
            try (BufferedReader reader = new BufferedReader(new FileReader(options.get(Flag.SOURCE)))) {
                return this.parser.apply(reader, options);
            }
        } else if (options.containsKey(Flag.INPUT)) {
            try (
                BufferedReader reader =
                    new BufferedReader(new StringReader(options.get(Flag.INPUT).replace("\\n", "\n")))
            ) {
                return this.parser.apply(reader, options);
            }
        }
        return this.generator.apply(options);
    }

}
