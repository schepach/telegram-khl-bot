package ru.khl.utils;

import org.apache.log4j.Logger;
import ru.khl.bot.KHLBot;
import ru.khl.bot.constants.Constants;

import java.io.IOException;

/**
 * Created by Alexey on 09.01.2017.
 */
public class KHLBotHelper {

    private static final Logger LOGGER = Logger.getLogger(KHLBot.class.getSimpleName());

    public static String checkUserText(String message) {

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

                // WEST Conference
                case Constants.SKA:
                    return Connection.sendRequest(message);
                case Constants.CSKA:
                    return Connection.sendRequest(message);
                case Constants.TORPEDO:
                    return Connection.sendRequest(message);
                case Constants.LOKOMOTIV:
                    return Connection.sendRequest(message);
                case Constants.DINAMO_MSK:
                    return Connection.sendRequest(message);
                case Constants.DINAMO_MINSK:
                    return Connection.sendRequest(message);
                case Constants.SOCHI:
                    return Connection.sendRequest(message);
                case Constants.VITYAZ:
                    return Connection.sendRequest(message);
                case Constants.SPARTAK:
                    return Connection.sendRequest(message);
                case Constants.MEDVESCAK:
                    return Connection.sendRequest(message);
                case Constants.SLOVAN:
                    return Connection.sendRequest(message);
                case Constants.SEVERSTAL:
                    return Connection.sendRequest(message);
                case Constants.DINAMO_RIGA:
                    return Connection.sendRequest(message);
                case Constants.JOKERIT:
                    return Connection.sendRequest(message);


                // EAST Conference
                case Constants.METALLURG_MAGNITOGORSK:
                    return Connection.sendRequest(message);
                case Constants.AVANGARD:
                    return Connection.sendRequest(message);
                case Constants.AK_BARS:
                    return Connection.sendRequest(message);
                case Constants.SALAVAT_YULAEV:
                    return Connection.sendRequest(message);
                case Constants.TRAKTOR:
                    return Connection.sendRequest(message);
                case Constants.ADMIRAL:
                    return Connection.sendRequest(message);
                case Constants.KUNLUN_RED_STAR:
                    return Connection.sendRequest(message);
                case Constants.NEFTEKHIMIK:
                    return Connection.sendRequest(message);
                case Constants.SIBIR:
                    return Connection.sendRequest(message);
                case Constants.BARYS:
                    return Connection.sendRequest(message);
                case Constants.AVTOMOBILIST:
                    return Connection.sendRequest(message);
                case Constants.LADA:
                    return Connection.sendRequest(message);
                case Constants.AMUR:
                    return Connection.sendRequest(message);
                case Constants.UGRA:
                    return Connection.sendRequest(message);
                case Constants.METALLURG_NOVOKUZNETSK:
                    return Connection.sendRequest(message);
                default:
                    return Constants.ERROR_OTHER_INPUT;
            }

        } catch (IOException ex) {
            LOGGER.error(Constants.UNEXPECTED_ERROR.concat(ex.getMessage()) + ex);
        }
        return Constants.ERROR_OTHER_INPUT;
    }
}
