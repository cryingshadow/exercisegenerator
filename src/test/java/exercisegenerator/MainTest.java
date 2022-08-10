package exercisegenerator;

import java.io.*;

import org.testng.*;
import org.testng.annotations.*;

import exercisegenerator.io.*;

public class MainTest {

    private static final String EX_FILE = "C:\\Daten\\Test\\exgen\\ex.tex";

    private static final String SOL_FILE = "C:\\Daten\\Test\\exgen\\sol.tex";

    @AfterMethod
    public void cleanUp() {
        final File exFile = new File(MainTest.EX_FILE);
        final File solFile = new File(MainTest.SOL_FILE);
        if (exFile.exists()) {
            exFile.delete();
        }
        if (solFile.exists()) {
            solFile.delete();
        }
    }

    @Test
    public void fromOnesComplement() throws IOException {
        if (!Main.STUDENT_MODE) {
            Main.main(
                new String[]{
                    "-a", "fromonescompl",
                    "-e", MainTest.EX_FILE,
                    "-t", MainTest.SOL_FILE,
                    "-i", "-3,5;1,4;-111,8"
                }
            );
            try (
                BufferedReader exReader = new BufferedReader(new FileReader(MainTest.EX_FILE));
                BufferedReader solReader = new BufferedReader(new FileReader(MainTest.SOL_FILE));
            ) {
                Assert.assertEquals(exReader.readLine(), "Die Binärzahl 11100 im 5-bit Einerkomplement hat den Wert:\\\\[2ex]");
                Assert.assertEquals(exReader.readLine(), "\\begin{tikzpicture}");
                Assert.assertEquals(exReader.readLine(), "[node/.style={rectangle,draw=black,thick,inner sep=5pt}, node distance=0.25 and 0]");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n0) {\\phantom{0000}};");
                Assert.assertEquals(exReader.readLine(), "\\end{tikzpicture}");
                Assert.assertEquals(exReader.readLine(), "");
                Assert.assertEquals(exReader.readLine(), "Die Binärzahl 0001 im 4-bit Einerkomplement hat den Wert:\\\\[2ex]");
                Assert.assertEquals(exReader.readLine(), "\\begin{tikzpicture}");
                Assert.assertEquals(exReader.readLine(), "[node/.style={rectangle,draw=black,thick,inner sep=5pt}, node distance=0.25 and 0]");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n2) {\\phantom{0000}};");
                Assert.assertEquals(exReader.readLine(), "\\end{tikzpicture}");
                Assert.assertEquals(exReader.readLine(), "");
                Assert.assertEquals(exReader.readLine(), "Die Binärzahl 10010000 im 8-bit Einerkomplement hat den Wert:\\\\[2ex]");
                Assert.assertEquals(exReader.readLine(), "\\begin{tikzpicture}");
                Assert.assertEquals(exReader.readLine(), "[node/.style={rectangle,draw=black,thick,inner sep=5pt}, node distance=0.25 and 0]");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n4) {\\phantom{0000}};");
                Assert.assertEquals(exReader.readLine(), "\\end{tikzpicture}");
                Assert.assertEquals(exReader.readLine(), "");
                Assert.assertNull(exReader.readLine());

                Assert.assertEquals(solReader.readLine(), "\\begin{tikzpicture}");
                Assert.assertEquals(solReader.readLine(), "[node/.style={rectangle,draw=black,thick,inner sep=5pt}, node distance=0.25 and 0]");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n1) {\\phantom{00}-3};");
                Assert.assertEquals(solReader.readLine(), "\\end{tikzpicture}");
                Assert.assertEquals(solReader.readLine(), "");
                Assert.assertEquals(solReader.readLine(), "\\begin{tikzpicture}");
                Assert.assertEquals(solReader.readLine(), "[node/.style={rectangle,draw=black,thick,inner sep=5pt}, node distance=0.25 and 0]");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n3) {\\phantom{000}1};");
                Assert.assertEquals(solReader.readLine(), "\\end{tikzpicture}");
                Assert.assertEquals(solReader.readLine(), "");
                Assert.assertEquals(solReader.readLine(), "\\begin{tikzpicture}");
                Assert.assertEquals(solReader.readLine(), "[node/.style={rectangle,draw=black,thick,inner sep=5pt}, node distance=0.25 and 0]");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n5) {-111};");
                Assert.assertEquals(solReader.readLine(), "\\end{tikzpicture}");
                Assert.assertEquals(solReader.readLine(), "");
                Assert.assertNull(solReader.readLine());
            }
        }
    }

    @Test
    public void fromTwosComplement() throws IOException {
        if (!Main.STUDENT_MODE) {
            Main.main(
                new String[]{
                    "-a", "fromtwoscompl",
                    "-e", MainTest.EX_FILE,
                    "-t", MainTest.SOL_FILE,
                    "-i", "-3,5;1,4;-111,8"
                }
            );
            try (
                BufferedReader exReader = new BufferedReader(new FileReader(MainTest.EX_FILE));
                BufferedReader solReader = new BufferedReader(new FileReader(MainTest.SOL_FILE));
            ) {
                Assert.assertEquals(exReader.readLine(), "Die Binärzahl 11101 im 5-bit Zweierkomplement hat den Wert:\\\\[2ex]");
                Assert.assertEquals(exReader.readLine(), "\\begin{tikzpicture}");
                Assert.assertEquals(exReader.readLine(), "[node/.style={rectangle,draw=black,thick,inner sep=5pt}, node distance=0.25 and 0]");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n0) {\\phantom{0000}};");
                Assert.assertEquals(exReader.readLine(), "\\end{tikzpicture}");
                Assert.assertEquals(exReader.readLine(), "");
                Assert.assertEquals(exReader.readLine(), "Die Binärzahl 0001 im 4-bit Zweierkomplement hat den Wert:\\\\[2ex]");
                Assert.assertEquals(exReader.readLine(), "\\begin{tikzpicture}");
                Assert.assertEquals(exReader.readLine(), "[node/.style={rectangle,draw=black,thick,inner sep=5pt}, node distance=0.25 and 0]");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n2) {\\phantom{0000}};");
                Assert.assertEquals(exReader.readLine(), "\\end{tikzpicture}");
                Assert.assertEquals(exReader.readLine(), "");
                Assert.assertEquals(exReader.readLine(), "Die Binärzahl 10010001 im 8-bit Zweierkomplement hat den Wert:\\\\[2ex]");
                Assert.assertEquals(exReader.readLine(), "\\begin{tikzpicture}");
                Assert.assertEquals(exReader.readLine(), "[node/.style={rectangle,draw=black,thick,inner sep=5pt}, node distance=0.25 and 0]");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n4) {\\phantom{0000}};");
                Assert.assertEquals(exReader.readLine(), "\\end{tikzpicture}");
                Assert.assertEquals(exReader.readLine(), "");
                Assert.assertNull(exReader.readLine());

                Assert.assertEquals(solReader.readLine(), "\\begin{tikzpicture}");
                Assert.assertEquals(solReader.readLine(), "[node/.style={rectangle,draw=black,thick,inner sep=5pt}, node distance=0.25 and 0]");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n1) {\\phantom{00}-3};");
                Assert.assertEquals(solReader.readLine(), "\\end{tikzpicture}");
                Assert.assertEquals(solReader.readLine(), "");
                Assert.assertEquals(solReader.readLine(), "\\begin{tikzpicture}");
                Assert.assertEquals(solReader.readLine(), "[node/.style={rectangle,draw=black,thick,inner sep=5pt}, node distance=0.25 and 0]");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n3) {\\phantom{000}1};");
                Assert.assertEquals(solReader.readLine(), "\\end{tikzpicture}");
                Assert.assertEquals(solReader.readLine(), "");
                Assert.assertEquals(solReader.readLine(), "\\begin{tikzpicture}");
                Assert.assertEquals(solReader.readLine(), "[node/.style={rectangle,draw=black,thick,inner sep=5pt}, node distance=0.25 and 0]");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n5) {-111};");
                Assert.assertEquals(solReader.readLine(), "\\end{tikzpicture}");
                Assert.assertEquals(solReader.readLine(), "");
                Assert.assertNull(solReader.readLine());
            }
        }
    }

    @Test
    public void insertionsort() throws IOException {
        if (!Main.STUDENT_MODE) {
            Main.main(
                new String[]{"-a", "insertionsort", "-e", MainTest.EX_FILE, "-t", MainTest.SOL_FILE, "-i", "3,5,1,4,2"}
            );
            try (
                BufferedReader exReader = new BufferedReader(new FileReader(MainTest.EX_FILE));
                BufferedReader solReader = new BufferedReader(new FileReader(MainTest.SOL_FILE));
            ) {
                Assert.assertEquals(exReader.readLine(), "\\begin{tikzpicture}");
                Assert.assertEquals(exReader.readLine(), "[node/.style={rectangle,draw=black,thick,inner sep=5pt}, node distance=0.25 and 0]");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n0) {\\phantom{0}3};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n1) [right=of n0] {\\phantom{0}5};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n2) [right=of n1] {\\phantom{0}1};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n3) [right=of n2] {\\phantom{0}4};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n4) [right=of n3] {\\phantom{0}2};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n30) [below=of n0] {\\phantom{00}};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n31) [right=of n30] {\\phantom{00}};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n32) [right=of n31] {\\phantom{00}};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n33) [right=of n32] {\\phantom{00}};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n34) [right=of n33] {\\phantom{00}};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n35) [below=of n30] {\\phantom{00}};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n36) [right=of n35] {\\phantom{00}};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n37) [right=of n36] {\\phantom{00}};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n38) [right=of n37] {\\phantom{00}};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n39) [right=of n38] {\\phantom{00}};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n40) [below=of n35] {\\phantom{00}};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n41) [right=of n40] {\\phantom{00}};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n42) [right=of n41] {\\phantom{00}};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n43) [right=of n42] {\\phantom{00}};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n44) [right=of n43] {\\phantom{00}};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n45) [below=of n40] {\\phantom{00}};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n46) [right=of n45] {\\phantom{00}};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n47) [right=of n46] {\\phantom{00}};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n48) [right=of n47] {\\phantom{00}};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n49) [right=of n48] {\\phantom{00}};");
                Assert.assertEquals(exReader.readLine(), "\\end{tikzpicture}");
                Assert.assertNull(exReader.readLine());

                Assert.assertEquals(solReader.readLine(), "\\begin{tikzpicture}");
                Assert.assertEquals(solReader.readLine(), "[node/.style={rectangle,draw=black,thick,inner sep=5pt}, node distance=0.25 and 0]");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n5) {\\phantom{0}3};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n6) [right=of n5] {\\phantom{0}5};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n7) [right=of n6] {\\phantom{0}1};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n8) [right=of n7] {\\phantom{0}4};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n9) [right=of n8] {\\phantom{0}2};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n10) [below=of n5] {\\phantom{0}3};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n11) [right=of n10] {\\phantom{0}5};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n12) [right=of n11] {\\phantom{0}1};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n13) [right=of n12] {\\phantom{0}4};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n14) [right=of n13] {\\phantom{0}2};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n15) [below=of n10] {\\phantom{0}1};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n16) [right=of n15] {\\phantom{0}3};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n17) [right=of n16] {\\phantom{0}5};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n18) [right=of n17] {\\phantom{0}4};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n19) [right=of n18] {\\phantom{0}2};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n20) [below=of n15] {\\phantom{0}1};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n21) [right=of n20] {\\phantom{0}3};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n22) [right=of n21] {\\phantom{0}4};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n23) [right=of n22] {\\phantom{0}5};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n24) [right=of n23] {\\phantom{0}2};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n25) [below=of n20] {\\phantom{0}1};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n26) [right=of n25] {\\phantom{0}2};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n27) [right=of n26] {\\phantom{0}3};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n28) [right=of n27] {\\phantom{0}4};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n29) [right=of n28] {\\phantom{0}5};");
                Assert.assertEquals(solReader.readLine(), "\\end{tikzpicture}");
                Assert.assertNull(solReader.readLine());
            }
        }
    }

    @BeforeMethod
    public void prepare() {
        TikZUtils.reset();
    }

    @Test
    public void toOnesComplement() throws IOException {
        if (!Main.STUDENT_MODE) {
            Main.main(
                new String[]{
                    "-a", "toonescompl",
                    "-e", MainTest.EX_FILE,
                    "-t", MainTest.SOL_FILE,
                    "-i", "3,5;-1,4;-2,8"
                }
            );
            try (
                BufferedReader exReader = new BufferedReader(new FileReader(MainTest.EX_FILE));
                BufferedReader solReader = new BufferedReader(new FileReader(MainTest.SOL_FILE));
            ) {
                Assert.assertEquals(exReader.readLine(), "Die Zahl 3 wird im 5-bit Einerkomplement dargestellt als:\\\\[2ex]");
                Assert.assertEquals(exReader.readLine(), "\\begin{tikzpicture}");
                Assert.assertEquals(exReader.readLine(), "[node/.style={rectangle,draw=black,thick,inner sep=5pt}, node distance=0.25 and 0]");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n0) {\\phantom{0}};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n1) [right=of n0] {\\phantom{0}};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n2) [right=of n1] {\\phantom{0}};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n3) [right=of n2] {\\phantom{0}};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n4) [right=of n3] {\\phantom{0}};");
                Assert.assertEquals(exReader.readLine(), "\\end{tikzpicture}");
                Assert.assertEquals(exReader.readLine(), "");
                Assert.assertEquals(exReader.readLine(), "Die Zahl -1 wird im 4-bit Einerkomplement dargestellt als:\\\\[2ex]");
                Assert.assertEquals(exReader.readLine(), "\\begin{tikzpicture}");
                Assert.assertEquals(exReader.readLine(), "[node/.style={rectangle,draw=black,thick,inner sep=5pt}, node distance=0.25 and 0]");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n10) {\\phantom{0}};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n11) [right=of n10] {\\phantom{0}};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n12) [right=of n11] {\\phantom{0}};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n13) [right=of n12] {\\phantom{0}};");
                Assert.assertEquals(exReader.readLine(), "\\end{tikzpicture}");
                Assert.assertEquals(exReader.readLine(), "");
                Assert.assertEquals(exReader.readLine(), "Die Zahl -2 wird im 8-bit Einerkomplement dargestellt als:\\\\[2ex]");
                Assert.assertEquals(exReader.readLine(), "\\begin{tikzpicture}");
                Assert.assertEquals(exReader.readLine(), "[node/.style={rectangle,draw=black,thick,inner sep=5pt}, node distance=0.25 and 0]");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n18) {\\phantom{0}};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n19) [right=of n18] {\\phantom{0}};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n20) [right=of n19] {\\phantom{0}};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n21) [right=of n20] {\\phantom{0}};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n22) [right=of n21] {\\phantom{0}};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n23) [right=of n22] {\\phantom{0}};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n24) [right=of n23] {\\phantom{0}};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n25) [right=of n24] {\\phantom{0}};");
                Assert.assertEquals(exReader.readLine(), "\\end{tikzpicture}");
                Assert.assertEquals(exReader.readLine(), "");
                Assert.assertNull(exReader.readLine());

                Assert.assertEquals(solReader.readLine(), "\\begin{tikzpicture}");
                Assert.assertEquals(solReader.readLine(), "[node/.style={rectangle,draw=black,thick,inner sep=5pt}, node distance=0.25 and 0]");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n5) {0};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n6) [right=of n5] {0};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n7) [right=of n6] {0};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n8) [right=of n7] {1};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n9) [right=of n8] {1};");
                Assert.assertEquals(solReader.readLine(), "\\end{tikzpicture}");
                Assert.assertEquals(solReader.readLine(), "");
                Assert.assertEquals(solReader.readLine(), "\\begin{tikzpicture}");
                Assert.assertEquals(solReader.readLine(), "[node/.style={rectangle,draw=black,thick,inner sep=5pt}, node distance=0.25 and 0]");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n14) {1};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n15) [right=of n14] {1};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n16) [right=of n15] {1};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n17) [right=of n16] {0};");
                Assert.assertEquals(solReader.readLine(), "\\end{tikzpicture}");
                Assert.assertEquals(solReader.readLine(), "");
                Assert.assertEquals(solReader.readLine(), "\\begin{tikzpicture}");
                Assert.assertEquals(solReader.readLine(), "[node/.style={rectangle,draw=black,thick,inner sep=5pt}, node distance=0.25 and 0]");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n26) {1};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n27) [right=of n26] {1};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n28) [right=of n27] {1};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n29) [right=of n28] {1};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n30) [right=of n29] {1};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n31) [right=of n30] {1};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n32) [right=of n31] {0};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n33) [right=of n32] {1};");
                Assert.assertEquals(solReader.readLine(), "\\end{tikzpicture}");
                Assert.assertEquals(solReader.readLine(), "");
                Assert.assertNull(solReader.readLine());
            }
        }
    }

    @Test
    public void toTwosComplement() throws IOException {
        if (!Main.STUDENT_MODE) {
            Main.main(
                new String[]{
                    "-a", "totwoscompl",
                    "-e", MainTest.EX_FILE,
                    "-t", MainTest.SOL_FILE,
                    "-i", "-3,5;1,4;-111,8"
                }
            );
            try (
                BufferedReader exReader = new BufferedReader(new FileReader(MainTest.EX_FILE));
                BufferedReader solReader = new BufferedReader(new FileReader(MainTest.SOL_FILE));
            ) {
                Assert.assertEquals(exReader.readLine(), "Die Zahl -3 wird im 5-bit Zweierkomplement dargestellt als:\\\\[2ex]");
                Assert.assertEquals(exReader.readLine(), "\\begin{tikzpicture}");
                Assert.assertEquals(exReader.readLine(), "[node/.style={rectangle,draw=black,thick,inner sep=5pt}, node distance=0.25 and 0]");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n0) {\\phantom{0}};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n1) [right=of n0] {\\phantom{0}};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n2) [right=of n1] {\\phantom{0}};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n3) [right=of n2] {\\phantom{0}};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n4) [right=of n3] {\\phantom{0}};");
                Assert.assertEquals(exReader.readLine(), "\\end{tikzpicture}");
                Assert.assertEquals(exReader.readLine(), "");
                Assert.assertEquals(exReader.readLine(), "Die Zahl 1 wird im 4-bit Zweierkomplement dargestellt als:\\\\[2ex]");
                Assert.assertEquals(exReader.readLine(), "\\begin{tikzpicture}");
                Assert.assertEquals(exReader.readLine(), "[node/.style={rectangle,draw=black,thick,inner sep=5pt}, node distance=0.25 and 0]");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n10) {\\phantom{0}};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n11) [right=of n10] {\\phantom{0}};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n12) [right=of n11] {\\phantom{0}};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n13) [right=of n12] {\\phantom{0}};");
                Assert.assertEquals(exReader.readLine(), "\\end{tikzpicture}");
                Assert.assertEquals(exReader.readLine(), "");
                Assert.assertEquals(exReader.readLine(), "Die Zahl -111 wird im 8-bit Zweierkomplement dargestellt als:\\\\[2ex]");
                Assert.assertEquals(exReader.readLine(), "\\begin{tikzpicture}");
                Assert.assertEquals(exReader.readLine(), "[node/.style={rectangle,draw=black,thick,inner sep=5pt}, node distance=0.25 and 0]");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n18) {\\phantom{0}};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n19) [right=of n18] {\\phantom{0}};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n20) [right=of n19] {\\phantom{0}};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n21) [right=of n20] {\\phantom{0}};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n22) [right=of n21] {\\phantom{0}};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n23) [right=of n22] {\\phantom{0}};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n24) [right=of n23] {\\phantom{0}};");
                Assert.assertEquals(exReader.readLine(), "\\node[node] (n25) [right=of n24] {\\phantom{0}};");
                Assert.assertEquals(exReader.readLine(), "\\end{tikzpicture}");
                Assert.assertEquals(exReader.readLine(), "");
                Assert.assertNull(exReader.readLine());

                Assert.assertEquals(solReader.readLine(), "\\begin{tikzpicture}");
                Assert.assertEquals(solReader.readLine(), "[node/.style={rectangle,draw=black,thick,inner sep=5pt}, node distance=0.25 and 0]");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n5) {1};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n6) [right=of n5] {1};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n7) [right=of n6] {1};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n8) [right=of n7] {0};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n9) [right=of n8] {1};");
                Assert.assertEquals(solReader.readLine(), "\\end{tikzpicture}");
                Assert.assertEquals(solReader.readLine(), "");
                Assert.assertEquals(solReader.readLine(), "\\begin{tikzpicture}");
                Assert.assertEquals(solReader.readLine(), "[node/.style={rectangle,draw=black,thick,inner sep=5pt}, node distance=0.25 and 0]");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n14) {0};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n15) [right=of n14] {0};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n16) [right=of n15] {0};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n17) [right=of n16] {1};");
                Assert.assertEquals(solReader.readLine(), "\\end{tikzpicture}");
                Assert.assertEquals(solReader.readLine(), "");
                Assert.assertEquals(solReader.readLine(), "\\begin{tikzpicture}");
                Assert.assertEquals(solReader.readLine(), "[node/.style={rectangle,draw=black,thick,inner sep=5pt}, node distance=0.25 and 0]");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n26) {1};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n27) [right=of n26] {0};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n28) [right=of n27] {0};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n29) [right=of n28] {1};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n30) [right=of n29] {0};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n31) [right=of n30] {0};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n32) [right=of n31] {0};");
                Assert.assertEquals(solReader.readLine(), "\\node[node] (n33) [right=of n32] {1};");
                Assert.assertEquals(solReader.readLine(), "\\end{tikzpicture}");
                Assert.assertEquals(solReader.readLine(), "");
                Assert.assertNull(solReader.readLine());
            }
        }
    }

}
