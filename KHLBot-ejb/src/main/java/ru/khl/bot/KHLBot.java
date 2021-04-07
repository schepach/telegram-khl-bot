package ru.khl.bot;

import common.vk.utils.RedisEntity;
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
    private final String botName;
    private final String botToken;

    public KHLBot() {
        botName = RedisEntity.getInstance().getElement("khl_botName");
        botToken = RedisEntity.getInstance().getElement("khl_botToken");
    }


    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();

        if (message != null && message.hasText()) {

            logger.log(Level.INFO, "FirstName: {0}, LastName: {1}, UserName: {2} \n" +
                            "UserId: {3}, ChatId: {4}, CommandInput: {5}",
                    new Object[]{message.getFrom().getFirstName(),
                            message.getFrom().getLastName(),
                            message.getFrom().getUserName(),
                            message.getFrom().getId(),
                            message.getChatId(),
                            message.getText()});

            sendMsg(message, BotHelper.checkUserText(message.getText().toUpperCase()));
        }
    }


    public String getBotUsername() {
        return botName;
    }

    public String getBotToken() {
        return botToken;
    }

    private void sendMsg(Message message, String text) {
        if (text == null
                || text.isEmpty()
                || message.getChatId() == null)
            return;

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
