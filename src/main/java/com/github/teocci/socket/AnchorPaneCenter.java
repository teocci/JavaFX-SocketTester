package com.github.teocci.socket;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2019-May-17
 */
public class AnchorPaneCenter extends Application
{
    public static final int HEIGHT = 800;
    public static final int WIDTH = 600;

    public static final int MENU_BUTTONS_START_X = 0;
    public static final int MENU_BUTTONS_START_Y = 0;

    private Stage mainStage;
    private Scene mainScene;
    private BorderPane mainPane;
    private VBox menuPane;
    private List<Button> menuButtons;

    @Override
    public void start(Stage primaryStage)
    {
        mainStage = primaryStage;

        menuButtons = new ArrayList<>();

        mainPane = new BorderPane();

        menuPane = new VBox();
        menuPane.setAlignment(Pos.CENTER);
        menuPane.setSpacing(10);
        menuPane.setPadding(new Insets(50));
        menuPane.setStyle("-fx-border-style: solid;"
                + "-fx-border-width: 1;"
                + "-fx-border-color: black");

        mainPane.setCenter(menuPane);

        mainScene = new Scene(mainPane, WIDTH, HEIGHT);

        mainStage.setScene(mainScene);
        mainStage.setFullScreen(true);
        mainStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);

        mainStage.show();

        createButtons();
    }

    private void addMenuButton(Button button)
    {
        menuButtons.add(button);
        menuPane.getChildren().add(button);
    }


    private void createButtons()
    {
        createStartButton();
        createScoreButton();
        createQuitButton();
    }

    private void createStartButton()
    {
        Button startButton = new Button("PLAY");
        addMenuButton(startButton);
    }

    private void createScoreButton()
    {
        Button scoreButton = new Button("SCORE");
        addMenuButton(scoreButton);
    }

    private void createQuitButton()
    {
        Button quitButton = new Button("QUIT");
        addMenuButton(quitButton);
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}
