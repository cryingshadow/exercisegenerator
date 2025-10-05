package exercisegenerator.structures.logic;

import java.util.*;

public interface FormulaVisitor<T> {

    T onConjunction(List<T> children);

    T onDisjunction(List<T> children);

    T onEquivalence(T left, T right);

    T onFalse();

    T onImplication(T antecedence, T consequence);

    T onNegation(T child);

    T onTrue();

    T onVariable(String name);

    T onXor(T left, T right);

}
