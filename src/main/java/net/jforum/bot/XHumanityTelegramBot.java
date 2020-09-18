package net.jforum.bot;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.security.GeneralSecurityException;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Properties;
import java.util.stream.Collectors;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.log4j.Logger;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import com.google.api.services.youtube.model.VideoListResponse;

import net.jforum.JForumExecutionContext;
import net.jforum.dao.DataAccessDriver;
import net.jforum.dao.TelegramUserDAO;
import net.jforum.dao.UserDAO;
import net.jforum.entities.TelegramUser;
import net.jforum.entities.User;
import net.jforum.exceptions.APIException;
import net.jforum.exceptions.DatabaseException;
import net.jforum.util.Hash;
import net.jforum.util.preferences.ConfigKeys;
import net.jforum.util.preferences.SystemGlobals;

public class XHumanityTelegramBot extends TelegramLongPollingBot {

	private static final Logger LOGGER = Logger.getLogger(XHumanityTelegramBot.class);

	private static final String BOT_NAME = "xHumanityBot";
	private static final int PROMO_FORUM_ID = 1;
	private static final String FORUM_PROTOCOL = "https";
	private static final String FORUM_HOST_KEY = "forum.host";
	private static final int FORUM_PORT = 8443;
	private static final String USERNAME_PREFIX = "xH";
	private static final String EMAIL_DOMAIN = "xhumanity.org";
	private static final String TELEGRAM_TOKEN_PROP_KEY = "telegram.token";
	private static final String FORUM_API_PROP_KEY = "forum.api.key";

	private Properties integrationProp = new Properties();

	private final TelegramUserDAO telegramUserDao = DataAccessDriver.getInstance().newTelegramUserDAO();

	public XHumanityTelegramBot() {
		String appPath = SystemGlobals.getValue(ConfigKeys.APPLICATION_PATH);
		String integrationFile = appPath + "/WEB-INF/config/integration.properties";
		XUtils.loadProps(integrationProp, new File(integrationFile));
	}

	@Override
	public void onUpdateReceived(Update update) {
		if (update.hasMessage()) {
			TelegramUser telegramUser = createUserIfNotExists(update.getMessage());

			if (update.getMessage().hasContact()) {
				processContact(update, telegramUser);
			} else if (update.getMessage().hasText()) {
				long chatId = update.getMessage().getChatId();
				String messageText = update.getMessage().getText();

				if (messageText.equals("/start")) {
					processStart(update, chatId, messageText);
				} else if (messageText.equals("/forum_sign_up")) {
					createForumAccount(update, telegramUser, chatId, messageText);
				} else if (messageText.equals("/share_phone_number")) {
					displayShareNumberOption(update, chatId, messageText);
				} else if (messageText.equals("/share_email_address")) {
					displayShareEmailText(update, chatId, messageText);
				} else if (YoutubeUtils.isYoutubeLink(messageText)) {
					processYoutubeLink(update, telegramUser, chatId, messageText);
				} else if (XUtils.isEmailValid(messageText)) {
					processEmailAddress(update, telegramUser, chatId, messageText);
				} else {
					// Unknown command
					String answer = "Unknown command";
					SendMessage message = new SendMessage().setChatId(chatId).setText(answer);
					try {
						execute(message);
						logReceivedMessage(update.getMessage().getChat(), messageText, answer);
					} catch (TelegramApiException e) {
						LOGGER.error(e);
					}
				}
			}
		}
	}

	private TelegramUser createUserIfNotExists(Message message) {
		long userId = message.getChat().getId();
		TelegramUser telegramUser = telegramUserDao.selectByUserId(userId);
		if (telegramUser.getUserId() == 0) {
			long chatId = message.getChatId();
			String firstName = message.getChat().getFirstName();
			String lastName = message.getChat().getLastName();
			String username = message.getChat().getUserName();
			telegramUser.setChatId(chatId);
			telegramUser.setUserId(userId);
			telegramUser.setUsername(username);
			telegramUser.setFirstName(firstName);
			telegramUser.setLastName(lastName);

			telegramUserDao.addNew(telegramUser);
		}
		return telegramUser;
	}

	private void processStart(Update update, long chatId, String messageText) {
		String answer = "Salut xHumanicus! Deocamdata nu stiu sa fac mare lucru, dar in curand ma voi inzestra cu capacitati noi. "
				+ "To register on our Forum click /forum_sign_up\n"
				+ "By cicking on it you agree with our Terms and Conditions below\n\n"
				+ "While the administrators and moderators of this forum will attempt to remove or edit any generally objectionable material as quickly as possible, it is impossible to review every message. Therefore you acknowledge that all posts made to these forums express the views and opinions of the author and not the administrators, moderators or webmaster (except for posts by these people) and hence will not be held liable.\n\n"
				+ "You agree not to post any abusive, obscene, vulgar, slanderous, hateful, threatening, sexually-oriented or any other material that may violate any applicable laws. Doing so may lead to you being immediately and permanently banned (and your service provider being informed). The IP address of all posts is recorded to aid in enforcing these conditions. You agree that the webmaster, administrator and moderators of this forum have the right to remove, edit, move or close any topic at any time should they see fit. As a user you agree to any information you have entered above being stored in a database. While this information will not be disclosed to any third party without your consent the webmaster, administrator and moderators cannot be held responsible for any hacking attempt that may lead to the data being compromised.\n\n"
				+ "This forum system uses cookies to store information on your local computer. These cookies do not contain any of the information you have entered above; they serve only to improve your viewing pleasure. The e-mail address is used only for confirming your registration details and password (and for sending new passwords should you forget your current one).\n\n"
				+ "/forum_sign_up";
		SendMessage message = new SendMessage().setChatId(chatId).setText(answer).setParseMode(ParseMode.HTML);
		try {
			execute(message);
			logReceivedMessage(update.getMessage().getChat(), messageText, answer);
		} catch (TelegramApiException e) {
			LOGGER.error(e);
		}
	}

	private void createForumAccount(Update update, TelegramUser telegramUser, long chatId, String messageText) {
		String username = Long.toString(telegramUser.getChatId());
		String email = telegramUser.getChatId() + "@" + EMAIL_DOMAIN; // telegramUser.getFirstName() + "." +
																		// telegramUser.getLastName() +
																		// "@xhumanity.org";
		String password = generatePassword();

		String answer = "Account created.";

		try {
			User user = insertForumUser(username, email, password, chatId);
			telegramUser.setForumUserId(user.getId());
			telegramUserDao.update(telegramUser);
			answer += " Username: " + user.getUsername() + ", Pass: " + password + "\n" + "To login go to "
					+ FORUM_PROTOCOL + "://" + integrationProp.getProperty(FORUM_HOST_KEY) + ":" + FORUM_PORT + "/jforum/user/login.page\n\n"
					+ "Now you can send us links to your promotional videos. Just post the link here and we'll do the rest for you\n\n"
					+ "For a better experience within our comunity /share_phone_number with us\n"
					+ "Additionaly to register your email with your forum account click /share_email_address (will be used in case you want to reset the password)";
		} catch (Exception e) {
			LOGGER.error(e);
			answer = e.getMessage();
		}

		SendMessage msg = new SendMessage().setChatId(chatId).setText(answer);
		try {
			execute(msg);
			logReceivedMessage(update.getMessage().getChat(), messageText, answer);
		} catch (TelegramApiException e) {
			LOGGER.error(e);
		}
	}

	/**
	 * Creates a new forum user. Required parameters are "username", "email" and
	 * "password".
	 */
	public User insertForumUser(String username, String email, String password, Long chatId) {
		int userId = 0;
		final User user = new User();
		try {

			if (username.length() > SystemGlobals.getIntValue(ConfigKeys.USERNAME_MAX_LENGTH)) {
				throw new APIException("Username too big: " + username);
			}

			if (username.indexOf('<') > -1 || username.indexOf('>') > -1) {
				throw new APIException("Invalid characters for username: " + username);
			}

			final UserDAO dao = DataAccessDriver.getInstance().newUserDAO();

			if (dao.isUsernameRegistered(username)) {
				throw new APIException("Username already used: " + username);
			}

			if (dao.findByEmail(email) != null) {
				throw new APIException("Email already used: " + email);
			}

			user.setUsername(username);
			user.setEmail(email);
			user.setPassword(Hash.sha512(password + SystemGlobals.getValue(ConfigKeys.USER_HASH_SEQUENCE)));

			userId = dao.addNew(user);
			User forumUser = dao.findById(userId);
			updateUsernameAndEmail(userId, forumUser);
			dao.update(forumUser);
		} catch (Exception e) {
			LOGGER.error(e);
			throw e;
		} finally {
			try {
				JForumExecutionContext.getConnection().commit();
			} catch (SQLException e) {
				throw new DatabaseException(e);
			}
		}

		return user;
	}

	private void updateUsernameAndEmail(int userId, final User user) {
		user.setId(userId);
		String newUsername = USERNAME_PREFIX + String.format("%05d", userId);
		String newEmail = newUsername + "@" + EMAIL_DOMAIN;
		user.setEmail(newEmail);
		user.setUsername(newUsername);
	}

	private void displayShareNumberOption(Update update, long chatId, String messageText) {
		String answer = "To share your phone number click on the button bellow";
		SendMessage message = new SendMessage().setChatId(chatId).setText(answer);
		ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
		List<KeyboardRow> keyboard = new ArrayList<>();
		KeyboardRow row = new KeyboardRow();
		KeyboardButton requestContact = new KeyboardButton("Send my phone number");
		requestContact.setRequestContact(true);
		row.add(requestContact);
		keyboard.add(row);
		keyboardMarkup.setKeyboard(keyboard);
		keyboardMarkup.setResizeKeyboard(true);
		message.setReplyMarkup(keyboardMarkup);
		try {
			execute(message);
			logReceivedMessage(update.getMessage().getChat(), messageText, answer);
		} catch (TelegramApiException e) {
			LOGGER.error(e);
		}
	}

	private void displayShareEmailText(Update update, long chatId, String messageText) {
		String answer = "Enter your email address";
		SendMessage message = new SendMessage().setChatId(chatId).setText(answer);
		try {
			execute(message);
			logReceivedMessage(update.getMessage().getChat(), messageText, answer);
		} catch (TelegramApiException e) {
			LOGGER.error(e);
		}
	}

	private void processContact(Update update, TelegramUser telegramUser) {
		long chatId = update.getMessage().getChatId();
		String messageText = update.getMessage().getText();

		Contact contact = update.getMessage().getContact();
		logReceivedContact(contact);
		telegramUser.setPhoneNumber(contact.getPhoneNumber());
		telegramUserDao.update(telegramUser);

		String answer = "Your phone number was added to your profile";
		SendMessage message = new SendMessage().setChatId(chatId).setText(answer).setParseMode(ParseMode.MARKDOWN);
		message.setReplyMarkup(new ReplyKeyboardRemove());
		try {
			execute(message);
			logReceivedMessage(update.getMessage().getChat(), messageText, answer);
		} catch (TelegramApiException e) {
			LOGGER.error(e);
		}
	}

	private void processYoutubeLink(Update update, TelegramUser telegramUser, long chatId, String videoUrl) {
		String answer = "Your clip was taken into account. You can visit our forum to see the status.";
		if (telegramUser.getForumUserId() == 0) {
			answer = "We cannot take into account your link. You need to create before an account on our forum /forum_sign_up";
		} else {
			String videoId = YoutubeUtils.getVideoIdFromYoutubeUrl(videoUrl);
			LOGGER.info("videoId = " + videoId);
			try {
				VideoListResponse response = YoutubeUtils.getVideoDetails(videoId);
				LOGGER.info(response);
			} catch (GeneralSecurityException | IOException e) {
				LOGGER.error(e);
			}

			try {
				final UserDAO dao = DataAccessDriver.getInstance().newUserDAO();
				User forumUser = dao.findById(telegramUser.getForumUserId());

				String postLink = createPost(forumUser, telegramUser.getFirstName(), videoUrl);
				answer += " " + postLink;
				LOGGER.info(postLink);
			} catch (Exception e) {
				LOGGER.error(e);
				answer = "Error occurred while processing your link";
			}
		}
		try {
			SendMessage message = new SendMessage().setChatId(chatId).setText(answer);
			execute(message);
		} catch (TelegramApiException e) {
			LOGGER.error(e);
		}
		logReceivedMessage(update.getMessage().getChat(), videoUrl, answer);
	}

	public String createPost(User user, String firstName, String url) throws Exception {
		int forumId = PROMO_FORUM_ID;
		final String subject = firstName + "'s promotional video";
		final String message = "This is my video. Waiting for your reaction!\n[youtube]" + url + "[/youtube]";

		URI uri = new URI(FORUM_PROTOCOL, null, integrationProp.getProperty(FORUM_HOST_KEY), FORUM_PORT,
			    "/jforum/postApi/insert/" + integrationProp.getProperty(FORUM_API_PROP_KEY) + "/" + user.getEmail() + "/" + forumId + ".page", 
			    null,
			    null);
		String postLink = "Error creating automated post"; 
		try {
			String response = HttpUtils.sendPOST(uri, subject, message);
			postLink = FORUM_PROTOCOL + "://" + integrationProp.getProperty(FORUM_HOST_KEY) + ":" + FORUM_PORT + HttpUtils.extractPostPath(response);
		} catch (IOException | URISyntaxException e) {
			LOGGER.error(e);
			throw e;
		}
		return postLink;
	}

	private void processEmailAddress(Update update, TelegramUser telegramUser, long chatId, String email) {
		String answer = "Your email address was associated with your forum account.";
		if (telegramUser.getForumUserId() == 0) {
			answer = "We cannot link this email to your forum account. To create an account click on /forum_sign_up";
		} else {
			LOGGER.info("registering email address = " + email);
	
			try {
				final UserDAO dao = DataAccessDriver.getInstance().newUserDAO();
				User forumUser = dao.findById(telegramUser.getForumUserId());
				forumUser.setEmail(email);
				dao.update(forumUser);
			} catch (Exception e) {
				LOGGER.error(e);
				answer = "Error occurred while processing your email address";
			} finally {
				try {
					JForumExecutionContext.getConnection().commit();
				} catch (SQLException e) {
					throw new DatabaseException(e);
				}
			}

		}
		try {
			SendMessage message = new SendMessage().setChatId(chatId).setText(answer);
			execute(message);
		} catch (TelegramApiException e) {
			LOGGER.error(e);
		}
		logReceivedMessage(update.getMessage().getChat(), email, answer);
	}

	@Override
	public String getBotUsername() {
		return BOT_NAME;
	}

	@Override
	public String getBotToken() {
		return integrationProp.getProperty(TELEGRAM_TOKEN_PROP_KEY);
	}

	private String generatePassword() {
		String upperCaseLetters = RandomStringUtils.random(2, 65, 90, true, true);
		String lowerCaseLetters = RandomStringUtils.random(2, 97, 122, true, true);
		String numbers = RandomStringUtils.randomNumeric(2);
		String specialChar = RandomStringUtils.random(2, 33, 47, false, false);
		String totalChars = RandomStringUtils.randomAlphanumeric(2);
		String combinedChars = upperCaseLetters.concat(lowerCaseLetters).concat(numbers).concat(specialChar)
				.concat(totalChars);
		List<Character> pwdChars = combinedChars.chars().mapToObj(c -> (char) c).collect(Collectors.toList());
		Collections.shuffle(pwdChars);
		String password = pwdChars.stream().collect(StringBuilder::new, StringBuilder::append, StringBuilder::append)
				.toString();
		return password;
	}

	private void logReceivedMessage(Chat chat, String txt, String botAnswer) {
		long userId = chat.getId();
		String firstName = chat.getFirstName();
		String lastName = chat.getLastName();
		String userName = chat.getUserName();

		LOGGER.info("\n ----------------------------");
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		LOGGER.info(dateFormat.format(date));
		LOGGER.info("Message from " + firstName + " " + lastName + ". (id = " + userId + " userName = " + userName
				+ ") \n Text - " + txt);
		LOGGER.info("Bot answer: \n Text - " + botAnswer);
	}

	private void logReceivedContact(Contact contact) {
		long userId = contact.getUserID();
		String firstName = contact.getFirstName();
		String lastName = contact.getLastName();
		String phoneNumber = contact.getPhoneNumber();

		LOGGER.info("\n ----------------------------");
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		LOGGER.info(dateFormat.format(date));
		LOGGER.info("Message from " + firstName + " " + lastName + ". (userId = " + userId + ") \n Phone number - "
				+ phoneNumber);
	}
}