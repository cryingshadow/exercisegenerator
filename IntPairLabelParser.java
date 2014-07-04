import java.io.*;

/**
 * Parser to parse integer pair labels.
 * @author cryingshadow
 * @version $Id$
 */
public class IntPairLabelParser implements LabelParser<Pair<Integer, Integer>> {

    /* (non-Javadoc)
     * @see LabelParser#parse(java.lang.String)
     */
    @Override
    public Pair<Integer, Integer> parse(String text) throws IOException {
        try {
            String[] split = text.split(";");
            if (split == null || split.length != 2) {
                throw new IOException("Could not parse integer pair!");
            }
            return new Pair<Integer, Integer>(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
        } catch (NumberFormatException e) {
            throw new IOException(e);
        }
    }

}
