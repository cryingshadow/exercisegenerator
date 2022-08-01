package exercisegenerator;

import java.io.*;

/**
 * Parser to parse integer pair labels.
 * @author Thomas Stroeder
 * @version 1.0
 */
public class FlowPairLabelParser implements LabelParser<FlowPair> {

    /* (non-Javadoc)
     * @see LabelParser#parse(java.lang.String)
     */
    @Override
    public FlowPair parse(final String text) throws IOException {
        try {
            final String[] split = text.split(";");
            if (split == null || split.length != 2) {
                throw new IOException("Could not parse integer pair!");
            }
            return new FlowPair(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
        } catch (final NumberFormatException e) {
            throw new IOException(e);
        }
    }

}
