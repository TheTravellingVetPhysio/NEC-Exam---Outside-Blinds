package presentation.view;

import javafx.event.ActionEvent;
import presentation.viewmodel.MainViewModel;

public class MainController
{
  private MainViewModel viewModel;


  public void init(MainViewModel viewModel)
  {
    this.viewModel = viewModel;
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
