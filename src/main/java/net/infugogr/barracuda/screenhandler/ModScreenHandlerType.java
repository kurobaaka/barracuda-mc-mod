package net.infugogr.barracuda.screenhandler;

import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType;
import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.infugogr.barracuda.Barracuda;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.screen.ScreenHandlerType;
import net.minecraft.util.Identifier;

public class ModScreenHandlerType {

    public static final ScreenHandlerType<FuelGeneratorScreenHandler> FUEL_GENERATOR =
            Registry.register(Registries.SCREEN_HANDLER, Barracuda.id("fuel_generator"),
                    new ExtendedScreenHandlerType<>(FuelGeneratorScreenHandler::new));

    public static final ScreenHandlerType<FishingNetScreenHandler> FISHING_NET =
            Registry.register(Registries.SCREEN_HANDLER, new Identifier(Barracuda.MOD_ID, "fishing_net"),
                    new ExtendedScreenHandlerType<>(FishingNetScreenHandler::new));

    public static final ScreenHandlerType<SMESScreenHandler> SMES =
            Registry.register(Registries.SCREEN_HANDLER, Barracuda.id("smes"),
                    new ExtendedScreenHandlerType<>(SMESScreenHandler::new));

    //public static final ScreenHandlerType<SMESScreenHandler> TELEPORTER =
    //        Registry.register(Registries.SCREEN_HANDLER, Barracuda.id("teleporter"),
    //                new ExtendedScreenHandlerType<>(SMESScreenHandler::new));

    public static void registerModScreenHandlerType() {
        Barracuda.LOGGER.info("Registering Mod Screen Handler Types for " + Barracuda.MOD_ID);
    }
}
