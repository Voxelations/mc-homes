package com.voxelations.homes.data;

import com.voxelations.common.data.DataEntity;

import java.util.Map;
import java.util.UUID;

public interface HomeData extends DataEntity {

    /**
     * @return the player's id
     */
    UUID getId();

    /**
     * @return the homes, by name
     */
    Map<String, Home> getHomes();
}
