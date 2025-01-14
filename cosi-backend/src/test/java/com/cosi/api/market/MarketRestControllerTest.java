package com.cosi.api.market;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.cosi.upbit.dto.MarketInfo;
import com.cosi.util.GzipUtils;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.zip.GZIPInputStream;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

@SpringBootTest
@WebAppConfiguration
class MarketRestControllerTest {

    @Autowired
    WebApplicationContext context;

    MockMvc mockMvc;

    @BeforeEach
    public void setMockMvc() {
        mockMvc = MockMvcBuilders
                .webAppContextSetup(context)
                .build();
    }

    @Test
    void test() throws Exception {

        // 확인 사항 1 : 요청이 성공적으로 응답되는가?
        mockMvc.perform(get("/api/markets"))
                .andExpect(status().is2xxSuccessful())
                .andDo(result -> {

                    // 확인 사항 2 : compressed 가 gzip 형식인가?
                    byte[] compressed = result.getResponse().getContentAsByteArray();
                    Assertions.assertTrue(isGZipped(new ByteArrayInputStream(compressed)));

                    // 확인 사항 3 : decompressed 가 List<MarketInfo> 역직렬화 되는가?
                    Gson gson = new GsonBuilder().setPrettyPrinting().create();
                    String decompressed = new String(GzipUtils.decompress(compressed));
                    List<MarketInfo> marketInfoList = gson.fromJson(
                            decompressed, new TypeToken<List<MarketInfo>>(){}.getType());
                    System.out.println("marketInfoList[0] = " + gson.toJson(marketInfoList.get(0)));

                    // 확인 사항 4 : 캐싱이 잘 되고 있는가?
                    String etag = (String) result.getResponse().getHeaderValue("etag");
                    mockMvc.perform(
                                get("/api/markets").header("If-None-Match", etag)
                            )
                            .andExpect(status().isNotModified());

                });
    }

    /**
     * Checks if an input stream is gzipped.
     * <br>
     * <a href="https://stackoverflow.com/questions/30507653/how-to-check-whether-file-is-gzip-or-not-in-java">참고 자료(Stackoverflow)</a>
     * @param in
     * @return
     */
    public static boolean isGZipped(InputStream in) {
        if (!in.markSupported()) {
            in = new BufferedInputStream(in);
        }
        in.mark(2);
        int magic = 0;
        try {
            magic = in.read() & 0xff | ((in.read() << 8) & 0xff00);
            in.reset();
        } catch (IOException e) {
            e.printStackTrace(System.err);
            return false;
        }
        return magic == GZIPInputStream.GZIP_MAGIC;
    }
}