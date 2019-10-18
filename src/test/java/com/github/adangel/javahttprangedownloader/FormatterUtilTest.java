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

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class FormatterUtilTest {

    @Test
    public void testFormatSeconds() {
        Assertions.assertEquals("00:00:00", FormatterUtil.formatSeconds(0.0));
        Assertions.assertEquals("00:00:01", FormatterUtil.formatSeconds(0.7));
        Assertions.assertEquals("00:01:01", FormatterUtil.formatSeconds(60.7));
        Assertions.assertEquals("01:00:01", FormatterUtil.formatSeconds(3600.7));
        Assertions.assertEquals("01:01:01", FormatterUtil.formatSeconds(3660.7));
        Assertions.assertEquals("01:12:01", FormatterUtil.formatSeconds(4321.0));
    }
}
