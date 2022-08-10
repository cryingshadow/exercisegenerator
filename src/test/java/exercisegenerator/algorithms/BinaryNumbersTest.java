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
            + "\\end{tikzpicture}\n\n"
        );
        Assert.assertEquals(
            solResult.toString(),
            "\\begin{tikzpicture}\n"
            + "[node/.style={rectangle,draw=black,thick,inner sep=5pt}, node distance=0.25 and 0]\n"
            + "\\node[node] (n4) {1};\n"
            + "\\node[node] (n5) [right=of n4] {1};\n"
            + "\\node[node] (n6) [right=of n5] {0};\n"
            + "\\node[node] (n7) [right=of n6] {0};\n"
            + "\\end{tikzpicture}\n\n"
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
            + "\\end{tikzpicture}\n\n"
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
            + "\\end{tikzpicture}\n\n"
        );
    }

    @Test
    public void toOnesComplementUnit() {
        Assert.assertEquals(BinaryNumbers.toOnesComplement(3, 4).toString(), "0011");
        Assert.assertEquals(BinaryNumbers.toOnesComplement(4, 4).toString(), "0100");
        Assert.assertEquals(BinaryNumbers.toOnesComplement(0, 2).toString(), "00");
        Assert.assertEquals(BinaryNumbers.toOnesComplement(0, 9).toString(), "000000000");
        Assert.assertEquals(BinaryNumbers.toOnesComplement(1, 11).toString(), "00000000001");
        Assert.assertEquals(BinaryNumbers.toOnesComplement(7, 4).toString(), "0111");
        Assert.assertEquals(BinaryNumbers.toOnesComplement(-5, 4).toString(), "1010");
        Assert.assertEquals(BinaryNumbers.toOnesComplement(-7, 4).toString(), "1000");
        Assert.assertEquals(BinaryNumbers.toOnesComplement(-4, 7).toString(), "1111011");
        Assert.assertEquals(BinaryNumbers.toOnesComplement(-1, 3).toString(), "110");
        Assert.assertThrows(() -> BinaryNumbers.toOnesComplement(13, 4));
        Assert.assertThrows(() -> BinaryNumbers.toOnesComplement(8, 4));
        Assert.assertThrows(() -> BinaryNumbers.toOnesComplement(-8, 4));
        Assert.assertThrows(() -> BinaryNumbers.toOnesComplement(128, 8));
        Assert.assertThrows(() -> BinaryNumbers.toOnesComplement(-128, 8));
    }

    @Test
    public void toTwosComplementNegative() throws IOException {
        final StringWriter exResult = new StringWriter();
        final StringWriter solResult = new StringWriter();
        final BufferedWriter exWriter = new BufferedWriter(exResult);
        final BufferedWriter solWriter = new BufferedWriter(solResult);
        Main.lineSeparator = "\n";
        BinaryNumbers.toTwosComplement(-13, 7, exWriter, solWriter);
        exWriter.flush();
        solWriter.flush();
        Assert.assertEquals(
            exResult.toString(),
            "Die Zahl -13 wird im 7-bit Zweierkomplement dargestellt als:\\\\[2ex]\n"
            + "\\begin{tikzpicture}\n"
            + "[node/.style={rectangle,draw=black,thick,inner sep=5pt}, node distance=0.25 and 0]\n"
            + "\\node[node] (n0) {\\phantom{0}};\n"
            + "\\node[node] (n1) [right=of n0] {\\phantom{0}};\n"
            + "\\node[node] (n2) [right=of n1] {\\phantom{0}};\n"
            + "\\node[node] (n3) [right=of n2] {\\phantom{0}};\n"
            + "\\node[node] (n4) [right=of n3] {\\phantom{0}};\n"
            + "\\node[node] (n5) [right=of n4] {\\phantom{0}};\n"
            + "\\node[node] (n6) [right=of n5] {\\phantom{0}};\n"
            + "\\end{tikzpicture}\n\n"
        );
        Assert.assertEquals(
            solResult.toString(),
            "\\begin{tikzpicture}\n"
            + "[node/.style={rectangle,draw=black,thick,inner sep=5pt}, node distance=0.25 and 0]\n"
            + "\\node[node] (n7) {1};\n"
            + "\\node[node] (n8) [right=of n7] {1};\n"
            + "\\node[node] (n9) [right=of n8] {1};\n"
            + "\\node[node] (n10) [right=of n9] {0};\n"
            + "\\node[node] (n11) [right=of n10] {0};\n"
            + "\\node[node] (n12) [right=of n11] {1};\n"
            + "\\node[node] (n13) [right=of n12] {1};\n"
            + "\\end{tikzpicture}\n\n"
        );
    }

    @Test
    public void toTwosComplementPositive() throws IOException {
        final StringWriter exResult = new StringWriter();
        final StringWriter solResult = new StringWriter();
        final BufferedWriter exWriter = new BufferedWriter(exResult);
        final BufferedWriter solWriter = new BufferedWriter(solResult);
        Main.lineSeparator = "\n";
        BinaryNumbers.toTwosComplement(5, 4, exWriter, solWriter);
        exWriter.flush();
        solWriter.flush();
        Assert.assertEquals(
            exResult.toString(),
            "Die Zahl 5 wird im 4-bit Zweierkomplement dargestellt als:\\\\[2ex]\n"
            + "\\begin{tikzpicture}\n"
            + "[node/.style={rectangle,draw=black,thick,inner sep=5pt}, node distance=0.25 and 0]\n"
            + "\\node[node] (n0) {\\phantom{0}};\n"
            + "\\node[node] (n1) [right=of n0] {\\phantom{0}};\n"
            + "\\node[node] (n2) [right=of n1] {\\phantom{0}};\n"
            + "\\node[node] (n3) [right=of n2] {\\phantom{0}};\n"
            + "\\end{tikzpicture}\n\n"
        );
        Assert.assertEquals(
            solResult.toString(),
            "\\begin{tikzpicture}\n"
            + "[node/.style={rectangle,draw=black,thick,inner sep=5pt}, node distance=0.25 and 0]\n"
            + "\\node[node] (n4) {0};\n"
            + "\\node[node] (n5) [right=of n4] {1};\n"
            + "\\node[node] (n6) [right=of n5] {0};\n"
            + "\\node[node] (n7) [right=of n6] {1};\n"
            + "\\end{tikzpicture}\n\n"
        );
    }

    @Test
    public void toTwosComplementUnit() {
        Assert.assertEquals(BinaryNumbers.toTwosComplement(3, 4).toString(), "0011");
        Assert.assertEquals(BinaryNumbers.toTwosComplement(4, 4).toString(), "0100");
        Assert.assertEquals(BinaryNumbers.toTwosComplement(0, 2).toString(), "00");
        Assert.assertEquals(BinaryNumbers.toTwosComplement(0, 9).toString(), "000000000");
        Assert.assertEquals(BinaryNumbers.toTwosComplement(1, 11).toString(), "00000000001");
        Assert.assertEquals(BinaryNumbers.toTwosComplement(7, 4).toString(), "0111");
        Assert.assertEquals(BinaryNumbers.toTwosComplement(-5, 4).toString(), "1011");
        Assert.assertEquals(BinaryNumbers.toTwosComplement(-7, 4).toString(), "1001");
        Assert.assertEquals(BinaryNumbers.toTwosComplement(-4, 7).toString(), "1111100");
        Assert.assertEquals(BinaryNumbers.toTwosComplement(-1, 3).toString(), "111");
        Assert.assertEquals(BinaryNumbers.toTwosComplement(-8, 4).toString(), "1000");
        Assert.assertThrows(() -> BinaryNumbers.toTwosComplement(7, 3));
        Assert.assertThrows(() -> BinaryNumbers.toTwosComplement(8, 4));
        Assert.assertThrows(() -> BinaryNumbers.toTwosComplement(-9, 4));
        Assert.assertThrows(() -> BinaryNumbers.toTwosComplement(128, 8));
        Assert.assertThrows(() -> BinaryNumbers.toTwosComplement(-129, 8));
    }

}
