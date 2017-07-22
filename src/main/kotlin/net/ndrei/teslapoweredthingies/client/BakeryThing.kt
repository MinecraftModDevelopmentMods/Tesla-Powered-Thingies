package net.ndrei.teslapoweredthingies.client

import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.client.renderer.vertex.VertexFormatElement
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d
import net.minecraftforge.client.model.pipeline.UnpackedBakedQuad
import net.ndrei.teslapoweredthingies.render.bakery.RawCubeSideInfo

/**
 * Created by CF on 2017-07-16.
 */
class RawQuad(
        val p1: Vec3d, val u1: Float, val v1: Float,
        val p2: Vec3d, val u2: Float, val v2: Float,
        val p3: Vec3d, val u3: Float, val v3: Float,
        val p4: Vec3d, val u4: Float, val v4: Float,
        val side: EnumFacing, val sprite: TextureAtlasSprite, val color: Int) {

    fun Float.u() = sprite.getInterpolatedU(this.toDouble()/* / 16.0 * sprite.iconWidth*/)
    fun Float.v() = sprite.getInterpolatedV(this.toDouble()/* / 16.0 * sprite.iconHeight*/)

    fun bake(format: VertexFormat): BakedQuad {
        // val normal = this.p3.subtract(this.p2).crossProduct(this.p1.subtract(this.p2)).normalize()

        val normal = Vec3d(side.frontOffsetX.toDouble(), side.frontOffsetY.toDouble(), side.frontOffsetZ.toDouble())

        val builder = UnpackedBakedQuad.Builder(format)
        builder.setTexture(this.sprite)
        builder.putVertex(this.sprite, normal, this.p1.x / 32.0, this.p1.y / 32.0, this.p1.z / 32.0, this.u1.u(), this.v1.v(), this.color)
        builder.putVertex(this.sprite, normal, this.p2.x / 32.0, this.p2.y / 32.0, this.p2.z / 32.0, this.u2.u(), this.v2.v(), this.color)
        builder.putVertex(this.sprite, normal, this.p3.x / 32.0, this.p3.y / 32.0, this.p3.z / 32.0, this.u3.u(), this.v3.v(), this.color)
        builder.putVertex(this.sprite, normal, this.p4.x / 32.0, this.p4.y / 32.0, this.p4.z / 32.0, this.u4.u(), this.v4.v(), this.color)
        return builder.build()
    }
}

private fun UnpackedBakedQuad.Builder.putVertex(sprite: TextureAtlasSprite, normal: Vec3d, x: Double, y: Double, z: Double, u: Float, v: Float, color: Int) {
    for (e in 0 until this.vertexFormat.elementCount) {
        when (this.vertexFormat.getElement(e).usage) {
            VertexFormatElement.EnumUsage.POSITION -> this.put(e, x.toFloat(), y.toFloat(), z.toFloat())
            VertexFormatElement.EnumUsage.COLOR -> this.put(e,
                    ((color ushr 16) and 0xFF) / 255.0f,
                    ((color ushr 8) and 0xFF) / 255.0f,
                    (color and 0xFF) / 255.0f,
                    ((color ushr 24) and 0xFF) / 255.0f)
            VertexFormatElement.EnumUsage.UV -> this.put(e, u, v, 0f, 1f)
            VertexFormatElement.EnumUsage.NORMAL -> this.put(e, normal.x.toFloat(), normal.y.toFloat(), normal.z.toFloat(), 0f)
            else -> this.put(e)
        }
    }
}

fun TextureAtlasSprite.buildInfo(from: Vec2f, to: Vec2f, bothSides: Boolean = false, color: Int = -1)
    = RawCubeSideInfo(this, from, to, bothSides, color)
fun TextureAtlasSprite.buildInfo(u1: Float, v1: Float, u2: Float, v2: Float, bothSides: Boolean = false, color: Int = -1)
        = RawCubeSideInfo(this, Vec2f(u1, v1), Vec2f(u2, v2), bothSides, color)

fun MutableList<RawQuad>.addQuad(side: EnumFacing, sprite: TextureAtlasSprite, color: Int,
                                 x1: Double, y1: Double, z1: Double, u1: Float, v1: Float,
                                 x2: Double, y2: Double, z2: Double, u2: Float, v2: Float,
                                 x3: Double, y3: Double, z3: Double, u3: Float, v3: Float,
                                 x4: Double, y4: Double, z4: Double, u4: Float, v4: Float) {
    this.add(RawQuad(
            Vec3d(x1, y1, z1), u1, v1,
            Vec3d(x2, y2, z2), u2, v2,
            Vec3d(x3, y3, z3), u3, v3,
            Vec3d(x4, y4, z4), u4, v4,
            side, sprite, color))
}

fun MutableList<RawQuad>.addDoubleQuad(side: EnumFacing, sprite: TextureAtlasSprite, color: Int,
                                 x1: Double, y1: Double, z1: Double, u1: Float, v1: Float,
                                 x2: Double, y2: Double, z2: Double, u2: Float, v2: Float,
                                 x3: Double, y3: Double, z3: Double, u3: Float, v3: Float,
                                 x4: Double, y4: Double, z4: Double, u4: Float, v4: Float) {
    this.addQuad(side, sprite, color,
            x1, y1, z1, u1, v1,
            x2, y2, z2, u2, v2,
            x3, y3, z3, u3, v3,
            x4, y4, z4, u4, v4)
    this.addQuad(side, sprite, color,
            x4, y4, z4, u4, v4,
            x3, y3, z3, u3, v3,
            x2, y2, z2, u2, v2,
            x1, y1, z1, u1, v1)
}

fun MutableList<RawQuad>.addFullQuad(side: EnumFacing, sprite: TextureAtlasSprite, color: Int,
                                     x1: Double, y1: Double, z1: Double,
                                     x2: Double, y2: Double, z2: Double,
                                     x3: Double, y3: Double, z3: Double,
                                     x4: Double, y4: Double, z4: Double) {
    this.addQuad(side, sprite, color,
            x1, y1, z1, 0.0f, 0.0f,
            x2, y2, z2, 16.0f, 0.0f,
            x3, y3, z3, 16.0f, 16.0f,
            x4, y4, z4, 0.0f, 16.0f)
}

fun MutableList<RawQuad>.addFullDoubleQuad(side: EnumFacing, sprite: TextureAtlasSprite, color: Int,
                                           x1: Double, y1: Double, z1: Double,
                                           x2: Double, y2: Double, z2: Double,
                                           x3: Double, y3: Double, z3: Double,
                                           x4: Double, y4: Double, z4: Double) {
    this.addDoubleQuad(side, sprite, color,
            x1, y1, z1, 0.0f, 0.0f,
            x2, y2, z2, 16.0f, 0.0f,
            x3, y3, z3, 16.0f, 16.0f,
            x4, y4, z4, 0.0f, 16.0f)
}

fun MutableList<RawQuad>.addDoubleFace(facing: EnumFacing, sprite: TextureAtlasSprite, color: Int) {
    this.addDoubleFace(facing, sprite, color, Vec3d(0.0, 0.0, 0.0), Vec3d(1.0, 1.0, 1.0), Vec2f(0.0f, 0.0f), Vec2f(16.0f, 16.0f))
}

fun MutableList<RawQuad>.addDoubleFace(facing: EnumFacing, sprite: TextureAtlasSprite, color: Int, p1: Vec3d, p2: Vec3d, t1: Vec2f, t2: Vec2f) {
    addFace(facing, sprite, color, p1, p2, t1, t2) {
        p1, t1,
        p2, t2,
        p3, t3,
        p4, t4,
        f, s, a ->
        this.addDoubleQuad(f, s, a,
                p1.x, p1.y, p1.z, t1.x, t1.y,
                p2.x, p2.y, p2.z, t2.x, t2.y,
                p3.x, p3.y, p3.z, t3.x, t3.y,
                p4.x, p4.y, p4.z, t4.x, t4.y)
    }
}

fun MutableList<RawQuad>.addSingleFace(facing: EnumFacing, sprite: TextureAtlasSprite, color: Int, p1: Vec3d, p2: Vec3d, t1: Vec2f, t2: Vec2f) {
    addFace(facing, sprite, color, p1, p2, t1, t2) {
        p1, t1,
        p2, t2,
        p3, t3,
        p4, t4,
        f, s, a ->
        this.addQuad(f, s, a,
                p1.x, p1.y, p1.z, t1.x, t1.y,
                p2.x, p2.y, p2.z, t2.x, t2.y,
                p3.x, p3.y, p3.z, t3.x, t3.y,
                p4.x, p4.y, p4.z, t4.x, t4.y)
    }
}

fun addFace(facing: EnumFacing, sprite: TextureAtlasSprite, color: Int, p1: Vec3d, p2: Vec3d, t1: Vec2f, t2: Vec2f,
                                 thingy: (p1: Vec3d, t1: Vec2f,
                                          p2: Vec3d, t2: Vec2f,
                                          p3: Vec3d, t3: Vec2f,
                                          p4: Vec3d, t4: Vec2f,
                                          facing: EnumFacing, sprite: TextureAtlasSprite, color: Int) -> Unit) {
    when (facing) {
        EnumFacing.DOWN -> thingy(
                Vec3d(p1.x, p1.y, p1.z), Vec2f(t1.x, t1.y),
                Vec3d(p2.x, p1.y, p1.z), Vec2f(t2.x, t1.y),
                Vec3d(p2.x, p1.y, p2.z), Vec2f(t2.x, t2.y),
                Vec3d(p1.x, p1.y, p2.z), Vec2f(t1.x, t2.y),
                facing, sprite, color)
        EnumFacing.UP -> thingy(
                Vec3d(p2.x, p2.y, p1.z), Vec2f(t2.x, t1.y),
                Vec3d(p1.x, p2.y, p1.z), Vec2f(t1.x, t1.y),
                Vec3d(p1.x, p2.y, p2.z), Vec2f(t1.x, t2.y),
                Vec3d(p2.x, p2.y, p2.z), Vec2f(t2.x, t2.y),
                facing, sprite, color)
        EnumFacing.NORTH -> thingy(
                Vec3d(p1.x, p2.y, p1.z), Vec2f(t1.x, t2.y),
                Vec3d(p2.x, p2.y, p1.z), Vec2f(t2.x, t2.y),
                Vec3d(p2.x, p1.y, p1.z), Vec2f(t2.x, t1.y),
                Vec3d(p1.x, p1.y, p1.z), Vec2f(t1.x, t1.y),
                facing, sprite, color)
        EnumFacing.SOUTH -> thingy(
                Vec3d(p2.x, p2.y, p2.z), Vec2f(t2.x, t2.y),
                Vec3d(p1.x, p2.y, p2.z), Vec2f(t1.x, t2.y),
                Vec3d(p1.x, p1.y, p2.z), Vec2f(t1.x, t1.y),
                Vec3d(p2.x, p1.y, p2.z), Vec2f(t2.x, t1.y),
                facing, sprite, color)
        EnumFacing.EAST -> thingy(
                Vec3d(p2.x, p2.y, p1.z), Vec2f(t1.x, t2.y),
                Vec3d(p2.x, p2.y, p2.z), Vec2f(t2.x, t2.y),
                Vec3d(p2.x, p1.y, p2.z), Vec2f(t2.x, t1.y),
                Vec3d(p2.x, p1.y, p1.z), Vec2f(t1.x, t1.y),
                facing, sprite, color)
        EnumFacing.WEST -> thingy(
                Vec3d(p1.x, p2.y, p2.z), Vec2f(t2.x, t2.y),
                Vec3d(p1.x, p2.y, p1.z), Vec2f(t1.x, t2.y),
                Vec3d(p1.x, p1.y, p1.z), Vec2f(t1.x, t1.y),
                Vec3d(p1.x, p1.y, p2.z), Vec2f(t2.x, t1.y),
                facing, sprite, color)
    }
}