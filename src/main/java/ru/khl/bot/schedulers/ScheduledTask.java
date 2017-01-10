package ru.khl.bot.schedulers;

import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import ru.khl.bot.KHLBot;
import ru.khl.bot.constants.Constants;

import java.io.IOException;
import java.time.LocalTime;
import java.util.Calendar;
import java.util.TimerTask;

/**
 * Created by Alexey on 13.12.2016.
 */

public class ScheduledTask extends TimerTask {

    @Override
    public void run() {
        try {

            Calendar cal = Calendar.getInstance();

            LocalTime currentTime = LocalTime.of(cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), cal.get(Calendar.SECOND));
            System.out.println(currentTime);

            LocalTime startTime = LocalTime.of(11, 30, 0);
            LocalTime endTime = LocalTime.of(23, 59, 59);

            if (currentTime.isAfter(startTime) || currentTime.equals(startTime) && currentTime.isBefore(endTime)) {
                new KHLBot().sendMessage(new SendMessage().setChatId("@KHL_Info").setText(ru.khl.utils.Connection.getInfoForChanel(Constants.URL_KHL_INFO)));
            } else if (currentTime.isBefore(LocalTime.of(0, 40, 0))) {
                new KHLBot().sendMessage(new SendMessage().setChatId("@KHL_Info").setText(ru.khl.utils.Connection.getStandingsInfo(Constants.URL_STANDINGS)));
            } else if (currentTime.isAfter(LocalTime.of(0, 30, 0)) && currentTime.isBefore(LocalTime.of(11, 30, 0))) {
                System.out.println("Waiting....");
            }
        } catch (TelegramApiException | IOException e) {
            e.printStackTrace();
        }
    }
}