package com.github.teocci.socket.gui;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.TextArea;

/**
 * Created by teocci.
 *
 * @author teocci@yandex.com on 2019-Jan-03
 */
public class LogTextArea extends TextArea
{
    private final BooleanProperty pausedScrollProperty = new SimpleBooleanProperty(false);
    private double scrollPosition = 0;

    public LogTextArea()
    {
        super();
    }

    /**
     * @param data string that will be added to the LogTextArea
     */
    public void setMessage(String data)
    {
        if (isPausedScroll()) {
            scrollPosition = this.getScrollTop();
            this.setText(data);
            this.setScrollTop(scrollPosition);
        } else {
            this.setText(data);
            this.setScrollTop(Double.MAX_VALUE);
        }
    }

    public final BooleanProperty pausedScrollProperty() { return pausedScrollProperty; }

    public final boolean isPausedScroll() { return pausedScrollProperty.getValue(); }

    public final void setPausedScroll(boolean value) { pausedScrollProperty.setValue(value); }
}
