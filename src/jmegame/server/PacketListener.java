/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmegame.server;

import com.jme3.network.HostedConnection;
import com.jme3.network.Message;
import com.jme3.network.MessageListener;
import com.jme3.scene.Spatial;
import java.util.Map;
import java.util.UUID;
import jmegame.networking.MessagePlayerUpdate;
import jmegame.networking.ServerPlayerProfile;
import jmegame.networking.messages.MessageC2SGunSwitch;
import jmegame.weapons.PP2000;
import jmegame.weapons.Weapon;
import jmegame.weapons.WeaponRegistry;

/**
 *
 * @author campbell
 */
public class PacketListener implements MessageListener<HostedConnection> {

    private final Map<HostedConnection, ServerPlayerProfile> profiles;
    private final GameServer server;

    public final PacketListenerShoot shootListener = new PacketListenerShoot(this);

    public PacketListener(Map<HostedConnection, ServerPlayerProfile> profiles,
            GameServer server) {
        this.profiles = profiles;
        this.server = server;
    }

    /**
     * Handle messages sent from the client.
     *
     * @param source
     * @param message
     */
    @Override
    public void messageReceived(HostedConnection source, Message message) {
        if (message instanceof MessagePlayerUpdate) {
            // do something with the message
            MessagePlayerUpdate mpu = (MessagePlayerUpdate) message;
//            System.out.println("Received '" + mpu.getPosition()
//                    + "' with rotation '" + mpu.getRotation()
//                    + "' from client #" + source.getId());
            ServerPlayerProfile prof = profiles.get(source);
            if (prof == null) {
                prof = new ServerPlayerProfile(UUID.randomUUID(),
                        server.getAssetManager(), PP2000.INSTANCE);

                server.getPlayersNode().attachChild(
                        prof.getController().getRoot());

                prof.setHealth(100);
            }

            prof.update(mpu);

            Weapon w = WeaponRegistry.getInstance().getWeaponByID(mpu.getWeaponID());
            if (w != null) {
                prof.setWeapon(w);
            }

            profiles.put(source, prof);
        } else if (message instanceof MessageC2SGunSwitch) {
            MessageC2SGunSwitch sw = (MessageC2SGunSwitch) message;
            ServerPlayerProfile prof = profiles.get(source);
            if (prof != null) {
                Weapon newWeapon = WeaponRegistry.
                        getInstance().getWeaponByID(sw.getNewWeapon());
                prof.setWeapon(newWeapon);
            }
        }
    }

    public ServerPlayerProfile findProfileForPlayer(Spatial hit) {
        while (hit.getParent() != server.getPlayersNode()) {
            hit = hit.getParent();
        }
        for (HostedConnection conn : server.getProfiles().keySet()) {
            ServerPlayerProfile possiblePlayer = server.getProfiles().get(conn);
            if (possiblePlayer.getController().getRoot() == hit) {
                return possiblePlayer;
            }
        }
        return null;
    }

    public Map<HostedConnection, ServerPlayerProfile> getProfiles() {
        return profiles;
    }

    public GameServer getServer() {
        return server;
    }
}
