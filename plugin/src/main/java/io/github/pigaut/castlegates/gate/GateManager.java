package io.github.pigaut.castlegates.gate;

import io.github.pigaut.castlegates.*;
import io.github.pigaut.castlegates.gate.state.*;
import io.github.pigaut.castlegates.gate.template.*;
import io.github.pigaut.castlegates.util.*;
import io.github.pigaut.sql.*;
import io.github.pigaut.voxel.*;
import io.github.pigaut.voxel.core.hologram.*;
import io.github.pigaut.voxel.core.structure.*;
import io.github.pigaut.voxel.plugin.manager.*;
import io.github.pigaut.yaml.convert.parse.*;
import io.github.pigaut.voxel.bukkit.Rotation;
import org.bukkit.*;
import org.bukkit.block.*;
import org.jetbrains.annotations.*;

import java.util.*;
import java.util.concurrent.*;

public class GateManager extends Manager {

    private final CastleGatesPlugin plugin;
    private final Set<Gate> gates = new HashSet<>();
    private final Map<Location, Gate> gateBlocks = new ConcurrentHashMap<>();
    private final Map<Gate, List<BlockState>> removedBlocks = new HashMap<>();

    public GateManager(CastleGatesPlugin plugin) {
        super(plugin);
        this.plugin = plugin;
    }

    @Override
    public void disable() {
        for (Gate gate : gates) {
            GateState state = gate.getState();
            state.cancelTransition();
            state.removeBlocks();
            state.removeHologram();
            List<BlockState> removedBlocks = this.removedBlocks.remove(gate);
            if (plugin.getSettings().isRestoreBlocksOnRemove() && removedBlocks != null) {
                for (BlockState removedBlock : removedBlocks) {
                    removedBlock.update(true, false);
                }
            }
        }
    }

    @Override
    public void loadData() {
        gates.clear();
        gateBlocks.clear();

        Database database = plugin.getDatabase();
        if (database == null) {
            logger.severe("Could not load data because database was not found.");
            return;
        }

        database.createTableIfNotExists("gates",
                "world VARCHAR(255)",
                "x INT NOT NULL",
                "y INT NOT NULL",
                "z INT NOT NULL",
                "gate VARCHAR(255) NOT NULL",
                "rotation VARCHAR(5) NOT NULL",
                "stage INT NOT NULL",
                "transition BOOLEAN",
                "PRIMARY KEY (world, x, y, z)"
        );

        database.createTableIfNotExists("invalid_gates",
                "world VARCHAR(255)",
                "x INT NOT NULL",
                "y INT NOT NULL",
                "z INT NOT NULL",
                "gate VARCHAR(255) NOT NULL",
                "rotation VARCHAR(5) NOT NULL",
                "stage INT NOT NULL",
                "transition BOOLEAN",
                "PRIMARY KEY (world, x, y, z)"
        );

        database.selectAll("gates").fetchAllRows(rowQuery -> {
            String worldId = rowQuery.getString(1);
            int x = rowQuery.getInt(2);
            int y = rowQuery.getInt(3);
            int z = rowQuery.getInt(4);
            String gateName = rowQuery.getString(5);
            String rotationData = rowQuery.getString(6);
            int stage = rowQuery.getInt(7);
            String transitionData = rowQuery.getString(8);

            try {
                registerGate(worldId, x, y, z, gateName, rotationData, stage, transitionData);
            }
            catch (GateCreateException e) {
                logger.warning(e.getMessage());
                database.merge("invalid_gates", "world, x, y, z",
                        "world", "x", "y", "z", "gate", "rotation", "stage", "transition")
                        .withParameter(worldId)
                        .withParameter(x)
                        .withParameter(y)
                        .withParameter(z)
                        .withParameter(gateName)
                        .withParameter(rotationData)
                        .withParameter(stage)
                        .withParameter(transitionData)
                        .executeUpdate();
            }
        });

        database.selectAll("invalid_gates").fetchAllRows(rowQuery -> {
            String worldId = rowQuery.getString(1);
            int x = rowQuery.getInt(2);
            int y = rowQuery.getInt(3);
            int z = rowQuery.getInt(4);
            String gateName = rowQuery.getString(5);
            String rotationData = rowQuery.getString(6);
            int stage = rowQuery.getInt(7);
            String transitionData = rowQuery.getString(8);

            try {
                registerGate(worldId, x, y, z, gateName, rotationData, stage, transitionData);
                logger.info(String.format("Restored gate at %s, %d, %d, %d. Reason: gate is no longer invalid.",
                    SpigotLibs.getWorldName(UUID.fromString(worldId)), x, y, z));
                database.createStatement("DELETE FROM invalid_gates WHERE world = ? AND x = ? AND y = ? AND z = ?")
                        .withParameter(worldId)
                        .withParameter(x)
                        .withParameter(y)
                        .withParameter(z)
                        .executeUpdate();
            }
            catch (GateCreateException ignored) {
                //Invalid gate is already inserted in the database
            }
        });

    }

    @Override
    public void saveData() {
        Database database = plugin.getDatabase();
        if (database == null) {
            logger.severe("Could not save data because database was not found.");
            return;
        }

        database.createTableIfNotExists("gates",
                "world VARCHAR(255)",
                "x INT NOT NULL",
                "y INT NOT NULL",
                "z INT NOT NULL",
                "gate VARCHAR(255) NOT NULL",
                "rotation VARCHAR(5) NOT NULL",
                "stage INT NOT NULL",
                "transition BOOLEAN",
                "PRIMARY KEY (world, x, y, z)"
        );

        database.clearTable("gates");

        DatabaseStatement insertStatement = database.merge("gates", "world, x, y, z",
                "world", "x", "y", "z", "gate", "rotation", "stage", "transition");

        for (Gate gate : gates) {
            Location location = gate.getOrigin();
            insertStatement.withParameter(location.getWorld().getUID().toString());
            insertStatement.withParameter(location.getBlockX());
            insertStatement.withParameter(location.getBlockY());
            insertStatement.withParameter(location.getBlockZ());
            insertStatement.withParameter(gate.getTemplate().getName());
            insertStatement.withParameter(gate.getRotation().toString());
            insertStatement.withParameter(gate.getState().getCurrentStage());
            insertStatement.withParameter(gate.getState().getTransition().toString());
            insertStatement.addBatch();
        }

        insertStatement.executeBatch();
    }

    @Override
    public boolean isAutoSave() {
        return true;
    }

    private void registerGate(String worldId, int x, int y, int z, String gateName, String rotationData, int stage, String transitionData) throws GateCreateException {
        World world = Bukkit.getWorld(UUID.fromString(worldId));
        if (world == null) {
            throw new GateCreateException(worldId, x, y, z, "world not found");
        }

        String worldName = world.getName();
        GateTemplate template = plugin.getGateTemplate(gateName);
        if (template == null) {
            throw new GateCreateException(worldName, x, y, z, "gate template not found");
        }

        Location origin = new Location(world, x, y, z);
        Rotation rotation = ParseUtil.parseEnumOrNull(Rotation.class, rotationData);
        if (rotation == null) {
            rotation = Rotation.NONE;
            logger.warning(String.format("Failed to load rotation of gate at %s, %d, %d, %d. Default rotation (NONE) has been applied.",
                    worldName, x, y, z));
        }

        GateTransition transition = transitionData != null ?
                ParseUtil.parseEnumOrNull(GateTransition.class, transitionData) : null;

        if (transition == null) {
            transition = GateTransition.NONE;
            logger.warning(String.format("Failed to load transition of gate at %s, %d, %d, %d. Transition (none) has been applied.",
                    worldName, x, y, z));
        }

        int maxStage = template.getMaxStage();
        if (stage > maxStage) {
            logger.warning(String.format("Failed to load stage of gate at %s, %d, %d, %d. Maximum stage (" + maxStage + ") has been applied.",
                    worldName, x, y, z));
        }

        if (transition == GateTransition.NONE && (stage != 0 && stage != maxStage - 1)) {
            stage = 0;
            logger.warning(String.format("Failed to load transition of gate at %s, %d, %d, %d. Minimum stage (0) has been applied.",
                    worldName, x, y, z));
        }

        for (Block block : template.getAllOccupiedBlocks(origin, rotation)) {
            if (plugin.getGates().isGate(block.getLocation())) {
                throw new GateOverlapException(world.getName(), x, y, z);
            }
        }

        int finalStage = Math.min(stage, template.getMaxStage());
        Rotation finalRotation = rotation;
        GateTransition finalTransition = transition;
        plugin.getScheduler().runTask(() -> {
            try {
                Gate.create(template, origin, finalRotation, finalStage, finalTransition);
            } catch (GateOverlapException ignored) {
                //Block overlaps are checked before scheduling
            }
        });
    }

    public Collection<Gate> getAllGates() {
        return new ArrayList<>(gates);
    }

    public boolean isGate(@NotNull Location location) {
        return gateBlocks.containsKey(location);
    }

    public @Nullable Gate getGate(@NotNull Location location) {
        return gateBlocks.get(location);
    }

    public void registerGate(@NotNull Gate gate) throws GateOverlapException {
        GateTemplate template = gate.getTemplate();

        List<BlockState> removedBlocks = new ArrayList<>();
        for (Block block : template.getAllOccupiedBlocks(gate.getOrigin(), gate.getRotation())) {
            if (plugin.getGates().isGate(block.getLocation())) {
                throw new GateOverlapException();
            }
            removedBlocks.add(block.getState());
        }

        gates.add(gate);
        for (Block block : gate.getOccupiedBlocks()) {
            gateBlocks.put(block.getLocation(), gate);
        }

        if (plugin.getSettings().isRestoreBlocksOnRemove()) {
            this.removedBlocks.put(gate, removedBlocks);
        }
    }

    public void unregisterGate(@NotNull Gate gate) {
        gates.remove(gate);
        for (Block block : gate.getOccupiedBlocks()) {
            gateBlocks.remove(block.getLocation());
        }

        List<BlockState> removedBlocks = this.removedBlocks.remove(gate);
        if (plugin.getSettings().isRestoreBlocksOnRemove() && removedBlocks != null) {
            for (BlockState removedBlock : removedBlocks) {
                removedBlock.update(true, false);
            }
        }
    }

}
