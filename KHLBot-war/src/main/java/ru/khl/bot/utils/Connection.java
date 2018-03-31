package ru.khl.bot.utils;

import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.ServiceActor;
import com.vk.api.sdk.exceptions.ApiException;
import com.vk.api.sdk.exceptions.ClientException;
import com.vk.api.sdk.httpclient.HttpTransportClient;
import com.vk.api.sdk.objects.wall.WallPost;
import com.vk.api.sdk.objects.wall.WallPostFull;
import com.vk.api.sdk.objects.wall.WallpostAttachment;
import com.vk.api.sdk.objects.wall.responses.GetResponse;
import com.vk.api.sdk.queries.wall.WallGetFilter;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import redis.clients.jedis.Jedis;
import ru.khl.bot.model.Item;
import ru.khl.bot.model.MessageStructure;
import ru.khl.bot.model.WallItem;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by alexey on 01.11.16.
 */

public class Connection {

    private static final int OWNER_ID = -1;
    private static final Logger LOGGER = Logger.getLogger(Connection.class.getSimpleName());
    private static final Jedis REDIS_STORE = new Jedis("localhost", 6379);

    public static MessageStructure getInfo() {


        VkApiClient vk = new VkApiClient(new HttpTransportClient());

        MessageStructure messageStructure = new MessageStructure();
        messageStructure.setWallItems(new ArrayList<WallItem>());
        try {

            ServiceActor actor = new ServiceActor(0, "");

            GetResponse getResponse = vk.wall().get(actor)
                    .ownerId(OWNER_ID)
                    .count(20)
//                    .offset(15) //смещение
                    .filter(WallGetFilter.ALL)
                    .execute();

//            LOGGER.info("RESPONSE = " + getResponse);

            for (WallPostFull postFull : getResponse.getItems()) {

                WallItem wallItem = new WallItem();
                wallItem.setItemList(new ArrayList<Item>());

                if (postFull.getCopyHistory() != null) {
                    checkRepost(postFull, wallItem);
                } else {
                    checkUsualPost(postFull, wallItem);
                }
                messageStructure.getWallItems().add(wallItem);
            }
            return messageStructure;
        } catch (ApiException | ClientException ex) {
            LOGGER.info("EXCEPTION: ", ex);
        }
        return null;
    }

    private static void checkRedisStore(Item item, WallItem wallItem) {
        if (item.getLink() != null && !item.getLink().isEmpty()) {
            String link = item.getLink();

            if (item.getPostType() == Item.PostType.KHL_PHOTO) {
                link = link.substring(link.lastIndexOf("/") + 1, link.length());
            }

            List<String> redisList = REDIS_STORE.lrange(item.getPostType().value(), 0, REDIS_STORE.dbSize());
            boolean isContains = false;

            if (redisList != null && !redisList.isEmpty()) {

                for (String element : redisList) {
                    if (link.equals(element)) {
                        isContains = true;
                        break;
                    }
                }

                if (!isContains) {
                    LOGGER.info("Element type of " + item.getPostType().value() + " does not exist in redis...put it");
                    REDIS_STORE.rpush(item.getPostType().value(), link);
                    wallItem.getItemList().add(item);
                } else {
                    LOGGER.info("Element type of " + item.getPostType().value() + " already exist in redis...go on");
                    wallItem.getItemList().remove(item);
                }
            } else {
                LOGGER.info("Redis list type of " + item.getPostType().value() + " is empty, put first element");
                REDIS_STORE.lpush(item.getPostType().value(), link);
                wallItem.getItemList().add(item);
            }
        }
    }

    private static void checkRedisStoreOnlyTextType(Item item, WallPostFull postFull, WallItem wallItem) {
        if (!REDIS_STORE.exists(item.getTitle() + postFull.getId())) {
            LOGGER.info("Post with only text does not exist in redis...put it");
            REDIS_STORE.set(item.getTitle() + postFull.getId(), "");
            wallItem.getItemList().add(item);
        } else {
            LOGGER.info("Post with only text already exist in redis...go on");
        }
    }

    private static void checkUsualPost(WallPostFull postFull, WallItem wallItem) {
        if ((postFull.getText() != null && !postFull.getText().isEmpty())
                && postFull.getAttachments() == null) {
            Item item = new Item();
            item.setTitle(postFull.getText());
            item.setPostType(Item.PostType.KHL_ONLY_TEXT);
            checkRedisStoreOnlyTextType(item, postFull, wallItem);
        }

        if (postFull.getAttachments() != null && !postFull.getAttachments().isEmpty()) {
            //Post with one attachment
            if (postFull.getAttachments().size() == 1
                    && postFull.getAttachments().get(0) != null) {
                LOGGER.info("Post with one attachment...");
                Item item = new Item();
                item.setTitle(postFull.getText() != null && !postFull.getText().isEmpty() ? postFull.getText() : "");
                Utils.checkAttachmentTypeAndMapping(item, postFull.getAttachments().get(0));
                checkRedisStore(item, wallItem);
            }
            //Post with a few attachments
            else {
                mapPostWithAFewAttachments(postFull, wallItem);
            }
        }
    }

    private static void mapPostWithAFewAttachments(WallPost post, WallItem wallItem) {
        boolean emptyTitle = true;
        for (WallpostAttachment attachment : post.getAttachments()) {
            Item item = new Item();
            if (emptyTitle) {
                item.setTitle(post.getText() != null && !post.getText().isEmpty() ? post.getText() : "");
                emptyTitle = false;
            }
            Utils.checkAttachmentTypeAndMapping(item, attachment);
            checkRedisStore(item, wallItem);
        }
    }

    private static void checkRepost(WallPostFull postFull, WallItem wallItem) {
        for (WallPost repost : postFull.getCopyHistory()) {
            if ((repost.getText() != null && !repost.getText().isEmpty())
                    && repost.getAttachments() == null) {
                Item item = new Item();
                item.setTitle(postFull.getText().concat("\n").concat(repost.getText()));
                item.setPostType(Item.PostType.KHL_ONLY_TEXT);
                checkRedisStoreOnlyTextType(item, postFull, wallItem);
            }
            if (repost.getAttachments() != null && !repost.getAttachments().isEmpty()) {
                //Post with one attachment
                if (repost.getAttachments().size() == 1
                        && repost.getAttachments().get(0) != null) {
                    LOGGER.info("Post with one attachment...");
                    Item item = new Item();
                    item.setTitle(postFull.getText() != null && !postFull.getText().isEmpty() ? postFull.getText() : "".concat("\n").concat(repost.getText()));
                    Utils.checkAttachmentTypeAndMapping(item, repost.getAttachments().get(0));
                    checkRedisStore(item, wallItem);
                }
                //Post with a few attachments
                else {
                    mapPostWithAFewAttachments(repost, wallItem);
                }
            }
        }
    }

    public static MessageStructure getKHLNews(String url) throws IOException {

        if (BotHelper.getResponseCode(url) != 200) {
            LOGGER.info("ResponseCode != 200....");
            return null;
        }

        MessageStructure messageStructure = new MessageStructure();
        messageStructure.setWallItems(new ArrayList<WallItem>());
        WallItem wallItem = new WallItem();
        wallItem.setItemList(new ArrayList<Item>());

        Document doc = Jsoup.connect(url).get();
        Elements elements = doc.getElementsByAttributeValue("class", "b-content_section m-teaser").first().getAllElements();
        String newsUrl;

        for (Element elem : elements) {

            if (elem.attr("class").equals("b-middle_block")) {

                newsUrl = elem.select("a").first().attr("abs:href");
                Item item = new Item();
                item.setLink(newsUrl);
                item.setPostType(Item.PostType.KHL_LINK);
                checkRedisStore(item, wallItem);
                messageStructure.getWallItems().add(wallItem);
                return messageStructure;
            }
        }
        return null;
    }

    public static MessageStructure getPhotoToday(String url) throws IOException {

        if (BotHelper.getResponseCode(url) != 200) {
            LOGGER.info("ResponseCode != 200....");
            return null;
        }

        MessageStructure messageStructure = new MessageStructure();
        messageStructure.setWallItems(new ArrayList<WallItem>());
        WallItem wallItem = new WallItem();
        wallItem.setItemList(new ArrayList<Item>());

        Document doc = Jsoup.connect(url).get();
        Elements elements = doc.getElementsByAttributeValue("id", "tab-photo-photoday").select("ul").select("li").select("a").first().getAllElements();
        String photoUrl;

        for (Element elem : elements) {

            photoUrl = elem.attr("style");

            if (photoUrl != null && !photoUrl.isEmpty()) {

                String urlJPG = "https://".concat(photoUrl.substring(photoUrl.lastIndexOf("//") + 2, photoUrl.lastIndexOf(")")));

                Item item = new Item();
                item.setCaption("ФОТО ДНЯ");
                item.setLink(urlJPG);
                item.setPostType(Item.PostType.KHL_PHOTO);
                checkRedisStore(item, wallItem);
                messageStructure.getWallItems().add(wallItem);
                return messageStructure;
            }
        }
        return null;
    }


    public static MessageStructure getVideo(String url) throws IOException {

        if (BotHelper.getResponseCode(url) != 200) {
            LOGGER.info("ResponseCode != 200....");
            return null;
        }

        MessageStructure messageStructure = new MessageStructure();
        messageStructure.setWallItems(new ArrayList<WallItem>());
        WallItem wallItem = new WallItem();
        wallItem.setItemList(new ArrayList<Item>());

        Document doc = Jsoup.connect(url).get();
        Elements elements = doc.getElementsByAttributeValue("id", "tab-video-new").first().getAllElements();
        String videoUrl;

        for (Element elem : elements) {

            if (elem.attr("class").equals("b-middle_block")) {
                Item item = new Item();
                videoUrl = elem.select("a").first().attr("abs:href");
                item.setLink(videoUrl);
                item.setPostType(Item.PostType.KHL_VIDEO);
                checkRedisStore(item, wallItem);
            }

            if (elem.attr("class").equals("b-short_block")) {
                for (Element current : elem.getAllElements().select("div")) {
                    Item item = new Item();
                    videoUrl = current.select("a").attr("abs:href");
                    item.setLink(videoUrl);
                    item.setPostType(Item.PostType.KHL_VIDEO);
                    checkRedisStore(item, wallItem);
                }
            }
        }

        messageStructure.getWallItems().add(wallItem);
        return messageStructure;
    }

    static String getInfoForHockeyClub(String url) throws IOException {

        if (BotHelper.getResponseCode(url) != 200) {
            LOGGER.info("ResponseCode != 200....");
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
        LOGGER.info("INFO_ABOUT_COMMAND" + stringBuilder.toString());

        return stringBuilder.toString();
    }
}
