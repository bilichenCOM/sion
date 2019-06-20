package conv;

import java.util.Arrays;
import java.util.Calendar;

public enum ConversationArraysReplyChat {

	GREETINGS(new String[] { "Привет, привет", "Hi, man!", "Hello, here!", "Привіт, як справи?", "Hello, human being!",
			"Hello, bro!", "Hi", "Привіт", "....", "Здраствуй", "Здравствуй", "Привіт, землянин!" }),
	PLAIN_ANSWERS(new String[] { "Who call me?", "Чого тобі?", "Я тут", "Слухаю...", ".....", "Whaa-aaat?",
			"Хороша погода сьогодні" }),
	PERSONAL_AGE(new String[] { "мой отчет показывает, что я живу уже ", "Я був створений 26.04.2019",
			"I am just too old for you, you know...", "Я еще не старый." });

	private String[] replies;
	private Calendar calendar = Calendar.getInstance();

	private ConversationArraysReplyChat(String[] replies) {
		this.replies = replies;
		calendar.set(2019, Calendar.APRIL, 26);
	}

	public String[] getReplies() {
		replies[0] = replies[0]
				+ Long.toString(
						(Calendar.getInstance().getTimeInMillis() - calendar.getTimeInMillis()) / 1000 / 3600 / 24)
				+ " дней";
		return replies;
	}

	public String toString() {
		return Arrays.toString(getReplies());
	}
}
