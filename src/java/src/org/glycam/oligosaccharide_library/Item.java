// Author: Robert Davis

package org.glycam.oligosaccharide_library;

// This class represents a structure in the oligosaccharide library. It is suitable for use as
// a JavaBean.
public class Item {
    // The unique id assigned to this sequence.
    private int id;

    // The sequence in GLYCAM condensed nomenclature.
    private String sequence;

    // A name for the structure.
    private String name;

    // Additional information about the structure.
    private String description;

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
