package exercisegenerator.io;

import java.util.*;

public class Parameters extends LinkedHashMap<Flag, String> {

    private static final long serialVersionUID = 2289418280681916331L;

    public Parameters() {
        super();
    }

    public Parameters(final Map<Flag, String> map) {
        super(map);
    }

    public boolean containsAtLeastOne(final Flag... flags) {
        for (final Flag flag : flags) {
            if (this.containsKey(flag)) {
                return true;
            }
        }
        return false;
    }

}
