package com.github.teocci.socket;

import com.github.teocci.socket.gui.LogTextArea;
import com.github.teocci.socket.net.TcpClient;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.concurrent.TimeUnit;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2017-Jun-21
 */
public class SocketClientTester extends Application
{
    private final Button btnUpdate = new Button("GW Update");
    private final Button btnCommand = new Button("Command");
    private final Button btnDisconnect = new Button("Disconnect");

    private final BorderPane mainContainer = new BorderPane();
    private final ToolBar toolBar = new ToolBar();

    private final AnchorPane mapAnchor = new AnchorPane();
    private final TextArea mapScroll = new LogTextArea();

    private TcpClient thread;

    @Override
    public void start(Stage primaryStage)
    {
        String server = "1.246.220.44";

        mainContainer.setTop(toolBar);
        mainContainer.setCenter(addMapAnchor());

        toolBar.getItems().addAll(btnUpdate, btnCommand, btnDisconnect);

        Scene mapScene = new Scene(mainContainer, 600, 400);
        primaryStage.setScene(mapScene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(we -> {
            if (thread != null) {
                System.out.println("Stage is closing");
                thread.stop();
                shutdown();
            }
        });

        btnUpdate.setOnAction(t -> {
            if (thread != null) {
                thread.sendUpdate();
            }
        });

        btnCommand.setOnAction(t -> {
            if (thread != null) {
                thread.sendCommand();
            }
        });

        btnDisconnect.setOnAction(t -> {
            if (thread != null) {
                thread.sendDisconnect();
            }
        });

        try {
            System.out.println("Loading contents of URL: " + server);
            // create a new thread object
            thread = new TcpClient(server);
            thread.start();
        } catch (Exception e) {
            e.printStackTrace();
            thread = null;
            shutdown();
        }
    }

    private void shutdown()
    {
        try {
            TimeUnit.SECONDS.sleep(1);
            System.exit(0);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    //Creates an AnchorPane for the map
    private AnchorPane addMapAnchor()
    {
        mapAnchor.getChildren().add(mapScroll);

        AnchorPane.setLeftAnchor(mapScroll, 0.0);
        AnchorPane.setTopAnchor(mapScroll, 0.0);
        AnchorPane.setBottomAnchor(mapScroll, 0.0);
        AnchorPane.setRightAnchor(mapScroll, 0.0);

        return mapAnchor;
    }

    public static void main(String[] args)
    {
        launch(args);
    }
}
