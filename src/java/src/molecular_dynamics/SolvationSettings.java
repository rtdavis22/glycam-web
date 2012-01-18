package molecular_dynamics;

public class SolvationSettings {
    public enum Shape { Rectangular, Cubic } 

    private double buffer; 
    private double closeness;
    private Shape shape;

    public SolvationSettings() {
        this.shape = Shape.Rectangular;
        this.buffer = 8.0;
        this.closeness = 0.2;
    }

    public SolvationSettings(double buffer, double closeness, Shape shape) {
        this.buffer = buffer;
        this.closeness = closeness;
        this.shape = shape;
    }

    // Accessors
    public double getBuffer() { return buffer; }
    public double getCloseness() { return closeness; }
    public Shape getShape() { return shape; }

    // Mutators
    public void setBuffer(double buffer) { this.buffer = buffer; }
    public void setCloseness(double closeness) { this.closeness = closeness; }
    public void setShape(Shape shape) { this.shape = shape; }
}