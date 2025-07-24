package exercisegenerator.io;

import java.io.*;

import exercisegenerator.structures.graphs.flownetwork.*;

public class FlowAndCapacityLabelParser implements LabelParser<FlowAndCapacity> {

    @Override
    public FlowAndCapacity parse(final String text) throws IOException {
        try {
            final String[] split = text.split(";");
            if (split == null || split.length != 2) {
                throw new IOException("Could not parse integer pair!");
            }
            return new FlowAndCapacity(Integer.parseInt(split[0]), Integer.parseInt(split[1]));
        } catch (final NumberFormatException e) {
            throw new IOException(e);
        }
    }

}
