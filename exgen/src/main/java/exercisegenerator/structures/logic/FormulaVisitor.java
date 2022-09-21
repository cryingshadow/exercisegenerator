package exercisegenerator.structures.logic;

import java.util.*;

public interface FormulaVisitor<T> {

    T onConjunction(List<T> children);

    T onDisjunction(List<T> children);

    T onFalse();

    T onNegation(T child);

    T onTrue();

    T onVariable(String name);

}
