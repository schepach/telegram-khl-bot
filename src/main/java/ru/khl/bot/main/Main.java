package ru.khl.bot.main;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.TelegramBotsApi;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.khl.bot.KHLBot;
import ru.khl.bot.constants.Constants;
import ru.khl.bot.schedulers.ScheduledKHLInfo;
import ru.khl.bot.schedulers.ScheduledKHLNews;
import ru.khl.bot.schedulers.ScheduledKHLPhoto;
import ru.khl.bot.schedulers.ScheduledKHLVideo;

import java.util.Timer;

/**
 * Created by alexey on 01.11.16.
 */
public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getSimpleName());

    public static void main(String[] args) {
        ApiContextInitializer.init();
        System.out.println("Initialization BotsApi....");
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();

        try {
            System.out.println("OK!");
            System.out.println("Register KHLBot....");
            telegramBotsApi.registerBot(new KHLBot());
            System.out.println("Register done.");
            System.out.println("Start KHLBot...");
            LOGGER.info("Start KHLBot...");
            System.out.println("See your log...");
            Timer time = new Timer();

            ScheduledKHLInfo scheduledKHLInfo = new ScheduledKHLInfo();
            time.schedule(scheduledKHLInfo, 0, 900_000); // 15 min

            ScheduledKHLNews scheduledKHLNews = new ScheduledKHLNews();
            time.schedule(scheduledKHLNews, 0, 300_000); //5 min

            ScheduledKHLPhoto scheduledKHLPhoto = new ScheduledKHLPhoto();
            time.schedule(scheduledKHLPhoto, 0, 1_800_000); // 30 min

            ScheduledKHLVideo scheduledKHLVideo = new ScheduledKHLVideo();
            time.schedule(scheduledKHLVideo, 0, 1_800_000); // 30 min

        } catch (TelegramApiException | JSONException e) {
            LOGGER.info(Constants.UNEXPECTED_ERROR.concat(e.getMessage() + e));
        } catch (Exception ex) {
            LOGGER.info("EXCEPTION: " + ex.getMessage() + ex);
        }
    }
}
