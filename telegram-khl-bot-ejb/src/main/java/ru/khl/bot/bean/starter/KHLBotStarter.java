package ru.khl.bot.bean.starter;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.khl.bot.KHLBot;
import ru.khl.bot.bean.scheduler.KHLNewsScheduler;
import ru.khl.bot.bean.scheduler.KHLPhotoOfTheDayScheduler;
import ru.khl.bot.bean.scheduler.KHLVKInfoScheduler;
import ru.khl.bot.bean.scheduler.KHLVideoScheduler;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.AccessTimeout;
import javax.ejb.Schedule;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.inject.Inject;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 * Created by alexey on 01.11.16.
 */

@Singleton
@Startup
@AccessTimeout(value = 1, unit = TimeUnit.MINUTES)
public class KHLBotStarter {

    @Inject
    private KHLNewsScheduler newsScheduler;
    @Inject
    private KHLPhotoOfTheDayScheduler photoOfTheDayScheduler;
    @Inject
    private KHLVideoScheduler videoScheduler;
    @Inject
    private KHLVKInfoScheduler vkInfoScheduler;

    private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());
    private BotSession botSession;

    @PostConstruct
    public void init() {
        try {
            logger.log(Level.SEVERE, "ApiContextInitializer...");
            this.logger.log(Level.SEVERE, "Initialization BotsApi....");
            TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);

            this.logger.log(Level.SEVERE, "OK!");
            this.logger.log(Level.SEVERE, "Register KHLBot....");
            botSession = telegramBotsApi.registerBot(new KHLBot());
            this.logger.log(Level.SEVERE, "Register done.");
            this.logger.log(Level.SEVERE, "KHLBot was started...");

        } catch (Exception ex) {
            this.logger.log(Level.SEVERE, "ContextInitialized exception: ", ex);
        }
    }

    @Schedule(hour = "7-01")
    public void getNews() {
        newsScheduler.run();
    }

    @Schedule(hour = "7-01")
    public void getPhoto() {
        photoOfTheDayScheduler.run();
    }

    @Schedule(hour = "7-01")
    public void getVideo() {
        videoScheduler.run();
    }

    @Schedule(hour = "7-01", minute = "*/15")
    public void getVKInfo() {
        vkInfoScheduler.run();
    }

    @PreDestroy
    public void cleanup() {
        try {
            logger.log(Level.SEVERE, "Stop botSession...");
            if (botSession == null) {
                logger.log(Level.SEVERE, "botSession is null... return...");
                return;
            }
            botSession.stop();
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Destroyed exception: ", ex);
        }
    }
}
