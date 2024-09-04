#! /bin/bash

INTEL=asciilib-dependencies-mac-intel
SILICON=asciilib-dependencies-mac-silicon

cp -r $SILICON/include ./
mkdir lib

combine() {
    lipo -create $SILICON/lib/$1 $INTEL/lib/$1 -output lib/$1
}

combine_all() {
    for LIB in $LIBS; do
        (cd ../../ && combine $LIB)
    done
}

(cd $SILICON/lib && LIBS=*.a combine_all)
(cd $SILICON/lib && LIBS=*.dylib combine_all)