package ru.khl.bot.bean.scheduler;

import com.vk.api.sdk.client.actors.ServiceActor;
import common.vk.model.UserInfo;
import common.vk.utils.RedisEntity;

import javax.ejb.Stateless;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Alexey on 13.12.2016.
 */

@Stateless
public class KHLVKInfoScheduler {

    private final Logger logger = Logger.getLogger(this.getClass().getSimpleName());
    private final int appId = Integer.parseInt(RedisEntity.getInstance().getElement("khl_clientId"));
    private final int groupId = Integer.parseInt(RedisEntity.getInstance().getElement("khl_groupId"));
    private final String accessToken = RedisEntity.getInstance().getElement("khl_accessToken");

    public void run() {
        logger.log(Level.SEVERE, "Start KHLVKInfoScheduler...");

        try {
            UserInfo userInfo = new UserInfo();
            userInfo.setVkOwnerId(groupId);
            userInfo.setVkServiceActor(new ServiceActor(appId, accessToken));
            userInfo.setBotAlias("KHL");
            userInfo.setVkPostCount(10);
            TaskExecuter.execute(userInfo);
        } catch (Exception ex) {
            logger.log(Level.SEVERE, null, ex);
        }
    }
}