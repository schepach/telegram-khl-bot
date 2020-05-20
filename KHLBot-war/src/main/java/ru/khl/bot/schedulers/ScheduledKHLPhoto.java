package ru.khl.bot.schedulers;

import common.vk.model.Item;
import common.vk.model.MessageStructure;
import common.vk.model.WallItem;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import ru.khl.bot.KHLBot;
import ru.khl.bot.constants.Constants;
import ru.khl.bot.utils.Connection;

import java.io.InputStream;
import java.net.URL;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Alexey on 13.12.2016.
 */

public class ScheduledKHLPhoto extends TimerTask {

    private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());
    private final String chatId = "@KHL_Info";

    @Override
    public void run() {
        try {
            MessageStructure messageStructure = Connection.getPhotoToday(Constants.URL_KHL_INFO);

            if (messageStructure == null || messageStructure.getWallItems() == null)
                return;

            for (WallItem wallItem : messageStructure.getWallItems()) {

                if (wallItem.getItemList() == null || wallItem.getItemList().isEmpty())
                    continue;

                for (Item item : wallItem.getItemList()) {
                    if (item == null || item.getLink() == null || item.getLink().isEmpty()) {
                        this.logger.log(Level.SEVERE, "KHL photo url is null or is empty");
                        continue;
                    }
                    this.logger.log(Level.INFO, "KHL photo url - {0}", new Object[]{item.getLink()});
                    URL urlOfPhoto = new URL(item.getLink());
                    InputStream streamOfPhoto = urlOfPhoto.openStream();
                    new KHLBot().execute(new SendPhoto().setCaption(item.getCaption()).setChatId(chatId).setPhoto("khlPhotoName", streamOfPhoto));
                }
            }

        } catch (Exception ex) {
            this.logger.log(Level.SEVERE, "ScheduledKHLPhoto exception: ", ex);
        }
    }
}