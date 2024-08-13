package com.voxelations.homes.platform.paper;

import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.voxelations.common.data.DataService;
import com.voxelations.common.event.EventBus;
import com.voxelations.common.platform.paper.PaperPlugin;
import com.voxelations.common.util.FinePosition;
import com.voxelations.homes.Homes;
import com.voxelations.homes.HomesProvider;
import com.voxelations.homes.data.Home;
import com.voxelations.homes.data.HomeData;
import com.voxelations.homes.platform.paper.data.HomeDataImpl;
import com.voxelations.homes.platform.paper.data.HomeImpl;

import java.util.Map;
import java.util.UUID;

public class HomesPlugin extends PaperPlugin implements Homes {

    public HomesPlugin() {
        super(new HomesModule());
    }

    @Override
    public void enable() {
        HomesProvider.set(this);
    }

    @Override
    public void disable() {
        HomesProvider.set(null);
    }

    @Override
    public HomeData homeData(UUID id, Map<String, Home> homes, boolean dirty) {
        return new HomeDataImpl(id, homes, dirty);
    }

    @Override
    public Home home(String name, FinePosition position) {
        return new HomeImpl(name, position);
    }

    @Override
    public EventBus getEventBus() {
        return injector.getInstance(EventBus.class);
    }

    @Override
    public DataService<UUID, HomeData> getService() {
        return injector.getInstance(Key.get(new TypeLiteral<>() {}));
    }
}
