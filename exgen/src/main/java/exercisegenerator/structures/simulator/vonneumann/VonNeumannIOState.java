package exercisegenerator.structures.simulator.vonneumann;

import exercisegenerator.structures.binary.*;

public record VonNeumannIOState(BitString input, BitString output) {

    public VonNeumannIOState setInput(final BitString input) {
        return new VonNeumannIOState(input, this.output());
    }

    public VonNeumannIOState setOutput(final BitString output) {
        return new VonNeumannIOState(this.input(), output);
    }

}
