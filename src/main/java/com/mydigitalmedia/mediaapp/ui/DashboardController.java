package com.mydigitalmedia.mediaapp.ui;

import com.mydigitalmedia.mediaapp.model.*;
import com.mydigitalmedia.mediaapp.threads.SetChartDataThread;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Path;

import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DashboardController {

    @FXML
    private VBox dashboardTotalEngagementVbox;
    @FXML
    private Label dashboardTotalEngagementNumberLabel;
    @FXML
    private Label dashboardTotalEngagementLabel;
    @FXML
    private FlowPane dashboardTotalDataFlowPane;
    @FXML
    private Label dashboardTotalSharesNumberLabel;
    @FXML
    private VBox dashboardContentVBox;
    @FXML
    private HBox dashboardMainHbox;
    @FXML
    private VBox dashboardButtonVbox;
    @FXML
    private Button dashboardTwitterButton;
    @FXML
    private Button dashboardFacebookButton;
    @FXML
    private Button dashboardInstagramButton;
    @FXML
    private Button dashboardTiktokButton;
    @FXML
    private VBox dashboardDataVbox;
    @FXML
    private Label dashboardTotalLikesLabel;
    @FXML
    private Label dashboardTotalCommentsLabel;
    @FXML
    private Label dashboardTotalSharesLabel;
    @FXML
    private Label dashboardTotalViewsLabel;
    @FXML
    private VBox dashboardTotalLikesVbox;
    @FXML
    private Label dashboardTotalLikesNumberLabel;
    @FXML
    private VBox dashboardTotalCommentsVbox;
    @FXML
    private Label dashboardTotalCommentsNumberLabel;
    @FXML
    private VBox dashboardTotalSharesVbox;
    @FXML
    private VBox dashboardTotalViewsVbox;
    @FXML
    private Label dashboardTotalViewsNumberLabel;

    final CategoryAxis xAxis = new CategoryAxis();
    final NumberAxis yAxis = new NumberAxis();

    private LineChart<String, Number> lineChart = new LineChart<>(xAxis, yAxis);

    String selecetedButton;

    public static ObjectProperty<TwitterData> twitterProperty = new SimpleObjectProperty<>();
    public static ObjectProperty<FacebookData> facebookProperty = new SimpleObjectProperty<>();
    public static ObjectProperty<InstagramData> instagramProperty = new SimpleObjectProperty<>();
    public static ObjectProperty<TikTokData> tiktokProperty = new SimpleObjectProperty<>();

    public void initialize() {


        dashboardContentVBox.getStyleClass().add("dashboardContentVBox");
        dashboardContentVBox.setMinHeight(675);

        dashboardButtonVbox.getStyleClass().add("dashboardButtonVbox");
        dashboardButtonVbox.setMinHeight(630);
        dashboardButtonVbox.setMaxHeight(630);

        dashboardTotalDataFlowPane.setMinWidth(900);

        HBox.setMargin(dashboardButtonVbox, new Insets(25, 0, 0, 30));

        dashboardTwitterButton.fire();

        initData();
        initTotalData();
        initLabel();
        initTotalDataVboxListeners();
        initChart();
        initButtons();
        initUsernamePropertyListener();

    }

    private void initData(){
        Properties properties = new Properties();
        ExecutorService executorService = Executors.newCachedThreadPool();
        try {
            properties.load(new FileReader("config/settings.properties"));
            String twitterUsername = properties.getProperty("twitterUsername");
            String facebookUsername = properties.getProperty("facebookUsername");
            String instagramUsername = properties.getProperty("instagramUsername");
            String tiktokUsername = properties.getProperty("tiktokUsername");

            if(Optional.ofNullable(twitterUsername).isPresent() && !twitterUsername.isEmpty()){
                TwitterData twitterData = new TwitterData(twitterUsername);
                Thread thread = new Thread(new SetChartDataThread<>(twitterData));
                executorService.execute(thread);
            }
            if(Optional.ofNullable(facebookUsername).isPresent() && !facebookUsername.isEmpty()){
                FacebookData facebookData = new FacebookData(facebookUsername);
                Thread thread = new Thread(new SetChartDataThread<>(facebookData));
                executorService.execute(thread);
            }
            if(Optional.ofNullable(instagramUsername).isPresent() && !instagramUsername.isEmpty()){
                InstagramData instagramData = new InstagramData(instagramUsername);
                Thread thread = new Thread(new SetChartDataThread<>(instagramData));
                executorService.execute(thread);
            }
            if(Optional.ofNullable(tiktokUsername).isPresent() && !tiktokUsername.isEmpty()){
                TikTokData tikTokData = new TikTokData(tiktokUsername);
                Thread thread = new Thread(new SetChartDataThread<>(tikTokData));
                executorService.execute(thread);
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }finally {
            executorService.shutdown();
        }
    }

    private void initTotalDataVboxListeners(){

        dashboardTotalLikesVbox.onMouseClickedProperty().set(event -> {
            dashboardTotalLikesVbox.setStyle("-fx-background-color: rgba(92,42,234,0.75); -fx-background-radius: 3; -fx-border-color: #ffffff; -fx-border-width: 2;");
            dashboardTotalCommentsVbox.setStyle("-fx-background-color: rgba(77,147,237,0.75); -fx-background-radius: 3; -fx-border-color: transparent;");
            dashboardTotalSharesVbox.setStyle("-fx-background-color: rgba(84,236,91,0.75); -fx-background-radius: 3; -fx-border-color: transparent;");
            dashboardTotalViewsVbox.setStyle("-fx-background-color: rgba(239,165,82,0.75); -fx-background-radius: 3; -fx-border-color: transparent;");
            dashboardTotalEngagementVbox.setStyle("-fx-background-color: rgba(31,30,30,0.75); -fx-background-radius: 3; -fx-border-color: transparent;");

            if (selecetedButton.equals("twitter") && Optional.ofNullable(twitterProperty.get()).isPresent()){
                lineChart.getData().clear();
                addDataToChart(lineChart, "Likes", twitterProperty.get().getTotalLikesPerMonth(), "rgba(92,42,234,0.75)");
            }

            if (selecetedButton.equals("facebook") && Optional.ofNullable(facebookProperty.get()).isPresent()) {
                lineChart.getData().clear();
                addDataToChart(lineChart, "Likes", facebookProperty.get().getTotalLikesPerMonth(), "rgba(92,42,234,0.75)");
            }

            if (selecetedButton.equals("instagram") && Optional.ofNullable(instagramProperty.get()).isPresent()) {
                lineChart.getData().clear();
                addDataToChart(lineChart, "Likes", instagramProperty.get().getTotalLikesPerMonth(), "rgba(92,42,234,0.75)");
            }

            if (selecetedButton.equals("tiktok") && Optional.ofNullable(tiktokProperty.get()).isPresent()) {
                lineChart.getData().clear();
                addDataToChart(lineChart, "Likes", tiktokProperty.get().getTotalLikesPerMonth(), "rgba(92,42,234,0.75)");
            }
        });

        dashboardTotalCommentsVbox.onMouseClickedProperty().set(event -> {
            dashboardTotalCommentsVbox.setStyle("-fx-background-color: rgba(77,147,237,0.75); -fx-background-radius: 3;  -fx-border-color: #ffffff; -fx-border-width: 2;");
            dashboardTotalLikesVbox.setStyle("-fx-background-color: rgba(92,42,234,0.75); -fx-background-radius: 3; -fx-border-color: transparent;");
            dashboardTotalSharesVbox.setStyle("-fx-background-color: rgba(84,236,91,0.75); -fx-background-radius: 3; -fx-border-color: transparent;");
            dashboardTotalViewsVbox.setStyle("-fx-background-color: rgba(239,165,82,0.75); -fx-background-radius: 3; -fx-border-color: transparent;");
            dashboardTotalEngagementVbox.setStyle("-fx-background-color: rgba(31,30,30,0.75); -fx-background-radius: 3; -fx-border-color: transparent;");

            if (selecetedButton.equals("twitter") && Optional.ofNullable(twitterProperty.get()).isPresent()) {
                lineChart.getData().clear();
                addDataToChart(lineChart, "Comments", twitterProperty.get().getTotalCommentsPerMonth(), "rgba(77,147,237,0.75)");
            }

            if (selecetedButton.equals("facebook") && Optional.ofNullable(facebookProperty.get()).isPresent()) {
                lineChart.getData().clear();
                addDataToChart(lineChart, "Comments", facebookProperty.get().getTotalCommentsPerMonth(), "rgba(77,147,237,0.75)");
            }

            if (selecetedButton.equals("instagram") && Optional.ofNullable(instagramProperty.get()).isPresent()) {
                lineChart.getData().clear();
                addDataToChart(lineChart, "Comments", instagramProperty.get().getTotalCommentsPerMonth(), "rgba(77,147,237,0.75)");
            }

            if (selecetedButton.equals("tiktok") && Optional.ofNullable(tiktokProperty.get()).isPresent()) {
                lineChart.getData().clear();
                addDataToChart(lineChart, "Comments", tiktokProperty.get().getTotalCommentsPerMonth(), "rgba(77,147,237,0.75)");
            }
        });

        dashboardTotalSharesVbox.onMouseClickedProperty().set(event -> {
            dashboardTotalSharesVbox.setStyle("-fx-background-color: rgba(84,236,91,0.75); -fx-background-radius: 3; -fx-border-color: #ffffff; -fx-border-width: 2;");
            dashboardTotalLikesVbox.setStyle("-fx-background-color: rgba(92,42,234,0.75); -fx-background-radius: 3; -fx-border-color: transparent;");
            dashboardTotalCommentsVbox.setStyle("-fx-background-color: rgba(77,147,237,0.75); -fx-background-radius: 3; -fx-border-color: transparent;");
            dashboardTotalViewsVbox.setStyle("-fx-background-color: rgba(239,165,82,0.75); -fx-background-radius: 3; -fx-border-color: transparent;");
            dashboardTotalEngagementVbox.setStyle("-fx-background-color: rgba(31,30,30,0.75); -fx-background-radius: 3; -fx-border-color: transparent;");

            if (selecetedButton.equals("twitter") && Optional.ofNullable(twitterProperty.get()).isPresent()) {
                lineChart.getData().clear();
                addDataToChart(lineChart, "Shares", twitterProperty.get().getTotalSharesPerMonth(), "rgba(84,236,91,0.75)");
            }

            if (selecetedButton.equals("facebook") && Optional.ofNullable(facebookProperty.get()).isPresent()) {
                lineChart.getData().clear();
                addDataToChart(lineChart, "Shares", facebookProperty.get().getTotalSharesPerMonth(), "rgba(84,236,91,0.75)");
            }

            if (selecetedButton.equals("tiktok") && Optional.ofNullable(tiktokProperty.get()).isPresent()) {
                lineChart.getData().clear();
                addDataToChart(lineChart, "Shares", tiktokProperty.get().getTotalSharesPerMonth(), "rgba(84,236,91,0.75)");
            }
        });

        dashboardTotalViewsVbox.onMouseClickedProperty().set(event -> {
            dashboardTotalViewsVbox.setStyle("-fx-background-color: rgba(239,165,82,0.75); -fx-background-radius: 3; -fx-border-color: #ffffff; -fx-border-width: 2;");
            dashboardTotalLikesVbox.setStyle("-fx-background-color: rgba(92,42,234,0.75); -fx-background-radius: 3; -fx-border-color: transparent;");
            dashboardTotalCommentsVbox.setStyle("-fx-background-color: rgba(77,147,237,0.75); -fx-background-radius: 3; -fx-border-color: transparent;");
            dashboardTotalSharesVbox.setStyle("-fx-background-color: rgba(84,236,91,0.75); -fx-background-radius: 3; -fx-border-color: transparent;");
            dashboardTotalEngagementVbox.setStyle("-fx-background-color: rgba(31,30,30,0.75); -fx-background-radius: 3; -fx-border-color: transparent;");


            if (selecetedButton.equals("twitter") && Optional.ofNullable(twitterProperty.get()).isPresent()) {
                lineChart.getData().clear();
                addDataToChart(lineChart, "Views", twitterProperty.get().getTotalViewsPerMonth(), "rgba(239,165,82,0.75)");
            }
            if (selecetedButton.equals("tiktok") && Optional.ofNullable(tiktokProperty.get()).isPresent()) {
                lineChart.getData().clear();
                addDataToChart(lineChart, "Views", tiktokProperty.get().getTotalViewsPerMonth(), "rgba(239,165,82,0.75)");
            }
        });

    }

    private void initUsernamePropertyListener(){
        twitterProperty.addListener((observable, oldValue, newValue) -> {
            if(selecetedButton.equals("twitter")){
                dashboardTwitterButton.fire();
            }

        });

        facebookProperty.addListener((observable, oldValue, newValue) -> {
            if(selecetedButton.equals("facebook")){
                dashboardFacebookButton.fire();
            }
        });

        instagramProperty.addListener((observable, oldValue, newValue) -> {
            if(selecetedButton.equals("instagram")){
                dashboardInstagramButton.fire();
            }
        });

        tiktokProperty.addListener((observable, oldValue, newValue) -> {
            if(selecetedButton.equals("tiktok")){
                dashboardTiktokButton.fire();
            }
        });
    }

    private void initChart(){
        lineChart.setMinHeight(490);
        VBox.setMargin(lineChart, new Insets(10, 0, 0, 0));
        lineChart.getStyleClass().add("lineChart");
        lineChart.setLegendVisible(false);
        dashboardDataVbox.getChildren().add(lineChart);
    }

    private void addDataToChart(LineChart<String, Number> chart, String seriesName, Map<String, Integer> dataMap, String color) {
        if (Optional.ofNullable(dataMap).isPresent()) {
            XYChart.Series<String, Number> series = new XYChart.Series<>();
            series.setName(seriesName);

            dataMap.forEach((key, value) -> series.getData().add(new XYChart.Data<>(key, value)));


            chart.getData().add(series);

            series.getNode().setStyle("-fx-stroke: " + color + ";");
        }
    }

    private void initLabel(){
        dashboardTotalLikesLabel.setText("likes");
        dashboardTotalCommentsLabel.setText("comments");
        dashboardTotalSharesLabel.setText("shares");
        dashboardTotalViewsLabel.setText("views");
        dashboardTotalEngagementLabel.setText("engagement");
        dashboardTotalLikesLabel.getStyleClass().add("dashboardTotalLabel");
        dashboardTotalCommentsLabel.getStyleClass().add("dashboardTotalLabel");
        dashboardTotalSharesLabel.getStyleClass().add("dashboardTotalLabel");
        dashboardTotalViewsLabel.getStyleClass().add("dashboardTotalLabel");
        dashboardTotalEngagementLabel.getStyleClass().add("dashboardTotalLabel");


        dashboardTotalLikesNumberLabel.setText("0");
        dashboardTotalCommentsNumberLabel.setText("0");
        dashboardTotalSharesNumberLabel.setText("0");
        dashboardTotalViewsNumberLabel.setText("0");
        dashboardTotalEngagementNumberLabel.setText("0");
        dashboardTotalLikesNumberLabel.getStyleClass().add("dashboardTotalNumberLabel");
        dashboardTotalCommentsNumberLabel.getStyleClass().add("dashboardTotalNumberLabel");
        dashboardTotalSharesNumberLabel.getStyleClass().add("dashboardTotalNumberLabel");
        dashboardTotalViewsNumberLabel.getStyleClass().add("dashboardTotalNumberLabel");
        dashboardTotalEngagementNumberLabel.getStyleClass().add("dashboardTotalNumberLabel");

        VBox.setMargin(dashboardTotalLikesNumberLabel, new Insets(0, 0, 0, 25));
        VBox.setMargin(dashboardTotalCommentsNumberLabel, new Insets(0, 0, 0, 25));
        VBox.setMargin(dashboardTotalSharesNumberLabel, new Insets(0, 0, 0, 25));
        VBox.setMargin(dashboardTotalViewsNumberLabel, new Insets(0, 0, 0, 25));
        VBox.setMargin(dashboardTotalEngagementNumberLabel, new Insets(0, 0, 0, 25));

        VBox.setMargin(dashboardTotalLikesLabel, new Insets(0, 0, 0, 25));
        VBox.setMargin(dashboardTotalCommentsLabel, new Insets(0, 0, 0, 25));
        VBox.setMargin(dashboardTotalSharesLabel, new Insets(0, 0, 0, 25));
        VBox.setMargin(dashboardTotalViewsLabel, new Insets(0, 0, 0, 25));
        VBox.setMargin(dashboardTotalEngagementLabel, new Insets(0, 0, 0, 25));

    }

    private void initTotalData(){

        dashboardTotalLikesVbox.setStyle("-fx-background-color: rgba(92,42,234,0.75); -fx-background-radius: 3;");
        dashboardTotalLikesVbox.setMinHeight(60);
        dashboardTotalLikesVbox.setMaxHeight(60);
        dashboardTotalLikesVbox.setMinWidth(197);
        dashboardTotalLikesVbox.setMaxWidth(197);

        dashboardTotalCommentsVbox.setStyle("-fx-background-color: rgba(77,147,237,0.75); -fx-background-radius: 3;");
        dashboardTotalCommentsVbox.setMinHeight(60);
        dashboardTotalCommentsVbox.setMaxHeight(60);
        dashboardTotalCommentsVbox.setMinWidth(197);
        dashboardTotalCommentsVbox.setMaxWidth(197);

        dashboardTotalSharesVbox.setStyle("-fx-background-color: rgba(84,236,91,0.75); -fx-background-radius: 3;");
        dashboardTotalSharesVbox.setMinHeight(60);
        dashboardTotalSharesVbox.setMaxHeight(60);
        dashboardTotalSharesVbox.setMinWidth(197);
        dashboardTotalSharesVbox.setMaxWidth(197);

        dashboardTotalViewsVbox.setStyle("-fx-background-color: rgba(239,165,82,0.75); -fx-background-radius: 3;");
        dashboardTotalViewsVbox.setMinHeight(60);
        dashboardTotalViewsVbox.setMaxHeight(60);
        dashboardTotalViewsVbox.setMinWidth(197);
        dashboardTotalViewsVbox.setMaxWidth(197);

        dashboardTotalEngagementVbox.setStyle("-fx-background-color: rgba(31,30,30,0.75); -fx-background-radius: 3;");
        dashboardTotalEngagementVbox.setMinHeight(60);
        dashboardTotalEngagementVbox.setMaxHeight(60);
        dashboardTotalEngagementVbox.setMinWidth(197);
        dashboardTotalEngagementVbox.setMaxWidth(197);

        FlowPane.setMargin(dashboardTotalEngagementVbox, new Insets(25, 0, 0, 25));
        FlowPane.setMargin(dashboardTotalLikesVbox, new Insets(25, 0, 0, 25));
        FlowPane.setMargin(dashboardTotalCommentsVbox, new Insets(25, 0, 0, 25));
        FlowPane.setMargin(dashboardTotalSharesVbox, new Insets(25, 0, 0, 25));
        FlowPane.setMargin(dashboardTotalViewsVbox, new Insets(25, 0, 0, 25));

    }

    private void initButtons(){
        dashboardTwitterButton.getStyleClass().add("dashboardButton");
        dashboardFacebookButton.getStyleClass().add("dashboardButton");
        dashboardInstagramButton.getStyleClass().add("dashboardButton");
        dashboardTiktokButton.getStyleClass().add("dashboardButton");

        dashboardTwitterButton.setText("Twitter");
        dashboardFacebookButton.setText("Facebook");
        dashboardInstagramButton.setText("Instagram");
        dashboardTiktokButton.setText("TikTok");

        dashboardTwitterButton.setMinHeight(45);
        dashboardFacebookButton.setMinHeight(45);
        dashboardInstagramButton.setMinHeight(45);
        dashboardTiktokButton.setMinHeight(45);
        dashboardTwitterButton.maxHeight(45);
        dashboardFacebookButton.maxHeight(45);
        dashboardInstagramButton.maxHeight(45);
        dashboardTiktokButton.maxHeight(45);


        dashboardTwitterButton.setMinWidth(180);
        dashboardFacebookButton.setMinWidth(180);
        dashboardInstagramButton.setMinWidth(180);
        dashboardTiktokButton.setMinWidth(180);



    }

    @FXML
    private void handleTwitterButtonAction() {
        if (!dashboardTwitterButton.getStyleClass().contains("dashboardButtonActive")) {
            dashboardTwitterButton.getStyleClass().remove("dashboardButton");
            dashboardTwitterButton.getStyleClass().add("dashboardButtonActive");
        }
        if (dashboardFacebookButton.getStyleClass().contains("dashboardButtonActive")) {
            dashboardFacebookButton.getStyleClass().remove("dashboardButtonActive");
            dashboardFacebookButton.getStyleClass().add("dashboardButton");
        }
        if (dashboardInstagramButton.getStyleClass().contains("dashboardButtonActive")) {
            dashboardInstagramButton.getStyleClass().remove("dashboardButtonActive");
            dashboardInstagramButton.getStyleClass().add("dashboardButton");
        }
        if (dashboardTiktokButton.getStyleClass().contains("dashboardButtonActive")) {
            dashboardTiktokButton.getStyleClass().remove("dashboardButtonActive");
            dashboardTiktokButton.getStyleClass().add("dashboardButton");
        }

        dashboardTotalLikesVbox.setStyle("-fx-background-color: rgba(92,42,234,0.75); -fx-background-radius: 3; -fx-border-color: #ffffff; -fx-border-width: 2;");
        dashboardTotalCommentsVbox.setStyle("-fx-background-color: rgba(77,147,237,0.75); -fx-background-radius: 3; -fx-border-color: transparent;");
        dashboardTotalSharesVbox.setStyle("-fx-background-color: rgba(84,236,91,0.75); -fx-background-radius: 3; -fx-border-color: transparent;");
        dashboardTotalViewsVbox.setStyle("-fx-background-color: rgba(239,165,82,0.75); -fx-background-radius: 3; -fx-border-color: transparent;");
        dashboardTotalEngagementVbox.setStyle("-fx-background-color: rgba(31,30,30,0.75); -fx-background-radius: 3; -fx-border-color: transparent;");


        if (Optional.ofNullable(twitterProperty.get()).isPresent()){

            lineChart.getData().clear();

            dashboardTotalLikesNumberLabel.setText(String.valueOf(twitterProperty.get().getLikes()));
            dashboardTotalCommentsNumberLabel.setText(String.valueOf(twitterProperty.get().getComments()));
            dashboardTotalSharesNumberLabel.setText(String.valueOf(twitterProperty.get().getShares()));
            dashboardTotalViewsNumberLabel.setText(String.valueOf(twitterProperty.get().getViews()));
            dashboardTotalEngagementNumberLabel.setText(String.valueOf(twitterProperty.get().calculateEngagementRate()));


            lineChart.getData().clear();
            addDataToChart(lineChart, "Likes", twitterProperty.get().getTotalLikesPerMonth(), "rgba(92,42,234,0.75)");
        }else{
            dashboardTotalLikesNumberLabel.setText("0");
            dashboardTotalCommentsNumberLabel.setText("0");
            dashboardTotalSharesNumberLabel.setText("0");
            dashboardTotalViewsNumberLabel.setText("0");
            dashboardTotalEngagementNumberLabel.setText("0");
        }

        selecetedButton = "twitter";
        dashboardTotalLikesVbox.setVisible(true);
        dashboardTotalCommentsVbox.setVisible(true);
        dashboardTotalSharesVbox.setVisible(true);
        dashboardTotalViewsVbox.setVisible(true);
        dashboardTotalEngagementVbox.setVisible(true);

    }

    @FXML
    private void handleFacebookButtonAction() {
        if (!dashboardFacebookButton.getStyleClass().contains("dashboardButtonActive")) {
            dashboardFacebookButton.getStyleClass().remove("dashboardButton");
            dashboardFacebookButton.getStyleClass().add("dashboardButtonActive");
        }

        if (dashboardTwitterButton.getStyleClass().contains("dashboardButtonActive")) {
            dashboardTwitterButton.getStyleClass().remove("dashboardButtonActive");
            dashboardTwitterButton.getStyleClass().add("dashboardButton");
        }

        if (dashboardInstagramButton.getStyleClass().contains("dashboardButtonActive")) {
            dashboardInstagramButton.getStyleClass().remove("dashboardButtonActive");
            dashboardInstagramButton.getStyleClass().add("dashboardButton");
        }

        if (dashboardTiktokButton.getStyleClass().contains("dashboardButtonActive")) {
            dashboardTiktokButton.getStyleClass().remove("dashboardButtonActive");
            dashboardTiktokButton.getStyleClass().add("dashboardButton");
        }

        dashboardTotalLikesVbox.setVisible(true);
        dashboardTotalCommentsVbox.setVisible(true);
        dashboardTotalSharesVbox.setVisible(true);
        dashboardTotalViewsVbox.setVisible(false);
        dashboardTotalEngagementVbox.setVisible(false);

        dashboardTotalLikesVbox.setStyle("-fx-background-color: rgba(92,42,234,0.75); -fx-background-radius: 3; -fx-border-color: #ffffff; -fx-border-width: 2;");
        dashboardTotalCommentsVbox.setStyle("-fx-background-color: rgba(77,147,237,0.75); -fx-background-radius: 3; -fx-border-color: transparent;");
        dashboardTotalSharesVbox.setStyle("-fx-background-color: rgba(84,236,91,0.75); -fx-background-radius: 3; -fx-border-color: transparent;");
        dashboardTotalViewsVbox.setStyle("-fx-background-color: rgba(239,165,82,0.75); -fx-background-radius: 3; -fx-border-color: transparent;");
        dashboardTotalEngagementVbox.setStyle("-fx-background-color: rgba(31,30,30,0.75); -fx-background-radius: 3; -fx-border-color: transparent;");

        if (Optional.ofNullable(facebookProperty.get()).isPresent()){

            lineChart.getData().clear();

            dashboardTotalLikesNumberLabel.setText(String.valueOf(facebookProperty.get().getLikes()));
            dashboardTotalCommentsNumberLabel.setText(String.valueOf(facebookProperty.get().getComments()));
            dashboardTotalSharesNumberLabel.setText(String.valueOf(facebookProperty.get().getShares()));



            lineChart.getData().clear();
            addDataToChart(lineChart, "Likes", facebookProperty.get().getTotalLikesPerMonth(), "rgba(92,42,234,0.75)");
        }else {
            dashboardTotalLikesNumberLabel.setText("0");
            dashboardTotalCommentsNumberLabel.setText("0");
            dashboardTotalSharesNumberLabel.setText("0");
        }

        selecetedButton = "facebook";

    }

    @FXML
    private void handleInstagramButtonAction() {
        if (!dashboardInstagramButton.getStyleClass().contains("dashboardButtonActive")) {
            dashboardInstagramButton.getStyleClass().remove("dashboardButton");
            dashboardInstagramButton.getStyleClass().add("dashboardButtonActive");
        }

        if (dashboardTwitterButton.getStyleClass().contains("dashboardButtonActive")) {
            dashboardTwitterButton.getStyleClass().remove("dashboardButtonActive");
            dashboardTwitterButton.getStyleClass().add("dashboardButton");
        }

        if (dashboardFacebookButton.getStyleClass().contains("dashboardButtonActive")) {
            dashboardFacebookButton.getStyleClass().remove("dashboardButtonActive");
            dashboardFacebookButton.getStyleClass().add("dashboardButton");
        }

        if (dashboardTiktokButton.getStyleClass().contains("dashboardButtonActive")) {
            dashboardTiktokButton.getStyleClass().remove("dashboardButtonActive");
            dashboardTiktokButton.getStyleClass().add("dashboardButton");
        }

        dashboardTotalLikesVbox.setVisible(true);
        dashboardTotalCommentsVbox.setVisible(true);
        dashboardTotalSharesVbox.setVisible(false);
        dashboardTotalViewsVbox.setVisible(false);
        dashboardTotalEngagementVbox.setVisible(false);

        dashboardTotalLikesVbox.setStyle("-fx-background-color: rgba(92,42,234,0.75); -fx-background-radius: 3; -fx-border-color: #ffffff; -fx-border-width: 2;");
        dashboardTotalCommentsVbox.setStyle("-fx-background-color: rgba(77,147,237,0.75); -fx-background-radius: 3; -fx-border-color: transparent;");
        dashboardTotalSharesVbox.setStyle("-fx-background-color: rgba(84,236,91,0.75); -fx-background-radius: 3; -fx-border-color: transparent;");
        dashboardTotalViewsVbox.setStyle("-fx-background-color: rgba(239,165,82,0.75); -fx-background-radius: 3; -fx-border-color: transparent;");
        dashboardTotalEngagementVbox.setStyle("-fx-background-color: rgba(31,30,30,0.75); -fx-background-radius: 3; -fx-border-color: transparent;");

        if (Optional.ofNullable(instagramProperty.get()).isPresent()){

            lineChart.getData().clear();

            dashboardTotalLikesNumberLabel.setText(String.valueOf(instagramProperty.get().getLikes()));
            dashboardTotalCommentsNumberLabel.setText(String.valueOf(instagramProperty.get().getComments()));


            lineChart.getData().clear();
            addDataToChart(lineChart, "Likes", instagramProperty.get().getTotalLikesPerMonth(), "rgba(92,42,234,0.75)");
        }else {
            dashboardTotalLikesNumberLabel.setText("0");
            dashboardTotalCommentsNumberLabel.setText("0");
        }

        selecetedButton = "instagram";

    }

    @FXML
    private void handleTiktokButtonAction() {
        if (!dashboardTiktokButton.getStyleClass().contains("dashboardButtonActive")) {
            dashboardTiktokButton.getStyleClass().remove("dashboardButton");
            dashboardTiktokButton.getStyleClass().add("dashboardButtonActive");
        }
        if (dashboardTwitterButton.getStyleClass().contains("dashboardButtonActive")) {
            dashboardTwitterButton.getStyleClass().remove("dashboardButtonActive");
            dashboardTwitterButton.getStyleClass().add("dashboardButton");
        }
        if (dashboardFacebookButton.getStyleClass().contains("dashboardButtonActive")) {
            dashboardFacebookButton.getStyleClass().remove("dashboardButtonActive");
            dashboardFacebookButton.getStyleClass().add("dashboardButton");
        }
        if (dashboardInstagramButton.getStyleClass().contains("dashboardButtonActive")) {
            dashboardInstagramButton.getStyleClass().remove("dashboardButtonActive");
            dashboardInstagramButton.getStyleClass().add("dashboardButton");
        }

        dashboardTotalLikesVbox.setVisible(true);
        dashboardTotalCommentsVbox.setVisible(true);
        dashboardTotalSharesVbox.setVisible(true);
        dashboardTotalViewsVbox.setVisible(true);
        dashboardTotalEngagementVbox.setVisible(true);

        dashboardTotalLikesVbox.setStyle("-fx-background-color: rgba(92,42,234,0.75); -fx-background-radius: 3; -fx-border-color: #ffffff; -fx-border-width: 2;");
        dashboardTotalCommentsVbox.setStyle("-fx-background-color: rgba(77,147,237,0.75); -fx-background-radius: 3; -fx-border-color: transparent;");
        dashboardTotalSharesVbox.setStyle("-fx-background-color: rgba(84,236,91,0.75); -fx-background-radius: 3; -fx-border-color: transparent;");
        dashboardTotalViewsVbox.setStyle("-fx-background-color: rgba(239,165,82,0.75); -fx-background-radius: 3; -fx-border-color: transparent;");
        dashboardTotalEngagementVbox.setStyle("-fx-background-color: rgba(31,30,30,0.75); -fx-background-radius: 3; -fx-border-color: transparent;");

        if (Optional.ofNullable(tiktokProperty.get()).isPresent()){

            lineChart.getData().clear();

            dashboardTotalLikesNumberLabel.setText(String.valueOf(tiktokProperty.get().getLikes()));
            dashboardTotalCommentsNumberLabel.setText(String.valueOf(tiktokProperty.get().getComments()));
            dashboardTotalSharesNumberLabel.setText(String.valueOf(tiktokProperty.get().getShares()));
            dashboardTotalViewsNumberLabel.setText(String.valueOf(tiktokProperty.get().getViews()));
            dashboardTotalEngagementNumberLabel.setText(String.valueOf(tiktokProperty.get().calculateEngagementRate()));

            lineChart.getData().clear();
            addDataToChart(lineChart, "Likes", tiktokProperty.get().getTotalLikesPerMonth(), "rgba(92,42,234,0.75)");
        }else {
            dashboardTotalLikesNumberLabel.setText("0");
            dashboardTotalCommentsNumberLabel.setText("0");
            dashboardTotalSharesNumberLabel.setText("0");
            dashboardTotalViewsNumberLabel.setText("0");
            dashboardTotalEngagementNumberLabel.setText("0");
        }

        selecetedButton = "tiktok";
    }
}
