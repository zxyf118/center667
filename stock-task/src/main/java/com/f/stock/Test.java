package com.f.stock;

import cn.hutool.http.HttpRequest;
import cn.hutool.json.JSONObject;

public class Test {

	public static void main(String[] args) {
		String apiUrl = "https://sapi.k780.com";
		String params = "?app=finance.stock_realtime"
				+ "&stoSym=gb_aapl"
				+ "&appkey=74784"
				+ "&sign=dd8f43ab5b04c0bd29d7cfd07720b0da"
				+ "&format=json";
		String ret = HttpRequest.get(apiUrl+params).execute().body();
		System.err.println(ret);
		
		
		JSONObject lists = new JSONObject(ret).getJSONObject("result").getJSONObject("lists").getJSONObject("gb_aapl");
		System.err.println(lists.getStr("last_price"));
	}

}
