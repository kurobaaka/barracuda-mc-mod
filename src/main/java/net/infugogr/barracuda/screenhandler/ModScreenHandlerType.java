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

    public static final ScreenHandlerType<UraniumGeneratorScreenHandler> URANIUM_GENERATOR =
            Registry.register(Registries.SCREEN_HANDLER, Barracuda.id("uranium_generator"),
                    new ExtendedScreenHandlerType<>(UraniumGeneratorScreenHandler::new));

    public static final ScreenHandlerType<FishingNetScreenHandler> FISHING_NET =
            Registry.register(Registries.SCREEN_HANDLER, new Identifier(Barracuda.MOD_ID, "fishing_net"),
                    new ExtendedScreenHandlerType<>(FishingNetScreenHandler::new));

    public static final ScreenHandlerType<SMESScreenHandler> SMES =
            Registry.register(Registries.SCREEN_HANDLER, Barracuda.id("smes"),
                    new ExtendedScreenHandlerType<>(SMESScreenHandler::new));

    public static final ScreenHandlerType<OilRefineryScreenHandler> OIL_REFINERY =
            Registry.register(Registries.SCREEN_HANDLER, Barracuda.id("oil_refinery"),
                    new ExtendedScreenHandlerType<>(OilRefineryScreenHandler::new));

    public static final ScreenHandlerType<ChemicalPlantScreenHandler> CHEMICAL_PLANT =
            Registry.register(Registries.SCREEN_HANDLER, Barracuda.id("chemical_plant"),
                    new ExtendedScreenHandlerType<>(ChemicalPlantScreenHandler::new));

    public static final ScreenHandlerType<CrusherScreenHandler> CRUSHER =
            Registry.register(Registries.SCREEN_HANDLER, Barracuda.id("crusher"),
                    new ExtendedScreenHandlerType<>(CrusherScreenHandler::new));

    public static final ScreenHandlerType<ElectricSmelterScreenHandler> ELECTRIC_SMELTER =
            Registry.register(Registries.SCREEN_HANDLER, Barracuda.id("electric_smelter"),
                    new ExtendedScreenHandlerType<>(ElectricSmelterScreenHandler::new));

    public static final ScreenHandlerType<TeleporterScreenHandler> TELEPORTER =
            Registry.register(Registries.SCREEN_HANDLER, Barracuda.id("teleporter"),
                    new ExtendedScreenHandlerType<>(TeleporterScreenHandler::new));

    public static final ScreenHandlerType<CentrifugeScreenHandler> CENTRIFUGE =
            Registry.register(Registries.SCREEN_HANDLER, Barracuda.id("centrifuge"),
                    new ExtendedScreenHandlerType<>(CentrifugeScreenHandler::new));

    public static final ScreenHandlerType<ClosetScreenHandler> CLOSET =
            Registry.register(Registries.SCREEN_HANDLER, Barracuda.id("closet"),
                    new ExtendedScreenHandlerType<>(ClosetScreenHandler::new));

    public static final ScreenHandlerType<CircuitImprinterScreenHandler> CIRCUIT_IMPRINTER =
            Registry.register(Registries.SCREEN_HANDLER, Barracuda.id("circuit_imprinter"),
                    new ExtendedScreenHandlerType<>(CircuitImprinterScreenHandler::new));

    public static void registerModScreenHandlerType() {
        Barracuda.LOGGER.info("Registering Mod Screen Handler Types for " + Barracuda.MOD_ID);
    }
}
