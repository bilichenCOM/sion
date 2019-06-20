package apis;

public enum SlackAccessConstants {
	OAUTH_TOKEN("xoxb-23476289909-621952440711-JScS3vjcrjaFrpETvIKWd94m"),
	POSTMESSAGE_URL("https://slack.com/api/chat.postMessage"),
	CONVERSATIONS_MEMBERS("https://slack.com/api/conversations.members"),
	REACTIONS_GET("https://slack.com/api/reactions.get"),
	REACTIONS_ADD("https://slack.com/api/reactions.add"),
	CHANNEL_ID("GGTDVFQG6");

	private String value;

	private SlackAccessConstants(String value) {
		this.value = value;
	}

	public String getValue() {
		return value;
	}

	public String toString() {
		return value;
	}
}
