import UI.control.CustomJFXDecorator;
import UI.controller.MainController;
import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.container.DefaultFlowContainer;
import io.datafx.controller.flow.context.ViewFlowContext;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

public class App extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
//        Thread.setDefaultUncaughtExceptionHandler(App::logError);

        ViewFlowContext flowContext = new ViewFlowContext();
        flowContext.register("Stage", primaryStage);

        // create flow and flow container, flow container controls view decoration and view exchange
        Flow flow = new Flow(MainController.class);
        DefaultFlowContainer container = new DefaultFlowContainer();
        flow.createHandler(flowContext).start(container);

        // JFXDecorator will be applied to primaryStage, and decorated on view which is created by flow container
        CustomJFXDecorator decorator = new CustomJFXDecorator(primaryStage, container.getView(),
                false, true, true);

        // init scene with a decorator
        Scene scene = new Scene(decorator, 1000, 750);
        final ObservableList<String> stylesheets = scene.getStylesheets();
        stylesheets.add(App.class.getResource("/css/main.css").toExternalForm());
        primaryStage.setMinWidth(500);
        primaryStage.setMinHeight(400);
        primaryStage.setTitle("Process schedule");
        primaryStage.getIcons().add(new Image("/image/icon.png"));
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
