/*
   Copyright 2019 Andreas Dangel

   Licensed under the Apache License, Version 2.0 (the "License");
   you may not use this file except in compliance with the License.
   You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

   Unless required by applicable law or agreed to in writing, software
   distributed under the License is distributed on an "AS IS" BASIS,
   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
   See the License for the specific language governing permissions and
   limitations under the License.

 */
package com.github.adangel.javahttprangedownloader;

import org.junit.jupiter.api.Test;

public class AnsiCodeExplorationTest {

    /**
     * see https://en.wikipedia.org/wiki/ANSI_escape_code#DOS_and_Windows
     */
    @Test
    public void updateLine() {
        System.out.print("Hello");
        System.out.print("\u001b[1K"); // clear current line
        System.out.print("\u001b[1G"); // move cursor to first column
        System.out.print("World");
        System.out.println();
    }
}
