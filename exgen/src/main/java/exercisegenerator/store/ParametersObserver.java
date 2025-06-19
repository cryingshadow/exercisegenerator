package exercisegenerator.store;

import clit.*;
import exercisegenerator.io.*;

@FunctionalInterface
public interface ParametersObserver {

    void notify(Parameters<Flag> parameters);

}
