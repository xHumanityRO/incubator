package net.jforum.bot;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

public class XHumanityTelegramBot extends TelegramLongPollingBot {
	
	@Override
	public void onUpdateReceived(Update update) {
		if (update.hasMessage() && update.getMessage().hasContact()) {
			long chatId = update.getMessage().getChatId();
			String userUsername = update.getMessage().getChat().getUserName();
			String messageText = update.getMessage().getText();

			Contact contact = update.getMessage().getContact();
            long userId = contact.getUserID();
			String userFirstName = contact.getFirstName();
            String userLastName = contact.getLastName();
			String phoneNumber = contact.getPhoneNumber();
			logReceivedContact(userFirstName, userLastName, Long.toString(userId), phoneNumber);
			
			String answer = "/hide_keyboard";
			SendMessage message = new SendMessage().setChatId(chatId).setText(answer);
			try {
				execute(message);
		        logReceivedMessage(userFirstName, userLastName, Long.toString(userId), userUsername, messageText, answer);
			} catch (TelegramApiException e) {
				e.printStackTrace();
			}
		} else if (update.hasMessage() && update.getMessage().hasText()) { // // We check if the update has a message and the message has text
			long chatId = update.getMessage().getChatId();
            long userId = update.getMessage().getChat().getId();
			String userFirstName = update.getMessage().getChat().getFirstName();
            String userLastName = update.getMessage().getChat().getLastName();
            String userUsername = update.getMessage().getChat().getUserName();
			String messageText = update.getMessage().getText();
			
			if (messageText.equals("/start")) {
				String answer = "Salut xHumanicus! Deocamdata nu stiu sa fac mare lucru, dar in curand ma voi inzestra cu capacitati noi. "
						+ "Apasa /create_account pentru a-ti crea un cont pe forum\n"
						+ "Si Pregateste-te de promovare! :)"; // update.getMessage().getText()
				SendMessage message = new SendMessage() // Create a SendMessage object with mandatory fields
						.setChatId(chatId).setText(answer);
				try {
					execute(message); // Call method to send the message
			        logReceivedMessage(userFirstName, userLastName, Long.toString(userId), userUsername, messageText, answer);
				} catch (TelegramApiException e) {
					e.printStackTrace();
				}
			} else if (messageText.equals("/create_account")) {
				String answer = "Please share your phone number to create an account to out Forum.";
				SendMessage message = new SendMessage() // Create a message object object
						.setChatId(chatId).setText(answer);
				// Create ReplyKeyboardMarkup object
				ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
				// Create the keyboard (list of keyboard rows)
				List<KeyboardRow> keyboard = new ArrayList<>();
				// Create a keyboard row
				KeyboardRow row = new KeyboardRow();
				// Set each button, you can also use KeyboardButton objects if you need
				// something else than text
				KeyboardButton requestContact = new KeyboardButton("Trimite numarul meu de tel");
				requestContact.setRequestContact(true);
				row.add(requestContact);
				// Add the first row to the keyboard
				keyboard.add(row);
				// Set the keyboard to the markup
				keyboardMarkup.setKeyboard(keyboard);
				keyboardMarkup.setResizeKeyboard(true);
				// Add it to the message
				message.setReplyMarkup(keyboardMarkup);
				try {
					execute(message); // Sending our message object to user
			        logReceivedMessage(userFirstName, userLastName, Long.toString(userId), userUsername, messageText, answer);
				} catch (TelegramApiException e) {
					e.printStackTrace();
				}
			} else if (messageText.equals("/hide_keyboard")) {
				String answer = "Keyboard hidden";
				SendMessage msg = new SendMessage().setChatId(chatId).setText(answer);
				ReplyKeyboardRemove keyboardMarkup = new ReplyKeyboardRemove();
				msg.setReplyMarkup(keyboardMarkup);
				try {
					execute(msg); // Call method to hide the keyboard
			        logReceivedMessage(userFirstName, userLastName, Long.toString(userId), userUsername, messageText, answer);
				} catch (TelegramApiException e) {
					e.printStackTrace();
				}
			} else {
				// Unknown command
				String answer = "Unknown command";
				SendMessage message = new SendMessage() // Create a message object object
						.setChatId(chatId).setText(answer);
				try {
					execute(message); // Sending our message object to user
			        logReceivedMessage(userFirstName, userLastName, Long.toString(userId), userUsername, messageText, answer);
				} catch (TelegramApiException e) {
					e.printStackTrace();
				}
			}
		}
	}

	@Override
	public String getBotUsername() {
		return "xHumanityBot";
	}

	@Override
	public String getBotToken() {
		return "1176331978:AAFWKL0u5xWXuWxEUnkoewEiDdl1Ub9g2ns";
	}
	
	private void logReceivedMessage(String firstName, String lastName, String userId, String userName, String txt, String botAnswer) {
        System.out.println("\n ----------------------------");
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        System.out.println(dateFormat.format(date));
        System.out.println("Message from " + firstName + " " + lastName + ". (id = " + userId + " userName = " + userName + ") \n Text - " + txt);
        System.out.println("Bot answer: \n Text - " + botAnswer);
    }

	private void logReceivedContact(String firstName, String lastName, String userId, String phoneNumber) {
        System.out.println("\n ----------------------------");
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        Date date = new Date();
        System.out.println(dateFormat.format(date));
        System.out.println("Message from " + firstName + " " + lastName + ". (userId = " + userId +") \n Phone number - " + phoneNumber);
    }
}