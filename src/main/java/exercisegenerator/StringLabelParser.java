package exercisegenerator;

import java.io.*;

/**
 * Parser to parse (= return) String labels.
 * @author cryingshadow
 * @version $Id$
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
