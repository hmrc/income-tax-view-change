#!/bin/bash
set -e

sbt clean

for i in {1..10}
do
    sbt compile test
done