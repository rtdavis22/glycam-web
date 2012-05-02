package org.glycam.pdb.preprocessing;

import org.glycam.pdb.PdbFilePB.*;

import java.util.List;

public class HisMapping {
    private HisResidue residue;

    private HisMappingType mappedType;

    HisMapping(HisResidue residue) {
        this.residue = residue;
        List<HisMappingType> types = residue.getPossibleMappingTypeList();
        if (types.contains(HisMappingType.HIE)) {
            mappedType = HisMappingType.HIE;
        } else if (types.size() > 0) {
            mappedType = types.get(0);
        } else {
            mappedType = HisMappingType.UNKNOWN;
        }
    }

    public HisMappingType getMappedType() {
        return mappedType;
    }

    public void setMappedType(HisMappingType mappedType) {
        this.mappedType = mappedType;
    }

    public List<HisMappingType> getPossibleMappingTypes() {
        return residue.getPossibleMappingTypeList();
    }

    public PdbResidueInfo getResidueInfo() {
        return residue.getResidueInfo();
    }
}
