package org.glycam.molecular_dynamics.glycoprotein_builder;

import org.glycam.molecular_dynamics.glycan_builder.GlycanSession;
import org.glycam.pdb.PdbFilePB.GlycoproteinBuildInfo;
import org.glycam.pdb.PdbFilePB.GlycosylationSpot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

public class GlycosylationSiteList implements Iterable<GlycosylationSite> {
    private List<GlycosylationSite> sites;

    public GlycosylationSiteList() {
        sites = new ArrayList<GlycosylationSite>();
    }

    void add(GlycosylationSpot spot) {
        sites.add(new GlycosylationSite(spot));
    }

    public int size() {
        return sites.size();
    }

    public GlycosylationSite get(int index) {
        return sites.get(index);
    }

    public void attachGlycanSession(int index, GlycanSession glycanSession) {
        sites.get(index).setGlycanSession(glycanSession);
    }

    public void removeGlycanSession(int index) {
        sites.get(index).removeGlycanSession();
    }

    void sortByLikeliness() {
        Collections.sort(sites, new Comparator<GlycosylationSite>() {
            @Override
            public int compare(GlycosylationSite lhs, GlycosylationSite rhs) {
                return GlycosylationSite.compareByLikeliness(lhs, rhs);
            }
        });
    }

    public boolean isAnyGlycosylated() {
        for (GlycosylationSite site : this) {
            if (site.isGlycosylated())
                return true;
        }
        return false;
    }

    public boolean isAnyWithChainId() {
        for (GlycosylationSite site : this) {
            if (!site.getSpot().getInfo().getChainId().equals(" "))
                return true;
        }
        return false;
    }

    public boolean isAnyWithICode() {
        for (GlycosylationSite site : this) {
            if (!site.getSpot().getInfo().getICode().equals(" "))
                return true;
        }
        return false;
    }

    public boolean isAnyGlycosylatedAndWithChainId() {
        for (GlycosylationSite site : this) {
            if (site.isGlycosylated() && !site.getSpot().getInfo().getChainId().equals(" "))
                return true;
        }
        return false;
    }

    public boolean isAnyGlycosylatedAndWithICode() {
        for (GlycosylationSite site : this) {
            if (site.isGlycosylated() && !site.getSpot().getInfo().getICode().equals(" "))
                return true;
        }
        return false;
    }

    void addGlycosylatedSitesToBuildInfo(GlycoproteinBuildInfo.Builder info) {
        for (GlycosylationSite site : this) {
            if (site.isGlycosylated()) {
                info.addGlycosylation(site.createGlycosylationInfo());
            }
        }
    }

    @Override
    public Iterator<GlycosylationSite> iterator() {
        return sites.iterator();
    }

    // for jstl
    public Iterator<GlycosylationSite> getIterator() {
        return sites.iterator();
    }
}
