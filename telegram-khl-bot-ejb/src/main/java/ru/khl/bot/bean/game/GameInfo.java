package ru.khl.bot.bean.game;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import ru.khl.bot.utils.BotHelper;

import javax.ejb.Stateless;
import javax.enterprise.inject.Default;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Default
@Stateless
public class GameInfo implements Game {

    private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    @Override
    public String getGamesInfoOfHockeyClub(String clubName) {
        String url = "https://www.khl.ru/clubs".concat(clubName);

        try {
            int responseCode = BotHelper.getResponseCode(url);

            if (responseCode != 200) {
                logger.log(Level.SEVERE, "Response code = {0}", responseCode);
                return null;
            }

        } catch (IOException e) {
            logger.log(Level.SEVERE, null, e);
            return null;
        }

        String club1;
        String club2;
        String when;
        String totalScore;

        Document doc;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            logger.log(Level.SEVERE, null, e);
            return null;
        }

        Elements elements = doc.select("div.b-content_section.s-float_panel_start div.b-blocks_cover div.b-half_block ul.b-wide_tile li.b-wide_tile_item");

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Подписывайтесь на канал KHL https://t.me/khl_unofficial - там Вас ждет много интересного!").append("\n\n");

        for (Element item : elements) {

            //Get VS commands
            Elements clubsItem = item.select("dd.b-details_txt");
            club1 = clubsItem.get(0).select("h5.e-club_name").text().concat("(").concat(clubsItem.get(0).select("p.e-club_sity").text().concat(")"));
            club2 = clubsItem.get(1).select("h5.e-club_name").text().concat("(").concat(clubsItem.get(1).select("p.e-club_sity").text().concat(")"));
            stringBuilder.append(club1).append(" - ").append(club2).append("\n");

            //Get WHEN or TOTAL SCORE
            Elements whenItem = item.select("dt.b-total_score");
            if (whenItem.size() == 1) {
                when = "Дата встречи: " + whenItem.select("b.e-match-num").text() + " в " + whenItem.select("h3").first().text();
                stringBuilder.append(when);
            } else {
                totalScore = "Дата встречи: " + whenItem.get(0).select("b.e-match-num").text() + "\n"
                        + "Результат: " + whenItem.get(1).select("h3").text() + "\n"
                        + "Подробно: " + item.select("dd.b-period_score").text();
                stringBuilder.append(totalScore);
            }
            stringBuilder.append("\n-------\uD83C\uDFD2\uD83C\uDFC6\uD83D\uDCAA\uD83C\uDFFB-------\n");
        }

        logger.log(Level.INFO, "Info about hockey club - {0} ", stringBuilder.toString());

        return stringBuilder.toString();

    }
}
