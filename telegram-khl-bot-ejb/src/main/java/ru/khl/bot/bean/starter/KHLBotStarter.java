package ru.khl.bot.bean.starter;

import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.generics.BotSession;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;
import ru.khl.bot.KHLBot;
import ru.khl.bot.bean.game.Game;
import ru.khl.bot.bean.scheduler.news.INewsScheduler;
import ru.khl.bot.bean.scheduler.photo.IPhotoScheduler;
import ru.khl.bot.bean.scheduler.video.IVideoScheduler;
import ru.khl.bot.bean.scheduler.vk.IVKScheduler;

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
@AccessTimeout(value = 30, unit = TimeUnit.SECONDS)
public class KHLBotStarter {

    @Inject
    private INewsScheduler newsScheduler;
    @Inject
    private IPhotoScheduler photoScheduler;
    @Inject
    private IVideoScheduler videoScheduler;
    @Inject
    private IVKScheduler vkScheduler;
    @Inject
    private Game game;

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
            botSession = telegramBotsApi.registerBot(new KHLBot(game));
            this.logger.log(Level.SEVERE, "Register done.");
            this.logger.log(Level.SEVERE, "KHLBot was started...");

        } catch (Exception ex) {
            this.logger.log(Level.SEVERE, "ContextInitialized exception: ", ex);
        }
    }

    @Schedule(hour = "7-01")
    public void sendNews() {
        newsScheduler.sendKHLNews();
    }

    @Schedule(hour = "7-01")
    public void sendPhoto() {
        photoScheduler.sendPhotoOfDay();
    }

    @Schedule(hour = "7-01")
    public void sendVideo() {
        videoScheduler.sendVideo();
    }

    @Schedule(hour = "7-01", minute = "*/15")
    public void sendVKPosts() {
        vkScheduler.sendVKPosts();
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
