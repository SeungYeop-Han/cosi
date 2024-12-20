package com.cosi.api;

import com.cosi.upbit.dto.MarketInfo;
import com.cosi.upbit.httpclient.UpbitHttpClient;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestClient;

@SpringBootTest
class CosiApplicationTests {
	
	@Autowired
	RestClient restClient;

	@Autowired
	UpbitHttpClient upbitHttpClient;

	@Test
	void contextLoads() {
	}

	@Test
	void UpbitClientConfig에_등록된_RestClient가_잘_동작하는_지_눈으로_확인() {

		// given
		String uriString = "https://crix-static.upbit.com/crix_master";

		// when
		String responseBodyAsString = restClient
				.get()
				.uri(uriString)
				.retrieve()
				.body(String.class);

		// then
		System.out.println(" >>> " + responseBodyAsString);
	}

	@Test
	void UpbitHttpClient_빈으로_종목_리스트_요청_테스트() {

		// given
		Gson gson = new GsonBuilder()
				.setPrettyPrinting()
				.create();

		// when
		List<MarketInfo> marketInfoList = upbitHttpClient.getMarketInfoList();

		//then
		System.out.println(" >>> " + gson.toJson(marketInfoList));
	}
}
