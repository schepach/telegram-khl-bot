package ru.khl.bot.bean.game;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
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

        Document doc;
        try {
            doc = Jsoup.connect(url).get();
        } catch (IOException e) {
            logger.log(Level.SEVERE, null, e);
            return null;
        }

        // Get list of last games
        Elements games = doc.select("#wrapper > div.clubs-detail > div > div > section:nth-child(3) > div > div.information-body > div > div > div.information-body__cards > div.information-body__content-cards > div.card-games > div.card-game_height-fixed");

        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("Подписывайтесь на канал KHL @khl_unofficial - там Вас ждет много интересного!").append("\n\n");

        // Loop for each game
        games.forEach(game -> {

            // Get VS commands
            String clubOne = game.select("a.card-game__club.card-game__club_left > p.card-game__club-name").text()
                    .concat("(")
                    .concat(game.select("a.card-game__club.card-game__club_left > p.card-game__club-local").text())
                    .concat(")");

            String clubTwo = game.select("a.card-game__club.card-game__club_right > p.card-game__club-name").text()
                    .concat("(")
                    .concat(game.select("a.card-game__club.card-game__club_right > p.card-game__club-local").text())
                    .concat(")");

            stringBuilder.append(clubOne).append(" VS ").append(clubTwo).append("\n");

            // Get game DATE
            String gameDate = game.select("div.card-game__center > time.card-game__center-day").text();
            stringBuilder.append("Дата встречи: ").append(gameDate).append("\n");

            // Get TOTAL SCORE and DETAILS
            String gameScore = game.select("div.card-game__center > p.card-game__center-score").text();
            if (gameScore != null && !gameScore.isEmpty()) {
                String gameDetails = game.select("div.card-game__center-info > p.card-game__center-value").text();
                stringBuilder.append("Результат: ").append(gameScore).append("\n")
                        .append("Подробно: ").append(gameDetails).append("\n");
            }
            stringBuilder.append("-------🏒🥅🏆-------\n");
        });

        logger.log(Level.INFO, "Info about hockey club - {0} ", stringBuilder.toString());

        return stringBuilder.toString();

    }
}
