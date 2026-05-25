import client.BlindsClient;
import client.ClientSocketManagerTCP;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import model.DTO.SensorReadingDTO;
import presentation.view.MainController;
import presentation.viewmodel.MainViewModel;
import server.SensorReadingConsumer;
import server.ServerSocketManagerTCP;
import server.ServerSocketManagerUDP;
import service.BlindsService;
import shared.listener.BlindsStateListenerService;
import simulator.SensorSimulatorClient;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

public class RunApp extends Application
{

  @Override public void start(Stage stage) throws Exception
  {

    stage.initStyle(StageStyle.TRANSPARENT);

    // 1. Service
    BlindsService blindsService = new BlindsService();

    // 2. ViewModel, da den bruges som uiListener
    MainViewModel viewModel = new MainViewModel(blindsService);

    // 3. Serverinfrastruktur
    ServerSocketManagerTCP tcp = new ServerSocketManagerTCP(6790, viewModel);

    // 4. State listener
    BlindsStateListenerService stateListener = new BlindsStateListenerService(
        tcp, viewModel, blindsService);
    blindsService.setListener(stateListener);

    // 5. Queue - Producer/Consumer
    BlockingQueue<SensorReadingDTO> queue = new ArrayBlockingQueue<>(100);
    new Thread(new SensorReadingConsumer(queue, blindsService)).start();

    // 6. UDP og sensor-simulator (kører i egne tråde) EFTER listeners er sat
    new ServerSocketManagerUDP(6789, queue);
    new SensorSimulatorClient().start();

    // 7. Klienter
    ClientSocketManagerTCP blindsClient = new ClientSocketManagerTCP("localhost", 6790, "RunApp-BlindsClient" );
    blindsClient.addListener(viewModel);
    blindsClient.receiveCommand();

    // 8. FXML
    FXMLLoader loader = new FXMLLoader(
        getClass().getResource("/fxml/BlindsDashboardView.fxml"));
    Parent root = loader.load();

    MainController controller = loader.getController();
    controller.init(viewModel);

    Scene scene = new Scene(root, 1024, 760);
    scene.setFill(Color.TRANSPARENT);
    stage.initStyle(StageStyle.TRANSPARENT);
    stage.setScene(scene);
    stage.show();
  }
}
