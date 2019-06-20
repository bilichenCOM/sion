package model;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.json.JSONObject;

import apis.SlackAccessConstants;
import conv.ConversationArraysReplyChat;
import conv.ConversationPatternChat;
import conv.ConversationPatternMonitor;
import utils.FileReader;

public class Sion {
	private static final Sion instance = new Sion("Sion");

	private String displayName;
	private String defaultUserName;

	private boolean isInCheckerMode;
	private String messageTimeStamp = "0000";
	private Channel checkedChannel;

	private static final String TEST_CHANNEL = "CJ84589DK";
	private static final String HOME_CHANNEL = "GGTDVFQG6";
	private static final Logger LOGGER = Logger.getLogger(Sion.class);
	
	private Sion(String displayName) {
		this.displayName = displayName;
		this.defaultUserName = displayName.substring(0, 1).toLowerCase() + displayName.substring(1);
	}

	public static Sion getInstance() {
		return instance;
	}

	public String getDisplayName() {
		return displayName;
	}

	public String getDefaultUserName() {
		return defaultUserName;
	}

	public String sayHelloWorld() {
		return "Hello World! I am Sion!";
	}

	public String sayHelloToUser(String user) {
		return String.format("Hello, %s nice to meet you!", user);
	}

	public void handle(JSONObject message) {
		System.out.println("request received by sion!");

		String type = (String) message.get("type");

		if (type.equals("event_callback")) {
			System.out.println("event_callback");
			handleEventCallback(message);
			return;
		}
	}
	
	public void handleMemberJoined(JSONObject message) {
		String channel = message.getString("channel");
		sendMessage(sayHelloWorld(), channel, null);
	}

	public void handleReactionAdded(JSONObject message) {
		JSONObject item = message.getJSONObject("item");
		System.out.println("reaction to message with ts: " + item.getString("ts"));
		System.out.println("last ts: " + messageTimeStamp);
		
		if (isInCheckerMode && messageTimeStamp.equals(item.get("ts"))) {
			
		}
	}

	public void handleEventCallback(JSONObject message) {
		JSONObject event = message.getJSONObject("event");
		String type = event.getString("type");

		if (type.equals("message")) {
			System.out.println("message");
			handleEventMessage(event);
			return;
		}
		
		if (type.equals("reaction_added")) {
			System.out.println("reaction added");
			handleReactionAdded(event);
			return;
		}

		if (type.equals("member_joined_channel")) {
			System.out.println("member joined nested in 'event_callback'");
			handleMemberJoined(event);
			return;
		}

		if (type.equals("app_mention")) {
			System.out.println("app_metion nested in 'event_callback'");
			handleAppMention(event);
			return;
		}

		if (type.equals("member_left_channel")) {
			System.out.println("member left a channel");
			handleMemberLeftChannel(event);
			return;
		}
	}

	public void handleMemberLeftChannel(JSONObject message) {
		String channel = message.getString("channel");
		sendMessage("What is going on here?", channel, null);
	}

	public void handleEventMessage(JSONObject message) {

	}

	public void handleAppMention(JSONObject message) {
		if (message.getString("text").contains("/")) {
			beMonitor(message);
			return;
		} else {
			beListener(message);
		}
	}

	private void beListener(JSONObject message) {
		String user = message.getString("user");
		String channel = message.getString("channel");
		String text = message.getString("text").toLowerCase();
		String ts = message.getString("ts");

		if (Pattern.matches(ConversationPatternChat.CHAT_GREETINGS.getValue(), text)) {
			String[] replies = ConversationArraysReplyChat.GREETINGS.getReplies();
			int mudeIndex = getMudeIndex(replies.length - 1);
			if (user.equals("UGNHE8S8L")) {
				sendMessage(replies[mudeIndex] + " I am here, my master!", channel, null);
				return;
			}
			sendMessage(replies[mudeIndex], channel, null);
			return;
		}

		if (Pattern.matches(ConversationPatternChat.CHAT_PERSONAL_CR.getValue(), text)) {
			sendMessage("Я - Sion. Я був створений 26.04.2019. Мій творець <@UGNHE8S8L>", channel, ts);
			return;
		}
		
		if (Pattern.matches(ConversationPatternChat.CHAT_PERSONAL_HAU.getValue(), text)) {
			sendMessage("Все круто. А ти як?", channel, ts);
			return;
		}

		if (Pattern.matches(ConversationPatternChat.CHAT_PERSONAL_AGE.getValue(), text)) {
			String[] replies = ConversationArraysReplyChat.PERSONAL_AGE.getReplies();
			int mudeIndex = getMudeIndex(replies.length - 1);
			sendMessage(replies[mudeIndex], channel, ts);
			return;
		}
		
		String[] replies = ConversationArraysReplyChat.PLAIN_ANSWERS.getReplies();
		int mudeIndex = getMudeIndex(replies.length - 1);

		if (user.equals("UGNHE8S8L")) {
			sendMessage(" I am here, my master!", channel, ts);
		} else {
			sendMessage(replies[mudeIndex], channel, ts);
		}
	}
	
	private int getMudeIndex(int maxValue) {
		return (int) (Math.random() * maxValue);
	}

	private void beMonitor(JSONObject message) {
		String text = message.getString("text").toLowerCase();

		if (Pattern.matches(ConversationPatternMonitor.CHECKER_MODE.getRegex(), text)) {
			System.out.println("checker mode");
			checkerModeMonitor(message);
			return;
		}

		if (Pattern.matches(ConversationPatternMonitor.RANDOM_MODE.getRegex(), text)) {
			randomModeMonitor(message);
			return;
		}

		if (Pattern.matches(ConversationPatternMonitor.SINGLE_MESSAGE_MODE.getRegex(), text)) {
			System.out.println("single message mode");
			singleMessageModeMonitor(message);
			return;
		}
	}

	private void singleMessageModeMonitor(JSONObject message) {
		String singleMessage = new FileReader().readFromResourceFolder();
		sendMessage(singleMessage, TEST_CHANNEL, null);
		sendMessage(singleMessage, HOME_CHANNEL, null);
	}

	private void randomModeMonitor(JSONObject message) {
		String text = message.getString("text");
		String user = message.getString("user");
		String channel = message.getString("channel");
		String ts = message.getString("ts");

		String number = text.replaceAll("[^\\d]", "");
		int maxValue = number.equals("") ? 100 : Integer.parseInt(number);
		int random = (int) (Math.random() * maxValue) + 1;
		sendMessage("<@" + user + "> число: " + random, channel, ts);
	}

	private void checkerModeMonitor(JSONObject message) {
		String channel = message.getString("channel");
		String messageText = message.getString("text").toLowerCase();
		String ts = message.getString("ts");
		
		if (Pattern.matches(ConversationPatternMonitor.CHECKER_MODE_TRIGGER.getRegex(), messageText)) {
			messageTimeStamp = ts;
			checkedChannel = new Channel(channel);
			isInCheckerMode = true;
			addReaction("rocket", channel, ts);
		} else if (isInCheckerMode
				&& Pattern.matches(ConversationPatternMonitor.CHECKER_MODE_DISABLER.getRegex(), messageText)) {
			System.out.println("disable triggered");
			List<String> actUsers = checkedChannel.getReactedUsers(messageTimeStamp);
			List<String> idleUsers = new ArrayList<>();
			idleUsers.addAll(checkedChannel.getMembers());
			idleUsers.removeAll(actUsers);
			idleUsers.removeAll(getPriviledgedList());

			StringBuilder reply = new StringBuilder();
			if (idleUsers.size() == 0) {
				reply.append(":fire::fire::fire:Я вражений вашою активністю, земляни");
			} else {
				idleUsers.forEach(u -> reply.append("<@" + u + ">, "));
				reply.append(" - :sleeping:");
				System.out.println(reply);
			}
			sendMessage(reply.toString(), channel, ts);
			isInCheckerMode = false;
		}
	}

	private List<String> getPriviledgedList() {
		List<String> priviledged = new ArrayList<>();
		priviledged.add("UJ9U0CYLX");
		priviledged.add("DJ1PM69DX");
		priviledged.add("AJ33BH006");
		priviledged.add("U0PFCJ1AS");
		priviledged.add("UH26KF7BM");
		priviledged.add("U0PE0CYR3");
		priviledged.add("UHDNEB6RZ");
		priviledged.add("U0PE4CN8H");
		priviledged.add("UF2RWNU9H");
		priviledged.add("UH34HTZRA");
		priviledged.add("U3XP945V0");

		return priviledged;
	}

	private void addReaction(String name, String channel, String timestamp) {
		try {
			URL url = new URL(SlackAccessConstants.REACTIONS_ADD.getValue());
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
			
			String reply = "token=" + SlackAccessConstants.OAUTH_TOKEN + "&name=" + name + "&channel=" + channel + "&timestamp=" + timestamp;
			OutputStreamWriter outputStreamWriter = new OutputStreamWriter(connection.getOutputStream());
			outputStreamWriter.write(reply);
			outputStreamWriter.close();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private String sendMessage(String text, String channel, String parentTs) {
		StringBuilder response = new StringBuilder();
		PrintStream ps = null;
		DataInputStream dis = null;
		BufferedReader reader = null;
		try {
			URL url = new URL(SlackAccessConstants.POSTMESSAGE_URL.getValue());
			HttpURLConnection connection = (HttpURLConnection) url.openConnection();

			connection.setDoOutput(true);
			connection.setRequestMethod("POST");
			connection.setRequestProperty("Content-Type", "application/json; utf-8");
			connection.setRequestProperty("Authorization", "Bearer " + SlackAccessConstants.OAUTH_TOKEN.getValue());
			
			String reply = null;
			if (parentTs != null) {
				reply = "{\"channel\": \"" + channel + "\", \"thread_ts\": \"" + parentTs + "\", \"text\": \"" + text + "\"}";
			} else {
				reply = "{\"channel\": \"" + channel + "\", \"text\": \"" + text + "\"}";
			}
			
			ps = new PrintStream(connection.getOutputStream());
			ps.write(reply.getBytes());

			dis = new DataInputStream(connection.getInputStream());
			reader = new BufferedReader(new InputStreamReader(dis));

			String line = "";
			while((line = reader.readLine()) != null) {
				response.append(line);
			}
			return response.toString();
		} catch (MalformedURLException e) {
			LOGGER.debug("Wrong URL!", e);
		} catch (IOException e) {
			LOGGER.debug("sending message failed", e);
		} finally {
			if (ps != null) {
				ps.close();
			}
			try {
				if (dis != null) {
					dis.close();
				}
				if (reader != null) {
					reader.close();
				}
			} catch (IOException ex) {
				LOGGER.debug("closing resources failed");
			}
		}
		return response.toString();
	}
}