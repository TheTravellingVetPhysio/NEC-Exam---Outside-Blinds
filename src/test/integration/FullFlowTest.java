package test.integration;

import model.DTO.SensorReadingDTO;
import org.junit.jupiter.api.*;
import server.SensorReadingConsumer;
import server.ServerSocketManagerTCP;
import server.ServerSocketManagerUDP;
import service.BlindsService;
import shared.listener.BlindsStateListenerService;

import java.io.*;
import java.net.*;
import java.util.concurrent.*;

import static org.junit.jupiter.api.Assertions.*;

public class FullFlowTest
{
  private static final int UDP_PORT = 9880;
  private static final int TCP_PORT = 9881;

  private BlindsService blindsService;
  private ServerSocketManagerTCP tcpManager;
  private ServerSocketManagerUDP udpManager;

  private BlockingQueue<String> receivedCommands;

  @BeforeEach void setup() throws InterruptedException
  {
    receivedCommands = new LinkedBlockingQueue<>();

    blindsService = new BlindsService();
    tcpManager = new ServerSocketManagerTCP(TCP_PORT);

    BlindsStateListenerService listener = new BlindsStateListenerService(
        tcpManager, new MockBlindsServiceUIListener(), blindsService);
    blindsService.setListener(listener);

    BlockingQueue<SensorReadingDTO> queue = new LinkedBlockingQueue<>();
    new Thread(new SensorReadingConsumer(queue, blindsService)).start();

    udpManager = new ServerSocketManagerUDP(UDP_PORT, queue);

    startFakeBlindsClient();
    Thread.sleep(500);
  }

  @AfterEach void teardown() throws Exception
  {
    udpManager.stop();
    tcpManager.stop();
    Thread.sleep(200); // Giv tråde tid til at lukke
  }

  private void startFakeBlindsClient() throws InterruptedException
  {
    new Thread(() -> {
      try
      {
        Socket socket = new Socket("localhost", TCP_PORT);
        BufferedReader in = new BufferedReader(
            new InputStreamReader(socket.getInputStream()));
        String line;
        while ((line = in.readLine()) != null)
        {
          receivedCommands.add(line);
        }
      }
      catch (IOException e)
      {
        // Forbindelse lukket — forventet efter test
      }
    }).start();

    Thread.sleep(200);
  }

  private void sendUDP(String message) throws Exception
  {
    DatagramSocket socket = new DatagramSocket();
    byte[] data = message.getBytes();
    DatagramPacket packet = new DatagramPacket(data, data.length,
        InetAddress.getByName("localhost"), UDP_PORT);
    socket.send(packet);
    socket.close();
    Thread.sleep(100); // Giv serveren tid til at behandle pakken
  }

  @Test
  void givenHighTempHighSunLowWind_thenBlindsClose() throws Exception
  {
    sendUDP("TEMPERATURE:30.0");
    sendUDP("SUN:60000.0");
    sendUDP("WIND:2.0");

    String command = receivedCommands.poll(2, TimeUnit.SECONDS);
    assertNotNull(command, "Ingen kommando modtaget");
    assertTrue(command.contains("DOWN"), "Forventede DOWN (blinds kørt ned) men fik: " + command);
  }

  @Test
  void givenHighWind_thenBlindsOpen() throws Exception
  {
    sendUDP("TEMPERATURE:30.0");
    sendUDP("SUN:60000.0");
    sendUDP("WIND:2.0");
    receivedCommands.poll(2, TimeUnit.SECONDS); // vent på DOWN

    sendUDP("WIND:15.0");

    String command = receivedCommands.poll(2, TimeUnit.SECONDS);
    assertNotNull(command);
    assertTrue(command.contains("UP"), "Forventede UP (blinds kørt op) men fik: " + command);
  }

  @Test void givenLowTemperature_thenBlindsStayOpen() throws Exception
  {
    sendUDP("TEMPERATURE:20.0");
    sendUDP("SUN:60000.0");
    sendUDP("WIND:2.0");

    // Ingen kommando forventet da temp er for lav til at lukke
    String command = receivedCommands.poll(1, TimeUnit.SECONDS);
    assertNull(command, "Persiennen burde ikke reagere ved lav temperatur");
  }

  @Test void givenNoSun_thenBlindsStayOpen() throws Exception
  {
    sendUDP("TEMPERATURE:30.0");
    sendUDP("SUN:10000.0");
    sendUDP("WIND:2.0");

    String command = receivedCommands.poll(1, TimeUnit.SECONDS);
    assertNull(command, "Persiennen burde ikke reagere uden sol");
  }

  @Test void givenBlindsAlreadyDown_whenConditionsStillMet_thenNoNewCommand() throws Exception
  {
    // Arrange - send data der udløser DOWN
    sendUDP("TEMPERATURE:30.0");
    sendUDP("SUN:60000.0");
    sendUDP("WIND:2.0");

    // Vent på den første DOWN-kommando
    String firstCommand = receivedCommands.poll(2, TimeUnit.SECONDS);
    assertNotNull(firstCommand, "Forventede en første DOWN kommando");

    // Act - send samme data igen
    sendUDP("TEMPERATURE:30.0");
    sendUDP("SUN:60000.0");
    sendUDP("WIND:2.0");

    // Assert - ingen ny kommando forventet
    String secondCommand = receivedCommands.poll(1, TimeUnit.SECONDS);
    assertNull(secondCommand, "Forventede ingen ny kommando da tilstanden ikke ændrede sig");
  }

  @Test void givenManualDown_whenWindIsLow_thenBlindsClose() throws Exception
  {
    // Arrange
    sendUDP("TEMPERATURE:0.0");
    sendUDP("SUN:0.0");
    sendUDP("WIND:2.0");

    // Act - sæt manuel ned
    blindsService.setManualDown();

    // Assert
    String command = receivedCommands.poll(2, TimeUnit.SECONDS);
    assertNotNull(command, "Forventede en kommando");
    assertTrue(command.contains("DOWN"), "Forventede DOWN men fik: " + command);
  }
  // Fejlede fordi der ikke blev kaldt listeners på setter-metoder i BlindsService

  @Test void givenManualDown_whenWindIsHigh_thenBlindsStayOpen() throws Exception
  {
    // Arrange
    sendUDP("TEMPERATURE:0.0");
    sendUDP("SUN:0.0");
    sendUDP("WIND:15.0");

    // Act
    blindsService.setManualDown();

    // Assert
    String command = receivedCommands.poll(1, TimeUnit.SECONDS);
    assertNull(command, "Persiennen burde ikke gå ned ved høj vind");
  }

  @Test void givenManualUp_whenAllConditionsMet_thenBlindsStayOpen() throws Exception
  {
    // Arrange
    sendUDP("TEMPERATURE:30.0");
    sendUDP("SUN:60000.0");
    sendUDP("WIND:2.0");
    receivedCommands.poll(2, TimeUnit.SECONDS); // vent på automatisk DOWN

    // Act
    blindsService.setManualUp();

    // Assert
    String command = receivedCommands.poll(2, TimeUnit.SECONDS);
    assertNotNull(command, "Forventede en UP kommando");
    assertTrue(command.contains("UP"), "Forventede UP men fik: " + command);
  }
  // Fejlede fordi der ikke blev kaldt listeners på setter-metoder i BlindsService

  @Test void givenInvalidUDPMessage_thenNoCommandSent() throws Exception
  {
    // Act
    sendUDP("INVALID_MESSAGE");

    // Assert
    String command = receivedCommands.poll(1, TimeUnit.SECONDS);
    assertNull(command, "Forventede ingen kommando ved ugyldig besked");
  }
}
