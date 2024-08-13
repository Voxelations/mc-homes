package com.voxelations.homes;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.jetbrains.annotations.ApiStatus;

import java.util.Objects;

public class HomesProvider {

    @Nullable
    private static Homes instance = null;

    public static Homes get() {
        return Objects.requireNonNull(instance);
    }

    @ApiStatus.Internal
    public static void set(@Nullable Homes instance) {
        HomesProvider.instance = instance;
    }
}
