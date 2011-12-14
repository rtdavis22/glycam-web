SUBDIRS = deps/gmml deps/protobuf src/cpp/ .

all-local:
	$(MAKE) -C src/java
	mkdir -p bin
	mkdir -p lib
	cp src/cpp/draw_glycan bin
	cp src/cpp/getlinkages bin
	cp src/cpp/validate_structure bin
	cp src/cpp/mpi/build_torsions_mpi bin
	cp src/java/target/glycam-web.jar lib

clean-local:
	$(MAKE) -C src/java clean

MAINTAINERCLEANFILES = \
  aclocal.m4 \
  config.guess \
  config.h \
  config.h.in \
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
