package com.dock;

import com.sun.glass.ui.Pixels;
import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.Slider;
import javafx.scene.effect.BoxBlur;
import javafx.scene.effect.DropShadow;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.effect.MotionBlur;
import javafx.scene.image.Image;
import javafx.scene.image.PixelFormat;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.robot.Robot;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

import javafx.animation.*;
import javafx.application.Application;
import javafx.beans.property.*;
import javafx.geometry.Rectangle2D;
import javafx.scene.*;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.*;
import javafx.scene.image.*;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import javafx.util.Duration;

/*
public class Bar extends Application {

    public static void main(String args[]) {
         launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {

        StackPane root = new StackPane();
        root.setBackground(Background.EMPTY);
        root.setEffect(new GaussianBlur());

        Rectangle r = new Rectangle();
        r.setX(0);
        r.setY(0);
        r.setWidth(640.0);
        r.setHeight(480.0);
        r.setEffect(new BoxBlur());



        Text t1 = new Text("BoxBlur");
        t1.setFont(new Font("Times New Roman", 60));
        t1.setFill(Color.LIGHTGRAY);
        t1.setStroke(Color.BLACK);
        t1.setEffect(new BoxBlur());
        root.getChildren().add(t1);

        Scene scene = new Scene(root, 640.0, 480.0);
        scene.setFill(Color.rgb(10,10,10,0.5));


        primaryStage.initStyle(StageStyle.TRANSPARENT);
        primaryStage.setScene(scene);
        primaryStage.show();



    }



}
*/


import javafx.animation.*;
import javafx.application.*;
import javafx.beans.property.*;
import javafx.geometry.Insets;
import javafx.scene.*;
import javafx.scene.control.Label;
import javafx.scene.effect.*;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.image.*;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;

import java.awt.image.BufferedImage;

public class Bar extends Application {

    private static final double BLUR_AMOUNT = 30;

    private static final int margin = 25;
    private static final int height = 65;
    private static final int width = 430;


    private static final Effect frostEffect =
            new BoxBlur(BLUR_AMOUNT, BLUR_AMOUNT, 4);

    private static final ImageView background = new ImageView();
    private static final StackPane layout = new StackPane();

    @Override public void start(Stage stage) {
        layout.getChildren().setAll(background, createContent());
        layout.setStyle("-fx-background-color: null");

        Scene scene = new Scene(
                layout,
                width,
                height,
                Color.TRANSPARENT
        );

        Platform.setImplicitExit(false);

        scene.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2) Platform.exit();
        });
        makeSmoke(stage);

        stage.initStyle(StageStyle.TRANSPARENT);
        stage.setScene(scene);
        stage.show();

        //Make it botton-left aligned
        Rectangle2D visualBounds = Screen.getPrimary().getVisualBounds();
        stage.setX(margin);
        stage.setY(visualBounds.getMaxY());

        background.setImage(copyBackground(stage));
        background.setEffect(frostEffect);

        makeDraggable(stage, layout);
    }

    // copy a background node to be frozen over.
    private Image copyBackground(Stage stage) {
        final int X = (int) stage.getX();
        final int Y = (int) stage.getY();
        final int W = (int) stage.getWidth();
        final int H = (int) stage.getHeight();

        try {
            java.awt.Robot robot = new java.awt.Robot();
            java.awt.image.BufferedImage image = robot.createScreenCapture(new java.awt.Rectangle(X, Y, W, H));

            return convertToFxImage(image);
        } catch (java.awt.AWTException e) {
            System.out.println("The robot of doom strikes!");
            e.printStackTrace();

            return null;
        }
    }

    // create some content to be displayed on top of the frozen glass panel.
    private Label createContent() {
        Label label = new Label("Create a new question for drop shadow effects.\n\nDrag to move\n\nDouble click to close");
        label.setPadding(new Insets(10));

        label.setStyle("-fx-font-size: 15px; -fx-text-fill: green;");
        label.setMaxWidth(250);
        label.setWrapText(true);

        return label;
    }

    // makes a stage draggable using a given node.
    public void makeDraggable(final Stage stage, final Node byNode) {
        final Delta dragDelta = new Delta();
        byNode.setOnMousePressed(mouseEvent -> {
            // record a delta distance for the drag and drop operation.
            dragDelta.x = stage.getX() - mouseEvent.getScreenX();
            dragDelta.y = stage.getY() - mouseEvent.getScreenY();
            byNode.setCursor(Cursor.MOVE);
        });
        final BooleanProperty inDrag = new SimpleBooleanProperty(false);

        byNode.setOnMouseReleased(mouseEvent -> {
            byNode.setCursor(Cursor.HAND);

            if (inDrag.get()) {
                stage.hide();

                Timeline pause = new Timeline(new KeyFrame(Duration.millis(50), event -> {
                    background.setImage(copyBackground(stage));
                    layout.getChildren().set(
                            0,
                            background
                    );
                    stage.show();
                }));
                pause.play();
            }

            inDrag.set(false);
        });
        byNode.setOnMouseDragged(mouseEvent -> {
            stage.setX(mouseEvent.getScreenX() + dragDelta.x);
            stage.setY(mouseEvent.getScreenY() + dragDelta.y);

            layout.getChildren().set(
                    0,
                    makeSmoke(stage)
            );

            inDrag.set(true);
        });
        byNode.setOnMouseEntered(mouseEvent -> {
            if (!mouseEvent.isPrimaryButtonDown()) {
                byNode.setCursor(Cursor.HAND);
            }
        });
        byNode.setOnMouseExited(mouseEvent -> {
            if (!mouseEvent.isPrimaryButtonDown()) {
                byNode.setCursor(Cursor.DEFAULT);
            }
        });
    }

    private javafx.scene.shape.Rectangle makeSmoke(Stage stage) {
        return new javafx.scene.shape.Rectangle(
                stage.getWidth(),
                stage.getHeight(),
                Color.WHITESMOKE.deriveColor(
                        0, 1, 1, 0.08
                )
        );
    }

    /** records relative x and y co-ordinates. */
    private static class Delta {
        double x, y;
    }

    private static Image convertToFxImage(BufferedImage image) {
        WritableImage wr = null;
        if (image != null) {
            wr = new WritableImage(image.getWidth(), image.getHeight());
            PixelWriter pw = wr.getPixelWriter();
            for (int x = 0; x < image.getWidth(); x++) {
                for (int y = 0; y < image.getHeight(); y++) {
                    pw.setArgb(x, y, image.getRGB(x, y));
                }
            }
        }

        return new ImageView(wr).getImage();
    }

    public static void main(String[] args) {
        launch(args);
    }


}



// build gradle


plugins {
    id 'application'
    id 'org.openjfx.javafxplugin' version '0.0.10'
    id 'com.gluonhq.gluonfx-gradle-plugin' version '1.0.6'
}

repositories {
    mavenCentral()
    maven {
        url 'https://nexus.gluonhq.com/nexus/content/repositories/releases'
    }
    maven {
        url 'https://repo1.maven.org/maven2/'
    }
}

mainClassName = 'com.dock.Bar'

dependencies {
    implementation 'com.gluonhq:charm:6.0.6'
    implementation fileTree(include: ['*.jar', '*.dll'], dir: 'libs')
    implementation group: 'org.openjfx', name: 'javafx-swing', version: '17.0.0.1'
    implementation 'com.eljavatar:SwingUtils:1.3.2'
    implementation 'com.thesett:swing_utils:0.9.117'
}

javafx {
    version = '17.0.0.1'
    modules = [ 'javafx.controls' ]
}

gluonfx {
    attachConfig {
        version = "4.0.12"
        services 'display', 'lifecycle', 'statusbar', 'storage'
    }
}
