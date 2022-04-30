package ru.khl.bot.utils;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Created by Alexey on 09.01.2017.
 */
public class BotHelper {

    private static final Logger logger = Logger.getLogger(BotHelper.class.getSimpleName());

    public static int getResponseCode(String url) throws IOException {
        logger.log(Level.INFO, "ConnectTo - {0}", url);
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(url);
        HttpResponse response = client.execute(request);
        logger.log(Level.INFO, "Response Code - {0} ", response.getStatusLine().getStatusCode());

        return response.getStatusLine().getStatusCode();
    }
}
