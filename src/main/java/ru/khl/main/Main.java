package ru.khl.main;

import org.apache.log4j.Logger;
import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.TelegramBotsApi;
import ru.khl.bot.KHLBot;
import ru.khl.bot.ScheduledTask;

import java.util.Timer;

/**
 * Created by alexey on 01.11.16.
 */
public class Main {
    private static final Logger LOGGER = Logger.getLogger(Main.class.getSimpleName());

    public static void main(String[] args) {

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
            ScheduledTask scheduledTask = new ScheduledTask();
            time.schedule(scheduledTask, 0, 3_600_000);

        } catch (TelegramApiException e) {
            LOGGER.error(e.getMessage());
        }
    }
}
