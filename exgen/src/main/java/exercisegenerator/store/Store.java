package exercisegenerator.store;

import java.io.*;
import java.nio.file.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.*;

import clit.*;
import exercisegenerator.*;
import exercisegenerator.algorithms.*;
import exercisegenerator.io.*;

public class Store {

    public static final Store INSTANCE = new Store();

    private final List<AlgorithmObserver> algorithmObservers;

    private Set<Algorithm> algorithms;

    private Optional<File> chosenDirectory;

    private final List<DirectoryObserver> directoryObservers;

    private final Parameters<Flag> parameters;

    private final List<ParametersObserver> parametersObservers;

    private Store() {
        this.parameters = new Parameters<Flag>();
        this.algorithms = new LinkedHashSet<Algorithm>();
        this.chosenDirectory = Optional.empty();
        this.algorithmObservers = new LinkedList<AlgorithmObserver>();
        this.parametersObservers = new LinkedList<ParametersObserver>();
        this.directoryObservers = new LinkedList<DirectoryObserver>();
    }

    public void generatePDFs() throws Exception {
        final Path absolutePath = this.chosenDirectory.get().getAbsoluteFile().toPath();
        final Parameters<Flag> options = new Parameters<Flag>(this.parameters);
        options.put(Flag.ALGORITHM, this.algorithms.stream().map(alg -> alg.name).collect(Collectors.joining(",")));
        Path tmp = absolutePath.resolve("tmp");
        int i = 0;
        while (tmp.toFile().exists()) {
            tmp = absolutePath.resolve("tmp" + i++);
        }
        final File dir = tmp.toFile();
        dir.mkdir();
        final String texSuffix = ".tex";
        final String exercise = "exercise";
        final String solution = "solution";
        options.put(Flag.EXERCISE, tmp.resolve(exercise + texSuffix).toString());
        options.put(Flag.TARGET, tmp.resolve(solution + texSuffix).toString());
        options.put(Flag.EXECUTION_MODE, Main.STANDALONE);
        Main.main(options);
        Process exerciseProcess = Main.buildAndStartPDFLaTeXProcess(exercise, dir);
        Process solutionProcess = Main.buildAndStartPDFLaTeXProcess(solution, dir);
        exerciseProcess.waitFor(60, TimeUnit.SECONDS);
        exerciseProcess = Main.buildAndStartPDFLaTeXProcess(exercise, dir);
        solutionProcess.waitFor(60, TimeUnit.SECONDS);
        solutionProcess = Main.buildAndStartPDFLaTeXProcess(solution, dir);
        exerciseProcess.waitFor(60, TimeUnit.SECONDS);
        solutionProcess.waitFor(60, TimeUnit.SECONDS);
        final String pdfSuffix = ".pdf";
        File exerciseFile = absolutePath.resolve(exercise + pdfSuffix).toFile();
        File solutionFile = absolutePath.resolve(solution + pdfSuffix).toFile();
        i = 0;
        while (exerciseFile.exists() || solutionFile.exists()) {
            exerciseFile = absolutePath.resolve(exercise + i + pdfSuffix).toFile();
            solutionFile = absolutePath.resolve(solution + i + pdfSuffix).toFile();
            i++;
        }
        tmp.resolve(exercise + pdfSuffix).toFile().renameTo(exerciseFile);
        tmp.resolve(solution + pdfSuffix).toFile().renameTo(solutionFile);
        for (final File file : dir.listFiles()) {
            i = 0;
            while (!file.delete() && i < 100) {
                i++;
            }
        }
        i = 0;
        while (!dir.delete() && i < 100) {
            i++;
        }
    }

    public void registerAlgorithmObserver(final AlgorithmObserver observer) {
        this.algorithmObservers.add(observer);
        observer.notify(this.algorithms);
    }

    public void registerDirectoryObserver(final DirectoryObserver observer) {
        this.directoryObservers.add(observer);
        observer.notify(this.chosenDirectory);
    }

    public void registerParametersObserver(final ParametersObserver observer) {
        this.parametersObservers.add(observer);
        observer.notify(this.parameters);
    }

    public void removeParameter(final Flag flag) {
        this.parameters.remove(flag);
    }

    public void setAlgorithms(final Collection<Algorithm> algorithms) {
        if (!algorithms.containsAll(this.algorithms) || !this.algorithms.containsAll(algorithms)) {
            this.algorithms = new LinkedHashSet<Algorithm>(algorithms);
            for (final AlgorithmObserver observer : this.algorithmObservers) {
                observer.notify(this.algorithms);
            }
        }
    }

    public void setDirectory(final File directory) {
        this.chosenDirectory = Optional.of(directory);
        for (final DirectoryObserver observer : this.directoryObservers) {
            observer.notify(this.chosenDirectory);
        }
    }

    public void setParameter(final Flag key, final String value) {
        final String oldValue = this.parameters.put(key, value);
        if (oldValue != null && !oldValue.equals(value)) {
            for (final ParametersObserver observer : this.parametersObservers) {
                observer.notify(this.parameters);
            }
        }
    }

}
