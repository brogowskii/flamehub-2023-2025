package io.github.flamehub.commons.punishment;

import io.github.flamehub.commons.config.FlameConfig;
import io.github.flamehub.commons.config.FlameConfigProperties;
import io.github.flamehub.commons.message.Message;

@FlameConfigProperties(name = "punishmentMessages.json")
public final class PunishmentMessages extends FlameConfig {

  public Message banKick = Message.from(
      "\n<#DA0000>☹ <#F33434>Zostałeś zbanowany na tym serwerze! <#DA0000>☹ \n\n <#F33434>Administrator: <#DA0000>{admin} \n <#F33434>Powód: <#DA0000>{reason} \n <#F33434>Data wygaśnięcia: <#DA0000>{time}"
  );

  public Message banIPKick = Message.from(
      "\n<#DA0000>☹ <#F33434>Twoje IP zostało zbanowane na tym serwerze! <#DA0000>☹ \n\n <#F33434>Administrator: <#DA0000>{admin} \n <#F33434>Powód: <#DA0000>{reason} \n <#F33434>Data wygaśnięcia: <#DA0000>{time}"
  );

  public Message banBroadcast = Message.from(
      "<#DA0000>⚠ <dark_gray>〢 <#F33434>Gracz <#DA0000>{target} <#F33434>został permanentnie zbanowany przez administratora <#DA0000>{admin} <#F33434>z powodem: <#DA0000>{reason}"
  );

  public Message banIPBroadcast = Message.from(
      "<#DA0000>⚠ <dark_gray>〢 <#F33434>IP gracza <#DA0000>{target} <#F33434>zostało permanentnie zbanowane przez administratora <#DA0000>{admin} <#F33434>z powodem: <#DA0000>{reason}"
  );

  public Message tempBanBroadcast = Message.from(
      "<#DA0000>⚠ <dark_gray>〢 <#F33434>Gracz <#DA0000>{target} <#F33434>został zbanowany na czas <#DA0000>{duration} <#F33434>przez administratora <#DA0000>{admin} <#F33434>z powodem: <#DA0000>{reason}"
  );

  public Message tempBanIPBroadcast = Message.from(
      "<#DA0000>⚠ <dark_gray>〢 <#F33434>IP gracza <#DA0000>{target} <#F33434>zostało zbanowane na czas <#DA0000>{duration} <#F33434>przez administratora <#DA0000>{admin} <#F33434>z powodem: <#DA0000>{reason}"
  );

  public Message unbanBroadcast = Message.from(
      "<#07B877>✔ <dark_gray>〢 <#33D47F>Gracz <#07B877>{target} <#33D47F>został odbanowany przez administratora <#07B877>{admin}"
  );

  public Message muteBroadcast = Message.from(
      "<#DA0000>⚠ <dark_gray>〢 <#F33434>Gracz <#DA0000>{target} <#F33434>został permanentnie wyciszony <#F33434>przez administratora <#DA0000>{admin} <#F33434>z powodem: <#DA0000>{reason}"
  );

  public Message tempMuteBroadcast = Message.from(
      "<#DA0000>⚠ <dark_gray>〢 <#F33434>Gracz <#DA0000>{target} <#F33434>został wyciszony na czas <#DA0000>{duration} <#F33434>przez administratora <#DA0000>{admin} <#F33434>z powodem: <#DA0000>{reason}"
  );

  public Message unMuteBroadcast = Message.from(
      "<#07B877>✔ <dark_gray>〢 <#33D47F>Gracz <#07B877>{target} <#33D47F>został odciszony przez administratora <#07B877>{admin}"
  );

  public Message punishmentNotFound = Message.from(
      "<#DA0000>⚠ <dark_gray>〢 <#F33434>Ten gracz nie posiada żadnej kary!"
  );

  public Message mutedInfo = Message.from(
      " <#F33434>Zostałeś wyciszony przez administratora <#DA0000>{admin} <#F33434>z powodem: <#DA0000>{reason}",
      " <#F33434>Wyciszenie wygaśnie za: <#DA0000>{time}"
  );




}
