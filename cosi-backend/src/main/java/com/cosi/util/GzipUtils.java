package com.cosi.util;

import static com.cosi.util.GzipUtils.CompressionLevel.DEFAULT_COMPRESSION;
import static com.cosi.util.GzipUtils.CompressionStrategy.DEFAULT_STRATEGY;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

public class GzipUtils {

    /**
     * 주어진 압축 강도와 방식으로 gzip 압축합니다.
     * @param toBeCompressed 압축할 바이트열
     * @param level 압축 강도
     * @param strategy 압축 방식
     * @return gzip 압축된 plainText
     */
    public static byte[] compress(byte[] toBeCompressed, CompressionLevel level, CompressionStrategy strategy) {

        if (toBeCompressed == null) {
            throw new IllegalArgumentException("toBeCompressed 가 null 입니다.");
        }

        if (level == null) {
            throw new NullPointerException("compressiongLevel 이 null 입니다.");
        }

        try {

            ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
            GZIPOutputStream gzipOut = new GZIPOutputStream(bytesOut) {
                {
                    def.setLevel(level.value);
                    def.setStrategy(strategy.value);
                }
            };
            gzipOut.write(toBeCompressed);
            gzipOut.close();
            return bytesOut.toByteArray();

        } catch (IOException ioe) {
            throw new RuntimeException("[GzipCodec] gzip 압축 실패");
        }
    }

    /**
     * 디폴트 압축 방식으로 gzip 압축합니다.
     * @param toBeCompressed 압축할 바이트열
     * @param level 압축 강도
     * @return
     */
    public static byte[] compress(byte[] toBeCompressed, CompressionLevel level) {
        return compress(toBeCompressed, level, DEFAULT_STRATEGY);
    }

    /**
     * 디폴트 압축 강도로 gzip 압축합니다.
     * @param toBeCompressed 압축할 바이트열
     * @param strategy 압축 방식
     * @return
     */
    public static byte[] compress(byte[] toBeCompressed, CompressionStrategy strategy) {
        return compress(toBeCompressed, DEFAULT_COMPRESSION, strategy);
    }

    /**
     * 디폴트 설정으로 gzip 압축합니다.
     * @param toBeCompressed 압축할 바이트열
     * @return gzip 압축된 plainText
     */
    public static byte[] compress(byte[] toBeCompressed) {
        return compress(toBeCompressed, DEFAULT_COMPRESSION, DEFAULT_STRATEGY);
    }

    /**
     * gzip 압축을 해제합니다.
     * @param compressed gzip 방식으로 압축된 바이트 배열
     * @throws java.util.zip.ZipException compressed 배열이 gzip 방식으로 압축되어 있지 않은 경우 
     * @return 압축 해제된 원문
     */
    public static byte[] decompress(byte[] compressed) {

        try {

            ByteArrayInputStream bytesIn = new ByteArrayInputStream(compressed);
            GZIPInputStream gzipIn = new GZIPInputStream(bytesIn);
            BufferedReader br = new BufferedReader(new InputStreamReader(gzipIn, StandardCharsets.UTF_8));

            byte[] ret = gzipIn.readAllBytes();
            gzipIn.close();
            br.close();

            return ret;

        } catch (IOException ioe) {
            throw new RuntimeException("[GzipCodec] gzip 압축 해제 실패, 사유: " + ioe);
        }
    }

    public enum CompressionLevel {

        DEFAULT_COMPRESSION(-1),
        NO_COMPRESSION(0),
        BEST_SPEED(1),
        BEST_COMPRESSION(9);

        private int value;

        CompressionLevel(int value) {
            this.value = value;
        }
    }

    public enum CompressionStrategy {

        FILTERED(1),
        HUFFMAN_ONLY(2),
        DEFAULT_STRATEGY(0);

        private int value;

        CompressionStrategy(int value) {
            this.value = value;
        }
    }
}
