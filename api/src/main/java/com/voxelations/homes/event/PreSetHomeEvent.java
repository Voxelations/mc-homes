package com.voxelations.homes.event;

import com.voxelations.homes.data.Home;
import com.voxelations.homes.data.HomeData;

/**
 * When a player is about to set a home.
 */
public class PreSetHomeEvent extends CancellableHomeEvent {

    public PreSetHomeEvent(HomeData dataContext, Home home) {
        super(dataContext, home);
    }
}
