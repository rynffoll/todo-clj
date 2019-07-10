#!/bin/bash
export GRAALVM_HOME="~/bin/graalvm-ce-19.1.0/Contents/Home"
clj -A:native-image --verbose
