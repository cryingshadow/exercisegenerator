package exercisegenerator;

import java.util.*;

/**
 * Represents a node position in a grid layout.
 * @author Thomas Stroeder
 * @version 1.0
 */
public class NodeGridPosition {

    /**
     * The position east of the current one.
     */
    public NodeGridPosition east;

    /**
     * Does this position allow diagonal edges?
     */
    public final boolean hasDiagonals;

    /**
     * The position north of the current one.
     */
    public NodeGridPosition north;

    /**
     * The position northeast of the current one.
     */
    public NodeGridPosition northeast;

    /**
     * The position northwest of the current one.
     */
    public NodeGridPosition northwest;

    /**
     * The position south of the current one.
     */
    public NodeGridPosition south;

    /**
     * The position southeast of the current one.
     */
    public NodeGridPosition southeast;

    /**
     * The position southwest of the current one.
     */
    public NodeGridPosition southwest;

    /**
     * The position west of the current one.
     */
    public NodeGridPosition west;

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
    public NodeGridPosition(final int xPos, final int yPos, final boolean withDiagonals) {
        this.x = xPos;
        this.y = yPos;
        this.hasDiagonals = withDiagonals;
    }

    /**
     * @return A list of all existing positions next to the current one.
     */
    public List<Pair<Pair<Integer, Integer>, Boolean>> getExistingPositions() {
        final List<Pair<Pair<Integer, Integer>, Boolean>> res = new ArrayList<Pair<Pair<Integer, Integer>, Boolean>>();
        if (this.north != null) {
            res.add(
                new Pair<Pair<Integer, Integer>, Boolean>(
                    new Pair<Integer, Integer>(this.x, this.y - 1),
                    !this.hasDiagonals
                )
            );
        }
        if (this.east != null) {
            res.add(
                new Pair<Pair<Integer, Integer>, Boolean>(
                    new Pair<Integer, Integer>(this.x + 1, this.y),
                    !this.hasDiagonals
                )
            );
        }
        if (this.south != null) {
            res.add(
                new Pair<Pair<Integer, Integer>, Boolean>(
                    new Pair<Integer, Integer>(this.x, this.y + 1),
                    !this.hasDiagonals
                )
            );
        }
        if (this.west != null) {
            res.add(
                new Pair<Pair<Integer, Integer>, Boolean>(
                    new Pair<Integer, Integer>(this.x - 1, this.y),
                    !this.hasDiagonals
                )
            );
        }
        if (this.hasDiagonals) {
            if (this.northeast != null) {
                res.add(
                    new Pair<Pair<Integer, Integer>, Boolean>(new Pair<Integer, Integer>(this.x + 1, this.y - 1), true)
                );
            }
            if (this.southeast != null) {
                res.add(
                    new Pair<Pair<Integer, Integer>, Boolean>(new Pair<Integer, Integer>(this.x + 1, this.y + 1), true)
                );
            }
            if (this.southwest != null) {
                res.add(
                    new Pair<Pair<Integer, Integer>, Boolean>(new Pair<Integer, Integer>(this.x - 1, this.y + 1), true)
                );
            }
            if (this.northwest != null) {
                res.add(
                    new Pair<Pair<Integer, Integer>, Boolean>(new Pair<Integer, Integer>(this.x - 1, this.y - 1), true)
                );
            }
        }
        return res;
    }

    /**
     * @return A list of all free positions next to the current one.
     */
    public List<Pair<Pair<Integer, Integer>, Boolean>> getFreePositions() {
        final List<Pair<Pair<Integer, Integer>, Boolean>> res = new ArrayList<Pair<Pair<Integer, Integer>, Boolean>>();
        if (this.north == null) {
            res.add(
                new Pair<Pair<Integer, Integer>, Boolean>(
                    new Pair<Integer, Integer>(this.x, this.y - 1),
                    !this.hasDiagonals
                )
            );
        }
        if (this.east == null) {
            res.add(
                new Pair<Pair<Integer, Integer>, Boolean>(
                    new Pair<Integer, Integer>(this.x + 1, this.y),
                    !this.hasDiagonals
                )
            );
        }
        if (this.south == null) {
            res.add(
                new Pair<Pair<Integer, Integer>, Boolean>(
                    new Pair<Integer, Integer>(this.x, this.y + 1),
                    !this.hasDiagonals
                )
            );
        }
        if (this.west == null) {
            res.add(
                new Pair<Pair<Integer, Integer>, Boolean>(
                    new Pair<Integer, Integer>(this.x - 1, this.y),
                    !this.hasDiagonals
                )
            );
        }
        if (this.hasDiagonals) {
            if (this.northeast == null) {
                res.add(
                    new Pair<Pair<Integer, Integer>, Boolean>(new Pair<Integer, Integer>(this.x + 1, this.y - 1), true)
                );
            }
            if (this.southeast == null) {
                res.add(
                    new Pair<Pair<Integer, Integer>, Boolean>(new Pair<Integer, Integer>(this.x + 1, this.y + 1), true)
                );
            }
            if (this.southwest == null) {
                res.add(
                    new Pair<Pair<Integer, Integer>, Boolean>(new Pair<Integer, Integer>(this.x - 1, this.y + 1), true)
                );
            }
            if (this.northwest == null) {
                res.add(
                    new Pair<Pair<Integer, Integer>, Boolean>(new Pair<Integer, Integer>(this.x - 1, this.y - 1), true)
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
     * @param gen A random number generator.
     * @return A random free position next to the current position.
     */
    public Pair<Pair<Integer, Integer>, Boolean> randomFreePosition(final Random gen) {
        final List<Pair<Pair<Integer, Integer>, Boolean>> res = this.getFreePositions();
        return res.get(gen.nextInt(res.size()));
    }

}
