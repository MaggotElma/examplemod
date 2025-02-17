package com.example.examplemod;

import com.example.examplemod.item.custom.DestroyerItem;
import com.nimbusds.jose.util.Resource;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.level.block.SoundType;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

@Mod(ExampleMod.MODID)
public class ExampleMod
{
    public static final String MODID = "examplemod";
    private static final Logger LOGGER = LogUtils.getLogger();

    // BLOCKS register
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    // ITEMS register
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    // CREATIVE_MOD_TABS register
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);


    // METEORITE block
    public static final DeferredBlock<Block> METEORITE_BLOCK = BLOCKS.registerSimpleBlock("meteorite_block", BlockBehaviour.Properties.of().setId(ResourceKey.create(Registries.BLOCK, ResourceLocation.parse("examplemod:meteorite_block"))).strength(3f).requiresCorrectToolForDrops().sound(SoundType.STONE));
    // METEORITE_BLOCK_ITEM block item
    public static final DeferredItem<BlockItem> METEORITE_BLOCK_ITEM = ITEMS.register("meteorite_block", () -> new BlockItem(METEORITE_BLOCK.get(), new BlockItem.Properties().setId(ResourceKey.create(Registries.ITEM, ResourceLocation.parse("examplemod:meteorite_block")))));
    // METEORITE_ORE_ITEM item
    public static final DeferredItem<Item> METEORITE_DUST_ITEM = ITEMS.register("meteorite_dust", () -> new Item(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, ResourceLocation.parse("examplemod:meteorite_dust")))));
    // DESTROYER_ITEM item
    public static final DeferredItem<Item> DESTROYER_ITEM = ITEMS.register("destroyer", () -> new DestroyerItem(new Item.Properties().setId(ResourceKey.create(Registries.ITEM, ResourceLocation.parse("examplemod:destroyer"))).durability(10)));

    // TEST_FOOD food
    public static final DeferredItem<Item> GREEN_APPLE_FOOD_ITEM = ITEMS.register("green_apple", () -> new Item(new Item.Properties().food(new FoodProperties.Builder().nutrition(10).saturationModifier(0f).alwaysEdible().build()).setId(ResourceKey.create(Registries.ITEM, ResourceLocation.parse("examplemod:green_apple")))));


    // MOD_OBJECTS_TAB tab
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> MOD_OBJECTS_TAB = CREATIVE_MODE_TABS.register("mod_objects_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.mod_objects_tab"))
            .withTabsBefore(CreativeModeTabs.BUILDING_BLOCKS)
            .icon(() -> METEORITE_BLOCK_ITEM.get().getDefaultInstance())
            .displayItems((parameters, output ) -> {
                output.accept(METEORITE_BLOCK_ITEM.get());
                output.accept(METEORITE_DUST_ITEM.get());
                output.accept(DESTROYER_ITEM.get());
                output.accept(GREEN_APPLE_FOOD_ITEM.get());
            }).build());

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public ExampleMod(IEventBus modEventBus, ModContainer modContainer)
    {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so tabs get registered
        CREATIVE_MODE_TABS.register(modEventBus);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (ExampleMod) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);


    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.logDirtBlock)
            LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));

        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

        Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS) {
            //event.accept(EXAMPLE_BLOCK_ITEM);
        }
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }
    }
}
