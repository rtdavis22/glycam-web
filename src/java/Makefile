# TODO: This file can be simplified, especially the protocol buffer stuff.
PROTO_FILES = \
  src/org/glycam/molecular_dynamics/glycoprotein_builder/PdbInfoPB.java \
  src/org/glycam/molecular_dynamics/oligosaccharide_builder/BuildInfoPB.java \
  src/org/glycam/molecular_dynamics/oligosaccharide_builder/BuildResultsPB.java

PROTO_CMD = \
  ../../deps/protobuf/src/protoc \
  --java_out=./src \
  --proto_path=../proto/

PROTO_JAR = ../../deps/protobuf/java/target/protobuf-java-2.4.2-pre.jar

all: $(PROTO_FILES) target/glycam-web.jar

src/org/glycam/molecular_dynamics/oligosaccharide_builder/BuildInfoPB.java : \
                ../proto/BuildInfoPB.proto
	$(PROTO_CMD) ../proto/BuildInfoPB.proto

src/org/glycam/molecular_dynamics/oligosaccharide_builder/BuildResultsPB.java : \
                ../proto/BuildResultsPB.proto
	$(PROTO_CMD) ../proto/BuildResultsPB.proto

src/org/glycam/molecular_dynamics/glycoprotein_builder/PdbInfoPB.java : \
                ../proto/PdbInfoPB.proto
	$(PROTO_CMD) ../proto/PdbInfoPB.proto

# TODO: Make this so it works with different versions.
$(PROTO_JAR):
	cd ../../deps/protobuf/java && mvn install

CLASS_DEST = target/class

JAVA_FILES = \
  src/org/glycam/configuration/Configuration.java \
  src/org/glycam/CPP.java \
  src/org/glycam/data/DatabaseUtils.java \
  src/org/glycam/Logging.java \
  src/org/glycam/molecular_dynamics/glycoprotein_builder/GlycoproteinSession.java \
  src/org/glycam/molecular_dynamics/glycoprotein_builder/PdbInfoPB.java \
  src/org/glycam/molecular_dynamics/Linkage.java \
  src/org/glycam/molecular_dynamics/LinkageValues.java \
  src/org/glycam/molecular_dynamics/SolvationSettings.java \
  src/org/glycam/molecular_dynamics/oligosaccharide_builder/BuildInfoPB.java \
  src/org/glycam/molecular_dynamics/oligosaccharide_builder/BuildRequest.java \
  src/org/glycam/molecular_dynamics/oligosaccharide_builder/OligosaccharideSession.java \
  src/org/glycam/molecular_dynamics/oligosaccharide_builder/BuildResultsPB.java \
  src/org/glycam/molecular_dynamics/oligosaccharide_builder/ResultStructure.java \
  src/org/glycam/oligosaccharide_library/Item.java \
  src/org/glycam/Sequence.java

JFLAGS = -classpath $(PROTO_JAR)

target/glycam-web.jar: $(JAVA_FILES) $(PROTO_JAR)
	rm -rf $(CLASS_DEST)
	mkdir -p $(CLASS_DEST)
	javac $(JFLAGS) -d $(CLASS_DEST) $(JAVA_FILES)
	cd $(CLASS_DEST) && jar cf $(@F) *
	mv $(CLASS_DEST)/$(@F) $(@D)

clean:
	rm -rf target
	rm -f src/org/glycam/molecular_dynamics/glycoprotein_builder/PdbInfoPB.java
	rm -f src/org/glycam/molecular_dynamics/oligosaccharide_builder/BuildInfoPB.java
	rm -f src/org/glycam/molecular_dynamics/oligosaccharide_builder/BuildResultsPB.java
