PROTO_FILES = \
  src/molecular_dynamics/oligosaccharide_builder/BuildInfoPB.java \
  src/molecular_dynamics/oligosaccharide_builder/BuildResultsPB.java

PROTO_CMD = \
  ../../deps/protobuf/src/protoc \
  --java_out=./src \
  --proto_path=../proto/

PROTO_JAR = ../../deps/protobuf/java/target/protobuf-java-2.4.2-pre.jar

all: $(PROTO_FILES) target/glycam-web.jar

src/molecular_dynamics/oligosaccharide_builder/BuildInfoPB.java : \
                ../proto/BuildInfoPB.proto
	$(PROTO_CMD) ../proto/BuildInfoPB.proto

src/molecular_dynamics/oligosaccharide_builder/BuildResultsPB.java : \
                ../proto/BuildResultsPB.proto
	$(PROTO_CMD) ../proto/BuildResultsPB.proto

# TODO: Make this so it works with different versions.
$(PROTO_JAR):
	cd ../../deps/protobuf/java && mvn install

CLASS_DEST = target/class

JAVA_FILES = \
  src/configuration/Configuration.java \
  src/cplusplus/CPP.java \
  src/molecular_dynamics/Linkage.java \
  src/molecular_dynamics/LinkageValues.java \
  src/molecular_dynamics/SolvationSettings.java \
  src/molecular_dynamics/oligosaccharide_builder/BuildInfoPB.java \
  src/molecular_dynamics/oligosaccharide_builder/BuildRequest.java \
  src/molecular_dynamics/oligosaccharide_builder/OligosaccharideSession.java \
  src/molecular_dynamics/oligosaccharide_builder/BuildResultsPB.java \
  src/molecular_dynamics/oligosaccharide_builder/ResultStructure.java \
  src/oligosaccharide_library/Item.java \
  src/org/glycam/Logging.java \
  src/org/glycam/Sequence.java

JFLAGS = -classpath $(PROTO_JAR)

target/glycam-web.jar: $(JAVA_FILES) $(PROTO_JAR)
	mkdir -p $(CLASS_DEST)
	javac $(JFLAGS) -d $(CLASS_DEST) $(JAVA_FILES)
	cd $(CLASS_DEST) && jar cf $(@F) *
	mv $(CLASS_DEST)/$(@F) $(@D)

clean:
	rm -rf target
	rm -f src/molecular_dynamics/oligosaccharide_builder/BuildInfoPB.java
	rm -f src/molecular_dynamics/oligosaccharide_builder/BuildResultsPB.java
