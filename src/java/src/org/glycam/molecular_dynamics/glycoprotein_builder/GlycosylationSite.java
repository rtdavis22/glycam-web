package org.glycam.molecular_dynamics.glycoprotein_builder;

import org.glycam.molecular_dynamics.glycan_builder.GlycanSession;
import org.glycam.pdb.PdbFilePB.GlycosylationInfo;
import org.glycam.pdb.PdbFilePB.GlycosylationSpot;

public class GlycosylationSite {
    public static final double MIN_LIKELY_SASA = 40.0;
    public static final double MAX_UNLIKELY_SASA = 20.0;

    private GlycosylationSpot spot;

    private GlycanSession glycanSession = null;

    public GlycosylationSite(GlycosylationSpot spot) {
        this.spot = spot;
    }

    public GlycosylationSpot getSpot() {
        return spot;
    }

    public boolean isGlycosylated() {
        return glycanSession != null;
    }

    public GlycanSession getGlycanSession() {
        return glycanSession;
    }

    void setGlycanSession(GlycanSession glycanSession) {
        this.glycanSession = glycanSession;
    }

    public void removeGlycanSession() {
        this.glycanSession = null;
    }

    public boolean isWithLowSasa() {
        return spot.getSasa() < MAX_UNLIKELY_SASA;
    }

    public boolean isWithHighSasa() {
        return spot.getSasa() > MIN_LIKELY_SASA;
    }

    public boolean isPotentialGlycosylationSite() {
        return !isGlycosylated() && !isWithLowSasa();
    }

    static int compareByLikeliness(GlycosylationSite lhs, GlycosylationSite rhs) {
        if (lhs.getSpot().getLikely() != rhs.getSpot().getLikely()) {
            return lhs.getSpot().getLikely()?-1:1;
        } else {
            return Double.compare(rhs.getSpot().getSasa(), lhs.getSpot().getSasa());
        }
    }

    GlycosylationInfo createGlycosylationInfo() {
        // Take some action if there isn't a glycan attached.
        GlycosylationInfo.Builder glycosylationInfo = GlycosylationInfo.newBuilder();
        glycosylationInfo.setSpot(spot);
        glycosylationInfo.setGlycan(glycanSession.buildProtocolBuffer());
        return glycosylationInfo.build();
    }
}
