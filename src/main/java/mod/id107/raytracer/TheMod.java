package mod.id107.raytracer;

import net.minecraftforge.common.ForgeVersion;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;

@Mod(modid = TheMod.MOD_ID, name = TheMod.MOD_NAME, version = TheMod.MOD_VERSION, useMetadata = true)
public class TheMod
{
    public static final String MOD_ID = "raytracer";
    public static final String MOD_NAME = "Ray Tracer";
    public static final String MOD_VERSION = "0.0.0";
    
    public static final String RESOURCE_PREFIX = MOD_ID.toLowerCase() + ':';
    
    @Mod.Instance
    public static TheMod instance;
    
    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        Log.info("" + ForgeVersion.mcVersion);
        MinecraftForge.EVENT_BUS.register(new mod.id107.raytracer.EventHandler());
    }
}
