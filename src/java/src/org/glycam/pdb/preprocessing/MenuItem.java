package org.glycam.pdb.preprocessing;

public interface MenuItem {
    public enum Status { GOOD, NEEDS_ATTENTION, BAD }

    boolean isEnabled();
    String getUrl();
    String getName();
    String getSummary();
    Status getStatus();
}
