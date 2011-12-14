package molecular_dynamics;

// This class is intended to be used as a bean.
public class LinkageValues {
    private double phi;
    private double psi;
    private double omega;

    private boolean phiSet;
    private boolean psiSet;
    private boolean omegaSet;

    public LinkageValues() {
        phiSet = false;
        psiSet = false;
        omegaSet = false;
    }

    public double getPhi() { return phi; }
    public double getPsi() { return psi; }
    public double getOmega() { return omega; }

    public boolean isPhiSet() { return phiSet; }
    public boolean isPsiSet() { return psiSet; }
    public boolean isOmegaSet() { return omegaSet; }

    public void setPhi(double value) {
        phi = value;
        phiSet = true;
    }

    public void setPsi(double value) {
        psi = value;
        psiSet = true;
    }

    public void setOmega(double value) {
        omega = value;
        omegaSet = true;
    }
}
