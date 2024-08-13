package com.voxelations.homes.event;

import com.voxelations.homes.data.Home;
import com.voxelations.homes.data.HomeData;

/**
 * When a player is about to teleport to a home.
 */
public class PreTeleportHomeEvent extends CancellableHomeEvent {

    public PreTeleportHomeEvent(HomeData dataContext, Home home) {
        super(dataContext, home);
    }
}
