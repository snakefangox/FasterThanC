package net.snakefangox.fasterthanc;

import com.google.common.base.Supplier;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.biomes.v1.OverworldBiomes;
import net.fabricmc.fabric.api.biomes.v1.OverworldClimate;
import net.fabricmc.fabric.api.blockrenderlayer.v1.BlockRenderLayerMap;
import net.fabricmc.fabric.api.container.ContainerProviderRegistry;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.fabricmc.fabric.api.tool.attribute.v1.FabricToolTags;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.sound.BlockSoundGroup;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.Identifier;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.feature.DefaultFeatureConfig;
import net.minecraft.world.gen.feature.Feature;
import net.snakefangox.fasterthanc.blocks.*;
import net.snakefangox.fasterthanc.blocks.blockentities.*;
import net.snakefangox.fasterthanc.gui.*;
import net.snakefangox.fasterthanc.items.BeaconCoordsStorage;
import net.snakefangox.fasterthanc.items.DebugTool;
import net.snakefangox.fasterthanc.items.EnergyMeter;
import net.snakefangox.fasterthanc.items.ShipWeapon;
import net.snakefangox.fasterthanc.worldgen.CrashedShipFeature;
import net.snakefangox.fasterthanc.worldgen.LavaDotFeature;
import net.snakefangox.fasterthanc.worldgen.ShipGraveyardBiome;

import java.util.ArrayList;
import java.util.List;

public class FRegister {

	//Blocks
	public static final Block reactor_casing = new Block(FabricBlockSettings.of(Material.METAL).strength(3, 20));
	public static final ReactorController reactor_controller = new ReactorController(FabricBlockSettings.of(Material.METAL).strength(3, 20));
	public static final Block reactor_glass = new Block(FabricBlockSettings.of(Material.METAL).strength(3, 20).nonOpaque());
	public static final ReactorPort reactor_input = new ReactorPort(FabricBlockSettings.of(Material.METAL).strength(3, 20), ReactorPort.PortType.IN);
	public static final ReactorPort reactor_output = new ReactorPort(FabricBlockSettings.of(Material.METAL).strength(3, 20), ReactorPort.PortType.OUT);
	public static final ReactorTank reactor_tank = new ReactorTank(FabricBlockSettings.of(Material.METAL).strength(3, 20).nonOpaque());
	public static final ReactorEnergyPort reactor_energy_port = new ReactorEnergyPort(FabricBlockSettings.of(Material.METAL).strength(3, 20));
	public static final Block reactor_chamber = new Block(FabricBlockSettings.of(Material.METAL).strength(3, 20));
	public static final HighCapacityCable high_capacity_cable = new HighCapacityCable(FabricBlockSettings.of(Material.WOOL).strength(2, 5));
	public static final Block jump_drive_casing = new Block(FabricBlockSettings.of(Material.METAL).strength(3, 20));
	public static final JumpDriveController jump_drive_controller = new JumpDriveController(FabricBlockSettings.of(Material.METAL).strength(3, 20));
	public static final Block jump_drive_energy_port = new JumpDriveEnergyPort(FabricBlockSettings.of(Material.METAL).strength(3, 20));
	public static final Block jump_drive_field_chamber = new Block(FabricBlockSettings.of(Material.METAL).strength(3, 20));
	public static final Block jump_beacon = new JumpBeacon(FabricBlockSettings.of(Material.METAL).strength(3, 20).nonOpaque());
	public static final EnergyManagementComputer energy_management_computer = new EnergyManagementComputer(FabricBlockSettings.of(Material.METAL).strength(3, 20));
	public static final SolarPanel solar_panel = new SolarPanel(FabricBlockSettings.of(Material.METAL).strength(3, 20));
	public static final Block hardpoint = new Hardpoint(FabricBlockSettings.of(Material.METAL).strength(3, 20).nonOpaque());
	public static final Block jump_energy = new Block(FabricBlockSettings.of(Material.GLASS).strength(3, 20).nonOpaque());
	public static final Block jump_breaker = new Block(FabricBlockSettings.of(Material.METAL).strength(3, 20));
	public static final Block creative_energy_port = new CreativeEnergyPort(FabricBlockSettings.of(Material.METAL).strength(3, 20));
	public static final TargetingComputer targeting_computer = new TargetingComputer(FabricBlockSettings.of(Material.METAL).strength(3, 20));
	public static final Block holographic_sky = new HolographicSky(FabricBlockSettings.of(Material.GLASS).strength(3, 20).sounds(BlockSoundGroup.GLASS).nonOpaque().allowsSpawning((state, world, pos, type) -> false).solidBlock((state, world, pos) -> false).suffocates((state, world, pos) -> false));

	//Block Entity Types
	public static BlockEntityType<ReactorControllerBE> reactor_controller_type;
	public static BlockEntityType<ReactorPortBE> reactor_port_type;
	public static BlockEntityType<ReactorEnergyPortBE> reactor_energy_port_type;
	public static BlockEntityType<HighCapacityCableBE> high_capacity_cable_type;
	public static BlockEntityType<JumpDriveEnergyPortBE> jump_drive_energy_port_type;
	public static BlockEntityType<JumpDriveControllerBE> jump_drive_controller_type;
	public static BlockEntityType<EnergyManagementComputerBE> energy_management_computer_type;
	public static BlockEntityType<SolarPanelBE> solar_panel_type;
	public static BlockEntityType<HardpointBE> hardpoint_type;
	public static BlockEntityType<CreativeEnergyPortBE> creative_energy_port_type;
	public static BlockEntityType<TargetingComputerBE> targeting_computer_type;
	public static BlockEntityType<BlockEntity> holographic_sky_type;

	//Items
	public static final Item debug_tool = new DebugTool(new Item.Settings().group(FasterThanC.GENERAL).maxCount(1));
	public static final Item empty_fuel_cell = new Item(new Item.Settings().group(FasterThanC.GENERAL).maxCount(16));
	public static final Item fuel_cell = new Item(new Item.Settings().group(FasterThanC.GENERAL).maxCount(16));
	public static final Item energy_meter = new EnergyMeter(new Item.Settings().group(FasterThanC.GENERAL).maxCount(1));
	public static final Item beacon_coord_chip = new BeaconCoordsStorage(new Item.Settings().group(FasterThanC.GENERAL).maxCount(1));
	public static final Item basic_laser = new ShipWeapon(new Item.Settings().group(FasterThanC.GENERAL).maxCount(1));

	//Containers
	public static final Identifier five_slot_container = new Identifier(FasterThanC.MODID, "five_slot_container");
	public static final Identifier reactor_container = new Identifier(FasterThanC.MODID, "reactor_container");
	public static final Identifier jump_drive_container = new Identifier(FasterThanC.MODID, "jump_drive_container");
	public static final Identifier energy_computer_container = new Identifier(FasterThanC.MODID, "energy_computer_container");
	public static final Identifier targeting_computer_container = new Identifier(FasterThanC.MODID, "targeting_computer_container");

	//Sound Events
	public static SoundEvent JUMP_DRIVE_SPOOLS;
	public static SoundEvent LASER_FIRES;
	public static SoundEvent DARK_HALLS;

	//Biomes
	public static Biome SPACESHIP_GRAVEYARD;

	//Hull
	public static final List<Block> HULL_BLOCKS = new ArrayList<>();

	//Features
	public static Feature<DefaultFeatureConfig> CRASHED_SHIP;
	public static Feature<DefaultFeatureConfig> LAVA_DOT;

	public static void registerEverything() {
		registerBlock(reactor_casing, new Identifier(FasterThanC.MODID, "reactor_casing"));
		reactor_controller_type = registerBlock(reactor_controller, new Identifier(FasterThanC.MODID, "reactor_controller"), ReactorControllerBE::new);
		registerBlock(reactor_glass, new Identifier(FasterThanC.MODID, "reactor_glass"));
		registerBlock(reactor_input, new Identifier(FasterThanC.MODID, "reactor_input"));
		registerBlock(reactor_output, new Identifier(FasterThanC.MODID, "reactor_output"));
		reactor_port_type = Registry.register(Registry.BLOCK_ENTITY_TYPE, new Identifier(FasterThanC.MODID, "reactor_port"),
				BlockEntityType.Builder.create(ReactorPortBE::new, reactor_input, reactor_output).build(null));
		registerBlock(reactor_tank, new Identifier(FasterThanC.MODID, "reactor_tank"));
		reactor_energy_port_type = registerBlock(reactor_energy_port, new Identifier(FasterThanC.MODID,
				"reactor_energy_port"), ReactorEnergyPortBE::new);
		registerBlock(reactor_chamber, new Identifier(FasterThanC.MODID, "reactor_chamber"));
		high_capacity_cable_type = registerBlock(high_capacity_cable, new Identifier(FasterThanC.MODID, "high_capacity_cable"), HighCapacityCableBE::new);
		registerBlock(jump_drive_casing, new Identifier(FasterThanC.MODID, "jump_drive_casing"));
		jump_drive_controller_type = registerBlock(jump_drive_controller, new Identifier(FasterThanC.MODID, "jump_drive_controller"), JumpDriveControllerBE::new);
		jump_drive_energy_port_type = registerBlock(jump_drive_energy_port, new Identifier(FasterThanC.MODID, "jump_drive_energy_port"), JumpDriveEnergyPortBE::new);
		registerBlock(jump_drive_field_chamber, new Identifier(FasterThanC.MODID, "jump_drive_field_chamber"));
		registerBlock(jump_beacon, new Identifier(FasterThanC.MODID, "jump_beacon"));
		energy_management_computer_type = registerBlock(energy_management_computer, new Identifier(FasterThanC.MODID, "energy_management_computer"), EnergyManagementComputerBE::new);
		solar_panel_type = registerBlock(solar_panel, new Identifier(FasterThanC.MODID, "solar_panel"), SolarPanelBE::new);
		hardpoint_type = registerBlock(hardpoint, new Identifier(FasterThanC.MODID, "hardpoint"), HardpointBE::new);
		registerBlock(jump_energy, new Identifier(FasterThanC.MODID, "jump_energy"));
		registerBlock(jump_breaker, new Identifier(FasterThanC.MODID, "jump_breaker"));
		creative_energy_port_type = registerBlock(creative_energy_port, new Identifier(FasterThanC.MODID, "creative_energy_port"), CreativeEnergyPortBE::new);
		targeting_computer_type = registerBlock(targeting_computer, new Identifier(FasterThanC.MODID, "targeting_computer"), TargetingComputerBE::new);
		holographic_sky_type = registerBlock(holographic_sky, new Identifier(FasterThanC.MODID, "holographic_sky"), HolographicSky.BE::new);

		registerItem(debug_tool, new Identifier(FasterThanC.MODID, "debug_tool"));
		registerItem(empty_fuel_cell, new Identifier(FasterThanC.MODID, "empty_fuel_cell"));
		registerItem(fuel_cell, new Identifier(FasterThanC.MODID, "fuel_cell"));
		registerItem(energy_meter, new Identifier(FasterThanC.MODID, "energy_meter"));
		registerItem(beacon_coord_chip, new Identifier(FasterThanC.MODID, "beacon_coord_chip"));
		registerItem(basic_laser, new Identifier(FasterThanC.MODID, "basic_laser"));

		ContainerProviderRegistry.INSTANCE.registerFactory(five_slot_container,
				(syncId, id, player, buf) -> new FiveSlotContainer(syncId, player.inventory,
						(Inventory) player.world.getBlockEntity(buf.readBlockPos())));
		ContainerProviderRegistry.INSTANCE.registerFactory(reactor_container,
				(syncId, id, player, buf) -> new ReactorControllerContainer(syncId, player.inventory,
						(ReactorControllerBE) player.world.getBlockEntity(buf.readBlockPos())));
		ContainerProviderRegistry.INSTANCE.registerFactory(jump_drive_container,
				(syncId, id, player, buf) -> new JumpDriveControllerContainer(syncId, player.inventory,
						(JumpDriveControllerBE) player.world.getBlockEntity(buf.readBlockPos())));
		ContainerProviderRegistry.INSTANCE.registerFactory(energy_computer_container,
				(syncId, id, player, buf) -> new EnergyComputerContainer(syncId, player.inventory,
						(EnergyManagementComputerBE) player.world.getBlockEntity(buf.readBlockPos())));
		ContainerProviderRegistry.INSTANCE.registerFactory(targeting_computer_container,
				(syncId, id, player, buf) -> new TargetingComputerContainer(syncId, player.inventory,
						(TargetingComputerBE) player.world.getBlockEntity(buf.readBlockPos())));

		JUMP_DRIVE_SPOOLS = registerSoundEvent(new Identifier(FasterThanC.MODID, "jumpdrive"));
		LASER_FIRES = registerSoundEvent(new Identifier(FasterThanC.MODID, "laser_shot"));
		DARK_HALLS = registerSoundEvent(new Identifier(FasterThanC.MODID, "dark_halls"));

		CRASHED_SHIP = Registry.register(
				Registry.FEATURE,
				new Identifier(FasterThanC.MODID, "crashed_ship_parts"),
				new CrashedShipFeature(DefaultFeatureConfig.CODEC));
		LAVA_DOT = Registry.register(
				Registry.FEATURE,
				new Identifier(FasterThanC.MODID, "lava_dot"),
				new LavaDotFeature(DefaultFeatureConfig.CODEC));

		SPACESHIP_GRAVEYARD = Registry.register(Registry.BIOME,
				new Identifier(FasterThanC.MODID, "spaceship_graveyard"), new ShipGraveyardBiome());


		OverworldBiomes.addContinentalBiome(SPACESHIP_GRAVEYARD, OverworldClimate.DRY, 120D);

		registerHull();
	}

	@Environment(EnvType.CLIENT)
	public static void setRenderLayers() {
		setRenderLayer(reactor_glass, RenderLayerEnum.TRANSLUCENT);
		setRenderLayer(reactor_tank, RenderLayerEnum.TRANSLUCENT);
		setRenderLayer(jump_energy, RenderLayerEnum.TRANSLUCENT);
	}

	private static final String[] colours = new String[]{"white", "light_gray", "gray", "black", "brown", "red",
			"orange", "yellow", "lime", "green", "cyan", "light_blue", "blue", "purple", "magenta", "pink"};
	private static final String[] types = new String[]{"brick", "camo", "lined", "plate", "scale", "scatter",
			"scatter_plate", "skirting", "tile", "vert_brick"};

	public static void registerHull() {
		for (int c = 0; c < colours.length; c++) {
			for (int t = 0; t < types.length; t++) {
				String name = colours[c] + "_" + types[t] + "_hull";
				Identifier id = new Identifier(FasterThanC.MODID, name);
				Block hullBlock = new Block(FabricBlockSettings.of(Material.METAL).breakByTool(FabricToolTags.PICKAXES).strength(5, 15));
				Registry.register(Registry.BLOCK, id, hullBlock);
				Registry.register(Registry.ITEM, id, new BlockItem(hullBlock, new Item.Settings().group(FasterThanC.HULL)));
				HULL_BLOCKS.add(hullBlock);
			}
		}
	}

	private static void registerBlock(Block block, Identifier id) {
		Registry.register(Registry.BLOCK, id, block);
		Registry.register(Registry.ITEM, id, new BlockItem(block, new Item.Settings().group(FasterThanC.GENERAL)));
	}

	private static SoundEvent registerSoundEvent(Identifier id) {
		return Registry.register(Registry.SOUND_EVENT, id, new SoundEvent(id));
	}

	@SuppressWarnings("unchecked")
	private static <T extends BlockEntity> BlockEntityType<T> registerBlock(Block block, Identifier id,
																			Supplier<BlockEntity> be) {
		registerBlock(block, id);
		return (BlockEntityType<T>) Registry.register(Registry.BLOCK_ENTITY_TYPE, id,
				BlockEntityType.Builder.create(be, block).build(null));
	}

	@Environment(EnvType.CLIENT)
	private static void setRenderLayer(Block block, RenderLayerEnum layer) {
		switch (layer) {
			case CUTOUT:
				BlockRenderLayerMap.INSTANCE.putBlock(block, RenderLayer.getCutout());
				break;
			case TRANSLUCENT:
				BlockRenderLayerMap.INSTANCE.putBlock(block, RenderLayer.getTranslucent());
				break;
			default:
				break;
		}
	}

	private static void registerItem(Item item, Identifier id) {
		Registry.register(Registry.ITEM, id, item);
	}

	public enum RenderLayerEnum {
		CUTOUT, TRANSLUCENT;
	}
}
