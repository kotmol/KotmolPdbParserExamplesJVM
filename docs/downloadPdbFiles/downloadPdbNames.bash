#!/bin/bash

pdbs=`cat pdbNames.sorted`

for file in $pdbs
do
   wget --no-check-certificate https://files.rcsb.org/download/$file.pdb
   sleep 2
done
