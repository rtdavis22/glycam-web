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

if test ! -e deps/mysql-connector-java; then
  name="mysql-connector-java"
  version="5.1.18"
  tar_name=$name-$version.tar.gz
  echo "Fetching $name from the web"
  wget http://dev.mysql.com/get/Downloads/Connector-J/$tar_name/from/ftp://mirror.anl.gov/pub/mysql/
  tar -xvf $tar_name > /dev/null
  rm $tar_name
  mv $name-$version deps/$name
  cd deps/$name
  mv $name*.jar $name.jar
  cd ../..
fi

autoreconf -f -i

rm -rf autom4te.cache
