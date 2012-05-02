package org.glycam.molecular_dynamics.glycan_builder;

import org.glycam.LinkageValues;

import java.util.Map;

/**
 * This class represents a particular conformation built by the glycan builder.
 *
 * It is suitable for use as a JavaBean.
 *
 * @author Robert Davis
 */
public class ResultStructure {
    /**
     * A map from residue indices to possible linkage values of the residues,
     */
    private final Map<Integer, LinkageValues> angles;

    /**
     * The 0-based index of the structure representing the order it was generated.
     */
    private int index;

    /**
     * The minimized energy of the structure.
     */
    private final double energy;

    /**
     * The Boltzmann probability associated with this structure.
     */
    private double boltzmann;

    /**
     * Constructs a result structure.
     */
    ResultStructure(Map<Integer, LinkageValues> angles, int index, double energy,
                    double boltzmann) {
        this.angles = angles;
        this.index = index;
        this.energy = energy;
        this.boltzmann = boltzmann;
    }

    public Map<Integer, LinkageValues> getAngles() {
        return angles;
    }

    public int getIndex() {
        return index;
    }

    public double getEnergy() {
        return energy;
    }

    public double getBoltzmann() {
        return boltzmann;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public void setBoltzmann(double boltzmann) {
        this.boltzmann = boltzmann;
    }
}
