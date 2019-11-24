package ru.khl.bot.listener;

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
import java.util.logging.Level;
import java.util.logging.Logger;


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
        this.logger.log(Level.SEVERE, "ContextInitialized: botSession start....");
        ApiContextInitializer.init();
        this.logger.log(Level.SEVERE, "Initialization BotsApi....");
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();

        try {
            this.logger.log(Level.SEVERE, "OK!");
            this.logger.log(Level.SEVERE, "Register KHLBot....");
            botSession = telegramBotsApi.registerBot(new KHLBot());
            this.logger.log(Level.SEVERE, "Register done.");
            this.logger.log(Level.SEVERE, "Start KHLBot...");

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
            this.logger.log(Level.SEVERE, "ContextInitialized exception: ", ex);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        try {
            this.logger.log(Level.SEVERE, "ContextDestroyed: botSession stop....");
            botSession.stop();
            time.cancel();
        } catch (Exception ex) {
            this.logger.log(Level.SEVERE, "ContextDestroyed exception: ", ex);
        }
    }
}
