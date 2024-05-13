package com.mydigitalmedia.mediaapp.threads;

import com.mydigitalmedia.mediaapp.model.*;
import com.mydigitalmedia.mediaapp.ui.DashboardController;
import javafx.application.Platform;

public class SetChartDataThread<T extends SocialMediaData> implements Runnable {

    private T socialMediaData;

    public SetChartDataThread(T socialMediaData) {
        this.socialMediaData = socialMediaData;
    }

    @Override
    public void run() {
       socialMediaData.fetchData();

       Platform.runLater(()->{
           if(socialMediaData instanceof TikTokData e){
               DashboardController.tiktokProperty.set(e);
           }

           if (socialMediaData instanceof TwitterData e) {
               DashboardController.twitterProperty.set(e);
           }
           if (socialMediaData instanceof InstagramData e) {
               DashboardController.instagramProperty.set(e);
           }

           if (socialMediaData instanceof FacebookData e) {
               DashboardController.facebookProperty.set(e);
           }
       });

    }
}
