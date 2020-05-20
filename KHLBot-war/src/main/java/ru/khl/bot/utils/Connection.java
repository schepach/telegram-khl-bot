package ru.khl.bot.utils;

import common.vk.model.Item;
import common.vk.model.MessageStructure;
import common.vk.model.WallItem;
import common.vk.utils.RedisEntity;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by alexey on 01.11.16.
 */

public class Connection {

    private static final Logger LOGGER = Logger.getLogger(Connection.class.getSimpleName());

    public static MessageStructure getKHLNews(String url) throws IOException {

        if (BotHelper.getResponseCode(url) != 200) {
            LOGGER.log(Level.INFO, "ResponseCode != 200....");
            return null;
        }

        MessageStructure messageStructure = new MessageStructure();
        WallItem wallItem = new WallItem();

        Document doc = Jsoup.connect(url).get();

        if (doc.getElementsByAttributeValue("class", "b-content_section m-teaser") == null
                || doc.getElementsByAttributeValue("class", "b-content_section m-teaser").first() == null
                || doc.getElementsByAttributeValue("class", "b-content_section m-teaser").first().getAllElements() == null)
            return null;

        Elements elements = doc.getElementsByAttributeValue("class", "b-content_section m-teaser").first().getAllElements();
        String newsUrl;

        for (Element elem : elements) {

            if (elem.attr("class").equals("b-middle_block")) {

                newsUrl = elem.select("a").first().attr("abs:href");
                Item item = new Item();
                item.setLink(newsUrl);
                item.setPostType(Item.PostType.LINK);
                RedisEntity.getInstance().checkRedisStore("KHL", item, wallItem);
                messageStructure.getWallItems().add(wallItem);
                return messageStructure;
            }
        }
        return null;
    }

    public static MessageStructure getPhotoToday(String url) throws IOException {

        if (BotHelper.getResponseCode(url) != 200) {
            LOGGER.log(Level.INFO, "ResponseCode != 200....");
            return null;
        }

        MessageStructure messageStructure = new MessageStructure();
        WallItem wallItem = new WallItem();

        Document doc = Jsoup.connect(url).get();

        if (doc.getElementsByAttributeValue("id", "tab-photo-photoday") == null
                || doc.getElementsByAttributeValue("id", "tab-photo-photoday").select("ul").select("li").select("a").first() == null
                || doc.getElementsByAttributeValue("id", "tab-photo-photoday").select("ul").select("li").select("a").first().getAllElements() == null)
            return null;

        Elements elements = doc.getElementsByAttributeValue("id", "tab-photo-photoday").select("ul").select("li").select("a").first().getAllElements();
        String photoUrl;

        for (Element elem : elements) {

            photoUrl = elem.attr("style");

            if (photoUrl != null && !photoUrl.isEmpty()) {

                String urlJPG = "https://".concat(photoUrl.substring(photoUrl.lastIndexOf("//") + 2, photoUrl.lastIndexOf(")")));

                Item item = new Item();
                item.setCaption("ФОТО ДНЯ");
                item.setLink(urlJPG);
                item.setPostType(Item.PostType.PHOTO);
                RedisEntity.getInstance().checkRedisStore("KHL", item, wallItem);
                messageStructure.getWallItems().add(wallItem);
                return messageStructure;
            }
        }
        return null;
    }


    public static MessageStructure getVideo(String url) throws IOException {

        if (BotHelper.getResponseCode(url) != 200) {
            LOGGER.log(Level.INFO, "ResponseCode != 200....");
            return null;
        }

        MessageStructure messageStructure = new MessageStructure();
        WallItem wallItem = new WallItem();

        Document doc = Jsoup.connect(url).get();

        if (doc.getElementsByAttributeValue("id", "tab-video-new") == null
                || doc.getElementsByAttributeValue("id", "tab-video-new").first() == null
                || doc.getElementsByAttributeValue("id", "tab-video-new").first().getAllElements() == null)
            return null;

        Elements elements = doc.getElementsByAttributeValue("id", "tab-video-new").first().getAllElements();
        String videoUrl;

        for (Element elem : elements) {

            if (elem.attr("class").equals("b-middle_block")) {
                Item item = new Item();
                videoUrl = elem.select("a").first().attr("abs:href");
                item.setLink(videoUrl);
                item.setPostType(Item.PostType.VIDEO);
                RedisEntity.getInstance().checkRedisStore("KHL", item, wallItem);
            }

            if (elem.attr("class").equals("b-short_block")) {
                for (Element current : elem.getAllElements().select("div")) {
                    Item item = new Item();
                    videoUrl = current.select("a").attr("abs:href");
                    item.setLink(videoUrl);
                    item.setPostType(Item.PostType.VIDEO);
                    RedisEntity.getInstance().checkRedisStore("KHL", item, wallItem);
                }
            }
        }

        messageStructure.getWallItems().add(wallItem);
        return messageStructure;
    }

    static String getInfoForHockeyClub(String url) throws IOException {

        if (BotHelper.getResponseCode(url) != 200) {
            LOGGER.log(Level.INFO, "ResponseCode != 200....");
            return "";
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
