package ru.khl.bot.schedulers;

import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import ru.khl.bot.KHLBot;
import ru.khl.bot.constants.Constants;

import java.io.IOException;
import java.util.TimerTask;

/**
 * Created by Alexey on 13.12.2016.
 */

public class ScheduledTaskNews extends TimerTask {

    @Override
    public void run() {
        try {
            new KHLBot().sendMessage(new SendMessage().setChatId("@KHL_Info").setText(ru.khl.utils.Connection.getKHLNews(Constants.URL_KHL_NEWS)));
        } catch (TelegramApiException | IOException e) {
            e.printStackTrace();
        }
    }
}