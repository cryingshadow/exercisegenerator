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

}
