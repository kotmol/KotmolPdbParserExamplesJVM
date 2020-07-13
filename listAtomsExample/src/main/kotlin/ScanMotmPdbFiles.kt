/*
 *
 *  Copyright (C) 2020 James Andreas
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 *
 */

import com.kotmol.pdbParser.KotmolPdbParserClient
import com.kotmol.pdbParser.Molecule
import com.kotmol.pdbParser.ParserPdbFile
import util.MotmPdbNames
import java.io.*


/**
 * for every file in the util/ pdb list, read in the file (from the $Root/files directory)
 * via the pdb parser.
 */
fun main() {

    val files = MotmPdbNames().pdbNames

    val notThereList = mutableListOf<String>()
    val retainedMessages = mutableListOf<String>()

    for (file in files) {

        val thisPdbFile = File("../pdbs/$file.pdb")
        val fileExists = thisPdbFile.exists()
        if (fileExists) {
            retainedMessages.add(String.format("****** file: %s", file))
            val di = DataInputStream(FileInputStream(thisPdbFile))
            val mol = Molecule()
            val builder = KotmolPdbParserClient
                    .Builder()
                    .setStream(di)
                    .parse(mol, retainedMessages)
            val numAtoms = mol.atoms.size
            val numBonds = mol.bondList.size
            println("$file has $numAtoms atoms and $numBonds bonds")
            di.close()
        } else {
            println("UFFDA $thisPdbFile does not exist")
            notThereList.add(file)
        }
    }

    println("Not There List: *********************")
    println(notThereList)

    println("Messages: *********************")
    println(retainedMessages)

    println("done")
}


