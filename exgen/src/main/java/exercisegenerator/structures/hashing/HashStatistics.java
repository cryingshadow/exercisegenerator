package exercisegenerator.structures.hashing;

public record HashStatistics(int numberOfCollisions, int maxNumberOfProbingsForSameValue) {

    @Override
    public String toString() {
        return String.format(
            "collisions: %d, max. number of probings for same value: %d",
            this.numberOfCollisions,
            this.maxNumberOfProbingsForSameValue
        );
    }

}