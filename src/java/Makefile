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
  src/molecular_dynamics/oligosaccharide_builder/BuildResultsPB.java

JFLAGS = -classpath $(PROTO_JAR)

$(CLASS_DEST)/%.class: src/%.java $(PROTO_JAR)
	mkdir -p target
	javac $(JFLAGS) -d $(CLASS_DEST) $(JAVA_FILES)

CLASS_FILES = \
  $(CLASS_DEST)/configuration/Configuration.class \
  $(CLASS_DEST)/cplusplus/CPP.class \
  $(CLASS_DEST)/molecular_dynamics/Linkage.class \
  $(CLASS_DEST)/molecular_dynamics/LinkageValues.class \
  $(CLASS_DEST)/molecular_dynamics/SolvationSettings.class \
  $(CLASS_DEST)/molecular_dynamics/oligosaccharide_builder/BuildInfoPB.class \
  $(CLASS_DEST)/molecular_dynamics/oligosaccharide_builder/BuildRequest.class \
  $(CLASS_DEST)/molecular_dynamics/oligosaccharide_builder/OligosaccharideSession.class \
  $(CLASS_DEST)/molecular_dynamics/oligosaccharide_builder/BuildResultsPB.class
  
target/glycam-web.jar: $(CLASS_FILES)
	cd $(CLASS_DEST) && jar cf $(@F) *
	mv $(CLASS_DEST)/$(@F) $(@D)

clean:
	rm -rf target
	rm -f src/molecular_dynamics/oligosaccharide_builder/BuildInfoPB.java
	rm -r src/molecular_dynamics/oligosaccharide_builder/BuildResultsPB.java
