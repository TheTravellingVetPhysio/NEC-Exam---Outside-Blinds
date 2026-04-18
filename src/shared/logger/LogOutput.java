package shared.logger;

public interface LogOutput
{
  void log(String className, String level, String message);
}
