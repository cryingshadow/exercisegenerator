package exercisegenerator.store;

import java.io.*;
import java.util.*;

@FunctionalInterface
public interface DirectoryObserver {

    void notify(Optional<File> directory);

}
