package ru.khl.bot.utils;

import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.khl.bot.constants.Constants;

import java.io.IOException;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by alexey on 01.11.16.
 */

public class Connection {

    private static final Logger LOGGER = Logger.getLogger(Connection.class.getSimpleName());
    private static final Pattern PATTERN_TIME_OF_GAME = Pattern.compile("(.*)(\\d{2}:\\d{2})(.*)");
    private static final Pattern PATTERN_SCORE = Pattern.compile("(.*)(\\d{1,2}-\\d{1,2})(.*)");
    private static final Pattern PATTERN_END_GAME = Pattern.compile("(.*)(\\d{1,2}(Б||ОТ)\\d{1,2})(.*)");
    private static Map<String, String> NEWS_URL_MAP = new HashMap<>();
    private static Map<String, String> GAME_MAP = new HashMap<>();
    private static Map<String, String> PHOTOS_URL_MAP = new HashMap<>();
    private static Map<String, String> VIDEOS_URL_MAP = new HashMap<>();

    public static String getInfoForChannel(String url, LocalTime currentTime) throws IOException {
        Matcher m = null;

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

        boolean timeFlag = false;
        boolean endGameFlag = false;

        for (Element item : elements) {

            if (item != null) {

                if (currentTime.equals(Constants.START_TIME)
                        || (currentTime.isAfter(Constants.START_TIME) && currentTime.isBefore(LocalTime.of(10, 0, 0)))
                        || (currentTime.isBefore(LocalTime.of(0, 40, 0)))) {
                    timeFlag = true;
                }

                //Clear the map of KHL clubs from 8:30 to 9:00 in the morning....
                if (currentTime.equals(LocalTime.of(8, 30, 0))
                        || currentTime.isAfter(LocalTime.of(8, 30, 0)) && currentTime.isBefore(LocalTime.of(9, 0, 0))) {
                    LOGGER.info("Clear GAME_MAP: ");
                    GAME_MAP.clear();
                    LOGGER.info("GAME_MAP is empty: ");
                }


                if (item.attr("class").equals("b-matches_data_top")) {
                    when = item.select("td").text();
                    LOGGER.info("WHEN = " + when);
                }

                if (item.attr("class").equals("b-matches_data_middle")) {
                    who = item.select("td").text();
                    LOGGER.info("WHO before checking OT/SO = " + who);
                    LOGGER.info("timeFlag = " + timeFlag);
                    if (!timeFlag) {
                        LOGGER.info("Checking OT/SO because timeFlag is FALSE....");
                        if (when.contains("Сегодня") || when.contains("Сейчас") && !who.isEmpty()) {
                            m = PATTERN_END_GAME.matcher(who.replaceAll(" ", ""));
                            LOGGER.info("m.matches() = " + m.matches());
                            if (m.matches()) {
                                endGameFlag = true;
                                LOGGER.info("OT/SO= " + who);
                                LOGGER.info("The game end. endGameFlag = " + endGameFlag);
                            }
                        }
                    } else {
                        LOGGER.info("Don't checking OT/SO because timeFlag is TRUE....");
                    }
                    LOGGER.info("SUMMARY: endGameFlag = " + endGameFlag);
                }

                if (item.attr("class").equals("b-matches_data_bottom")) {

                    how = item.select("em").text();
                    int count = 0;
                    boolean flagGameFinished = false;

                    if (when.contains("Сегодня") || when.contains("Сейчас")) {

                        LOGGER.info("WHO = " + who);
                        LOGGER.info("timeFlag  = " + timeFlag);
                        LOGGER.info("Подготовка? = " + item.select("td").text().equals("подготовка"));
                        LOGGER.info("How is Empty? = " + how.isEmpty());

                        if (how.isEmpty() && (item.select("td").text().equals("подготовка"))) {
                            if (timeFlag) {
                                getInfo(stringBuilder, when, who, how);
                                continue;
                            } else {
                                LOGGER.info("Time isn't came! Waiting starting the game...");
                                continue;
                            }
                        }

                        if (!how.isEmpty() && !timeFlag) {

                            String[] arrStr = how.split(" ");
                            String timeGame = null;

                            for (String str1 : arrStr) {

                                LOGGER.info("HOW: " + str1);

                                m = PATTERN_SCORE.matcher(str1.replaceAll(" ", ""));
                                LOGGER.info("m.matches(): " + m.matches());
                                if (m.matches()) {
                                    count++;
                                }
                            }
                            LOGGER.info("Count = " + count);

                            if (GAME_MAP.containsKey(who)) {
                                LOGGER.info("The game already exist into map, go on....");
                                flagGameFinished = true;
                            }

                            if ((count == 4 || count == 5) && endGameFlag && !GAME_MAP.containsKey(who)) {
                                LOGGER.info("Put the game into map (OT/SO)....");
                                GAME_MAP.put(who, "");
                            }

                            if (count == 3 && !GAME_MAP.containsKey(who)) {
                                LOGGER.info("Put the game into map....");
                                GAME_MAP.put(who, "");
                            }

                            LOGGER.info("flagGameFinished = " + flagGameFinished);

                            if (flagGameFinished) {
                                stringBuilder.append("");
                                continue;
                            }

                            if (!flagGameFinished) {
                                LOGGER.info("FlagGameFinished is false...Need check time...");
                                timeGame = arrStr[0] + ":00";
                                LOGGER.info("timeGame = " + timeGame);
                                m = PATTERN_TIME_OF_GAME.matcher(timeGame.replaceAll(" ", ""));
                            }

                            if (m.matches() && timeGame != null && !flagGameFinished) {
                                LocalTime localTimeGame = LocalTime.parse(timeGame);
                                LOGGER.info("LOCAL GAME TIME = " + localTimeGame);

                                if (currentTime.equals(localTimeGame)
                                        || currentTime.isAfter(localTimeGame)) {
                                    LOGGER.info("Time is came! Need post!");
                                    getInfo(stringBuilder, when, who, how.concat(" ▶️"));
                                } else {
                                    LOGGER.info("Time isn't came! Waiting starting the game...");
                                }
                            } else {
                                if (item.select("span").text().equals("0")) {
                                    getInfo(stringBuilder, when, who, how.concat(" \u23F8"));
                                } else {
                                    getInfo(stringBuilder, when, who, how.concat(" ▶️"));
                                }
                            }
                        } else {
                            getInfo(stringBuilder, when, who, how);
                        }
                    }
                }
            }
        }

        if (stringBuilder.toString().isEmpty()) {
            LOGGER.info("return empty message....");
            return "";
        }

        LOGGER.info("INFO_ABOUT_GAME: " + stringBuilder.toString());
        return stringBuilder.toString();
    }


    public static String getStandingsInfo(String url) throws IOException {

        if (BotHelper.getResponseCode(url) != 200) {
            LOGGER.info("ResponseCode != 200....");
            return "";
        }

        Document doc = Jsoup.connect(url).get();

        Elements elements = doc.getElementsByAttributeValue("id", "tab-standings-conference");

        StringBuilder stringBuilder = new StringBuilder();

        HashMap<String, String> hashMap = new HashMap<>();
        String conferenceOfWest = "west";
        String conferenceOfEast = "east";

        String position;
        String club;
        String points;

        for (Element elem : elements.select("div").select("h4")) {
            if (hashMap.isEmpty()) {
                hashMap.put(conferenceOfWest, elem.text().substring(22));
            } else {
                hashMap.put(conferenceOfEast, elem.text().substring(22));
            }
        }
        stringBuilder.append("\n-------------------\n").append(hashMap.get(conferenceOfWest)).append("\n-------------------\n");


        for (Element elem : elements.select("table").select("tbody").select("tr")) {

            position = elem.select("td").first().text();
            stringBuilder.append(position).append(". ");

            club = elem.select("span").text();
            stringBuilder.append(club).append(" ");

            points = elem.select("td").select("b").text();
            if (club.equals("Динамо Р")) {
                stringBuilder.append("-").append(points).append("\n-------------------\n").append(hashMap.get(conferenceOfEast)).append("\n-------------------\n");
            } else {
                stringBuilder.append("-").append(points).append("\n");
            }
        }
        return stringBuilder.toString();
    }

    public static String getKHLNews(String url, LocalTime currentTime) throws IOException {

        if (BotHelper.getResponseCode(url) != 200) {
            LOGGER.info("ResponseCode != 200....");
            return "";
        }

        Document doc = Jsoup.connect(url).get();

        Elements elements = doc.getElementsByAttributeValue("class", "b-blocks_cover").first().getAllElements();

        StringBuilder stringBuilder = new StringBuilder();

        String newsUrl;

        for (Element elem : elements) {

            if (elem.attr("class").equals("b-middle_block")
                    || elem.attr("class").equals("b-short_block_cover m-left")
                    || elem.attr("class").equals("b-short_block_cover m-right")) {

                newsUrl = elem.select("a").first().attr("abs:href");

                //Clear the map of KHL news from 03:00 to 04:00 at night...
                if ((currentTime.equals(LocalTime.of(3, 0, 0))
                        || currentTime.isAfter(LocalTime.of(3, 0, 0)) && currentTime.isBefore(LocalTime.of(4, 0, 0)))) {

                    if (newsUrl != null && !newsUrl.isEmpty() && NEWS_URL_MAP.containsKey(newsUrl)) {
                        LOGGER.info("Remove the news from map:  " + newsUrl);
                        NEWS_URL_MAP.remove(newsUrl);
                    }
                } else {
                    if (!NEWS_URL_MAP.containsKey(newsUrl) && newsUrl != null && !newsUrl.isEmpty()) {
                        LOGGER.info("Put the article into map: " + newsUrl);
                        NEWS_URL_MAP.put(newsUrl, "");
                        stringBuilder.append(newsUrl).append("\n");
                    } else {
                        LOGGER.info("Article is already exist! Looking for next article...");
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

            if (photoUrl != null && !photoUrl.isEmpty() && !PHOTOS_URL_MAP.containsKey(photoUrl)) {
                LOGGER.info("Put the photo into map..." + photoUrl);
                PHOTOS_URL_MAP.put(photoUrl, "");
                photoTodaySb.append("ФОТО ДНЯ \n").append(photoUrl).append("\n");
            } else {
                LOGGER.info("Photo is already exist! Looking for next photoDay...");
            }
        }

        if (photoTodaySb.toString().isEmpty()) {
            return "";
        }

        return photoTodaySb.toString();
    }


    public static String getVideo(String url) throws IOException {

        if (BotHelper.getResponseCode(url) != 200) {
            LOGGER.info("ResponseCode != 200....");
            return "";
        }


        Document doc = Jsoup.connect(url).get();

        Elements elements = doc.getElementsByAttributeValue("id", "tab-video-new").first().getAllElements();

        StringBuilder photoTodaySb = new StringBuilder();

        String videoUrl;

        for (Element elem : elements) {

            if (elem.attr("class").equals("b-middle_block")) {

                videoUrl = elem.select("a").first().attr("abs:href");
                if (videoUrl != null && !videoUrl.isEmpty() && !VIDEOS_URL_MAP.containsKey(videoUrl)) {
                    LOGGER.info("Put the video into map..." + videoUrl);
                    VIDEOS_URL_MAP.put(videoUrl, "");
                    photoTodaySb.append(videoUrl).append("\n");
                } else {
                    LOGGER.info("Video is already exist! Looking for next video...");
                }
            }
        }

        if (photoTodaySb.toString().isEmpty()) {
            return "";
        }

        return photoTodaySb.toString();
    }


    static String getInfoForHockeyClub(String url) throws IOException {

        if (BotHelper.getResponseCode(url) != 200) {
            LOGGER.info("ResponseCode != 200....");
            return "";
        }
        Document doc = Jsoup.connect(url).get();

        Elements elements = doc.select("dl");

        StringBuilder stringBuilder = new StringBuilder();

        String club1;
        String club2;
        String when;
        String totalScore;
        String details;

        for (Element item : elements) {

            if (item.attr("class").equals("b-details m-club")) {
                club1 = item.select("dd").select("h5").text();
                if (!club1.isEmpty()) {
                    stringBuilder.append("Команда: ".concat(club1)).append(" (").append(item.select("dd").select("p").first().text()).append(")").append("\n");
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
}
