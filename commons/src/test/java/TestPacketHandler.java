import io.github.flamehub.commons.messenger.packet.PacketHandler;

public class TestPacketHandler {

  @PacketHandler
  void handle(TestPacket packet) {
    System.out.println("Handling packet: " + packet.toString());
  }

}
