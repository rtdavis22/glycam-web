SUBDIRS = .

CXX = mpicxx

bin_PROGRAMS = build_torsions_mpi

build_torsions_mpi_SOURCES = \
  build_torsions_mpi.cc \
  ../BuildInfoPB.pb.cc \
  ../BuildInfoPB.pb.h \
  ../BuildResultsPB.pb.cc \
  ../BuildResultsPB.pb.h

build_torsions_mpi_CPPFLAGS = \
  -I$(top_builddir)/deps/gmml/include \
  -I$(top_builddir)/deps/protobuf/src
 
build_torsions_mpi_LDADD = \
  $(top_builddir)/deps/gmml/libgmml.a \
  $(top_builddir)/deps/protobuf/src/libprotobuf.la

MAINTAINERCLEANFILES = \
  $(BUILT_SOURCES) \
  Makefile \
  Makefile.in
