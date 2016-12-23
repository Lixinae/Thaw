#!/bin/bash
jvm=$1
echo "Compiling using : $jvm"
ant -DJDK1.9.dir=$jvm
