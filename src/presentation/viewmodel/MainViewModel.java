package presentation.viewmodel;

import service.BlindsService;

public class MainViewModel
{
  private final BlindsService blindsService;


  public MainViewModel(BlindsService blindsService)
  {
    this.blindsService = blindsService;
  }

  public void onManualUp()
  {
    blindsService.setManualUp();
  }

  public void onManualDown()
  {
    blindsService.setManualDown();
  }

  public void onAutomatic()
  {
    blindsService.setAutomatic();
  }
}