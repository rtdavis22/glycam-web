JAVA_FILES = \
  controller/GlobeData.java \
  controller/HttpUtils.java \
  controller/RegisterCoordinate.java \
  controller/Utils.java \
  controller/molecular_dynamics/SolvationOptions.java \
  controller/molecular_dynamics/glycoprotein_builder/AttachGlycans.java \
  controller/molecular_dynamics/glycoprotein_builder/ChooseLocations.java \
  controller/molecular_dynamics/glycoprotein_builder/ChooseRotamers.java \
  controller/molecular_dynamics/glycoprotein_builder/DonePreprocessing.java \
  controller/molecular_dynamics/glycoprotein_builder/DownloadFiles.java \
  controller/molecular_dynamics/glycoprotein_builder/GlycanOptions.java \
  controller/molecular_dynamics/glycoprotein_builder/InputGlycan.java \
  controller/molecular_dynamics/glycoprotein_builder/SetAngles.java \
  controller/molecular_dynamics/glycoprotein_builder/UploadPdb.java \
  controller/molecular_dynamics/glycoprotein_builder/Utils.java \
  controller/molecular_dynamics/oligosaccharide_builder/BuildGlycan.java \
  controller/molecular_dynamics/oligosaccharide_builder/BuildProgress.java \
  controller/molecular_dynamics/oligosaccharide_builder/ChooseRotamers.java \
  controller/molecular_dynamics/oligosaccharide_builder/DownloadFiles.java \
  controller/molecular_dynamics/oligosaccharide_builder/Options.java \
  controller/molecular_dynamics/oligosaccharide_builder/SetGlycosidicAngles.java \
  controller/oligosaccharide_library/Detail.java \
  controller/oligosaccharide_library/OligosaccharideMenu.java \
  controller/oligosaccharide_library/OligosaccharidePage.java \
  controller/pdb_preprocessor/CysBonds.java \
  controller/pdb_preprocessor/HisMappings.java \
  controller/pdb_preprocessor/Menu.java \
  controller/pdb_preprocessor/MissingResidues.java \
  controller/pdb_preprocessor/RemovedAtoms.java \
  controller/pdb_preprocessor/TerminalResidues.java \
  controller/pdb_preprocessor/UnrecognizedAtoms.java \
  controller/pdb_preprocessor/UnrecognizedResidues.java \
  controller/pdb_preprocessor/UploadPdb.java \
  controller/pdb_preprocessor/Utils.java \
  controller/services/DrawGlycan.java

JAR_FILES = \
  model/deps/protobuf/java/target/protobuf-java-2.4.2-pre.jar \
  model/lib/glycam-web.jar \
  lib/commons-fileupload-1.2.2.jar \
  lib/mysql-connector-java-5.1.18-bin.jar \
  ../../../lib/servlet-api.jar

# This is a little trick to make the classpath argument
EMPTY :=
SPACE := $(EMPTY) $(EMPTY)
CLASSPATH := $(subst $(SPACE),:,$(JAR_FILES))
JFLAGS = -classpath $(CLASSPATH)

all: $(JAVA_FILES) $(JAR_FILES)
	$(MAKE) -C model
	cp model/lib/glycam-web.jar lib
	cp model/deps/protobuf/java/target/protobuf-java-2.4.2-pre.jar lib
	rm -rf classes/controller
	mkdir -p classes
	javac $(JFLAGS) -d classes $(JAVA_FILES)
	$(MAKE) -C docs

clean:
	$(MAKE) -C model clean
	rm -rf classes/controller
	rm -f lib/glycam-web.jar
	$(MAKE) -C docs clean
