package ru.khl.bot.bean.scheduler.news;

import common.vk.model.Item;
import common.vk.model.MessageStructure;
import common.vk.model.WallItem;
import common.vk.utils.RedisEntity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
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
public class NewsScheduler implements INewsScheduler {

    private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());
    private final String chatId = RedisEntity.getInstance().getElement("khl_chatId");
    public final String url = "https://www.khl.ru";

    @Override
    public void sendKHLNews() {
        logger.log(Level.SEVERE, "Start KHLNewsScheduler...");

        try {
            MessageStructure messageStructure = this.getNewsFromKHLPortal();

            if (messageStructure == null
                    || messageStructure.getWallItems() == null
                    || messageStructure.getWallItems().isEmpty())
                return;

            for (WallItem wallItem : messageStructure.getWallItems()) {

                if (wallItem.getItemList() == null || wallItem.getItemList().isEmpty())
                    continue;

                for (Item item : wallItem.getItemList()) {
                    if (item.getLink() == null || item.getLink().isEmpty()) {
                        this.logger.log(Level.SEVERE, "KHL news url is null or is empty");
                    }
                    this.logger.log(Level.INFO, "KHL news url - {0}", item.getLink());
                    new KHLBot().execute(SendMessage.builder()
                            .chatId(chatId)
                            .text(item.getLink())
                            .build());
                }
            }

        } catch (Exception ex) {
            this.logger.log(Level.SEVERE, "KHLNewsScheduler exception: ", ex);
        }
    }

    private MessageStructure getNewsFromKHLPortal() throws IOException {
        int responseCode = BotHelper.getResponseCode(url);
        if (responseCode != 200) {
            logger.log(Level.SEVERE, "Response code = {0}", responseCode);
            return null;
        }

        Document doc = Jsoup.connect(url).get();
        WallItem wallItem = new WallItem();
        String newsUrl;
        Item item;

        // Get fresh news
        Element newsElement = doc.select("div.b-content_section.m-teaser a[href]").first();
        if (newsElement != null) {
            newsUrl = newsElement.attr("abs:href");
            if (newsUrl != null && !newsUrl.isEmpty()) {
                item = new Item();
                item.setLink(newsUrl);
                item.setPostType(Item.PostType.LINK);
                RedisEntity.getInstance().checkRedisStore("KHL", item, wallItem);
            }
        }

        // Get old news
        Elements oldNewsElements = doc.select("div.b-content_section.m-video.s-float_panel_start div.b-news_bnr_item");
        for (Element newsElem : oldNewsElements) {
            newsElement = newsElem.select("a").first();
            if (newsElement == null)
                continue;

            newsUrl = newsElement.attr("abs:href");
            item = new Item();
            item.setLink(newsUrl);
            item.setPostType(Item.PostType.LINK);
            RedisEntity.getInstance().checkRedisStore("KHL", item, wallItem);
        }

        MessageStructure messageStructure = new MessageStructure();
        messageStructure.getWallItems().add(wallItem);

        return messageStructure;
    }
}