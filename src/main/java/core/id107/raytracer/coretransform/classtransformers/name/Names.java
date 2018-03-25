package core.id107.raytracer.coretransform.classtransformers.name;

public class Names {

	public static final ClassName Chunk = new ClassName("net.minecraft.world.chunk.Chunk", "aum");
	public static final MethodName Chunk_setBlockState = new MethodName("setBlockState", "func_177436_a", "a", "(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/state/IBlockState;)Lnet/minecraft/block/state/IBlockState;", "(Lco;Latl;)Latl;");
	
	public static final ClassName Minecraft = new ClassName("net.minecraft.client.Minecraft", "beq");
	public static final MethodName Minecraft_loadWorld = new MethodName("loadWorld", "func_71353_a", "a", "(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V", "(Lbno;Ljava/lang/String;)V");
	
	public static final ClassName RenderGlobal = new ClassName("net.minecraft.client.renderer.RenderGlobal", "bqk");
	public static final MethodName RenderGlobal_renderEntities = new MethodName("renderEntities", "func_180446_a", "a", "(Lnet/minecraft/entity/Entity;Lnet/minecraft/client/renderer/culling/ICamera;F)V", "(Lsm;Lbtj;F)V");
	public static final MethodName RenderGlobal_renderBlockLayer = new MethodName("renderBlockLayer", "func_174977_a", "a", "(Lnet/minecraft/util/BlockRenderLayer;DILnet/minecraft/entity/Entity;)I", "(Laji;DILsm;)I");
	public static final MethodName RenderGlobal_renderSky = new MethodName("renderSky", "func_174976_a", "a", "(FI)V", "(FI)V");
	public static final MethodName RenderGlobal_renderClouds = new MethodName("renderClouds", "func_180447_b", "b", "(FI)V", "(FI)V");
	public static final MethodName RenderGlobal_drawBlockDamageTexture = new MethodName("drawBlockDamageTexture", "func_174981_a", "a", "(Lnet/minecraft/client/renderer/Tessellator;Lnet/minecraft/client/renderer/VertexBuffer;Lnet/minecraft/entity/Entity;F)V", "(Lbqq;Lbpw;Lsm;F)V");
	public static final MethodName RenderGlobal_drawSelectionBox = new MethodName("drawSelectionBox", "func_72731_b", "a", "(Lnet/minecraft/entity/player/EntityPlayer;Lnet/minecraft/util/math/RayTraceResult;IF)V", "(Laax;Lbds;IF)V");
}
