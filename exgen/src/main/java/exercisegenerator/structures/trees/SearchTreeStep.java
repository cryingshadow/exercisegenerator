package exercisegenerator.structures.trees;

import java.util.*;
import java.util.stream.*;

public class SearchTreeStep<T> {

    public final SearchTreeStepType type;

    public final List<T> values;

    public SearchTreeStep(final SearchTreeStepType type, final Collection<T> values) {
        this.type = type;
        this.values = values.stream().toList();
    }

    public SearchTreeStep(final SearchTreeStepType type, final T value) {
        this(type, List.of(value));
    }

    public String toLaTeX() {
        switch (this.type) {
        case ADD:
            return String.format("f\\\"uge %s ein", this.values.get(0).toString());
        case COLOR:
            return String.format("f\\\"arbe %s um", this.valuesToString());
        case MERGE:
            return String.format("verschmelze die Kinder von %s", this.values.get(0).toString());
        case REMOVE:
            return String.format("entferne %s", this.values.get(0).toString());
        case REPLACE:
            return String.format("ersetze %s", this.values.get(0).toString());
        case ROTATE_LEFT:
            return String.format("rotiere %s nach links", this.values.get(0).toString());
        case ROTATE_RIGHT:
            return String.format("rotiere %s nach rechts", this.values.get(0).toString());
        case SPLIT:
            return String.format("spalte %s auf", this.values.get(0).toString());
        case STEAL_LEFT:
            return String.format("stehle Vorg\\\"anger von %s", this.values.get(0).toString());
        case STEAL_RIGHT:
            return String.format("stehle Nachfolger von %s", this.values.get(0).toString());
        default:
            throw new IllegalStateException("Someone added a new step type but did not extend toLaTeX accordingly!");
        }
    }

    @Override
    public String toString() {
        return this.type.toString() + this.values.stream().map(v -> v.toString()).collect(Collectors.joining("|"));
    }

    private String valuesToString() {
        if (this.values.size() == 1) {
            return this.values.get(0).toString();
        }
        final String last = this.values.get(this.values.size() - 1).toString();
        return String.format(
            "%s und %s",
            this.values.stream().limit(this.values.size() - 1).map(v -> v.toString()).collect(Collectors.joining(", ")),
            last
        );
    }

}
