package bot.utils;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.stage.Stage;

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

	public static boolean isJavaFxLaunched() {
		return javaFxLaunched;
	}

	public static void setJavaFxLaunched(boolean javaFxLaunched) {
		JavaFXUtils.javaFxLaunched = javaFxLaunched;
	}

}
