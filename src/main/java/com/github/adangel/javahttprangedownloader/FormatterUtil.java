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

import java.util.Locale;

class FormatterUtil {

    private FormatterUtil() {}

    static String formatSeconds(double totalSeconds) {
        int hours = (int)(totalSeconds / 3600.0);
        int minutes = (int)((totalSeconds - hours*3600.0) / 60.0);
        int seconds = (int)Math.round(totalSeconds - hours*3600.0 - minutes * 60.0);
        return String.format(Locale.ENGLISH, "%02d:%02d:%02d", hours, minutes, seconds);
    }
}
