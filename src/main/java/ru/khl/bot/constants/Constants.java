package ru.khl.bot.constants;

/**
 * Created by alexey on 01.11.16.
 */
public class Constants {

    public static final String HC_CLUBS_LIST_EAST = "/HOCKEYCLUBSOFEAST";
    public static final String HC_CLUBS_LIST_WEST = "/HOCKEYCLUBSOFWEST";

    // WEST Conference
    public static final String SKA = "/SKA";
    public static final String CSKA = "/CSKA";
    public static final String TORPEDO = "/TORPEDO";
    public static final String LOKOMOTIV = "/LOKOMOTIV";
    public static final String DINAMO_MSK = "/DINAMO_MSK";
    public static final String DINAMO_MINSK = "/DINAMO_MINSK";
    public static final String JOKERIT = "/JOKERIT";
    public static final String VITYAZ = "/VITYAZ";
    public static final String SOCHI = "/SOCHI";
    public static final String SPARTAK = "/SPARTAK";
    public static final String MEDVESCAK = "/MEDVESCAK";
    public static final String SLOVAN = "/SLOVAN";
    public static final String SEVERSTAL = "/SEVERSTAL";
    public static final String DINAMO_RIGA = "/DINAMO_RIGA";


    public static final String URL_SKA = "http://www.khl.ru/clubs/ska/";
    public static final String URL_CSKA = "http://www.khl.ru/clubs/cska/";
    public static final String URL_TORPEDO = "http://www.khl.ru/clubs/torpedo/";
    public static final String URL_LOKOMOTIV = "http://www.khl.ru/clubs/lokomotiv/";
    public static final String URL_DINAMO_MSK = "http://www.khl.ru/clubs/dynamo_msk/";
    public static final String URL_DINAMO_MINSK = "http://www.khl.ru/clubs/dinamo_mn/";
    public static final String URL_JOKERIT = "http://www.khl.ru/clubs/jokerit/";
    public static final String URL_VITYAZ = "http://www.khl.ru/clubs/vityaz/";
    public static final String URL_SOCHI = "http://www.khl.ru/clubs/hc_sochi/";
    public static final String URL_SPARTAK = "http://www.khl.ru/clubs/spartak/";
    public static final String URL_MEDVESCAK = "http://www.khl.ru/clubs/medvescak/";
    public static final String URL_SLOVAN = "http://www.khl.ru/clubs/slovan/";
    public static final String URL_SEVERSTAL = "http://www.khl.ru/clubs/severstal/";
    public static final String URL_DINAMO_RIGA = "http://www.khl.ru/clubs/dinamo_r/";


    //EAST Conference
    public static final String METALLURG_MAGNITOGORSK = "/METALLURG_MAGNITOGORSK";
    public static final String AVANGARD = "/AVANGARD";
    public static final String AK_BARS = "/AK_BARS";
    public static final String SALAVAT_YULAEV = "/SALAVAT_YULAEV";
    public static final String TRAKTOR = "/TRAKTOR";
    public static final String ADMIRAL = "/ADMIRAL";
    public static final String KUNLUN_RED_STAR = "/KUNLUN_RED_STAR";
    public static final String NEFTEKHIMIK = "/NEFTEKHIMIK";
    public static final String SIBIR = "/SIBIR";
    public static final String BARYS = "/BARYS";
    public static final String AVTOMOBILIST = "/AVTOMOBILIST";
    public static final String LADA = "/LADA";
    public static final String AMUR = "/AMUR";
    public static final String UGRA = "/UGRA";
    public static final String METALLURG_NOVOKUZNETSK = "/METALLURG_NOVOKUZNETSK";

    public static final String URL_METALLURG_MAGNITOGORSK = "http://www.khl.ru/clubs/metallurg_mg/";
    public static final String URL_AVANGARD = "http://www.khl.ru/clubs/avangard/";
    public static final String URL_AK_BARS = "http://www.khl.ru/clubs/ak_bars/";
    public static final String URL_SALAVAT_YULAEV = "http://www.khl.ru/clubs/salavat_yulaev/";
    public static final String URL_TRAKTOR = "http://www.khl.ru/clubs/traktor/";
    public static final String URL_ADMIRAL = "http://www.khl.ru/clubs/admiral/";
    public static final String URL_KUNLUN_RED_STAR = "http://www.khl.ru/clubs/kunlun/";
    public static final String URL_NEFTEKHIMIK = "http://www.khl.ru/clubs/neftekhimik/";
    public static final String URL_SIBIR = "http://www.khl.ru/clubs/sibir/";
    public static final String URL_BARYS = "http://www.khl.ru/clubs/barys/";
    public static final String URL_AVTOMOBILIST = "http://www.khl.ru/clubs/avtomobilist/";
    public static final String URL_LADA = "http://www.khl.ru/clubs/lada/";
    public static final String URL_AMUR = "http://www.khl.ru/clubs/amur/";
    public static final String URL_UGRA = "http://www.khl.ru/clubs/ugra/";
    public static final String URL_METALLURG_NOVOKUZNETSK = "http://www.khl.ru/clubs/metallurg_nk/";


    public static final String URL_KHL_INFO = "http://www.khl.ru/";
    public static final String URL_STANDINGS = "http://www.khl.ru/standings/";
    public static final String URL_KHL_NEWS = "http://www.khl.ru/news/";

    public static final String START_TEXT = "Enter your favorite conference. /help - помощь";
    public static final String HELP = "/HELP";
    public static final String START = "/START";
    public static final String ERROR_OTHER_INPUT = "Bad Command! /help - помощь";
    public static final String UNEXPECTED_ERROR = "Unexpected error: ";

    public static final String HELP_TEXT =
            "I have next commands:\n" +
                    "/hockeyclubsofeast\n" +
                    "/hockeyclubsofwest\n" +
                    "/help";

    public static String getWESTTeams() {

        return "/SKA" + "\n" +
                "/CSKA" + "\n" +
                "/TORPEDO" + "\n" +
                "/LOKOMOTIV" + "\n" +
                "/DINAMO_MSK" + "\n" +
                "/DINAMO_MINSK" + "\n" +
                "/JOKERIT" + "\n" +
                "/VITYAZ" + "\n" +
                "/SOCHI" + "\n" +
                "/SPARTAK" + "\n" +
                "/MEDVESCAK" + "\n" +
                "/SLOVAN" + "\n" +
                "/SEVERSTAL" + "\n" +
                "/DINAMO_RIGA";
    }

    public static String getEASTTeams() {

        return "/METALLURG_MAGNITOGORSK \n" +
                "/AVANGARD \n" +
                "/AK_BARS \n" +
                "/SALAVAT_YULAEV \n" +
                "/TRAKTOR \n" +
                "/ADMIRAL \n" +
                "/KUNLUN_RED_STAR \n" +
                "/NEFTEKHIMIK \n" +
                "/SIBIR \n" +
                "/BARYS \n" +
                "/AVTOMOBILIST \n" +
                "/LADA \n" +
                "/AMUR \n" +
                "/UGRA \n" +
                "/METALLURG_NOVOKUZNETSK";
    }

}
