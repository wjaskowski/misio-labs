#!/bin/bash
wget http://www.cs.mcgill.ca/~dprecup/workshops/ICML06/Octopus/octopus-code-distribution.zip

rm -rf agent environment 2>/dev/null

unzip octopus-code-distribution.zip

mv -f octopus-code-distribution/* .

rm -rf octopus-code-distribution*
