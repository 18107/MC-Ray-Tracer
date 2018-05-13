package mod.id107.raytracer.chunk;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

import mod.id107.raytracer.Log;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

public class Maps {
	
	private static HashMap<Integer, int[]> chunkMap;
	private static ArrayList<int[][]> idMapOld = new ArrayList<int[][]>(); //TODO remove
	private static int[][][] idMap;
	
	public static int[] getBlock(int id) {
		int[] block = chunkMap.get(id);
		if (block != null) {
			return block;
		} else {
			return new int[] {MapUtil.getBlock("error"), 0};
		}
	}
	
	//TODO remove below
	/*Gson gson = new Gson();
	Map<Integer, int[]> map = new HashMap<Integer, int[]>();
	map.put(5, new int[] {5, 4});
	map.put(3, new int[] {3, 6});
	map.put(4+(3<<12), new int[] {8, 16});
	String string = gson.toJson(map);
	Log.info(string);
	Map<Integer, int[]> map2 = gson.fromJson("{\"3\":[1,2],\"1\":[7,8]}", new TypeToken<Map<Integer, int[]>>(){}.getType());
	Log.info(map2.get(1)[0]);*/
	//TODO remove above
	
	//blockStateId = block.getIdFromBlock(block) + (block.getMetaFromState(state) << 12)
	public static void loadMaps() {
		chunkMap = new HashMap<Integer, int[]>();
		MapUtil.reset();
		chunkMap.put(0, new int[] {MapUtil.getBlock("air"), 0}); //air
		MapUtil.newBlock("error");
		chunkMap.put(1, new int[] {MapUtil.newBlock("stone"), 0});
		chunkMap.put(1 + (1<<12), new int[] {MapUtil.newBlock("granite"), 0});
		chunkMap.put(1 + (2<<12), new int[] {MapUtil.newBlock("polished granite"), 0});
		chunkMap.put(1 + (3<<12), new int[] {MapUtil.newBlock("diorite"), 0});
		chunkMap.put(1 + (4<<12), new int[] {MapUtil.newBlock("polished diorite"), 0});
		chunkMap.put(1 + (5<<12), new int[] {MapUtil.newBlock("andesite"), 0});
		chunkMap.put(1 + (6<<12), new int[] {MapUtil.newBlock("polished andesite"), 0});
		chunkMap.put(2, new int[] {MapUtil.newBlock("grass"), 0});
		chunkMap.put(3, new int[] {MapUtil.newBlock("dirt"), 0});
		chunkMap.put(3 + (1<<12), new int[] {MapUtil.newBlock("coarse dirt"), 0});
		chunkMap.put(3 + (2<<12), new int[] {MapUtil.newBlock("podzol"), 0});
		chunkMap.put(4, new int[] {MapUtil.newBlock("cobblestone"), 0});
		chunkMap.put(5, new int[] {MapUtil.newBlock("oak wood planks"), 0});
		chunkMap.put(5 + (1<<12), new int[] {MapUtil.newBlock("spruce wood planks"), 0});
		chunkMap.put(5 + (2<<12), new int[] {MapUtil.newBlock("birch wood planks"), 0});
		chunkMap.put(5 + (3<<12), new int[] {MapUtil.newBlock("jungle wood planks"), 0});
		chunkMap.put(5 + (4<<12), new int[] {MapUtil.newBlock("acacia wood planks"), 0});
		chunkMap.put(5 + (5<<12), new int[] {MapUtil.newBlock("dark oak wood planks"), 0});
		chunkMap.put(6, new int[] {MapUtil.newBlock("oak sapling"), 0});
		chunkMap.put(6 + (1<<12), new int[] {MapUtil.newBlock("spruce sapling"), 0});
		chunkMap.put(6 + (2<<12), new int[] {MapUtil.newBlock("birch sapling"), 0});
		chunkMap.put(6 + (3<<12), new int[] {MapUtil.newBlock("jungle sapling"), 0});
		chunkMap.put(6 + (4<<12), new int[] {MapUtil.newBlock("acacia sapling"), 0});
		chunkMap.put(6 + (5<<12), new int[] {MapUtil.newBlock("dark oak sapling"), 0});
		chunkMap.put(6 + (8<<12), new int[] {MapUtil.getBlock("oak sapling"), 0});
		chunkMap.put(6 + (9<<12), new int[] {MapUtil.getBlock("spruce sapling"), 0});
		chunkMap.put(6 + (10<<12), new int[] {MapUtil.getBlock("birch sapling"), 0});
		chunkMap.put(6 + (11<<12), new int[] {MapUtil.getBlock("jungle sapling"), 0});
		chunkMap.put(6 + (12<<12), new int[] {MapUtil.getBlock("acacia sapling"), 0});
		chunkMap.put(6 + (13<<12), new int[] {MapUtil.getBlock("dark oak sapling"), 0});
		chunkMap.put(7, new int[] {MapUtil.newBlock("bedrock"), 0});
		chunkMap.put(8, new int[] {MapUtil.newBlock("water16"), 0});
		chunkMap.put(8 + (1<<12), new int[] {MapUtil.newBlock("water14"), 0});
		chunkMap.put(8 + (2<<12), new int[] {MapUtil.newBlock("water12"), 0});
		chunkMap.put(8 + (3<<12), new int[] {MapUtil.newBlock("water10"), 0});
		chunkMap.put(8 + (4<<12), new int[] {MapUtil.newBlock("water08"), 0});
		chunkMap.put(8 + (5<<12), new int[] {MapUtil.newBlock("water06"), 0});
		chunkMap.put(8 + (6<<12), new int[] {MapUtil.newBlock("water04"), 0});
		chunkMap.put(8 + (7<<12), new int[] {MapUtil.newBlock("water02"), 0});
		chunkMap.put(9, new int[] {MapUtil.getBlock("water16"), 0});
		chunkMap.put(9 + (1<<12), new int[] {MapUtil.getBlock("water14"), 0});
		chunkMap.put(9 + (2<<12), new int[] {MapUtil.getBlock("water12"), 0});
		chunkMap.put(9 + (3<<12), new int[] {MapUtil.getBlock("water10"), 0});
		chunkMap.put(9 + (4<<12), new int[] {MapUtil.getBlock("water08"), 0});
		chunkMap.put(9 + (5<<12), new int[] {MapUtil.getBlock("water06"), 0});
		chunkMap.put(9 + (6<<12), new int[] {MapUtil.getBlock("water04"), 0});
		chunkMap.put(9 + (7<<12), new int[] {MapUtil.getBlock("water02"), 0});
		chunkMap.put(10, new int[] {MapUtil.newBlock("lava16"), 0});
		chunkMap.put(10 + (1<<12), new int[] {MapUtil.newBlock("lava14"), 0});
		chunkMap.put(10 + (2<<12), new int[] {MapUtil.newBlock("lava12"), 0});
		chunkMap.put(10 + (3<<12), new int[] {MapUtil.newBlock("lava10"), 0});
		chunkMap.put(10 + (4<<12), new int[] {MapUtil.newBlock("lava08"), 0});
		chunkMap.put(10 + (5<<12), new int[] {MapUtil.newBlock("lava06"), 0});
		chunkMap.put(10 + (6<<12), new int[] {MapUtil.newBlock("lava04"), 0});
		chunkMap.put(10 + (7<<12), new int[] {MapUtil.newBlock("lava02"), 0});
		chunkMap.put(11, new int[] {MapUtil.getBlock("lava16"), 0});
		chunkMap.put(11 + (1<<12), new int[] {MapUtil.getBlock("lava14"), 0});
		chunkMap.put(11 + (2<<12), new int[] {MapUtil.getBlock("lava12"), 0});
		chunkMap.put(11 + (3<<12), new int[] {MapUtil.getBlock("lava10"), 0});
		chunkMap.put(11 + (4<<12), new int[] {MapUtil.getBlock("lava08"), 0});
		chunkMap.put(11 + (5<<12), new int[] {MapUtil.getBlock("lava06"), 0});
		chunkMap.put(11 + (6<<12), new int[] {MapUtil.getBlock("lava04"), 0});
		chunkMap.put(11 + (7<<12), new int[] {MapUtil.getBlock("lava02"), 0});
		for (int i = 8; i < 16; i++) {
			chunkMap.put(8 + (i<<12), new int[] {MapUtil.getBlock("water16"), 0});
			chunkMap.put(9 + (i<<12), new int[] {MapUtil.getBlock("water16"), 0});
			chunkMap.put(10 + (i<<12), new int[] {MapUtil.getBlock("lava16"), 0});
			chunkMap.put(11 + (i<<12), new int[] {MapUtil.getBlock("lava16"), 0});
		}
		chunkMap.put(12, new int[] {MapUtil.newBlock("sand"), 0});
		chunkMap.put(12 + (1<<12), new int[] {MapUtil.newBlock("red sand"), 0});
		chunkMap.put(13, new int[] {MapUtil.newBlock("gravel"), 0});
		chunkMap.put(14, new int[] {MapUtil.newBlock("gold ore"), 0});
		chunkMap.put(15, new int[] {MapUtil.newBlock("iron ore"), 0});
		chunkMap.put(16, new int[] {MapUtil.newBlock("coal ore"), 0});
		chunkMap.put(17, new int[] {MapUtil.newBlock("oak wood"), 4});
		chunkMap.put(17 + (1<<12), new int[] {MapUtil.newBlock("spruce wood"), 4});
		chunkMap.put(17 + (2<<12), new int[] {MapUtil.newBlock("birch wood"), 4});
		chunkMap.put(17 + (3<<12), new int[] {MapUtil.newBlock("jungle wood"), 4});
		chunkMap.put(17 + (4<<12), new int[] {MapUtil.getBlock("oak wood"), 3});
		chunkMap.put(17 + (5<<12), new int[] {MapUtil.getBlock("spruce wood"), 3});
		chunkMap.put(17 + (6<<12), new int[] {MapUtil.getBlock("birch wood"), 3});
		chunkMap.put(17 + (7<<12), new int[] {MapUtil.getBlock("jungle wood"), 3});
		chunkMap.put(17 + (8<<12), new int[] {MapUtil.getBlock("oak wood"), 0});
		chunkMap.put(17 + (9<<12), new int[] {MapUtil.getBlock("spruce wood"), 0});
		chunkMap.put(17 + (10<<12), new int[] {MapUtil.getBlock("birch wood"), 0});
		chunkMap.put(17 + (11<<12), new int[] {MapUtil.getBlock("jungle wood"), 0});
		chunkMap.put(17 + (12<<12), new int[] {MapUtil.newBlock("oak bark"), 0});
		chunkMap.put(17 + (13<<12), new int[] {MapUtil.newBlock("spruce bark"), 0});
		chunkMap.put(17 + (14<<12), new int[] {MapUtil.newBlock("birch bark"), 0});
		chunkMap.put(17 + (15<<12), new int[] {MapUtil.newBlock("jungle bark"), 0});
		chunkMap.put(18, new int[] {MapUtil.newBlock("oak leaves"), 0});
		chunkMap.put(18 + (1<<12), new int[] {MapUtil.newBlock("spruce leaves"), 0});
		chunkMap.put(18 + (2<<12), new int[] {MapUtil.newBlock("birch leaves"), 0});
		chunkMap.put(18 + (3<<12), new int[] {MapUtil.newBlock("jungle leaves"), 0});
		for (int i = 4; i <= 12; i+=4) {
			chunkMap.put(18 + (i<<12), new int[] {MapUtil.getBlock("oak leaves"), 0});
			chunkMap.put(18 + ((i+1)<<12), new int[] {MapUtil.getBlock("spruce leaves"), 0});
			chunkMap.put(18 + ((i+2)<<12), new int[] {MapUtil.getBlock("birch leaves"), 0});
			chunkMap.put(18 + ((i+3)<<12), new int[] {MapUtil.getBlock("jungle leaves"), 0});
		}
		chunkMap.put(19, new int[] {MapUtil.newBlock("sponge"), 0});
		chunkMap.put(19 + (1<<12), new int[] {MapUtil.newBlock("wet sponge"), 0});
		chunkMap.put(20, new int[] {MapUtil.newBlock("glass"), 0});
		chunkMap.put(21, new int[] {MapUtil.newBlock("lapis ore"), 0});
		chunkMap.put(22, new int[] {MapUtil.newBlock("lapis block"), 0});
		chunkMap.put(23, new int[] {MapUtil.newBlock("dispenser vertical"), 5}); //TODO
		chunkMap.put(23 + (1<<12), new int[] {MapUtil.getBlock("dispenser vertical"), 4}); //TODO
		chunkMap.put(23 + (2<<12), new int[] {MapUtil.newBlock("dispenser horizontal"), 2});
		chunkMap.put(23 + (3<<12), new int[] {MapUtil.getBlock("dispenser horizontal"), 0});
		chunkMap.put(23 + (4<<12), new int[] {MapUtil.getBlock("dispenser horizontal"), 1});
		chunkMap.put(23 + (5<<12), new int[] {MapUtil.getBlock("dispenser horizontal"), 3});
		chunkMap.put(24, new int[] {MapUtil.newBlock("sandstone"), 0});
		chunkMap.put(24 + (1<<12), new int[] {MapUtil.newBlock("chiseled sandstone"), 0});
		chunkMap.put(24 + (2<<12), new int[] {MapUtil.newBlock("smooth sandstone"), 0});
		chunkMap.put(25, new int[] {MapUtil.newBlock("note block"), 0});
		chunkMap.put(26, new int[] {MapUtil.newBlock("bed feet"), 2});
		chunkMap.put(26 + (1<<12), new int[] {MapUtil.getBlock("bed feet"), 3});
		chunkMap.put(26 + (2<<12), new int[] {MapUtil.getBlock("bed feet"), 0});
		chunkMap.put(26 + (3<<12), new int[] {MapUtil.getBlock("bed feet"), 1});
		chunkMap.put(26 + (4<<12), new int[] {MapUtil.getBlock("bed feet"), 2});
		chunkMap.put(26 + (5<<12), new int[] {MapUtil.getBlock("bed feet"), 3});
		chunkMap.put(26 + (6<<12), new int[] {MapUtil.getBlock("bed feet"), 0});
		chunkMap.put(26 + (7<<12), new int[] {MapUtil.getBlock("bed feet"), 1});
		chunkMap.put(26 + (8<<12), new int[] {MapUtil.newBlock("bed head"), 2});
		chunkMap.put(26 + (9<<12), new int[] {MapUtil.getBlock("bed head"), 3});
		chunkMap.put(26 + (10<<12), new int[] {MapUtil.getBlock("bed head"), 0});
		chunkMap.put(26 + (11<<12), new int[] {MapUtil.getBlock("bed head"), 1});
		chunkMap.put(26 + (12<<12), new int[] {MapUtil.getBlock("bed head"), 2});
		chunkMap.put(26 + (13<<12), new int[] {MapUtil.getBlock("bed head"), 3});
		chunkMap.put(26 + (14<<12), new int[] {MapUtil.getBlock("bed head"), 0});
		chunkMap.put(26 + (15<<12), new int[] {MapUtil.getBlock("bed head"), 1});
		/*
		for (int i = 0; i < 2; i++) { //powered rail + detector rail
			chunkMap.put(27 + i, new int[] {72+i*4, 0}); //unpowered
			chunkMap.put(27 + i + (1<<12), new int[] {72+i*4, 1}); //unpowered
			chunkMap.put(27 + i + (2<<12), new int[] {73+i*4, 1}); //unpowered
			chunkMap.put(27 + i + (3<<12), new int[] {73+i*4, 3}); //unpowered
			chunkMap.put(27 + i + (4<<12), new int[] {73+i*4, 0}); //unpowered
			chunkMap.put(27 + i + (5<<12), new int[] {73+i*4, 2}); //unpowered
			chunkMap.put(27 + i + (8<<12), new int[] {74+i*4, 0});
			chunkMap.put(27 + i + (9<<12), new int[] {74+i*4, 1});
			chunkMap.put(27 + i + (10<<12), new int[] {75+i*4, 1});
			chunkMap.put(27 + i + (11<<12), new int[] {75+i*4, 3});
			chunkMap.put(27 + i + (12<<12), new int[] {75+i*4, 0});
			chunkMap.put(27 + i + (13<<12), new int[] {75+i*4, 2});
		}
		chunkMap.put(29, new int[] {80, 5}); //sticky piston
		chunkMap.put(29 + (1<<12), new int[] {80, 4}); //sticky piston
		chunkMap.put(29 + (2<<12), new int[] {80, 2}); //sticky piston
		chunkMap.put(29 + (3<<12), new int[] {80, 0}); //sticky piston
		chunkMap.put(29 + (4<<12), new int[] {80, 1}); //sticky piston
		chunkMap.put(29 + (5<<12), new int[] {80, 3}); //sticky piston
		chunkMap.put(29 + (8<<12), new int[] {81, 5}); //sticky piston (extended)
		chunkMap.put(29 + (9<<12), new int[] {81, 4}); //sticky piston (extended)
		chunkMap.put(29 + (10<<12), new int[] {81, 2}); //sticky piston (extended)
		chunkMap.put(29 + (11<<12), new int[] {81, 0}); //sticky piston (extended)
		chunkMap.put(29 + (12<<12), new int[] {81, 1}); //sticky piston (extended)
		chunkMap.put(29 + (13<<12), new int[] {81, 3}); //sticky piston (extended)
		chunkMap.put(30, new int[] {82, 0}); //cobweb
		chunkMap.put(31, new int[] {83, 0}); //shrub
		chunkMap.put(31 + (1<<12), new int[] {84, 0}); //tallgrass
		chunkMap.put(31 + (2<<12), new int[] {85, 0}); //fern
		chunkMap.put(32, new int[] {86, 0}); //dead bush
		chunkMap.put(33, new int[] {87, 5}); //piston
		chunkMap.put(33 + (1<<12), new int[] {87, 4}); //piston
		chunkMap.put(33 + (2<<12), new int[] {87, 2}); //piston
		chunkMap.put(33 + (3<<12), new int[] {87, 0}); //piston
		chunkMap.put(33 + (4<<12), new int[] {87, 1}); //piston
		chunkMap.put(33 + (5<<12), new int[] {87, 3}); //piston
		chunkMap.put(33 + (8<<12), new int[] {88, 5}); //piston (extended)
		chunkMap.put(33 + (9<<12), new int[] {88, 4}); //piston (extended)
		chunkMap.put(33 + (10<<12), new int[] {88, 2}); //piston (extended)
		chunkMap.put(33 + (11<<12), new int[] {88, 0}); //piston (extended)
		chunkMap.put(33 + (12<<12), new int[] {88, 1}); //piston (extended)
		chunkMap.put(33 + (13<<12), new int[] {88, 3}); //piston (extended)
		chunkMap.put(34, new int[] {89, 5}); //piston head
		chunkMap.put(34 + (1<<12), new int[] {89, 4}); //piston head
		chunkMap.put(34 + (2<<12), new int[] {89, 2}); //piston head
		chunkMap.put(34 + (3<<12), new int[] {89, 0}); //piston head
		chunkMap.put(34 + (4<<12), new int[] {89, 1}); //piston head
		chunkMap.put(34 + (5<<12), new int[] {89, 3}); //piston head
		chunkMap.put(34 + (8<<12), new int[] {90, 5}); //sticky piston head
		chunkMap.put(34 + (9<<12), new int[] {90, 4}); //sticky piston head
		chunkMap.put(34 + (10<<12), new int[] {90, 2}); //sticky piston head
		chunkMap.put(34 + (11<<12), new int[] {90, 0}); //sticky piston head
		chunkMap.put(34 + (12<<12), new int[] {90, 1}); //sticky piston head
		chunkMap.put(34 + (13<<12), new int[] {90, 3}); //sticky piston head
		chunkMap.put(35, new int[] {91, 0}); //wool white
		chunkMap.put(35 + (1<<12), new int[] {92, 0}); //wool orange
		chunkMap.put(35 + (2<<12), new int[] {93, 0}); //wool magenta
		chunkMap.put(35 + (3<<12), new int[] {94, 0}); //wool light blue
		chunkMap.put(35 + (4<<12), new int[] {95, 0}); //wool yellow
		chunkMap.put(35 + (5<<12), new int[] {96, 0}); //wool lime
		chunkMap.put(35 + (6<<12), new int[] {97, 0}); //wool pink
		chunkMap.put(35 + (7<<12), new int[] {98, 0}); //wool gray
		chunkMap.put(35 + (8<<12), new int[] {99, 0}); //wool light gray
		chunkMap.put(35 + (9<<12), new int[] {100, 0}); //wool cyan
		chunkMap.put(35 + (10<<12), new int[] {101, 0}); //wool purple
		chunkMap.put(35 + (11<<12), new int[] {102, 0}); //wool blue
		chunkMap.put(35 + (12<<12), new int[] {103, 0}); //wool brown
		chunkMap.put(35 + (13<<12), new int[] {104, 0}); //wool green
		chunkMap.put(35 + (14<<12), new int[] {105, 0}); //wool red
		chunkMap.put(35 + (15<<12), new int[] {106, 0}); //wool black
		chunkMap.put(37, new int[] {107, 0}); //yellow flower
		chunkMap.put(38, new int[] {108, 0}); //poppy (red flower)
		chunkMap.put(38 + (1<<12), new int[] {109, 0}); //blue orchid
		chunkMap.put(38 + (2<<12), new int[] {110, 0}); //allium
		chunkMap.put(38 + (3<<12), new int[] {111, 0}); //azure bluet
		chunkMap.put(38 + (4<<12), new int[] {112, 0}); //red tulip
		chunkMap.put(38 + (5<<12), new int[] {113, 0}); //orange tulip
		chunkMap.put(38 + (6<<12), new int[] {114, 0}); //white tulip
		chunkMap.put(38 + (7<<12), new int[] {115, 0}); //pink tulip
		chunkMap.put(38 + (8<<12), new int[] {116, 0}); //oxeye daisy
		chunkMap.put(39, new int[] {117, 0}); //brown mushroom
		chunkMap.put(40, new int[] {118, 0}); //red mushroom
		chunkMap.put(41, new int[] {119, 0}); //gold block
		chunkMap.put(42, new int[] {120, 0}); //iron block
		chunkMap.put(43, new int[] {121, 0}); //double stone slab
		chunkMap.put(43 + (1<<12), new int[] {122, 0}); //double sandstone slab
		chunkMap.put(43 + (2<<12), new int[] {123, 0}); //double (stone) wooden slab
		chunkMap.put(43 + (3<<12), new int[] {124, 0}); //double cobblestone slab
		chunkMap.put(43 + (4<<12), new int[] {125, 0}); //double bricks slab
		chunkMap.put(43 + (5<<12), new int[] {126, 0}); //double stone brick slab
		chunkMap.put(43 + (6<<12), new int[] {127, 0}); //double nether brick slab
		chunkMap.put(43 + (7<<12), new int[] {128, 0}); //double quartz slab
		chunkMap.put(43 + (8<<12), new int[] {129, 0}); //smooth double stone slab
		chunkMap.put(43 + (9<<12), new int[] {130, 0}); //smooth double sandstone slab
		chunkMap.put(43 + (15<<12), new int[] {131, 0}); //tile double quartz slab
		chunkMap.put(44, new int[] {132, 0}); //stone slab
		chunkMap.put(44 + (1<<12), new int[] {133, 0}); //sandstone slab
		chunkMap.put(44 + (2<<12), new int[] {134, 0}); //(stone) wooden slab
		chunkMap.put(44 + (3<<12), new int[] {135, 0}); //cobblestone slab
		chunkMap.put(44 + (4<<12), new int[] {136, 0}); //bricks slab
		chunkMap.put(44 + (5<<12), new int[] {137, 0}); //stone brick slab
		chunkMap.put(44 + (6<<12), new int[] {138, 0}); //nether brick slab
		chunkMap.put(44 + (7<<12), new int[] {139, 0}); //quartz slab
		chunkMap.put(44 + (8<<12), new int[] {140, 0}); //upper stone slab
		chunkMap.put(44 + (9<<12), new int[] {141, 0}); //upper sandstone slab
		chunkMap.put(44 + (10<<12), new int[] {142, 0}); //upper (stone) wooden slab
		chunkMap.put(44 + (11<<12), new int[] {143, 0}); //upper cobblestone slab
		chunkMap.put(44 + (12<<12), new int[] {144, 0}); //upper bricks slab
		chunkMap.put(44 + (13<<12), new int[] {145, 0}); //upper stone brick slab
		chunkMap.put(44 + (14<<12), new int[] {146, 0}); //upper nether brick slab
		chunkMap.put(44 + (15<<12), new int[] {147, 0}); //upper quartz slab
		chunkMap.put(45, new int[] {148, 0}); //bricks
		chunkMap.put(46, new int[] {149, 0}); //tnt
		chunkMap.put(47, new int[] {150, 0}); //bookshelf
		chunkMap.put(48, new int[] {151, 0}); //moss stone
		chunkMap.put(49, new int[] {152, 0}); //obsidian
		chunkMap.put(50 + (1<<12), new int[] {153, 3}); //torch
		chunkMap.put(50 + (2<<12), new int[] {153, 1}); //torch
		chunkMap.put(50 + (3<<12), new int[] {153, 0}); //torch
		chunkMap.put(50 + (4<<12), new int[] {153, 2}); //torch
		chunkMap.put(50 + (5<<12), new int[] {154, 0}); //torch
		for (int i = 0; i < 16; i++) {
			chunkMap.put(51 + (i<<12), new int[] {155, 0}); //fire
		}
		chunkMap.put(52, new int[] {156, 0}); //monster spawner
		chunkMap.put(53, new int[] {157, 1}); //oak wood stairs
		chunkMap.put(53 + (1<<12), new int[] {157, 3}); //oak wood stairs
		chunkMap.put(53 + (2<<12), new int[] {157, 2}); //oak wood stairs
		chunkMap.put(53 + (3<<12), new int[] {157, 0}); //oak wood stairs
		chunkMap.put(53 + (4<<12), new int[] {158, 1}); //oak wood stairs
		chunkMap.put(53 + (5<<12), new int[] {158, 3}); //oak wood stairs
		chunkMap.put(53 + (6<<12), new int[] {158, 2}); //oak wood stairs
		chunkMap.put(53 + (7<<12), new int[] {158, 0}); //oak wood stairs
		chunkMap.put(54 + (2<<12), new int[] {159, 2}); //chest
		chunkMap.put(54 + (3<<12), new int[] {159, 0}); //chest
		chunkMap.put(54 + (4<<12), new int[] {159, 1}); //chest
		chunkMap.put(54 + (5<<12), new int[] {159, 3}); //chest
		chunkMap.put(55, new int[] {160, 0}); //redstone wire
		for (int i = 1; i < 16; i++) {
			chunkMap.put(55 + (i<<12), new int[] {161, 0}); //redstone wire
		}
		chunkMap.put(56, new int[] {162, 0}); //diamond ore
		chunkMap.put(57, new int[] {163, 0}); //diamond block
		chunkMap.put(58, new int[] {164, 0}); //crafting table
		chunkMap.put(59, new int[] {165, 0}); //wheat
		chunkMap.put(59 + (1<<12), new int[] {166, 0}); //wheat
		chunkMap.put(59 + (2<<12), new int[] {167, 0}); //wheat
		chunkMap.put(59 + (3<<12), new int[] {168, 0}); //wheat
		chunkMap.put(59 + (4<<12), new int[] {169, 0}); //wheat
		chunkMap.put(59 + (5<<12), new int[] {170, 0}); //wheat
		chunkMap.put(59 + (6<<12), new int[] {171, 0}); //wheat
		chunkMap.put(59 + (7<<12), new int[] {172, 0}); //wheat
		chunkMap.put(60, new int[] {173, 0}); //farmland
		chunkMap.put(60 + (1<<12), new int[] {173, 0}); //farmland
		chunkMap.put(60 + (2<<12), new int[] {173, 0}); //farmland
		chunkMap.put(60 + (3<<12), new int[] {173, 0}); //farmland
		chunkMap.put(60 + (4<<12), new int[] {173, 0}); //farmland
		chunkMap.put(60 + (5<<12), new int[] {173, 0}); //farmland
		chunkMap.put(60 + (6<<12), new int[] {173, 0}); //farmland
		chunkMap.put(60 + (7<<12), new int[] {174, 0}); //farmland
		chunkMap.put(61 + (2<<12), new int[] {175, 2}); //furnace
		chunkMap.put(61 + (3<<12), new int[] {175, 0}); //furnace
		chunkMap.put(61 + (4<<12), new int[] {175, 1}); //furnace
		chunkMap.put(61 + (5<<12), new int[] {175, 3}); //furnace
		chunkMap.put(62 + (2<<12), new int[] {176, 2}); //furnace
		chunkMap.put(62 + (3<<12), new int[] {176, 0}); //furnace
		chunkMap.put(62 + (4<<12), new int[] {176, 1}); //furnace
		chunkMap.put(62 + (5<<12), new int[] {176, 3}); //furnace
		chunkMap.put(63, new int[] {177, 0}); //standing sign
		chunkMap.put(63 + (1<<12), new int[] {177, 0}); //standing sign
		chunkMap.put(63 + (2<<12), new int[] {177, 0}); //standing sign
		chunkMap.put(63 + (3<<12), new int[] {177, 1}); //standing sign
		chunkMap.put(63 + (4<<12), new int[] {177, 1}); //standing sign
		chunkMap.put(63 + (5<<12), new int[] {177, 1}); //standing sign
		chunkMap.put(63 + (6<<12), new int[] {177, 1}); //standing sign
		chunkMap.put(63 + (7<<12), new int[] {177, 2}); //standing sign
		chunkMap.put(63 + (8<<12), new int[] {177, 2}); //standing sign
		chunkMap.put(63 + (9<<12), new int[] {177, 2}); //standing sign
		chunkMap.put(63 + (10<<12), new int[] {177, 2}); //standing sign
		chunkMap.put(63 + (11<<12), new int[] {177, 3}); //standing sign
		chunkMap.put(63 + (12<<12), new int[] {177, 3}); //standing sign
		chunkMap.put(63 + (13<<12), new int[] {177, 3}); //standing sign
		chunkMap.put(63 + (14<<12), new int[] {177, 3}); //standing sign
		chunkMap.put(63 + (15<<12), new int[] {177, 0}); //standing sign
		*/
		//TODO
		
		idMap = new int[MapUtil.getAllBlocks().length][][];
		idMap[MapUtil.getBlock("air")] = MapUtil.nextVoxel("error", 0); //will not be rendered
		idMap[MapUtil.getBlock("error")] = MapUtil.nextVoxel("error", 0);
		idMap[MapUtil.getBlock("stone")] = MapUtil.nextTexture("stone", 0);
		idMap[MapUtil.getBlock("granite")] = MapUtil.nextTexture("stone_granite", 0);
		idMap[MapUtil.getBlock("polished granite")] = MapUtil.nextTexture("stone_granite_smooth", 0);
		idMap[MapUtil.getBlock("diorite")] = MapUtil.nextTexture("stone_diorite", 0);
		idMap[MapUtil.getBlock("polished diorite")] = MapUtil.nextTexture("stone_diorite_smooth", 0);
		idMap[MapUtil.getBlock("andesite")] = MapUtil.nextTexture("stone_andesite", 0);
		idMap[MapUtil.getBlock("polished andesite")] = MapUtil.nextTexture("stone_andesite_smooth", 0);
		idMap[MapUtil.getBlock("grass")] = MapUtil.nextTexture(new String[] {"grass_side", "grass_side", "grass_top", "dirt", "grass_side", "grass_side"}, 0);
		idMap[MapUtil.getBlock("dirt")] = MapUtil.nextTexture("dirt", 0);
		idMap[MapUtil.getBlock("coarse dirt")] = MapUtil.nextTexture("coarse_dirt", 0);
		idMap[MapUtil.getBlock("podzol")] = MapUtil.nextTexture(new String[] {"dirt_podzol_side", "dirt_podzol_side", "dirt_podzol_top", "dirt", "dirt_podzol_side", "dirt_podzol_side"}, 0);
		idMap[MapUtil.getBlock("cobblestone")] = MapUtil.nextTexture("cobblestone", 0);
		idMap[MapUtil.getBlock("oak wood planks")] = MapUtil.nextTexture("planks_oak", 0);
		idMap[MapUtil.getBlock("spruce wood planks")] = MapUtil.nextTexture("planks_spruce", 0);
		idMap[MapUtil.getBlock("birch wood planks")] = MapUtil.nextTexture("planks_birch", 0);
		idMap[MapUtil.getBlock("jungle wood planks")] = MapUtil.nextTexture("planks_jungle", 0);
		idMap[MapUtil.getBlock("acacia wood planks")] = MapUtil.nextTexture("planks_acacia", 0);
		idMap[MapUtil.getBlock("dark oak wood planks")] = MapUtil.nextTexture("planks_big_oak", 0);
		idMap[MapUtil.getBlock("oak sapling")] = MapUtil.nextVoxel("sapling_oak", 0);
		idMap[MapUtil.getBlock("spruce sapling")] = MapUtil.nextVoxel("sapling_spruce", 0);
		idMap[MapUtil.getBlock("birch sapling")] = MapUtil.nextVoxel("sapling_birch", 0);
		idMap[MapUtil.getBlock("jungle sapling")] = MapUtil.nextVoxel("sapling_jungle", 0);
		idMap[MapUtil.getBlock("acacia sapling")] = MapUtil.nextVoxel("sapling_acacia", 0);
		idMap[MapUtil.getBlock("dark oak sapling")] = MapUtil.nextVoxel("sapling_dark_oak", 0);
		idMap[MapUtil.getBlock("bedrock")] = MapUtil.nextTexture("bedrock", 0);
		idMap[MapUtil.getBlock("water16")] = MapUtil.nextVoxel("water16", 0);
		idMap[MapUtil.getBlock("water14")] = MapUtil.nextVoxel("water14", 0);
		idMap[MapUtil.getBlock("water12")] = MapUtil.nextVoxel("water12", 0);
		idMap[MapUtil.getBlock("water10")] = MapUtil.nextVoxel("water10", 0);
		idMap[MapUtil.getBlock("water08")] = MapUtil.nextVoxel("water08", 0);
		idMap[MapUtil.getBlock("water06")] = MapUtil.nextVoxel("water06", 0);
		idMap[MapUtil.getBlock("water04")] = MapUtil.nextVoxel("water04", 0);
		idMap[MapUtil.getBlock("water02")] = MapUtil.nextVoxel("water02", 0);
		idMap[MapUtil.getBlock("lava16")] = MapUtil.nextVoxel("lava16", 0);
		idMap[MapUtil.getBlock("lava14")] = MapUtil.nextVoxel("lava14", 0);
		idMap[MapUtil.getBlock("lava12")] = MapUtil.nextVoxel("lava12", 0);
		idMap[MapUtil.getBlock("lava10")] = MapUtil.nextVoxel("lava10", 0);
		idMap[MapUtil.getBlock("lava08")] = MapUtil.nextVoxel("lava08", 0);
		idMap[MapUtil.getBlock("lava06")] = MapUtil.nextVoxel("lava06", 0);
		idMap[MapUtil.getBlock("lava04")] = MapUtil.nextVoxel("lava04", 0);
		idMap[MapUtil.getBlock("lava02")] = MapUtil.nextVoxel("lava02", 0);
		idMap[MapUtil.getBlock("sand")] = MapUtil.nextTexture("sand", 0);
		idMap[MapUtil.getBlock("red sand")] = MapUtil.nextTexture("red_sand", 0);
		idMap[MapUtil.getBlock("gravel")] = MapUtil.nextTexture("gravel", 0);
		idMap[MapUtil.getBlock("gold ore")] = MapUtil.nextTexture("gold_ore", 0);
		idMap[MapUtil.getBlock("iron ore")] = MapUtil.nextTexture("iron_ore", 0);
		idMap[MapUtil.getBlock("coal ore")] = MapUtil.nextTexture("coal_ore", 0);
		idMap[MapUtil.getBlock("oak wood")] = MapUtil.nextTexture(new String[] {"log_oak", "log_oak", "log_oak_top", "log_oak_top", "log_oak", "log_oak"}, 0);
		idMap[MapUtil.getBlock("spruce wood")] = MapUtil.nextTexture(new String[] {"log_spruce", "log_spruce", "log_spruce_top", "log_spruce_top", "log_spruce", "log_spruce"}, 0);
		idMap[MapUtil.getBlock("birch wood")] = MapUtil.nextTexture(new String[] {"log_birch", "log_birch", "log_birch_top", "log_birch_top", "log_birch", "log_birch"}, 0);
		idMap[MapUtil.getBlock("jungle wood")] = MapUtil.nextTexture(new String[] {"log_jungle", "log_jungle", "log_jungle_top", "log_jungle_top", "log_jungle", "log_jungle"}, 0);
		idMap[MapUtil.getBlock("oak bark")] = MapUtil.nextTexture("log_oak", 0);
		idMap[MapUtil.getBlock("spruce bark")] = MapUtil.nextTexture("log_spruce", 0);
		idMap[MapUtil.getBlock("birch bark")] = MapUtil.nextTexture("log_birch", 0);
		idMap[MapUtil.getBlock("jungle bark")] = MapUtil.nextTexture("log_jungle", 0);
		idMap[MapUtil.getBlock("oak leaves")] = MapUtil.nextTexture("leaves_oak", 0);
		idMap[MapUtil.getBlock("spruce leaves")] = MapUtil.nextTexture("leaves_spruce", 0);
		idMap[MapUtil.getBlock("birch leaves")] = MapUtil.nextTexture("leaves_birch", 0);
		idMap[MapUtil.getBlock("jungle leaves")] = MapUtil.nextTexture("leaves_jungle", 0);
		idMap[MapUtil.getBlock("sponge")] = MapUtil.nextTexture("sponge", 0);
		idMap[MapUtil.getBlock("wet sponge")] = MapUtil.nextTexture("sponge_wet", 0);
		idMap[MapUtil.getBlock("glass")] = MapUtil.nextTexture("glass", 0);
		idMap[MapUtil.getBlock("lapis ore")] = MapUtil.nextTexture("lapis_ore", 0);
		idMap[MapUtil.getBlock("lapis block")] = MapUtil.nextTexture("lapis_block", 0);
		idMap[MapUtil.getBlock("dispenser vertical")] = MapUtil.nextTexture(new String[] {"furnace_top", "furnace_top", "furnace_top", "furnace_top", "dispenser_front_vertical", "furnace_top"}, 0);
		idMap[MapUtil.getBlock("dispenser horizontal")] = MapUtil.nextTexture(new String[] {"furnace_side", "furnace_side", "furnace_top", "furnace_top", "dispenser_front_horizontal", "furnace_side"}, 0);
		idMap[MapUtil.getBlock("sandstone")] = MapUtil.nextTexture(new String[] {"sandstone_normal", "sandstone_normal", "sandstone_top", "sandstone_bottom", "sandstone_normal", "sandstone_normal"}, 0);
		idMap[MapUtil.getBlock("chiseled sandstone")] = MapUtil.nextTexture(new String[] {"sandstone_carved", "sandstone_carved", "sandstone_top", "sandstone_top", "sandstone_carved", "sandstone_carved"}, 0);
		idMap[MapUtil.getBlock("smooth sandstone")] = MapUtil.nextTexture(new String[] {"sandstone_smooth", "sandstone_smooth", "sandstone_top", "sandstone_top", "sandstone_smooth", "sandstone_smooth"}, 0);
		idMap[MapUtil.getBlock("note block")] = MapUtil.nextTexture("noteblock", 0);
		idMap[MapUtil.getBlock("bed feet")] = MapUtil.nextVoxel("bed_feet", 0);
		idMap[MapUtil.getBlock("bed head")] = MapUtil.nextVoxel("bed_head", 0);
	}
	
	public static int[][][] getIdMap() {
		return idMap;
	}
}
