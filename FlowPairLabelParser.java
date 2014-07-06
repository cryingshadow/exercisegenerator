import java.io.*;

/**
 * Parser to parse integer pair labels.
 * @author cryingshadow
 * @version $Id$
 */
public class FlowPairLabelParser implements LabelParser<FlowPair> {

    /* (non-Javadoc)
     * @see LabelParser#parse(java.lang.String)
     */
    @Override
    public FlowPair parse(String text) throws IOException {
        try {
            String[] split = text.split(";");
            if (split == null || split.length != 2) {
                throw new IOException("Could not parse integer pair!");
            }
            return new FlowPair(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
        } catch (NumberFormatException e) {
            throw new IOException(e);
        }
    }

}
