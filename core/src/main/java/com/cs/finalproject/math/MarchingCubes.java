package com.cs.finalproject.math;

import com.cs.finalproject.core.blocks.Block;
import com.badlogic.gdx.math.Vector3;

import java.util.ArrayList;
import java.util.List;

public final class MarchingCubes {

    private MarchingCubes() {
    }

    // adjustable
    // values above this count as solid.
    private static final float ISO = 0.5f;

    // pre-generated lookup table
    private static final int[][] TET_TABLE = {
        {},                      // 0000: empty
        {0, 1, 2},               // 0001: v0
        {0, 4, 3},               // 0010: v1
        {1, 2, 4, 1, 4, 3},     // 0011: v0 v1
        {1, 3, 5},               // 0100: v2
        {0, 3, 5, 0, 5, 2},     // 0101: v0 v2
        {5, 1, 0, 4, 5, 0},     // 0110: v1 v2
        {4, 5, 2},               // 0111: v0 v1 v2
        {2, 5, 4},               // 1000: v3
        {0, 1, 5, 0, 5, 4},     // 1001: v0 v3
        {5, 3, 0, 2, 5, 0},     // 1010: v1 v3
        {5, 3, 1},               // 1011: v0 v1 v3
        {4, 2, 1, 3, 4, 1},     // 1100: v2 v3
        {3, 4, 0},               // 1101: v0 v2 v3
        {2, 1, 0},               // 1110: v1 v2 v3
        {}                       // 1111: full
    };

    private static final int[] EDGE_A = {0, 0, 0, 1, 1, 2};
    private static final int[] EDGE_B = {1, 2, 3, 2, 3, 3};

    private static final int[][] TETS = {
        {0, 1, 2, 6}, {0, 5, 1, 6}, {0, 4, 5, 6},
        {0, 7, 4, 6}, {0, 3, 7, 6}, {0, 2, 3, 6}
    };

    private static final int[][] CORNER = {
        {0, 0, 0}, {1, 0, 0}, {1, 1, 0}, {0, 1, 0},
        {0, 0, 1}, {1, 0, 1}, {1, 1, 1}, {0, 1, 1}
    };

    public static float[] iter(ArrayList<Block> blocks, float cellSize) {
        return generate(blocks, cellSize);
    }

    public static float[] generate(List<Block> blocks, float cellSize) {
        Field f = buildField(blocks, cellSize);
        float[][][] d = f.data;
        int sizeX = d.length, sizeY = d[0].length, sizeZ = d[0][0].length;

        ArrayList<float[]> tris = new ArrayList<>();
        float[] cornerPos = new float[24]; // world position of the 8 cube corners
        float[] tetVal = new float[4];
        float[] p0 = new float[3], p1 = new float[3], p2 = new float[3];

        // insane loop!
        for (int x = 0; x < sizeX - 1; x++) {
            for (int y = 0; y < sizeY - 1; y++) {
                for (int z = 0; z < sizeZ - 1; z++) {
                    float[] v = {
                        d[x][y][z], d[x + 1][y][z], d[x + 1][y + 1][z], d[x][y + 1][z],
                        d[x][y][z + 1], d[x + 1][y][z + 1], d[x + 1][y + 1][z + 1], d[x][y + 1][z + 1]
                    };

                    if (v[0] == v[1] && v[1] == v[2] && v[2] == v[3]
                        && v[3] == v[4] && v[4] == v[5] && v[5] == v[6] && v[6] == v[7])
                        continue;

                    for (int c = 0; c < 8; c++) {
                        cornerPos[3 * c] = f.originX + (x + CORNER[c][0]) * f.cellSize;
                        cornerPos[3 * c + 1] = f.originY + (y + CORNER[c][1]) * f.cellSize;
                        cornerPos[3 * c + 2] = f.originZ + (z + CORNER[c][2]) * f.cellSize;
                    }

                    for (int[] tet : TETS) {
                        int caseBits = 0;

                        // i wrote this
                        // so theres no checking if this is valid
                        // so double check later if problems arise
                        for (int k = 0; k < 4; k++) {
                            tetVal[k] = v[tet[k]];
                            if (tetVal[k] > ISO) caseBits |= 1 << k;
                        }

                        if (caseBits == 0 || caseBits == 15) continue;

                        int[] table = TET_TABLE[caseBits];
                        for (int i = 0; i < table.length; i += 3) {
                            edgePoint(cornerPos, tetVal, table[i], p0);
                            edgePoint(cornerPos, tetVal, table[i + 1], p1);
                            edgePoint(cornerPos, tetVal, table[i + 2], p2);
                            tris.add(new float[]{
                                p0[0], p0[1], p0[2], p1[0], p1[1], p1[2], p2[0], p2[1], p2[2]
                            });
                        }
                    }
                }
            }
        }

        float[] mesh = new float[tris.size() * 18];
        int o = 0;

        for (float[] t : tris) {
            float e1x = t[3] - t[0], e1y = t[4] - t[1], e1z = t[5] - t[2];
            float e2x = t[6] - t[0], e2y = t[7] - t[1], e2z = t[8] - t[2];
            float nx = e1y * e2z - e1z * e2y;
            float ny = e1z * e2x - e1x * e2z;
            float nz = e1x * e2y - e1y * e2x;
            float len = (float) Math.sqrt(nx * nx + ny * ny + nz * nz);

            if (len > 1e-12f) {
                nx /= len;
                ny /= len;
                nz /= len;
            }

            for (int k = 0; k < 9; k += 3) {
                mesh[o++] = t[k];
                mesh[o++] = t[k + 1];
                mesh[o++] = t[k + 2];
                mesh[o++] = nx;
                mesh[o++] = ny;
                mesh[o++] = nz;
            }
        }
        return mesh;
    }

    private static void edgePoint(float[] cornerPos, float[] tetVal, int e, float[] out) {
        int a = EDGE_A[e], b = EDGE_B[e];
        float va = tetVal[a], vb = tetVal[b];
        float t = (vb == va) ? 0.5f : (ISO - va) / (vb - va);

        out[0] = cornerPos[3 * a] + t * (cornerPos[3 * b] - cornerPos[3 * a]);
        out[1] = cornerPos[3 * a + 1] + t * (cornerPos[3 * b + 1] - cornerPos[3 * a + 1]);
        out[2] = cornerPos[3 * a + 2] + t * (cornerPos[3 * b + 2] - cornerPos[3 * a + 2]);
    }

    public static Field buildField(List<Block> blocks, float cellSize) {
        float minX = Float.POSITIVE_INFINITY, minY = Float.POSITIVE_INFINITY, minZ = Float.POSITIVE_INFINITY;
        float maxX = Float.NEGATIVE_INFINITY, maxY = Float.NEGATIVE_INFINITY, maxZ = Float.NEGATIVE_INFINITY;

        if (blocks != null) {
            for (Block b : blocks) {
                for (Vector3 p : b.getVertices()) {
                    minX = Math.min(minX, p.x);
                    minY = Math.min(minY, p.y);
                    minZ = Math.min(minZ, p.z);
                    maxX = Math.max(maxX, p.x);
                    maxY = Math.max(maxY, p.y);
                    maxZ = Math.max(maxZ, p.z);
                }
            }
        }

        if (minX > maxX) return new Field(new float[1][1][1], 0, 0, 0, cellSize);

        minX -= cellSize;
        minY -= cellSize;
        minZ -= cellSize;
        maxX += cellSize;
        maxY += cellSize;
        maxZ += cellSize;

        int sx = (int) Math.ceil((maxX - minX) / cellSize) + 1;
        int sy = (int) Math.ceil((maxY - minY) / cellSize) + 1;
        int sz = (int) Math.ceil((maxZ - minZ) / cellSize) + 1;
        float[][][] data = new float[sx][sy][sz];

        for (Block b : blocks) {
            Vector3[] vs = b.getVertices();
            float bx0 = vs[0].x, by0 = vs[0].y, bz0 = vs[0].z, bx1 = bx0, by1 = by0, bz1 = bz0;

            for (Vector3 p : vs) {
                bx0 = Math.min(bx0, p.x);
                by0 = Math.min(by0, p.y);
                bz0 = Math.min(bz0, p.z);
                bx1 = Math.max(bx1, p.x);
                by1 = Math.max(by1, p.y);
                bz1 = Math.max(bz1, p.z);
            }

            int x0 = Math.max(0, (int) Math.floor((bx0 - minX) / cellSize));
            int x1 = Math.min(sx - 1, (int) Math.ceil((bx1 - minX) / cellSize));
            int y0 = Math.max(0, (int) Math.floor((by0 - minY) / cellSize));
            int y1 = Math.min(sy - 1, (int) Math.ceil((by1 - minY) / cellSize));
            int z0 = Math.max(0, (int) Math.floor((bz0 - minZ) / cellSize));
            int z1 = Math.min(sz - 1, (int) Math.ceil((bz1 - minZ) / cellSize));

            for (int x = x0; x <= x1; x++)
                for (int y = y0; y <= y1; y++)
                    for (int z = z0; z <= z1; z++)
                        data[x][y][z] = 1f;
        }

        return new Field(data, minX, minY, minZ, cellSize);
    }

    // probably switch to record later
    public static final class Field {
        public final float[][][] data;
        public final float originX, originY, originZ, cellSize;

        Field(float[][][] data, float ox, float oy, float oz, float cellSize) {
            this.data = data;
            this.originX = ox;
            this.originY = oy;
            this.originZ = oz;
            this.cellSize = cellSize;
        }
    }
}
