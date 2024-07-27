package exercisegenerator.structures.graphs;

import java.util.*;

import exercisegenerator.*;
import exercisegenerator.structures.*;

/**
 * Represents a vertex position in a grid layout.
 */
public class VertexGridPosition {

    /**
     * The position east of the current one.
     */
    public VertexGridPosition east;

    /**
     * Does this position allow diagonal edges?
     */
    public final boolean hasDiagonals;

    /**
     * The position north of the current one.
     */
    public VertexGridPosition north;

    /**
     * The position northeast of the current one.
     */
    public VertexGridPosition northeast;

    /**
     * The position northwest of the current one.
     */
    public VertexGridPosition northwest;

    /**
     * The position south of the current one.
     */
    public VertexGridPosition south;

    /**
     * The position southeast of the current one.
     */
    public VertexGridPosition southeast;

    /**
     * The position southwest of the current one.
     */
    public VertexGridPosition southwest;

    /**
     * The position west of the current one.
     */
    public VertexGridPosition west;

    /**
     * The x coordinate of this position.
     */
    public final int x;

    /**
     * The y coordinate of this position.
     */
    public final int y;

    /**
     * @param xPos The x coordinate of this position.
     * @param yPos The y coordinate of this position.
     * @param withDiagonals Does this position allow diagonal edges?
     */
    public VertexGridPosition(final int xPos, final int yPos, final boolean withDiagonals) {
        this.x = xPos;
        this.y = yPos;
        this.hasDiagonals = withDiagonals;
    }

    /**
     * @return A list of all existing positions next to the current one.
     */
    public List<Pair<GridCoordinates, Boolean>> getExistingPositions() {
        final List<Pair<GridCoordinates, Boolean>> res = new ArrayList<Pair<GridCoordinates, Boolean>>();
        if (this.north != null) {
            res.add(
                new Pair<GridCoordinates, Boolean>(
                    new GridCoordinates(this.x, this.y - 1),
                    !this.hasDiagonals
                )
            );
        }
        if (this.east != null) {
            res.add(
                new Pair<GridCoordinates, Boolean>(
                    new GridCoordinates(this.x + 1, this.y),
                    !this.hasDiagonals
                )
            );
        }
        if (this.south != null) {
            res.add(
                new Pair<GridCoordinates, Boolean>(
                    new GridCoordinates(this.x, this.y + 1),
                    !this.hasDiagonals
                )
            );
        }
        if (this.west != null) {
            res.add(
                new Pair<GridCoordinates, Boolean>(
                    new GridCoordinates(this.x - 1, this.y),
                    !this.hasDiagonals
                )
            );
        }
        if (this.hasDiagonals) {
            if (this.northeast != null) {
                res.add(
                    new Pair<GridCoordinates, Boolean>(new GridCoordinates(this.x + 1, this.y - 1), true)
                );
            }
            if (this.southeast != null) {
                res.add(
                    new Pair<GridCoordinates, Boolean>(new GridCoordinates(this.x + 1, this.y + 1), true)
                );
            }
            if (this.southwest != null) {
                res.add(
                    new Pair<GridCoordinates, Boolean>(new GridCoordinates(this.x - 1, this.y + 1), true)
                );
            }
            if (this.northwest != null) {
                res.add(
                    new Pair<GridCoordinates, Boolean>(new GridCoordinates(this.x - 1, this.y - 1), true)
                );
            }
        }
        return res;
    }

    /**
     * @return A list of all free positions next to the current one.
     */
    public List<Pair<GridCoordinates, Boolean>> getFreePositions() {
        final List<Pair<GridCoordinates, Boolean>> res = new ArrayList<Pair<GridCoordinates, Boolean>>();
        if (this.north == null) {
            res.add(
                new Pair<GridCoordinates, Boolean>(
                    new GridCoordinates(this.x, this.y - 1),
                    !this.hasDiagonals
                )
            );
        }
        if (this.east == null) {
            res.add(
                new Pair<GridCoordinates, Boolean>(
                    new GridCoordinates(this.x + 1, this.y),
                    !this.hasDiagonals
                )
            );
        }
        if (this.south == null) {
            res.add(
                new Pair<GridCoordinates, Boolean>(
                    new GridCoordinates(this.x, this.y + 1),
                    !this.hasDiagonals
                )
            );
        }
        if (this.west == null) {
            res.add(
                new Pair<GridCoordinates, Boolean>(
                    new GridCoordinates(this.x - 1, this.y),
                    !this.hasDiagonals
                )
            );
        }
        if (this.hasDiagonals) {
            if (this.northeast == null) {
                res.add(
                    new Pair<GridCoordinates, Boolean>(new GridCoordinates(this.x + 1, this.y - 1), true)
                );
            }
            if (this.southeast == null) {
                res.add(
                    new Pair<GridCoordinates, Boolean>(new GridCoordinates(this.x + 1, this.y + 1), true)
                );
            }
            if (this.southwest == null) {
                res.add(
                    new Pair<GridCoordinates, Boolean>(new GridCoordinates(this.x - 1, this.y + 1), true)
                );
            }
            if (this.northwest == null) {
                res.add(
                    new Pair<GridCoordinates, Boolean>(new GridCoordinates(this.x - 1, this.y - 1), true)
                );
            }
        }
        return res;
    }

    /**
     * @return True if there is at least one free position next to the current one.
     */
    public boolean hasFreePosition() {
        return
            this.north == null
            || this.east == null
            || this.south == null
            || this.west == null
            || (
                this.hasDiagonals
                && (
                    this.northeast == null
                    || this.southeast == null
                    || this.southwest == null
                    || this.northwest == null
                )
            );
    }

    /**
     * @return A random free position next to the current position.
     */
    public Pair<GridCoordinates, Boolean> randomFreePosition() {
        final List<Pair<GridCoordinates, Boolean>> res = this.getFreePositions();
        return res.get(Main.RANDOM.nextInt(res.size()));
    }

}
