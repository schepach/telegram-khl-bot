package ru.khl.bot.bean.scheduler;

import com.vk.api.sdk.client.actors.ServiceActor;
import common.vk.connection.VKConnection;
import common.vk.model.Item;
import common.vk.model.MessageStructure;
import common.vk.model.UserInfo;
import common.vk.model.WallItem;
import org.telegram.telegrambots.meta.api.methods.send.SendDocument;
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.methods.send.SendVideo;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import ru.khl.bot.KHLBot;

import javax.ejb.Stateless;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Alexey on 13.12.2016.
 */

@Stateless
public class KHLVKInfoScheduler {

    private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());
    private final String chatId = "@khl_unofficial";


    public void run() {
        logger.log(Level.SEVERE, "Start KHLVKInfoScheduler...");

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

            if (messageStructure == null
                    || messageStructure.getWallItems() == null
                    || messageStructure.getWallItems().isEmpty()) {
                logger.log(Level.SEVERE, "No new messages from VK...");
                return;
            }

            for (WallItem wallItem : messageStructure.getWallItems()) {
                if (wallItem.getItemList() == null
                        || wallItem.getItemList().isEmpty())
                    continue;

                List<InputMediaPhoto> photoList = new ArrayList<>();
                List<InputMedia> inputMediaList = new ArrayList<>();
                String titleWithPhoto = "";
                boolean captionFlag = false;
                InputStream inputStream;
                InputFile inputFile;

                for (Item item : wallItem.getItemList()) {
                    if (item.getPostType() == null
                            || item.getPostType().value() == null
                            || item.getPostType().value().isEmpty())
                        continue;

                    switch (item.getPostType()) {
                        case GIF:
                            URL urlGif = new URL(item.getLink());
                            inputStream = urlGif.openStream();
                            inputFile = new InputFile();
                            inputFile.setMedia(inputStream, "file.gif");
                            SendVideo sendVideo = new SendVideo();
                            sendVideo.setChatId(chatId);
                            sendVideo.setCaption(item.getTitle());
                            sendVideo.setVideo(inputFile);
                            new KHLBot().execute(sendVideo);
                            break;
                        case FILE:
                            URL urlOfFile = new URL(item.getLink());
                            inputStream = urlOfFile.openStream();
                            inputFile = new InputFile();
                            inputFile.setMedia(inputStream, item.getTitle() != null ? item.getTitle() : "fileName");
                            SendDocument sendDocument = new SendDocument();
                            sendDocument.setChatId(chatId);
                            sendDocument.setCaption(item.getCaption());
                            sendDocument.setDocument(inputFile);
                            new KHLBot().execute(sendDocument);
                            break;
                        case PHOTO:
                            if (item.getLink() == null)
                                break;

                            double random = Math.random();
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
                            String title = item.getTitle() != null && !item.getTitle().isEmpty() ? item.getTitle() : "";
                            String link = item.getLink() != null && !item.getLink().isEmpty() ? item.getLink() : "";
                            SendMessage sendMessage = new SendMessage();
                            sendMessage.setChatId(chatId);
                            sendMessage.setText(title.concat("\n").concat(link).concat("\n"));
                            new KHLBot().execute(sendMessage);
                            break;
                    }
                }

                if (!photoList.isEmpty()) {
                    if (!captionFlag) {
                        SendMessage sendMessage = new SendMessage();
                        sendMessage.setChatId(chatId);
                        sendMessage.setText(titleWithPhoto);
                        new KHLBot().execute(sendMessage);
                    }
                    inputMediaList.addAll(photoList);
                    SendMediaGroup sendMediaGroup = new SendMediaGroup();
                    sendMediaGroup.setChatId(chatId);
                    sendMediaGroup.setMedias(inputMediaList);
                    new KHLBot().execute(sendMediaGroup);
                }
            }

        } catch (Exception ex) {
            this.logger.log(Level.SEVERE, "KHLVKInfoScheduler exception: ", ex);
        }
    }
}