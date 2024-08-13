package com.voxelations.homes.event;

import com.voxelations.homes.data.Home;
import com.voxelations.homes.data.HomeData;

/**
 * When a player is about to delete a home.
 */
public class PreDeleteHomeEvent extends CancellableHomeEvent {

    public PreDeleteHomeEvent(HomeData dataContext, Home home) {
        super(dataContext, home);
    }
}
