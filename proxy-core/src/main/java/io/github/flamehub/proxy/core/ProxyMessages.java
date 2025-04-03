package io.github.flamehub.proxy.core;

import static io.github.flamehub.proxy.core.message.VelocityMessage.from;

import io.github.flamehub.commons.config.FlameConfig;
import io.github.flamehub.commons.config.FlameConfigProperties;
import io.github.flamehub.proxy.core.message.VelocityMessage;

@FlameConfigProperties(name = "messages.json")
public final class ProxyMessages extends FlameConfig {

  public VelocityMessage userDoesNotExists = from(
      "&#DA0000☹ &8〢 &#F33434Ten użytkownik nie istnieje w bazie danych!"
  );

  public VelocityMessage successfullyLoggedIn = from(
      "&#07B877✔ &8〢 &#33D47FZostałeś pomyślnie zalogowany!");

  public VelocityMessage successfullyRegistered = from(
      "&#07B877✔ &8〢 &#33D47FZostałeś pomyślnie zarejestrowany!"
  );

  public VelocityMessage playerHasPremiumAuthorization = from(
      "&6⚠ &8〢 &#FCFF00Jesteś zweryfikowany jako gracz premium!");

  public VelocityMessage firstYouHaveToRegister = from(
      "&#DA0000☹ &8〢 &#F33434Najpierw musisz się zarejestrować!");

  public VelocityMessage firstYouHaveToLogin = from(
      "&#DA0000☹ &8〢 &#F33434Najpierw musisz się zalogować!");

  public VelocityMessage alreadyLogged = from(
      "&6⚠ &8〢 &#FCFF00Jesteś już zalogowany!");

  public VelocityMessage alreadyRegistered = from(
      "&6⚠ &8〢 &#FCFF00Jesteś już zarejestrowany!");

  public VelocityMessage wrongPassword = from(
      "&#DA0000☹ &8〢 &#F33434Hasło jest nieprawidłowe!");

  public VelocityMessage wrongPasswordLength = from(
      "&#DA0000☹ &8〢 &#F33434Hasło musi mieć od 6 do 32 znaków!");

  public VelocityMessage successfullyChangedPassword = from(
      "&#07B877✔ &8〢 &#33D47FPomyślnie zmieniono hasło!");

  public VelocityMessage wrongCaptcha = from(
      "&#DA0000☹ &8〢 &#F33434Przepisałeś błedny kod captcha! Spróbuj ponownie.");

  public VelocityMessage passwordAreNotTheSame = from(
      "&#DA0000☹ &8〢 &#F33434Hasła się nie zgadzają!");

  public VelocityMessage playerAlreadyOnline = from(
      "&#DA0000☹ &8〢 &#F33434Gracz o tym nicku jest już online na serwerze!"
  );

  public VelocityMessage wrongClientVersion = from(
      "&#DA0000☹ &8〢 &#F33434Na serwer możesz wejśc od wersji &#DA00001.16+"
  );

  public VelocityMessage alreadyConnectedToThisServer = from(
      "&6⚠ &8〢 &#FCFF00Jesteś aktualnie połączony z tym serwerem"
  );

  public VelocityMessage cannotFindOnlineLobby = from(
      "&#DA0000☹ &8〢 &#F33434Wystąpił błąd! Wszystkie serwery lobby są prawdopodobnie &#DA0000offline&#F33434!"
  );

  public VelocityMessage attemptToConnect = from(
      "&#42A6BC⚠ &8〢 &#5AE1FFTrwa próba łączenia z serwerem &#42A6BC{server}"
  );

  public VelocityMessage incorrectNickname = from(
      "&#DA0000☹ &8〢 &#F33434Nieprawidłowy nickname! Wejdź na serwer z nicku: &#DA0000{nick}"
  );

  public VelocityMessage notAllowedNickname = from(
      "&#DA0000☹ &8〢 &#F33434Niedozwolony nickname! Maksymalna długość nicku wynosi 16 "
          + "\\n&#F33434Natomiast minimalna długość to 3 znaki"
  );

  public VelocityMessage connectionDelay = from(
      "&#DA0000☹ &8〢 &#F33434Kolejny raz będziesz mógł się połączyć za: &#DA0000{time}"
  );

  public VelocityMessage accountsLimitReached = from(
      "&#DA0000☹ &8〢 &#F33434Osiągnąłeś limit zarejestrowanych kont na tym adresie IP!"
  );

  public VelocityMessage vpnDetected = from(
      "&#DA0000☹ &#F33434Wykryto VPN &#DA0000☹ \n &#F33434Jeżeli uważasz, że to błąd skontaktuj się z nami na: &#DA0000dc.flamehub.pl"
  );

  public VelocityMessage blacklistKick = from(
      "&#DA0000⚠ &#F33434Jesteś dodany na blackliste! &#DA0000⚠\n"
          + "&#DA0000Admin: &#F33434{admin}\n"
          + "&#DA0000Powód: &#F33434{reason}\n"
          + "&#DA0000Data: &#F33434{date}\n"
  );

  public VelocityMessage insufficientPermissions = from(
      "&#DA0000☹ &8〢 &#F33434Nie posiadasz wystarczających uprawnień do wykonania tej komendy! &#DA0000({permission})"
  );

  public VelocityMessage correctUsage = from(
      "&#DA0000☹ &8〢 &#F33434Poprawne użycie: &#DA0000{usage}"
  );

  public VelocityMessage correctUsageMultiple = from(
      "&8- &#DA0000{usage}"
  );

  public VelocityMessage playerIsOffline = from(
      "&#DA0000☹ &8〢 &#F33434Podany przez Ciebie gracz jest offline!"
  );

}
