package io.github.flamehub.proxy.core;

import static io.github.flamehub.proxy.core.message.VelocityMessage.from;

import io.github.flamehub.commons.config.FlameConfig;
import io.github.flamehub.commons.config.FlameConfigProperties;
import io.github.flamehub.proxy.core.message.VelocityMessage;

@FlameConfigProperties(name = "messages.json")
public final class ProxyMessages extends FlameConfig {

  public VelocityMessage userDoesNotExists = from(
      "<#DA0000>☹ <dark_gray>〢 <#F33434>Ten użytkownik nie istnieje w bazie danych!"
  );

  public VelocityMessage successfullyLoggedIn = from(
      "<#07B877>✔ <dark_gray>〢 <#33D47F>Zostałeś pomyślnie zalogowany!");

  public VelocityMessage successfullyRegistered = from(
      "<#07B877>✔ <dark_gray>〢 <#33D47F>Zostałeś pomyślnie zarejestrowany!"
  );

  public VelocityMessage playerHasPremiumAuthorization = from(
      "<gold>⚠ <dark_gray>〢 <#FCFF00>Jesteś zweryfikowany jako gracz premium!");

  public VelocityMessage firstYouHaveToRegister = from(
      "<#DA0000>☹ <dark_gray>〢 <#F33434>Najpierw musisz się zarejestrować!");

  public VelocityMessage firstYouHaveToLogin = from(
      "<#DA0000>☹ <dark_gray>〢 <#F33434>Najpierw musisz się zalogować!");

  public VelocityMessage alreadyLogged = from(
      "<gold>⚠ <dark_gray>〢 <#FCFF00>Jesteś już zalogowany!");

  public VelocityMessage alreadyRegistered = from(
      "<gold>⚠ <dark_gray>〢 <#FCFF00>Jesteś już zarejestrowany!");

  public VelocityMessage wrongPassword = from(
      "<#DA0000>☹ <dark_gray>〢 <#F33434>Hasło jest nieprawidłowe!");

  public VelocityMessage wrongPasswordLength = from(
      "<#DA0000>☹ <dark_gray>〢 <#F33434>Hasło musi mieć od 6 do 32 znaków!");

  public VelocityMessage successfullyChangedPassword = from(
      "<#07B877>✔ <dark_gray>〢 <#33D47F>Pomyślnie zmieniono hasło!");

  public VelocityMessage wrongCaptcha = from(
      "<#DA0000>☹ <dark_gray>〢 <#F33434>Przepisałeś błedny kod captcha! Spróbuj ponownie.");

  public VelocityMessage passwordAreNotTheSame = from(
      "<#DA0000>☹ <dark_gray>〢 <#F33434>Hasła się nie zgadzają!");

  public VelocityMessage playerAlreadyOnline = from(
      "<#DA0000>☹ <dark_gray>〢 <#F33434>Gracz o tym nicku jest już online na serwerze!"
  );

  public VelocityMessage wrongClientVersion = from(
      "<#DA0000>☹ <dark_gray>〢 <#F33434>Na serwer możesz wejśc od wersji <#DA0000>1.16+"
  );

  public VelocityMessage alreadyConnectedToThisServer = from(
      "<gold>⚠ <dark_gray>〢 <#FCFF00>Jesteś aktualnie połączony z tym serwerem"
  );

  public VelocityMessage cannotFindOnlineLobby = from(
      "<#DA0000>☹ <dark_gray>〢 <#F33434>Wystąpił błąd! Wszystkie serwery lobby są prawdopodobnie <#DA0000>offline<#F33434>!"
  );

  public VelocityMessage attemptToConnect = from(
      "<#42A6BC>⚠ <dark_gray>〢 <#5AE1FF>Trwa próba łączenia z serwerem <#42A6BC>{server}"
  );

  public VelocityMessage incorrectNickname = from(
      "<#DA0000>☹ <dark_gray>〢 <#F33434>Nieprawidłowy nickname! Wejdź na serwer z nicku: <#DA0000>{nick}"
  );

  public VelocityMessage notAllowedNickname = from(
      "<#DA0000>☹ <dark_gray>〢 <#F33434>Niedozwolony nickname! Maksymalna długość nicku wynosi 16 "
          + "\\n<#F33434>Natomiast minimalna długość to 3 znaki"
  );

  public VelocityMessage connectionDelay = from(
      "<#DA0000>☹ <dark_gray>〢 <#F33434>Kolejny raz będziesz mógł się połączyć za: <#DA0000>{time}"
  );

  public VelocityMessage accountsLimitReached = from(
      "<#DA0000>☹ <dark_gray>〢 <#F33434>Osiągnąłeś limit zarejestrowanych kont na tym adresie IP!"
  );

  public VelocityMessage vpnDetected = from(
      "<#DA0000>☹ <#F33434>Wykryto VPN &#DA0000☹ \n <#F33434>Jeżeli uważasz, że to błąd skontaktuj się z nami na: <#DA0000>dc.flamehub.pl"
  );

  public VelocityMessage blacklistKick = from(
      "<#DA0000>⚠ <#F33434>Jesteś dodany na blackliste! <#DA0000>⚠\n"
          + "<#DA0000>Admin: <#F33434>{admin}\n"
          + "<#DA0000>Powód: <#F33434>{reason}\n"
          + "<#DA0000>Data: <#F33434>{date}\n"
  );

  public VelocityMessage insufficientPermissions = from(
      "<#DA0000>☹ <dark_gray>〢 <#F33434>Nie posiadasz wystarczających uprawnień do wykonania tej komendy! <#DA0000>({permission})"
  );

  public VelocityMessage correctUsage = from(
      "<#DA0000>☹ <dark_gray>〢 <#F33434>Poprawne użycie: <#DA0000>{usage}"
  );

  public VelocityMessage correctUsageMultiple = from(
      "<dark_gray>- <#DA0000>{usage}"
  );

  public VelocityMessage playerIsOffline = from(
      "<#DA0000>☹ <dark_gray>〢 <#F33434>Podany przez Ciebie gracz jest offline!"
  );

}
