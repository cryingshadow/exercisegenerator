package exercisegenerator.structures.simulator.commands;

import java.util.function.*;

import exercisegenerator.structures.simulator.*;

@FunctionalInterface
public interface ProgramCommand extends Function<ProgramState, ProgramState> {}
