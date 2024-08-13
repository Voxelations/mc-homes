package com.voxelations.homes.platform.paper.data;

import com.voxelations.common.util.FinePosition;
import com.voxelations.homes.data.Home;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class HomeImpl implements Home {

    private String name;
    private FinePosition position;
}
