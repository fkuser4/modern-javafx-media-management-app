package com.mydigitalmedia.mediaapp.service;

import com.mydigitalmedia.mediaapp.MainApplication;
import com.mydigitalmedia.mediaapp.exceptions.EmptyResponseException;
import com.mydigitalmedia.mediaapp.exceptions.NotFoundException;
import com.mydigitalmedia.mediaapp.exceptions.RateLimitedException;
import com.mydigitalmedia.mediaapp.exceptions.ResponseException;
import com.mydigitalmedia.mediaapp.interfaces.AccountInformationProvider;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;


public final class SocialMediaService implements AccountInformationProvider {


    private final String rapidApiKey = "33e450295emsh5370d2bcfad809bp113c0cjsna66bf9ed2232";
    private final String rapidApiHost = "instagram-statistics-api.p.rapidapi.com";

    public SocialMediaService() {
    }


    @Override
    public String getProfile(String profileUrl) throws RateLimitedException {
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder()
                .url("https://instagram-statistics-api.p.rapidapi.com/community?url=" + profileUrl)
                .get()
                .addHeader("X-RapidAPI-Key", rapidApiKey)
                .addHeader("X-RapidAPI-Host", rapidApiHost)
                .build();
        try(Response response = client.newCall(request).execute();){
            int responseCode = response.code();
            MainApplication.logger.info("HTTP response code: " + responseCode);

            if(responseCode == 200){
                Optional<ResponseBody> body = Optional.ofNullable(response.body());
                if (body.isPresent()){
                    return body.get().string();
                } else {
                    MainApplication.logger.error("Error while getting profile: Empty response");
                    throw new EmptyResponseException("Empty response");
                }

            }else if(responseCode == 404){
                MainApplication.logger.error("Error while getting profile: Not found");
                throw new NotFoundException("Error while getting profile: Not found");
            } else if (responseCode == 429) {
                MainApplication.logger.error("Error while getting profile: Too many requests");
                throw new RateLimitedException("Error while getting profile: Too many requests");

            } else {
                String responseBody = response.body() != null ? response.body().string() : "null";
                MainApplication.logger.error("Error while getting profile: Response code " + responseCode + ", Response body: " + responseBody);
                throw new ResponseException("Error while getting profile: Response code " + responseCode);
            }

        } catch (IOException e) {
            MainApplication.logger.error("Error while getting profile", e);
            return null;
        }
    }

    @Override
    public String getFeed(String cid) throws RateLimitedException {

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        LocalDate today = LocalDate.now();
        LocalDate twelveMonthsAgo = today.minusMonths(12);
        String formattedToday = today.format(formatter);
        String formattedTwelveMonthsAgo = twelveMonthsAgo.format(formatter);
        String url = "https://instagram-statistics-api.p.rapidapi.com/posts?cid=" + cid +
                "&from=" + formattedTwelveMonthsAgo + "&to=" + formattedToday + "&sort=date";


        OkHttpClient client = new OkHttpClient();

        Request request = new Request.Builder()
                .url(url)
                .get()
                .addHeader("X-RapidAPI-Key", rapidApiKey)
                .addHeader("X-RapidAPI-Host", rapidApiHost)
                .build();

        try(Response response = client.newCall(request).execute();){
            if( response.code() == 200){
                Optional<ResponseBody> body = Optional.ofNullable(response.body());
                if (body.isPresent()){
                    return body.get().string();
                } else {
                    MainApplication.logger.error("Error while getting feed");
                    throw new EmptyResponseException("Empty response");
                }
            }else if (response.code() == 404) {
                MainApplication.logger.error("Error while getting feed: Not found");
                throw new NotFoundException("Error while getting feed: Not found");
            } else if (response.code() == 429) {
                MainApplication.logger.error("Error while getting profile: Too many requests");
                throw new RateLimitedException("Error while getting profile: Too many requests");

            }else {
                MainApplication.logger.error("Error while getting feed");
                throw new ResponseException("Error while getting feed");
            }

        } catch (IOException e) {
            MainApplication.logger.error("Error while getting feed");
            return null;
        }
    }
}
