package ru.khl.bot.db;

import ru.khl.bot.model.ClubInfo;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.enterprise.inject.Default;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@Default
@Singleton
@Startup
public class DBImpl implements IDBOperations {

    private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());
    private Connection connection;

    @PostConstruct
    public void initialize() {
        try {
            logger.log(Level.SEVERE, "Initial datasource and connection...");
            DataSource dataSource = (DataSource) new InitialContext().lookup("java:jboss/datasources/SqliteDS");
            connection = dataSource.getConnection();
            logger.log(Level.SEVERE, "Initial done.");
        } catch (SQLException | NamingException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }

    @Override
    public List<ClubInfo> getClubsByConference(String conference) {
        logger.log(Level.INFO, "Get clubs by conference - {0}", conference);
        String selectQuery = "SELECT * FROM KHL_CLUBS WHERE conference = ?;";
        List<ClubInfo> clubs = null;
        try (PreparedStatement statement = connection.prepareStatement(selectQuery)) {
            statement.setString(1, conference);
            ResultSet resultSet = statement.executeQuery();
            clubs = new ArrayList<>();
            while (resultSet.next()) {
                clubs.add(new ClubInfo(
                        resultSet.getString("club_name"),
                        resultSet.getString("club_id"),
                        resultSet.getString("conference"),
                        resultSet.getString("city")
                ));
            }
            clubs.forEach(club -> {
                logger.log(Level.INFO, "Club name from database - {0}", club.getName());
            });
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        return clubs;
    }

    @Override
    public List<ClubInfo> getAllClubsInfo() {
        logger.log(Level.INFO, "Get all clubs info ...");
        String selectQuery = "SELECT * FROM KHL_CLUBS;";
        List<ClubInfo> clubs = null;
        try (PreparedStatement statement = connection.prepareStatement(selectQuery)) {
            ResultSet resultSet = statement.executeQuery();
            clubs = new ArrayList<>();
            while (resultSet.next()) {
                clubs.add(new ClubInfo(
                        resultSet.getString("club_name"),
                        resultSet.getString("club_id"),
                        resultSet.getString("conference"),
                        resultSet.getString("city")
                ));
            }
            clubs.forEach(club -> {
                logger.log(Level.INFO, "Club id from database - {0}", club.getId());
            });
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
        return clubs;
    }

    @PreDestroy
    public void cleanup() {
        try {
            logger.log(Level.SEVERE, "Close connection...");
            connection.close();
            connection = null;
            logger.log(Level.SEVERE, "Connection was closed...");
        } catch (SQLException ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }
}