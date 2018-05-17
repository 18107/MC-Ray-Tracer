#version 430

#define M_PI 3.141592

#define CHUNK_SIZE 16*16*16

#define CHUNK_WIDTH 16 //must be a power of 2

#define WORLD_HEIGHT_CHUNKS 16

#define TEXTURE_RESOLUTION 16

const mat4 rotation[8] = mat4[](
  mat4(1,0,0,0, 0,1,0,0, 0,0,1,0, 0,0,0,1),
  mat4(0,0,-1,0, 0,1,0,0, 1,0,0,0, 0,0,1,1),
  mat4(-1,0,0,0, 0,1,0,0, 0,0,-1,0, 1,0,1,1),
  mat4(0,0,1,0, 0,1,0,0, -1,0,0,0, 1,0,0,1),
  mat4(1,0,0,0, 0,0,1,0, 0,-1,0,0, 0,1,0,1),
  mat4(1,0,0,0, 0,0,-1,0, 0,1,0,0, 0,0,1,1),
  mat4(0,1,0,0, -1,0,0,0, 0,0,1,0, 1,0,0,1),
  mat4(0,-1,0,0, 1,0,0,0, 0,0,1,0, 0,1,0,1)
);

in vec2 texcoord;

uniform vec3 cameraPos;
uniform vec3 cameraDir;
uniform float fovx;
uniform float fovy;
uniform int renderDistance;
uniform int chunkHeight;
uniform bool spherical;
uniform bool stereoscopic3d;
uniform float eyeWidth;

uniform sampler2D tex;

out vec4 color;

struct BlockData {
  int id;
  int rotation;
  int lightLevel;
  int unused;
};

layout(std430, binding = 2) buffer worldChunks
{
  int location[];
};

layout(std430, binding = 3) buffer chunk
{
  BlockData blockData[];
};

layout(std430, binding = 4) buffer iddata
{
  int idData[];
};

layout(std430, binding = 5) buffer voxelData
{
  vec4 voxelColor[];
};

layout(std430, binding = 6) buffer textureData
{
  vec4 textureColor[];
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
vec3 getRayFlat(float fovx, float fovy, bool stereoscopic) {
  vec3 ray;
  if (stereoscopic) {
    if (texcoord.x < 0.5) {
      ray = vec3((-1+texcoord.x*4)*tan(fovy/2)*fovx/fovy, (-1+texcoord.y*2)*tan(fovy/2), -1);
    } else {
      ray = vec3((-3+texcoord.x*4)*tan(fovy/2)*fovx/fovy, (-1+texcoord.y*2)*tan(fovy/2), -1);
    }
  } else {
    ray = vec3((-1+texcoord.x*2)*tan(fovy/2)*fovx/fovy, (-1+texcoord.y*2)*tan(fovy/2), -1);
  }
  return rotate(cameraDir, ray);
}

//Gets the direction of the ray assuming a spherical screen
vec3 getRaySphere(float fovx, float fovy, bool stereoscopic) {
  vec3 ray;
  if (stereoscopic) {
    ray = vec3(-sin((texcoord.x*2)*fovx)*sin((texcoord.y)*fovy),
      -cos((texcoord.y)*fovy),
      cos((texcoord.x*2)*fovx)*sin((texcoord.y)*fovy));
  } else {
    ray = vec3(-sin((texcoord.x)*fovx)*sin((texcoord.y)*fovy),
      -cos((texcoord.y)*fovy),
      cos((texcoord.x)*fovx)*sin((texcoord.y)*fovy));
  }
  return rotate(cameraDir, ray);
}

bool traceBlock(int id, vec3 nearestVoxel, ivec4 currentVoxel, mat4 rotate, vec3 vinc, ivec3 iinc) {
  ivec4 rotated;
  while (true) { //TODO condition
    if (nearestVoxel.x < nearestVoxel.y) {
      if (nearestVoxel.x < nearestVoxel.z) {
        nearestVoxel.x += vinc.x;
        currentVoxel.x += iinc.x;
        if (currentVoxel.x < 0 || currentVoxel.x >= TEXTURE_RESOLUTION) {
          return false;
        }
      } else {
        nearestVoxel.z += vinc.z;
        currentVoxel.z += iinc.z;
        if (currentVoxel.z < 0 || currentVoxel.z >= TEXTURE_RESOLUTION) {
          return false;
        }
      }
    } else {
      if (nearestVoxel.y < nearestVoxel.z) {
        nearestVoxel.y += vinc.y;
        currentVoxel.y += iinc.y;
        if (currentVoxel.y < 0 || currentVoxel.y >= TEXTURE_RESOLUTION) {
          return false;
        }
      } else {
        nearestVoxel.z += vinc.z;
        currentVoxel.z += iinc.z;
        if (currentVoxel.z < 0 || currentVoxel.z >= TEXTURE_RESOLUTION) {
          return false;
        }
      }
    }
    rotated = ivec4(rotate*currentVoxel);
    color = voxelColor[id*TEXTURE_RESOLUTION*TEXTURE_RESOLUTION*TEXTURE_RESOLUTION +
        rotated.z*TEXTURE_RESOLUTION*TEXTURE_RESOLUTION +
        rotated.y*TEXTURE_RESOLUTION + rotated.x];
    if (color.a != 0) {
      return true;
    }
  }
}

bool setupTraceFirstBlock() { //TODO
  return false;
  //ivec4 currentVoxel = ivec4(floor(mod(pos,1)),1);
}

bool setupTraceBlock(int id, vec3 nearestCube, vec3 inc, ivec3 iinc, ivec3 current, ivec3 last, mat4 rotate) {
  vec3 nearestVoxel;
  ivec4 currentVoxel;

  //get current location
  if (current.x != last.x) {
    float plane = nearestCube.x - inc.x;
    float distanceY = (nearestCube.y - plane)/inc.y;
    float distanceZ = (nearestCube.z - plane)/inc.z;
    currentVoxel.y = int(floor(TEXTURE_RESOLUTION*((iinc.y+1)/2) - distanceY*iinc.y*TEXTURE_RESOLUTION));
    currentVoxel.z = int(floor(TEXTURE_RESOLUTION*((iinc.z+1)/2) - distanceZ*iinc.z*TEXTURE_RESOLUTION));
    currentVoxel.x = (TEXTURE_RESOLUTION-1)*((-iinc.x+1)/2);
    currentVoxel.w = (TEXTURE_RESOLUTION-1);

    nearestVoxel.x = plane + inc.x/TEXTURE_RESOLUTION;
    nearestVoxel.y = nearestCube.y - inc.y*floor(distanceY*TEXTURE_RESOLUTION)/TEXTURE_RESOLUTION;
    nearestVoxel.z = nearestCube.z - inc.z*floor(distanceZ*TEXTURE_RESOLUTION)/TEXTURE_RESOLUTION;
  }
  else if (current.z != last.z) {
    float plane = nearestCube.z - inc.z;
    float distanceX = (nearestCube.x - plane)/inc.x;
    float distanceY = (nearestCube.y - plane)/inc.y;
    currentVoxel.x = int(floor(TEXTURE_RESOLUTION*((iinc.x+1)/2) - distanceX*iinc.x*TEXTURE_RESOLUTION));
    currentVoxel.y = int(floor(TEXTURE_RESOLUTION*((iinc.y+1)/2) - distanceY*iinc.y*TEXTURE_RESOLUTION));
    currentVoxel.z = (TEXTURE_RESOLUTION-1)*((-iinc.z+1)/2);
    currentVoxel.w = (TEXTURE_RESOLUTION-1);

    nearestVoxel.z = plane + inc.z/TEXTURE_RESOLUTION;
    nearestVoxel.x = nearestCube.x - inc.x*floor(distanceX*TEXTURE_RESOLUTION)/TEXTURE_RESOLUTION;
    nearestVoxel.y = nearestCube.y - inc.y*floor(distanceY*TEXTURE_RESOLUTION)/TEXTURE_RESOLUTION;
  }
  else { //if (current.y != last.y)
    float plane = nearestCube.y - inc.y;
    float distanceX = (nearestCube.x - plane)/inc.x;
    float distanceZ = (nearestCube.z - plane)/inc.z;
    currentVoxel.x = int(floor(TEXTURE_RESOLUTION*((iinc.x+1)/2) - distanceX*iinc.x*TEXTURE_RESOLUTION));
    currentVoxel.z = int(floor(TEXTURE_RESOLUTION*((iinc.z+1)/2) - distanceZ*iinc.z*TEXTURE_RESOLUTION));
    currentVoxel.y = (TEXTURE_RESOLUTION-1)*((-iinc.y+1)/2);
    currentVoxel.w = (TEXTURE_RESOLUTION-1);

    nearestVoxel.y = plane + inc.y/TEXTURE_RESOLUTION;
    nearestVoxel.x = nearestCube.x - inc.x*floor(distanceX*TEXTURE_RESOLUTION)/TEXTURE_RESOLUTION;
    nearestVoxel.z = nearestCube.z - inc.z*floor(distanceZ*TEXTURE_RESOLUTION)/TEXTURE_RESOLUTION;
  }

  ivec4 rotated = ivec4(rotate*currentVoxel);

  color = voxelColor[id*TEXTURE_RESOLUTION*TEXTURE_RESOLUTION*TEXTURE_RESOLUTION +
      rotated.z*TEXTURE_RESOLUTION*TEXTURE_RESOLUTION +
      rotated.y*TEXTURE_RESOLUTION + rotated.x];
  if (color.a != 0) {
    return true;
  }

  //trace voxel
  return traceBlock(id, nearestVoxel, currentVoxel, rotate, inc/TEXTURE_RESOLUTION, iinc);
}

bool drawTexture(int blockId, int blockRotation, int side, float texX, float texY, int light) {
  float temp;
  switch (blockRotation) {
  case 1:
    switch (side) {
    case 0:
      side = 5;
      break;
    case 1:
      side = 4;
      break;
    case 2:
      temp = texX;
      texX = 1 - texY;
      texY = temp;
      break;
    case 3:
      temp = texX;
      texX = texY;
      texY = 1 - temp;
      break;
    case 4:
      side = 0;
      break;
    case 5:
      side = 1;
      break;
    }
    break;
  case 2:
    switch (side) {
    case 0:
      side = 1;
      break;
    case 1:
      side = 0;
      break;
    case 2:
    case 3:
      texX = 1 - texX;
      texY = 1 - texY;
      break;
    case 4:
      side = 5;
      break;
    case 5:
      side = 4;
      break;
    }
    break;
  case 3:
    switch (side) {
    case 0:
      side = 4;
      break;
    case 1:
      side = 5;
      break;
    case 2:
      temp = texX;
      texX = texY;
      texY = 1 - temp;
      break;
    case 3:
      temp = texX;
      texX = 1 - texY;
      texY = temp;
      break;
    case 4:
      side = 1;
      break;
    case 5:
      side = 0;
      break;
    }
    break;
  case 4:
    switch (side) {
    case 0:
      temp = texX;
      texX = texY;
      texY = 1 - temp;
      break;
    case 1:
      temp = texX;
      texX = 1 - texY;
      texY = temp;
      break;
    case 2:
      side = 4;
      temp = texX;
      texX = 1 - texY;
      texY = 1 - temp;
      break;
    case 3:
      side = 5;
      temp = texX;
      texX = texY;
      texY = temp;
      break;
    case 4:
      side = 3;
      temp = texX;
      texX = 1 - texY;
      texY = 1 - temp;
      break;
    case 5:
      side = 2;
      temp = texX;
      texX = texY;
      texY = temp;
      break;
    }
    break;
  case 5:
    switch (side) {
    case 0:
      temp = texX;
      texX = 1 - texY;
      texY = temp;
      break;
    case 1:
      temp = texX;
      texX = texY;
      texY = 1 - temp;
      break;
    case 2:
      side = 5;
      temp = texX;
      texX = texY;
      texY = temp;
      break;
    case 3:
      side = 4;
      temp = texX;
      texX = 1 - texY;
      texY = 1 - temp;
      break;
    case 4:
      side = 2;
      temp = texX;
      texX = 1 - texY;
      texY = 1 - temp;
      break;
    case 5:
      side = 3;
      temp = texX;
      texX = texY;
      texY = temp;
      break;
    }
    break;
  case 6:
    switch (side) {
    case 0:
      side = 2;
      texY = 1 - texY;
      break;
    case 1:
      side = 3;
      texY = 1 - texY;
      break;
    case 2:
      side = 1;
      texX = 1 - texX;
      break;
    case 3:
      side = 0;
      texX = 1 - texX;
      break;
    case 4:
      temp = texX;
      texX = texY;
      texY = 1 - temp;
      break;
    case 5:
      temp = texX;
      texX = 1 - texY;
      texY = temp;
      break;
    }
    break;
  case 7:
    switch (side) {
    case 0:
      side = 3;
      texX = 1 - texX;
      break;
    case 1:
      side = 2;
      texX = 1 - texX;
      break;
    case 2:
      side = 0;
      texX = texX;
      texY = 1 - texY;
      break;
    case 3:
      side = 1;
      texX = texX;
      texY = 1 - texY;
      break;
    case 4:
      temp = texX;
      texX = 1 - texY;
      texY = temp;
      break;
    case 5:
      temp = texX;
      texX = texY;
      texY = 1 - temp;
      break;
    }
    break;
  }
  int texId = idData[blockId*6*3 + side*3 + 1];
  color = textureColor[texId*TEXTURE_RESOLUTION*TEXTURE_RESOLUTION +
    int(floor(texY*TEXTURE_RESOLUTION))*TEXTURE_RESOLUTION +
    int(floor(texX*TEXTURE_RESOLUTION))] *
    vec4((light+1)/16.0, (light+1)/16.0, (light+1)/16.0, 1);
  if (color.a == 0) {
    return false;
  }
  return true;
}

//returns true if successful
bool skipChunk(inout ivec3 current, vec3 nearestCube, inout ivec3 chunkPos, vec3 dir, vec3 pos, vec3 inc, ivec3 iinc, int worldWidth) {
  //TODO avoid magic numbers
  ivec3 jump = (iinc == 1 ? 16 - current : current + 1);
  vec3 jumpDist = (jump-1)*inc;
  if (nearestCube.x + jumpDist.x < nearestCube.y + jumpDist.y) {
    if (nearestCube.x + jumpDist.x < nearestCube.z + jumpDist.z) {
      chunkPos.x += iinc.x;
      if (chunkPos.x == worldWidth || chunkPos.x == -1) {
        return false;
      }
      nearestCube.x += jumpDist.x + inc.x;
      current.x += jump.x*iinc.x;
      //TODO nearestCube.yz, current.yz
      int temp = int(floor(dir.y*(current.x+16*(chunkPos.x-renderDistance)-pos.x)/dir.x + pos.y)) % 16;
      current.y += temp*iinc.y;
      nearestCube.y += temp*inc.y;
      temp = int(floor(dir.z*(current.x+16*(chunkPos.x-renderDistance)-pos.x)/dir.x + pos.z)) % 16;
      current.z += temp*iinc.z;
      nearestCube.z += temp*inc.z;
      return true;
    }
  }
  return false; //TODO remove
}
//TODO chunkId

int nextCube(inout vec3 nearestCube, inout ivec3 current, inout ivec3 last, inout ivec3 chunkPos,
  inout int chunkId, inout int lastChunkId, vec3 inc, ivec3 iinc, int worldWidth) {
  //TODO improve direction testing
  last = current;
  lastChunkId = chunkId;

  if (nearestCube.x < nearestCube.y) {
    if (nearestCube.x < nearestCube.z) {
      nearestCube.x += inc.x;
      current.x += iinc.x;
      if ((current.x&CHUNK_WIDTH) == CHUNK_WIDTH) {
        current.x &= (CHUNK_WIDTH-1);
        last.x = current.x - iinc.x;
        chunkPos.x += iinc.x;
        if (chunkPos.x == worldWidth || chunkPos.x == -1) {
          discard;
          return -1;
        }
        chunkId = location[chunkPos.z*worldWidth*WORLD_HEIGHT_CHUNKS + chunkPos.x*WORLD_HEIGHT_CHUNKS + chunkPos.y];
      }
    } else {
      nearestCube.z += inc.z;
      current.z += iinc.z;
      if ((current.z&CHUNK_WIDTH) == CHUNK_WIDTH) {
        current.z &= (CHUNK_WIDTH-1);
        last.z = current.z - iinc.z;
        chunkPos.z += iinc.z;
        if (chunkPos.z == worldWidth || chunkPos.z == -1) {
          discard;
          return -1;
        }
        chunkId = location[chunkPos.z*worldWidth*WORLD_HEIGHT_CHUNKS + chunkPos.x*WORLD_HEIGHT_CHUNKS + chunkPos.y];
      }
    }
  } else {
    if (nearestCube.y < nearestCube.z) {
      nearestCube.y += inc.y;
      current.y += iinc.y;
      if ((current.y&CHUNK_WIDTH) == CHUNK_WIDTH) {
        current.y &= (CHUNK_WIDTH-1);
        last.y = current.y - iinc.y;
        chunkPos.y += iinc.y;
        if (chunkPos.y == WORLD_HEIGHT_CHUNKS || chunkPos.y == -1) {
          discard;
          return -1;
        }
        chunkId = location[chunkPos.z*worldWidth*WORLD_HEIGHT_CHUNKS + chunkPos.x*WORLD_HEIGHT_CHUNKS + chunkPos.y];
      }
    } else {
      nearestCube.z += inc.z;
      current.z += iinc.z;
      if ((current.z&CHUNK_WIDTH) == CHUNK_WIDTH) {
        current.z &= (CHUNK_WIDTH-1);
        last.z = current.z - iinc.z;
        chunkPos.z += iinc.z;
        if (chunkPos.z == worldWidth || chunkPos.z == -1) {
          discard;
          return -1;
        }
        chunkId = location[chunkPos.z*worldWidth*WORLD_HEIGHT_CHUNKS + chunkPos.x*WORLD_HEIGHT_CHUNKS + chunkPos.y];
      }
    }
  }

  //TODO skip empty chunks
  //if (chunkId == 0) {
    //bool success = skipChunk(current, nearestCube, chunkPos, dir, pos, inc, iinc, worldWidth);
    //if (!success) {
      //return;
    //}
  //}

  if (chunkId != 0) {
    return blockData[(chunkId-1)*CHUNK_SIZE + current.y*CHUNK_WIDTH*CHUNK_WIDTH + current.z*CHUNK_WIDTH + current.x].id;
  } else {
    return 0;
  }
}

//TODO make this redundant
bool setupDrawBlock(int blockId, int blockRotation, ivec3 current, ivec3 last,
  vec3 nearestCube, vec3 inc, ivec3 iinc, int lastChunkId) {
  int side;
  float texX;
  float texY;
  int light = 15;
  if (lastChunkId != 0) {
    light = blockData[(lastChunkId-1)*CHUNK_SIZE +
      ((last.y%CHUNK_WIDTH)*CHUNK_WIDTH*CHUNK_WIDTH) +
      ((last.z%CHUNK_WIDTH)*CHUNK_WIDTH) +
      (last.x%CHUNK_WIDTH)].lightLevel;
  }

  if (current.x != last.x) {
    float plane = nearestCube.x - inc.x;
    float distanceY = (nearestCube.y - plane)/inc.y;
    float distanceZ = (nearestCube.z - plane)/inc.z;
    texY = 1-(iinc.y+1)/2 + distanceY*iinc.y;
    texX = (iinc.z+1)/2 - distanceZ*iinc.z;
    if (current.x > last.x) {
      side = 1;
    } else {
      texX = 1-texX;
      side = 0;
    }
  } else if (current.y != last.y) {
    float plane = nearestCube.y - inc.y;
    float distanceX = (nearestCube.x - plane)/inc.x;
    float distanceZ = (nearestCube.z - plane)/inc.z;
    texY = 1-(iinc.x+1)/2 + distanceX*iinc.x;
    texX = (iinc.z+1)/2 - distanceZ*iinc.z;
    if (current.y > last.y) {
      side = 3;
    } else {
      texX = 1-texX;
      side = 2;
    }
  } else { //if (current.z != last.z)
    float plane = nearestCube.z - inc.z;
    float distanceY = (nearestCube.y - plane)/inc.y;
    float distanceX = (nearestCube.x - plane)/inc.x;
    texY = 1-(iinc.y+1)/2 + distanceY*iinc.y;
    texX = (iinc.x+1)/2 - distanceX*iinc.x;
    if (current.z > last.z) {
      side = 5;
      texX = 1-texX;
    } else {
      side = 4;
    }
  }

  bool isVoxel = bool(idData[blockId*6*3 + side*3]);
  if (isVoxel) {
    int voxId = idData[blockId*6*3 + side*3 + 1];
    int rotate = idData[blockId*6*3 + side*3 + 2];
    return setupTraceBlock(voxId, nearestCube, inc, iinc, current, last, rotation[blockRotation]*rotation[rotate]);
  } else {
    return drawTexture(blockId, blockRotation, side, texX, texY, /*light*/ 15);
  }
}

void trace(inout ivec3 current, vec3 nearestCube, vec3 dir, vec3 pos, ivec3 chunkPos, vec3 inc, ivec3 iinc, ivec3 offset) {
  ivec3 last = current;
  int worldWidth = renderDistance*2+1;
  int chunkId = location[chunkPos.z*worldWidth*WORLD_HEIGHT_CHUNKS + chunkPos.x*WORLD_HEIGHT_CHUNKS + chunkPos.y];
  //TODO if player is above the world
  int lastChunkId = chunkId; //used for light levels
  int blockId = 0;
  int blockRotation = 0;
  if (chunkId != 0) {
    blockId = blockData[(chunkId-1)*CHUNK_SIZE + current.y*CHUNK_WIDTH*CHUNK_WIDTH + current.z*CHUNK_WIDTH + current.x].id;
    blockRotation = blockData[(chunkId-1)*CHUNK_SIZE + current.y*CHUNK_WIDTH*CHUNK_WIDTH + current.z*CHUNK_WIDTH + current.x].rotation;
  }
  bool blockFound = false;
  if (blockId != 0) {
    blockFound = setupTraceFirstBlock(); //TODO
  }
  while (!blockFound) {
    blockId = 0;
    while (blockId == 0) {
      blockId = nextCube(nearestCube, current, last, chunkPos, chunkId, lastChunkId, inc, iinc, worldWidth);
      blockRotation = blockData[(chunkId-1)*CHUNK_SIZE + current.y*CHUNK_WIDTH*CHUNK_WIDTH + current.z*CHUNK_WIDTH + current.x].rotation;
    }
    blockFound = setupDrawBlock(blockId, blockRotation, current, last, nearestCube, inc, iinc, lastChunkId);
  }
}

void main(void) {
  vec3 dir;
  if (spherical) {
    dir = getRaySphere(2*M_PI, M_PI, stereoscopic3d);
  } else {
    dir = getRayFlat(fovx, fovy, stereoscopic3d);
  }
  vec3 point = vec3(cameraPos);
  ivec3 chunkPos = ivec3(renderDistance, chunkHeight, renderDistance);

  ivec3 offset = ivec3(0, 0, 0);
  //3D
  if (stereoscopic3d) {
    if (spherical) {
      if (texcoord.x < 0.5) {
        point.x += (eyeWidth/2)*dir.z/sqrt(dir.x*dir.x + dir.z*dir.z);
        point.z -= (eyeWidth/2)*dir.x/sqrt(dir.x*dir.x + dir.z*dir.z);
      } else {
        point.x -= (eyeWidth/2)*dir.z/sqrt(dir.x*dir.x + dir.z*dir.z);
        point.z += (eyeWidth/2)*dir.x/sqrt(dir.x*dir.x + dir.z*dir.z);
      }
    } else {// !spherical
      if (texcoord.x < 0.5) {
        point.x -= (eyeWidth/2)*cos(cameraDir.y);
        point.z -= (eyeWidth/2)*sin(cameraDir.y);
      } else {
        point.x += (eyeWidth/2)*cos(cameraDir.y);
        point.z += (eyeWidth/2)*sin(cameraDir.y);
      }
    }
    //Readjust chunkPos for 3d offset
    ivec3 cameraFloor = ivec3(floor(cameraPos/CHUNK_WIDTH));
    ivec3 pointFloor = ivec3(floor(point/CHUNK_WIDTH));
    if (cameraFloor.x > pointFloor.x) {
      chunkPos.x--;
      offset.x = 1;
    } else if (cameraFloor.x < pointFloor.x) {
      chunkPos.x++;
      offset.x = -1;
    }
    if (cameraFloor.z > pointFloor.z) {
      chunkPos.z--;
      offset.z = 1;
    } else if (cameraFloor.z < pointFloor.z) {
      chunkPos.z++;
      offset.z = -1;
    }
  }

  ivec3 current = ivec3(floor(point))%CHUNK_WIDTH;

  //number of steps to the next cube
  vec3 nearestCube;
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

    nearestCube = abs((nextDir - point)/dir);
  }
  //number of steps across a cube
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

  trace(current, nearestCube, dir, mod(point, CHUNK_WIDTH), chunkPos, inc, iinc, offset);
}
