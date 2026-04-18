import client.ClientSocket;
import client.ClientSocketManagerUDP;

import java.util.Scanner;

public class MainClientUDP
{
  public static void main(String[] args)
  {
    // Create client socket
    ClientSocket socket = new ClientSocketManagerUDP("localhost", 6789);

    // Create keyboard input stream
    Scanner input = new Scanner(System.in);

    while (true)
    {
      System.out.print("Write a line for the server: ");
      String request = input.nextLine();  // Read line from keyboard

      String reply = socket.toUppercase(request);
      System.out.println("Sever replied: " + reply);
    }

    // socket.disconnect();  // Close socket
  }
}
