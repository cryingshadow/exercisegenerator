package exercisegenerator.structures.algebra;

public interface MatrixTerm {

    boolean isCompound();

    boolean isDirectlyEvaluable();

    default String toCompoundLaTeX() {
        return this.isCompound() ? String.format("\\left(%s\\right)", this.toLaTeX()) : this.toLaTeX();
    }

    String toLaTeX();

    Matrix toMatrix();

}
