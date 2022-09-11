# exercisegenerator

Command line tool to automatically create exercises and corresponding solutions for algorithmic problems as LaTeX files.

The project is built using gradle. In order to work on it with Eclipse, use the gradle wrapper with the eclipse task and then import the project as an existing gradle project. To generate a runnable jar file, execute the gradle jar task. The generated jar file can then be found in the exgen/build/libs folder.

Type java -jar exercisegenerator.jar -h to see the available commands for the generator.
