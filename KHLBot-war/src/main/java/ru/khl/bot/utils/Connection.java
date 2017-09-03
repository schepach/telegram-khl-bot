package ru.khl.bot.utils;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import redis.clients.jedis.Jedis;
import ru.khl.bot.schedulers.ScheduledKHLInfo;

import java.io.IOException;
import java.time.LocalTime;
import java.util.List;

/**
 * Created by alexey on 01.11.16.
 */

public class Connection {

    private static final Logger LOGGER = Logger.getLogger(Connection.class.getSimpleName());
    private static final Jedis REDIS_STORE = new Jedis("localhost", 1234);

    public static String getInfoForChannel(String url, boolean timeFlag) throws IOException {

        if (BotHelper.getResponseCode(url) != 200) {
            LOGGER.info("ResponseCode != 200....");
            return "";
        }

        Document doc = Jsoup.connect(url).get();

        Elements elements = doc.select("tr");

        StringBuilder stringBuilder = new StringBuilder();

        String when = "";
        String who = "";
        String how;
        int countTodayFinishedGames = 0;
        int countTodayGames = 0;


        for (Element item : elements) {

            boolean containFlag = false;

            if (item != null) {

                if (item.attr("class").equals("b-matches_data_top")) {
                    when = item.select("td").text();
                }

                if (item.attr("class").equals("b-matches_data_middle")
                        && (when.contains("Сегодня")
                        || when.contains("Сейчас"))) {
                    who = item.select("td").text();
                    LOGGER.info("WHO = " + who);
                }

                if (item.attr("class").equals("b-matches_data_bottom")) {

                    if (when.contains("Сегодня") || when.contains("Сейчас")) {
                        countTodayGames++;
                        how = item.select("em").text();
                        LOGGER.info("HOW = " + how);
                        LOGGER.info("how.isEmpty()? " + how.isEmpty());
                        LOGGER.info("item.select(\"td\").text().equals(\"подготовка\")? " + item.select("td").text().equals("подготовка"));
                        LOGGER.info("!checkHow(item.select(\"span\").text()).replaceAll(\";\", \"\").isEmpty() ? " + !checkHow(item.select("span").text()).replaceAll(";", "").isEmpty());

                        if (how.isEmpty() && (item.select("td").text().equals("подготовка"))) {
                            how = checkHow(item.select("td").text());
                        } else if (how.isEmpty() || !how.isEmpty() && !checkHow(item.select("span").text()).replaceAll(";", "").isEmpty()) {
                            how += checkHow(item.select("span").text()).replaceAll(";", "");
                        }

                        List<String> list = REDIS_STORE.lrange(who, 0, REDIS_STORE.dbSize());

                        if (list != null) {
                            for (String elem : list) {
                                if (how.equals(elem)) {
                                    LOGGER.info("The game already exist into map, go on....");
                                    containFlag = true;
                                }
                            }
                        }

                        if (!containFlag && !timeFlag) {
                            LOGGER.info("Put the game " + who + " into store...");
                            REDIS_STORE.lpush(who, how);
                            getInfo(stringBuilder, when, who, how);
                        } else if (containFlag && !timeFlag) {
                            LOGGER.info("Waiting...");
                            countTodayFinishedGames++;
                        } else if (!containFlag && timeFlag) {
                            LOGGER.info("Waiting...");
                        } else if (containFlag && timeFlag) {
                            LOGGER.info("Delete the game " + who + " into store...");
                            REDIS_STORE.del(who);
                            getInfo(stringBuilder, when, who, how);
                        }
                        LOGGER.info("Final HOW = " + how);
                    }
                }
            }
        }

        LOGGER.info("countTodayFinishedGames = " + countTodayFinishedGames);
        LOGGER.info("countTodayGames = " + countTodayGames);

        if (countTodayFinishedGames == countTodayGames) {
            ScheduledKHLInfo.stopCheckingInfoAfterAllGamesFinished = true;
            LOGGER.info("All today games finished...");
            LOGGER.info("Waiting summary of all finished games...");
            return "";
        }

        if (stringBuilder.toString().isEmpty()) {
            LOGGER.info("return empty message....");
            return "";
        }

        LOGGER.info("INFO_ABOUT_GAME: " + stringBuilder.toString());
        return stringBuilder.toString();
    }

    //TODO: допилить standings
    public static String getStandingsInfo(String url) throws IOException {

        if (BotHelper.getResponseCode(url) != 200) {
            LOGGER.info("ResponseCode != 200....");
            return "";
        }

        Document doc = Jsoup.connect(url).get();

        Elements elements = doc.getElementsByAttributeValue("id", "tab-standings-conference");

        StringBuilder stringBuilder = new StringBuilder();
        String conferenceOfWest;
        String conferenceOfEast;
        String position = "0";
        String club;
        String points;

        for (Element elem : elements.select("div").attr("class", "b-data_row")) {
//            LOGGER.info("H4 = " + elem.select("h4").first().text());
        }

        for (Element elem1 : elements.select("div").attr("class", "b-data_row").select("div").attr("class", "k-data_table").select("table").select("tbody").select("tr")) {

            if (elem1.select("td").first().text() != null && !elem1.select("td").first().text().isEmpty()) {
                position = elem1.select("td").first().text();
            }
            stringBuilder.append(position).append(". ");

            club = elem1.select("span").text();
            stringBuilder.append(club).append(" ");

            points = elem1.select("td").select("b").text();
            stringBuilder.append("-").append(points).append("\n");

//            if (elem1.select("span").is())

        }


        return stringBuilder.toString();
    }

    public static String getKHLNews(String url, LocalTime currentTime) throws IOException {

        if (BotHelper.getResponseCode(url) != 200) {
            LOGGER.info("ResponseCode != 200....");
            return "";
        }

        Document doc = Jsoup.connect(url).get();

        Elements elements = doc.getElementsByAttributeValue("class", "b-content_section m-teaser").first().getAllElements();

        StringBuilder stringBuilder = new StringBuilder();
        String newsUrl;

        for (Element elem : elements) {

            if (elem.attr("class").equals("b-middle_block")) {

                newsUrl = elem.select("a").first().attr("abs:href");
                if (newsUrl != null && !newsUrl.isEmpty()) {
                    if (!REDIS_STORE.exists(newsUrl)) {
                        LOGGER.info("Put the new article " + newsUrl + " into map: " + newsUrl);
                        REDIS_STORE.set(newsUrl, "");
                        stringBuilder.append(newsUrl).append("\n");
                    } else {
                        LOGGER.info("Article " + newsUrl + " is already exist! Looking for next article...");
                    }
                }

            }
        }

        if (stringBuilder.toString().isEmpty()) {
            return "";
        }

        return stringBuilder.toString();
    }

    public static String getPhotoToday(String url) throws IOException {

        if (BotHelper.getResponseCode(url) != 200) {
            LOGGER.info("ResponseCode != 200....");
            return "";
        }


        Document doc = Jsoup.connect(url).get();

        Elements elements = doc.getElementsByAttributeValue("id", "tab-photo-photoday").select("ul").select("li").select("a").first().getAllElements();

        StringBuilder photoTodaySb = new StringBuilder();

        String photoUrl;

        for (Element elem : elements) {

            photoUrl = elem.attr("abs:href");

            if (photoUrl != null && !photoUrl.isEmpty()) {
                if (!REDIS_STORE.exists(photoUrl)) {
                    LOGGER.info("Put the photo " + photoUrl + " into map...");
                    REDIS_STORE.set(photoUrl, "");
                    photoTodaySb.append("ФОТО ДНЯ \n").append(photoUrl).append("\n");
                } else {
                    LOGGER.info("Photo " + photoUrl + " is already exist! Looking for next photoDay...");
                }
            }
        }

        if (photoTodaySb.toString().isEmpty()) {
            return "";
        }

        return photoTodaySb.toString();
    }


    public static StringBuilder getVideo(String url) throws IOException {

        if (BotHelper.getResponseCode(url) != 200) {
            LOGGER.info("ResponseCode != 200....");
            return new StringBuilder();
        }

        Document doc = Jsoup.connect(url).get();
        Elements elements = doc.getElementsByAttributeValue("id", "tab-video-new").first().getAllElements();

        StringBuilder videoTodaySb = new StringBuilder();
        String videoUrl;

        for (Element elem : elements) {

            if (elem.attr("class").equals("b-middle_block")) {
                videoUrl = elem.select("a").first().attr("abs:href");
                checkVideoUrl(videoUrl, videoTodaySb);
            }

            if (elem.attr("class").equals("b-short_block")) {
                for (Element current : elem.getAllElements().select("div")) {
                    videoUrl = current.select("a").attr("abs:href");
                    checkVideoUrl(videoUrl, videoTodaySb);
                }
            }
        }

        if (videoTodaySb.toString().isEmpty()) {
            return new StringBuilder();
        }

        return videoTodaySb;
    }

    private static void checkVideoUrl(String videoUrl, StringBuilder sb) {
        if (videoUrl != null && !videoUrl.isEmpty()) {
            if (!REDIS_STORE.exists(videoUrl)) {
                LOGGER.info("Put the video " + videoUrl + "into map...");
                REDIS_STORE.set(videoUrl, "");
                sb.append(videoUrl).append("\n");
            } else {
                LOGGER.info("Video " + videoUrl + " is already exist! Looking for next video...");
            }
        }
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

    private static void getInfo(StringBuilder stringBuilder, String when, String who, String how) {
        stringBuilder.append("Когда: ".concat(when)).append("\n");
        stringBuilder.append("Versus: ".concat(who)).append("\n");
        stringBuilder.append("Результат: ".concat(how)).append("\n-------\uD83C\uDFD2\uD83C\uDFC6\uD83D\uDCAA\uD83C\uDFFB-------\n");
    }

    private static String checkHow(String how) {
        LOGGER.info("HOW BEFORE checking.... " + how);

        switch (how) {
            case "0":
                how = " \u23F8";
                break;
            case "1":
                how = " 1⃣";
                break;
            case "2":
                how = " 2⃣;";
                break;
            case "3":
                how = " 3⃣;";
                break;
            case "подготовка":
                how = "подготовка";
                break;
            case "б":
                how = " \uD83C\uDFD2";
                break;
            case "от":
                how = " \uD83D\uDD50";
                break;
            default:
                how = "";
        }
        LOGGER.info("HOW AFTER checking.... " + how);
        return how;
    }
}
