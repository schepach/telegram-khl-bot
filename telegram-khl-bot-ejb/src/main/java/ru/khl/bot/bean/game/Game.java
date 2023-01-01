package ru.khl.bot.bean.game;

public interface Game {

    void getConferences(Long userId);

    void sendDefaultMessage(Long userId);

    void getClubsByConference(Long userId, String conference);

    void sendInfoAboutGames(Long userId, String clubId);
}
