package io.github.flamehub.player.sync.data;

public final class PlayerSyncDataFacade {

  private final PlayerSyncDataRepository playerSyncDataRepository;

  public PlayerSyncDataFacade(
      final PlayerSyncDataRepository playerSyncDataRepository) {
    this.playerSyncDataRepository = playerSyncDataRepository;
  }


//  public PlayerSyncData load(final UUID uniqueId) {
//    PlayerSyncData syncData = playerSyncStorage.get(uniqueId.toString(), PlayerSyncData.class);
//    if (syncData == null) {
//      syncData = playerSyncDataRepository.load(uniqueId);
//      playerSyncStorage.set(uniqueId.toString(), syncData);
//    }
//    return syncData;
//  }
//
//  public PlayerSyncData save(final PlayerSyncData syncData) {
//    playerSyncStorage.set(syncData.getPlayerUniqueId().toString(), syncData);
//    return playerSyncDataRepository.save(syncData);
//  }
}
