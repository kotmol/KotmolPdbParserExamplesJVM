/*
 *  Copyright 2020 James Andreas
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *       http://www.apache.org/licenses/LICENSE-2.0
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License
 */

import com.kotmol.pdbParser.*
import com.kotmol.pdbParser.AtomInformationTable.atomSymboltoAtomNumNameColor
import com.kotmol.pdbParser.AtomInformationTable.atomTable
import util.MotmPdbNames
import java.io.*

/**
 * This main() routine (below after the class) loops through a list of PDB files
 * and parses them with the kotmolpdbparser library.
 * It accumulates statistics about atom usage for each PDB file.
 *
 * At the end of the loop it sorts the statistics and prints them out.
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
const val E03_RELATIVE_PATH = "../pdbs"

data class AtomCounts(var atomNumber: Int = 0, var abundance: Int = 0)

const val numberOfKnownAtoms = 118
val atomCountsArray = mutableListOf(AtomCounts())

class PdbAtomStatistics {

    init {
        for (i in 0..numberOfKnownAtoms) {
            atomCountsArray.add(AtomCounts(i, 0))
        }
    }

    val eleHash = atomSymboltoAtomNumNameColor

    fun atomsByType(atomList: MutableList<PdbAtom>) {
        for (atom in atomList) {
            if (atom.elementSymbol.isEmpty()) {
                continue
            }
            val atomTwoLetterName = atom.elementSymbol
            val atomInfo = eleHash[atomTwoLetterName]
            if (atomInfo == null) {
                println("atomsByType: didn't find this atom: $atomTwoLetterName ******************************")
                continue
            }
            if (atomInfo.symbol == "OS") {
                println("this MOL has OSMIUM ******************************")
            }
            val atomNumber = atomInfo.number
            if (atomNumber < 0
                    || atomNumber > 118) {
                println("oopsie $atomNumber is OUT OF RANGE **************************")
            }
            atomCountsArray[atomNumber].abundance++
        }
    }
}

/**
 * for every file in the util/ pdb list, read in the file (from the $Root/files directory)
 * via the pdb parser.
 *
 * Then scan the ATOM / HETATM list in the molecule() structure, and compile a sorted
 * usage of all ATOM and HETATM types.
 */
fun main() {
//    val files = MotmPdbNames().pdbNamesTEST
    val files = MotmPdbNames().pdbNames

    val notThereList = mutableListOf<String>()
    val retainedMessages = mutableListOf<String>()

    val stats = PdbAtomStatistics()  // this will accumulate the counts

    for (file in files) {

        val filePath = "$E03_RELATIVE_PATH/$file.pdb"
        val thisPdbFile = File(filePath)
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

            /*
             * The PDB file is now parsed into the mol structure.
             * now scan the mol and compile stats in the number of
             * Atom() types
             */
            val atomList = mutableListOf<PdbAtom>()
            // val atomHash = mol.atomNumberToAtomInfoHash
            val atoms = mol.atomNumberList
            for (atomNumber in atoms) {
                atomList.add(mol.atomNumberToAtomInfoHash[atomNumber]!!)
            }
            stats.atomsByType(atomList)
            stream.close()
        } else {
            println("UFFDA $thisPdbFile does not exist")
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

    val atomArray = atomTable
    /*
     * Note that Hydrogen has an element number of 1- so the table is "one-based"
     * but the array is "zero-based".   Element "0" has zero counts.
     */
//    for (i in 1 until numberOfKnownAtoms) {
//        val elementName = atomArray[i-1].name
//        println("$elementName,${atomCountsArray[i].abundance}")
//    }

    val sortedList = atomCountsArray.sortedByDescending { it.abundance }

    for (i in 0 until numberOfKnownAtoms-1) {
        val elementName = atomArray[sortedList[i].atomNumber].name
        println("$elementName,${sortedList[i].abundance}")
    }

    println("done")
}

