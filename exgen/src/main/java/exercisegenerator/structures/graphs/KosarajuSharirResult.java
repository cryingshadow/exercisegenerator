package exercisegenerator.structures.graphs;

import java.util.*;

public record KosarajuSharirResult(List<String> stack, Map<String, String> assignment) {

    public int computeContentLength() {
        return this.stack().stream().map(String::length).reduce(1, Math::max);
    }}
