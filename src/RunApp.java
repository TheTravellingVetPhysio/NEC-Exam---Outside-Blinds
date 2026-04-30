import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import presentation.view.MainController;
import presentation.viewmodel.MainViewModel;
import service.BlindsService;

public class RunApp extends Application
{

  @Override public void start(Stage stage) throws Exception
  {

    stage.initStyle(StageStyle.TRANSPARENT);

    BlindsService blindsService = new BlindsService();

    MainViewModel viewModel = new MainViewModel(blindsService);

    FXMLLoader loader = new FXMLLoader(getClass().getResource("@/fxml/BlindsDashboardView.fxml"));
    Parent root = loader.load();

    MainController controller = loader.getController();

    controller.init(viewModel);

    Scene scene = new Scene(root);

    scene.setFill(null);
    stage.setScene(scene);
    stage.show();
  }
}
