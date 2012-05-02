package org.glycam;

import java.util.ArrayList;
import java.util.List;

// This class is suitable for use as a JavaBean.
public class Linkage {
    private final String name;
    private final boolean flexibleOmega;
    private final boolean flexiblePhi;
    
    private List<Double> omegaValues = new ArrayList<Double>();
    private List<Double> phiValues = new ArrayList<Double>();
    private List<Double> psiValues = new ArrayList<Double>();

    // If this is true, the user has set the phi value to phiValues[0]
    private boolean phiSet = false;
    private boolean psiSet = false;
    private boolean omegaSet = false;

    public Linkage(String name, boolean flexibleOmega, boolean flexiblePhi) {
        this.name = name;
        this.flexibleOmega = flexibleOmega;
        this.flexiblePhi = flexiblePhi;
        
    }

    public Linkage(Linkage linkage) {
        this.name = linkage.name;
        this.flexibleOmega = linkage.flexibleOmega;
        this.flexiblePhi = linkage.flexiblePhi;
        this.omegaValues = new ArrayList<Double>(linkage.omegaValues);
        this.phiValues = new ArrayList<Double>(linkage.phiValues);
        this.psiValues = new ArrayList<Double>(linkage.psiValues);
        this.phiSet = linkage.phiSet;
        this.psiSet = linkage.psiSet;
        this.omegaSet = linkage.omegaSet;
    }

    // Accessors
    public String getName() { return name; }

    public boolean isPhiSet() { return phiSet; }
    public boolean isPsiSet() { return psiSet; }
    public boolean isOmegaSet() { return omegaSet; }

    public boolean isFlexibleOmega() { return flexibleOmega; }
    public boolean isFlexiblePhi() { return flexiblePhi; }

    public List<Double> getOmegaValues() { return omegaValues; }
    public List<Double> getPhiValues() { return phiValues; }
    public List<Double> getPsiValues() { return psiValues; }

    // Mutators
    public void setPhiSet(boolean value) { phiSet = value; }
    public void setPsiSet(boolean value) { psiSet = value; }
    public void setOmegaSet(boolean value) { omegaSet = value; }

    public void addPhiValue(double value) { phiValues.add(value); }
    public void addPsiValue(double value) { psiValues.add(value); }
    public void addOmegaValue(double value) { omegaValues.add(value); }
}
