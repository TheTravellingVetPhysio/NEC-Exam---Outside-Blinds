package presentation.view;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import presentation.viewmodel.MainViewModel;

import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable
{
  private MainViewModel viewModel;

  @FXML private HBox topBar;

  @FXML private Label temperatureLabel;
  @FXML private Label sunLabel;
  @FXML private Label windLabel;
  @FXML private Label statusLabel;
  @FXML private Label modeLabel;
  @FXML private Label blindsIconLabel;
  @FXML private Label blindsTextLabel;

  @FXML private Button manualUpButton;
  @FXML private Button manualDownButton;
  @FXML private Button automaticButton;

  private double xOffset;
  private double yOffset;

  public void init(MainViewModel viewModel)
  {
    this.viewModel = viewModel;

    temperatureLabel.textProperty().bind(viewModel.temperatureTextProperty());
    sunLabel.textProperty().bind(viewModel.sunTextProperty());
    windLabel.textProperty().bind(viewModel.windTextProperty());
    statusLabel.textProperty().bind(viewModel.statusTextProperty());
    modeLabel.textProperty().bind(viewModel.modeTextProperty());
    blindsIconLabel.textProperty().bind(viewModel.blindsIconTextProperty());
    blindsTextLabel.textProperty().bind(viewModel.blindsStateTextProperty());

    setActiveButton(automaticButton);
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
    setActiveButton(manualUpButton);
  }

  public void handleManualDown(ActionEvent actionEvent)
  {
    viewModel.onManualDown();
    setActiveButton(manualDownButton);
  }

  public void handleAutomatic(ActionEvent actionEvent)
  {
    viewModel.onAutomatic();
    setActiveButton(automaticButton);
  }

  public void handleClose(ActionEvent actionEvent)
  {
    Stage stage = (Stage) topBar.getScene().getWindow();
    stage.close();
  }

  public void handleMinimize(ActionEvent actionEvent)
  {
    Stage stage = (Stage) topBar.getScene().getWindow();
    stage.setIconified(true);
  }

  private void setActiveButton(Button activeButton)
  {
    resetButton(manualUpButton);
    resetButton(manualDownButton);
    resetButton(automaticButton);

    activeButton.getStyleClass().remove("action-button-secondary");

    if (!activeButton.getStyleClass().contains("action-button-primary"))
    {
      activeButton.getStyleClass().add("action-button-primary");
    }
  }

  private void resetButton(Button button)
  {
    button.getStyleClass().removeAll("action-button-primary", "action-button-secondary");

    if (!button.getStyleClass().contains("action-button-secondary"))
    {
      button.getStyleClass().add("action-button-secondary");
    }
  }
}