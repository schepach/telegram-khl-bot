package ru.khl.bot;


import org.apache.log4j.Logger;
import org.telegram.telegrambots.TelegramApiException;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.Message;
import org.telegram.telegrambots.api.objects.Update;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import ru.khl.bot.constants.Constants;
import ru.khl.utils.Connection;

import java.io.IOException;

/**
 * Created by alexey on 01.11.16.
 */
public class KHLBot extends TelegramLongPollingBot implements Runnable {

    private static final Logger LOGGER = Logger.getLogger(KHLBot.class.getSimpleName());


    public void onUpdateReceived(Update update) {
        Message message = update.getMessage();

        if (message != null && message.hasText()) {

            LOGGER.info("FirstName: " + message.getFrom().getFirstName());
            LOGGER.info("LastName: " + message.getFrom().getLastName());
            LOGGER.info("UserName: " + message.getFrom().getUserName());
            LOGGER.info("UserId: " + message.getFrom().getId());
            LOGGER.info("CommandInput: " + message.getText());


            try {
                if (message.getText().toLowerCase().equals(Constants.START)) {
                    sendMsg(message, Constants.START_TEXT);
                } else if (message.getText().toLowerCase().equals(Constants.HELP)) {
                    sendMsg(message, Constants.HELP_TEXT);
                } else if (message.getText().toLowerCase().equals(Constants.HC_CLUBS_LIST_WEST)) {
                    sendMsg(message, Constants.getWESTTeams());
                } else if (message.getText().toLowerCase().equals(Constants.HC_CLUBS_LIST_EAST)) {
                    sendMsg(message, Constants.getEASTTeams());
                }

                //TODO: В sendRequest передавать message.getText() (+вынести в отдельный метод)

                // WEST Conference
                else if (message.getText().toUpperCase().equals(Constants.SKA)) {
                    sendMsg(message, Connection.sendRequest(Constants.SKA));
                } else if (message.getText().toUpperCase().equals(Constants.CSKA)) {
                    sendMsg(message, Connection.sendRequest(Constants.CSKA));
                } else if (message.getText().toUpperCase().equals(Constants.TORPEDO)) {
                    sendMsg(message, Connection.sendRequest(Constants.TORPEDO));
                } else if (message.getText().toUpperCase().equals(Constants.LOKOMOTIV)) {
                    sendMsg(message, Connection.sendRequest(Constants.LOKOMOTIV));
                } else if (message.getText().toUpperCase().equals(Constants.DINAMO_MSK)) {
                    sendMsg(message, Connection.sendRequest(Constants.DINAMO_MSK));
                } else if (message.getText().toUpperCase().equals(Constants.DINAMO_MINSK)) {
                    sendMsg(message, Connection.sendRequest(Constants.DINAMO_MINSK));
                } else if (message.getText().toUpperCase().equals(Constants.SOCHI)) {
                    sendMsg(message, Connection.sendRequest(Constants.SOCHI));
                } else if (message.getText().toUpperCase().equals(Constants.VITYAZ)) {
                    sendMsg(message, Connection.sendRequest(Constants.VITYAZ));
                } else if (message.getText().toUpperCase().equals(Constants.SPARTAK)) {
                    sendMsg(message, Connection.sendRequest(Constants.SPARTAK));
                } else if (message.getText().toUpperCase().equals(Constants.MEDVESCAK)) {
                    sendMsg(message, Connection.sendRequest(Constants.MEDVESCAK));
                } else if (message.getText().toUpperCase().equals(Constants.SLOVAN)) {
                    sendMsg(message, Connection.sendRequest(Constants.SLOVAN));
                } else if (message.getText().toUpperCase().equals(Constants.SEVERSTAL)) {
                    sendMsg(message, Connection.sendRequest(Constants.SEVERSTAL));
                } else if (message.getText().toUpperCase().equals(Constants.DINAMO_RIGA)) {
                    sendMsg(message, Connection.sendRequest(Constants.DINAMO_RIGA));
                } else if (message.getText().toUpperCase().equals(Constants.JOKERIT)) {
                    sendMsg(message, Connection.sendRequest(Constants.JOKERIT));
                }

                // EAST Conference
                else if (message.getText().toUpperCase().equals(Constants.METALLURG_MAGNITOGORSK)) {
                    sendMsg(message, Connection.sendRequest(Constants.METALLURG_MAGNITOGORSK));
                } else if (message.getText().toUpperCase().equals(Constants.AVANGARD)) {
                    sendMsg(message, Connection.sendRequest(Constants.AVANGARD));
                } else if (message.getText().toUpperCase().equals(Constants.AK_BARS)) {
                    sendMsg(message, Connection.sendRequest(Constants.AK_BARS));
                } else if (message.getText().toUpperCase().equals(Constants.SALAVAT_YULAEV)) {
                    sendMsg(message, Connection.sendRequest(Constants.SALAVAT_YULAEV));
                } else if (message.getText().toUpperCase().equals(Constants.TRAKTOR)) {
                    sendMsg(message, Connection.sendRequest(Constants.TRAKTOR));
                } else if (message.getText().toUpperCase().equals(Constants.ADMIRAL)) {
                    sendMsg(message, Connection.sendRequest(Constants.ADMIRAL));
                } else if (message.getText().toUpperCase().equals(Constants.KUNLUN_RED_STAR)) {
                    sendMsg(message, Connection.sendRequest(Constants.KUNLUN_RED_STAR));
                } else if (message.getText().toUpperCase().equals(Constants.NEFTEKHIMIK)) {
                    sendMsg(message, Connection.sendRequest(Constants.NEFTEKHIMIK));
                } else if (message.getText().toUpperCase().equals(Constants.SIBIR)) {
                    sendMsg(message, Connection.sendRequest(Constants.SIBIR));
                } else if (message.getText().toUpperCase().equals(Constants.BARYS)) {
                    sendMsg(message, Connection.sendRequest(Constants.BARYS));
                } else if (message.getText().toUpperCase().equals(Constants.AVTOMOBILIST)) {
                    sendMsg(message, Connection.sendRequest(Constants.AVTOMOBILIST));
                } else if (message.getText().toUpperCase().equals(Constants.LADA)) {
                    sendMsg(message, Connection.sendRequest(Constants.LADA));
                } else if (message.getText().toUpperCase().equals(Constants.AMUR)) {
                    sendMsg(message, Connection.sendRequest(Constants.AMUR));
                } else if (message.getText().toUpperCase().equals(Constants.UGRA)) {
                    sendMsg(message, Connection.sendRequest(Constants.UGRA));
                } else if (message.getText().toUpperCase().equals(Constants.METALLURG_NOVOKUZNETSK)) {
                    sendMsg(message, Connection.sendRequest(Constants.METALLURG_NOVOKUZNETSK));
                } else {
                    sendMsg(message, Constants.ERROR_OTHER_INPUT);
                }


            } catch (IOException ex) {
                LOGGER.error(Constants.UNEXPECTED_ERROR.concat(ex.getMessage()) + ex);
            }
        }


    }

    public String getBotUsername() {
        return "botname";
    }

    public String getBotToken() {
        return "bottoken";
    }


    private void sendMsg(Message message, String text) {
        SendMessage sendMessage = new SendMessage();
        sendMessage.setText(text);
        sendMessage.setChatId(message.getChatId().toString());
        sendMessage.setReplayToMessageId(message.getMessageId());
        try {
            sendMessage(sendMessage);
        } catch (TelegramApiException ex) {
            LOGGER.error(Constants.UNEXPECTED_ERROR.concat(ex.getMessage() + ex));
        }
    }

    @Override
    public void run() {
        ScheduledTask scheduledTask = new ScheduledTask();
        scheduledTask.run();
    }
}
