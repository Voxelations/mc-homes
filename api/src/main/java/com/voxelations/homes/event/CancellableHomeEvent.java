package com.voxelations.homes.event;

import com.voxelations.common.event.Cancellable;
import com.voxelations.homes.data.Home;
import com.voxelations.homes.data.HomeData;
import lombok.Data;

@Data
public abstract class CancellableHomeEvent implements Cancellable {

    /**
     * The relevant data context.
     **/
    private final HomeData dataContext;

    /**
     * The home.
     **/
    private final Home home;

    /**
     * Whether the event has been cancelled.
     **/
    private boolean cancelled = false;
}
