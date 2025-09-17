package exercisegenerator.structures.trees;

import java.util.*;

public record ConstructionAndTasks<T extends Comparable<T>>(
    Deque<TreeOperation<T>> construction,
    Deque<TreeOperation<T>> tasks
) {}
