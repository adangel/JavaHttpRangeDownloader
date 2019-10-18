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

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.Properties;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

public class DownloadInfoTest {

    @Test
    public void testFilenames() {
        DownloadInfo info = new DownloadInfo(URI.create("https://host.example/dir/file.iso"), ".");
        Assertions.assertEquals("file.iso", info.getFilename());
        Assertions.assertEquals("file.iso.part", info.getFilenamePart());
        Assertions.assertEquals("file.iso.info", info.getFilenameInfo());
    }

    @Test
    public void testStatus() throws IOException {
        Path tempDir = Files.createTempDirectory("javahttprangedownloader");
        try {
            String uri = "https://host.example/dir/file.iso";
            DownloadInfo info = new DownloadInfo(URI.create(uri), tempDir.toString());
            Assertions.assertTrue(info.isNewDownload());
            Assertions.assertFalse(info.isContinuation());
            Assertions.assertFalse(info.isFinished());

            Path infoFile = tempDir.resolve("file.iso.info");
            writeInfoFile(infoFile, uri, 1000L);
            Assertions.assertTrue(info.isNewDownload());
            Assertions.assertFalse(info.isContinuation());
            Assertions.assertFalse(info.isFinished());

            Path partFile = tempDir.resolve("file.iso.part");
            byte[] partialContent = "Partial-Content...".getBytes(StandardCharsets.UTF_8);
            Files.write(partFile, partialContent, StandardOpenOption.CREATE);

            Assertions.assertFalse(info.isNewDownload());
            Assertions.assertTrue(info.isContinuation());
            Assertions.assertFalse(info.isFinished());

            writeInfoFile(infoFile, uri, partialContent.length);
            Assertions.assertFalse(info.isNewDownload());
            Assertions.assertFalse(info.isContinuation());
            Assertions.assertFalse(info.isFinished());

            Path targetFile = tempDir.resolve("file.iso");
            Files.move(partFile, targetFile, StandardCopyOption.REPLACE_EXISTING);
            Assertions.assertFalse(info.isNewDownload());
            Assertions.assertFalse(info.isContinuation());
            Assertions.assertTrue(info.isFinished());
        } finally {
            Files.list(tempDir).forEach(p -> p.toFile().delete());
            Assertions.assertTrue(tempDir.toFile().delete(), "Couldn't delete temp dir " + tempDir);
        }
        
    }

    private void writeInfoFile(Path infoFile, String uri, long size) throws IOException {
        Properties props = new Properties();
        props.setProperty("uri", uri);
        props.setProperty("size", String.valueOf(size));
        try (OutputStream out = Files.newOutputStream(infoFile, StandardOpenOption.CREATE)) {
            props.store(out, "");
        }
    }
}
