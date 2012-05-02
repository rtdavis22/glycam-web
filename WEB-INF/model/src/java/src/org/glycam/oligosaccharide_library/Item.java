package org.glycam.oligosaccharide_library;

/**
 * An oligosaccharide in the oligosaccharide library.
 *
 * This class is suitable for use as a JavaBean.
 *
 * @author Robert Davis
 */
public class Item {
    /**
     * A unique ID assigned to this sequence.
     */
    private int id;

    /**
     * The sequence in GLYCAM condensed nomenclature.
     * TODO: Use GlycanSequence instead.
     */
    private String sequence;

    /**
     * A name for the sequence.
     */
    private String name;

    /**
     * Additional information associated with the sequence.
     */
    private String description;

    /**
     * Creates an oligosaccharide library item.
     */
    public Item(int id, String sequence, String name, String description) {
        this.id = id;
        this.sequence = sequence;
        this.name = name;
        this.description = description;
    }

    public int getId() { return id; }
    public String getSequence() { return sequence; }
    public String getName() { return name; }
    public String getDescription() { return description; }

    public void setId(int id) { this.id = id; }
    public void setSequence(String sequence) { this.sequence = sequence; }
    public void setName(String name) { this.name = name; }
    public void setDescription(String description) { this.description = description; }
}
