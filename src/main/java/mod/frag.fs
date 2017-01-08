#version 430

#define M_PI 3.141592

#define CHUNK_SIZE 16*16*16

in vec2 texcoord;

uniform vec3 cameraPos;
uniform vec3 cameraDir;
uniform float fovx;
uniform float fovy;
uniform int renderDistance;
uniform int chunkHeight;

uniform sampler2D tex;

out vec4 color;

struct BlockData {
  int id;
  int lightLevel;
};

layout(std430, binding = 2) buffer worldChunks
{
  int location[];
};

layout(std430, binding = 3) buffer chunk
{
  BlockData blockData[];
};

layout(std430, binding = 4) buffer worldMetadata
{
  int metaLocation[];
};

layout(std430, binding = 5) buffer metadata
{
  int blockMetadata[];
};

vec3 rotate(vec3 camera, vec3 ray) {
  //rotate z
  float x = cos(camera.z)*ray.x + sin(camera.z)*ray.y;
  float y = cos(camera.z)*ray.y - sin(camera.z)*ray.x;
  ray.x = x;
  ray.y = y;
  
  //rotate x
  y = cos(camera.x)*ray.y - sin(camera.x)*ray.z;
  float z = cos(camera.x)*ray.z + sin(camera.x)*ray.y;
  ray.y = y;
  ray.z = z;
  
  //rotate y
  x = cos(camera.y)*ray.x - sin(camera.y)*ray.z;
  z = cos(camera.y)*ray.z + sin(camera.y)*ray.x;
  ray.x = x;
  ray.z = z;
  
  return ray;
}

//Gets the direction of the ray assuming a flat screen
vec3 getRayFlat(float fovx, float fovy) {
  vec3 ray = vec3((-1+texcoord.x*2)*tan(fovy/2)*fovx/fovy, (-1+texcoord.y*2)*tan(fovy/2), -1);
  return rotate(cameraDir, ray);
}

//Gets the direction of the ray assuming a spherical screen
vec3 getRaySphere(float fovx, float fovy) {
  vec3 ray = vec3(-sin((texcoord.x)*fovx)*sin((texcoord.y)*fovy),
    -cos((texcoord.y)*fovy),
    cos((texcoord.x)*fovx)*sin((texcoord.y)*fovy));
  return rotate(cameraDir, ray);
}

bool drawTexture(int id, int side, float xIn, float yIn, int light, int metadata) {
  float x = 12;
  float y = 24;
  switch (id) {
  case 1: //stone
    x = 24;
    switch (metadata) {
    case 0: //stone
      y = 0;
      break;
    case 1: //granite
      y = 5;
      break;
    case 2: //polished granite
      y = 6;
      break;
    case 3: //diorite
      y = 3;
      break;
    case 4: //polished diorite
      y = 4;
      break;
    case 5: //andesite
      y = 1;
      break;
    case 6: //polished andesite
      y = 2;
      break;
    }
    break;
  case 2: //grass
    if (side == 1) {
      x = 7; //TODO if snow above
      y = 12;
    } else if (side == 0) {
      x = 10;
      y = 6;
    } else {
      x = 4;
      y = 12;
    }
    break;
  case 3: //dirt
    switch (metadata) {
    case 0: //dirt
      x = 10;
      y = 6;
      break;
    case 1: //coarse dirt
      x = 7;
      y = 6;
      break;
    case 2: //podzol
      if (side == 0) {
        x = 10;
        y = 6;
      }
      else if (side == 1) {
        x = 11;
        y = 0;
      } else {
        x = 10;
        y = 7;
      }
    }
    break;
  case 4: //cobblestone
    x = 0;
    y = 7;
    break;
  case 5: //planks
    x = 18;
    switch (metadata) {
    case 0: //oak
      y = 3;
      break;
    case 1: //spruce
      y = 4;
      break;
    case 2: //birch
      y = 1;
      break;
    case 3: //jungle
      y = 2;
      break;
    case 4: //acacia
      x = 17;
      y = 15;
      break;
    case 5: //dark oak
      y = 0;
      break;
    }
    break;
  case 7: //bedrock
    x = 4;
    y = 3;
    break;
  case 12: //sand
    if (metadata == 0) { //sand
      x = 21;
      y = 14;
    } else { //red sand
      x = 20;
      y = 7;
    }
    break;
  case 8: //flowing_water
    x = 2; //TODO
    y = 0;
    break;
  case 9: //water
    if (side == 1) {
      x = 25;
      y = 12;
    } else {
      x = 2;
      y = 0;
    }
    break;
  case 10: //flowing_lava
  case 11: //TODO
    x = 0;
    y = 0;
    break;
  case 13: //gravel
    x = 8;
    y = 12;
    break;
  case 14: //gold_ore
    x = 1;
    y = 12;
    break;
  case 15: //iron_ore
    x = 3;
    y = 14;
    break;
  case 16: //coal_ore
    x = 6;
    y = 6;
    break;
  case 17: //log
    y = 15;
    switch (metadata&3) { //types of log
    case 0: //oak
      x = 11;
      break;
    case 1: //spruce
      x = 13;
      break;
    case 2: //birch
      x = 7;
      break;
    case 3: //jungle
      x = 9;
      break;
    }
    if ((metadata&12) == 0) { //facing y
      if (side < 2) {
        x++;
      }
    }
    else if ((metadata&12) == 4) { //facing x
      if (side == 4 || side == 5) {
        x++;
        if (side == 5) {
          xIn = 1-xIn; //rotate face
          yIn = 1-yIn;
        }
      } else {
        float temp = xIn; //rotate faces
        xIn = yIn;
        yIn = 1-temp;
        if (side == 2) {
          xIn = 1-xIn;
          yIn = 1-yIn;
        }
      }
    }
    else if ((metadata&12) == 8) { //facing z
      if (side == 2 || side == 3) {
        x++;
        if (side == 2) {
          xIn = 1-xIn;
          yIn = 1-yIn;
        }
      } else {
        if (side == 0) {
          xIn = 1-xIn;
          yIn = 1-yIn;
        }
        else if (side == 4) {
          float temp = xIn;
          xIn = 1-yIn;
          yIn = temp;
        }
        else if (side == 5) {
          float temp = xIn;
          xIn = yIn;
          yIn = 1-temp;
        }
      }
    }
    break;
  case 18: //leaves
    switch (metadata&3) {
    case 0: //oak
      x = 12;
      y = 14;
      break;
    case 1: //spruce
      x = 1;
      y = 15;
      break;
    case 2: //birch
      x = 0;
      y = 15;
      break;
    case 3: //jungle
      x = 15;
      y = 14;
      break;
    }
    break;
  case 19: //sponge
    x = 23;
    y = 14 + metadata; //or wet sponge
    break;
  case 20: //glass
    x = 13;
    y = 9;
    break;
  case 21: //lapis_ore
    x = 10;
    y = 14;
    break;
  case 22: //lapis_block
    x = 9;
    y = 14;
    break;
  case 23: //dispenser
    switch (metadata) {
    case 0: // negative y
      if (side == 0) {
        x = 11;
        y = 2;
      } else {
        x = 12;
        y = 9;
      }
      if (side >= 2) {
        xIn = 1-xIn;
        yIn = 1-yIn;
      }
      break;
    case 1: // positive y
      if (side == 1) {
        x = 11;
        y = 2;
      } else {
        x = 12;
        y = 9;
      }
      break;
    case 2: //negative z
      if (side == 2) {
        x = 11;
        y = 1;
      }
      else if (side < 2) {
        x = 12;
        y = 9;
      } else {
        x = 11;
        y = 9;
      }
      break;
    case 3: //positive z
      if (side == 3) {
        x = 11;
        y = 1;
      }
      else if (side < 2) {
        x = 12;
        y = 9;
        xIn = 1-xIn;
        yIn = 1-yIn;
      } else {
        x = 11;
        y = 9;
      }
      break;
    case 4: //negative x
      if (side == 4) {
        x = 11;
        y = 1;
      }
      else if (side < 2) {
        x = 12;
        y = 9;
        if (side == 0) {
          float temp = xIn;
          xIn = yIn;
          yIn = 1-temp;
        } else {
          float temp = xIn;
          xIn = 1-yIn;
          yIn = temp;
        }
      } else {
        x = 11;
        y = 9;
      }
      break;
    case 5: //positive x
      if (side == 5) {
        x = 11;
        y = 1;
      }
      else if (side < 2) {
        x = 12;
        y = 9;
        if (side == 0) {
          float temp = xIn;
          xIn = 1-yIn;
          yIn = temp;
        } else {
          float temp = xIn;
          xIn = yIn;
          yIn = 1-temp;
        }
      } else {
        x = 11;
        y = 9;
      }
      break;
    }
    break;
  case 24: //sandstone
    switch (metadata) {
    case 0: //sandstone
      if (side == 0) {
        x = 21;
        y = 15;
      }
      else if (side == 1) {
        x = 22;
        y = 3;
      } else {
        x = 22;
        y = 1;
      }
      break;
    case 1: //chiseled sandstone
      if (side < 2) {
        x = 22;
        y = 3;
      } else {
        x = 22;
        y = 0;
      }
      break;
    case 2: //smooth sandstone
      if (side < 2) {
        x = 22;
        y = 3;
      } else {
        x = 22;
        y = 2;
      }
      break;
    }
    break;
  case 25: //noteblock
    x = 17;
    y = 3;
    break;
  case 35: //wool
    switch (metadata) {
    case 0: //white
      x = 27;
      y = 5;
      break;
    case 1: //orange
      x = 27;
      y = 0;
      break;
    case 2: //magenta
      x = 26;
      y = 15;
      break;
    case 3: //light blue
      x = 26;
      y = 13;
      break;
    case 4: //yellow
      x = 27;
      y = 6;
      break;
    case 5: //lime
      x = 26;
      y = 14;
      break;
    case 6: //pink
      x = 27;
      y = 1;
      break;
    case 7: //gray
      x = 26;
      y = 11;
      break;
    case 8: //light gray
      x = 27;
      y = 4;
      break;
    case 9: //cyan
      x = 26;
      y = 10;
      break;
    case 10: //purple
      x = 27;
      y = 2;
      break;
    case 11: //blue
      x = 26;
      y = 8;
      break;
    case 12: //brown
      x = 26;
      y = 9;
      break;
    case 13: //green
      x = 26;
      y = 12;
      break;
    case 14: //red
      x = 27;
      y = 3;
      break;
    case 15: //black
      x = 26;
      y = 7;
      break;
    }
    break;
  case 41: //gold_block
    x = 0;
    y = 12;
    break;
  case 42: //iron_block
    x = 2;
    y = 14;
    break;
  case 43: //double_stone_slab
    switch (metadata) {
    case 0: //stone
      x = 24;
      if (side < 2) {
        y = 8;
      } else {
        y = 7;
      }
      break;
    case 1: //sandstone
      if (side == 0) {
        x = 21;
        y = 15;
      }
      else if (side == 1) {
        x = 22;
        y = 3;
      } else {
        x = 22;
        y = 1;
      }
      break;
    case 3: //cobblestone
      x = 0;
      y = 7;
      break;
    case 4: //brick
      x = 7;
      y = 1;
      break;
    case 5: //stone brick
      x = 24;
      y = 9;
      break;
    case 6: //nether brick
      x = 16;
      y = 13;
      break;
    case 7: //quartz
      if (side == 0) {
        x = 19;
        y = 6;
      } else {
        x = 19;
        y = 11;
      }
      break;
    }
    break;
  case 45: //brick_block
    x = 7;
    y = 1;
    break;
  case 46: //tnt
    x = 25;
    if (side == 0) {
      y = 3;
    }
    else if (side == 1) {
      y = 5;
    } else {
      y = 4;
    }
    break;
  case 47: //bookshelf
    if (side < 2) {
      x = 18;
      y = 3;
    } else {
      x = 6;
      y = 2;
    }
    break;
  case 48: //mossy_cobblestone
    x = 1;
    y = 7;
    break;
  case 49: //obsidian
    x = 17;
    y = 9;
    break;
  case 52: //mob_spawner
    x = 16;
    y = 4;
    break;
  case 56: //diamond_ore
    x = 10;
    y = 5;
    break;
  case 57: //diamond_block
    x = 10;
    y = 4;
    break;
  case 58: //crafting_table
    if (side == 0) {
      x = 18;
      y = 3;
    }
    else if (side == 1) {
      x = 8;
      y = 5;
    }
    else if ((side&1) == 1) {
      x = 8;
      y = 4;
    } else {
      x = 8;
      y = 3;
    }
    break;
  case 61: //furnace
    y = 9;
    if (side < 2) {
      x = 12;
    }
    else if (side == metadata) {
      x = 9;
    } else {
      x = 11;
    }
    break;
  case 62: //lit_furnace
    y = 9;
    if (side < 2) {
      x = 12;
    }
    else if (side == metadata) {
      x = 10;
    } else {
      x = 11;
    }
    break;
  case 73: //redstone_ore
  case 74: //lit_redstone_ore
    x = 21;
    y = 4;
    break;
  case 79: //ice
    x = 15;
    y = 13;
    break;
  case 80: //snow
    x = 23;
    y = 12;
    break;
  case 82: //clay
    x = 4;
    y = 6;
    break;
  case 84: //jukebox
    if (side == 1) {
      x = 7;
      y = 14;
    } else {
      x = 6;
      y = 14;
    }
    break;
  case 86: //pumpkin
    if (side < 2) {
      x = 19;
      y = 2;
      xIn = 1-xIn;
      yIn = 1-yIn;
    } else {
      x = 18;
      y = 15;
    }
    switch (metadata) {
    case 0:
      if (side == 3) {
        y = 13;
      }
      break;
    case 1:
      if (side == 4) {
        y = 13;
      }
      break;
    case 2:
      if (side == 2) {
        y = 13;
      }
      break;
    case 3:
      if (side == 5) {
        y = 13;
      }
      break;
    }
    break;
  case 87: //netherrack
    x = 17; //TODO fix rotation
    y = 2;
    break;
  case 88: //soul_sand
    x = 23;
    y = 13;
    break;
  case 89: //glowstone
    x = 15;
    y = 11;
    break;
  case 91: //lit_pumpkin
    if (side < 2) {
      x = 19;
      y = 2;
      xIn = 1-xIn;
      yIn = 1-yIn;
    } else {
      x = 18;
      y = 15;
    }
    switch (metadata) {
    case 0:
      if (side == 3) {
        y = 14;
      }
      break;
    case 1:
      if (side == 4) {
        y = 14;
      }
      break;
    case 2:
      if (side == 2) {
        y = 14;
      }
      break;
    case 3:
      if (side == 5) {
        y = 14;
      }
      break;
    }
    break;
  case 97: //monster_egg
    switch (metadata) {
    case 0: //stone
      x = 24;
      y = 0;
      break;
    case 1: //cobblestone
      x = 0;
      y = 7;
      break;
    case 2: //stone brick
      x = 24;
      y = 9;
      break;
    case 3: //mossy stone brick
      x = 24;
      y = 12;
      break;
    case 4: //cracked stone brick
      x = 24;
      y = 11;
      break;
    case 5: //chiseled stone brick
      x = 24;
      y = 10;
      break;
    }
    break;
  case 98: //stonebrick
    switch (metadata) {
    case 0:
      x = 24;
      y = 9;
      break;
    case 1: //mossy
      x = 24;
      y = 12;
      break;
    case 2: //cracked
      x = 24;
      y = 11;
      break;
    case 3: //chiseled
      x = 24;
      y = 10;
      break;
    }
    break;
    /*
  case 99: //brown_mushroom_block
  case 100: //red_mushroom_block
    x = 16;
    switch (metadata) {
    case 0: //all_inside
      y = 5;
      break;
    case 1: //north_west
      if (side == 1 || side == 2 || side == 4) {
        y = 6;
      } else {
        y = 5;
      }
      break;
    case 2: //north
      if (side == 1 || side == 2) {
        y = 6;
      } else {
        y = 5;
      }
      break;
    case 3: //north_east
      if (side == 1 || side == 2 || side == 5) {
        y = 6;
      } else {
        y = 5;
      }
      break;
    case 4: //west
      if (side == 1 || side == 4) {
        y = 6;
      } else {
        y = 5;
      }
      break;
    case 5: //center
      if (side == 1) {
        y = 6;
      } else {
        y = 5;
      }
      break;
    case 6: //east
      if (side == 1 || side == 5) {
        y = 6;
      } else {
        y = 5;
      }
      break;
    case 7: //south_west
      if (side == 1 || side == 3 || side == 4) {
        y = 6;
      } else {
        y = 5;
      }
      break;
    case 8: //south
      if (side == 1 || side == 3) {
        y = 6;
      } else {
        y = 5;
      }
      break;
    case 9: //south_east
      if (side == 1 || side == 3 || side == 5) {
        y = 6;
      } else {
        y = 5;
      }
      break;
    }
    if (id == 100 && y == 6) {
      y = 7;
    }
    break;
    */
  default:
    return false;
  }
  //TODO do sunlight
  color = texture(tex, vec2((x + xIn)/32, (y + yIn)/32))/* * vec4((light+1)/16.0, (light+1)/16.0, (light+1)/16.0, 1)*/;
  return true;
}

void trace(inout ivec3 current, vec3 vector, vec3 dir, vec3 pos, vec3 inc, ivec3 iinc) {
  ivec3 last = ivec3(current.xyz);
  ivec3 chunkPos = ivec3(renderDistance, chunkHeight, renderDistance);
  int worldWidth = renderDistance*2+1;
  int chunkId = location[chunkPos.z*worldWidth*16 + chunkPos.x*16 + chunkPos.y];
  //TODO if player is above the world
  int lastChunkId; //used for light levels
  int blockId;
  bool blockFound = false;
  int side;
  float texX;
  float texY;
  while (!blockFound) {
    //TODO improve direction testing
    last = current;
    blockId = 0;
    
    //Next cube
    if (vector.x < vector.y) {
      if (vector.x < vector.z) {
          vector.x += inc.x;
          current.x += iinc.x;
          if ((current.x&16) == 16) {
            current.x &= 15;
            last.x = current.x - iinc.x;
            chunkPos.x += iinc.x;
            if (chunkPos.x == worldWidth || chunkPos.x == -1) {
              return;
            }
            lastChunkId = chunkId;
            chunkId = location[chunkPos.z*worldWidth*16 + chunkPos.x*16 + chunkPos.y];
          }
      } else {
          vector.z += inc.z;
          current.z += iinc.z;
          if ((current.z&16) == 16) {
            current.z &= 15;
            last.z = current.z - iinc.z;
            chunkPos.z += iinc.z;
            if (chunkPos.z == worldWidth || chunkPos.z == -1) {
              return;
            }
            lastChunkId = chunkId;
            chunkId = location[chunkPos.z*worldWidth*16 + chunkPos.x*16 + chunkPos.y];
          }
      }
    } else {
      if (vector.y < vector.z) {
          vector.y += inc.y;
          current.y += iinc.y;
          if ((current.y&16) == 16) {
            current.y &= 15;
            last.y = current.y - iinc.y;
            chunkPos.y += iinc.y;
            if ((chunkPos.y&16) == 16) {
              return;
            }
            lastChunkId = chunkId;
            chunkId = location[chunkPos.z*worldWidth*16 + chunkPos.x*16 + chunkPos.y];
          }
      } else {
          vector.z += inc.z;
          current.z += iinc.z;
          if ((current.z&16) == 16) {
            current.z &= 15;
            last.z = current.z - iinc.z;
            chunkPos.z += iinc.z;
            if (chunkPos.z == worldWidth || chunkPos.z == -1) {
              return;
            }
            lastChunkId = chunkId;
            chunkId = location[chunkPos.z*worldWidth*16 + chunkPos.x*16 + chunkPos.y];
          }
      }
    }
    
    //TODO skip empty chunks
    
    if (chunkId != 0) {
      blockId = blockData[(chunkId-1)*CHUNK_SIZE + (current.y<<8) + (current.z<<4) + current.x].id;
    }
    
    //if (block != air)
    if (blockId != 0) {
      int light = blockData[(lastChunkId-1)*CHUNK_SIZE + ((last.y%16)<<8) + ((last.z%16)<<4) + (last.x%16)].lightLevel;
      int metadataId = metaLocation[chunkPos.z*worldWidth*16 + chunkPos.x*16 + chunkPos.y];
      int metadata = 0;
      
      if (metadataId != 0) {
        metadata = blockMetadata[(metadataId-1)*CHUNK_SIZE + (current.y<<8) + (current.z<<4) + current.x];
      }
      
      if (current.x != last.x) {
        if (current.x > last.x) {
          texX = (dir.z*(current.x+16*(chunkPos.x-renderDistance)-pos.x)/dir.x)-(current.z+16*(chunkPos.z-renderDistance)-pos.z);
          texY = 1-((dir.y*(current.x+16*(chunkPos.x-renderDistance)-pos.x)/dir.x)-(current.y+16*(chunkPos.y-chunkHeight)-pos.y));
          side = 4;
        } else {
          texX = 1-((dir.z*(current.x+16*(chunkPos.x-renderDistance)+1-pos.x)/dir.x)-(current.z+16*(chunkPos.z-renderDistance)-pos.z));
          texY = 1-((dir.y*(current.x+16*(chunkPos.x-renderDistance)+1-pos.x)/dir.x)-(current.y+16*(chunkPos.y-chunkHeight)-pos.y));
          side = 5;
        }
      } else if (current.y != last.y) {
        if (current.y > last.y) {
          texX = (dir.x*(current.y+16*(chunkPos.y-chunkHeight)-pos.y)/dir.y)-(current.x+16*(chunkPos.x-renderDistance)-pos.x);
          texY = 1-((dir.z*(current.y+16*(chunkPos.y-chunkHeight)-pos.y)/dir.y)-(current.z+16*(chunkPos.z-renderDistance)-pos.z));
          side = 0;
        } else {
          texX = (dir.x*(current.y+16*(chunkPos.y-chunkHeight)+1-pos.y)/dir.y)-(current.x+16*(chunkPos.x-renderDistance)-pos.x);
          texY = (dir.z*(current.y+16*(chunkPos.y-chunkHeight)+1-pos.y)/dir.y)-(current.z+16*(chunkPos.z-renderDistance)-pos.z);
          side = 1;
        }
      } else { //if (current.z != last.z) {
        if (current.z > last.z) {
          texX = 1-((dir.x*(current.z+16*(chunkPos.z-renderDistance)-pos.z)/dir.z)-(current.x+16*(chunkPos.x-renderDistance)-pos.x));
          texY = 1-((dir.y*(current.z+16*(chunkPos.z-renderDistance)-pos.z)/dir.z)-(current.y+16*(chunkPos.y-chunkHeight)-pos.y));
          side = 2;
        } else {
          texX = (dir.x*(current.z+16*(chunkPos.z-renderDistance)+1-pos.z)/dir.z)-(current.x+16*(chunkPos.x-renderDistance)-pos.x);
          texY = 1-((dir.y*(current.z+16*(chunkPos.z-renderDistance)+1-pos.z)/dir.z)-(current.y+16*(chunkPos.y-chunkHeight)-pos.y));
          side = 3;
        }
      }
      blockFound = drawTexture(blockId, side, texX, texY, light, metadata);
    }
  }
}

void main(void) {
  vec3 dir = getRaySphere(2*M_PI, M_PI);
  //vec3 dir = getRayFlat(fovx, fovy);
  vec3 point = vec3(cameraPos);
  ivec3 current = ivec3(floor(point))%16;
	//color = texture(tex, texcoord);
  
  //nearest cube (not really a vector)
  vec3 vector;
  {
    //temp variable
    ivec3 nextDir;
    if (dir.x > 0) {
        nextDir.x = int(ceil(point.x));
    } else {
        nextDir.x = int(floor(point.x));
    }
    if (dir.y > 0) {
        nextDir.y = int(ceil(point.y));
    } else {
        nextDir.y = int(floor(point.y));
    }
    if (dir.z > 0) {
        nextDir.z = int(ceil(point.z));
    } else {
        nextDir.z = int(floor(point.z));
    }
    
    vector = abs((nextDir - point)/dir);
  }
  vec3 inc = abs(1/dir);
  
  //Allows for incrementing/decrementing without calculating which one every time.
  ivec3 iinc;
  if (dir.x > 0) {
      iinc.x = 1;
  } else {
      iinc.x = -1;
  }
  if (dir.y > 0) {
      iinc.y = 1;
  } else {
      iinc.y = -1;
  }
  if (dir.z > 0) {
      iinc.z = 1;
  } else {
      iinc.z = -1;
  }
  
  trace(current, vector, dir, mod(cameraPos, 16), inc, iinc);
}