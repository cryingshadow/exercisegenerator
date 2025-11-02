package exercisegenerator.structures.simulator;

import java.util.*;

import exercisegenerator.structures.simulator.expressions.*;

public class Program extends LinkedHashMap<String, ProgramDataStructure> {

    private static final long serialVersionUID = 1L;

    public ProgramPosition findFirstPositionInMethod(
        final String structureType,
        final String methodName,
        final List<String> parameterTypes
    ) {
        final ProgramDataStructure structure = this.get(structureType);
        final int methodIndex = structure.findMethodIndex(methodName, parameterTypes);
        return new ProgramPosition(structureType, methodIndex, ProgramPositionIndex.START, ProgramPositionIndex.EMPTY);
    }

    public List<ProgramVariable> getParameters(final ProgramPosition firstPositionInMethod) {
        final ProgramDataStructure structure = this.get(firstPositionInMethod.dataStructureName());
        final ProgramMethodDefinition method = structure.methods().get(firstPositionInMethod.methodIndex());
        return method.parameters();
    }

}
