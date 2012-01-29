SUBDIRS = . ./mpi

bin_PROGRAMS = \
  draw_glycan \
  get_linkages \
  get_pdb_info \
  validate_pdb \
  validate_sequence

BUILT_SOURCES = \
  PdbInfoPB.pb.cc \
  PdbInfoPB.pb.h

PROTO_CMD = \
  $(top_builddir)/deps/protobuf/src/protoc \
  --cpp_out=./ \
  --proto_path=$(top_builddir)/src/proto

draw_glycan_SOURCES = draw_glycan.cc
draw_glycan_CPPFLAGS = -I$(top_builddir)/deps/gmml/include
draw_glycan_LDADD = $(top_builddir)/deps/gmml/libgmml.a

get_linkages_SOURCES = get_linkages.cc
get_linkages_CPPFLAGS = -I$(top_builddir)/deps/gmml/include
get_linkages_LDADD = $(top_builddir)/deps/gmml/libgmml.a

get_pdb_info_SOURCES = PdbInfoPB.pb.cc PdbInfoPB.pb.h get_pdb_info.cc
get_pdb_info_CPPFLAGS = \
  -I$(top_builddir)/deps/gmml/include \
  -I$(top_builddir)/deps/protobuf/src
get_pdb_info_LDADD = \
  $(top_builddir)/deps/gmml/libgmml.a \
  $(top_builddir)/deps/protobuf/src/libprotobuf.la

validate_pdb_SOURCES = validate_pdb.cc
validate_pdb_CPPFLAGS = -I$(top_builddir)/deps/gmml/include
validate_pdb_LDADD = $(top_builddir)/deps/gmml/libgmml.a

validate_sequence_SOURCES = validate_sequence.cc
validate_sequence_CPPFLAGS = -I$(top_builddir)/deps/gmml/include
validate_sequence_LDADD = $(top_builddir)/deps/gmml/libgmml.a

PdbInfoPB.pb.cc PdbInfoPB.pb.h: \
                $(top_builddir)/src/proto/PdbInfoPB.proto
	$(PROTO_CMD) $(top_builddir)/src/proto/PdbInfoPB.proto

MAINTAINERCLEANFILES = \
  Makefile \
  Makefile.in
