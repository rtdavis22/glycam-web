BUILT_SOURCES = \
  src/org/glycam/molecular_dynamics/glycan_builder/BuildInfoPB.java \
  src/org/glycam/molecular_dynamics/glycan_builder/BuildResultsPB.java \
  src/org/glycam/pdb/PdbFilePB.java

PROTO_CMD = \
  ../../deps/protobuf/src/protoc \
  --java_out=./src \
  --proto_path=../proto/

PROTO_JAR = ../../deps/protobuf/java/target/protobuf-java-2.4.2-pre.jar

CLASS_DEST = target/class

JAVA_FILES = \
  $(BUILT_SOURCES) \
  src/org/glycam/CPP.java \
  src/org/glycam/FileUtils.java \
  src/org/glycam/GlycamSequence.java \
  src/org/glycam/Linkage.java \
  src/org/glycam/LinkageValues.java \
  src/org/glycam/Logging.java \
  src/org/glycam/Utils.java \
  src/org/glycam/configuration/Configuration.java \
  src/org/glycam/data/DatabaseUtils.java \
  src/org/glycam/molecular_dynamics/SolvationSettings.java \
  src/org/glycam/molecular_dynamics/glycan_builder/BuildRequest.java \
  src/org/glycam/molecular_dynamics/glycan_builder/GlycanSession.java \
  src/org/glycam/molecular_dynamics/glycan_builder/ResultStructure.java \
  src/org/glycam/molecular_dynamics/glycoprotein_builder/BuildRequest.java \
  src/org/glycam/molecular_dynamics/glycoprotein_builder/GlycoproteinSession.java \
  src/org/glycam/molecular_dynamics/glycoprotein_builder/GlycosylationSite.java \
  src/org/glycam/molecular_dynamics/glycoprotein_builder/GlycosylationSiteList.java \
  src/org/glycam/oligosaccharide_library/Item.java \
  src/org/glycam/pdb/preprocessing/CysBondsSection.java \
  src/org/glycam/pdb/preprocessing/HisMapping.java \
  src/org/glycam/pdb/preprocessing/HisMappingsSection.java \
  src/org/glycam/pdb/preprocessing/MenuItem.java \
  src/org/glycam/pdb/preprocessing/MissingResiduesSection.java \
  src/org/glycam/pdb/preprocessing/PreprocessingSection.java \
  src/org/glycam/pdb/preprocessing/PreprocessingSession.java \
  src/org/glycam/pdb/preprocessing/RemovedAtomsSection.java \
  src/org/glycam/pdb/preprocessing/TerminalResiduesSection.java \
  src/org/glycam/pdb/preprocessing/UnrecognizedAtomsSection.java \
  src/org/glycam/pdb/preprocessing/UnrecognizedResidue.java \
  src/org/glycam/pdb/preprocessing/UnrecognizedResiduesSection.java

JFLAGS = -classpath $(PROTO_JAR)

all: target/glycam-web.jar

# TODO: Is there a good way to combine these 2 rules?
src/org/glycam/molecular_dynamics/glycoprotein_builder/%.java: ../proto/%.proto
	$(PROTO_CMD) $<

src/org/glycam/molecular_dynamics/glycan_builder/%.java: ../proto/%.proto
	$(PROTO_CMD) $<

src/org/glycam/pdb/%.java: ../proto/%.proto
	$(PROTO_CMD) $<

# TODO: Make this so it works with different versions.
$(PROTO_JAR):
	cd ../../deps/protobuf/java && mvn install

target/glycam-web.jar: $(JAVA_FILES) $(PROTO_JAR)
	rm -rf $(CLASS_DEST)
	mkdir -p $(CLASS_DEST)
	javac $(JFLAGS) -d $(CLASS_DEST) $(JAVA_FILES)
	cd $(CLASS_DEST) && jar cf $(@F) *
	mv $(CLASS_DEST)/$(@F) $(@D)

clean:
	rm -rf target
	rm -f $(BUILT_SOURCES)
