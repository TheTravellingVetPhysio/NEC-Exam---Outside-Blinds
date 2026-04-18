package client;

public interface ClientSocket
{
  void connect(String host, int port);
  void disconnect();
  void send(String message, String value);
}
