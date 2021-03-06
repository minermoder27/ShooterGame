/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package jmegame.networking;

import com.jme3.network.AbstractMessage;
import com.jme3.network.serializing.Serializable;
import java.util.UUID;
import jmegame.common.SUUID;

/**
 *
 * @author campbell
 */
@Serializable
public class MessagePlayerDisconnect extends AbstractMessage {

    private SUUID uuid;

    public MessagePlayerDisconnect() {
        super(true);
    }

    public MessagePlayerDisconnect(PlayerProfile profile) {
        this();
        uuid = profile.getUUID();
    }

    public MessagePlayerDisconnect(ServerPlayerProfile profile) {
        this(profile.makeSendableVarsion());
    }
    
    public SUUID getUUID() {
        return uuid;
    }
}
