package molecular_dynamics;

import java.util.ArrayList;

public class Linkage {
    private String name;
    private boolean flexibleOmega;
    private boolean flexiblePhi;
    
    private ArrayList<Double> omegaValues;
    private ArrayList<Double> phiValues;
    private ArrayList<Double> psiValues;

    // If this is true, the user has set the phi value to phiValues[0]
    private boolean phiSet;
    private boolean psiSet;
    private boolean omegaSet;

    public Linkage(String name, boolean flexibleOmega, boolean flexiblePhi) {
        this.name = name;
        this.flexibleOmega = flexibleOmega;
        this.flexiblePhi = flexiblePhi;
        omegaValues = new ArrayList<Double>();
        phiValues = new ArrayList<Double>();
        psiValues = new ArrayList<Double>();
        phiSet = false;
        psiSet = false;
        omegaSet = false;
    }

    public void addPhiValue(double value) { phiValues.add(value); }
    public void addPsiValue(double value) { psiValues.add(value); }
    public void addOmegaValue(double value) { omegaValues.add(value); }

    // Accessors
    public String getName() { return name; }

    public boolean isPhiSet() { return phiSet; }
    public boolean isPsiSet() { return psiSet; }
    public boolean isOmegaSet() { return omegaSet; }

    public boolean isFlexibleOmega() { return flexibleOmega; }
    public boolean isFlexiblePhi() { return flexiblePhi; }

    // Mutators
    public void setName(String name) { this.name = name; }

    public void setPhiSet(boolean value) { phiSet = value; }
    public void setPsiSet(boolean value) { psiSet = value; }
    public void setOmegaSet(boolean value) { omegaSet = value; }

    public void setFlexibleOmega(boolean value) { flexibleOmega = value; }
    public void setFlexiblePhi(boolean value) { flexiblePhi = value; }

    public ArrayList<Double> getOmegaValues() { return omegaValues; }
    public ArrayList<Double> getPhiValues() { return phiValues; }
    public ArrayList<Double> getPsiValues() { return psiValues; }

    // Remove this
    public String getString() {
        String ret = "";
        if (phiValues.size() > 0)
            ret += phiValues.get(0);
        for (int i = 1; i < phiValues.size(); i++)
            ret += "," + phiValues.get(i);
        ret += ":";
        if (psiValues.size() > 0)
            ret += psiValues.get(0);
        for (int i = 1; i < psiValues.size(); i++)
            ret += "," + psiValues.get(i);
        ret += ":";
        if (omegaValues.size() > 0)
            ret += omegaValues.get(0);
        for (int i = 1; i < omegaValues.size(); i++)
            ret += "," + omegaValues.get(i);
        return ret;
    }
}
