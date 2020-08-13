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
import java.io.*


/**
 * This main() routine opens a "wired-in" PDB file
 * located in the files folder at the root.
 * It then simply does a test parse of the file
 * and prints out any messages from the parsing library
 * that were emitted during parsing.
 * and parses them with the kotmolpdbparser library.
 *
 * SETUP:
 *    None required.
 *    This main function should run just fine in IntelliJ IDEA.
 *
 */

const val INPUT_FILE = "files/1bna.pdb"
fun main() {

    val thisPdbFile = File(INPUT_FILE)
    val fileExists = thisPdbFile.exists()
    if (fileExists) {
        val stream = DataInputStream(FileInputStream(INPUT_FILE))
        val retainedMessages = mutableListOf<String>()
        val molecule: Molecule = Molecule()
        ParserPdbFile
                .Builder(molecule)
                .loadPdbFromStream(stream)
                .setMessageStrings(retainedMessages)
                .parse()

        println("Messages are: $retainedMessages")
    }

}


