package com.cosi.api;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import net.minidev.json.parser.JSONParser;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.client.RestClient;

@SpringBootTest
class CosiApplicationTests {
	
	@Autowired
	RestClient restClient;

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
}
