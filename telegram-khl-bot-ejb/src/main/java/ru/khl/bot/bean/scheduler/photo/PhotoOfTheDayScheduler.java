package ru.khl.bot.bean.scheduler.photo;

import common.vk.model.Item;
import common.vk.model.MessageStructure;
import common.vk.model.WallItem;
import common.vk.utils.RedisEntity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import ru.khl.bot.KHLBot;
import ru.khl.bot.utils.BotHelper;

import javax.ejb.Singleton;
import javax.enterprise.inject.Default;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Alexey on 13.12.2016.
 */
@Default
@Singleton
public class PhotoOfTheDayScheduler implements IPhotoScheduler {

    private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());
    private final String chatId = RedisEntity.getInstance().getElement("khl_chatId");

    @Override
    public void sendPhotoOfDay() {
        logger.log(Level.SEVERE, "Start KHLPhotoOfTheDayScheduler...");

        try {
            MessageStructure messageStructure = this.getPhotoFromKHLPortal();

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

                    new KHLBot().execute(SendPhoto.builder()
                            .chatId(chatId)
                            .caption(item.getCaption())
                            .photo(inputFile)
                            .build());
                }
            }

        } catch (Exception ex) {
            this.logger.log(Level.SEVERE, "KHLPhotoOfTheDayScheduler exception: ", ex);
        }
    }

    private MessageStructure getPhotoFromKHLPortal() throws IOException {

        int responseCode = BotHelper.getResponseCode("https://photo.khl.ru/");
        if (responseCode != 200) {
            logger.log(Level.SEVERE, "Response code = {0}", responseCode);
            return null;
        }

        Document doc = Jsoup.connect("https://photo.khl.ru/").get();
        Element photoNameElement = doc.select("div.page_wrapper.us-non1e div.page_content a").first();
        Element photoElement = doc.select("img[src$=.jpg]").first();

        if (photoElement == null)
            return null;

        String photoUrl = photoElement.absUrl("src");
        String photoName = (photoNameElement != null && !photoNameElement.text().isEmpty() ? photoNameElement.text() : "ФОТО ДНЯ");
        logger.log(Level.INFO, "Name photo of day - {0}, url - {1}", new Object[]{photoName, photoUrl});

        if (photoUrl != null && !photoUrl.isEmpty()) {
            MessageStructure messageStructure = new MessageStructure();
            WallItem wallItem = new WallItem();
            Item item = new Item();
            item.setCaption(photoName);
            item.setLink(photoUrl);
            item.setPostType(Item.PostType.PHOTO);
            RedisEntity.getInstance().checkRedisStore("KHL", item, wallItem);
            messageStructure.getWallItems().add(wallItem);
            return messageStructure;
        }
        return null;
    }
}