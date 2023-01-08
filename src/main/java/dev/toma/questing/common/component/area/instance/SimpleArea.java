package dev.toma.questing.common.component.area.instance;

import dev.toma.questing.Questing;
import dev.toma.questing.common.component.area.provider.SimpleAreaProvider;
import dev.toma.questing.common.component.area.spawner.Spawner;
import dev.toma.questing.common.party.Party;
import dev.toma.questing.common.quest.instance.Quest;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

import java.util.List;

public abstract class SimpleArea<P extends SimpleAreaProvider<?>> implements Area {

    private final P provider;
    private final BlockPos center;
    private final Vector3d a, b;
    private final List<Spawner> spawnerList;
    private boolean active;
    private boolean abandonded;
    protected int gracePeriod;

    public SimpleArea(P provider, BlockPos center, List<Spawner> spawnerList) {
        this.provider = provider;
        this.center = center;
        int size = provider.getAreaSize();
        this.a = new Vector3d(center.getX() + 0.5 - size, center.getY(), center.getZ() + 0.5 - size);
        this.b = new Vector3d(center.getX() + 0.5 + size, center.getY(), center.getZ() + 0.5 + size);
        this.spawnerList = spawnerList;
        this.gracePeriod = this.getDefaultGracePeriod();
    }

    protected SimpleArea(P provider, BlockPos center, Vector3d a, Vector3d b, List<Spawner> spawnerList, boolean active, boolean abandonded, int gracePeriod) {
        this.provider = provider;
        this.center = center;
        this.a = a;
        this.b = b;
        this.spawnerList = spawnerList;
        this.active = active;
        this.abandonded = abandonded;
        this.gracePeriod = gracePeriod;
    }

    @Override
    public void onUpdate(World world, Quest quest) {
        this.checkPlayers(world, quest);
        if (this.active) {
            this.spawnerList.forEach(spawner -> spawner.trySpawn(world, this, quest));
        }
    }

    @Override
    public Vector3d getLeftCorner() {
        return this.a;
    }

    @Override
    public Vector3d getRightCorner() {
        return this.b;
    }

    @Override
    public double getEntityDistance(Entity entity) {
        double x = this.center.getX() + 0.5 - entity.getX();
        double z = this.center.getZ() + 0.5 - entity.getZ();
        return Math.sqrt(x * x + z * z);
    }

    @Override
    public boolean isActiveArea() {
        return this.active;
    }

    @Override
    public boolean hasBeenAbandonded() {
        return this.abandonded;
    }

    @Override
    public boolean isWithin(double x, double y, double z) {
        return x >= this.a.x && z >= this.a.z && x <= this.b.x && z <= this.b.z;
    }

    @Override
    public P getAreaProvider() {
        return this.provider;
    }

    public BlockPos getCenter() {
        return center;
    }

    public List<Spawner> getSpawnerList() {
        return spawnerList;
    }

    public boolean isActive() {
        return active;
    }

    protected int getDefaultGracePeriod() {
        return Questing.config.areaGracePeriodDuration;
    }

    protected void setActive(boolean active) {
        this.active = active;
    }

    protected void checkPlayers(World world, Quest quest) {
        Party party = quest.getParty();
        List<PlayerEntity> players = party.getOnlineMembers(world, null);
        if (this.active) {
            boolean areAllIn = true;
            for (PlayerEntity player : players) {
                if (!this.isWithin(player)) {
                    areAllIn = false;
                    break;
                }
            }
            if (areAllIn) {
                this.gracePeriod = this.getDefaultGracePeriod();
            } else {
                if (--this.gracePeriod <= 0) {
                    this.abandonded = true;
                }
            }
        } else {
            for (PlayerEntity player : players) {
                if (!this.isWithin(player))
                    return;
            }
            this.setActive(true);
        }
    }
}
