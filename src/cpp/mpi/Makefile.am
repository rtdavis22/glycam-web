SUBDIRS = .

CXX = mpicxx

BUILT_SOURCES = \
  BuildInfoPB.pb.cc \
  BuildInfoPB.pb.h \
  BuildResultsPB.pb.cc \
  BuildResultsPB.pb.h

# make a separate variable PROTOC
PROTO_CMD = \
  $(top_builddir)/deps/protobuf/src/protoc \
  --cpp_out=./ \
  --proto_path=$(top_builddir)/src/proto

BuildResultsPB.pb.cc BuildResultsPB.pb.h: \
                $(top_builddir)/src/proto/BuildResultsPB.proto
	$(PROTO_CMD) $(top_builddir)/src/proto/BuildResultsPB.proto

BuildInfoPB.pb.cc BuildInfoPB.pb.h: \
                $(top_builddir)/src/proto/BuildInfoPB.proto
	$(PROTO_CMD) $(top_builddir)/src/proto/BuildInfoPB.proto

bin_PROGRAMS = build_torsions_mpi

build_torsions_mpi_SOURCES = \
  build_torsions_mpi.cc \
  BuildInfoPB.pb.cc \
  BuildInfoPB.pb.h \
  BuildResultsPB.pb.cc \
  BuildResultsPB.pb.h

build_torsions_mpi_CPPFLAGS = \
  -I$(top_builddir)/deps/gmml/include \
  -I$(top_builddir)/deps/protobuf/src
 
build_torsions_mpi_LDADD = \
  $(top_builddir)/deps/gmml/libgmml.a \
  $(top_builddir)/deps/protobuf/src/libprotobuf.la

MAINTAINERCLEANFILES = \
  BuildInfoPB.pb.cc \
  BuildInfoPB.pb.h \
  BuildResultsPB.pb.cc \
  BuildResultsPB.pb.h \
  Makefile \
  Makefile.in
