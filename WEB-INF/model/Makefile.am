SUBDIRS = deps/gmml deps/protobuf src/cpp/ .

all-local:
	$(MAKE) -C src/java
	mkdir -p bin
	cd bin && ln -fs ../src/cpp/build_glycoprotein build_glycoprotein
	cd bin && ln -fs ../src/cpp/draw_glycan draw_glycan
	cd bin && ln -fs ../src/cpp/get_glycoprotein_info get_glycoprotein_info
	cd bin && ln -fs ../src/cpp/get_linkages get_linkages
	cd bin && ln -fs ../src/cpp/get_pdb_preprocessing_info get_pdb_preprocessing_info
	cd bin && ln -fs ../src/cpp/mpi/build_torsions_mpi build_torsions_mpi
	cd bin && ln -fs ../src/cpp/validate_sequence validate_sequence
	mkdir -p lib
	cp src/java/target/glycam-web.jar lib

docs:
	javadoc -d docs org org.glycam org.glycam.configuration org.glycam.data org.glycam.molecular_dynamics org.glycam.oligosaccharide_library org.glycam.molecular_dynamics.glycan_builder org.glycam.molecular_dynamics.glycoprotein_builder


clean-local:
	$(MAKE) -C src/java clean
	rm -rf bin lib

MAINTAINERCLEANFILES = \
  aclocal.m4 \
  config.guess \
  config.h \
  config.h.in \
  config.h.in~ \
  config.log \
  config.status \
  config.sub \
  configure \
  depcomp \
  install-sh \
  libtool \
  ltmain.sh \
  Makefile \
  Makefile.in \
  missing \
  stamp-h1
