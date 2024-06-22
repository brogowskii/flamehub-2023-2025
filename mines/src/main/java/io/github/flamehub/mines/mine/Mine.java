package io.github.flamehub.mines.mine;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.function.pattern.RandomPattern;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.regions.CuboidRegion;
import com.sk89q.worldedit.regions.Region;
import io.github.flamehub.commons.util.JsonPostDeserialize;
import io.github.flamehub.commons.util.RandomUtil;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public final class Mine implements Serializable {

    private String id;
    private String renewDelay;
    private long totalBlocks;

    @JsonIgnore
    private long lastTimeGenerate;

    private List<MineBlock> spawningBlocks;

    @JsonIgnore
    private List<Block> blocks;

    private Location firstLocation;
    private Location secondLocation;

    @JsonIgnore
    private RandomPattern randomPattern;

    @JsonIgnore
    private Region region;

    public Mine() {
    }

    public Mine(String id, String renewDelay, List<MineBlock> spawningBlocks, Location firstLocation, Location secondLocation) {
        this.id = id;
        this.renewDelay = renewDelay;
        this.spawningBlocks = spawningBlocks;
        this.blocks = new ArrayList<>();
        this.firstLocation = firstLocation;
        this.secondLocation = secondLocation;

        updateLocation();
        updateRandomPattern();
    }


    public void regenerate() {

        World world = firstLocation.getWorld();
        int x1 = firstLocation.getBlockX();
        int y1 = firstLocation.getBlockY();
        int z1 = firstLocation.getBlockZ();

        int x2 = secondLocation.getBlockX();
        int y2 = secondLocation.getBlockY();
        int z2 = secondLocation.getBlockZ();

        int minX = Math.min(x1, x2);
        int minY = Math.min(y1, y2);
        int minZ = Math.min(z1, z2);

        int maxX = Math.max(x1, x2);
        int maxY = Math.max(y1, y2);
        int maxZ = Math.max(z1, z2);

        for (int x = minX; x <= maxX; x++) {
            for (int y = minY; y <= maxY; y++) {
                for (int z = minZ; z <= maxZ; z++) {
                    Location blockLocation = new Location(world, x, y, z);
                    Block block = blockLocation.getBlock();
                    for (MineBlock spawningBlock : this.spawningBlocks) {
                        if (!RandomUtil.getChance(spawningBlock.getChance()))
                            continue;

                        block.setType(spawningBlock.getMaterial());
                    }

                }
            }
        }
    }

    @JsonPostDeserialize(forceAccess = true)
    private void updateRandomPattern() {
        this.randomPattern = new RandomPattern();
        for (MineBlock mineBlock : this.spawningBlocks) {
            randomPattern.add(BukkitAdapter.adapt(mineBlock.getMaterial().createBlockData()), mineBlock.getChance());
        }
    }

    @JsonPostDeserialize
    public void updateLocation() {
        int minX = Math.min(firstLocation.getBlockX(), secondLocation.getBlockX());
        int minY = Math.min(firstLocation.getBlockY(), secondLocation.getBlockY());
        int minZ = Math.min(firstLocation.getBlockZ(), secondLocation.getBlockZ());
        int maxX = Math.max(firstLocation.getBlockX(), secondLocation.getBlockX());
        int maxY = Math.max(firstLocation.getBlockY(), secondLocation.getBlockY());
        int maxZ = Math.max(firstLocation.getBlockZ(), secondLocation.getBlockZ());

        this.firstLocation.setX(minX);
        this.firstLocation.setY(minY);
        this.firstLocation.setZ(minZ);
        this.secondLocation.setX(maxX);
        this.secondLocation.setY(maxY);
        this.secondLocation.setZ(maxZ);

        this.region = new CuboidRegion(
                BukkitAdapter.adapt(firstLocation.getWorld()),
                BlockVector3.at(firstLocation.getX(), firstLocation.getY(), firstLocation.getZ()),
                BlockVector3.at(secondLocation.getX(), secondLocation.getY(), secondLocation.getZ())
        );
        this.totalBlocks = (long) region.getHeight() * region.getWidth() * region.getLength();
    }

    public String getId() {
        return id;
    }

    public String getRenewDelay() {
        return renewDelay;
    }

    public long getLastTimeGenerate() {
        return lastTimeGenerate;
    }

    public List<MineBlock> getSpawningBlocks() {
        return spawningBlocks;
    }

    public List<Block> getBlocks() {
        return blocks;
    }

    public Location getFirstLocation() {
        return firstLocation;
    }

    public Location getSecondLocation() {
        return secondLocation;
    }

    public RandomPattern getRandomPattern() {
        return randomPattern;
    }

    public Region getRegion() {
        return region;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setRenewDelay(String renewDelay) {
        this.renewDelay = renewDelay;
    }

    public void setLastTimeGenerate(long lastTimeGenerate) {
        this.lastTimeGenerate = lastTimeGenerate;
    }

    public void setSpawningBlocks(List<MineBlock> spawningBlocks) {
        this.spawningBlocks = spawningBlocks;
    }

    public void setBlocks(List<Block> blocks) {
        this.blocks = blocks;
    }

    public void setFirstLocation(Location firstLocation) {
        this.firstLocation = firstLocation;
    }

    public void setSecondLocation(Location secondLocation) {
        this.secondLocation = secondLocation;
    }

    public void setRandomPattern(RandomPattern randomPattern) {
        this.randomPattern = randomPattern;
    }

    public void setRegion(Region region) {
        this.region = region;
    }

    public long getTotalBlocks() {
        return totalBlocks;
    }

    public void setTotalBlocks(long totalBlocks) {
        this.totalBlocks = totalBlocks;
    }
}
