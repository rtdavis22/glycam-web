#! /bin/sh

set -e

mkdir -p deps

if test ! -e deps/protobuf; then
  echo "Fetching protobuf from the web..."
  svn export http://protobuf.googlecode.com/svn/trunk/ deps/protobuf > /dev/null
fi

cd deps/protobuf
if test ! -e configure; then
  sh autogen.sh
fi
cd ../..

if test ! -e deps/gmml; then
  echo "Fetching gmml from the web..."
  git clone https://code.google.com/p/gmml/ deps/gmml
fi

cd deps/gmml
if test ! -e configure; then
  sh autogen.sh
fi
cd ../..

autoreconf -f -i

rm -rf autom4te.cache
