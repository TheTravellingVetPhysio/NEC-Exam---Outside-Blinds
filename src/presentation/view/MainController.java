package presentation.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import presentation.viewmodel.MainViewModel;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable
{
  private MainViewModel viewModel;

  @FXML private VBox topBar;

  private double xOffset;
  private double yOffset;


  public void init(MainViewModel viewModel)
  {
    this.viewModel = viewModel;
  }

  @Override public void initialize(URL location, ResourceBundle resources)
  {
    topBar.setOnMousePressed(event -> {
      xOffset = event.getSceneX();
      yOffset = event.getSceneY();
    });

    topBar.setOnMouseDragged(event -> {
      Stage stage = (Stage) topBar.getScene().getWindow();
      stage.setX(event.getScreenX() - xOffset);
      stage.setY(event.getScreenY() - yOffset);
    });
  }




  public void handleManualUp(ActionEvent actionEvent)
  {
    viewModel.onManualUp();
  }

  public void handleManualDown(ActionEvent actionEvent)
  {
    viewModel.onManualDown();
  }

  public void handleAutomatic(ActionEvent actionEvent)
  {
    viewModel.onAutomatic();
  }


  /* AI's forslag til at implementere listeneren
  @FXML private ImageView blindsImage; // eller Label, Circle osv.

  @Override public void onBlindsChanged(BlindsStatus status)
  {
    // Skal køres på JavaFX-tråden!
    Platform.runLater(() -> {
      switch (status)
      {
        case OPEN   -> blindsImage.setImage(new Image("open.png"));
        case CLOSED -> blindsImage.setImage(new Image("closed.png"));
      }
    });
  }
   */
}
