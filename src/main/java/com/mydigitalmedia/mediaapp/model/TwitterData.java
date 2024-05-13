package com.mydigitalmedia.mediaapp.model;

import com.mydigitalmedia.mediaapp.MainApplication;
import com.mydigitalmedia.mediaapp.exceptions.RateLimitedException;
import com.mydigitalmedia.mediaapp.interfaces.Engageable;
import com.mydigitalmedia.mediaapp.service.SocialMediaService;
import com.mydigitalmedia.mediaapp.utils.DataSorter;
import org.json.JSONArray;
import org.json.JSONObject;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.TreeMap;

public class TwitterData extends SocialMediaData implements Engageable {

    private Integer likes = 0;
    private Integer shares = 0;
    private Integer comments = 0;
    private Integer views = 0;

    private Map<String, Integer> totalLikesPerMonth = new TreeMap<>(new DataSorter());
    private Map<String, Integer> totalCommentsPerMonth = new TreeMap<>(new DataSorter());
    private Map<String, Integer> totalSharesPerMonth = new TreeMap<>(new DataSorter());
    private Map<String, Integer> totalViewsPerMonth = new TreeMap<>(new DataSorter());

    public TwitterData(String profileName) {
        this.profileName = profileName;
    }

    @Override
    public void fetchData() {
        SocialMediaService socialMediaService = new SocialMediaService();
        String profileData = null;
        try {
            profileData = socialMediaService.getProfile("https%3A%2F%2Ftwitter.com%2F" + profileName);
        } catch (RateLimitedException e) {
            MainApplication.logger.error(e.getMessage());
        }
        if (Optional.ofNullable(profileData).isPresent()) {
            JSONObject obj  = new JSONObject(profileData);
            cid = obj.getJSONObject("data").getString("cid");
            fetchFeed();
        }
    }

    private void fetchFeed() {
        SocialMediaService socialMediaService = new SocialMediaService();
        String feedData = null;
        try {
            feedData = socialMediaService.getFeed(cid);
        } catch (RateLimitedException e) {
            MainApplication.logger.error(e.getMessage());
        }
        if (Optional.ofNullable(feedData).isPresent()) {
            JSONObject jsonObject  = new JSONObject(feedData);
            parseData(jsonObject);
        }
    }

    private void parseData(JSONObject jsonObject) {
        JSONArray posts = jsonObject.getJSONObject("data").getJSONArray("posts");

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        LocalDate today = LocalDate.now(ZoneId.systemDefault());
        LocalDate twelveMonthsAgo = today.minusMonths(12);

        for (int i = 0; i < posts.length(); i++) {
            JSONObject post = posts.getJSONObject(i);
            LocalDate postDate = LocalDate.parse(post.getString("date"), formatter);
            if (postDate.isAfter(twelveMonthsAgo) || postDate.isEqual(twelveMonthsAgo)) {
                String monthKey = postDate.getMonthValue() + "-" + postDate.getYear();
                int comments = post.optInt("comments", 0);
                int shares = post.optInt("rePosts", 0);
                int likes = post.optInt("likes", 0);
                int views = post.optInt("views", 0);

                this.comments += comments;
                this.shares += shares;
                this.likes += likes;
                this.views += views;

                totalLikesPerMonth.merge(monthKey, likes, Integer::sum);
                totalSharesPerMonth.merge(monthKey, shares, Integer::sum);
                totalCommentsPerMonth.merge(monthKey, comments, Integer::sum);
                totalViewsPerMonth.merge(monthKey, views, Integer::sum);
            }
        }

    }

    public BigDecimal calculateEngagementRate() {
        if (views == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal totalEngagement = new BigDecimal(likes + shares + comments);

        BigDecimal totalViews = new BigDecimal(views);
        return totalEngagement.divide(totalViews, 4, BigDecimal.ROUND_HALF_UP);
    }


    public Integer getLikes() {
        return likes;
    }

    public Integer getShares() {
        return shares;
    }

    public Integer getComments() {
        return comments;
    }

    public Integer getViews() {
        return views;
    }

    public Map<String, Integer> getTotalLikesPerMonth() {
        return totalLikesPerMonth;
    }

    public Map<String, Integer> getTotalCommentsPerMonth() {
        return totalCommentsPerMonth;
    }

    public Map<String, Integer> getTotalSharesPerMonth() {
        return totalSharesPerMonth;
    }

    public Map<String, Integer> getTotalViewsPerMonth() {
        return totalViewsPerMonth;
    }


}
