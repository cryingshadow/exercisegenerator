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

    public ItemWithTikZInformation(final Optional<T> optionalContent, final int index) {
        this(optionalContent, false, false, Optional.of(index));
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof ItemWithTikZInformation)) {
            return false;
        }
        final ItemWithTikZInformation<?> other = (ItemWithTikZInformation<?>)o;
        return
            this.separateBefore == other.separateBefore
            && this.marker == other.marker
            && this.optionalContent.equals(other.optionalContent)
            && this.optionalIndex.equals(other.optionalIndex);
    }

    @Override
    public int hashCode() {
        return this.optionalContent.hashCode()
            + this.optionalIndex.hashCode()
            + (this.separateBefore ? 3 : 0)
            + (this.marker ? 7 : 0);
    }

    @Override
    public String toString() {
        return String.format(
            "%s[%s]%s%s",
            this.optionalIndex.isPresent() ? this.optionalIndex.get() + ":" : "",
            this.optionalContent.isPresent() ? this.optionalContent.get().toString() : "",
            this.separateBefore ? "s" : "",
            this.marker ? "m" : ""
        );
    }

}
