package ru.khl.bot.schedulers;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.telegram.telegrambots.api.methods.send.SendPhoto;
import org.telegram.telegrambots.exceptions.TelegramApiException;
import ru.khl.bot.KHLBot;
import ru.khl.bot.constants.Constants;
import ru.khl.bot.model.Item;
import ru.khl.bot.model.MessageStructure;
import ru.khl.bot.model.WallItem;
import ru.khl.bot.utils.Connection;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.TimerTask;

/**
 * Created by Alexey on 13.12.2016.
 */

public class ScheduledKHLPhoto extends TimerTask {

    private static final Logger LOGGER = Logger.getLogger(ScheduledKHLPhoto.class.getSimpleName());
    private static final String CHAT_ID = "@KHL_Info";

    @Override
    public void run() {
        try {
            MessageStructure messageStructure = Connection.getPhotoToday(Constants.URL_KHL_INFO);

            if (messageStructure != null && messageStructure.getWallItems() != null) {
                for (WallItem wallItem : messageStructure.getWallItems()) {
                    if (wallItem.getItemList() != null && !wallItem.getItemList().isEmpty()) {
                        for (Item item : wallItem.getItemList()) {
                            if (!item.getLink().isEmpty()) {
                                LOGGER.info("PHOTO_KHL URL = " + item.getLink());
                                URL urlOfPhoto = new URL(item.getLink());
                                InputStream streamOfPhoto = urlOfPhoto.openStream();
                                new KHLBot().sendPhoto(new SendPhoto().setCaption(item.getCaption()).setChatId(CHAT_ID).setNewPhoto("khlPhotoName", streamOfPhoto));
                            }
                        }
                    }
                }
            }
        } catch (TelegramApiException | IOException | JSONException ex) {
            LOGGER.info(Constants.UNEXPECTED_ERROR.concat(ex.getMessage() + ex), ex);
        } catch (Exception ex) {
            LOGGER.info("EXCEPTION: " + ex.getMessage(), ex);
        }
    }
}