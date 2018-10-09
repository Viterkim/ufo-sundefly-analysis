package service;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import javax.net.ssl.HttpsURLConnection;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Created by schelde on 13/05/17.
 */
public class FlightsService {
    private static final FlightsService singleton = new FlightsService();
    private ThreadPoolExecutor fetchOtherAirlinesPool = null;
    private ReentrantLock reentrantLock = new ReentrantLock(true);

    private FlightsService() {
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        this.fetchOtherAirlinesPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(4, threadFactory);
    }

    public void setAndStartAirlineFetch(JsonArray jsonArray, String url, String from, String to, String date, int tickets) {
        OtherAirlineFetch otherAirlineFetch = new OtherAirlineFetch();
        if (to == null || to.equals("")) {
            otherAirlineFetch.url = String.format("%s/%s/%s/%s", url, from, date, tickets);
        } else {
            otherAirlineFetch.url = String.format("%s/%s/%s/%s/%s", url, from, to, date, tickets);
        }
        otherAirlineFetch.jsonArray = jsonArray;
        otherAirlineFetch.lock = reentrantLock;
        this.fetchOtherAirlinesPool.execute(otherAirlineFetch);
    }

    public static FlightsService getSingleton() {
        return singleton;
    }

    public void killAllAirlineFetches() {
        this.fetchOtherAirlinesPool.shutdown();
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        this.fetchOtherAirlinesPool = (ThreadPoolExecutor) Executors.newFixedThreadPool(4, threadFactory);
    }

    private class OtherAirlineFetch implements Runnable {

        public String url = "";
        public JsonArray jsonArray;
        public Gson gson;
        public ReentrantLock lock;

        @Override
        public void run() {
            BufferedReader bufferedReader = null;
            if (url.contains("https")) {
                System.out.println("...Found https in url.");
                HttpsURLConnection httpsURLConnection = null;
                try {
                    httpsURLConnection = (HttpsURLConnection) new URL(this.url).openConnection();
                } catch (IOException e) {
                    System.out.println("...Tried to open a https connection");
                    e.printStackTrace();
                }

                try {
                    bufferedReader = new BufferedReader(new InputStreamReader(httpsURLConnection.getInputStream()));
                } catch (IOException e) {
                    System.out.printf("...Tried to get input stream from https connection");
                    e.printStackTrace();
                }
            } else {
                System.out.println("...Found http in url.");
                HttpURLConnection httpURLConnection = null;
                try {
                    httpURLConnection = (HttpURLConnection) new URL(this.url).openConnection();
                } catch (IOException e) {
                    System.out.println("...Tried to open a http connection");
                    e.printStackTrace();
                }

                try {
                    bufferedReader = new BufferedReader(new InputStreamReader(httpURLConnection.getInputStream()));
                } catch (IOException e) {
                    System.out.printf("...Tried to get input stream from http connection");
                    e.printStackTrace();
                    return;
                }
            }
            StringBuilder stringBuilder = new StringBuilder();
            try {
                if (bufferedReader != null) {
                    reentrantLock.lock();
                    while (bufferedReader.ready()) {
                        try {
                            stringBuilder.append(bufferedReader.readLine());
                        } catch (IOException e) {
                            System.out.println("...Tried to readline from input stream as URLConnection");
                            e.printStackTrace();
                        }
                    }
                    reentrantLock.unlock();
                }
            } catch (Exception e) {
                reentrantLock.unlock();
                System.out.println("...Tried to loop bufferedReader, but failed.");
                e.printStackTrace();
            }
            String json = stringBuilder.toString();
            if (json == null || json.equals("")) {
                System.err.println("...Json received from airline, was nothing");
                return;
            }
            try {
                reentrantLock.lock();
                JsonObject jsonObject = getGson().fromJson(json,JsonObject.class);
                if (jsonObject != null && !jsonObject.toString().equals("")) {
                    System.out.println("...Adding flights to JsonArray, from: " + this.url);
                    jsonArray.add(jsonObject);
                }
                reentrantLock.unlock();
            } catch (Exception exception) {
                reentrantLock.unlock();
                exception.printStackTrace();
            }
        }

        private Gson getGson() {
            if (gson == null) {
                gson = new GsonBuilder().setPrettyPrinting().create();
            }
            return gson;
        }
    }
}
