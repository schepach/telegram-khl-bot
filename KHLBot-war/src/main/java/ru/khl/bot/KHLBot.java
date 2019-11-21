package ru.khl.bot;

import org.apache.log4j.Logger;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import ru.khl.bot.utils.BotHelper;

/**
 * Created by alexey on 01.11.16.
 */

public class KHLBot extends TelegramLongPollingBot {

    private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();

        if (message != null && message.hasText()) {

            logger.info("FirstName: " + message.getFrom().getFirstName());
            logger.info("LastName: " + message.getFrom().getLastName());
            logger.info("UserName: " + message.getFrom().getUserName());
            logger.info("UserId: " + message.getFrom().getId());
            logger.info("InputCommand: " + message.getText());

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
                logger.info("Method sendMsg exception: ", ex);
            }
        }
    }
}
