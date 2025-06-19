package exercisegenerator.store;

import java.util.*;

import exercisegenerator.algorithms.*;

@FunctionalInterface
public interface AlgorithmObserver {

    void notify(Set<Algorithm> algorithms);

}
