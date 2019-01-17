package com.github.teocci.socket;

import com.github.teocci.socket.gui.LogTextArea;
import com.github.teocci.socket.net.TcpClient;
import com.github.teocci.socket.workers.Worker;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.ToolBar;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.util.Timer;
import java.util.concurrent.TimeUnit;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2017-Jun-21
 */
public class SocketClientTester extends Application
{

    private Stage stage;

    private final Button btnUpdate = new Button("Start Updates");
    private final Button btnCommand = new Button("Command");
    private final Button btnDisconnect = new Button("Disconnect");

    private final BorderPane mainContainer = new BorderPane();
    private final ToolBar toolBar = new ToolBar();

    private final AnchorPane mapAnchor = new AnchorPane();
    private final TextArea mapScroll = new LogTextArea();

    private TcpClient thread;

    private Timer timer = new Timer();
    private Worker worker;

    @Override
    public void start(Stage primaryStage)
    {
        stage = primaryStage;

        mainContainer.setTop(toolBar);
        mainContainer.setCenter(addMapAnchor());

        toolBar.getItems().addAll(btnUpdate, btnCommand, btnDisconnect);

        Scene mapScene = new Scene(mainContainer, 600, 400);
        stage.setScene(mapScene);
        stage.show();

        initThread();
        initHandlers();
    }

    private void initThread()
    {
        try {
            String server = "1.246.220.44";
            System.out.println("Loading contents of URL: " + server);
            // create a new thread object
            thread = new TcpClient(server);
            thread.start();
            worker = new Worker(thread);
        } catch (Exception e) {
            e.printStackTrace();
            thread = null;
            shutdown();
        }
    }

    private void initHandlers()
    {
        stage.setOnCloseRequest(we -> {
            if (thread != null) {
                System.out.println("Stage is closing");
                thread.sendDisconnect();
                thread.stop();
                shutdown();
            }
        });

        btnUpdate.setOnAction(t -> updateTask());

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
    }

    private void updateTask()
    {
        if (worker.isRunning()) {
            timer.cancel();
            worker.stop();
            btnUpdate.setText("Start Updates");
        } else {
            long period = 5000;
            timer.scheduleAtFixedRate(worker, 0, period);
            btnUpdate.setText("End Updates");
//            timer.schedule(new Worker(thread), 0, period);
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
