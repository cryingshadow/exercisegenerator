package exercisegenerator.io;

import java.io.*;

/**
 * Parser from Strings to L.
 * @author Thomas Stroeder
 * @version 1.0
 * @param <L> The type of the label to parse a String to.
 */
public interface LabelParser<L> {

    /**
     * @param text The String to parse.
     * @return The parsed label.
     * @throws IOException If the text cannot be parsed to the expected label type.
     */
    L parse(String text) throws IOException;

}
