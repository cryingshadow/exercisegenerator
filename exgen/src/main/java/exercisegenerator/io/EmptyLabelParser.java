package exercisegenerator.io;

import java.io.*;

/**
 * Parser to parse null labels.
 */
public class EmptyLabelParser implements LabelParser<Object> {

    /* (non-Javadoc)
     * @see LabelParser#parse(java.lang.String)
     */
    @Override
    public Object parse(final String text) throws IOException {
        return null;
    }

}
