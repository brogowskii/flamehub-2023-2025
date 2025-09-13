package io.github.flamehub.crates;

import com.fasterxml.jackson.annotation.JsonIgnore;
import io.github.flamehub.commons.util.TimeUtil;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

public final class Crate implements Serializable {

  private final Map<Integer, CrateItem> itemsBySlot;

  private String id;
  private long enabledFrom;
  private final Set<Location> location;

  private String guiName;
  private ItemStack key;

  private String rotationTime;
  private long lastRotationTime;
  private int rotationItems;
  private Set<Integer> currentRotationSlots;

  public Crate(final String id) {
    this.id = id;
    enabledFrom = System.currentTimeMillis();
    itemsBySlot = new HashMap<>();
    location = new HashSet<>();
    currentRotationSlots = new HashSet<>();
    lastRotationTime = System.currentTimeMillis();
    rotationItems = 5;
  }

  public Crate() {
    itemsBySlot = new HashMap<>();
    location = new HashSet<>();
    currentRotationSlots = new HashSet<>();
    lastRotationTime = System.currentTimeMillis();
    rotationItems = 5;
  }

  public Map<Integer, CrateItem> getItemsBySlot() {
    return itemsBySlot;
  }

  @JsonIgnore
  public Collection<CrateItem> getItems() {
    if (hasRotation()) {
      return currentRotationSlots.stream()
          .map(itemsBySlot::get)
          .filter(Objects::nonNull)
          .collect(Collectors.toList());
    }
    return itemsBySlot.values();
  }

  public boolean hasRotation() {
    return rotationTime != null;
  }

  public boolean needsRotation() {
    if (!hasRotation()) {
      return false;
    }

    final Instant lastRotation = Instant.ofEpochMilli(lastRotationTime);
    final Instant nextRotation = lastRotation.plus(TimeUtil.parseTime(rotationTime));
    return Instant.now().isAfter(nextRotation);
  }

  public void performRotation() {
    if (!hasRotation() || itemsBySlot.isEmpty()) {
      return;
    }

    final List<Integer> availableSlots = new ArrayList<>(itemsBySlot.keySet());
    Collections.shuffle(availableSlots);

    currentRotationSlots.clear();

    int index = 0;
    while (currentRotationSlots.size() < rotationItems && index < availableSlots.size()) {
      currentRotationSlots.add(availableSlots.get(index));
      index++;
    }

    lastRotationTime = System.currentTimeMillis();
  }



  public Instant getNextRotationTime() {
    if (!hasRotation()) {
      return null;
    }

    return Instant.ofEpochMilli(lastRotationTime).plus(TimeUtil.parseTime(rotationTime));
  }

  public Duration getTimeUntilNextRotation() {
    if (!hasRotation()) {
      return null;
    }

    final Instant nextRotation = getNextRotationTime();
    return Duration.between(Instant.now(), nextRotation);
  }

  public void checkAndRotate() {
    if (needsRotation()) {
      performRotation();
    }
  }

  public boolean isEnabled() {
    return Instant.ofEpochMilli(enabledFrom).isBefore(Instant.now());
  }

  public Instant getEnabledFrom() {
    return Instant.ofEpochMilli(enabledFrom);
  }

  public void setEnabledFrom(final Instant enabledFrom) {
    this.enabledFrom = enabledFrom.toEpochMilli();
  }

  public String getId() {
    return id;
  }

  public Set<Location> getLocation() {
    return location;
  }

  public String getGuiName() {
    return guiName;
  }

  public void setGuiName(final String guiName) {
    this.guiName = guiName;
  }

  public ItemStack getKey() {
    return key;
  }

  public void setKey(final ItemStack key) {
    this.key = key;
  }

  public String getRotationTime() {
    return rotationTime;
  }

  public void setRotationTime(final String rotationTime) {
    this.rotationTime = rotationTime;
  }

  public long getLastRotationTime() {
    return lastRotationTime;
  }

  public void setLastRotationTime(final long lastRotationTime) {
    this.lastRotationTime = lastRotationTime;
  }

  public int getRotationItems() {
    return rotationItems;
  }

  public void setRotationItems(final int rotationItems) {
    this.rotationItems = rotationItems;
  }

  public Set<Integer> getCurrentRotationSlots() {
    return currentRotationSlots;
  }

  public void setCurrentRotationSlots(final Set<Integer> currentRotationSlots) {
    this.currentRotationSlots = currentRotationSlots;
  }
}