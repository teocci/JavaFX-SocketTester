package com.github.teocci.socket.gui;

import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2019-Jan-03
 */
public class LogPane extends ScrollPane
{
    private TextArea log;

    LogPane()
    {
        initialize();
    }

    private void initialize()
    {
        log = new TextArea();
        log.setEditable(false);
        log.prefWidth(512);
        log.setText("Text Log loaded. Messages:");

        setContent(log);
        setFitToWidth(true);
        setFitToHeight(true);
    }

    /**
     * @param message string to be pushed into the TextArea
     */
    public void push(String message)
    {
        log.setText(log.getText() + "\n" + message);
        log.selectPositionCaret(log.getLength());
        log.deselect();
    }
}
