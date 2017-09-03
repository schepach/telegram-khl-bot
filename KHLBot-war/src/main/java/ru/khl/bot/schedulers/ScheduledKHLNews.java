package ru.khl.bot.schedulers;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.khl.bot.KHLBot;
import ru.khl.bot.constants.Constants;
import ru.khl.bot.utils.Connection;

import java.io.IOException;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.TimerTask;

/**
 * Created by Alexey on 13.12.2016.
 */

public class ScheduledKHLNews extends TimerTask {

    private static final Logger LOGGER = Logger.getLogger(ScheduledKHLNews.class.getSimpleName());

    @Override
    public void run() {
        try {

            Calendar cal = Calendar.getInstance();
            LocalTime currentTime = LocalTime.of(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
            LOGGER.info("currentTimeForKHLNews = " + currentTime);

            String newsUrl = Connection.getKHLNews(Constants.URL_KHL_INFO, currentTime);

            if (!newsUrl.isEmpty()) {
                new KHLBot().sendMessage(new SendMessage().setChatId("@KHL_Info").setText(newsUrl));
            }

        } catch (TelegramApiException | IOException | JSONException e) {
            LOGGER.info(Constants.UNEXPECTED_ERROR.concat(e.getMessage() + e));
        } catch (Exception ex) {
            LOGGER.info("EXCEPTION: " + ex.getMessage() + ex);
        }
    }
}