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
import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URI;
import java.util.Date;
import java.util.Locale;
import java.util.Properties;

public class JavaHttpRangeDownloader {
    static final String EOL = System.getProperty("line.separator");
    private static final String CLEAR_LINE = "\u001b[1K\u001b[1G";

    public static void main(String[] args) {
        if (args.length != 1) {
            showUsage();
        }

        URI uri = URI.create(args[0]);
        System.out.println("Downloading " + uri + "...");

        DownloadInfo download = new DownloadInfo(uri, ".");
        System.out.println(download);

        if (download.isNewDownload()) {
            System.out.println("new download");
            downloadHead(download);
            downloadFile(download);
        } else if (download.isContinuation()) {
            System.out.println("Continuation");
            downloadFile(download);
        } else if (download.isFinished()) {
            System.out.println("Already Finished");
        } else {
            System.out.println("Don't know what to do");
        }
    }

    private static void downloadFile(DownloadInfo download) {
        File filenamePart = new File(download.getFilenamePart());
        long start = 0L;

        if (filenamePart.exists()) {
            start = filenamePart.length();
        }

        URI uri = download.getUri();
        HttpURLConnection con;
        try (RandomAccessFile target = new RandomAccessFile(filenamePart, "rw")) {
            target.seek(start);
            System.out.println("Starting to download from offset " + start + " (" + (start / 1024) + "kB)");
            con = (HttpURLConnection) uri.toURL().openConnection();
            con.setRequestMethod("GET");
            if (start > 0L) {
                con.setRequestProperty("Range", "bytes=" + start + "-");
            }
            con.connect();
            int responseCode = con.getResponseCode();
            System.out.println("Response: " + responseCode);
            if (start == 0L && responseCode != 200) {
                throw new RuntimeException("Invalid response code, expected 200");
            }
            if (start > 0L && responseCode != 206) {
                throw new RuntimeException("Invalid response code, expected 206");
            }
            long downloaded = 0L;
            long startTimestamp = System.currentTimeMillis();
            long lastUpdate = 0L;
            byte[] buffer = new byte[8192];
            try (BufferedInputStream in = new BufferedInputStream(con.getInputStream())) {
                int c;
                while ((c = in.read(buffer)) != -1) {
                    downloaded += c;
                    target.write(buffer, 0, c);
                    if (System.currentTimeMillis() - lastUpdate >= 1000L) {
                        lastUpdate = System.currentTimeMillis();
                        double percentage = (double)target.length() / download.getTotalSize() * 100.0;
                        long remaining = download.getTotalSize() - target.length();
                        long duration = System.currentTimeMillis() - startTimestamp;
                        double speedBytesPerSecond = (double)downloaded / (duration / 1000.0);
                        double etaInSeconds = (double)remaining / speedBytesPerSecond;
                        long etaTimestamp = System.currentTimeMillis() + (long)(etaInSeconds * 1000.0);
                        System.out.printf(Locale.ENGLISH,
                                "%sProgress: %d kB / %d kB (%.2f %%) ETA = %s (%s), speed = %.2f kB/s",
                                CLEAR_LINE,
                                (target.length() / 1024),
                                (download.getTotalSize() / 1024),
                                percentage,
                                String.valueOf(new Date(etaTimestamp)),
                                FormatterUtil.formatSeconds(etaInSeconds),
                                speedBytesPerSecond / 1024.0);
                        duration = System.currentTimeMillis();
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        if (filenamePart.length() == download.getTotalSize()) {
            System.out.println(EOL + "Downloaded all");
            filenamePart.renameTo(new File(download.getFilename()));
        }
    }

    private static void downloadHead(DownloadInfo download) {
        URI uri = download.getUri();
        File infoFile = new File(download.getFilenameInfo());

        try {
            HttpURLConnection con = (HttpURLConnection) uri.toURL().openConnection();
            con.setRequestMethod("HEAD");
            con.connect();
            con.getInputStream().close();

            long totalSize = Long.parseLong(con.getHeaderField("Content-Length"));

            try (FileOutputStream out = new FileOutputStream(infoFile)) {
                Properties props = new Properties();
                props.setProperty("size", Long.toString(totalSize));
                props.setProperty("uri", uri.toString());
                props.store(out, "");
            }
            download.setTotalSize(totalSize);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static void showUsage() {
        System.out.println(JavaHttpRangeDownloader.class.getSimpleName() + " <url-to-download>");
        System.exit(1);
    }
}
