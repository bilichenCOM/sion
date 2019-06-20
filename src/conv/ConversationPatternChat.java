package conv;

public enum ConversationPatternChat {

	CHAT_GREETINGS(".*((hello)|(привіт)|(привет)|(здраствуй)|(здравствуй)|(hi)).*"),
	CHAT_PERSONAL_CR(".*((ти хто)|(who you are)|(who are you)|(ты кто)|(кто ты)|(хто ти)|(создатель)|(master)|(creator)|(тебе створив)|(create you)|(кто тебя создал)).*"),
	CHAT_PERSONAL_AGE(".*((тебе лет)|(возраст)|(тобі років)|(old are you)).*"),
	CHAT_PERSONAL_HAU(".*((как дела)|(ты как)|(как ты)|(как поживаеш)|(як справи)|(як живеться)|(how are you)).*");

	private String regex;

	private ConversationPatternChat(String regex) {
		this.regex = regex;
	}

	public String getValue() {
		return regex;
	}

	public String toString() {
		return regex;
	}
}
