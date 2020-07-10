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


fun main() {


    File("files/testfile.txt").reader().forEachLine { println(it) }

    val di = DataInputStream(FileInputStream("files/1bna.pdb"))


    println("hello")
    val mol = Molecule()
    val pdbParser = ParserPdbFile(mol)

    pdbParser.loadPdbFromStream(di)

    println("try this next")
}


