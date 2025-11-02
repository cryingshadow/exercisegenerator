package exercisegenerator.structures.simulator.expressions;

import java.util.*;

import exercisegenerator.structures.simulator.*;

public class ProgramEquals extends ProgramOperator {

    public ProgramEquals(final ProgramExpression left, final ProgramExpression right) {
        super(List.of(left, right));
    }

    @Override
    protected String display(final List<ProgramExpression> arguments) {
        return String.format("%s == %s", arguments.get(0), arguments.get(1));
    }

    @Override
    protected ProgramValue operator(final List<ProgramValue> arguments) {
        return new ProgramBoolean(arguments.get(0).equals(arguments.get(1)));
    }

    @Override
    protected String type(final List<ProgramExpression> arguments, final ProgramState state) {
        return "boolean";
    }

}
