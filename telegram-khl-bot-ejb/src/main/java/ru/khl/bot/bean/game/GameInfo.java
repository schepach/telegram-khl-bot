package ru.khl.bot.bean.game;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.InlineKeyboardButton;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.khl.bot.KHLBot;
import ru.khl.bot.db.IDBOperations;
import ru.khl.bot.model.ClubInfo;
import ru.khl.bot.utils.BotHelper;

import javax.ejb.Stateless;
import javax.enterprise.inject.Default;
import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Default
@Stateless
public class GameInfo implements Game {

    @Inject
    private IDBOperations idbOperations;

    private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());

    @Override
    public void getConferences(Long userId) {
        try {
            new KHLBot().execute(SendMessage.builder()
                    .chatId(userId)
                    .text("Привет!🤖\n" +
                            "Я покажу тебе информацию о недавно прошедших и предстоящих играх в КХЛ!🏒\n" +
                            "Для начала выбери конференцию и я покажу тебе список клубов, которые в нее входят")
                    .replyMarkup(getConferenceButtons())
                    .build());
        } catch (TelegramApiException e) {
            logger.log(Level.SEVERE, null, e);
        }
    }

    @Override
    public void sendDefaultMessage(Long userId) {
        try {
            new KHLBot().execute(SendMessage.builder()
                    .chatId(userId)
                    .text("Выбери конференцию и я покажу тебе список клубов, которые в нее входят")
                    .replyMarkup(getConferenceButtons())
                    .build());
        } catch (TelegramApiException e) {
            logger.log(Level.SEVERE, null, e);
        }
    }

    @Override
    public void getClubsByConference(Long userId, String conference) {
        // Get all hockey clubs by Conference
        List<ClubInfo> clubs = idbOperations.getClubsByConference(conference);

        try {
            new KHLBot().execute(SendMessage.builder()
                    .chatId(userId)
                    .text("Вот список клубов конференции " + (conference.equals("west") ? "<b>Запад</b>\n" : "<b>Восток</b>\n") +
                            "Теперь выбери клуб и я покажу тебе информацию об играх")
                    .parseMode(ParseMode.HTML)
                    .replyMarkup(getClubButtons(clubs))
                    .build());
        } catch (TelegramApiException e) {
            logger.log(Level.SEVERE, null, e);
        }
    }

    @Override
    public void sendInfoAboutGames(Long userId, String clubId) {
        // Get info about all clubs
        List<ClubInfo> clubs = idbOperations.getAllClubsInfo();

        // Check clubId from callback data
        if (clubs.stream().noneMatch(clubInfo -> clubInfo.getId().equals(clubId))) {
            logger.log(Level.SEVERE, "clubId - {0} not found", clubId);
            return;
        }

        // Get info about current club by clubId
        ClubInfo currentClub = clubs.stream().filter(clubInfo -> clubInfo.getId().equals(clubId)).findFirst().get();
        // Get games info from KHL site
        String gamesInfo = this.getInfoAboutGames(currentClub);
        String buttonText = "Клубы " + (currentClub.getConference().equals("west") ? "Запада ⬅️" : "Востока ➡️");

        try {
            new KHLBot().execute(SendMessage.builder()
                    .chatId(userId)
                    .text(gamesInfo)
                    .parseMode(ParseMode.HTML)
                    .disableWebPagePreview(true)
                    .replyMarkup(InlineKeyboardMarkup.builder()
                            .keyboardRow(Collections.singletonList(InlineKeyboardButton.builder()
                                    .text(buttonText)
                                    .callbackData(currentClub.getConference())
                                    .build()))
                            .keyboardRow(Collections.singletonList(InlineKeyboardButton.builder()
                                    .text("Конференции 🏆")
                                    .callbackData("conferences")
                                    .build()))
                            .build())
                    .build());
        } catch (TelegramApiException e) {
            logger.log(Level.SEVERE, null, e);
        }
    }

    public String getInfoAboutGames(ClubInfo clubInfo) {
        String url = "https://www.khl.ru/clubs/".concat(clubInfo.getId());

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
        stringBuilder.insert(0, "<b>Информация по клубу " + clubInfo.getName() + " (" + clubInfo.getCity() + ")" + "</b>\n\n");

        // Loop for each game
        games.forEach(game -> {

            // Get VS commands
            String clubOne = game.select("a.card-game__club.card-game__club_left > p.card-game__club-name").text()
                    .concat(" (")
                    .concat(game.select("a.card-game__club.card-game__club_left > p.card-game__club-local").text())
                    .concat(")");

            String clubTwo = game.select("a.card-game__club.card-game__club_right > p.card-game__club-name").text()
                    .concat(" (")
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

        stringBuilder.append("\n").append("Подпишись на наш канал @khl_unofficial - там тебя ждет много интересного!");
        return stringBuilder.toString();
    }

    private InlineKeyboardMarkup getConferenceButtons() {
        return InlineKeyboardMarkup.builder()
                .keyboardRow(Collections.singletonList(InlineKeyboardButton.builder()
                        .text("Запад ⬅️")
                        .callbackData("west")
                        .build()))
                .keyboardRow(Collections.singletonList(InlineKeyboardButton.builder()
                        .text("Восток ➡️")
                        .callbackData("east")
                        .build()))
                .build();
    }

    private InlineKeyboardMarkup getClubButtons(List<ClubInfo> clubs) {
        InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
        List<List<InlineKeyboardButton>> listOfListInlineKey = new ArrayList<>();

        int count = 0;
        List<InlineKeyboardButton> inlineKeyboardButtons = new ArrayList<>();

        for (ClubInfo club : clubs) {
            // Two buttons in a row
            if (count % 2 == 0) {
                listOfListInlineKey.add(inlineKeyboardButtons);
                inlineKeyboardButtons = new ArrayList<>();
            }

            inlineKeyboardButtons.add(
                    InlineKeyboardButton.builder()
                            .text(club.getName() + " (" + club.getCity() + ")")
                            .callbackData(club.getId())
                            .build());

            // If element is last - save row
            if (count == clubs.size() - 1) {
                listOfListInlineKey.add(inlineKeyboardButtons);
            }
            count++;
        }

        inlineKeyboardMarkup.setKeyboard(listOfListInlineKey);
        return inlineKeyboardMarkup;
    }
}
