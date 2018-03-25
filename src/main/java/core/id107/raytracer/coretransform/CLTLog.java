package core.id107.raytracer.coretransform;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class CLTLog
{
	public static final Logger logger = LogManager.getLogger("RayTracer-CLT");

	public static void info(String s)
	{
		logger.info(s);
	}
	
	public static <T> void info(T s) {
		logger.info(s.toString());
	}
}
