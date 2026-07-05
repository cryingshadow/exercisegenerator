package exercisegenerator.structures.graphs.petrinets;

import java.util.*;

public record PlaceCandidate(Set<String> precedingActivities, Set<String> followingActivities) {

}
