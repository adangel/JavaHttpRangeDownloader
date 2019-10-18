# JavaHttpRangeDownloader

A simple http downloader, that supports continuation - handy for big downloads.

## Building

    mvn clean verify

## Usage Example

    java -jar target/javahttprangedownloader-1.0.0-SNAPSHOT-jar-with-dependencies.jar http://ftp.uni-kl.de/pub/linux/knoppix-dvd/KNOPPIX_V8.6-2019-08-08-DE.iso

Then just hit &lt;CTRL&gt;+C to interrupt the download. Execute the same command again to continue.

The download is executed in the current directory.

While the download is not finished yet, the partial downloaded file is stored in `*.part`. The download is started
fresh, a `*.info` file is created which stores the URI and the total size.
