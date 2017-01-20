package ru.khl.bot.schedulers;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.khl.bot.KHLBot;
import ru.khl.bot.constants.Constants;
import ru.khl.bot.utils.Connection;

import java.io.IOException;
import java.util.TimerTask;

/**
 * Created by Alexey on 13.12.2016.
 */

public class ScheduledKHLVideo extends TimerTask {

    private static final Logger LOGGER = Logger.getLogger(ScheduledKHLVideo.class.getSimpleName());

    @Override
    public void run() {
        try {
            String videoUrl = Connection.getVideo(Constants.URL_KHL_INFO);

            if (!videoUrl.isEmpty()) {
                new KHLBot().sendMessage(new SendMessage().setChatId("@KHL_Info").setText(videoUrl));
            }

        } catch (TelegramApiException | IOException | JSONException e) {
            LOGGER.info(Constants.UNEXPECTED_ERROR.concat(e.getMessage() + e));
        } catch (Exception ex) {
            LOGGER.info("EXCEPTION: " + ex.getMessage() + ex);
        }
    }
}