package com.mydigitalmedia.mediaapp.interfaces;

import com.mydigitalmedia.mediaapp.exceptions.RateLimitedException;
import com.mydigitalmedia.mediaapp.service.SocialMediaService;

public sealed interface AccountInformationProvider permits SocialMediaService {


    String getProfile(String profileUrl) throws RateLimitedException;

    String getFeed(String cid) throws RateLimitedException;

}
