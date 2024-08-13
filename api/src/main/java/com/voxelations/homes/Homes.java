package com.voxelations.homes;

import com.voxelations.common.data.DataService;
import com.voxelations.common.event.EventBus;
import com.voxelations.common.util.FinePosition;
import com.voxelations.homes.data.Home;
import com.voxelations.homes.data.HomeData;

import java.util.Map;
import java.util.UUID;

public interface Homes {

    /**
     * Create a home data entity.
     *
     * @param id the user's id
     * @param homes the homes
     * @param dirty whether the home data is dirty
     * @return the home data
     */
    HomeData homeData(UUID id, Map<String, Home> homes, boolean dirty);

    /**
     * Create a home.
     *
     * @param name the name of the home
     * @param position the position of the home
     * @return the home
     */
    Home home(String name, FinePosition position);

    /**
     * @return the event bus
     */
    EventBus getEventBus();

    /**
     * @return the data service
     */
    DataService<UUID, HomeData> getService();
}
