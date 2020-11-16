package ru.khl.bot.utils;

import common.vk.model.Item;
import common.vk.model.MessageStructure;
import common.vk.model.WallItem;
import common.vk.utils.RedisEntity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.khl.bot.constants.Constants;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by alexey on 01.11.16.
 */

public class Connection {

    private static final Logger LOGGER = Logger.getLogger(Connection.class.getSimpleName());
    private static final String ALIAS = "KHL";

    public static MessageStructure getKHLNews(String url) throws IOException {

        int responseCode = BotHelper.getResponseCode(url);
        if (responseCode != 200) {
            LOGGER.log(Level.SEVERE, "Response code = {0}", responseCode);
            return null;
        }

        Document doc = Jsoup.connect(url).get();
        WallItem wallItem = new WallItem();
        String newsUrl;
        Item item;

        // Get fresh news
        Element newsElement = doc.select("div.b-content_section.m-teaser a[href]").first();
        if (newsElement != null) {
            newsUrl = newsElement.absUrl("abs:href");
            if (newsUrl != null && !newsUrl.isEmpty()) {
                item = new Item();
                item.setLink(newsUrl);
                item.setPostType(Item.PostType.LINK);
                RedisEntity.getInstance().checkRedisStore(ALIAS, item, wallItem);
            }
        }

        // Get old news
        Elements oldNewsElements = doc.select("div.b-content_section.m-video.s-float_panel_start div.b-news_bnr_item");
        for (Element current : oldNewsElements) {
            newsElement = current.select("a").first();
            if (newsElement == null)
                continue;

            newsUrl = newsElement.absUrl("abs:href");
            item = new Item();
            item.setLink(newsUrl);
            item.setPostType(Item.PostType.LINK);
            RedisEntity.getInstance().checkRedisStore(ALIAS, item, wallItem);
        }

        MessageStructure messageStructure = new MessageStructure();
        messageStructure.getWallItems().add(wallItem);

        return messageStructure;
    }

    public static MessageStructure getPhotoToday() throws IOException {

        int responseCode = BotHelper.getResponseCode(Constants.URL_PHOTO_OF_DAY);
        if (responseCode != 200) {
            LOGGER.log(Level.SEVERE, "Response code = {0}", responseCode);
            return null;
        }

        Document doc = Jsoup.connect(Constants.URL_PHOTO_OF_DAY).get();
        Element photoNameElement = doc.select("div.page_wrapper.us-non1e div.page_content a").first();
        Element photoElement = doc.select("img[src$=.jpg]").first();

        if (photoElement == null)
            return null;

        String photoUrl = photoElement.absUrl("src");
        String photoName = (photoNameElement != null && !photoNameElement.text().isEmpty() ? photoNameElement.text() : "ФОТО ДНЯ");
        LOGGER.log(Level.INFO, "Name photo of day = {0}, url = {1}", new Object[]{photoName, photoUrl});

        if (photoUrl != null && !photoUrl.isEmpty()) {
            MessageStructure messageStructure = new MessageStructure();
            WallItem wallItem = new WallItem();
            Item item = new Item();
            item.setCaption(photoName);
            item.setLink(photoUrl);
            item.setPostType(Item.PostType.PHOTO);
            RedisEntity.getInstance().checkRedisStore(ALIAS, item, wallItem);
            messageStructure.getWallItems().add(wallItem);
            return messageStructure;
        }
        return null;
    }


    public static MessageStructure getVideo(String url) throws IOException {

        int responseCode = BotHelper.getResponseCode(url);
        if (responseCode != 200) {
            LOGGER.log(Level.SEVERE, "Response code = {0}", responseCode);
            return null;
        }

        WallItem wallItem = new WallItem();
        String videoUrl;
        Item item;
        Document doc = Jsoup.connect(url).get();

        // middle block (new video)
        Element videoElement = doc.select("div.tab-video div.b-middle_block a[href]").first();
        if (videoElement != null) {
            videoUrl = videoElement.absUrl("abs:href");
            if (videoUrl != null && !videoUrl.isEmpty()) {
                item = new Item();
                item.setLink(videoUrl);
                item.setPostType(Item.PostType.VIDEO);
                RedisEntity.getInstance().checkRedisStore(ALIAS, item, wallItem);
            }
        }

        // short block (old videos)
        Elements videoElementsOfShortBlock = doc.select("div.tab-video div.b-short_block div.b-short_block_cover");
        for (Element videoItem : videoElementsOfShortBlock) {
            videoElement = videoItem.select("a").first();
            if (videoElement == null)
                continue;

            videoUrl = videoElement.absUrl("abs:href");
            item = new Item();
            item.setLink(videoUrl);
            item.setPostType(Item.PostType.VIDEO);
            RedisEntity.getInstance().checkRedisStore(ALIAS, item, wallItem);
        }

        MessageStructure messageStructure = new MessageStructure();
        messageStructure.getWallItems().add(wallItem);

        return messageStructure;
    }

    static String getInfoForHockeyClub(String url) throws IOException {

        int responseCode = BotHelper.getResponseCode(url);
        if (responseCode != 200) {
            LOGGER.log(Level.SEVERE, "Response code = {0}", responseCode);
            return null;
        }

        Document doc = Jsoup.connect(url).get();
        Elements elements = doc.select("dl");

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Подписывайтесь на канал KHL Info https://t.me/KHL_Info - там Вас ждет много интересного!").append("\n");

        String club1;
        String club2;
        String when;
        String totalScore;
        String details;

        for (Element item : elements) {

            if (item.attr("class").equals("b-details m-club")) {
                club1 = item.select("dd").select("h5").text();
                if (!club1.isEmpty()) {
                    stringBuilder.append("\n").append("Команда: ".concat(club1)).append(" (").append(item.select("dd").select("p").first().text()).append(")").append("\n");
                }
            }

            if (item.attr("class").equals("b-score")) {
                when = item.select("dt").select("b").text();
                if (!when.isEmpty()) {
                    stringBuilder.append("Когда: ".concat(when)).append("\n");
                }
                totalScore = item.select("dt").select("h3").text();
                if (!totalScore.isEmpty()) {
                    stringBuilder.append("Результат: ".concat(totalScore)).append("\n");
                }
                details = item.select("dd").select("ul").text();
                if (!details.isEmpty()) {
                    stringBuilder.append("Подробно: ".concat(details)).append("\n");
                }
            }

            if (item.attr("class").equals("b-details m-club m-rightward")) {
                club2 = item.select("dd").select("h5").text();
                if (!club2.isEmpty()) {
                    stringBuilder.append("Против: ".concat(club2)).append(" (").append(item.select("dd").select("p").first().text()).append(")").append("\n-------\uD83C\uDFD2\uD83C\uDFC6\uD83D\uDCAA\uD83C\uDFFB-------\n");
                }
            }

        }
        LOGGER.log(Level.INFO, "INFO_ABOUT_COMMAND" + stringBuilder.toString());

        return stringBuilder.toString();
    }
}
