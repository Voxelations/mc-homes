package com.voxelations.homes.platform.paper;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.google.inject.TypeLiteral;
import com.google.inject.multibindings.Multibinder;
import com.voxelations.common.data.DataRepository;
import com.voxelations.common.data.DataService;
import com.voxelations.common.data.JooqCrudRepository;
import com.voxelations.common.platform.paper.data.PlayerDataService;
import com.voxelations.common.platform.paper.util.Schedulers;
import com.voxelations.homes.data.HomeData;
import com.voxelations.homes.platform.paper.data.HomeDataImpl;
import com.voxelations.homes.platform.paper.data.HomeImpl;
import org.jooq.DSLContext;
import org.jooq.Field;
import org.jooq.JSON;
import org.jooq.Record;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class HomesModule extends AbstractModule {

    private static final Gson GSON = new Gson();

    @Provides
    @Singleton
    public DataRepository<UUID, HomeData> provideHomeRepository(DSLContext dsl) {
        return new JooqCrudRepository<>(
                dsl,
                DSL.table(DSL.name("voxelations_homes")),
                ctx -> ctx.createTableIfNotExists("voxelations_homes")
                        .column("id", SQLDataType.UUID)
                        .column("data", SQLDataType.JSON)
                        .primaryKey("id")
                        .execute(),
                record -> new HomeDataImpl(
                        record.getValue("id", UUID.class),
                        new ConcurrentHashMap<>(GSON.fromJson(record.getValue("data", JSON.class).data(), new TypeToken<Map<String, HomeImpl>>() {}.getType())),
                        false
                ),
                entity -> {
                    Field<UUID> id = DSL.field(DSL.name("id"), UUID.class);
                    Field<JSON> data = DSL.field(DSL.name("data"), JSON.class);
                    Record record = dsl.newRecord(id, data);

                    record.set(id, entity.getId());
                    record.set(data, JSON.json(GSON.toJson(entity.getHomes())));

                    return record;
                }
        );
    }

    @Provides
    @Singleton
    public DataService<UUID, HomeData> provideHomeService(DataRepository<UUID, HomeData> repository, Schedulers schedulers) {
        return new PlayerDataService<>(repository, schedulers, id -> new HomeDataImpl(id, new ConcurrentHashMap<>(), false));
    }

    @Override
    protected void configure() {
        Multibinder<Object> containers = Multibinder.newSetBinder(binder(), Object.class);
        containers.addBinding().to(new TypeLiteral<DataRepository<UUID, HomeData>>() {});
        containers.addBinding().to(new TypeLiteral<DataService<UUID, HomeData>>() {});
    }
}
