package exercisegenerator.structures.graphs.layout;

import java.io.*;
import java.util.*;

import exercisegenerator.*;
import exercisegenerator.io.*;
import exercisegenerator.structures.graphs.*;

public class ForceGraphLayout<V, E> implements GraphLayout<V, E, Double> {

    private static final int ITERATIONS = 75;

    private static final double TEMPERATURE_FACTOR = 4.0;

//  private static final double DELTA = 0.001;
//
//  private static final Comparator<Coordinates2D<Double>> DISTANCE_COMPARATOR =
//      new Comparator<Coordinates2D<Double>>() {
//
//          @Override
//          public int compare(final Coordinates2D<Double> v1, final Coordinates2D<Double> v2) {
//              return Double.compare(v1.euclidianSize(), v2.euclidianSize());
//          }
//
//  };
//
//    private static Coordinates2D<Double> computeDistanceVector(
//        final Coordinates2D<Double> from,
//        final Coordinates2D<Double> to,
//        final double averageVertexWidth,
//        final double averageVertexHeight
//    ) {
//        final double halfWidth = averageVertexWidth / 2;
//        final double halfHeight = averageVertexHeight / 2;
//        final double fromNorth = from.y() + halfHeight;
//        final double fromEast = from.x() + halfWidth;
//        final double fromSouth = from.y() - halfHeight;
//        final double fromWest = from.x() - halfWidth;
//        final double toNorth = to.y() + halfHeight;
//        final double toEast = to.x() + halfWidth;
//        final double toSouth = to.y() - halfHeight;
//        final double toWest = to.x() - halfWidth;
//        if (
//            (fromNorth <= toNorth && fromNorth >= toSouth && fromEast >= toWest && fromEast <= toEast)
//            || (fromNorth <= toNorth && fromNorth >= toSouth && fromWest >= toWest && fromWest <= toEast)
//            || (fromSouth <= toNorth && fromSouth >= toSouth && fromEast >= toWest && fromEast <= toEast)
//            || (fromSouth <= toNorth && fromSouth >= toSouth && fromWest >= toWest && fromWest <= toEast)
//        ) {
//            return new Coordinates2D<Double>(0.0, 0.0);
//        }
//        final Coordinates2D<Double> vector = to.minus(from);
//        final List<Optional<Coordinates2D<Double>>> fromIntersections =
//            List.of(
//                ForceGraphLayout.computeIntersection(from, vector, fromNorth, false),
//                ForceGraphLayout.computeIntersection(from, vector, fromEast, true),
//                ForceGraphLayout.computeIntersection(from, vector, fromSouth, false),
//                ForceGraphLayout.computeIntersection(from, vector, fromWest, true)
//            );
//        final List<Optional<Coordinates2D<Double>>> toIntersections =
//            List.of(
//                ForceGraphLayout.computeIntersection(from, vector, toNorth, false),
//                ForceGraphLayout.computeIntersection(from, vector, toEast, true),
//                ForceGraphLayout.computeIntersection(from, vector, toSouth, false),
//                ForceGraphLayout.computeIntersection(from, vector, toWest, true)
//            );
//        final List<Coordinates2D<Double>> fromIntersectionsWithin =
//            fromIntersections.stream()
//            .filter(
//                coordinates ->
//                coordinates.isPresent()
//                && coordinates.get().y() <= fromNorth + ForceGraphLayout.DELTA
//                && coordinates.get().y() >= fromSouth - ForceGraphLayout.DELTA
//                && coordinates.get().x() <= fromEast + ForceGraphLayout.DELTA
//                && coordinates.get().x() >= fromWest - ForceGraphLayout.DELTA
//            ).map(Optional::get)
//            .toList();
//        final List<Coordinates2D<Double>> toIntersectionsWithin =
//            toIntersections.stream()
//            .filter(
//                coordinates ->
//                coordinates.isPresent()
//                && coordinates.get().y() <= toNorth + ForceGraphLayout.DELTA
//                && coordinates.get().y() >= toSouth - ForceGraphLayout.DELTA
//                && coordinates.get().x() <= toEast + ForceGraphLayout.DELTA
//                && coordinates.get().x() >= toWest - ForceGraphLayout.DELTA
//            ).map(Optional::get)
//            .toList();
//        final List<Coordinates2D<Double>> fromIntersectionCandidates =
//            fromIntersectionsWithin.isEmpty() ? List.of(from) : fromIntersectionsWithin;
//        final List<Coordinates2D<Double>> toIntersectionCandidates =
//            toIntersectionsWithin.isEmpty() ? List.of(to) : toIntersectionsWithin;
//        final List<Coordinates2D<Double>> distances = new LinkedList<Coordinates2D<Double>>();
//        for (final Coordinates2D<Double> fromIntersection : fromIntersectionCandidates) {
//            for (final Coordinates2D<Double> toIntersection : toIntersectionCandidates) {
//                distances.add(toIntersection.minus(fromIntersection));
//            }
//        }
//        return Collections.min(distances, ForceGraphLayout.DISTANCE_COMPARATOR);
//    }
//
//    private static Optional<Coordinates2D<Double>> computeIntersection(
//        final Coordinates2D<Double> from,
//        final Coordinates2D<Double> vector,
//        final double axisValue,
//        final boolean horizontalAxis
//    ) {
//        if (horizontalAxis) {
//            return vector.x() == 0.0 ?
//                Optional.empty() :
//                    Optional.of(vector.multiply((axisValue - from.x()) / vector.x()).plus(from));
//        }
//        return vector.y() == 0.0 ?
//            Optional.empty() :
//                Optional.of(vector.multiply((axisValue - from.y()) / vector.y()).plus(from));
//    }

    private final TikZStyle graphStyle;

    private final double optimalDistance;

    private final Map<Vertex<V>, Coordinates2D<Double>> vertexPositions;

    public ForceGraphLayout(
        final Graph<V, E> graph,
        final TikZStyle graphStyle,
        final double minHorizontalDistance,
        final double minVerticalDistance,
        final double preferredWidth,
        final double preferredHeight
    ) {
        this.vertexPositions = new LinkedHashMap<Vertex<V>, Coordinates2D<Double>>();
        this.graphStyle = graphStyle;
        final Set<Vertex<V>> vertices = graph.getVertices();
        final int numberOfVertices = vertices.size();
        if (numberOfVertices < 2) {
            this.optimalDistance = 0;
            for (final Vertex<V> vertex : vertices) {
                this.vertexPositions.put(vertex, new Coordinates2D<Double>(0.0, 0.0));
            }
        } else {
            final int maxNumberOfVerticesInPreferredSize =
                (int)(
                    ((preferredHeight - 1) / minVerticalDistance) * ((preferredWidth - 1) / minHorizontalDistance) / 4
                );
            final double width;
            final double height;
            if (numberOfVertices > maxNumberOfVerticesInPreferredSize) {
                width = Math.sqrt(numberOfVertices) * Math.max(minHorizontalDistance, minVerticalDistance) * 2;
                height = width;
            } else {
                width = preferredWidth;
                height = preferredHeight;
            }
            final double area = width * height;
            this.optimalDistance = Math.sqrt(area/numberOfVertices);
            this.placeVerticesCloseToGrid(vertices, width);
            double temperature = Math.max(width, height) / ForceGraphLayout.TEMPERATURE_FACTOR;
            final double cooling = temperature / (ForceGraphLayout.ITERATIONS - 5);
            for (int i = 0; i < ForceGraphLayout.ITERATIONS; i++) {
                final Map<Vertex<V>, Coordinates2D<Double>> displacement =
                    new LinkedHashMap<Vertex<V>, Coordinates2D<Double>>();
                for (final Vertex<V> vertex : vertices) {
                    for (final Vertex<V> other : vertices) {
                        this.applyRepulsiveForces(
                            vertex,
                            other,
                            displacement,
                            minHorizontalDistance,
                            minVerticalDistance
                        );
                    }
                }
                for (final Vertex<V> from : vertices) {
                    for (final Vertex<V> to : vertices) {
                        if (
                            from.id.compareTo(to.id) < 0
                            && (!graph.getEdges(from, to).isEmpty() || !graph.getEdges(to, from).isEmpty())
                        ) {
                            this.applyAttractiveForces(from, to, displacement);
                        }
                    }
                }
                for (final Vertex<V> vertex : vertices) {
                    this.applyCentricAttraction(vertex, width, height, displacement);
                    this.applyLimitedDisplacement(vertex, temperature, width, height, displacement, i);
                }
                if (
                    displacement.isEmpty()
                    || (
                        displacement.values().size() == 1
                        && displacement.values().contains(new Coordinates2D<Double>(0.0, 0.0))
                    )
                ) {
                    break;
                }
                if (i >= 5) {
                    temperature -= cooling;
                }
            }
        }
    }

    @Override
    public Coordinates2D<Double> getPosition(final Vertex<V> vertex) {
        return this.vertexPositions.get(vertex);
    }

    @Override
    public void printTikZ(final Graph<V, E> graph, final BufferedWriter writer) throws IOException {
        LaTeXUtils.printTikzBeginning(this.graphStyle, writer);
        for (final Vertex<V> vertex : graph.getVertices()) {
            writer.write(this.toTikZ(vertex, graph.isStartVertex(vertex), graph.isEndVertex(vertex)));
        }
        for (final Vertex<V> from : graph.getVertices()) {
            for (final Vertex<V> to : graph.getVertices()) {
                final Set<Edge<E, V>> edges = graph.getEdges(from, to);
                if (edges.isEmpty()) {
                    continue;
                }
                final List<E> labels = new LinkedList<E>();
                for (final Edge<E, V> edge : edges) {
                    if (edge.label().isPresent()) {
                        labels.add(edge.label().get());
                    }
                }
                writer.write(
                    GraphLayout.edgeFormat(
                        TikZStyle.EDGE_STYLE.style,
                        from.id.toString(),
                        labels,
                        to.id.toString()
                    )
                );
            }
        }
        LaTeXUtils.printTikzEnd(writer);
    }

    private void applyAttractiveForces(
        final Vertex<V> from,
        final Vertex<V> to,
        final Map<Vertex<V>, Coordinates2D<Double>> displacement
    ) {
        final Coordinates2D<Double> distance = this.vertexPositions.get(from).minus(this.vertexPositions.get(to));
        final double size = distance.euclidianSize();
        if (size != 0.0) {
            final Coordinates2D<Double> force = distance.multiply(this.attractiveForce(size) / size);
            displacement.merge(from, force.negate(), Coordinates2D::plus);
            displacement.merge(to, force, Coordinates2D::plus);
        }
    }

    private void applyCentricAttraction(
        final Vertex<V> vertex,
        final double width,
        final double height,
        final Map<Vertex<V>, Coordinates2D<Double>> displacement
    ) {
        final Coordinates2D<Double> distance =
            new Coordinates2D<Double>(width / 2, height / 2).minus(this.vertexPositions.get(vertex));
        final double size = distance.euclidianSize();
        if (size != 0.0) {
            displacement.merge(vertex, distance.multiply(this.attractiveForce(size) / (2 * size)), Coordinates2D::plus);
        }
    }

    private void applyLimitedDisplacement(
        final Vertex<V> vertex,
        final double temperature,
        final double width,
        final double height,
        final Map<Vertex<V>, Coordinates2D<Double>> displacement,
        final int iteration
    ) {
        final Coordinates2D<Double> pos = this.vertexPositions.get(vertex);
        final Coordinates2D<Double> disp = displacement.getOrDefault(vertex, new Coordinates2D<Double>(0.0, 0.0));
        final double size = disp.euclidianSize();
        if (iteration < 5) {
            this.vertexPositions.put(vertex, pos.plus(disp));
        } else {
            final Coordinates2D<Double> newPos =
                size == 0.0 ? pos : pos.plus(disp.multiply(Math.min(size, temperature)/size));
            this.vertexPositions.put(
                vertex,
                new Coordinates2D<Double>(
                    Math.min(width, Math.max(0, newPos.x())),
                    Math.min(height, Math.max(0, newPos.y()))
                )
            );
        }
    }

    private void applyRepulsiveForces(
        final Vertex<V> vertex,
        final Vertex<V> other,
        final Map<Vertex<V>, Coordinates2D<Double>> displacement,
        final double minHorizontalDistance,
        final double minVerticalDistance
    ) {
        if (vertex.equals(other)) {
            return;
        }
        final Coordinates2D<Double> pos1 = this.vertexPositions.get(vertex);
        final Coordinates2D<Double> pos2 = this.vertexPositions.get(other);
        final Coordinates2D<Double> distance = pos1.minus(pos2);
        final double size = distance.euclidianSize();
        if (size != 0.0) {
            displacement.merge(vertex, distance.multiply(this.repulsiveForce(size) / size), Coordinates2D::plus);
        }
        final double horizontalDistance = Math.abs(distance.x());
        final double verticalDistance = Math.abs(distance.y());
        if (horizontalDistance < minHorizontalDistance && verticalDistance < minVerticalDistance) {
            if (size == 0.0) {
                displacement.merge(
                    vertex,
                    new Coordinates2D<Double>(0.0, (vertex.id.compareTo(other.id) < 0 ? 1 : -1) * 1.0),
                    Coordinates2D::plus
                );
            } else {
                double angle = pos1.getAngle(pos2);
                if (angle > 180) {
                    angle -= 180;
                }
                if (angle <= 45 || angle >= 135) {
                    displacement.merge(
                        vertex,
                        new Coordinates2D<Double>(Math.signum(distance.x()) * minHorizontalDistance * 2, 0.0),
                        Coordinates2D::plus
                    );
                } else {
                    displacement.merge(
                        vertex,
                        new Coordinates2D<Double>(0.0, Math.signum(distance.y()) * minVerticalDistance * 2),
                        Coordinates2D::plus
                    );
                }
            }
        }
    }

    private double attractiveForce(final double distance) {
        return distance * distance / this.optimalDistance;
    }

    private void placeVerticesCloseToGrid(final Set<Vertex<V>> vertices, final double width) {
        //TODO better use spiral?
        double currentWidth = 0;
        double currentHeight = 0;
        final double distortion = this.optimalDistance * this.optimalDistance / width;
        int direction = 0;
        final List<Vertex<V>> halfInvertedVertices = new ArrayList<Vertex<V>>(vertices);
        final int half = vertices.size() / 2;
        for (int i = 0; i < half / 2; i++) {
            final Vertex<V> store = halfInvertedVertices.get(half - i);
            halfInvertedVertices.set(half - i, halfInvertedVertices.get(i));
            halfInvertedVertices.set(i, store);
        }
        for (final Vertex<V> vertex : halfInvertedVertices) {
            if (currentWidth > width) {
                currentWidth = 0;
                currentHeight += this.optimalDistance;
            }
            switch (direction) {
            case 1:
                currentHeight += distortion;
                break;
            case 2:
                currentHeight += distortion / 2;
                currentWidth += distortion / 2;
                break;
            case 3:
                currentWidth += distortion;
                break;
            case 4:
                currentWidth += distortion / 2;
                currentHeight -= distortion / 2;
                break;
            case 5:
                currentHeight -= distortion;
                break;
            case 6:
                currentHeight -= distortion / 2;
                currentWidth -= distortion / 2;
                break;
            case 7:
                currentWidth -= distortion;
                break;
            case 8:
                currentWidth -= distortion / 2;
                currentHeight += distortion / 2;
            }
            this.vertexPositions.put(vertex, new Coordinates2D<Double>(currentWidth, currentHeight));
            currentWidth += this.optimalDistance;
            direction = (direction + 1) % 9;
        }
    }

    private double repulsiveForce(final double distance) {
        return this.optimalDistance * this.optimalDistance / distance;
    }

    private String toTikZ(final Vertex<V> vertex, final boolean startVertex, final boolean endVertex) {
        final Coordinates2D<Double> coordinates = this.vertexPositions.get(vertex);
        final String id = vertex.id.toString();
        return String.format(
            Locale.US,
            "\\node[%s] (n%s) at (%.2f,%.2f) {%s};%s",
            endVertex ? "endnode" : "node",
            id,
            coordinates.x(),
            coordinates.y(),
            vertex.label.map(label -> label.toString()).orElse(""),
            startVertex ? GraphLayout.startNodeDecoration(id) : Main.lineSeparator
        );
    }

}
