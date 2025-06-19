import io.github.flamehub.commons.messenger.packet.Packet;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;

public class TestPacket implements Packet {

  private String message = "cwel";
  private Instant timestamp = Instant.now();
  private Duration duration = Duration.ofSeconds(5);
  private double[] cwel = {1.0, 2.0, 3.0, 4.0, 5.0};

  public String getMessage() {
    return message;
  }

  public Instant getTimestamp() {
    return timestamp;
  }

  public Duration getDuration() {
    return duration;
  }

  public double[] getCwel() {
    return cwel;
  }

  @Override
  public String toString() {
    return "TestPacket{" +
        "message='" + message + '\'' +
        ", timestamp=" + timestamp +
        ", duration=" + duration +
        ", cwel=" + Arrays.toString(cwel) +
        '}';
  }
}
