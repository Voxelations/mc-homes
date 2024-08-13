package com.voxelations.homes.platform.paper.data;

import com.voxelations.homes.data.Home;
import com.voxelations.homes.data.HomeData;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.Map;
import java.util.UUID;

@Data
@AllArgsConstructor
public class HomeDataImpl implements HomeData {

    private UUID id;
    private Map<String, Home> homes;
    private boolean dirty;
}
