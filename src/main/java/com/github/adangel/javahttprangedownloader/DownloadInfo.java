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

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URI;
import java.util.Properties;

class DownloadInfo {
    private URI uri;
    private String filename;
    private String targetDir;
    private String filenamePart;
    private String filenameInfo;
    private long totalSize;

    DownloadInfo(URI uri, String downloadDirectory) {
        this.uri = uri;
        this.filename = determineFilename(uri);
        this.targetDir = determineCurrentDirectory(downloadDirectory);
        this.filenamePart = determineFilenamePart(filename);
        this.filenameInfo = determineFilenameInfo(filename);
    }

    public URI getUri() {
        return uri;
    }
    public String getFilenameInfo() {
        return filenameInfo;
    }
    
    public void setTotalSize(long totalSize) {
        this.totalSize = totalSize;
    }
    public long getTotalSize() {
        return totalSize;
    }
    public String getFilenamePart() {
        return filenamePart;
    }
    public String getFilename() {
        return filename;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder(100);
        sb.append("  target dir: ").append(targetDir).append(JavaHttpRangeDownloader.EOL)
        .append("  target file: ").append(filename).append(JavaHttpRangeDownloader.EOL)
        .append("  part: ").append(filenamePart).append(JavaHttpRangeDownloader.EOL)
        .append("  info: ").append(filenameInfo).append(JavaHttpRangeDownloader.EOL);
        return sb.toString();
    }

    public boolean isNewDownload() {
        File targetFile = new File(targetDir, filename);
        File partFile = new File(targetDir, filenamePart);
        
        if (!targetFile.exists() && !partFile.exists()) {
            return true;
        }
        return false;
    }

    public boolean isContinuation() {
        File targetFile = new File(targetDir, filename);
        File partFile = new File(targetDir, filenamePart);
        File infoFile = new File(targetDir, filenameInfo);

        if (!targetFile.exists() && partFile.exists() && infoFile.exists()) {
            Properties props = new Properties();
            try {
                props.load(new FileInputStream(infoFile));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // check URL
            if (!props.getProperty("uri").equals(uri.toString())) {
                throw new RuntimeException("URI doesn't match");
            }

            // check total size
            long totalSize = Long.parseLong(props.getProperty("size"));
            setTotalSize(totalSize);

            if (partFile.length() >= totalSize) {
                // Already downloaded according to size...
                return false;
            }

            return true;
        }
        return false;
    }

    public boolean isFinished() {
        File targetFile = new File(targetDir, filename);
        File infoFile = new File(targetDir, filenameInfo);

        if (targetFile.exists() && infoFile.exists()) {
            Properties props = new Properties();
            try {
                props.load(new FileInputStream(infoFile));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // check URL
            if (!props.getProperty("uri").equals(uri.toString())) {
                throw new RuntimeException("URI doesn't match");
            }

            // check total size
            long totalSize = Long.parseLong(props.getProperty("size"));
            if (targetFile.length() != totalSize) {
                throw new RuntimeException("Size doesn't match...");
            }

            return true;
        }
        return false;
    }

    private static String determineFilename(URI uri) {
        String path = uri.getPath();
        String[] elements = path.split("/");
        return elements[elements.length - 1];
    }

    private static String determineFilenamePart(String filename) {
        return filename + ".part";
    }

    private static String determineFilenameInfo(String filename) {
        return filename + ".info";
    }

    private static String determineCurrentDirectory(String downloadDirectory) {
        try {
            return new File(downloadDirectory).getCanonicalPath();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}