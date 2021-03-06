package org.glycam.molecular_dynamics;

/**
 * Parameters for solvation.
 *
 * @author Robert Davis
 */
public class SolvationSettings {
    /**
     * The default solvent buffer, in Angstroms.
     */
    public static final double DEFAULT_BUFFER = 8.0;

    /**
     * The default minimum distance between a solvent atom and a solute atom.
     */
    public static final double DEFAULT_CLOSENESS = 1.5;

    public enum Shape { RECTANGULAR, CUBIC }

    /**
     * The amount of solvent buffer, in Angstroms, on each side.
     */
    private double buffer; 

    /**
     * The closest distance, in Angstroms, a solvent atom can be to a solute atom.
     */
    private double closeness;

    private Shape shape;

    public SolvationSettings() {
        this.shape = Shape.RECTANGULAR;
        this.buffer = DEFAULT_BUFFER;
        this.closeness = DEFAULT_CLOSENESS;
    }

    /**
     * Initializes the solvation settings.
     *
     * @param buffer the amount of solvent, in Angstroms, on each side.
     * @param closeness the closest a solvent atom can be to a solute atom.
     * @param shape the shape of the solvent.
     */
    public SolvationSettings(double buffer, double closeness, Shape shape) {
        this.buffer = buffer;
        this.closeness = closeness;
        this.shape = shape;
    }

    /**
     * Returns the amount of solvent buffer in Angstroms on each side.
     */
    public double getBuffer() {
        return buffer;
    }

    /**
     * Returns the closest distance, in Angstoms, a solvent atom can be to a solute atom.
     */
    public double getCloseness() {
        return closeness;
    }

    public Shape getShape() {
        return shape;
    }

    /**
     * Sets the amount of solvent buffer, in Angstoms, on each side.
     */
    public void setBuffer(double buffer) {
        this.buffer = buffer;
    }

    /**
     * Sets the closest distance, in Angstoms, a solvent atom can be to a solute atom.
     */
    public void setCloseness(double closeness) {
        this.closeness = closeness;
    }

    public void setShape(Shape shape) {
        this.shape = shape;
    }
}
