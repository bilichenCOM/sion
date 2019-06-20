package model;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;

import apis.SlackAccessConstants;

public class Channel {
	
	private String channelId;
	private List<String> members;
	private static final Logger logger = Logger.getLogger(Channel.class);

	public Channel(String channelId) {
		this.channelId = channelId;
		this.reloadMembers();
	}
	
	public void reloadMembers() {
		members = new ArrayList<>();
		JSONObject data = loadMembers();
		JSONArray membersArray = data.getJSONArray("members");
		membersArray.toList().stream()
			.map(o -> (String) o)
			.forEach(members::add);
			
	}

	private JSONObject loadMembers() {
		String url = SlackAccessConstants.CONVERSATIONS_MEMBERS.getValue();
		String requestBody = "token=" + SlackAccessConstants.OAUTH_TOKEN + "&channel=" + channelId;
		StringBuilder response = new StringBuilder();
		try {
			URL slackMethodUrl = new URL(url);
			URLConnection connection = slackMethodUrl.openConnection();
			connection.setDoOutput(true);
			
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
			writer.write(requestBody);
			writer.close();
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			String line = "";
			while((line = reader.readLine()) != null) {
				response.append(line);
			}
			reader.close();
			return new JSONObject(response.toString());
		} catch (MalformedURLException e) {
			System.err.println("bad url  " + url);
		} catch (IOException e) {
			System.err.println("IOException in load members method!");
		}
		return new JSONObject(response);
	}

	public List<String> getMembers() {
		return members;
	}
	
	public List<String> getReactedUsers(String timestamp) {
		String url = SlackAccessConstants.REACTIONS_GET.getValue();
		String requestBody = "token=" + SlackAccessConstants.OAUTH_TOKEN
				+ "&channel=" + channelId
				+ "&timestamp=" + timestamp;
		List<String> reactedUsers = new ArrayList<>();
		try {
			URL slackMethodUrl = new URL(url);
			URLConnection connection = slackMethodUrl.openConnection();
			connection.setDoOutput(true);
			
			BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(connection.getOutputStream()));
			writer.write(requestBody);
			writer.close();
			
			BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
			StringBuilder response = new StringBuilder();
			reader.lines().forEach(response::append);
			reader.close();

			JSONObject jsonResponse = new JSONObject(response.toString());
			JSONObject jsonMessage = jsonResponse.getJSONObject("message");
			System.out.println(jsonMessage.toString());
			if(jsonMessage.isNull("reactions")) {
				return reactedUsers;
			}
			JSONArray reactionsData = jsonMessage.getJSONArray("reactions");
			List<JSONObject> reactions = new ArrayList<>();
			reactionsData.forEach(r -> reactions.add((JSONObject) r));
			reactions.stream()
				.flatMap(r -> r.getJSONArray("users").toList().stream())
				.map(o -> (String) o)
				.forEach(reactedUsers::add);
			return reactedUsers;
		} catch (MalformedURLException e) {
			logger.error("wrong url for getting users reaction", e);
		} catch (IOException e) {
			logger.error("something wrong by getting reactions", e);
		}
		return reactedUsers;
	}
}