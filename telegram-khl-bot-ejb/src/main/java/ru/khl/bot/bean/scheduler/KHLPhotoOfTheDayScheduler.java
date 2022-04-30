package ru.khl.bot.bean.scheduler;

import common.vk.model.Item;
import common.vk.model.MessageStructure;
import common.vk.model.WallItem;
import common.vk.utils.RedisEntity;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import ru.khl.bot.KHLBot;
import ru.khl.bot.utils.Connection;

import javax.ejb.Stateless;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Alexey on 13.12.2016.
 */
@Stateless
public class KHLPhotoOfTheDayScheduler {

    private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());
    private final String chatId = RedisEntity.getInstance().getElement("khl_chatId");

    public void run() {
        logger.log(Level.SEVERE, "Start KHLPhotoOfTheDayScheduler...");

        try {
            MessageStructure messageStructure = Connection.getPhotoOfTheDay();

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
                    this.logger.log(Level.INFO, "KHL photo url - {0}", item.getLink());
                    URL urlOfPhoto = new URL(item.getLink());
                    InputStream streamOfPhoto = urlOfPhoto.openStream();
                    InputFile inputFile = new InputFile();
                    inputFile.setMedia(streamOfPhoto, "khlPhotoName.jpg");
                    SendPhoto sendPhoto = new SendPhoto();
                    sendPhoto.setCaption(item.getCaption());
                    sendPhoto.setChatId(chatId);
                    sendPhoto.setPhoto(inputFile);
                    new KHLBot().execute(sendPhoto);
                }
            }

        } catch (Exception ex) {
            this.logger.log(Level.SEVERE, "KHLPhotoOfTheDayScheduler exception: ", ex);
        }
    }
}