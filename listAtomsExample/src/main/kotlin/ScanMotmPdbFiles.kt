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


import com.kotmol.pdbParser.Molecule
import com.kotmol.pdbParser.ParserPdbFile
import util.MotmPdbNames
import java.io.*

const val RELATIVE_PATH = "../pdbs"

/**
 * This main() routine loops through a list of PDB files
 * and parses them with the kotmolpdbparser library.
 * It prints out a couple of basic statistics for each PDB file.
 *
 * At the end of the loop it writes the name of the PDB file
 * and then any messages logged by the library, if any.
 *
 * SETUP:
 *    See the script in $ROOT/docs/downloadPdbFiles
 *    You will need to download the PDB files using the script
 *    in the folder.  The RELATIVE_PATH (see above) gives the
 *    path to where you put the files.
 *
 *    The various examples will cycle through the PDBs as based on the
 *    MotmPdbNames().pdbNamesShort list
 *    defined in the util/MotmPdbNames.kt file.
 *
 */
fun main() {

//    val files = MotmPdbNames().pdbNames
    val files = MotmPdbNames().pdbNamesShort

    val notThereList = mutableListOf<String>()
    val retainedMessages = mutableListOf<String>()

    for (file in files) {

        val thisPdbFile = File("$RELATIVE_PATH/$file.pdb")
        val fileExists = thisPdbFile.exists()
        if (fileExists) {
            retainedMessages.add(String.format("****** file: %s", file))
            val stream = DataInputStream(FileInputStream(thisPdbFile))
            val mol = Molecule()
            ParserPdbFile
                    .Builder(mol)
                    .setMessageStrings(retainedMessages)
                    .loadPdbFromStream(stream)
                    .parse()
            val numAtoms = mol.atomNumberList.size
            val numBonds = mol.bondList.size
            println("$file has $numAtoms atoms and $numBonds bonds")
            stream.close()
        } else {
            println("Error: $thisPdbFile does not exist")
            notThereList.add(file)
        }
    }

    if (notThereList.isEmpty()) {
        println("*************************")
        println("No missing PDBs were found from the list of ${files.size} PDB files")
        println("*************************")
    } else {
        println("*************************")
        println("The following PDBs were NOT found from the list of ${files.size} PDB files")
        println(notThereList)
        println("*************************")
    }

    val outputMessagesFilePath = "$RELATIVE_PATH/0outfileMessages.txt"
    println("Writing parser messages to $outputMessagesFilePath")
    val outFile = File("$outputMessagesFilePath")
    val writer = outFile.bufferedWriter()

    writer.append("Messages from the PDB parser:\n")
    for (item in retainedMessages) {
        writer.append(item)
        writer.append("\n")
    }
    writer.close()

    println("done")
}


