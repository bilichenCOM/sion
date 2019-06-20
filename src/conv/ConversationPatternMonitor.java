package conv;

public enum ConversationPatternMonitor {

	CHECKER_MODE(".*((count)|(end)).*"),
	CHECKER_MODE_TRIGGER(".*(count).*"),
	CHECKER_MODE_DISABLER(".*(end).*"),
	RANDOM_MODE(".*((rand)|(случ)|(число)|(number)).*"),
	SINGLE_MESSAGE_MODE(".*(singlemessage).*");

	private String regex;

	private ConversationPatternMonitor(String regex) {
		this.regex = regex;
	}

	public String getRegex() {
		return regex;
	}

	public String toString() {
		return regex;
	}
}
