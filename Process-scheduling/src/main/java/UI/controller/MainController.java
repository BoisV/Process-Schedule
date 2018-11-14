package UI.controller;

import UI.control.ExtendedAnimatedFlowContainer;
import com.sun.xml.internal.ws.wsdl.writer.document.soap.Body;
import io.datafx.controller.ViewController;
import io.datafx.controller.flow.Flow;
import io.datafx.controller.flow.FlowException;
import io.datafx.controller.flow.FlowHandler;
import io.datafx.controller.flow.container.ContainerAnimations;
import io.datafx.controller.flow.context.FXMLViewFlowContext;
import io.datafx.controller.flow.context.ViewFlowContext;
import io.reactivex.rxjavafx.observables.JavaFxObservable;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.BorderPane;
import javafx.util.Duration;

import javax.annotation.PostConstruct;
import java.util.Objects;

/**
 * Created by Snart Lu on 2018/2/5. <br/>
 */
@ViewController(value = "/views/main.fxml")
public class MainController {
    @FXMLViewFlowContext
    private ViewFlowContext context;
    @FXML
    private BorderPane root;
    @FXML
    private MenuItem author;
    @FXML
    private MenuItem process_schedule;

    @PostConstruct
    public void init() throws FlowException {
        Objects.requireNonNull(context);
        // create the inner flow and content, set the default controller
        Flow innerFlow = new Flow(AuthorController.class);

        final FlowHandler flowHandler = innerFlow.createHandler(context);
        context.register("ContentFlowHandler", flowHandler);
        context.register("ContentFlow", innerFlow);
        final Duration containerAnimationDuration = Duration.millis(320);
        root.setCenter(flowHandler.start(new ExtendedAnimatedFlowContainer(containerAnimationDuration,
                ContainerAnimations.SWIPE_LEFT)));
        context.register("ContentPane", root.getCenter());

        // bind events on menu
        JavaFxObservable.actionEventsOf(author).subscribe(actionEvent -> {
            if (!(flowHandler.getCurrentView().getViewContext().getController() instanceof AuthorController)) {
                flowHandler.handle(author.getId());

            }
        });
        JavaFxObservable.actionEventsOf(process_schedule).subscribe(actionEvent -> {
            if (!(flowHandler.getCurrentView().getViewContext().getController() instanceof BodyController)) {
                flowHandler.handle(process_schedule.getId());

            }
        });


        // bind menu to view in flow
        bindMenuToController(author, AuthorController.class, innerFlow);
        bindMenuToController(process_schedule, BodyController.class, innerFlow);

    }

    private void bindMenuToController(MenuItem menu, Class<?> controllerClass, Flow flow) {
        flow.withGlobalLink(menu.getId(), controllerClass);
    }
}
