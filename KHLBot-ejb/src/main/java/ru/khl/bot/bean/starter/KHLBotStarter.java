package ru.khl.bot.bean.starter;

import org.telegram.telegrambots.ApiContextInitializer;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.generics.BotSession;
import ru.khl.bot.KHLBot;
import ru.khl.bot.bean.scheduler.NewsScheduler;
import ru.khl.bot.bean.scheduler.PhotoOfTheDayScheduler;
import ru.khl.bot.bean.scheduler.VKInfoScheduler;
import ru.khl.bot.bean.scheduler.VideoScheduler;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Created by alexey on 01.11.16.
 */

@Singleton
@Startup
public class KHLBotStarter {

    @Inject
    NewsScheduler newsScheduler;
    @Inject
    PhotoOfTheDayScheduler photoOfTheDayScheduler;
    @Inject
    VideoScheduler videoScheduler;
    @Inject
    VKInfoScheduler vkInfoScheduler;

    private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());
    private BotSession botSession;

    @PostConstruct
    public void init() {
        logger.log(Level.SEVERE, "ApiContextInitializer...");
        ApiContextInitializer.init();
        this.logger.log(Level.SEVERE, "Initialization BotsApi....");
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi();

        try {
            this.logger.log(Level.SEVERE, "OK!");
            this.logger.log(Level.SEVERE, "Register KHLBot....");
            botSession = telegramBotsApi.registerBot(new KHLBot());
            this.logger.log(Level.SEVERE, "Register done.");
            this.logger.log(Level.SEVERE, "Start KHLBot...");

        } catch (Exception ex) {
            this.logger.log(Level.SEVERE, "ContextInitialized exception: ", ex);
        }
    }

    @Schedule(hour = "*")
    public void getNews() {
        newsScheduler.run();
    }

    @Schedule(hour = "*")
    public void getPhoto() {
        photoOfTheDayScheduler.run();
    }

    @Schedule(hour = "*")
    public void getVideo() {
        videoScheduler.run();
    }

    @Schedule(hour = "*", minute = "30")
    public void getVKInfo() {
        vkInfoScheduler.run();
    }

    @PreDestroy
    public void cleanup() {
        try {
            logger.log(Level.SEVERE, "Stop botSession...");
            botSession.stop();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Destroyed exception: ", ex);
        }
    }
}
