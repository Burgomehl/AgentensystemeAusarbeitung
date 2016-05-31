package tests;

import com.google.gson.Gson;

import agent.Message;

public class TestGson {

	public static void main(String[] args) {
		Gson gson = new Gson();
		Message message = new Message();
		message.color="rosa";
		String json = gson.toJson(message);
		System.out.println(json);
	}

}
