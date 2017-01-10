package ru.khl.utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.khl.bot.constants.Constants;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by alexey on 01.11.16.
 */

public class Connection {

    private static final Logger LOGGER = Logger.getLogger(Connection.class.getSimpleName());
    private static Map<String, String> NEWS_MAP = new HashMap<>();

    public static String sendRequest(String command) throws IOException {

        String url;
        switch (command) {

            //WEST Conference
            case Constants.SKA:
                url = Constants.URL_SKA;
                return getInfoForHockeyClub(url);
            case Constants.CSKA:
                url = Constants.URL_CSKA;
                return getInfoForHockeyClub(url);
            case Constants.TORPEDO:
                url = Constants.URL_TORPEDO;
                return getInfoForHockeyClub(url);
            case Constants.LOKOMOTIV:
                url = Constants.URL_LOKOMOTIV;
                return getInfoForHockeyClub(url);
            case Constants.DINAMO_MSK:
                url = Constants.URL_DINAMO_MSK;
                return getInfoForHockeyClub(url);
            case Constants.DINAMO_MINSK:
                url = Constants.URL_DINAMO_MINSK;
                return getInfoForHockeyClub(url);
            case Constants.JOKERIT:
                url = Constants.URL_JOKERIT;
                return getInfoForHockeyClub(url);
            case Constants.VITYAZ:
                url = Constants.URL_VITYAZ;
                return getInfoForHockeyClub(url);
            case Constants.SOCHI:
                url = Constants.URL_SOCHI;
                return getInfoForHockeyClub(url);
            case Constants.SPARTAK:
                url = Constants.URL_SPARTAK;
                return getInfoForHockeyClub(url);
            case Constants.MEDVESCAK:
                url = Constants.URL_MEDVESCAK;
                return getInfoForHockeyClub(url);
            case Constants.SLOVAN:
                url = Constants.URL_SLOVAN;
                return getInfoForHockeyClub(url);
            case Constants.SEVERSTAL:
                url = Constants.URL_SEVERSTAL;
                return getInfoForHockeyClub(url);
            case Constants.DINAMO_RIGA:
                url = Constants.URL_DINAMO_RIGA;
                return getInfoForHockeyClub(url);

            // EAST Conference
            case Constants.METALLURG_MAGNITOGORSK:
                url = Constants.URL_METALLURG_MAGNITOGORSK;
                return getInfoForHockeyClub(url);
            case Constants.AVANGARD:
                url = Constants.URL_AVANGARD;
                return getInfoForHockeyClub(url);
            case Constants.AK_BARS:
                url = Constants.URL_AK_BARS;
                return getInfoForHockeyClub(url);
            case Constants.SALAVAT_YULAEV:
                url = Constants.URL_SALAVAT_YULAEV;
                return getInfoForHockeyClub(url);
            case Constants.TRAKTOR:
                url = Constants.URL_TRAKTOR;
                return getInfoForHockeyClub(url);
            case Constants.ADMIRAL:
                url = Constants.URL_ADMIRAL;
                return getInfoForHockeyClub(url);
            case Constants.KUNLUN_RED_STAR:
                url = Constants.URL_KUNLUN_RED_STAR;
                return getInfoForHockeyClub(url);
            case Constants.NEFTEKHIMIK:
                url = Constants.URL_NEFTEKHIMIK;
                return getInfoForHockeyClub(url);
            case Constants.SIBIR:
                url = Constants.URL_SIBIR;
                return getInfoForHockeyClub(url);
            case Constants.BARYS:
                url = Constants.URL_BARYS;
                return getInfoForHockeyClub(url);
            case Constants.AVTOMOBILIST:
                url = Constants.URL_AVTOMOBILIST;
                return getInfoForHockeyClub(url);
            case Constants.LADA:
                url = Constants.URL_LADA;
                return getInfoForHockeyClub(url);
            case Constants.AMUR:
                url = Constants.URL_AMUR;
                return getInfoForHockeyClub(url);
            case Constants.UGRA:
                url = Constants.URL_UGRA;
                return getInfoForHockeyClub(url);
            case Constants.METALLURG_NOVOKUZNETSK:
                url = Constants.URL_METALLURG_NOVOKUZNETSK;
                return getInfoForHockeyClub(url);
            default:
                return "Bad command! ".concat(command);
        }
    }

    public static String getInfoForHockeyClub(String url) throws IOException {

        getResponseCode(url);
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

    public static String getInfoForChanel(String url) throws IOException {

        getResponseCode(url);
        Document doc = Jsoup.connect(url).get();

        Elements elements = doc.select("tr");

        StringBuilder stringBuilder = new StringBuilder();

        String when = "";
        String who;
        String how;

        for (Element item : elements) {

            if (item != null) {


                if (item.attr("class").equals("b-matches_data_top")) {
                    when = item.select("td").text();
                    if (when.contains("Сегодня") || when.contains("Сейчас")) {
                        stringBuilder.append("Когда: ".concat(when)).append("\n");
                    }
                }

                if (item.attr("class").equals("b-matches_data_middle")) {
                    who = item.select("td").text();
                    if (when.contains("Сегодня") || when.contains("Сейчас")) {
                        if (!who.isEmpty()) {
                            stringBuilder.append("Versus: ".concat(who)).append("\n");
                        }
                    }
                }

                if (item.attr("class").equals("b-matches_data_bottom")) {
                    how = item.select("em").text();
                    if (when.contains("Сегодня") || when.contains("Сейчас")) {
                        if (!how.isEmpty()) {
                            stringBuilder.append("Результат: ".concat(how)).append("\n-------\uD83C\uDFD2\uD83C\uDFC6\uD83D\uDCAA\uD83C\uDFFB-------\n");
                        } else {
                            stringBuilder.append("Результат: ".concat(item.select("td").first().text())).append("\n-------\uD83C\uDFD2\uD83C\uDFC6\uD83D\uDCAA\uD83C\uDFFB-------\n");
                        }
                    }
                }
            }
        }

        if (stringBuilder.toString().isEmpty()) {
            return "Сегодня нет игр в КХЛ \uD83C\uDFD2\uD83C\uDFC6\uD83D\uDE10";
        }

        LOGGER.info("INFO_ABOUT_GAME: " + stringBuilder.toString());

        return stringBuilder.toString();
    }


    public static String getStandingsInfo(String url) throws IOException {

        getResponseCode(url);
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

    public static String getKHLNews(String url) throws IOException {

        getResponseCode(url);

        Document doc = Jsoup.connect(url).get();

        Elements elements = doc.getElementsByAttributeValue("class", "b-blocks_cover").first().getAllElements();

        StringBuilder stringBuilder = new StringBuilder();

        String newsUrl;

        for (Element elem : elements) {

            if (elem.attr("class").equals("b-middle_block")
                    || elem.attr("class").equals("b-short_block_cover m-left")
                    || elem.attr("class").equals("b-short_block_cover m-right")) {

                newsUrl = elem.select("a").first().attr("abs:href");

                if (!NEWS_MAP.containsKey(newsUrl) && newsUrl != null && !newsUrl.isEmpty()) {
                    System.err.println("Put to map: " + newsUrl);
                    NEWS_MAP.put(newsUrl, "");
                    stringBuilder.append(newsUrl).append("\n");
                } else {
                    System.err.println("Article is already exist! Looking for next article...");
                }
            }
        }

        if (stringBuilder.toString().isEmpty()) {
            return "";
        }

        return stringBuilder.toString();
    }

    private static void getResponseCode(String url) throws IOException {
        LOGGER.info("ConnectTo: " + url);
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
        HttpResponse response = client.execute(request);
        LOGGER.info("Response Code: " + response.getStatusLine().getStatusCode());
    }
}
