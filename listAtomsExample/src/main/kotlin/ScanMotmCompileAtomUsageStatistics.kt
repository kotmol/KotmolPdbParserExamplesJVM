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


import com.kotmol.pdbParser.*
import util.MotmPdbNames
import java.io.*

class PdbAtomStatistics {

    val CharCountArray  = Array(118){0}
    val atomInfo = AtomInformationTable()
    val eleHash = atomInfo.atomSymboltoAtomNumNameColor

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
            CharCountArray[atomNumber]++
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

    val stats = PdbAtomStatistics()

    val files = MotmPdbNames().pdbNames

    val notThereList = mutableListOf<String>()
    val retainedMessages = mutableListOf<String>()

    for (file in files) {

        val thisPdbFile = File("../pdbs/$file.pdb")
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
         * now scan the molecule() and compile stats in the number of
         * Atom() types
         *
         */
            val atomList = mutableListOf<PdbAtom>()
            val atomHash = mol.atomNumberToAtomInfoHash
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

    println("Not There List: *********************")
    println(notThereList)

    val outFile = File("../pdbs/0outfileMessages.txt")
    val writer = outFile.bufferedWriter()

    writer.append("hello there")
    writer.append("more text\n")
    for (item in retainedMessages) {
        writer.append(item)
        writer.append("\n")

    }
    writer.close()

    val theFinalCount = stats.CharCountArray
    val atomInfo = AtomInformationTable()
    val atomArray = atomInfo.atomInformationTable
    /*
     * Note that Hydrogen has an element number of 1- so the table is "one-based"
     * but the array is "zero-based".   Element "0" has zero counts.
     */
    for (i in 1..theFinalCount.size-1) {
        val elementName = atomArray[i-1].name
        println("$elementName,${theFinalCount[i]}")
    }
    println("done")
}

