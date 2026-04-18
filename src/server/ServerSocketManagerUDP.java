package server;

import shared.logger.Logger;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ServerSocketManagerUDP
{
  private final int DATAGRAM_SIZE = 20;
  private Logger logger;

  public ServerSocketManagerUDP(int port)
  {
    System.out.println("Starting Server...");
    try
    {
      // Create UDP server socket at port
      DatagramSocket serverSocket = new DatagramSocket(port);

      // Specify the size for receiving datagrams
      byte[] receiveData = new byte[DATAGRAM_SIZE];

      while (true)
      {
        DatagramPacket receivePacket = null;

        String output = "";
        boolean continu = true;
        while (continu)
        {
          // Create a packet to receive datagram
          receivePacket = new DatagramPacket(receiveData, receiveData.length);

          // Receive datagram from client
          serverSocket.receive(receivePacket);

          String request = new String(receivePacket.getData(), 0,
              receivePacket.getLength());
          output = output + request;
          if (request.contains("|"))
          {
            continu = false;
          }
        }

        // Get the IP address and port number of the client
        InetAddress clientIPAddress = receivePacket.getAddress();
        int clientPort = receivePacket.getPort();

        // Combine them into a string
        String client_address =
            clientIPAddress.getHostAddress() + ":" + clientPort;

        System.out.println("Client " + client_address + "> " + output);

        String reply = output.toUpperCase(); // Convert to upper case

        System.out.println("Server> " + reply);

        byte[] sendData = reply.getBytes();

        // Create datagram to send to the client
        DatagramPacket sendPacket = new DatagramPacket(sendData,
            sendData.length,
            clientIPAddress,
            clientPort);

        // Send datagram to client
        serverSocket.send(sendPacket);
      }
    }
    catch (IOException e)
    {
      System.out.println("Error: Server socket IO failure.");
    }
  }
}
