package exercisegenerator.structures.simulator;

import java.util.*;

public record ProgramDataStructure(List<ProgramVariable> fields, List<ProgramMethodDefinition> methods) {

    public int findMethodIndex(final String methodName, final List<String> parameterTypes) {
        for (int i = 0; i < this.methods().size(); i++) {
            final ProgramMethodDefinition method = this.methods().get(i);
            if (
                method.name().equals(methodName)
                && method.parameters().stream().map(ProgramVariable::type).toList().equals(parameterTypes)
            ) {
                return i;
            }
        }
        return -1;
    }

}
