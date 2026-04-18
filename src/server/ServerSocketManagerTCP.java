package server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class ServerSocketManagerTCP
{
  public ServerSocketManagerTCP(int port)
  {

    System.out.println("Starting Server...");
    try
    {

      // Create welcoming socket at port
      ServerSocket welcomeSocket = new ServerSocket(port);

      System.out.println("Waiting for a client to establish connection.");

      while (true)
      {

        try
        {
          // Wait for contact by client

          System.out.println("Waiting for client...");
          Socket socket = welcomeSocket.accept();

          // Create input stream attached to the socket
          BufferedReader in = new BufferedReader(
              new InputStreamReader(socket.getInputStream()));

          // Create output stream attached to the socket
          PrintWriter out = new PrintWriter(socket.getOutputStream(), true);

          // Note down the IP address and socket port of the client.
          String client_address =
              socket.getInetAddress().getHostAddress() + ":" + socket.getPort();
          System.out.println(
              "Connection established with client " + client_address);

          while (true)
          {
            System.out.println("Waiting for new request from client.");
            String request = in.readLine(); // Read line from client.

            if (request == null)
            {
              System.out.println("Client disconnected: " + client_address);
              break;
            }
            else
            {
              System.out.println("Client " + client_address + "> " + request);
              String reply = request.toUpperCase(); // Convert to upper case
              System.out.println("Server> " + reply);

              out.println(reply); // Send line back to client.
            }
          }
          socket.close();
          System.out.println("Socket closed. Waiting for new client...");

        }
        catch (IOException e)
        {
          System.out.println("Error: Server socket IO failure");
        }
      }
    }   catch (IOException e)
    {
      System.out.println("error: server was closed");
    }

  }
}
