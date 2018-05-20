package mod.id107.raytracer.chunk;

import java.util.HashMap;

import mod.id107.raytracer.Log;
import mod.id107.raytracer.Matrices;

public class Maps {
	
	private static final int NONE = 0;
	private static final int FLIP = 1;
	
	private static final int RIGHT = 2;
	private static final int LEFT = 4;
	private static final int OVER = 6;
	
	private static final int EAST = 8;
	private static final int WEST = 16;
	private static final int NORTH = 24;
	private static final int UP = 32;
	private static final int DOWN = 40;
	
	private static HashMap<Integer, int[]> chunkMap;
	private static MapUtil mapUtil;
	private static int[][][] idMap;
	
	public static int[] getBlock(int id) {
		int[] block = chunkMap.get(id);
		if (block != null) {
			return block;
		} else {
			return new int[] {mapUtil.getBlock("error"), NONE};
		}
	}
	
	//TODO remove below
	/*Gson gson = new Gson();
	Map<Integer, int[]> map = new HashMap<Integer, int[]>();
	map.put(5, new int[] {5, UP});
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
		mapUtil = new MapUtil();
		chunkMap.put(0, new int[] {mapUtil.getBlock("air"), NONE}); //air
		mapUtil.newBlock("error");
		chunkMap.put(1, new int[] {mapUtil.newBlock("stone"), NONE});
		chunkMap.put(1 + (1<<12), new int[] {mapUtil.newBlock("granite"), NONE});
		chunkMap.put(1 + (2<<12), new int[] {mapUtil.newBlock("polished granite"), NONE});
		chunkMap.put(1 + (3<<12), new int[] {mapUtil.newBlock("diorite"), NONE});
		chunkMap.put(1 + (4<<12), new int[] {mapUtil.newBlock("polished diorite"), NONE});
		chunkMap.put(1 + (5<<12), new int[] {mapUtil.newBlock("andesite"), NONE});
		chunkMap.put(1 + (6<<12), new int[] {mapUtil.newBlock("polished andesite"), NONE});
		chunkMap.put(2, new int[] {mapUtil.newBlock("grass"), NONE});
		chunkMap.put(3, new int[] {mapUtil.newBlock("dirt"), NONE});
		chunkMap.put(3 + (1<<12), new int[] {mapUtil.newBlock("coarse dirt"), NONE});
		chunkMap.put(3 + (2<<12), new int[] {mapUtil.newBlock("podzol"), NONE});
		chunkMap.put(4, new int[] {mapUtil.newBlock("cobblestone"), NONE});
		chunkMap.put(5, new int[] {mapUtil.newBlock("oak wood planks"), NONE});
		chunkMap.put(5 + (1<<12), new int[] {mapUtil.newBlock("spruce wood planks"), NONE});
		chunkMap.put(5 + (2<<12), new int[] {mapUtil.newBlock("birch wood planks"), NONE});
		chunkMap.put(5 + (3<<12), new int[] {mapUtil.newBlock("jungle wood planks"), NONE});
		chunkMap.put(5 + (4<<12), new int[] {mapUtil.newBlock("acacia wood planks"), NONE});
		chunkMap.put(5 + (5<<12), new int[] {mapUtil.newBlock("dark oak wood planks"), NONE});
		chunkMap.put(6, new int[] {mapUtil.newBlock("oak sapling"), NONE});
		chunkMap.put(6 + (1<<12), new int[] {mapUtil.newBlock("spruce sapling"), NONE});
		chunkMap.put(6 + (2<<12), new int[] {mapUtil.newBlock("birch sapling"), NONE});
		chunkMap.put(6 + (3<<12), new int[] {mapUtil.newBlock("jungle sapling"), NONE});
		chunkMap.put(6 + (4<<12), new int[] {mapUtil.newBlock("acacia sapling"), NONE});
		chunkMap.put(6 + (5<<12), new int[] {mapUtil.newBlock("dark oak sapling"), NONE});
		chunkMap.put(6 + (8<<12), new int[] {mapUtil.getBlock("oak sapling"), NONE});
		chunkMap.put(6 + (9<<12), new int[] {mapUtil.getBlock("spruce sapling"), NONE});
		chunkMap.put(6 + (10<<12), new int[] {mapUtil.getBlock("birch sapling"), NONE});
		chunkMap.put(6 + (11<<12), new int[] {mapUtil.getBlock("jungle sapling"), NONE});
		chunkMap.put(6 + (12<<12), new int[] {mapUtil.getBlock("acacia sapling"), NONE});
		chunkMap.put(6 + (13<<12), new int[] {mapUtil.getBlock("dark oak sapling"), NONE});
		chunkMap.put(7, new int[] {mapUtil.newBlock("bedrock"), NONE});
		chunkMap.put(8, new int[] {mapUtil.newBlock("water16"), NONE});
		chunkMap.put(8 + (1<<12), new int[] {mapUtil.newBlock("water14"), NONE});
		chunkMap.put(8 + (2<<12), new int[] {mapUtil.newBlock("water12"), NONE});
		chunkMap.put(8 + (3<<12), new int[] {mapUtil.newBlock("water10"), NONE});
		chunkMap.put(8 + (4<<12), new int[] {mapUtil.newBlock("water08"), NONE});
		chunkMap.put(8 + (5<<12), new int[] {mapUtil.newBlock("water06"), NONE});
		chunkMap.put(8 + (6<<12), new int[] {mapUtil.newBlock("water04"), NONE});
		chunkMap.put(8 + (7<<12), new int[] {mapUtil.newBlock("water02"), NONE});
		chunkMap.put(9, new int[] {mapUtil.getBlock("water16"), NONE});
		chunkMap.put(9 + (1<<12), new int[] {mapUtil.getBlock("water14"), NONE});
		chunkMap.put(9 + (2<<12), new int[] {mapUtil.getBlock("water12"), NONE});
		chunkMap.put(9 + (3<<12), new int[] {mapUtil.getBlock("water10"), NONE});
		chunkMap.put(9 + (4<<12), new int[] {mapUtil.getBlock("water08"), NONE});
		chunkMap.put(9 + (5<<12), new int[] {mapUtil.getBlock("water06"), NONE});
		chunkMap.put(9 + (6<<12), new int[] {mapUtil.getBlock("water04"), NONE});
		chunkMap.put(9 + (7<<12), new int[] {mapUtil.getBlock("water02"), NONE});
		chunkMap.put(10, new int[] {mapUtil.newBlock("lava16"), NONE});
		chunkMap.put(10 + (1<<12), new int[] {mapUtil.newBlock("lava14"), NONE});
		chunkMap.put(10 + (2<<12), new int[] {mapUtil.newBlock("lava12"), NONE});
		chunkMap.put(10 + (3<<12), new int[] {mapUtil.newBlock("lava10"), NONE});
		chunkMap.put(10 + (4<<12), new int[] {mapUtil.newBlock("lava08"), NONE});
		chunkMap.put(10 + (5<<12), new int[] {mapUtil.newBlock("lava06"), NONE});
		chunkMap.put(10 + (6<<12), new int[] {mapUtil.newBlock("lava04"), NONE});
		chunkMap.put(10 + (7<<12), new int[] {mapUtil.newBlock("lava02"), NONE});
		chunkMap.put(11, new int[] {mapUtil.getBlock("lava16"), NONE});
		chunkMap.put(11 + (1<<12), new int[] {mapUtil.getBlock("lava14"), NONE});
		chunkMap.put(11 + (2<<12), new int[] {mapUtil.getBlock("lava12"), NONE});
		chunkMap.put(11 + (3<<12), new int[] {mapUtil.getBlock("lava10"), NONE});
		chunkMap.put(11 + (4<<12), new int[] {mapUtil.getBlock("lava08"), NONE});
		chunkMap.put(11 + (5<<12), new int[] {mapUtil.getBlock("lava06"), NONE});
		chunkMap.put(11 + (6<<12), new int[] {mapUtil.getBlock("lava04"), NONE});
		chunkMap.put(11 + (7<<12), new int[] {mapUtil.getBlock("lava02"), NONE});
		for (int i = 8; i < 16; i++) {
			chunkMap.put(8 + (i<<12), new int[] {mapUtil.getBlock("water16"), NONE});
			chunkMap.put(9 + (i<<12), new int[] {mapUtil.getBlock("water16"), NONE});
			chunkMap.put(10 + (i<<12), new int[] {mapUtil.getBlock("lava16"), NONE});
			chunkMap.put(11 + (i<<12), new int[] {mapUtil.getBlock("lava16"), NONE});
		}
		chunkMap.put(12, new int[] {mapUtil.newBlock("sand"), NONE});
		chunkMap.put(12 + (1<<12), new int[] {mapUtil.newBlock("red sand"), NONE});
		chunkMap.put(13, new int[] {mapUtil.newBlock("gravel"), NONE});
		chunkMap.put(14, new int[] {mapUtil.newBlock("gold ore"), NONE});
		chunkMap.put(15, new int[] {mapUtil.newBlock("iron ore"), NONE});
		chunkMap.put(16, new int[] {mapUtil.newBlock("coal ore"), NONE});
		chunkMap.put(17, new int[] {mapUtil.newBlock("oak wood"), NONE});
		chunkMap.put(17 + (1<<12), new int[] {mapUtil.newBlock("spruce wood"), NONE});
		chunkMap.put(17 + (2<<12), new int[] {mapUtil.newBlock("birch wood"), NONE});
		chunkMap.put(17 + (3<<12), new int[] {mapUtil.newBlock("jungle wood"), NONE});
		chunkMap.put(17 + (4<<12), new int[] {mapUtil.getBlock("oak wood"), RIGHT});
		chunkMap.put(17 + (5<<12), new int[] {mapUtil.getBlock("spruce wood"), RIGHT});
		chunkMap.put(17 + (6<<12), new int[] {mapUtil.getBlock("birch wood"), RIGHT});
		chunkMap.put(17 + (7<<12), new int[] {mapUtil.getBlock("jungle wood"), RIGHT});
		chunkMap.put(17 + (8<<12), new int[] {mapUtil.getBlock("oak wood"), DOWN});
		chunkMap.put(17 + (9<<12), new int[] {mapUtil.getBlock("spruce wood"), DOWN});
		chunkMap.put(17 + (10<<12), new int[] {mapUtil.getBlock("birch wood"), DOWN});
		chunkMap.put(17 + (11<<12), new int[] {mapUtil.getBlock("jungle wood"), DOWN});
		chunkMap.put(17 + (12<<12), new int[] {mapUtil.newBlock("oak bark"), NONE});
		chunkMap.put(17 + (13<<12), new int[] {mapUtil.newBlock("spruce bark"), NONE});
		chunkMap.put(17 + (14<<12), new int[] {mapUtil.newBlock("birch bark"), NONE});
		chunkMap.put(17 + (15<<12), new int[] {mapUtil.newBlock("jungle bark"), NONE});
		chunkMap.put(18, new int[] {mapUtil.newBlock("oak leaves"), NONE});
		chunkMap.put(18 + (1<<12), new int[] {mapUtil.newBlock("spruce leaves"), NONE});
		chunkMap.put(18 + (2<<12), new int[] {mapUtil.newBlock("birch leaves"), NONE});
		chunkMap.put(18 + (3<<12), new int[] {mapUtil.newBlock("jungle leaves"), NONE});
		for (int i = 4; i <= 12; i+=4) {
			chunkMap.put(18 + (i<<12), new int[] {mapUtil.getBlock("oak leaves"), NONE});
			chunkMap.put(18 + ((i+1)<<12), new int[] {mapUtil.getBlock("spruce leaves"), NONE});
			chunkMap.put(18 + ((i+2)<<12), new int[] {mapUtil.getBlock("birch leaves"), NONE});
			chunkMap.put(18 + ((i+3)<<12), new int[] {mapUtil.getBlock("jungle leaves"), NONE});
		}
		chunkMap.put(19, new int[] {mapUtil.newBlock("sponge"), NONE});
		chunkMap.put(19 + (1<<12), new int[] {mapUtil.newBlock("wet sponge"), NONE});
		chunkMap.put(20, new int[] {mapUtil.newBlock("glass"), NONE});
		chunkMap.put(21, new int[] {mapUtil.newBlock("lapis ore"), NONE});
		chunkMap.put(22, new int[] {mapUtil.newBlock("lapis block"), NONE});
		chunkMap.put(23, new int[] {mapUtil.newBlock("dispenser vertical"), DOWN}); //TODO
		chunkMap.put(23 + (1<<12), new int[] {mapUtil.getBlock("dispenser vertical"), UP}); //TODO
		chunkMap.put(23 + (2<<12), new int[] {mapUtil.newBlock("dispenser horizontal"), NORTH});
		chunkMap.put(23 + (3<<12), new int[] {mapUtil.getBlock("dispenser horizontal"), NONE});
		chunkMap.put(23 + (4<<12), new int[] {mapUtil.getBlock("dispenser horizontal"), WEST});
		chunkMap.put(23 + (5<<12), new int[] {mapUtil.getBlock("dispenser horizontal"), EAST});
		chunkMap.put(24, new int[] {mapUtil.newBlock("sandstone"), NONE});
		chunkMap.put(24 + (1<<12), new int[] {mapUtil.newBlock("chiseled sandstone"), NONE});
		chunkMap.put(24 + (2<<12), new int[] {mapUtil.newBlock("smooth sandstone"), NONE});
		chunkMap.put(25, new int[] {mapUtil.newBlock("note block"), NONE});
		chunkMap.put(26, new int[] {mapUtil.newBlock("bed feet"), NORTH});
		chunkMap.put(26 + (1<<12), new int[] {mapUtil.getBlock("bed feet"), EAST});
		chunkMap.put(26 + (2<<12), new int[] {mapUtil.getBlock("bed feet"), NONE});
		chunkMap.put(26 + (3<<12), new int[] {mapUtil.getBlock("bed feet"), WEST});
		chunkMap.put(26 + (4<<12), new int[] {mapUtil.getBlock("bed feet"), NORTH});
		chunkMap.put(26 + (5<<12), new int[] {mapUtil.getBlock("bed feet"), EAST});
		chunkMap.put(26 + (6<<12), new int[] {mapUtil.getBlock("bed feet"), NONE});
		chunkMap.put(26 + (7<<12), new int[] {mapUtil.getBlock("bed feet"), WEST});
		chunkMap.put(26 + (8<<12), new int[] {mapUtil.newBlock("bed head"), NORTH});
		chunkMap.put(26 + (9<<12), new int[] {mapUtil.getBlock("bed head"), EAST});
		chunkMap.put(26 + (10<<12), new int[] {mapUtil.getBlock("bed head"), NONE});
		chunkMap.put(26 + (11<<12), new int[] {mapUtil.getBlock("bed head"), WEST});
		chunkMap.put(26 + (12<<12), new int[] {mapUtil.getBlock("bed head"), NORTH});
		chunkMap.put(26 + (13<<12), new int[] {mapUtil.getBlock("bed head"), EAST});
		chunkMap.put(26 + (14<<12), new int[] {mapUtil.getBlock("bed head"), NONE});
		chunkMap.put(26 + (15<<12), new int[] {mapUtil.getBlock("bed head"), WEST});
		chunkMap.put(27, new int[] {mapUtil.newBlock("powered rail"), NONE});
		chunkMap.put(27 + (1<<12), new int[] {mapUtil.getBlock("powered rail"), WEST});
		chunkMap.put(27 + (2<<12), new int[] {mapUtil.newBlock("sloped powered rail"), WEST});
		chunkMap.put(27 + (3<<12), new int[] {mapUtil.getBlock("sloped powered rail"), EAST});
		chunkMap.put(27 + (4<<12), new int[] {mapUtil.getBlock("sloped powered rail"), NONE});
		chunkMap.put(27 + (5<<12), new int[] {mapUtil.getBlock("sloped powered rail"), NORTH});
		chunkMap.put(27 + (8<<12), new int[] {mapUtil.newBlock("activated powered rail"), NONE});
		chunkMap.put(27 + (9<<12), new int[] {mapUtil.getBlock("activated powered rail"), WEST});
		chunkMap.put(27 + (10<<12), new int[] {mapUtil.newBlock("activated sloped powered rail"), WEST});
		chunkMap.put(27 + (11<<12), new int[] {mapUtil.getBlock("activated sloped powered rail"), EAST});
		chunkMap.put(27 + (12<<12), new int[] {mapUtil.getBlock("activated sloped powered rail"), NONE});
		chunkMap.put(27 + (13<<12), new int[] {mapUtil.getBlock("activated sloped powered rail"), NORTH});
		chunkMap.put(28, new int[] {mapUtil.newBlock("detector rail"), NONE});
		chunkMap.put(28 + (1<<12), new int[] {mapUtil.getBlock("detector rail"), WEST});
		chunkMap.put(28 + (2<<12), new int[] {mapUtil.newBlock("sloped detector rail"), WEST});
		chunkMap.put(28 + (3<<12), new int[] {mapUtil.getBlock("sloped detector rail"), EAST});
		chunkMap.put(28 + (4<<12), new int[] {mapUtil.getBlock("sloped detector rail"), NONE});
		chunkMap.put(28 + (5<<12), new int[] {mapUtil.getBlock("sloped detector rail"), NORTH});
		chunkMap.put(28 + (8<<12), new int[] {mapUtil.newBlock("activated detector rail"), NONE});
		chunkMap.put(28 + (9<<12), new int[] {mapUtil.getBlock("activated detector rail"), WEST});
		chunkMap.put(28 + (10<<12), new int[] {mapUtil.newBlock("activated sloped detector rail"), WEST});
		chunkMap.put(28 + (11<<12), new int[] {mapUtil.getBlock("activated sloped detector rail"), EAST});
		chunkMap.put(28 + (12<<12), new int[] {mapUtil.getBlock("activated sloped detector rail"), NONE});
		chunkMap.put(28 + (13<<12), new int[] {mapUtil.getBlock("activated sloped detector rail"), NORTH});
		chunkMap.put(29, new int[] {mapUtil.newBlock("sticky piston retracted"), DOWN});
		chunkMap.put(29 + (1<<12), new int[] {mapUtil.getBlock("sticky piston retracted"), UP});
		chunkMap.put(29 + (2<<12), new int[] {mapUtil.getBlock("sticky piston retracted"), NORTH});
		chunkMap.put(29 + (3<<12), new int[] {mapUtil.getBlock("sticky piston retracted"), NONE});
		chunkMap.put(29 + (4<<12), new int[] {mapUtil.getBlock("sticky piston retracted"), WEST});
		chunkMap.put(29 + (5<<12), new int[] {mapUtil.getBlock("sticky piston retracted"), EAST});
		chunkMap.put(29 + (8<<12), new int[] {mapUtil.newBlock("piston extended"), DOWN});
		chunkMap.put(29 + (9<<12), new int[] {mapUtil.getBlock("piston extended"), UP});
		chunkMap.put(29 + (10<<12), new int[] {mapUtil.getBlock("piston extended"), NORTH});
		chunkMap.put(29 + (11<<12), new int[] {mapUtil.getBlock("piston extended"), NONE});
		chunkMap.put(29 + (12<<12), new int[] {mapUtil.getBlock("piston extended"), WEST});
		chunkMap.put(29 + (13<<12), new int[] {mapUtil.getBlock("piston extended"), EAST});
		chunkMap.put(30, new int[] {mapUtil.newBlock("cobweb"), NONE});
		chunkMap.put(31, new int[] {mapUtil.newBlock("shrub"), NONE});
		chunkMap.put(31 + (1<<12), new int[] {mapUtil.newBlock("tall grass"), NONE});
		chunkMap.put(31 + (2<<12), new int[] {mapUtil.newBlock("fern"), NONE});
		chunkMap.put(32, new int[] {mapUtil.newBlock("dead bush"), NONE});
		chunkMap.put(33, new int[] {mapUtil.newBlock("piston retracted"), DOWN});
		chunkMap.put(33 + (1<<12), new int[] {mapUtil.getBlock("piston retracted"), UP});
		chunkMap.put(33 + (2<<12), new int[] {mapUtil.getBlock("piston retracted"), NORTH});
		chunkMap.put(33 + (3<<12), new int[] {mapUtil.getBlock("piston retracted"), NONE});
		chunkMap.put(33 + (4<<12), new int[] {mapUtil.getBlock("piston retracted"), WEST});
		chunkMap.put(33 + (5<<12), new int[] {mapUtil.getBlock("piston retracted"), EAST});
		chunkMap.put(33 + (8<<12), new int[] {mapUtil.getBlock("piston extended"), DOWN});
		chunkMap.put(33 + (9<<12), new int[] {mapUtil.getBlock("piston extended"), UP});
		chunkMap.put(33 + (10<<12), new int[] {mapUtil.getBlock("piston extended"), NORTH});
		chunkMap.put(33 + (11<<12), new int[] {mapUtil.getBlock("piston extended"), NONE});
		chunkMap.put(33 + (12<<12), new int[] {mapUtil.getBlock("piston extended"), WEST});
		chunkMap.put(33 + (13<<12), new int[] {mapUtil.getBlock("piston extended"), EAST});
		chunkMap.put(34, new int[] {mapUtil.newBlock("piston head"), DOWN});
		chunkMap.put(34 + (1<<12), new int[] {mapUtil.getBlock("piston head"), UP});
		chunkMap.put(34 + (2<<12), new int[] {mapUtil.getBlock("piston head"), NORTH});
		chunkMap.put(34 + (3<<12), new int[] {mapUtil.getBlock("piston head"), NONE});
		chunkMap.put(34 + (4<<12), new int[] {mapUtil.getBlock("piston head"), WEST});
		chunkMap.put(34 + (5<<12), new int[] {mapUtil.getBlock("piston head"), EAST});
		chunkMap.put(34 + (8<<12), new int[] {mapUtil.newBlock("sticky piston head"), DOWN});
		chunkMap.put(34 + (9<<12), new int[] {mapUtil.getBlock("sticky piston head"), UP});
		chunkMap.put(34 + (10<<12), new int[] {mapUtil.getBlock("sticky piston head"), NORTH});
		chunkMap.put(34 + (11<<12), new int[] {mapUtil.getBlock("sticky piston head"), NONE});
		chunkMap.put(34 + (12<<12), new int[] {mapUtil.getBlock("sticky piston head"), WEST});
		chunkMap.put(34 + (13<<12), new int[] {mapUtil.getBlock("sticky piston head"), EAST});
		chunkMap.put(35, new int[] {mapUtil.newBlock("white wool"), NONE});
		chunkMap.put(35 + (1<<12), new int[] {mapUtil.newBlock("orange wool"), NONE});
		chunkMap.put(35 + (2<<12), new int[] {mapUtil.newBlock("magenta wool"), NONE});
		chunkMap.put(35 + (3<<12), new int[] {mapUtil.newBlock("light blue wool"), NONE});
		chunkMap.put(35 + (4<<12), new int[] {mapUtil.newBlock("yellow wool"), NONE});
		chunkMap.put(35 + (5<<12), new int[] {mapUtil.newBlock("lime wool"), NONE});
		chunkMap.put(35 + (6<<12), new int[] {mapUtil.newBlock("pink wool"), NONE});
		chunkMap.put(35 + (7<<12), new int[] {mapUtil.newBlock("gray wool"), NONE});
		chunkMap.put(35 + (8<<12), new int[] {mapUtil.newBlock("light gray wool"), NONE});
		chunkMap.put(35 + (9<<12), new int[] {mapUtil.newBlock("cyan wool"), NONE});
		chunkMap.put(35 + (10<<12), new int[] {mapUtil.newBlock("purple wool"), NONE});
		chunkMap.put(35 + (11<<12), new int[] {mapUtil.newBlock("blue wool"), NONE});
		chunkMap.put(35 + (12<<12), new int[] {mapUtil.newBlock("brown wool"), NONE});
		chunkMap.put(35 + (13<<12), new int[] {mapUtil.newBlock("green wool"), NONE});
		chunkMap.put(35 + (14<<12), new int[] {mapUtil.newBlock("red wool"), NONE});
		chunkMap.put(35 + (15<<12), new int[] {mapUtil.newBlock("black wool"), NONE});
		chunkMap.put(37, new int[] {mapUtil.newBlock("dandelion"), NONE});
		chunkMap.put(38, new int[] {mapUtil.newBlock("poppy"), NONE});
		chunkMap.put(38 + (1<<12), new int[] {mapUtil.newBlock("blue orchid"), NONE});
		chunkMap.put(38 + (2<<12), new int[] {mapUtil.newBlock("allium"), NONE});
		chunkMap.put(38 + (3<<12), new int[] {mapUtil.newBlock("azure bluet"), NONE});
		chunkMap.put(38 + (4<<12), new int[] {mapUtil.newBlock("red tulip"), NONE});
		chunkMap.put(38 + (5<<12), new int[] {mapUtil.newBlock("orange tulip"), NONE});
		chunkMap.put(38 + (6<<12), new int[] {mapUtil.newBlock("white tulip"), NONE});
		chunkMap.put(38 + (7<<12), new int[] {mapUtil.newBlock("pink tulip"), NONE});
		chunkMap.put(38 + (8<<12), new int[] {mapUtil.newBlock("oxeye daisy"), NONE});
		chunkMap.put(39, new int[] {mapUtil.newBlock("brown mushroom"), NONE});
		chunkMap.put(40, new int[] {mapUtil.newBlock("red mushroom"), NONE});
		chunkMap.put(41, new int[] {mapUtil.newBlock("gold block"), NONE});
		chunkMap.put(42, new int[] {mapUtil.newBlock("iron block"), NONE});
		chunkMap.put(43, new int[] {mapUtil.newBlock("double stone slab"), NONE});
		chunkMap.put(43 + (1<<12), new int[] {mapUtil.getBlock("sandstone"), NONE});
		chunkMap.put(43 + (2<<12), new int[] {mapUtil.getBlock("oak wood planks"), NONE});
		chunkMap.put(43 + (3<<12), new int[] {mapUtil.getBlock("cobblestone"), NONE});
		chunkMap.put(43 + (4<<12), new int[] {mapUtil.newBlock("bricks"), NONE});
		chunkMap.put(43 + (5<<12), new int[] {mapUtil.newBlock("stone brick"), NONE});
		chunkMap.put(43 + (6<<12), new int[] {mapUtil.newBlock("nether brick"), NONE});
		chunkMap.put(43 + (7<<12), new int[] {mapUtil.newBlock("quartz"), NONE});
		chunkMap.put(43 + (8<<12), new int[] {mapUtil.newBlock("smooth double stone slab"), NONE});
		chunkMap.put(43 + (9<<12), new int[] {mapUtil.newBlock("smooth double sandstone slab"), NONE});
		chunkMap.put(43 + (15<<12), new int[] {mapUtil.newBlock("smooth quartz slab"), NONE});
		//TODO slabs
		chunkMap.put(45, new int[] {mapUtil.getBlock("bricks"), NONE});
		chunkMap.put(46, new int[] {mapUtil.newBlock("tnt"), NONE});
		chunkMap.put(47, new int[] {mapUtil.newBlock("bookshelf"), NONE});
		chunkMap.put(48, new int[] {mapUtil.newBlock("mossy cobblestone"), NONE});
		chunkMap.put(49, new int[] {mapUtil.newBlock("obsidian"), NONE});
		chunkMap.put(50 + (1<<12), new int[] {mapUtil.newBlock("torch wall"), EAST});
		chunkMap.put(50 + (2<<12), new int[] {mapUtil.getBlock("torch wall"), WEST});
		chunkMap.put(50 + (3<<12), new int[] {mapUtil.getBlock("torch wall"), NONE});
		chunkMap.put(50 + (4<<12), new int[] {mapUtil.getBlock("torch wall"), NORTH});
		chunkMap.put(50 + (5<<12), new int[] {mapUtil.newBlock("torch floor"), NONE});
		chunkMap.put(51, new int[] {mapUtil.newBlock("fire"), NONE});
		chunkMap.put(51 + (1<<12), new int[] {mapUtil.getBlock("fire"), NONE});
		chunkMap.put(51 + (2<<12), new int[] {mapUtil.getBlock("fire"), NONE});
		chunkMap.put(51 + (3<<12), new int[] {mapUtil.getBlock("fire"), NONE});
		chunkMap.put(51 + (4<<12), new int[] {mapUtil.getBlock("fire"), NONE});
		chunkMap.put(51 + (5<<12), new int[] {mapUtil.getBlock("fire"), NONE});
		chunkMap.put(51 + (6<<12), new int[] {mapUtil.getBlock("fire"), NONE});
		chunkMap.put(51 + (7<<12), new int[] {mapUtil.getBlock("fire"), NONE});
		chunkMap.put(51 + (8<<12), new int[] {mapUtil.getBlock("fire"), NONE});
		chunkMap.put(51 + (9<<12), new int[] {mapUtil.getBlock("fire"), NONE});
		chunkMap.put(51 + (10<<12), new int[] {mapUtil.getBlock("fire"), NONE});
		chunkMap.put(51 + (11<<12), new int[] {mapUtil.getBlock("fire"), NONE});
		chunkMap.put(51 + (12<<12), new int[] {mapUtil.getBlock("fire"), NONE});
		chunkMap.put(51 + (13<<12), new int[] {mapUtil.getBlock("fire"), NONE});
		chunkMap.put(51 + (14<<12), new int[] {mapUtil.getBlock("fire"), NONE});
		chunkMap.put(51 + (15<<12), new int[] {mapUtil.getBlock("fire"), NONE});
		chunkMap.put(52, new int[] {mapUtil.newBlock("mob spawner"), NONE});
		chunkMap.put(53, new int[] {mapUtil.newBlock("oak stairs bottom"), WEST});
		chunkMap.put(53 + (1<<12), new int[] {mapUtil.getBlock("oak stairs bottom"), EAST});
		chunkMap.put(53 + (2<<12), new int[] {mapUtil.getBlock("oak stairs bottom"), NORTH});
		chunkMap.put(53 + (3<<12), new int[] {mapUtil.getBlock("oak stairs bottom"), NONE});
		chunkMap.put(53 + (4<<12), new int[] {mapUtil.newBlock("oak stairs top"), WEST});
		chunkMap.put(53 + (5<<12), new int[] {mapUtil.getBlock("oak stairs top"), EAST});
		chunkMap.put(53 + (6<<12), new int[] {mapUtil.getBlock("oak stairs top"), NORTH});
		chunkMap.put(53 + (7<<12), new int[] {mapUtil.getBlock("oak stairs top"), NONE});
		//TODO chest?, redstone wire
		chunkMap.put(56, new int[] {mapUtil.newBlock("diamond ore"), NONE});
		chunkMap.put(57, new int[] {mapUtil.newBlock("diamond block"), NONE});
		chunkMap.put(58, new int[] {mapUtil.newBlock("crafting table"), NONE});
		//TODO wheat, farmland
		chunkMap.put(61 + (2<<12), new int[] {mapUtil.newBlock("furnace"), NORTH});
		chunkMap.put(61 + (3<<12), new int[] {mapUtil.getBlock("furnace"), NONE});
		chunkMap.put(61 + (4<<12), new int[] {mapUtil.getBlock("furnace"), WEST});
		chunkMap.put(61 + (5<<12), new int[] {mapUtil.getBlock("furnace"), EAST});
		chunkMap.put(62 + (2<<12), new int[] {mapUtil.newBlock("furnace on"), NORTH});
		chunkMap.put(62 + (3<<12), new int[] {mapUtil.getBlock("furnace on"), NONE});
		chunkMap.put(62 + (4<<12), new int[] {mapUtil.getBlock("furnace on"), WEST});
		chunkMap.put(62 + (5<<12), new int[] {mapUtil.getBlock("furnace on"), EAST});
		//TODO standing sign
		//rotation is added later
		chunkMap.put(64, new int[] {mapUtil.newBlock("oak door lower"), NONE});
		chunkMap.put(64 + (1<<12), new int[] {mapUtil.getBlock("oak door lower"), NONE});
		chunkMap.put(64 + (2<<12), new int[] {mapUtil.getBlock("oak door lower"), NONE});
		chunkMap.put(64 + (3<<12), new int[] {mapUtil.getBlock("oak door lower"), NONE});
		chunkMap.put(64 + (4<<12), new int[] {mapUtil.getBlock("oak door lower"), NONE});
		chunkMap.put(64 + (5<<12), new int[] {mapUtil.getBlock("oak door lower"), NONE});
		chunkMap.put(64 + (6<<12), new int[] {mapUtil.getBlock("oak door lower"), NONE});
		chunkMap.put(64 + (7<<12), new int[] {mapUtil.getBlock("oak door lower"), NONE});
		chunkMap.put(64 + (8<<12), new int[] {mapUtil.newBlock("oak door upper"), NONE});
		chunkMap.put(64 + (9<<12), new int[] {mapUtil.getBlock("oak door upper"), NONE});
		chunkMap.put(64 + (10<<12), new int[] {mapUtil.getBlock("oak door upper"), NONE});
		chunkMap.put(64 + (11<<12), new int[] {mapUtil.getBlock("oak door upper"), NONE});
		chunkMap.put(65 + (2<<12), new int[] {mapUtil.newBlock("ladder"), NORTH});
		chunkMap.put(65 + (3<<12), new int[] {mapUtil.getBlock("ladder"), NONE});
		chunkMap.put(65 + (4<<12), new int[] {mapUtil.getBlock("ladder"), WEST});
		chunkMap.put(65 + (5<<12), new int[] {mapUtil.getBlock("ladder"), EAST});
		chunkMap.put(66, new int[] {mapUtil.newBlock("rail straight"), NONE});
		chunkMap.put(66 + (1<<12), new int[] {mapUtil.getBlock("rail straight"), WEST});
		chunkMap.put(66 + (2<<12), new int[] {mapUtil.newBlock("rail sloped"), WEST});
		chunkMap.put(66 + (3<<12), new int[] {mapUtil.getBlock("rail sloped"), EAST});
		chunkMap.put(66 + (4<<12), new int[] {mapUtil.getBlock("rail sloped"), NONE});
		chunkMap.put(66 + (5<<12), new int[] {mapUtil.getBlock("rail sloped"), NORTH});
		chunkMap.put(66 + (6<<12), new int[] {mapUtil.newBlock("rail curved"), WEST});
		chunkMap.put(66 + (7<<12), new int[] {mapUtil.getBlock("rail curved"), NORTH});
		chunkMap.put(66 + (8<<12), new int[] {mapUtil.getBlock("rail curved"), EAST});
		chunkMap.put(66 + (9<<12), new int[] {mapUtil.getBlock("rail curved"), NONE});
		chunkMap.put(67, new int[] {mapUtil.newBlock("cobblestone stairs bottom"), WEST});
		chunkMap.put(67 + (1<<12), new int[] {mapUtil.getBlock("cobblestone stairs bottom"), EAST});
		chunkMap.put(67 + (2<<12), new int[] {mapUtil.getBlock("cobblestone stairs bottom"), NORTH});
		chunkMap.put(67 + (3<<12), new int[] {mapUtil.getBlock("cobblestone stairs bottom"), NONE});
		chunkMap.put(67 + (4<<12), new int[] {mapUtil.newBlock("cobblestone stairs top"), WEST});
		chunkMap.put(67 + (5<<12), new int[] {mapUtil.getBlock("cobblestone stairs top"), EAST});
		chunkMap.put(67 + (6<<12), new int[] {mapUtil.getBlock("cobblestone stairs top"), NORTH});
		chunkMap.put(67 + (7<<12), new int[] {mapUtil.getBlock("cobblestone stairs top"), NONE});
		//TODO sign
		
		idMap = new int[mapUtil.getAllBlocks().length][][];
		idMap[mapUtil.getBlock("air")] = mapUtil.nextVoxel("error", 0); //will not be rendered
		idMap[mapUtil.getBlock("error")] = mapUtil.nextVoxel("error", 0);
		idMap[mapUtil.getBlock("stone")] = mapUtil.nextTexture("stone", 0);
		idMap[mapUtil.getBlock("granite")] = mapUtil.nextTexture("stone_granite", 0);
		idMap[mapUtil.getBlock("polished granite")] = mapUtil.nextTexture("stone_granite_smooth", 0);
		idMap[mapUtil.getBlock("diorite")] = mapUtil.nextTexture("stone_diorite", 0);
		idMap[mapUtil.getBlock("polished diorite")] = mapUtil.nextTexture("stone_diorite_smooth", 0);
		idMap[mapUtil.getBlock("andesite")] = mapUtil.nextTexture("stone_andesite", 0);
		idMap[mapUtil.getBlock("polished andesite")] = mapUtil.nextTexture("stone_andesite_smooth", 0);
		idMap[mapUtil.getBlock("grass")] = mapUtil.nextTexture(new String[] {"grass_side", "grass_side", "grass_top", "dirt", "grass_side", "grass_side"}, 0);
		idMap[mapUtil.getBlock("dirt")] = mapUtil.nextTexture("dirt", 0);
		idMap[mapUtil.getBlock("coarse dirt")] = mapUtil.nextTexture("coarse_dirt", 0);
		idMap[mapUtil.getBlock("podzol")] = mapUtil.nextTexture(new String[] {"dirt_podzol_side", "dirt_podzol_side", "dirt_podzol_top", "dirt", "dirt_podzol_side", "dirt_podzol_side"}, 0);
		idMap[mapUtil.getBlock("cobblestone")] = mapUtil.nextTexture("cobblestone", 0);
		idMap[mapUtil.getBlock("oak wood planks")] = mapUtil.nextTexture("planks_oak", 0);
		idMap[mapUtil.getBlock("spruce wood planks")] = mapUtil.nextTexture("planks_spruce", 0);
		idMap[mapUtil.getBlock("birch wood planks")] = mapUtil.nextTexture("planks_birch", 0);
		idMap[mapUtil.getBlock("jungle wood planks")] = mapUtil.nextTexture("planks_jungle", 0);
		idMap[mapUtil.getBlock("acacia wood planks")] = mapUtil.nextTexture("planks_acacia", 0);
		idMap[mapUtil.getBlock("dark oak wood planks")] = mapUtil.nextTexture("planks_big_oak", 0);
		idMap[mapUtil.getBlock("oak sapling")] = mapUtil.nextVoxel("sapling_oak", 0);
		idMap[mapUtil.getBlock("spruce sapling")] = mapUtil.nextVoxel("sapling_spruce", 0);
		idMap[mapUtil.getBlock("birch sapling")] = mapUtil.nextVoxel("sapling_birch", 0);
		idMap[mapUtil.getBlock("jungle sapling")] = mapUtil.nextVoxel("sapling_jungle", 0);
		idMap[mapUtil.getBlock("acacia sapling")] = mapUtil.nextVoxel("sapling_acacia", 0);
		idMap[mapUtil.getBlock("dark oak sapling")] = mapUtil.nextVoxel("sapling_dark_oak", 0);
		idMap[mapUtil.getBlock("bedrock")] = mapUtil.nextTexture("bedrock", 0);
		idMap[mapUtil.getBlock("water16")] = mapUtil.nextVoxel("water16", 0);
		idMap[mapUtil.getBlock("water14")] = mapUtil.nextVoxel("water14", 0);
		idMap[mapUtil.getBlock("water12")] = mapUtil.nextVoxel("water12", 0);
		idMap[mapUtil.getBlock("water10")] = mapUtil.nextVoxel("water10", 0);
		idMap[mapUtil.getBlock("water08")] = mapUtil.nextVoxel("water08", 0);
		idMap[mapUtil.getBlock("water06")] = mapUtil.nextVoxel("water06", 0);
		idMap[mapUtil.getBlock("water04")] = mapUtil.nextVoxel("water04", 0);
		idMap[mapUtil.getBlock("water02")] = mapUtil.nextVoxel("water02", 0);
		idMap[mapUtil.getBlock("lava16")] = mapUtil.nextVoxel("lava16", 0);
		idMap[mapUtil.getBlock("lava14")] = mapUtil.nextVoxel("lava14", 0);
		idMap[mapUtil.getBlock("lava12")] = mapUtil.nextVoxel("lava12", 0);
		idMap[mapUtil.getBlock("lava10")] = mapUtil.nextVoxel("lava10", 0);
		idMap[mapUtil.getBlock("lava08")] = mapUtil.nextVoxel("lava08", 0);
		idMap[mapUtil.getBlock("lava06")] = mapUtil.nextVoxel("lava06", 0);
		idMap[mapUtil.getBlock("lava04")] = mapUtil.nextVoxel("lava04", 0);
		idMap[mapUtil.getBlock("lava02")] = mapUtil.nextVoxel("lava02", 0);
		idMap[mapUtil.getBlock("sand")] = mapUtil.nextTexture("sand", 0);
		idMap[mapUtil.getBlock("red sand")] = mapUtil.nextTexture("red_sand", 0);
		idMap[mapUtil.getBlock("gravel")] = mapUtil.nextTexture("gravel", 0);
		idMap[mapUtil.getBlock("gold ore")] = mapUtil.nextTexture("gold_ore", 0);
		idMap[mapUtil.getBlock("iron ore")] = mapUtil.nextTexture("iron_ore", 0);
		idMap[mapUtil.getBlock("coal ore")] = mapUtil.nextTexture("coal_ore", 0);
		idMap[mapUtil.getBlock("oak wood")] = mapUtil.nextTexture(new String[] {"log_oak", "log_oak", "log_oak_top", "log_oak_top", "log_oak", "log_oak"}, 0);
		idMap[mapUtil.getBlock("spruce wood")] = mapUtil.nextTexture(new String[] {"log_spruce", "log_spruce", "log_spruce_top", "log_spruce_top", "log_spruce", "log_spruce"}, 0);
		idMap[mapUtil.getBlock("birch wood")] = mapUtil.nextTexture(new String[] {"log_birch", "log_birch", "log_birch_top", "log_birch_top", "log_birch", "log_birch"}, 0);
		idMap[mapUtil.getBlock("jungle wood")] = mapUtil.nextTexture(new String[] {"log_jungle", "log_jungle", "log_jungle_top", "log_jungle_top", "log_jungle", "log_jungle"}, 0);
		idMap[mapUtil.getBlock("oak bark")] = mapUtil.nextTexture("log_oak", 0);
		idMap[mapUtil.getBlock("spruce bark")] = mapUtil.nextTexture("log_spruce", 0);
		idMap[mapUtil.getBlock("birch bark")] = mapUtil.nextTexture("log_birch", 0);
		idMap[mapUtil.getBlock("jungle bark")] = mapUtil.nextTexture("log_jungle", 0);
		idMap[mapUtil.getBlock("oak leaves")] = mapUtil.nextTexture("leaves_oak", 0);
		idMap[mapUtil.getBlock("spruce leaves")] = mapUtil.nextTexture("leaves_spruce", 0);
		idMap[mapUtil.getBlock("birch leaves")] = mapUtil.nextTexture("leaves_birch", 0);
		idMap[mapUtil.getBlock("jungle leaves")] = mapUtil.nextTexture("leaves_jungle", 0);
		idMap[mapUtil.getBlock("sponge")] = mapUtil.nextTexture("sponge", 0);
		idMap[mapUtil.getBlock("wet sponge")] = mapUtil.nextTexture("sponge_wet", 0);
		idMap[mapUtil.getBlock("glass")] = mapUtil.nextTexture("glass", 0);
		idMap[mapUtil.getBlock("lapis ore")] = mapUtil.nextTexture("lapis_ore", 0);
		idMap[mapUtil.getBlock("lapis block")] = mapUtil.nextTexture("lapis_block", 0);
		idMap[mapUtil.getBlock("dispenser vertical")] = mapUtil.nextTexture(new String[] {"furnace_top", "furnace_top", "furnace_top", "furnace_top", "dispenser_front_vertical", "furnace_top"}, 0);
		idMap[mapUtil.getBlock("dispenser horizontal")] = mapUtil.nextTexture(new String[] {"furnace_side", "furnace_side", "furnace_top", "furnace_top", "dispenser_front_horizontal", "furnace_side"}, 0);
		idMap[mapUtil.getBlock("sandstone")] = mapUtil.nextTexture(new String[] {"sandstone_normal", "sandstone_normal", "sandstone_top", "sandstone_bottom", "sandstone_normal", "sandstone_normal"}, 0);
		idMap[mapUtil.getBlock("chiseled sandstone")] = mapUtil.nextTexture(new String[] {"sandstone_carved", "sandstone_carved", "sandstone_top", "sandstone_top", "sandstone_carved", "sandstone_carved"}, 0);
		idMap[mapUtil.getBlock("smooth sandstone")] = mapUtil.nextTexture(new String[] {"sandstone_smooth", "sandstone_smooth", "sandstone_top", "sandstone_top", "sandstone_smooth", "sandstone_smooth"}, 0);
		idMap[mapUtil.getBlock("note block")] = mapUtil.nextTexture("noteblock", 0);
		idMap[mapUtil.getBlock("bed feet")] = mapUtil.nextVoxel("bed_feet", 0);
		idMap[mapUtil.getBlock("bed head")] = mapUtil.nextVoxel("bed_head", 0);
		idMap[mapUtil.getBlock("powered rail")] = mapUtil.nextVoxel("rail_powered", 0);
		idMap[mapUtil.getBlock("sloped powered rail")] = mapUtil.nextVoxel("rail_powered_slope", 0);
		idMap[mapUtil.getBlock("activated powered rail")] = mapUtil.nextVoxel("rail_powered_on", 0);
		idMap[mapUtil.getBlock("activated sloped powered rail")] = mapUtil.nextVoxel("rail_powered_slope_on", 0);
		idMap[mapUtil.getBlock("detector rail")] = mapUtil.nextVoxel("rail_detector", 0);
		idMap[mapUtil.getBlock("sloped detector rail")] = mapUtil.nextVoxel("rail_detector_slope", 0);
		idMap[mapUtil.getBlock("activated detector rail")] = mapUtil.nextVoxel("rail_detector_on", 0);
		idMap[mapUtil.getBlock("activated sloped detector rail")] = mapUtil.nextVoxel("rail_detector_slope_on", 0);
		idMap[mapUtil.getBlock("sticky piston retracted")] = mapUtil.nextVoxel("piston_retracted_sticky", 0);
		idMap[mapUtil.getBlock("piston extended")] = mapUtil.nextVoxel("piston_extended", 0);
		idMap[mapUtil.getBlock("cobweb")] = mapUtil.nextVoxel("web", 0);
		idMap[mapUtil.getBlock("shrub")] = mapUtil.nextVoxel("deadbush", 0);
		idMap[mapUtil.getBlock("tall grass")] = mapUtil.nextVoxel("tallgrass", 0);
		idMap[mapUtil.getBlock("fern")] = mapUtil.nextVoxel("fern", 0);
		idMap[mapUtil.getBlock("dead bush")] = mapUtil.nextVoxel("deadbush", 0);
		idMap[mapUtil.getBlock("piston retracted")] = mapUtil.nextVoxel("piston_retracted", 0);
		idMap[mapUtil.getBlock("piston head")] = mapUtil.nextVoxel("piston_head", 0);
		idMap[mapUtil.getBlock("sticky piston head")] = mapUtil.nextVoxel("piston_head_sticky", 0);
		idMap[mapUtil.getBlock("white wool")] = mapUtil.nextTexture("wool_colored_white", 0);
		idMap[mapUtil.getBlock("orange wool")] = mapUtil.nextTexture("wool_colored_orange", 0);
		idMap[mapUtil.getBlock("magenta wool")] = mapUtil.nextTexture("wool_colored_magenta", 0);
		idMap[mapUtil.getBlock("light blue wool")] = mapUtil.nextTexture("wool_colored_light_blue", 0);
		idMap[mapUtil.getBlock("yellow wool")] = mapUtil.nextTexture("wool_colored_yellow", 0);
		idMap[mapUtil.getBlock("lime wool")] = mapUtil.nextTexture("wool_colored_lime", 0);
		idMap[mapUtil.getBlock("pink wool")] = mapUtil.nextTexture("wool_colored_pink", 0);
		idMap[mapUtil.getBlock("gray wool")] = mapUtil.nextTexture("wool_colored_gray", 0);
		idMap[mapUtil.getBlock("light gray wool")] = mapUtil.nextTexture("wool_colored_silver", 0);
		idMap[mapUtil.getBlock("cyan wool")] = mapUtil.nextTexture("wool_colored_cyan", 0);
		idMap[mapUtil.getBlock("purple wool")] = mapUtil.nextTexture("wool_colored_purple", 0);
		idMap[mapUtil.getBlock("blue wool")] = mapUtil.nextTexture("wool_colored_blue", 0);
		idMap[mapUtil.getBlock("brown wool")] = mapUtil.nextTexture("wool_colored_brown", 0);
		idMap[mapUtil.getBlock("green wool")] = mapUtil.nextTexture("wool_colored_green", 0);
		idMap[mapUtil.getBlock("red wool")] = mapUtil.nextTexture("wool_colored_red", 0);
		idMap[mapUtil.getBlock("black wool")] = mapUtil.nextTexture("wool_colored_black", 0);
		idMap[mapUtil.getBlock("dandelion")] = mapUtil.nextVoxel("flower_dandelion", 0);
		idMap[mapUtil.getBlock("poppy")] = mapUtil.nextVoxel("flower_rose", 0);
		idMap[mapUtil.getBlock("blue orchid")] = mapUtil.nextVoxel("flower_blue_orchid", 0);
		idMap[mapUtil.getBlock("allium")] = mapUtil.nextVoxel("flower_allium", 0);
		idMap[mapUtil.getBlock("azure bluet")] = mapUtil.nextVoxel("flower_houstonia", 0);
		idMap[mapUtil.getBlock("red tulip")] = mapUtil.nextVoxel("flower_tulip_red", 0);
		idMap[mapUtil.getBlock("orange tulip")] = mapUtil.nextVoxel("flower_tulip_orange", 0);
		idMap[mapUtil.getBlock("white tulip")] = mapUtil.nextVoxel("flower_tulip_white", 0);
		idMap[mapUtil.getBlock("pink tulip")] = mapUtil.nextVoxel("flower_tulip_pink", 0);
		idMap[mapUtil.getBlock("oxeye daisy")] = mapUtil.nextVoxel("flower_oxeye_daisy", 0);
		idMap[mapUtil.getBlock("brown mushroom")] = mapUtil.nextVoxel("mushroom_brown", 0);
		idMap[mapUtil.getBlock("red mushroom")] = mapUtil.nextVoxel("mushroom_red", 0);
		idMap[mapUtil.getBlock("gold block")] = mapUtil.nextTexture("gold_block", 0);
		idMap[mapUtil.getBlock("iron block")] = mapUtil.nextTexture("iron_block", 0);
		idMap[mapUtil.getBlock("double stone slab")] = mapUtil.nextTexture(new String[] {"stone_slab_side", "stone_slab_side", "stone_slab_top", "stone_slab_top", "stone_slab_side", "stone_slab_side"}, 0);
		idMap[mapUtil.getBlock("bricks")] = mapUtil.nextTexture("brick", 0);
		idMap[mapUtil.getBlock("stone brick")] = mapUtil.nextTexture("stonebrick", 0);
		idMap[mapUtil.getBlock("nether brick")] = mapUtil.nextTexture("nether_brick", 0);
		idMap[mapUtil.getBlock("quartz")] = mapUtil.nextTexture(new String[] {"quartz_block_side", "quartz_block_side", "quartz_block_top", "quartz_block_bottom", "quartz_block_side", "quartz_block_side"}, 0);
		idMap[mapUtil.getBlock("smooth double stone slab")] = mapUtil.nextTexture("stone_slab_top", 0);
		idMap[mapUtil.getBlock("smooth double sandstone slab")] = mapUtil.nextTexture("sandstone_top", 0);
		idMap[mapUtil.getBlock("smooth quartz slab")] = mapUtil.nextTexture("quartz_block_top", 0);
		idMap[mapUtil.getBlock("tnt")] = mapUtil.nextTexture(new String[] {"tnt_side", "tnt_side", "tnt_top", "tnt_bottom", "tnt_side", "tnt_side"}, 0);
		int b = mapUtil.nextVoxel("bookshelf");
		int p = mapUtil.nextTexture("planks_oak");
		idMap[mapUtil.getBlock("bookshelf")] = new int[][] {{1,b,EAST}, {1,b,WEST}, {0,p,0}, {0,p,0}, {1,b,NONE}, {1,b,NORTH}};
		idMap[mapUtil.getBlock("mossy cobblestone")] = mapUtil.nextTexture("cobblestone_mossy", 0);
		idMap[mapUtil.getBlock("obsidian")] = mapUtil.nextTexture("obsidian", 0);
		idMap[mapUtil.getBlock("torch wall")] = mapUtil.nextVoxel("torch_wall", 0);
		idMap[mapUtil.getBlock("torch floor")] = mapUtil.nextVoxel("torch_floor", 0);
		idMap[mapUtil.getBlock("fire")] = mapUtil.nextVoxel("fire", 0);
		idMap[mapUtil.getBlock("mob spawner")] = mapUtil.nextVoxel("mob_spawner", 0);
		idMap[mapUtil.getBlock("oak stairs top")] = mapUtil.nextVoxel("stairs_top_oak", 0);
		idMap[mapUtil.getBlock("oak stairs bottom")] = mapUtil.nextVoxel("stairs_bottom_oak", 0);
		idMap[mapUtil.getBlock("diamond ore")] = mapUtil.nextTexture("diamond_ore", 0);
		idMap[mapUtil.getBlock("diamond block")] = mapUtil.nextTexture("diamond_block", 0);
		idMap[mapUtil.getBlock("crafting table")] = mapUtil.nextVoxel("crafting_table", 0);
		idMap[mapUtil.getBlock("furnace")] = mapUtil.nextVoxel("furnace", 0);
		idMap[mapUtil.getBlock("furnace on")] = mapUtil.nextVoxel("furnace_on", 0);
		idMap[mapUtil.getBlock("oak door lower")] = mapUtil.nextVoxel("door_oak_lower", 0);
		idMap[mapUtil.getBlock("oak door upper")] = mapUtil.nextVoxel("door_oak_upper", 0);
		idMap[mapUtil.getBlock("ladder")] = mapUtil.nextVoxel("ladder", 0);
		idMap[mapUtil.getBlock("rail straight")] = mapUtil.nextVoxel("rail_normal", 0);
		idMap[mapUtil.getBlock("rail sloped")] = mapUtil.nextVoxel("rail_normal_slope", 0);
		idMap[mapUtil.getBlock("rail curved")] = mapUtil.nextVoxel("rail_normal_turned", 0);
		idMap[mapUtil.getBlock("cobblestone stairs bottom")] = mapUtil.nextVoxel("stairs_bottom_cobblestone", 0);
		idMap[mapUtil.getBlock("cobblestone stairs top")] = mapUtil.nextVoxel("stairs_top_cobblestone", 0);
	}
	
	public static int getDoorRotation(int lowerMetadata, int upperMetadata) {
		int metadata = ((upperMetadata&1)*8)|(lowerMetadata&7);
		int rotation = NONE;
		switch (metadata) {
		case 0:
			rotation = WEST;
			break;
		case 1:
			rotation = NORTH;
			break;
		case 2:
			rotation = EAST;
			break;
		case 3:
			rotation = NONE;
			break;
		case 4:
			rotation = NORTH | FLIP;
			break;
		case 5:
			rotation = EAST | FLIP;
			break;
		case 6:
			rotation = NONE | FLIP;
			break;
		case 7:
			rotation = WEST | FLIP;
			break;
		case 8:
			rotation = WEST | FLIP;
			break;
		case 9:
			rotation = NORTH | FLIP;
			break;
		case 10:
			rotation = EAST | FLIP;
			break;
		case 11:
			rotation = NONE | FLIP;
			break;
		case 12:
			rotation = NONE;
			break;
		case 13:
			rotation = WEST;
			break;
		case 14:
			rotation = NORTH;
			break;
		case 15:
			rotation = EAST;
			break;
		}
		return rotation;
	}
	
	public static int[][][] getIdMap() {
		return idMap;
	}
	
	public static String[] getAllBlocks() {
		return mapUtil.getAllBlocks();
	}
	
	public static String[] getAllTextures() {
		return mapUtil.getAllTextures();
	}
	
	public static String[] getAllVoxels() {
		return mapUtil.getAllVoxels();
	}
}
