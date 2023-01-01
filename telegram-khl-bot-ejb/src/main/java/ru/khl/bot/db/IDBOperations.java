package ru.khl.bot.db;

import ru.khl.bot.model.ClubInfo;

import java.util.List;

public interface IDBOperations {

    List<ClubInfo> getClubsByConference(String conference);

    List<ClubInfo> getAllClubsInfo();

}
