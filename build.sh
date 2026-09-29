#!/bin/bash
rm -rf bin
mkdir -p bin
javac -d bin -sourcepath src src/Main.java || exit 1
java -cp bin Main
