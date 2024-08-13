package com.voxelations.homes.data;

import com.voxelations.common.util.FinePosition;

public interface Home {

    /**
     * @return the name
     */
    String getName();

    /**
     * @return the position
     */
    FinePosition getPosition();

    /**
     * @param name the name
     */
    void setName(String name);

    /**
     * @param position the position
     */
    void setPosition(FinePosition position);
}
