SUBDIRS = deps/gmml deps/protobuf src/cpp/ .



all-local:
	$(MAKE) -C src/java
	mkdir -p bin
	cd bin && ln -fs ../src/cpp/draw_glycan draw_glycan
	cd bin && ln -fs ../src/cpp/get_linkages get_linkages
	cd bin && ln -fs ../src/cpp/get_pdb_info get_pdb_info
	cd bin && ln -fs ../src/cpp/mpi/build_torsions_mpi build_torsions_mpi
	cd bin && ln -fs ../src/cpp/validate_pdb validate_pdb
	cd bin && ln -fs ../src/cpp/validate_sequence validate_sequence
	mkdir -p lib
	cp src/java/target/glycam-web.jar lib

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
