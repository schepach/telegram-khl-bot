package ru.khl.bot;

import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.khl.bot.utils.BotHelper;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by alexey on 01.11.16.
 */

public class KHLBot extends TelegramLongPollingBot {

    private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();

        if (message != null && message.hasText()) {

            this.logger.log(Level.INFO, "FirstName: " + message.getFrom().getFirstName());
            this.logger.log(Level.INFO, "LastName: " + message.getFrom().getLastName());
            this.logger.log(Level.INFO, "UserName: " + message.getFrom().getUserName());
            this.logger.log(Level.INFO, "UserId: " + message.getFrom().getId());
            this.logger.log(Level.INFO, "InputCommand: " + message.getText());

            sendMsg(message, BotHelper.checkUserText(message.getText().toUpperCase()));
        }
    }


    public String getBotUsername() {
        return "botname";
    }

    public String getBotToken() {
        return "bottoken";
    }

    private void sendMsg(Message message, String text) {
        if (text != null && !text.isEmpty()) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setText(text);
            sendMessage.setChatId(message.getChatId().toString());
            sendMessage.setReplyToMessageId(message.getMessageId());
            try {
                execute(sendMessage);
            } catch (Exception ex) {
                this.logger.log(Level.SEVERE, "Method sendMsg exception: ", ex);
            }
        }
    }
}
