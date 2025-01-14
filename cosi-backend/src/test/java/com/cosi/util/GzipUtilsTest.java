package com.cosi.util;

import static com.cosi.util.GzipUtils.CompressionLevel.BEST_COMPRESSION;
import static com.cosi.util.GzipUtils.CompressionLevel.BEST_SPEED;
import static com.cosi.util.GzipUtils.CompressionLevel.DEFAULT_COMPRESSION;
import static com.cosi.util.GzipUtils.CompressionStrategy.DEFAULT_STRATEGY;
import static com.cosi.util.GzipUtils.CompressionStrategy.FILTERED;
import static com.cosi.util.GzipUtils.CompressionStrategy.HUFFMAN_ONLY;
import static java.nio.charset.StandardCharsets.*;
import static org.junit.jupiter.api.Assertions.*;

import com.cosi.util.GzipUtils.CompressionLevel;
import com.cosi.util.GzipUtils.CompressionStrategy;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import org.junit.jupiter.api.Test;

class GzipUtilsTest {

    @Test
    void 올바르게_동작하는_지_확인() {

        // given
        byte[] plain = "abcdefghijklnmopqrstuvwxyz0123456789!@#$%^&*()-=_+".getBytes(UTF_8);

        // when
        byte[] compressed = GzipUtils.compress(plain);
        byte[] decompressed = GzipUtils.decompress(compressed);

        // then
        assertArrayEquals(plain, decompressed);

        // print
        System.out.println("plain = " + new String(plain));
        System.out.println("compressed = " + new String(compressed));
        System.out.println("decompressed = " + new String(GzipUtils.decompress(compressed)));
    }

    @Test
    public void 성능_비교() throws IOException {

        // 원문 가져오기
        byte[] plain;

        Path txtFile = Path.of("C:\\Github\\cosi\\cosi-backend\\src\\test\\java\\com\\cosi\\util\\marketlistresponse.txt");
        if (Files.notExists(txtFile)) {
            fail();
        }
        
        plain = Files.readAllBytes(txtFile);
        System.out.println("원문 길이(byte) = " + plain.length);

        // 성능 비교
        int EPOCH = 1000;
        CompressionLevel[] LEVELS = {DEFAULT_COMPRESSION, BEST_SPEED, BEST_COMPRESSION};
        CompressionStrategy[] STRATEGIES = {DEFAULT_STRATEGY, FILTERED, HUFFMAN_ONLY};

        byte[] compressed = null;
        long begin;
        long end;
        
        for (CompressionLevel level : LEVELS) {
            for (CompressionStrategy strategy : STRATEGIES) {

                System.out.println();
                System.out.println("[압축 강도]: " + level.toString());
                System.out.println("[압축 방식]: " + strategy.toString());

                begin = System.currentTimeMillis();

                for (int i = 0; i < EPOCH; i++) {
                    compressed = GzipUtils.compress(plain, level);
                }

                end = System.currentTimeMillis();

                System.out.println(" >>> 결과 크기(byte) = " + compressed.length);
                System.out.println(" >>> 압축율 = " + String.format ("%.4f", (double) plain.length / compressed.length) + ": 1");
                System.out.println(" >>> 소요 시간(초) = " + (double) (end - begin) / 1000);
                System.out.println(" >>> 1회 평균 소요 시간(초) = " + (double) (end - begin) / 1000 / EPOCH);
            }
        }
    }
}