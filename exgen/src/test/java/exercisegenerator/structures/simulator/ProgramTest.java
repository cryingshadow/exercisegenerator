package exercisegenerator.structures.simulator;

import java.util.*;

import org.testng.annotations.Test;

import exercisegenerator.structures.simulator.commands.*;
import exercisegenerator.structures.simulator.expressions.*;

public class ProgramTest {

    @Test
    public void f() {
        final Program program = new Program();
        final ProgramDataStructure listNodeStructure =
            new ProgramDataStructure(
                List.of(new ProgramVariable("next", "Node"), new ProgramVariable("value", "int")),
                List.of(
                    new ProgramMethodDefinition(
                        "contains",
                        "boolean",
                        List.of(new ProgramVariable("value", "int")),
                        List.of(
                            new ProgramIf(
                                new ProgramEquals(
                                    new ProgramVariable("next", "Node"),
                                    new ProgramNull()
                                ),
                                List.of(),
                                List.of()
                            )
                        ) //TODO
                    )
                )
            );
        final ProgramDataStructure listStructure =
            new ProgramDataStructure(
                List.of(new ProgramVariable("root", "Node")),
                List.of(
                    new ProgramMethodDefinition(
                        "contains",
                        "boolean",
                        List.of(new ProgramVariable("value", "int")),
                        List.of(
                            new ProgramReturn(
                                new ProgramMethodCall(
                                    new ProgramVariable("root", "Node"),
                                    "contains",
                                    "boolean",
                                    List.of(new ProgramVariable("value", "int"))
                                )
                            )
                        )
                    )
                )
            );
        program.put("List", listStructure);
        program.put("Node", listNodeStructure);
    }

}
