package bot.utils;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class JavaFXUtils {

    private static boolean javaFxLaunched = false;

    public static void launch(Class<? extends Application> applicationClass) {
        if (!isJavaFxLaunched()) { // First time
            Platform.setImplicitExit(false);
            new Thread(() -> Application.launch(applicationClass)).start();
            setJavaFxLaunched(true);
        } else { // Next times
            Platform.runLater(() -> {
                try {
                    Application application = applicationClass.getDeclaredConstructor().newInstance();
                    Stage primaryStage = new Stage();
                    application.start(primaryStage);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }
    }

    public static void saveFile(Image content, File file) {
        try {
            BufferedImage bufferedImage = SwingFXUtils.fromFXImage(content, null);
            ImageIO.write(bufferedImage, "png", file);
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    public static boolean isJavaFxLaunched() {
        return javaFxLaunched;
    }

    public static void setJavaFxLaunched(boolean javaFxLaunched) {
        JavaFXUtils.javaFxLaunched = javaFxLaunched;
    }

}
