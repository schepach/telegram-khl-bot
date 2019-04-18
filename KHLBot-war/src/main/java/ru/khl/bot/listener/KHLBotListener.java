package ru.khl.bot.listener;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.generics.BotSession;
import ru.khl.bot.KHLBot;
import ru.khl.bot.constants.Constants;
import ru.khl.bot.schedulers.ScheduledKHLNews;
import ru.khl.bot.schedulers.ScheduledKHLPhoto;
import ru.khl.bot.schedulers.ScheduledKHLVideo;
import ru.khl.bot.schedulers.ScheduledVKInfo;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;
import javax.servlet.annotation.WebListener;
import java.util.Timer;


/**
 * Created by alexey on 01.11.16.
 */

@WebListener
public class KHLBotListener implements ServletContextListener {

    private static final Logger LOGGER = Logger.getLogger(KHLBotListener.class.getSimpleName());

    private BotSession botSession;
    private Timer time;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        LOGGER.info("ContextInitialized: botSession start....");
        ApiContextInitializer.init();
        LOGGER.info("Initialization BotsApi....");
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();

        try {
            LOGGER.info("OK!");
            LOGGER.info("Register KHLBot....");
            botSession = telegramBotsApi.registerBot(new KHLBot());
            LOGGER.info("Register done.");
            LOGGER.info("Start KHLBot...");

            time = new Timer();

            ScheduledVKInfo scheduledKHLInfo = new ScheduledVKInfo();
            time.schedule(scheduledKHLInfo, 0, 300_000); //5 min

            ScheduledKHLNews scheduledKHLNews = new ScheduledKHLNews();
            time.schedule(scheduledKHLNews, 0, 300_000); //5 min

            ScheduledKHLPhoto scheduledKHLPhoto = new ScheduledKHLPhoto();
            time.schedule(scheduledKHLPhoto, 0, 1_800_000); // 30 min

            ScheduledKHLVideo scheduledKHLVideo = new ScheduledKHLVideo();
            time.schedule(scheduledKHLVideo, 0, 1_800_000); // 30 min

        } catch (TelegramApiException | JSONException e) {
            LOGGER.error(Constants.UNEXPECTED_ERROR.concat(e.getMessage() + e));
        } catch (Exception ex) {
            LOGGER.error("EXCEPTION: " + ex.getMessage() + ex);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        try {
            LOGGER.info("ContextDestroyed: botSession stop....");
            botSession.stop();
            time.cancel();
        } catch (Exception ex) {
            LOGGER.error("EXCEPTION: " + ex.getMessage() + ex);
        }
    }
}
