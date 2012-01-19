// Author: Robert Davis

package molecular_dynamics.oligosaccharide_builder;

import molecular_dynamics.LinkageValues;

import java.util.SortedMap;

// This class represents a particular structure that's built by the oligosaccharide builder.
// It it suitable for use as a JavaBean.
public class ResultStructure {
    // A map from the residue index to the possible linkage values of the residue.
    private SortedMap<Integer, LinkageValues> angles;

    // The 0-based index of the structure representing the order it was generated.
    private int index;

    // The minimized energy of the structure.
    private double energy;

    public ResultStructure(SortedMap<Integer, LinkageValues> angles, int index, double energy) {
        this.angles = angles;
        this.index = index;
        this.energy = energy;
    }

    public SortedMap<Integer, LinkageValues> getAngles() { return angles; }
    public int getIndex() { return index; }
    public double getEnergy() { return energy; }

    public void setAngles(SortedMap<Integer, LinkageValues> angles) { this.angles = angles; }
    public void setIndex(int index) { this.index = index; }
    public void setEnergy(double energy) { this.energy = energy; }
}
