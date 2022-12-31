package ru.khl.bot.bean.scheduler.video;

import common.vk.model.Item;
import common.vk.model.MessageStructure;
import common.vk.model.WallItem;
import common.vk.utils.RedisEntity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.khl.bot.KHLBot;
import ru.khl.bot.utils.BotHelper;

import javax.ejb.Singleton;
import javax.enterprise.inject.Default;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Alexey on 13.12.2016.
 */
@Default
@Singleton
public class VideoScheduler implements IVideoScheduler {

    private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());
    private final String chatId = RedisEntity.getInstance().getElement("khl_chatId");
    public final String url = "https://www.khl.ru";

    @Override
    public void sendVideo() {
        logger.log(Level.SEVERE, "Start KHLVideoScheduler...");

        try {
            MessageStructure messageStructure = this.getVideoFromKHLPortal();

            if (messageStructure == null
                    || messageStructure.getWallItems() == null
                    || messageStructure.getWallItems().isEmpty())
                return;

            for (WallItem wallItem : messageStructure.getWallItems()) {

                if (wallItem.getItemList() == null
                        || wallItem.getItemList().isEmpty())
                    continue;

                for (Item item : wallItem.getItemList()) {
                    if (item.getLink() == null || item.getLink().isEmpty()) {
                        this.logger.log(Level.SEVERE, "KHL video url is null or is empty");
                        continue;
                    }
                    this.logger.log(Level.INFO, "KHL video url - {0}", item.getLink());
                    new KHLBot().execute(SendMessage.builder()
                            .chatId(chatId)
                            .text(item.getLink())
                            .build());
                }
            }

        } catch (Exception ex) {
            this.logger.log(Level.SEVERE, "KHLVideoScheduler exception: ", ex);
        }
    }

    private MessageStructure getVideoFromKHLPortal() throws IOException {

        int responseCode = BotHelper.getResponseCode(url);
        if (responseCode != 200) {
            logger.log(Level.SEVERE, "Response code = {0}", responseCode);
            return null;
        }

        WallItem wallItem = new WallItem();
        Document doc = Jsoup.connect(url).get();

        // Get all videos from main page
        Elements videoElements = doc.select("div > div.video-items > div > a[href]");
        if (videoElements != null) {
            videoElements.forEach(video -> {
                Item item = new Item();
                item.setLink(video.attr("abs:href"));
                item.setPostType(Item.PostType.VIDEO);
                RedisEntity.getInstance().checkRedisStore("KHL", item, wallItem);
            });
        }

        MessageStructure messageStructure = new MessageStructure();
        messageStructure.getWallItems().add(wallItem);

        return messageStructure;
    }
}