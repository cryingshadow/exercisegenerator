package exercisegenerator.algorithms.simulator;

import java.math.*;
import java.util.*;

import org.testng.*;
import org.testng.annotations.*;

import exercisegenerator.structures.binary.*;
import exercisegenerator.structures.simulator.vonneumann.*;

public class VonNeumannTest {

    @DataProvider
    public Object[][] cycleData() {
        final BitString zero = BitString.create(BigInteger.ZERO, VonNeumann.BIT_LENGTH);
        final List<BitString> memory =
            List.of(
                BitString.parse("01110000000000000000000000000000"),
                BitString.parse("00000000000000000000000000001010"),
                BitString.parse("01110100000000000000000000000000"),
                BitString.parse("00000000000000000000000000000010"),
                BitString.parse("01010100000000000000000000000000"),
                BitString.parse("10000000000000000000000000000000"),
                BitString.parse("01100000000000000000000000000000"),
                BitString.parse("00100000000000000000000000000000"),
                BitString.parse("00000000000000000000000000000000")
            );
        return new Object[][] {
            {
                new VonNeumannState(
                    List.of(),
                    new VonNeumannIOState(zero, zero),
                    new VonNeumannRegisters(
                        zero,
                        zero,
                        zero,
                        zero,
                        zero,
                        new VonNeumannGPRegisters(new BitString[] {zero, zero, zero, zero})
                    ),
                    false,
                    false
                ),
                zero,
                new VonNeumannState(
                    List.of(),
                    new VonNeumannIOState(zero, zero),
                    new VonNeumannRegisters(
                        zero,
                        zero,
                        zero,
                        zero,
                        zero,
                        new VonNeumannGPRegisters(new BitString[] {zero, zero, zero, zero})
                    ),
                    true,
                    true
                )
            },
            {
                new VonNeumannState(
                    memory,
                    new VonNeumannIOState(zero, zero),
                    new VonNeumannRegisters(
                        zero,
                        zero,
                        zero,
                        zero,
                        zero,
                        new VonNeumannGPRegisters(new BitString[] {zero, zero, zero, zero})
                    ),
                    false,
                    false
                ),
                zero,
                new VonNeumannState(
                    memory,
                    new VonNeumannIOState(zero, zero),
                    new VonNeumannRegisters(
                        BitString.parse("00000000000000000000000000001010"),
                        BitString.parse("00000000000000000000000000000001"),
                        BitString.parse("01110000000000000000000000000000"),
                        BitString.parse("00000000000000000000000000000010"),
                        zero,
                        new VonNeumannGPRegisters(
                            new BitString[] {BitString.parse("00000000000000000000000000001010"), zero, zero, zero}
                        )
                    ),
                    false,
                    false
                )
            }
        };
    }

    @Test(dataProvider="cycleData")
    public void cycleTest(final VonNeumannState state, final BitString input, final VonNeumannState expected) {
        Assert.assertEquals(VonNeumann.cycle(state, input), expected);

    }

}
