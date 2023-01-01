package ru.khl.bot;

import common.vk.utils.RedisEntity;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.AnswerCallbackQuery;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.khl.bot.bean.game.Game;

import javax.inject.Inject;
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

    @Inject
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

        try {
            if (update != null) {

                Message message = update.getMessage();

                if (message != null) {

                    // If the message is from bot, channel or group - no action
                    if ((message.getFrom() != null && message.getFrom().getIsBot())
                            || message.isSuperGroupMessage()
                            || message.isGroupMessage()
                            || message.isChannelMessage())
                        return;


                    if (update.hasMessage() && message.hasText()) {
                        String text = message.getText();
                        Long userId = message.getFrom().getId();

                        logger.log(Level.INFO, "FirstName: {0}, LastName: {1}, UserName: {2} \n" +
                                        "UserId: {3}, Input: {4}",
                                new Object[]{message.getFrom().getFirstName(),
                                        message.getFrom().getLastName(),
                                        message.getFrom().getUserName(),
                                        userId,
                                        text});

                        if (text != null) {
                            if (text.equals("/start")) {
                                this.game.getConferences(userId);
                            } else {
                                this.game.sendDefaultMessage(userId);
                            }
                        }
                    }
                }

                // Check callback
                if (update.hasCallbackQuery()) {
                    String callBackData = update.getCallbackQuery().getData();
                    String callBackQueryId = update.getCallbackQuery().getId();
                    Long userId = update.getCallbackQuery().getFrom().getId();

                    logger.log(Level.INFO, "callBackData - {0}", callBackData);
                    logger.log(Level.INFO, "userId - {0}", userId);

                    if (callBackData.equals("west")
                            || callBackData.equals("east")) {
                        this.game.getClubsByConference(userId, callBackData);
                    } else if (callBackData.equals("conferences")) {
                        this.game.sendDefaultMessage(userId);
                    } else {
                        this.game.sendInfoAboutGames(userId, callBackData);
                    }
                    this.sendAnswerCallbackQuery(callBackQueryId);
                }
            }

        } catch (Exception e) {
            logger.log(Level.SEVERE, null, e);
        }
    }

    private void sendAnswerCallbackQuery(String callBackQueryId) {
        AnswerCallbackQuery answerCallbackQuery = AnswerCallbackQuery.builder()
                .callbackQueryId(callBackQueryId)
                .build();
        try {
            execute(answerCallbackQuery);
        } catch (TelegramApiException e) {
            logger.log(Level.SEVERE, null, e);
        }
    }

    public String getBotUsername() {
        return this.botName;
    }

    public String getBotToken() {
        return this.botToken;
    }

}
