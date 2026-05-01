import client.ClientSocketManagerTCP;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import presentation.view.MainController;
import presentation.viewmodel.MainViewModel;
import server.ServerSocketManagerTCP;
import server.ServerSocketManagerUDP;
import service.BlindsService;
import shared.listener.BlindsStateListenerService;
import simulator.SensorSimulatorClient;

public class RunApp extends Application
{

  @Override public void start(Stage stage) throws Exception
  {

    stage.initStyle(StageStyle.TRANSPARENT);

    // 1. Start server-infrastruktur
    BlindsService blindsService = new BlindsService();
    ServerSocketManagerTCP tcp = new ServerSocketManagerTCP(6790);

    // 2. ViewModel og listeners
    MainViewModel viewModel = new MainViewModel(blindsService);
    BlindsStateListenerService stateListener = new BlindsStateListenerService(
        tcp, viewModel, blindsService);
    blindsService.setListener(stateListener);

    // 2. Start UDP og sensor-simulator (kører i egne tråde) EFTER listeners er sat
    ServerSocketManagerUDP udp = new ServerSocketManagerUDP(6789, blindsService, tcp);
    new SensorSimulatorClient().start();

    // 4. Forbind til persiennen og sæt listeners på viewModel
    ClientSocketManagerTCP blindsClient = new ClientSocketManagerTCP("localhost", 6790);
    blindsClient.addListener(viewModel);
    blindsClient.receiveCommand();

    // 5. FXML
    FXMLLoader loader = new FXMLLoader(
        getClass().getResource("/fxml/BlindsDashboardView.fxml"));
    Parent root = loader.load();

    MainController controller = loader.getController();
    controller.init(viewModel);

    Scene scene = new Scene(root, 1024, 760);
    scene.setFill(null);
    stage.setScene(scene);
    stage.show();
  }
}
