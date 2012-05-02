#                                               -*- Autoconf -*-
# Process this file with autoconf to produce a configure script.

AC_PREREQ([2.60])
AC_INIT([model], [0.0.1], rtdavis22@gmail.com)
AM_INIT_AUTOMAKE([foreign silent-rules])
AC_CONFIG_SRCDIR([src/cpp/validate_sequence.cc])
AC_CONFIG_HEADERS([config.h])

# Checks for programs.
AC_PROG_CXX
AC_PROG_CC
LT_INIT([dlopen])

# Checks for libraries.

# Checks for header files.

# Checks for typedefs, structures, and compiler characteristics.
AC_HEADER_STDBOOL
AC_C_INLINE
AC_TYPE_SIZE_T

# Checks for library functions.
AC_CHECK_FUNCS([memset])

AC_DEFINE_UNQUOTED([PROJECT_ROOT], ["$(pwd)/"], [The root of the project.])

AC_SUBST([project_root], [$(pwd)])

AC_CONFIG_FILES([Makefile
                 src/cpp/Makefile
                 src/cpp/mpi/Makefile
                 src/java/src/org/glycam/configuration/Configuration.java])

AC_CONFIG_SUBDIRS([deps/gmml
                   deps/protobuf])
AC_OUTPUT
