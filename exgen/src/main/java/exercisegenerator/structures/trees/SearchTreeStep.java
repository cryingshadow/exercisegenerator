package exercisegenerator.structures.trees;

import java.util.*;
import java.util.stream.*;

public record SearchTreeStep<T>(SearchTreeStepType type, List<T> values, String annotation) {

    public SearchTreeStep(final SearchTreeStepType type, final Collection<T> values) {
        this(type, values.stream().toList(), "");
    }

    public SearchTreeStep(final SearchTreeStepType type, final T value) {
        this(type, List.of(value), "");
    }

    public SearchTreeStep(final SearchTreeStepType type, final T value, final String annotation) {
        this(type, List.of(value), annotation);
    }

    public String toLaTeX() {
        switch (this.type()) {
        case ADD:
            return String.format("%sf\\\"uge %s ein", this.annotation(), this.values().get(0).toString());
        case COLOR:
            return String.format("%sf\\\"arbe %s um", this.annotation(), this.valuesToString());
        case MERGE:
            return String.format("%sverschmelze die Kinder von %s", this.annotation(), this.values().get(0).toString());
        case REMOVE:
            return String.format("%sentferne %s", this.annotation(), this.values().get(0).toString());
        case REPLACE:
            return String.format("%sersetze %s", this.annotation(), this.values().get(0).toString());
        case ROTATE_LEFT:
            return String.format("%srotiere %s nach links", this.annotation(), this.values().get(0).toString());
        case ROTATE_RIGHT:
            return String.format("%srotiere %s nach rechts", this.annotation(), this.values().get(0).toString());
        case SPLIT:
            return String.format("%sspalte %s auf", this.annotation(), this.values().get(0).toString());
        case STEAL_LEFT:
            return String.format("%sstehle Vorg\\\"anger von %s", this.annotation(), this.values().get(0).toString());
        case STEAL_RIGHT:
            return String.format("%sstehle Nachfolger von %s", this.annotation(), this.values().get(0).toString());
        default:
            throw new IllegalStateException("Someone added a new step type but did not extend toLaTeX accordingly!");
        }
    }

    @Override
    public String toString() {
        return String.format(
            "%s%s%s",
            this.annotation(),
            this.type().toString(),
            this.values().stream().sorted().map(v -> v.toString()).collect(Collectors.joining("|"))
        );
    }

    private String valuesToString() {
        if (this.values().size() == 1) {
            return this.values().get(0).toString();
        }
        final String last = this.values().get(this.values().size() - 1).toString();
        return String.format(
            "%s und %s",
            this.values()
                .stream()
                .limit(this.values().size() - 1)
                .map(v -> v.toString())
                .collect(Collectors.joining(", ")),
            last
        );
    }

}
