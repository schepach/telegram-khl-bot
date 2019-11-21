package ru.khl.bot.listener;

import org.apache.log4j.Logger;
import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.generics.BotSession;
import ru.khl.bot.KHLBot;
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

    private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    private BotSession botSession;
    private Timer time;

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        logger.info("ContextInitialized: botSession start....");
        ApiContextInitializer.init();
        logger.info("Initialization BotsApi....");
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();

        try {
            logger.info("OK!");
            logger.info("Register KHLBot....");
            botSession = telegramBotsApi.registerBot(new KHLBot());
            logger.info("Register done.");
            logger.info("Start KHLBot...");

            time = new Timer();

            ScheduledVKInfo scheduledKHLInfo = new ScheduledVKInfo();
            time.schedule(scheduledKHLInfo, 0, 300_000); //5 min

            ScheduledKHLNews scheduledKHLNews = new ScheduledKHLNews();
            time.schedule(scheduledKHLNews, 0, 300_000); //5 min

            ScheduledKHLPhoto scheduledKHLPhoto = new ScheduledKHLPhoto();
            time.schedule(scheduledKHLPhoto, 0, 1_800_000); // 30 min

            ScheduledKHLVideo scheduledKHLVideo = new ScheduledKHLVideo();
            time.schedule(scheduledKHLVideo, 0, 1_800_000); // 30 min

        } catch (Exception ex) {
            logger.error("ContextInitialized exception: ", ex);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        try {
            logger.info("ContextDestroyed: botSession stop....");
            botSession.stop();
            time.cancel();
        } catch (Exception ex) {
            logger.error("ContextDestroyed exception: ", ex);
        }
    }
}
