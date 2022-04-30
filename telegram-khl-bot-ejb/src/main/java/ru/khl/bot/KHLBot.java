package ru.khl.bot;

import common.vk.utils.RedisEntity;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.khl.bot.bean.game.Game;
import ru.khl.bot.utils.Clubs;

import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by alexey on 01.11.16.
 */

public class KHLBot extends TelegramLongPollingBot {

    private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());
    private final String botName;
    private final String botToken;
    private Game game;

    public KHLBot(Game game) {
        botName = RedisEntity.getInstance().getElement("khl_botName");
        botToken = RedisEntity.getInstance().getElement("khl_botToken");
        this.game = game;
    }

    public KHLBot() {
        botName = RedisEntity.getInstance().getElement("khl_botName");
        botToken = RedisEntity.getInstance().getElement("khl_botToken");
    }

    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();

        if (update.hasChannelPost())
            return;

        if (message != null && message.getText() != null) {

            logger.log(Level.INFO, "FirstName: {0}, LastName: {1}, UserName: {2} \n" +
                            "UserId: {3}, CommandInput: {4}",
                    new Object[]{message.getFrom().getFirstName(),
                            message.getFrom().getLastName(),
                            message.getFrom().getUserName(),
                            message.getFrom().getId(),
                            message.getText()});

            String text;

            if (message.getText().equals("/start")) {
                text = "Enter your favorite conference. /help - помощь";
            } else if (message.getText().equals("/help")) {
                text = "/hockey_clubs_of_east - клубы восточной конференции\n" +
                        "/hockey_clubs_of_west - клубы западной конференции\n" +
                        "/help - помощь";
            } else if (message.getText().equals("/hockey_clubs_of_west")) {
                text = Clubs.getWESTTeams();
            } else if (message.getText().equals("/hockey_clubs_of_east")) {
                text = Clubs.getEASTTeams();
            } else if (Clubs.getEASTTeams().contains(message.getText())
                    || Clubs.getWESTTeams().contains(message.getText())) {
                text = game.getGamesInfoOfHockeyClub(message.getText());
            } else {
                text = "Bad Command! /help - помощь";
            }

            if (text == null
                    || text.isEmpty())
                return;

            try {
                execute(SendMessage.builder()
                        .text(text)
                        .chatId(String.valueOf(message.getFrom().getId()))
                        .replyToMessageId(message.getMessageId())
                        .disableWebPagePreview(true)
                        .build());
            } catch (TelegramApiException e) {
                logger.log(Level.SEVERE, null, e);
            }
        }
    }

    public String getBotUsername() {
        return this.botName;
    }

    public String getBotToken() {
        return this.botToken;
    }

}
