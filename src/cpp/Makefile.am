SUBDIRS = . ./mpi

bin_PROGRAMS = draw_glycan getlinkages validate_structure

draw_glycan_SOURCES = draw_glycan.cc
draw_glycan_CPPFLAGS = -I$(top_builddir)/deps/gmml/include
draw_glycan_LDADD = $(top_builddir)/deps/gmml/libgmml.a

getlinkages_SOURCES = getlinkages.cc
getlinkages_CPPFLAGS = -I$(top_builddir)/deps/gmml/include
getlinkages_LDADD = $(top_builddir)/deps/gmml/libgmml.a

validate_structure_SOURCES = validate_structure.cc
validate_structure_CPPFLAGS = -I$(top_builddir)/deps/gmml/include
validate_structure_LDADD = $(top_builddir)/deps/gmml/libgmml.a

MAINTAINERCLEANFILES = \
  Makefile \
  Makefile.in
