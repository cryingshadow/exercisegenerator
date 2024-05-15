# exercisegenerator

Command line tool to automatically create exercises and corresponding solutions for algorithmic problems as LaTeX files.

The project is built using gradle. In order to work on it with Eclipse, use the gradle wrapper with the eclipse task and then import the project as an existing gradle project. To generate a runnable jar file, execute the gradle jar task (i.e., `./gradlew jar`). The generated jar file can then be found in the `exgen/build/libs` folder.

Type `java -jar exercisegenerator.jar -h` to see the available commands for the generator.

To build the executable jar file, you need JDK 17 or higher and Gradle. To convert the resulting exercise and solution files to PDFs, run `pdflatex` (from your favourite LaTeX distribution) on these files.

To simply use the tool, you can download the pre-built executable jar file in the root folder. You need Java version 17 or higher and a LaTeX distribution.

For instance, to create an exercise sheet with 12 random exercises from a list `L` of algorithms, store the exercise sheet in the file `ex.tex` and the solution in the file `sol.tex`, use the following command:
`java -jar exercisegenerator.jar -a L -n 12 -e ex.tex -t sol.tex`
