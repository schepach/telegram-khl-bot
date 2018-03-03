package ru.khl.bot.schedulers;

import org.apache.log4j.Logger;
import org.json.JSONException;
import org.telegram.telegrambots.api.methods.send.SendDocument;
import org.telegram.telegrambots.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.api.methods.send.SendMessage;
import org.telegram.telegrambots.api.objects.media.InputMedia;
import org.telegram.telegrambots.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.exceptions.TelegramApiRequestException;
import ru.khl.bot.KHLBot;
import ru.khl.bot.constants.Constants;
import ru.khl.bot.model.Item;
import ru.khl.bot.model.MessageStructure;
import ru.khl.bot.model.WallItem;
import ru.khl.bot.utils.Connection;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;

/**
 * Created by Alexey on 13.12.2016.
 */

public class ScheduledVKInfo extends TimerTask {

    private static final Logger LOGGER = Logger.getLogger(ScheduledVKInfo.class.getSimpleName());
    private static final String CHAT_ID = "@KHL_Info";

    @Override
    public void run() {

        try {
            MessageStructure messageStructure = Connection.getInfo();

            if (messageStructure != null && messageStructure.getWallItems() != null) {

                for (WallItem wallItem : messageStructure.getWallItems()) {
                    if (wallItem.getItemList() != null && !wallItem.getItemList().isEmpty()) {
                        List<InputMediaPhoto> photoList = new ArrayList<>();
                        List<InputMedia> inputMediaList = new ArrayList<>();
                        String titleWithPhoto = "";
                        boolean captionFlag = false;

                        for (Item item : wallItem.getItemList()) {
                            if (item.getPostType() != null && item.getPostType().value() != null && !item.getPostType().value().isEmpty()) {
                                switch (item.getPostType()) {
                                    case KHL_FILE:
                                        LOGGER.info("type KHL_FILE...");
                                        LOGGER.info("Caption = " + item.getCaption());
                                        LOGGER.info("Title = " + item.getTitle());
                                        URL urlOfFile = new URL(item.getLink());
                                        InputStream streamOfFile = urlOfFile.openStream();
                                        new KHLBot().sendDocument(new SendDocument().setChatId(CHAT_ID)
                                                .setCaption(item.getCaption())
                                                .setNewDocument(item.getTitle(), streamOfFile));
                                        break;
                                    case KHL_PHOTO:
                                        double random = Math.random();
                                        LOGGER.info("type KHL_PHOTO...");
                                        LOGGER.info("KHL_LINK OF ITEM = " + item.getLink());

                                        if (titleWithPhoto.isEmpty()) {
                                            titleWithPhoto = item.getTitle() != null && !item.getTitle().isEmpty() ? item.getTitle() : "";
                                        }
                                        InputMediaPhoto inputMediaPhoto = new InputMediaPhoto();
                                        URL urlOfPhoto = new URL(item.getLink());
                                        InputStream streamOfPhoto = urlOfPhoto.openStream();
                                        inputMediaPhoto.setMedia(streamOfPhoto, "name".concat(String.valueOf(random)));
                                        if (!captionFlag) {
                                            if (titleWithPhoto.length() <= 200) {
                                                inputMediaPhoto.setCaption(titleWithPhoto);
                                                captionFlag = true;
                                            }
                                        }
                                        photoList.add(inputMediaPhoto);
                                        break;
                                    default:
                                        LOGGER.info("OTHER_POST TYPE...");
                                        String title = item.getTitle() != null && !item.getTitle().isEmpty() ? item.getTitle() : "";
                                        String link = item.getLink() != null && !item.getLink().isEmpty() ? item.getLink() : "";
                                        new KHLBot().sendMessage(new SendMessage().setChatId(CHAT_ID).setText(title.concat("\n").concat(link).concat("\n")));
                                        break;
                                }
                            }
                        }

                        if (!photoList.isEmpty()) {
                            if (!captionFlag) {
                                if (titleWithPhoto != null && !titleWithPhoto.isEmpty()) {
                                    new KHLBot().sendMessage(new SendMessage().setChatId(CHAT_ID).setText(titleWithPhoto));
                                }
                            }
                            LOGGER.info("SEND GROUP KHL_PHOTO...");
                            inputMediaList.addAll(photoList);
                            SendMediaGroup sendMediaGroup = new SendMediaGroup();
                            sendMediaGroup.setChatId(CHAT_ID);
                            sendMediaGroup.setMedia(inputMediaList);
                            new KHLBot().sendMediaGroup(sendMediaGroup);
                        }
                    }
                }
            }

        } catch (TelegramApiRequestException | JSONException | IOException e) {
            LOGGER.info(Constants.UNEXPECTED_ERROR.concat(e.getMessage() + e));
        } catch (Exception ex) {
            LOGGER.info("EXCEPTION: ", ex);
        }
    }
}