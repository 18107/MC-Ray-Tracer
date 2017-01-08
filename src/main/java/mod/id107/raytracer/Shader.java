package mod.id107.raytracer;

import java.nio.ByteBuffer;
import java.nio.IntBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.GL43;

public class Shader {

	private static final String vertexShader = "#version 130//\n /* The position of the vertex as two-dimensional vector */ in vec2 vertex; /* Write interpolated texture coordinate to fragment shader */ out vec2 texcoord; void main(void) { gl_Position = vec4(vertex, 0.0, 1.0); /* * Compute texture coordinate by simply * interval-mapping from [-1..+1] to [0..1] */ texcoord = vertex * 0.5 + vec2(0.5, 0.5); } ";
	private static final String fragmentShader = "#version 430\n\n#define M_PI 3.141592\n\n#define CHUNK_SIZE 16*16*16\n\nin vec2 texcoord;\n\nuniform vec3 cameraPos;\nuniform vec3 cameraDir;\nuniform float fovx;\nuniform float fovy;\nuniform int renderDistance;\nuniform int chunkHeight;\n\nuniform sampler2D tex;\n\nout vec4 color;\n\nstruct BlockData {\n  int id;\n  int lightLevel;\n};\n\nlayout(std430, binding = 2) buffer worldChunks\n{\n  int location[];\n};\n\nlayout(std430, binding = 3) buffer chunk\n{\n  BlockData blockData[];\n};\n\nlayout(std430, binding = 4) buffer worldMetadata\n{\n  int metaLocation[];\n};\n\nlayout(std430, binding = 5) buffer metadata\n{\n  int blockMetadata[];\n};\n\nvec3 rotate(vec3 camera, vec3 ray) {\n  //rotate z\n  float x = cos(camera.z)*ray.x + sin(camera.z)*ray.y;\n  float y = cos(camera.z)*ray.y - sin(camera.z)*ray.x;\n  ray.x = x;\n  ray.y = y;\n  \n  //rotate x\n  y = cos(camera.x)*ray.y - sin(camera.x)*ray.z;\n  float z = cos(camera.x)*ray.z + sin(camera.x)*ray.y;\n  ray.y = y;\n  ray.z = z;\n  \n  //rotate y\n  x = cos(camera.y)*ray.x - sin(camera.y)*ray.z;\n  z = cos(camera.y)*ray.z + sin(camera.y)*ray.x;\n  ray.x = x;\n  ray.z = z;\n  \n  return ray;\n}\n\n//Gets the direction of the ray assuming a flat screen\nvec3 getRayFlat(float fovx, float fovy) {\n  vec3 ray = vec3((-1+texcoord.x*2)*tan(fovy/2)*fovx/fovy, (-1+texcoord.y*2)*tan(fovy/2), -1);\n  return rotate(cameraDir, ray);\n}\n\n//Gets the direction of the ray assuming a spherical screen\nvec3 getRaySphere(float fovx, float fovy) {\n  vec3 ray = vec3(-sin((texcoord.x)*fovx)*sin((texcoord.y)*fovy),\n    -cos((texcoord.y)*fovy),\n    cos((texcoord.x)*fovx)*sin((texcoord.y)*fovy));\n  return rotate(cameraDir, ray);\n}\n\nbool drawTexture(int id, int side, float xIn, float yIn, int light, int metadata) {\n  float x = 12;\n  float y = 24;\n  switch (id) {\n  case 1: //stone\n    x = 24;\n    switch (metadata) {\n    case 0: //stone\n      y = 0;\n      break;\n    case 1: //granite\n      y = 5;\n      break;\n    case 2: //polished granite\n      y = 6;\n      break;\n    case 3: //diorite\n      y = 3;\n      break;\n    case 4: //polished diorite\n      y = 4;\n      break;\n    case 5: //andesite\n      y = 1;\n      break;\n    case 6: //polished andesite\n      y = 2;\n      break;\n    }\n    break;\n  case 2: //grass\n    if (side == 1) {\n      x = 7; //TODO if snow above\n      y = 12;\n    } else if (side == 0) {\n      x = 10;\n      y = 6;\n    } else {\n      x = 4;\n      y = 12;\n    }\n    break;\n  case 3: //dirt\n    switch (metadata) {\n    case 0: //dirt\n      x = 10;\n      y = 6;\n      break;\n    case 1: //coarse dirt\n      x = 7;\n      y = 6;\n      break;\n    case 2: //podzol\n      if (side == 0) {\n        x = 10;\n        y = 6;\n      }\n      else if (side == 1) {\n        x = 11;\n        y = 0;\n      } else {\n        x = 10;\n        y = 7;\n      }\n    }\n    break;\n  case 4: //cobblestone\n    x = 0;\n    y = 7;\n    break;\n  case 5: //planks\n    x = 18;\n    switch (metadata) {\n    case 0: //oak\n      y = 3;\n      break;\n    case 1: //spruce\n      y = 4;\n      break;\n    case 2: //birch\n      y = 1;\n      break;\n    case 3: //jungle\n      y = 2;\n      break;\n    case 4: //acacia\n      x = 17;\n      y = 15;\n      break;\n    case 5: //dark oak\n      y = 0;\n      break;\n    }\n    break;\n  case 7: //bedrock\n    x = 4;\n    y = 3;\n    break;\n  case 12: //sand\n    if (metadata == 0) { //sand\n      x = 21;\n      y = 14;\n    } else { //red sand\n      x = 20;\n      y = 7;\n    }\n    break;\n  case 8: //flowing_water\n    x = 2; //TODO\n    y = 0;\n    break;\n  case 9: //water\n    if (side == 1) {\n      x = 25;\n      y = 12;\n    } else {\n      x = 2;\n      y = 0;\n    }\n    break;\n  case 10: //flowing_lava\n  case 11: //TODO\n    x = 0;\n    y = 0;\n    break;\n  case 13: //gravel\n    x = 8;\n    y = 12;\n    break;\n  case 14: //gold_ore\n    x = 1;\n    y = 12;\n    break;\n  case 15: //iron_ore\n    x = 3;\n    y = 14;\n    break;\n  case 16: //coal_ore\n    x = 6;\n    y = 6;\n    break;\n  case 17: //log\n    y = 15;\n    switch (metadata&3) { //types of log\n    case 0: //oak\n      x = 11;\n      break;\n    case 1: //spruce\n      x = 13;\n      break;\n    case 2: //birch\n      x = 7;\n      break;\n    case 3: //jungle\n      x = 9;\n      break;\n    }\n    if ((metadata&12) == 0) { //facing y\n      if (side < 2) {\n        x++;\n      }\n    }\n    else if ((metadata&12) == 4) { //facing x\n      if (side == 4 || side == 5) {\n        x++;\n        if (side == 5) {\n          xIn = 1-xIn; //rotate face\n          yIn = 1-yIn;\n        }\n      } else {\n        float temp = xIn; //rotate faces\n        xIn = yIn;\n        yIn = 1-temp;\n        if (side == 2) {\n          xIn = 1-xIn;\n          yIn = 1-yIn;\n        }\n      }\n    }\n    else if ((metadata&12) == 8) { //facing z\n      if (side == 2 || side == 3) {\n        x++;\n        if (side == 2) {\n          xIn = 1-xIn;\n          yIn = 1-yIn;\n        }\n      } else {\n        if (side == 0) {\n          xIn = 1-xIn;\n          yIn = 1-yIn;\n        }\n        else if (side == 4) {\n          float temp = xIn;\n          xIn = 1-yIn;\n          yIn = temp;\n        }\n        else if (side == 5) {\n          float temp = xIn;\n          xIn = yIn;\n          yIn = 1-temp;\n        }\n      }\n    }\n    break;\n  case 18: //leaves\n    switch (metadata&3) {\n    case 0: //oak\n      x = 12;\n      y = 14;\n      break;\n    case 1: //spruce\n      x = 1;\n      y = 15;\n      break;\n    case 2: //birch\n      x = 0;\n      y = 15;\n      break;\n    case 3: //jungle\n      x = 15;\n      y = 14;\n      break;\n    }\n    break;\n  case 19: //sponge\n    x = 23;\n    y = 14 + metadata; //or wet sponge\n    break;\n  case 20: //glass\n    x = 13;\n    y = 9;\n    break;\n  case 21: //lapis_ore\n    x = 10;\n    y = 14;\n    break;\n  case 22: //lapis_block\n    x = 9;\n    y = 14;\n    break;\n  case 23: //dispenser\n    switch (metadata) {\n    case 0: // negative y\n      if (side == 0) {\n        x = 11;\n        y = 2;\n      } else {\n        x = 12;\n        y = 9;\n      }\n      if (side >= 2) {\n        xIn = 1-xIn;\n        yIn = 1-yIn;\n      }\n      break;\n    case 1: // positive y\n      if (side == 1) {\n        x = 11;\n        y = 2;\n      } else {\n        x = 12;\n        y = 9;\n      }\n      break;\n    case 2: //negative z\n      if (side == 2) {\n        x = 11;\n        y = 1;\n      }\n      else if (side < 2) {\n        x = 12;\n        y = 9;\n      } else {\n        x = 11;\n        y = 9;\n      }\n      break;\n    case 3: //positive z\n      if (side == 3) {\n        x = 11;\n        y = 1;\n      }\n      else if (side < 2) {\n        x = 12;\n        y = 9;\n        xIn = 1-xIn;\n        yIn = 1-yIn;\n      } else {\n        x = 11;\n        y = 9;\n      }\n      break;\n    case 4: //negative x\n      if (side == 4) {\n        x = 11;\n        y = 1;\n      }\n      else if (side < 2) {\n        x = 12;\n        y = 9;\n        if (side == 0) {\n          float temp = xIn;\n          xIn = yIn;\n          yIn = 1-temp;\n        } else {\n          float temp = xIn;\n          xIn = 1-yIn;\n          yIn = temp;\n        }\n      } else {\n        x = 11;\n        y = 9;\n      }\n      break;\n    case 5: //positive x\n      if (side == 5) {\n        x = 11;\n        y = 1;\n      }\n      else if (side < 2) {\n        x = 12;\n        y = 9;\n        if (side == 0) {\n          float temp = xIn;\n          xIn = 1-yIn;\n          yIn = temp;\n        } else {\n          float temp = xIn;\n          xIn = yIn;\n          yIn = 1-temp;\n        }\n      } else {\n        x = 11;\n        y = 9;\n      }\n      break;\n    }\n    break;\n  case 24: //sandstone\n    switch (metadata) {\n    case 0: //sandstone\n      if (side == 0) {\n        x = 21;\n        y = 15;\n      }\n      else if (side == 1) {\n        x = 22;\n        y = 3;\n      } else {\n        x = 22;\n        y = 1;\n      }\n      break;\n    case 1: //chiseled sandstone\n      if (side < 2) {\n        x = 22;\n        y = 3;\n      } else {\n        x = 22;\n        y = 0;\n      }\n      break;\n    case 2: //smooth sandstone\n      if (side < 2) {\n        x = 22;\n        y = 3;\n      } else {\n        x = 22;\n        y = 2;\n      }\n      break;\n    }\n    break;\n  case 25: //noteblock\n    x = 17;\n    y = 3;\n    break;\n  case 35: //wool\n    switch (metadata) {\n    case 0: //white\n      x = 27;\n      y = 5;\n      break;\n    case 1: //orange\n      x = 27;\n      y = 0;\n      break;\n    case 2: //magenta\n      x = 26;\n      y = 15;\n      break;\n    case 3: //light blue\n      x = 26;\n      y = 13;\n      break;\n    case 4: //yellow\n      x = 27;\n      y = 6;\n      break;\n    case 5: //lime\n      x = 26;\n      y = 14;\n      break;\n    case 6: //pink\n      x = 27;\n      y = 1;\n      break;\n    case 7: //gray\n      x = 26;\n      y = 11;\n      break;\n    case 8: //light gray\n      x = 27;\n      y = 4;\n      break;\n    case 9: //cyan\n      x = 26;\n      y = 10;\n      break;\n    case 10: //purple\n      x = 27;\n      y = 2;\n      break;\n    case 11: //blue\n      x = 26;\n      y = 8;\n      break;\n    case 12: //brown\n      x = 26;\n      y = 9;\n      break;\n    case 13: //green\n      x = 26;\n      y = 12;\n      break;\n    case 14: //red\n      x = 27;\n      y = 3;\n      break;\n    case 15: //black\n      x = 26;\n      y = 7;\n      break;\n    }\n    break;\n  case 41: //gold_block\n    x = 0;\n    y = 12;\n    break;\n  case 42: //iron_block\n    x = 2;\n    y = 14;\n    break;\n  case 43: //double_stone_slab\n    switch (metadata) {\n    case 0: //stone\n      x = 24;\n      if (side < 2) {\n        y = 8;\n      } else {\n        y = 7;\n      }\n      break;\n    case 1: //sandstone\n      if (side == 0) {\n        x = 21;\n        y = 15;\n      }\n      else if (side == 1) {\n        x = 22;\n        y = 3;\n      } else {\n        x = 22;\n        y = 1;\n      }\n      break;\n    case 3: //cobblestone\n      x = 0;\n      y = 7;\n      break;\n    case 4: //brick\n      x = 7;\n      y = 1;\n      break;\n    case 5: //stone brick\n      x = 24;\n      y = 9;\n      break;\n    case 6: //nether brick\n      x = 16;\n      y = 13;\n      break;\n    case 7: //quartz\n      if (side == 0) {\n        x = 19;\n        y = 6;\n      } else {\n        x = 19;\n        y = 11;\n      }\n      break;\n    }\n    break;\n  case 45: //brick_block\n    x = 7;\n    y = 1;\n    break;\n  case 46: //tnt\n    x = 25;\n    if (side == 0) {\n      y = 3;\n    }\n    else if (side == 1) {\n      y = 5;\n    } else {\n      y = 4;\n    }\n    break;\n  case 47: //bookshelf\n    if (side < 2) {\n      x = 18;\n      y = 3;\n    } else {\n      x = 6;\n      y = 2;\n    }\n    break;\n  case 48: //mossy_cobblestone\n    x = 1;\n    y = 7;\n    break;\n  case 49: //obsidian\n    x = 17;\n    y = 9;\n    break;\n  case 52: //mob_spawner\n    x = 16;\n    y = 4;\n    break;\n  case 56: //diamond_ore\n    x = 10;\n    y = 5;\n    break;\n  case 57: //diamond_block\n    x = 10;\n    y = 4;\n    break;\n  case 58: //crafting_table\n    if (side == 0) {\n      x = 18;\n      y = 3;\n    }\n    else if (side == 1) {\n      x = 8;\n      y = 5;\n    }\n    else if ((side&1) == 1) {\n      x = 8;\n      y = 4;\n    } else {\n      x = 8;\n      y = 3;\n    }\n    break;\n  case 61: //furnace\n    y = 9;\n    if (side < 2) {\n      x = 12;\n    }\n    else if (side == metadata) {\n      x = 9;\n    } else {\n      x = 11;\n    }\n    break;\n  case 62: //lit_furnace\n    y = 9;\n    if (side < 2) {\n      x = 12;\n    }\n    else if (side == metadata) {\n      x = 10;\n    } else {\n      x = 11;\n    }\n    break;\n  case 73: //redstone_ore\n  case 74: //lit_redstone_ore\n    x = 21;\n    y = 4;\n    break;\n  case 79: //ice\n    x = 15;\n    y = 13;\n    break;\n  case 80: //snow\n    x = 23;\n    y = 12;\n    break;\n  case 82: //clay\n    x = 4;\n    y = 6;\n    break;\n  case 84: //jukebox\n    if (side == 1) {\n      x = 7;\n      y = 14;\n    } else {\n      x = 6;\n      y = 14;\n    }\n    break;\n  case 86: //pumpkin\n    if (side < 2) {\n      x = 19;\n      y = 2;\n      xIn = 1-xIn;\n      yIn = 1-yIn;\n    } else {\n      x = 18;\n      y = 15;\n    }\n    switch (metadata) {\n    case 0:\n      if (side == 3) {\n        y = 13;\n      }\n      break;\n    case 1:\n      if (side == 4) {\n        y = 13;\n      }\n      break;\n    case 2:\n      if (side == 2) {\n        y = 13;\n      }\n      break;\n    case 3:\n      if (side == 5) {\n        y = 13;\n      }\n      break;\n    }\n    break;\n  case 87: //netherrack\n    x = 17; //TODO fix rotation\n    y = 2;\n    break;\n  case 88: //soul_sand\n    x = 23;\n    y = 13;\n    break;\n  case 89: //glowstone\n    x = 15;\n    y = 11;\n    break;\n  case 91: //lit_pumpkin\n    if (side < 2) {\n      x = 19;\n      y = 2;\n      xIn = 1-xIn;\n      yIn = 1-yIn;\n    } else {\n      x = 18;\n      y = 15;\n    }\n    switch (metadata) {\n    case 0:\n      if (side == 3) {\n        y = 14;\n      }\n      break;\n    case 1:\n      if (side == 4) {\n        y = 14;\n      }\n      break;\n    case 2:\n      if (side == 2) {\n        y = 14;\n      }\n      break;\n    case 3:\n      if (side == 5) {\n        y = 14;\n      }\n      break;\n    }\n    break;\n  case 97: //monster_egg\n    switch (metadata) {\n    case 0: //stone\n      x = 24;\n      y = 0;\n      break;\n    case 1: //cobblestone\n      x = 0;\n      y = 7;\n      break;\n    case 2: //stone brick\n      x = 24;\n      y = 9;\n      break;\n    case 3: //mossy stone brick\n      x = 24;\n      y = 12;\n      break;\n    case 4: //cracked stone brick\n      x = 24;\n      y = 11;\n      break;\n    case 5: //chiseled stone brick\n      x = 24;\n      y = 10;\n      break;\n    }\n    break;\n  case 98: //stonebrick\n    switch (metadata) {\n    case 0:\n      x = 24;\n      y = 9;\n      break;\n    case 1: //mossy\n      x = 24;\n      y = 12;\n      break;\n    case 2: //cracked\n      x = 24;\n      y = 11;\n      break;\n    case 3: //chiseled\n      x = 24;\n      y = 10;\n      break;\n    }\n    break;\n    /*\n  case 99: //brown_mushroom_block\n  case 100: //red_mushroom_block\n    x = 16;\n    switch (metadata) {\n    case 0: //all_inside\n      y = 5;\n      break;\n    case 1: //north_west\n      if (side == 1 || side == 2 || side == 4) {\n        y = 6;\n      } else {\n        y = 5;\n      }\n      break;\n    case 2: //north\n      if (side == 1 || side == 2) {\n        y = 6;\n      } else {\n        y = 5;\n      }\n      break;\n    case 3: //north_east\n      if (side == 1 || side == 2 || side == 5) {\n        y = 6;\n      } else {\n        y = 5;\n      }\n      break;\n    case 4: //west\n      if (side == 1 || side == 4) {\n        y = 6;\n      } else {\n        y = 5;\n      }\n      break;\n    case 5: //center\n      if (side == 1) {\n        y = 6;\n      } else {\n        y = 5;\n      }\n      break;\n    case 6: //east\n      if (side == 1 || side == 5) {\n        y = 6;\n      } else {\n        y = 5;\n      }\n      break;\n    case 7: //south_west\n      if (side == 1 || side == 3 || side == 4) {\n        y = 6;\n      } else {\n        y = 5;\n      }\n      break;\n    case 8: //south\n      if (side == 1 || side == 3) {\n        y = 6;\n      } else {\n        y = 5;\n      }\n      break;\n    case 9: //south_east\n      if (side == 1 || side == 3 || side == 5) {\n        y = 6;\n      } else {\n        y = 5;\n      }\n      break;\n    }\n    if (id == 100 && y == 6) {\n      y = 7;\n    }\n    break;\n    */\n  default:\n    return false;\n  }\n  //TODO do sunlight\n  color = texture(tex, vec2((x + xIn)/32, (y + yIn)/32))/* * vec4((light+1)/16.0, (light+1)/16.0, (light+1)/16.0, 1)*/;\n  return true;\n}\n\nvoid trace(inout ivec3 current, vec3 vector, vec3 dir, vec3 pos, vec3 inc, ivec3 iinc) {\n  ivec3 last = ivec3(current.xyz);\n  ivec3 chunkPos = ivec3(renderDistance, chunkHeight, renderDistance);\n  int worldWidth = renderDistance*2+1;\n  int chunkId = location[chunkPos.z*worldWidth*16 + chunkPos.x*16 + chunkPos.y];\n  //TODO if player is above the world\n  int lastChunkId; //used for light levels\n  int blockId;\n  bool blockFound = false;\n  int side;\n  float texX;\n  float texY;\n  while (!blockFound) {\n    //TODO improve direction testing\n    last = current;\n    blockId = 0;\n    \n    //Next cube\n    if (vector.x < vector.y) {\n      if (vector.x < vector.z) {\n          vector.x += inc.x;\n          current.x += iinc.x;\n          if ((current.x&16) == 16) {\n            current.x &= 15;\n            last.x = current.x - iinc.x;\n            chunkPos.x += iinc.x;\n            if (chunkPos.x == worldWidth || chunkPos.x == -1) {\n              return;\n            }\n            lastChunkId = chunkId;\n            chunkId = location[chunkPos.z*worldWidth*16 + chunkPos.x*16 + chunkPos.y];\n          }\n      } else {\n          vector.z += inc.z;\n          current.z += iinc.z;\n          if ((current.z&16) == 16) {\n            current.z &= 15;\n            last.z = current.z - iinc.z;\n            chunkPos.z += iinc.z;\n            if (chunkPos.z == worldWidth || chunkPos.z == -1) {\n              return;\n            }\n            lastChunkId = chunkId;\n            chunkId = location[chunkPos.z*worldWidth*16 + chunkPos.x*16 + chunkPos.y];\n          }\n      }\n    } else {\n      if (vector.y < vector.z) {\n          vector.y += inc.y;\n          current.y += iinc.y;\n          if ((current.y&16) == 16) {\n            current.y &= 15;\n            last.y = current.y - iinc.y;\n            chunkPos.y += iinc.y;\n            if ((chunkPos.y&16) == 16) {\n              return;\n            }\n            lastChunkId = chunkId;\n            chunkId = location[chunkPos.z*worldWidth*16 + chunkPos.x*16 + chunkPos.y];\n          }\n      } else {\n          vector.z += inc.z;\n          current.z += iinc.z;\n          if ((current.z&16) == 16) {\n            current.z &= 15;\n            last.z = current.z - iinc.z;\n            chunkPos.z += iinc.z;\n            if (chunkPos.z == worldWidth || chunkPos.z == -1) {\n              return;\n            }\n            lastChunkId = chunkId;\n            chunkId = location[chunkPos.z*worldWidth*16 + chunkPos.x*16 + chunkPos.y];\n          }\n      }\n    }\n    \n    //TODO skip empty chunks\n    \n    if (chunkId != 0) {\n      blockId = blockData[(chunkId-1)*CHUNK_SIZE + (current.y<<8) + (current.z<<4) + current.x].id;\n    }\n    \n    //if (block != air)\n    if (blockId != 0) {\n      int light = blockData[(lastChunkId-1)*CHUNK_SIZE + ((last.y%16)<<8) + ((last.z%16)<<4) + (last.x%16)].lightLevel;\n      int metadataId = metaLocation[chunkPos.z*worldWidth*16 + chunkPos.x*16 + chunkPos.y];\n      int metadata = 0;\n      \n      if (metadataId != 0) {\n        metadata = blockMetadata[(metadataId-1)*CHUNK_SIZE + (current.y<<8) + (current.z<<4) + current.x];\n      }\n      \n      if (current.x != last.x) {\n        if (current.x > last.x) {\n          texX = (dir.z*(current.x+16*(chunkPos.x-renderDistance)-pos.x)/dir.x)-(current.z+16*(chunkPos.z-renderDistance)-pos.z);\n          texY = 1-((dir.y*(current.x+16*(chunkPos.x-renderDistance)-pos.x)/dir.x)-(current.y+16*(chunkPos.y-chunkHeight)-pos.y));\n          side = 4;\n        } else {\n          texX = 1-((dir.z*(current.x+16*(chunkPos.x-renderDistance)+1-pos.x)/dir.x)-(current.z+16*(chunkPos.z-renderDistance)-pos.z));\n          texY = 1-((dir.y*(current.x+16*(chunkPos.x-renderDistance)+1-pos.x)/dir.x)-(current.y+16*(chunkPos.y-chunkHeight)-pos.y));\n          side = 5;\n        }\n      } else if (current.y != last.y) {\n        if (current.y > last.y) {\n          texX = (dir.x*(current.y+16*(chunkPos.y-chunkHeight)-pos.y)/dir.y)-(current.x+16*(chunkPos.x-renderDistance)-pos.x);\n          texY = 1-((dir.z*(current.y+16*(chunkPos.y-chunkHeight)-pos.y)/dir.y)-(current.z+16*(chunkPos.z-renderDistance)-pos.z));\n          side = 0;\n        } else {\n          texX = (dir.x*(current.y+16*(chunkPos.y-chunkHeight)+1-pos.y)/dir.y)-(current.x+16*(chunkPos.x-renderDistance)-pos.x);\n          texY = (dir.z*(current.y+16*(chunkPos.y-chunkHeight)+1-pos.y)/dir.y)-(current.z+16*(chunkPos.z-renderDistance)-pos.z);\n          side = 1;\n        }\n      } else { //if (current.z != last.z) {\n        if (current.z > last.z) {\n          texX = 1-((dir.x*(current.z+16*(chunkPos.z-renderDistance)-pos.z)/dir.z)-(current.x+16*(chunkPos.x-renderDistance)-pos.x));\n          texY = 1-((dir.y*(current.z+16*(chunkPos.z-renderDistance)-pos.z)/dir.z)-(current.y+16*(chunkPos.y-chunkHeight)-pos.y));\n          side = 2;\n        } else {\n          texX = (dir.x*(current.z+16*(chunkPos.z-renderDistance)+1-pos.z)/dir.z)-(current.x+16*(chunkPos.x-renderDistance)-pos.x);\n          texY = 1-((dir.y*(current.z+16*(chunkPos.z-renderDistance)+1-pos.z)/dir.z)-(current.y+16*(chunkPos.y-chunkHeight)-pos.y));\n          side = 3;\n        }\n      }\n      blockFound = drawTexture(blockId, side, texX, texY, light, metadata);\n    }\n  }\n}\n\nvoid main(void) {\n  vec3 dir = getRaySphere(2*M_PI, M_PI);\n  //vec3 dir = getRayFlat(fovx, fovy);\n  vec3 point = vec3(cameraPos);\n  ivec3 current = ivec3(floor(point))%16;\n	//color = texture(tex, texcoord);\n  \n  //nearest cube (not really a vector)\n  vec3 vector;\n  {\n    //temp variable\n    ivec3 nextDir;\n    if (dir.x > 0) {\n        nextDir.x = int(ceil(point.x));\n    } else {\n        nextDir.x = int(floor(point.x));\n    }\n    if (dir.y > 0) {\n        nextDir.y = int(ceil(point.y));\n    } else {\n        nextDir.y = int(floor(point.y));\n    }\n    if (dir.z > 0) {\n        nextDir.z = int(ceil(point.z));\n    } else {\n        nextDir.z = int(floor(point.z));\n    }\n    \n    vector = abs((nextDir - point)/dir);\n  }\n  vec3 inc = abs(1/dir);\n  \n  //Allows for incrementing/decrementing without calculating which one every time.\n  ivec3 iinc;\n  if (dir.x > 0) {\n      iinc.x = 1;\n  } else {\n      iinc.x = -1;\n  }\n  if (dir.y > 0) {\n      iinc.y = 1;\n  } else {\n      iinc.y = -1;\n  }\n  if (dir.z > 0) {\n      iinc.z = 1;\n  } else {\n      iinc.z = -1;\n  }\n  \n  trace(current, vector, dir, mod(cameraPos, 16), inc, iinc);\n}";
	
	private int shaderProgram;
	private int vShader;
	private int fShader;
	private int vbo;
	private int worldChunkSsbo;
	private int chunkSsbo;
	private int worldMetadataSsbo;
	private int metadataSsbo;
	
	public void createShaderProgram() {
		shaderProgram = GL20.glCreateProgram();
		vShader = createShader(vertexShader, GL20.GL_VERTEX_SHADER);
		fShader = createShader(fragmentShader, GL20.GL_FRAGMENT_SHADER);
		
		GL20.glAttachShader(shaderProgram, vShader);
		GL20.glAttachShader(shaderProgram, fShader);
		GL20.glBindAttribLocation(shaderProgram, 0, "vertex");
		GL30.glBindFragDataLocation(shaderProgram, 0, "color");
		GL20.glLinkProgram(shaderProgram);
		
		GL20.glDetachShader(shaderProgram, vShader);
		GL20.glDetachShader(shaderProgram, fShader);
		GL20.glDeleteShader(vShader);
		vShader = 0;
		GL20.glDeleteShader(fShader);
		fShader = 0;
		
		int linked = GL20.glGetProgrami(shaderProgram, GL20.GL_LINK_STATUS);
		String programLog = GL20.glGetProgramInfoLog(shaderProgram, GL20.glGetProgrami(shaderProgram, GL20.GL_INFO_LOG_LENGTH));
		if (programLog.trim().length() > 0) {
			System.err.println(programLog);
		}
		if (linked == 0) {
			throw new AssertionError("Could not link program");
		}
		
		//init vbo
		vbo = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		ByteBuffer bb = BufferUtils.createByteBuffer(2 * 6);
		bb.put((byte) -1).put((byte) -1);
		bb.put((byte) 1).put((byte) -1);
		bb.put((byte) 1).put((byte) 1);
		bb.put((byte) 1).put((byte) 1);
		bb.put((byte) -1).put((byte) 1);
		bb.put((byte) -1).put((byte) -1);
		bb.flip();
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, bb, GL15.GL_STATIC_DRAW);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		
		worldChunkSsbo = GL15.glGenBuffers();
		chunkSsbo = GL15.glGenBuffers();
		worldMetadataSsbo = GL15.glGenBuffers();
		metadataSsbo = GL15.glGenBuffers();
		
		GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, worldChunkSsbo);
		GL15.glBufferData(GL43.GL_SHADER_STORAGE_BUFFER, 0, GL15.GL_DYNAMIC_DRAW);
		GL30.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, 2, worldChunkSsbo);
		
		GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, chunkSsbo);
		GL15.glBufferData(GL43.GL_SHADER_STORAGE_BUFFER, 0, GL15.GL_DYNAMIC_DRAW);
		GL30.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, 3, chunkSsbo);
		
		GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, worldMetadataSsbo);
		GL15.glBufferData(GL43.GL_SHADER_STORAGE_BUFFER, 0, GL15.GL_DYNAMIC_DRAW);
		GL30.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, 4, worldMetadataSsbo);
		
		GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, metadataSsbo);
		GL15.glBufferData(GL43.GL_SHADER_STORAGE_BUFFER, 0, GL15.GL_DYNAMIC_DRAW);
		GL30.glBindBufferBase(GL43.GL_SHADER_STORAGE_BUFFER, 5, metadataSsbo);
		GL15.glBindBuffer(GL43.GL_SHADER_STORAGE_BUFFER, 0);
	}
	
	private int createShader(String resource, int type) {
		int shader = GL20.glCreateShader(type);
		GL20.glShaderSource(shader, resource);
		GL20.glCompileShader(shader);
		int compiled = GL20.glGetShaderi(shader, GL20.GL_COMPILE_STATUS);
		String shaderLog = GL20.glGetShaderInfoLog(shader, GL20.glGetShaderi(shader, GL20.GL_INFO_LOG_LENGTH));
		if (shaderLog.trim().length() > 0) {
			System.err.println(shaderLog);
		}
		if (compiled == 0) {
			throw new AssertionError("Could not compile shader");
		}
		return shader;
	}
	
	public void deleteShaderProgram() {
		GL15.glDeleteBuffers(vbo);
		vbo = 0;
		GL15.glDeleteBuffers(worldChunkSsbo);
		worldChunkSsbo = 0;
		GL15.glDeleteBuffers(chunkSsbo);
		chunkSsbo = 0;
		GL20.glDeleteProgram(shaderProgram);
		shaderProgram = 0;
	}
	
	public int getShaderProgram() {
		return shaderProgram;
	}
	
	public int getVbo() {
		return vbo;
	}
	
	public int getWorldChunkSsbo() {
		return worldChunkSsbo;
	}
	
	public int getChunkSsbo() {
		return chunkSsbo;
	}
	
	public int getWorldMetadataSsbo() {
		return worldMetadataSsbo;
	}
	
	public int getMetadataSsbo() {
		return metadataSsbo;
	}
}
