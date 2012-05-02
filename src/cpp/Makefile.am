SUBDIRS = . ./mpi

bin_PROGRAMS = \
  build_glycoprotein \
  draw_glycan \
  get_glycoprotein_info \
  get_linkages \
  get_pdb_preprocessing_info \
  validate_sequence

BUILT_SOURCES = \
  BuildInfoPB.pb.cc \
  BuildInfoPB.pb.h \
  BuildResultsPB.pb.cc \
  BuildResultsPB.pb.h \
  PdbFilePB.pb.cc \
  PdbFilePB.pb.h

PROTO_CMD = \
  $(top_builddir)/deps/protobuf/src/protoc \
  --cpp_out=./ \
  --proto_path=$(top_builddir)/src/proto

build_glycoprotein_SOURCES = \
  build_glycoprotein.cc \
  BuildInfoPB.pb.cc \
  BuildInfoPB.pb.h \
  PdbFilePB.pb.cc \
  PdbFilePB.pb.h
build_glycoprotein_CPPFLAGS = \
  -I$(top_builddir)/deps/gmml/include \
  -I$(top_builddir)/deps/protobuf/src
build_glycoprotein_LDADD = \
  $(top_builddir)/deps/gmml/libgmml.a \
  $(top_builddir)/deps/protobuf/src/libprotobuf.la

draw_glycan_SOURCES = draw_glycan.cc
draw_glycan_CPPFLAGS = -I$(top_builddir)/deps/gmml/include
draw_glycan_LDADD = $(top_builddir)/deps/gmml/libgmml.a

get_glycoprotein_info_SOURCES = \
  PdbFilePB.pb.cc \
  PdbFilePB.pb.h \
  BuildInfoPB.pb.h \
  BuildInfoPB.pb.cc \
  get_glycoprotein_info.cc
get_glycoprotein_info_CPPFLAGS = \
  -I$(top_builddir)/deps/gmml/include \
  -I$(top_builddir)/deps/protobuf/src
get_glycoprotein_info_LDADD = \
  $(top_builddir)/deps/gmml/libgmml.a \
  $(top_builddir)/deps/protobuf/src/libprotobuf.la

get_linkages_SOURCES = get_linkages.cc
get_linkages_CPPFLAGS = -I$(top_builddir)/deps/gmml/include
get_linkages_LDADD = $(top_builddir)/deps/gmml/libgmml.a

get_pdb_preprocessing_info_SOURCES = \
  PdbFilePB.pb.cc \
  PdbFilePB.pb.h \
  BuildInfoPB.pb.cc \
  BuildInfoPB.pb.h \
  get_pdb_preprocessing_info.cc
get_pdb_preprocessing_info_CPPFLAGS = \
  -I$(top_builddir)/deps/gmml/include \
  -I$(top_builddir)/deps/protobuf/src
get_pdb_preprocessing_info_LDADD = \
  $(top_builddir)/deps/gmml/libgmml.a \
  $(top_builddir)/deps/protobuf/src/libprotobuf.la

validate_sequence_SOURCES = validate_sequence.cc
validate_sequence_CPPFLAGS = -I$(top_builddir)/deps/gmml/include
validate_sequence_LDADD = $(top_builddir)/deps/gmml/libgmml.a

%.pb.cc %.pb.h: $(top_builddir)/src/proto/%.proto
	$(PROTO_CMD) $<

MAINTAINERCLEANFILES = \
  $(BUILT_SOURCES) \
  Makefile \
  Makefile.in
