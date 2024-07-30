package io.github.flamehub.restapi.punishment;

import io.github.flamehub.commons.punishment.Punishment;
import io.github.flamehub.commons.punishment.PunishmentRepository;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
class PunishmentController {

  private final PunishmentRepository punishmentRepository;

  @Autowired
  PunishmentController(PunishmentRepository punishmentRepository) {
    this.punishmentRepository = punishmentRepository;
  }

  @GetMapping("/punishment/find/{punished}")
  List<Punishment> getPunishment(final @PathVariable("punished") String punished) {
    return this.punishmentRepository.loadAll("punished", punished)
        .stream()
        .peek(punishment -> punishment.setPunishedIp("ukryte"))
        .collect(Collectors.toList());
  }

  @GetMapping("/punishment/all")
  List<Punishment> getPunishments() {
    return this.punishmentRepository.loadAll().stream()
        .peek(punishment -> punishment.setPunishedIp("ukryte"))
        .collect(Collectors.toList());
  }

  @GetMapping("/punishment/count")
  int count() {
    return this.punishmentRepository.loadAll().size();
  }
}
