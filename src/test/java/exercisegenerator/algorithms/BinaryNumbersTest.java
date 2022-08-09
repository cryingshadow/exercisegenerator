package exercisegenerator.algorithms;

import java.io.*;

import org.testng.*;
import org.testng.annotations.*;

import exercisegenerator.*;
import exercisegenerator.io.*;

public class BinaryNumbersTest {

    @BeforeMethod
    public void prepare() {
        TikZUtils.reset();
    }

    @Test
    public void toOnesComplementNegative() throws IOException {
        final StringWriter exResult = new StringWriter();
        final StringWriter solResult = new StringWriter();
        final BufferedWriter exWriter = new BufferedWriter(exResult);
        final BufferedWriter solWriter = new BufferedWriter(solResult);
        Main.lineSeparator = "\n";
        BinaryNumbers.toOnesComplement(-3, 4, exWriter, solWriter);
        exWriter.flush();
        solWriter.flush();
        Assert.assertEquals(
            exResult.toString(),
            "Die Zahl -3 wird im 4-bit Einerkomplement dargestellt als:\\\\[2ex]\n"
            + "\\begin{tikzpicture}\n"
            + "[node/.style={rectangle,draw=black,thick,inner sep=5pt}, node distance=0.25 and 0]\n"
            + "\\node[node] (n0) {\\phantom{0}};\n"
            + "\\node[node] (n1) [right=of n0] {\\phantom{0}};\n"
            + "\\node[node] (n2) [right=of n1] {\\phantom{0}};\n"
            + "\\node[node] (n3) [right=of n2] {\\phantom{0}};\n"
            + "\\end{tikzpicture}\n"
        );
        Assert.assertEquals(
            solResult.toString(),
            "\\begin{tikzpicture}\n"
            + "[node/.style={rectangle,draw=black,thick,inner sep=5pt}, node distance=0.25 and 0]\n"
            + "\\node[node] (n4) {1};\n"
            + "\\node[node] (n5) [right=of n4] {1};\n"
            + "\\node[node] (n6) [right=of n5] {0};\n"
            + "\\node[node] (n7) [right=of n6] {0};\n"
            + "\\end{tikzpicture}\n"
        );
    }


    @Test
    public void toOnesComplementPositive() throws IOException {
        final StringWriter exResult = new StringWriter();
        final StringWriter solResult = new StringWriter();
        final BufferedWriter exWriter = new BufferedWriter(exResult);
        final BufferedWriter solWriter = new BufferedWriter(solResult);
        Main.lineSeparator = "\n";
        BinaryNumbers.toOnesComplement(7, 8, exWriter, solWriter);
        exWriter.flush();
        solWriter.flush();
        Assert.assertEquals(
            exResult.toString(),
            "Die Zahl 7 wird im 8-bit Einerkomplement dargestellt als:\\\\[2ex]\n"
            + "\\begin{tikzpicture}\n"
            + "[node/.style={rectangle,draw=black,thick,inner sep=5pt}, node distance=0.25 and 0]\n"
            + "\\node[node] (n0) {\\phantom{0}};\n"
            + "\\node[node] (n1) [right=of n0] {\\phantom{0}};\n"
            + "\\node[node] (n2) [right=of n1] {\\phantom{0}};\n"
            + "\\node[node] (n3) [right=of n2] {\\phantom{0}};\n"
            + "\\node[node] (n4) [right=of n3] {\\phantom{0}};\n"
            + "\\node[node] (n5) [right=of n4] {\\phantom{0}};\n"
            + "\\node[node] (n6) [right=of n5] {\\phantom{0}};\n"
            + "\\node[node] (n7) [right=of n6] {\\phantom{0}};\n"
            + "\\end{tikzpicture}\n"
        );
        Assert.assertEquals(
            solResult.toString(),
            "\\begin{tikzpicture}\n"
            + "[node/.style={rectangle,draw=black,thick,inner sep=5pt}, node distance=0.25 and 0]\n"
            + "\\node[node] (n8) {0};\n"
            + "\\node[node] (n9) [right=of n8] {0};\n"
            + "\\node[node] (n10) [right=of n9] {0};\n"
            + "\\node[node] (n11) [right=of n10] {0};\n"
            + "\\node[node] (n12) [right=of n11] {0};\n"
            + "\\node[node] (n13) [right=of n12] {1};\n"
            + "\\node[node] (n14) [right=of n13] {1};\n"
            + "\\node[node] (n15) [right=of n14] {1};\n"
            + "\\end{tikzpicture}\n"
        );
    }

}
