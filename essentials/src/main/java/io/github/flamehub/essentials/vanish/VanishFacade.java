package io.github.flamehub.essentials.vanish;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public final class VanishFacade {

  private final Set<UUID> vanishedEntries = new HashSet<>();
  private final VanishedEntryRepository vanishedEntryRepository;

  public VanishFacade(final VanishedEntryRepository vanishedEntryRepository) {
    this.vanishedEntryRepository = vanishedEntryRepository;
  }

  boolean isVanished(final UUID uniqueId) {
    return vanishedEntries.contains(uniqueId);
  }

  void addVanished(final UUID uniqueId) {
    vanishedEntries.add(uniqueId);
  }

  void removeVanished(final UUID uniqueId) {
    vanishedEntries.remove(uniqueId);
  }

  void save(final VanishedEntry vanishedEntry) {
    this.vanishedEntryRepository.save(vanishedEntry);
  }

  void delete(final VanishedEntry vanishedEntry) {
    this.vanishedEntryRepository.delete(vanishedEntry);
  }

  VanishedEntry load(final UUID uuid) {
    return this.vanishedEntryRepository.load(uuid);
  }

}
