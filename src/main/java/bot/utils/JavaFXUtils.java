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
import java.util.concurrent.atomic.AtomicBoolean;

public class JavaFXUtils {

    private static final AtomicBoolean javaFxLaunched = new AtomicBoolean(false);

    public static void launch(Class<? extends Application> applicationClass) {
        if (javaFxLaunched.compareAndSet(false, true)) {
            Platform.setImplicitExit(false);
            new Thread(() -> Application.launch(applicationClass)).start();
        } else {
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
}
