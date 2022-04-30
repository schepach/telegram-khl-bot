package ru.khl.bot.bean.scheduler.vk;

import com.vk.api.sdk.client.actors.ServiceActor;
import common.vk.connection.VKConnection;
import common.vk.model.Item;
import common.vk.model.MessageStructure;
import common.vk.model.UserInfo;
import common.vk.model.WallItem;
import common.vk.utils.RedisEntity;
import org.telegram.telegrambots.meta.api.methods.send.*;
import org.telegram.telegrambots.meta.api.objects.InputFile;
import org.telegram.telegrambots.meta.api.objects.media.InputMedia;
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.meta.exceptions.TelegramApiRequestException;
import ru.khl.bot.KHLBot;

import javax.ejb.Singleton;
import javax.enterprise.inject.Default;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Alexey on 13.12.2016.
 */
@Default
@Singleton
public class VKInfoScheduler implements IVKScheduler {

    private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());
    private final int appId = Integer.parseInt(RedisEntity.getInstance().getElement("khl_clientId"));
    private final int groupId = Integer.parseInt(RedisEntity.getInstance().getElement("khl_groupId"));
    private final String accessToken = RedisEntity.getInstance().getElement("khl_accessToken");
    private final String chatId = RedisEntity.getInstance().getElement("khl_chatId");
    private final String alias = "KHL";
    private final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();

    @Override
    public void sendVKPosts() {
        logger.log(Level.SEVERE, "Start KHLVKInfoScheduler...");

            /* 1. Alias of telegram bot for redis storage (for example: KHL)
               2. Group id (public page id, negative value)
               3. VK service actor with application id and access token (service key)
               4. As many posts as you need from VK public page
            */
        UserInfo userInfo = new UserInfo("KHL",
                groupId,
                new ServiceActor(appId, accessToken),
                10);

        try {
            MessageStructure messageStructure = VKConnection.getVKWallInfo(userInfo);

            if (messageStructure == null
                    || messageStructure.getWallItems() == null
                    || messageStructure.getWallItems().isEmpty()) {
                logger.log(Level.SEVERE, "No new messages from VK...");
                return;
            }

            long delay = 20;
            long countElement = 1;
            for (WallItem wallItem : messageStructure.getWallItems()) {

                if (wallItem.getItemList() == null
                        || wallItem.getItemList().isEmpty())
                    continue;

                executorService.schedule(() ->
                        runTask(wallItem), delay * countElement, TimeUnit.SECONDS);

                countElement++;
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Alias - " + userInfo.getBotAlias(), ex);
        }
    }

    private void runTask(WallItem wallItem) {

        List<InputMediaPhoto> photoList = null;
        InputMediaPhoto inputMediaPhoto;
        InputStream inputStream;
        InputFile inputFile;
        String titleWithPhoto = "";
        boolean captionFlag = false;
        boolean moreTwoAttach = false;

        try {
            if (wallItem.getItemList().size() > 1) {
                photoList = new ArrayList<>();
                moreTwoAttach = true;
            }

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
                        inputFile.setMedia(inputStream, "image.gif");

                        String caption = item.getTitle();
                        if (caption.length() > 1024) {
                            caption = caption.substring(0, 1024);
                        }

                        logger.log(Level.INFO, "Alias - {0}; Send gif...", alias);
                        new KHLBot().execute(SendVideo.builder()
                                .chatId(chatId)
                                .caption(caption)
                                .video(inputFile)
                                .build());
                        break;
                    case FILE:
                        URL urlOfFile = new URL(item.getLink());
                        inputStream = urlOfFile.openStream();
                        inputFile = new InputFile();
                        inputFile.setMedia(inputStream, item.getTitle() != null ? item.getTitle() : "fileName");

                        logger.log(Level.INFO, "Alias - {0}; Send file...", alias);
                        new KHLBot().execute(SendDocument.builder()
                                .chatId(chatId)
                                .caption(item.getCaption())
                                .document(inputFile)
                                .build());
                        break;
                    case PHOTO:

                        if (item.getLink() == null)
                            break;

                        URL urlOfPhoto = new URL(item.getLink());
                        inputStream = urlOfPhoto.openStream();

                        double random = Math.random();

                        if (titleWithPhoto.isEmpty()) {
                            titleWithPhoto = item.getTitle() != null && !item.getTitle().isEmpty() ? item.getTitle() : "";
                        }

                        if (moreTwoAttach) {
                            inputMediaPhoto = new InputMediaPhoto();
                            inputMediaPhoto.setMedia(inputStream, "name".concat(String.valueOf(random)));
                            if (!captionFlag) {
                                if (titleWithPhoto.length() <= 1024) {
                                    inputMediaPhoto.setCaption(titleWithPhoto);
                                    captionFlag = true;
                                }
                            }
                            photoList.add(inputMediaPhoto);
                        } else {
                            inputFile = new InputFile();
                            inputFile.setMedia(inputStream, "photo.png");

                            SendPhoto sendPhoto = new SendPhoto();
                            sendPhoto.setChatId(chatId);

                            if (titleWithPhoto.length() <= 1024) {
                                sendPhoto.setCaption(titleWithPhoto);
                                captionFlag = true;
                            }
                            if (!captionFlag) {
                                new KHLBot().execute(SendMessage.builder()
                                        .chatId(chatId)
                                        .text(titleWithPhoto)
                                        .build());
                            }
                            sendPhoto.setPhoto(inputFile);
                            logger.log(Level.INFO, "Alias - {0}; Send photo...", alias);
                            new KHLBot().execute(sendPhoto);
                        }
                        break;
                    default:
                        String title = item.getTitle() != null && !item.getTitle().isEmpty() ? item.getTitle() : "";
                        String link = item.getLink() != null && !item.getLink().isEmpty() ? item.getLink() : "";

                        logger.log(Level.INFO, "Alias - {0}; Send default message...", alias);
                        new KHLBot().execute(SendMessage.builder()
                                .chatId(chatId)
                                .text(title.concat("\n")
                                        .concat(link)
                                        .concat("\n"))
                                .build());
                        break;
                }
            }

            // Если в посте более одного медиафайла
            if (photoList != null && !photoList.isEmpty())
                this.sendMediaGroup(photoList, captionFlag, titleWithPhoto);

        } catch (TelegramApiException ex) {
            logger.log(Level.SEVERE, "Alias - " + alias, ex);

            if (ex instanceof TelegramApiRequestException) {
                if (((TelegramApiRequestException) ex).getErrorCode() != null
                        && ((TelegramApiRequestException) ex).getErrorCode() == 429) {

                    // Если Too many requests, то выполняем повтор через указанное в ошибке кол-во секунд
                    logger.log(Level.SEVERE, "Too many requests exception. Retry after {0} seconds", ((TelegramApiRequestException) ex).getParameters().getRetryAfter());
                    executorService.schedule(() ->
                            runTask(wallItem), ((TelegramApiRequestException) ex).getParameters().getRetryAfter(), TimeUnit.SECONDS);
                    return;
                }
            }
            logger.log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Alias - " + alias, ex);
        }
    }

    private void sendMediaGroup(List<InputMediaPhoto> photoList, boolean captionFlag, String titleWithPhoto) throws TelegramApiException {
        List<InputMedia> inputMediaList;
        if (!captionFlag) {
            SendMessage sendMessage = new SendMessage();
            sendMessage.setChatId(chatId);
            // Если текст не превышает 4096 символов, то отправляем текст как есть
            // Иначе разбиваем сообщение на 2 части, ровно пополам
            if (titleWithPhoto.length() <= 4096) {
                sendMessage.setText(titleWithPhoto);
                new KHLBot().execute(sendMessage);
            } else {
                String title = titleWithPhoto.substring(0, titleWithPhoto.length() / 2);
                sendMessage.setText(title);
                new KHLBot().execute(sendMessage);
                title = titleWithPhoto.substring(titleWithPhoto.length() / 2);
                sendMessage.setText(title);
                new KHLBot().execute(sendMessage);
            }
        }
        //  Если кол-во медиа от 2 до 10 шт, то отправляем пачкой
        if (photoList.size() > 1 && photoList.size() <= 10) {
            inputMediaList = new ArrayList<>(photoList);
            logger.log(Level.INFO, "Alias - {0}; Send sendMediaGroup...", alias);
            new KHLBot().execute(SendMediaGroup.builder()
                    .chatId(chatId)
                    .medias(inputMediaList)
                    .build());
        } else {
            photoList.forEach(elem -> {
                try {
                    InputFile file = new InputFile();
                    file.setMedia(elem.getNewMediaStream(), "mediaFile.jpg");
                    logger.log(Level.INFO, "Alias - {0}; Send photo...", alias);
                    new KHLBot().execute(SendPhoto.builder()
                            .chatId(chatId)
                            .photo(file)
                            .caption(elem.getCaption())
                            .build());
                } catch (Exception ex) {
                    logger.log(Level.SEVERE, null, ex);
                }
            });
        }
    }
}