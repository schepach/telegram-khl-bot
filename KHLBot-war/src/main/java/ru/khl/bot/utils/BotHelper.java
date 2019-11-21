package ru.khl.bot.utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.log4j.Logger;
import ru.khl.bot.constants.Constants;

import java.io.IOException;

/**
 * Created by Alexey on 09.01.2017.
 */
public class BotHelper {

    private static final Logger LOGGER = Logger.getLogger(BotHelper.class.getSimpleName());

    public static String checkUserText(String message) {

        String url;

        try {

            switch (message) {
                case Constants.START:
                    return Constants.START_TEXT;
                case Constants.HELP:
                    return Constants.HELP_TEXT;
                case Constants.HC_CLUBS_LIST_WEST:
                    return Constants.getWESTTeams();
                case Constants.HC_CLUBS_LIST_EAST:
                    return Constants.getEASTTeams();

                //WEST Conference
                case Constants.SKA:
                    url = Constants.URL_SKA;
                    return Connection.getInfoForHockeyClub(url);
                case Constants.CSKA:
                    url = Constants.URL_CSKA;
                    return Connection.getInfoForHockeyClub(url);
                case Constants.TORPEDO:
                    url = Constants.URL_TORPEDO;
                    return Connection.getInfoForHockeyClub(url);
                case Constants.LOKOMOTIV:
                    url = Constants.URL_LOKOMOTIV;
                    return Connection.getInfoForHockeyClub(url);
                case Constants.DINAMO_MSK:
                    url = Constants.URL_DINAMO_MSK;
                    return Connection.getInfoForHockeyClub(url);
                case Constants.DINAMO_MINSK:
                    url = Constants.URL_DINAMO_MINSK;
                    return Connection.getInfoForHockeyClub(url);
                case Constants.JOKERIT:
                    url = Constants.URL_JOKERIT;
                    return Connection.getInfoForHockeyClub(url);
                case Constants.VITYAZ:
                    url = Constants.URL_VITYAZ;
                    return Connection.getInfoForHockeyClub(url);
                case Constants.SOCHI:
                    url = Constants.URL_SOCHI;
                    return Connection.getInfoForHockeyClub(url);
                case Constants.SPARTAK:
                    url = Constants.URL_SPARTAK;
                    return Connection.getInfoForHockeyClub(url);
                case Constants.MEDVESCAK:
                    url = Constants.URL_MEDVESCAK;
                    return Connection.getInfoForHockeyClub(url);
                case Constants.SLOVAN:
                    url = Constants.URL_SLOVAN;
                    return Connection.getInfoForHockeyClub(url);
                case Constants.SEVERSTAL:
                    url = Constants.URL_SEVERSTAL;
                    return Connection.getInfoForHockeyClub(url);
                case Constants.DINAMO_RIGA:
                    url = Constants.URL_DINAMO_RIGA;
                    return Connection.getInfoForHockeyClub(url);

                // EAST Conference
                case Constants.METALLURG_MAGNITOGORSK:
                    url = Constants.URL_METALLURG_MAGNITOGORSK;
                    return Connection.getInfoForHockeyClub(url);
                case Constants.AVANGARD:
                    url = Constants.URL_AVANGARD;
                    return Connection.getInfoForHockeyClub(url);
                case Constants.AK_BARS:
                    url = Constants.URL_AK_BARS;
                    return Connection.getInfoForHockeyClub(url);
                case Constants.SALAVAT_YULAEV:
                    url = Constants.URL_SALAVAT_YULAEV;
                    return Connection.getInfoForHockeyClub(url);
                case Constants.TRAKTOR:
                    url = Constants.URL_TRAKTOR;
                    return Connection.getInfoForHockeyClub(url);
                case Constants.ADMIRAL:
                    url = Constants.URL_ADMIRAL;
                    return Connection.getInfoForHockeyClub(url);
                case Constants.KUNLUN_RED_STAR:
                    url = Constants.URL_KUNLUN_RED_STAR;
                    return Connection.getInfoForHockeyClub(url);
                case Constants.NEFTEKHIMIK:
                    url = Constants.URL_NEFTEKHIMIK;
                    return Connection.getInfoForHockeyClub(url);
                case Constants.SIBIR:
                    url = Constants.URL_SIBIR;
                    return Connection.getInfoForHockeyClub(url);
                case Constants.BARYS:
                    url = Constants.URL_BARYS;
                    return Connection.getInfoForHockeyClub(url);
                case Constants.AVTOMOBILIST:
                    url = Constants.URL_AVTOMOBILIST;
                    return Connection.getInfoForHockeyClub(url);
                case Constants.LADA:
                    url = Constants.URL_LADA;
                    return Connection.getInfoForHockeyClub(url);
                case Constants.AMUR:
                    url = Constants.URL_AMUR;
                    return Connection.getInfoForHockeyClub(url);
                case Constants.UGRA:
                    url = Constants.URL_UGRA;
                    return Connection.getInfoForHockeyClub(url);
                case Constants.METALLURG_NOVOKUZNETSK:
                    url = Constants.URL_METALLURG_NOVOKUZNETSK;
                    return Connection.getInfoForHockeyClub(url);
                default:
                    return Constants.ERROR_OTHER_INPUT;
            }

        } catch (Exception ex) {
            LOGGER.info("Method checkUserText exception: ", ex);
        }
        return Constants.ERROR_OTHER_INPUT;
    }

    static int getResponseCode(String url) throws IOException {
        LOGGER.info("ConnectTo: " + url);
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
        HttpResponse response = client.execute(request);
        LOGGER.info("Response Code: " + response.getStatusLine().getStatusCode());

        return response.getStatusLine().getStatusCode();
    }
}
