package ru.khl.bot.schedulers;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.khl.bot.KHLBot;
import ru.khl.bot.constants.Constants;
import ru.khl.bot.listener.KHLBotListener;
import ru.khl.bot.utils.Connection;

import java.io.IOException;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.TimerTask;

/**
 * Created by Alexey on 13.12.2016.
 */

public class ScheduledKHLInfo extends TimerTask {

    private static final Logger LOGGER = Logger.getLogger(ScheduledKHLInfo.class.getSimpleName());
    private static boolean stopCheckingInfoAfterNight = false;
    private static boolean stopCheckingStandings = false;
    public static boolean stopCheckingInfoAfterAllGamesFinished = false;

    @Override
    public void run() {
        try {
            Calendar cal = Calendar.getInstance();
            LocalTime currentTime = LocalTime.of(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));

            String textKHLInfo;
            String textStandingInfo;

            if (currentTime.equals(Constants.START_TIME)
                    || (currentTime.isAfter(Constants.START_TIME)
                    && currentTime.isBefore(Constants.END_TIME))
                    && !stopCheckingInfoAfterAllGamesFinished) {
                LOGGER.info("currentTimeForKHLInfo = " + currentTime);
                textKHLInfo = Connection.getInfoForChannel(Constants.URL_KHL_INFO, false);
                if (!textKHLInfo.isEmpty()) {
                    new KHLBot().sendMessage(new SendMessage().setChatId("@KHL_Info").setText(textKHLInfo));
                }
                stopCheckingInfoAfterNight = false;
                stopCheckingStandings = false;
            } else if (currentTime.isAfter(LocalTime.of(0, 0, 0))
                    && currentTime.isBefore(LocalTime.of(0, 1, 0))
                    && !stopCheckingInfoAfterNight) {
                LOGGER.info("currentTimeForKHLInfo = " + currentTime);
                textKHLInfo = Connection.getInfoForChannel(Constants.URL_KHL_INFO, true);
                if (!textKHLInfo.isEmpty()) {
                    new KHLBot().sendMessage(new SendMessage().setChatId("@KHL_Info").setText(textKHLInfo));
                }
                stopCheckingInfoAfterNight = true;
                stopCheckingInfoAfterAllGamesFinished = false;
                LOGGER.info("Waiting...");
            } else if (currentTime.isAfter(LocalTime.of(0, 10, 0))
                    && currentTime.isBefore(LocalTime.of(0, 11, 0))
                    && !stopCheckingStandings) {
                LOGGER.info("currentTimeForKHLInfo = " + currentTime);
                textStandingInfo = Connection.getStandingsInfo(Constants.URL_STANDINGS);
                if (!textStandingInfo.isEmpty()) {
//                    new KHLBot().sendMessage(new SendMessage().setChatId("@KHL_Info").setText(textStandingInfo));
                }
                stopCheckingStandings = true;
                LOGGER.info("Waiting...");
            }
        } catch (TelegramApiException | IOException | JSONException e) {
            LOGGER.info(Constants.UNEXPECTED_ERROR.concat(e.getMessage() + e));
        } catch (Exception ex) {
            LOGGER.info("EXCEPTION: " + ex.getMessage() + ex);
        }
    }
}