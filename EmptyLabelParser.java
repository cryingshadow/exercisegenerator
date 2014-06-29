import java.io.*;

/**
 * Parser to parse null labels.
 * @author cryingshadow
 * @version $Id$
 */
public class EmptyLabelParser implements LabelParser<Object> {

    /* (non-Javadoc)
     * @see LabelParser#parse(java.lang.String)
     */
    @Override
    public Object parse(String text) throws IOException {
        return null;
    }

}
