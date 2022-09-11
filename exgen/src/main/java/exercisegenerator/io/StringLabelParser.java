package exercisegenerator.io;

import java.io.*;

/**
 * Parser to parse (= return) String labels.
 */
public class StringLabelParser implements LabelParser<String> {

    /* (non-Javadoc)
     * @see LabelParser#parse(java.lang.String)
     */
    @Override
    public String parse(final String text) throws IOException {
        return text;
    }

}
