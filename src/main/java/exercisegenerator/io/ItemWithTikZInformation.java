package exercisegenerator.io;

import java.util.*;

public class ItemWithTikZInformation<T> {

    public final boolean marker;

    public final Optional<T> optionalContent;

    public final Optional<Integer> optionalIndex;

    public final boolean separateBefore;

    public ItemWithTikZInformation() {
        this(Optional.empty(), false, false, Optional.empty());
    }

    public ItemWithTikZInformation(final int index) {
        this(Optional.empty(), false, false, Optional.of(index));
    }

    public ItemWithTikZInformation(final Optional<T> optionalContent) {
        this(optionalContent, false, false, Optional.empty());
    }

    public ItemWithTikZInformation(
        final Optional<T> optionalContent,
        final boolean separateBefore
    ) {
        this(optionalContent, false, separateBefore, Optional.empty());
    }

    public ItemWithTikZInformation(
        final Optional<T> optionalContent,
        final boolean marker,
        final boolean separateBefore
    ) {
        this(optionalContent, marker, separateBefore, Optional.empty());
    }

    public ItemWithTikZInformation(
        final Optional<T> optionalContent,
        final boolean marker,
        final boolean separateBefore,
        final Optional<Integer> optionalIndex
    ) {
        this.optionalContent = optionalContent;
        this.marker = marker;
        this.separateBefore = separateBefore;
        this.optionalIndex = optionalIndex;
    }

}
