package presentation.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import presentation.viewmodel.MainViewModel;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable
{
  private MainViewModel viewModel;

  @FXML private VBox topBar;
  @FXML private Label temperatureLabel;
  @FXML private Label sunLabel;
  @FXML private Label windLabel;
  @FXML private Label statusLabel;
  @FXML private Label modeLabel;
  @FXML private Label blindsIconLabel;
  @FXML private Label blindsTextLabel;

  private double xOffset;
  private double yOffset;

  public void init(MainViewModel viewModel)
  {
    this.viewModel = viewModel;

    // Binder labels til ViewModel properties
    temperatureLabel.textProperty().bind(viewModel.temperatureTextProperty());
    sunLabel.textProperty().bind(viewModel.sunTextProperty());
    windLabel.textProperty().bind(viewModel.windTextProperty());
    statusLabel.textProperty().bind(viewModel.statusTextProperty());
    modeLabel.textProperty().bind(viewModel.modeTextProperty());
    blindsIconLabel.textProperty().bind(viewModel.blindsIconTextProperty());
    blindsTextLabel.textProperty().bind(viewModel.blindsStateTextProperty());
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
}
