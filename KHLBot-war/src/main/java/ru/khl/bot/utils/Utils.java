package ru.khl.bot.utils;

import com.vk.api.sdk.objects.wall.WallpostAttachment;
import com.vk.api.sdk.objects.wall.WallpostAttachmentType;
import org.apache.log4j.Logger;
import ru.khl.bot.model.Item;

public class Utils {

    private static final Logger LOGGER = Logger.getLogger(Utils.class.getSimpleName());

    public static void checkAttachmentTypeAndMapping(Item wallItem, WallpostAttachment attachment) {

        if (attachment.getType() != null && (attachment.getType() == WallpostAttachmentType.DOC && attachment.getDoc() != null)
                || (attachment.getType() == WallpostAttachmentType.AUDIO && attachment.getAudio() != null)
                || (attachment.getType() == WallpostAttachmentType.PHOTO && attachment.getPhoto() != null)
                || (attachment.getType() == WallpostAttachmentType.VIDEO && attachment.getVideo() != null)
                || (attachment.getType() == WallpostAttachmentType.LINK && attachment.getLink() != null)) {


            switch (attachment.getType()) {

                case PHOTO:
                    LOGGER.info("KHL_PHOTO TYPE...");

                    String photoLink = "";

                    if (attachment.getPhoto().getPhoto75() != null) {
                        photoLink = attachment.getPhoto().getPhoto75();
                    }
                    if (attachment.getPhoto().getPhoto130() != null) {
                        photoLink = attachment.getPhoto().getPhoto130();
                    }
                    if (attachment.getPhoto().getPhoto604() != null) {
                        photoLink = attachment.getPhoto().getPhoto604();
                    }
                    if (attachment.getPhoto().getPhoto807() != null) {
                        photoLink = attachment.getPhoto().getPhoto807();
                    }
                    if (attachment.getPhoto().getPhoto1280() != null) {
                        photoLink = attachment.getPhoto().getPhoto1280();
                    }
                    if (attachment.getPhoto().getPhoto2560() != null) {
                        photoLink = attachment.getPhoto().getPhoto2560();
                    }
                    wallItem.setLink(photoLink);
                    wallItem.setPostType(Item.PostType.KHL_PHOTO);
                    LOGGER.info("KHL_LINK = " + wallItem.getLink());
                    LOGGER.info("PostType = " + wallItem.getPostType().value());
                    break;
                case VIDEO:
                    LOGGER.info("KHL_VIDEO TYPE...");
                    if (wallItem.getTitle() != null && !wallItem.getTitle().isEmpty()) {
                        wallItem.setTitle(wallItem.getTitle().concat("\n").concat(attachment.getVideo().getTitle()));
                    } else {
                        wallItem.setTitle(attachment.getVideo().getTitle());
                    }
                    wallItem.setLink("http://vk.com/video".concat(attachment.getVideo().getOwnerId() + "_" + attachment.getVideo().getId()));
                    wallItem.setPostType(Item.PostType.KHL_VIDEO);
                    LOGGER.info("KHL_LINK = " + wallItem.getLink());
                    LOGGER.info("PostType = " + wallItem.getPostType().value());
                    LOGGER.info("Title = " + wallItem.getTitle());
                    break;
                case DOC:
                    LOGGER.info("DOC TYPE...");
                    String url = attachment.getDoc().getUrl().substring(0, attachment.getDoc().getUrl().indexOf("?"));
                    wallItem.setTitle(attachment.getDoc().getTitle());
                    wallItem.setCaption(attachment.getDoc().getTitle());
                    wallItem.setLink(url);
                    wallItem.setPostType(Item.PostType.KHL_FILE);
                    LOGGER.info("KHL_LINK = " + wallItem.getLink());
                    LOGGER.info("PostType = " + wallItem.getPostType().value());
                    LOGGER.info("Title = " + wallItem.getTitle());
                    LOGGER.info("Caption = " + wallItem.getCaption());
                    break;
                case LINK:
                    if (wallItem.getTitle() != null && !wallItem.getTitle().isEmpty()) {
                        wallItem.setTitle(wallItem.getTitle().concat("\n").concat(attachment.getLink().getTitle()));
                    } else {
                        wallItem.setTitle(attachment.getLink().getTitle());
                    }
                    wallItem.setLink(attachment.getLink().getUrl());
                    wallItem.setPostType(Item.PostType.KHL_LINK);
                    LOGGER.info("KHL_LINK = " + wallItem.getLink());
                    LOGGER.info("PostType = " + wallItem.getPostType().value());
                    LOGGER.info("Title = " + wallItem.getTitle());
                    break;
            }
        }
    }
}
