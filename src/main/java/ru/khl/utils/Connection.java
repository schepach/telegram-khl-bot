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

/**
 * Created by alexey on 01.11.16.
 */

public class Connection {

    private static final Logger LOGGER = Logger.getLogger(Connection.class.getSimpleName());

    public static String sendRequest(String command) throws IOException {


        //TODO: url'ы брать с сайта, а не хардкодить в константах

        String url[] = new String[1];
        switch (command) {

            //WEST Conference
            case Constants.SKA:
                url[0] = Constants.URL_SKA;
                return getInfoForHockeyClub(url[0]);
            case Constants.CSKA:
                url[0] = Constants.URL_CSKA;
                return getInfoForHockeyClub(url[0]);
            case Constants.TORPEDO:
                url[0] = Constants.URL_TORPEDO;
                return getInfoForHockeyClub(url[0]);
            case Constants.LOKOMOTIV:
                url[0] = Constants.URL_LOKOMOTIV;
                return getInfoForHockeyClub(url[0]);
            case Constants.DINAMO_MSK:
                url[0] = Constants.URL_DINAMO_MSK;
                return getInfoForHockeyClub(url[0]);
            case Constants.DINAMO_MINSK:
                url[0] = Constants.URL_DINAMO_MINSK;
                return getInfoForHockeyClub(url[0]);
            case Constants.JOKERIT:
                url[0] = Constants.URL_JOKERIT;
                return getInfoForHockeyClub(url[0]);
            case Constants.VITYAZ:
                url[0] = Constants.URL_VITYAZ;
                return getInfoForHockeyClub(url[0]);
            case Constants.SOCHI:
                url[0] = Constants.URL_SOCHI;
                return getInfoForHockeyClub(url[0]);
            case Constants.SPARTAK:
                url[0] = Constants.URL_SPARTAK;
                return getInfoForHockeyClub(url[0]);
            case Constants.MEDVESCAK:
                url[0] = Constants.URL_MEDVESCAK;
                return getInfoForHockeyClub(url[0]);
            case Constants.SLOVAN:
                url[0] = Constants.URL_SLOVAN;
                return getInfoForHockeyClub(url[0]);
            case Constants.SEVERSTAL:
                url[0] = Constants.URL_SEVERSTAL;
                return getInfoForHockeyClub(url[0]);
            case Constants.DINAMO_RIGA:
                url[0] = Constants.URL_DINAMO_RIGA;
                return getInfoForHockeyClub(url[0]);


            // EAST Conference
            case Constants.METALLURG_MAGNITOGORSK:
                url[0] = Constants.URL_METALLURG_MAGNITOGORSK;
                return getInfoForHockeyClub(url[0]);
            case Constants.AVANGARD:
                url[0] = Constants.URL_AVANGARD;
                return getInfoForHockeyClub(url[0]);
            case Constants.AK_BARS:
                url[0] = Constants.URL_AK_BARS;
                return getInfoForHockeyClub(url[0]);
            case Constants.SALAVAT_YULAEV:
                url[0] = Constants.URL_SALAVAT_YULAEV;
                return getInfoForHockeyClub(url[0]);
            case Constants.TRAKTOR:
                url[0] = Constants.URL_TRAKTOR;
                return getInfoForHockeyClub(url[0]);
            case Constants.ADMIRAL:
                url[0] = Constants.URL_ADMIRAL;
                return getInfoForHockeyClub(url[0]);
            case Constants.KUNLUN_RED_STAR:
                url[0] = Constants.URL_KUNLUN_RED_STAR;
                return getInfoForHockeyClub(url[0]);
            case Constants.NEFTEKHIMIK:
                url[0] = Constants.URL_NEFTEKHIMIK;
                return getInfoForHockeyClub(url[0]);
            case Constants.SIBIR:
                url[0] = Constants.URL_SIBIR;
                return getInfoForHockeyClub(url[0]);
            case Constants.BARYS:
                url[0] = Constants.URL_BARYS;
                return getInfoForHockeyClub(url[0]);
            case Constants.AVTOMOBILIST:
                url[0] = Constants.URL_AVTOMOBILIST;
                return getInfoForHockeyClub(url[0]);
            case Constants.LADA:
                url[0] = Constants.URL_LADA;
                return getInfoForHockeyClub(url[0]);
            case Constants.AMUR:
                url[0] = Constants.URL_AMUR;
                return getInfoForHockeyClub(url[0]);
            case Constants.UGRA:
                url[0] = Constants.URL_UGRA;
                return getInfoForHockeyClub(url[0]);
            case Constants.METALLURG_NOVOKUZNETSK:
                url[0] = Constants.URL_METALLURG_NOVOKUZNETSK;
                return getInfoForHockeyClub(url[0]);
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

        stringBuilder.append("Новости КХЛ").append("\n");

        String news_middle_block;
        String news_m_left;
        String news_m_right;

        for (Element elem : elements) {

            if (elem.attr("class").equals("b-middle_block")) {
                news_middle_block = elem.select("a").first().attr("abs:href");
                stringBuilder.append(news_middle_block).append("\n");
            }

            if (elem.attr("class").equals("b-short_block_cover m-left")) {
                news_m_left = elem.select("a").first().attr("abs:href");
                stringBuilder.append(news_m_left).append("\n");
            }

            if (elem.attr("class").equals("b-short_block_cover m-right")) {
                news_m_right = elem.select("a").first().attr("abs:href");
                stringBuilder.append(news_m_right).append("\n");
            }
        }
        if (stringBuilder.toString().isEmpty()) {
            return "Сегодня нет новостей в КХЛ";
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
