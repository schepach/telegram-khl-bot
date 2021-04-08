package ru.khl.bot.bean.scheduler;

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

public class KHLTaskExecuter {

    private static final ScheduledExecutorService executorService = Executors.newSingleThreadScheduledExecutor();
    private static final Logger logger = Logger.getLogger(KHLTaskExecuter.class.getName());
    private static final String chatId = RedisEntity.getInstance().getElement("khl_chatId");

    public static void execute(UserInfo userInfo) {
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
                        runTask(wallItem, userInfo.getBotAlias()), delay * countElement, TimeUnit.SECONDS);

                countElement++;
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Alias - " + userInfo.getBotAlias(), ex);
        }
    }

    public static void runTask(WallItem wallItem, String alias) {

        List<InputMediaPhoto> photoList = null;
        List<InputMedia> inputMediaList;
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

                        SendVideo sendVideo = new SendVideo();
                        sendVideo.setChatId(chatId);
                        sendVideo.setCaption(item.getTitle());
                        sendVideo.setVideo(inputFile);
                        logger.log(Level.INFO, "Alias - {0}; Send gif...", alias);
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
                        logger.log(Level.INFO, "Alias - {0}; Send file...", alias);
                        new KHLBot().execute(sendDocument);
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
                                SendMessage sendMessage = new SendMessage();
                                sendMessage.setChatId(chatId);
                                sendMessage.setText(titleWithPhoto);
                                new KHLBot().execute(sendMessage);
                            }
                            sendPhoto.setPhoto(inputFile);
                            logger.log(Level.INFO, "Alias - {0}; Send photo...", alias);
                            new KHLBot().execute(sendPhoto);
                        }
                        break;
                    default:
                        String title = item.getTitle() != null && !item.getTitle().isEmpty() ? item.getTitle() : "";
                        String link = item.getLink() != null && !item.getLink().isEmpty() ? item.getLink() : "";
                        SendMessage sendMessage = new SendMessage();
                        sendMessage.setChatId(chatId);
                        sendMessage.setText(title.concat("\n").concat(link).concat("\n"));
                        logger.log(Level.INFO, "Alias - {0}; Send default message...", alias);
                        new KHLBot().execute(sendMessage);
                        break;
                }
            }

            if (photoList != null && !photoList.isEmpty()) {

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
                //  Если кол-во медиа ДО 10 шт
                if (photoList.size() > 1 && photoList.size() <= 10) {
                    inputMediaList = new ArrayList<>(photoList);
                    SendMediaGroup sendMediaGroup = new SendMediaGroup();
                    sendMediaGroup.setChatId(chatId);
                    sendMediaGroup.setMedias(inputMediaList);
                    logger.log(Level.INFO, "Alias - {0}; Send sendMediaGroup...", alias);
                    new KHLBot().execute(sendMediaGroup);
                } else {
                    photoList.forEach(elem -> {
                        try {
                            InputFile file = new InputFile();
                            file.setMedia(elem.getNewMediaStream(), "mediaFile.jpg");
                            SendPhoto sendPhoto = new SendPhoto();
                            sendPhoto.setChatId(chatId);
                            sendPhoto.setPhoto(file);
                            sendPhoto.setCaption(elem.getCaption());
                            logger.log(Level.INFO, "Alias - {0}; Send photo...", alias);
                            new KHLBot().execute(sendPhoto);
                        } catch (Exception ex) {
                            logger.log(Level.SEVERE, null, ex);
                        }
                    });
                }
            }
        } catch (TelegramApiException ex) {
            logger.log(Level.SEVERE, "Alias - " + alias, ex);

            if (ex instanceof TelegramApiRequestException) {

                if (((TelegramApiRequestException) ex).getErrorCode() != null) {

                    // Если Too many requests, то выполняем повтор через указанное в ошибке кол-во секунд
                    if (((TelegramApiRequestException) ex).getErrorCode() == 429) {
                        logger.log(Level.SEVERE, "Too many requests exception. Retry after {0} seconds", ((TelegramApiRequestException) ex).getParameters().getRetryAfter());

                        executorService.schedule(() ->
                                runTask(wallItem, alias), ((TelegramApiRequestException) ex).getParameters().getRetryAfter(), TimeUnit.SECONDS);
                    }
                    // Если Bad request, то выполняем повтор через 30 сек
                    else if (((TelegramApiRequestException) ex).getErrorCode() == 400) {
                        logger.log(Level.SEVERE, "Bad request exception. Retry after 30 seconds...");
                        executorService.schedule(() ->
                                runTask(wallItem, alias), 30, TimeUnit.SECONDS);
                    }
                }
            }
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Alias - " + alias, ex);
        }
    }
}