BUILT_SOURCES = \
  src/org/glycam/molecular_dynamics/glycoprotein_builder/PdbInfoPB.java \
  src/org/glycam/molecular_dynamics/oligosaccharide_builder/BuildInfoPB.java \
  src/org/glycam/molecular_dynamics/oligosaccharide_builder/BuildResultsPB.java

PROTO_CMD = \
  ../../deps/protobuf/src/protoc \
  --java_out=./src \
  --proto_path=../proto/

PROTO_JAR = ../../deps/protobuf/java/target/protobuf-java-2.4.2-pre.jar

CLASS_DEST = target/class

JAVA_FILES = \
  $(BUILT_SOURCES) \
  src/org/glycam/CPP.java \
  src/org/glycam/Logging.java \
  src/org/glycam/Sequence.java \
  src/org/glycam/Utils.java \
  src/org/glycam/configuration/Configuration.java \
  src/org/glycam/data/DatabaseUtils.java \
  src/org/glycam/molecular_dynamics/Linkage.java \
  src/org/glycam/molecular_dynamics/LinkageValues.java \
  src/org/glycam/molecular_dynamics/SolvationSettings.java \
  src/org/glycam/molecular_dynamics/glycoprotein_builder/BuildRequest.java \
  src/org/glycam/molecular_dynamics/glycoprotein_builder/GlycoproteinSession.java \
  src/org/glycam/molecular_dynamics/oligosaccharide_builder/BuildRequest.java \
  src/org/glycam/molecular_dynamics/oligosaccharide_builder/OligosaccharideSession.java \
  src/org/glycam/molecular_dynamics/oligosaccharide_builder/ResultStructure.java \
  src/org/glycam/oligosaccharide_library/Item.java 

JFLAGS = -classpath $(PROTO_JAR)

all: target/glycam-web.jar

# TODO: Is there a good way to combine these 2 rules?
src/org/glycam/molecular_dynamics/glycoprotein_builder/%.java: ../proto/%.proto
	$(PROTO_CMD) $<

src/org/glycam/molecular_dynamics/oligosaccharide_builder/%.java: ../proto/%.proto
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
