package io.github.flamehub.commons.punishment;

import io.github.flamehub.commons.config.FlameConfig;
import io.github.flamehub.commons.config.FlameConfigProperties;
import io.github.flamehub.commons.message.Message;

@FlameConfigProperties(name = "punishmentMessages.json")
public final class PunishmentMessages extends FlameConfig {

  public Message banKick = Message.from(
      "\n&#DA0000☹ &#F33434Zostałeś zbanowany na tym serwerze! &#DA0000☹ \n\n &#F33434Administrator: &#DA0000{admin} \n &#F33434Powód: &#DA0000{reason} \n &#F33434Data wygaśnięcia: &#DA0000{time}"
  );

  public Message banIPKick = Message.from(
      "\n&#DA0000☹ &#F33434Twoje IP zostało zbanowane na tym serwerze! &#DA0000☹ \n\n &#F33434Administrator: &#DA0000{admin} \n &#F33434Powód: &#DA0000{reason} \n &#F33434Data wygaśnięcia: &#DA0000{time}"
  );

  public Message banBroadcast = Message.from(
      "&#DA0000⚠ &8〢 &#F33434Gracz &#DA0000{target} &#F33434został permanentnie zbanowany przez administratora &#DA0000{admin} &#F33434z powodem: &#DA0000{reason}"
  );

  public Message banIPBroadcast = Message.from(
      "&#DA0000⚠ &8〢 &#F33434IP gracza &#DA0000{target} &#F33434zostało permanentnie zbanowane przez administratora &#DA0000{admin} &#F33434z powodem: &#DA0000{reason}"
  );

  public Message tempBanBroadcast = Message.from(
      "&#DA0000⚠ &8〢 &#F33434Gracz &#DA0000{target} &#F33434został zbanowany na czas &#DA0000{duration} &#F33434przez administratora &#DA0000{admin} &#F33434z powodem: &#DA0000{reason}"
  );

  public Message tempBanIPBroadcast = Message.from(
      "&#DA0000⚠ &8〢 &#F33434IP gracza &#DA0000{target} &#F33434zostało zbanowane na czas &#DA0000{duration} &#F33434przez administratora &#DA0000{admin} &#F33434z powodem: &#DA0000{reason}"
  );

  public Message unbanBroadcast = Message.from(
      "&#07B877✔ &8〢 &#33D47FGracz &#07B877{target} &#33D47Fzostał odbanowany przez administratora &#07B877{admin}"
  );

  public Message muteBroadcast = Message.from(
      "&#DA0000⚠ &8〢 &#F33434Gracz &#DA0000{target} &#F33434został permanentnie wyciszony &#F33434przez administratora &#DA0000{admin} &#F33434z powodem: &#DA0000{reason}"
  );

  public Message tempMuteBroadcast = Message.from(
      "&#DA0000⚠ &8〢 &#F33434Gracz &#DA0000{target} &#F33434został wyciszony na czas &#DA0000{duration} &#F33434przez administratora &#DA0000{admin} &#F33434z powodem: &#DA0000{reason}"
  );

  public Message unMuteBroadcast = Message.from(
      "&#07B877✔ &8〢 &#33D47FGracz &#07B877{target} &#33D47Fzostał odciszony przez administratora &#07B877{admin}"
  );

  public Message punishmentNotFound = Message.from(
      "&#DA0000⚠ &8〢 &#F33434Ten gracz nie posiada żadnej kary!"
  );

  public Message mutedInfo = Message.from(
      " &#F33434Zostałeś wyciszony przez administratora &#DA0000{admin} &#F33434z powodem: &#DA0000{reason}",
      " &#F33434Wyciszenie wygaśnie za: &#DA0000{time}"
  );


}
