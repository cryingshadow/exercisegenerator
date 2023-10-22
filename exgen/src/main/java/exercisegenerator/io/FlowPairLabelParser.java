package exercisegenerator.io;

import java.io.*;

import exercisegenerator.structures.graphs.*;

/**
 * Parser to parse integer pair labels.
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
