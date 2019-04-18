package ru.khl.bot.schedulers;

import com.vk.api.sdk.client.actors.ServiceActor;
import common.vk.connection.VKConnection;
import common.vk.model.Item;
import common.vk.model.MessageStructure;
import common.vk.model.UserInfo;
import common.vk.model.WallItem;
import org.json.JSONException;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import ru.khl.bot.KHLBot;
import ru.khl.bot.constants.Constants;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Alexey on 13.12.2016.
 */

public class ScheduledVKInfo extends TimerTask {

    private static final Logger LOGGER = Logger.getLogger(ScheduledVKInfo.class.getSimpleName());
    private static final String CHAT_ID = "@KHL_Info";

    @Override
    public void run() {

        try {
            UserInfo userInfo = new UserInfo();
            //vk ownerId of public page
            userInfo.setVkOwnerId(-1);
            //your vk service actor
            userInfo.setVkServiceActor(null);
            //your alias of telegram bot for redis storage (for example: KHL)
            userInfo.setBotAlias("");
            //as many posts as you need from vk public page
            userInfo.setVkPostCount(30);

            MessageStructure messageStructure = VKConnection.getVKWallInfo(userInfo);

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
                                    case GIF:
                                        URL URLGIF = new URL(item.getLink());
                                        InputStream streamOfGIF = URLGIF.openStream();
                                        new KHLBot().execute(new SendVideo().setChatId(CHAT_ID)
                                                .setCaption(item.getTitle())
                                                .setVideo("title", streamOfGIF));
                                        break;
                                    case FILE:
                                        LOGGER.info("type FILE...");
                                        LOGGER.info("Caption = " + item.getCaption());
                                        LOGGER.info("Title = " + item.getTitle());
                                        URL urlOfFile = new URL(item.getLink());
                                        InputStream streamOfFile = urlOfFile.openStream();
                                        new KHLBot().execute(new SendDocument().setChatId(CHAT_ID)
                                                .setCaption(item.getCaption())
                                                .setDocument(item.getTitle(), streamOfFile));
                                        break;
                                    case PHOTO:
                                        double random = Math.random();
                                        LOGGER.info("type PHOTO...");
                                        LOGGER.info("LINK OF ITEM = " + item.getLink());

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
                                        new KHLBot().execute(new SendMessage().setChatId(CHAT_ID).setText(title.concat("\n").concat(link).concat("\n")));
                                        break;
                                }
                            }
                        }

                        if (!photoList.isEmpty()) {
                            if (!captionFlag) {
                                new KHLBot().execute(new SendMessage().setChatId(CHAT_ID).setText(titleWithPhoto));
                            }
                            LOGGER.info("SEND GROUP PHOTO...");
                            inputMediaList.addAll(photoList);
                            SendMediaGroup sendMediaGroup = new SendMediaGroup();
                            sendMediaGroup.setChatId(CHAT_ID);
                            sendMediaGroup.setMedia(inputMediaList);
                            new KHLBot().execute(sendMediaGroup);
                        }
                    }
                }
            }

        } catch (TelegramApiRequestException | JSONException | IOException e) {
            LOGGER.log(Level.INFO, Constants.UNEXPECTED_ERROR.concat(e.getMessage() + e), e);
        } catch (Exception ex) {
            LOGGER.log(Level.INFO, "EXCEPTION", ex);
        }
    }
}