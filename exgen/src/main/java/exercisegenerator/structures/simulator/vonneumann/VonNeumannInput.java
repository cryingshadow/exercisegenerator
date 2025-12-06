package exercisegenerator.structures.simulator.vonneumann;

import java.util.*;

import exercisegenerator.structures.binary.*;

public record VonNeumannInput(VonNeumannState initialState, SortedMap<Integer, BitString> inputs) {

}
