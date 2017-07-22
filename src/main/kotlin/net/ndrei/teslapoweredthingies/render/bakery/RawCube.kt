package net.ndrei.teslapoweredthingies.render.bakery

import net.minecraft.block.state.IBlockState
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.item.ItemStack
import net.minecraft.util.EnumFacing
import net.minecraft.util.math.Vec2f
import net.minecraft.util.math.Vec3d
import net.ndrei.teslapoweredthingies.client.RawQuad
import net.ndrei.teslapoweredthingies.client.addDoubleFace
import net.ndrei.teslapoweredthingies.client.addSingleFace

class RawCube(val p1: Vec3d, val p2: Vec3d, val sprite: TextureAtlasSprite? = null)
    : IBakery {
    override fun getQuads(state: IBlockState?, stack: ItemStack?, side: EnumFacing?, vertexFormat: VertexFormat)
            = mutableListOf<BakedQuad>().also { this.bake(it, vertexFormat) }

    private val map = mutableMapOf<EnumFacing, RawCubeSideInfo>()
    private var lastSide: EnumFacing? = null
    private var autoUVFlag = false
    private var dualSideFlag = false

    fun addFace(face: EnumFacing) = this.also {
        this.map[face] = RawCubeSideInfo()
        this.lastSide = face

        this.getLastSideInfo().also {
            if (this.autoUVFlag) {
                it.autoUV(this)
            }
            it.bothSides = this.dualSideFlag
        }
    }

    private fun getLastSideInfo()
            = (if (this.lastSide != null) this.map[this.lastSide!!] else null)
            ?: throw Exception("No side created yet!")

    fun sprite(sprite: TextureAtlasSprite) = this.also {
        this.getLastSideInfo().sprite = sprite
    }

    fun autoUV(flag: Boolean = true) = this.also {
        if (this.lastSide != null) {
            this.getLastSideInfo().autoUV(if (flag) this else null)
        } else {
            this.autoUVFlag = flag
        }
    }

    fun uv(u1: Float, v1: Float, u2: Float, v2: Float) = this.uv(Vec2f(u1, v1), Vec2f(u2, v2))

    fun uv(t1: Vec2f, t2: Vec2f) = this.also {
        this.getLastSideInfo().also {
            it.from = t1
            it.to = t2
        }
    }

    fun color(color: Int) = this.also {
        this.getLastSideInfo().color = color
    }

    fun dualSide(flag: Boolean = true) = this.also {
        if (this.lastSide != null) {
            this.getLastSideInfo().bothSides = flag
        } else {
            this.dualSideFlag = flag
        }
    }

//    fun setFace(face: EnumFacing, u1: Float, v1: Float, u2: Float, v2: Float, bothSides: Boolean = false, sprite: TextureAtlasSprite? = null, color: Int = -1): RawCube {
//        this.map[face] = (sprite ?: this.sprite ?: throw Exception("Missing texture sprite for face $face."))
//                .buildInfo(u1, v1, u2, v2, bothSides, color)
//        return this
//    }

    fun bake(quads: MutableList<BakedQuad>, format: VertexFormat) {
        val rawrs = mutableListOf<RawQuad>()

        // order coords TODO: maybe not needed?
        val (x1, x2) = if (this.p1.x < this.p2.x) Pair(this.p1.x, this.p2.x) else Pair(this.p2.x, this.p1.x)
        val (y1, y2) = if (this.p1.y < this.p2.y) Pair(this.p1.y, this.p2.y) else Pair(this.p2.y, this.p1.y)
        val (z1, z2) = if (this.p1.z < this.p2.z) Pair(this.p1.z, this.p2.z) else Pair(this.p2.z, this.p1.z)

        this.map.forEach { face, info ->
            val sprite = info.sprite ?: this.sprite ?: Minecraft.getMinecraft().textureMapBlocks.missingSprite
            if (info.bothSides) {
                rawrs.addDoubleFace(face, sprite, info.color, Vec3d(x1, y1, z1), Vec3d(x2, y2, z2), info.from, info.to)
            }
            else {
                rawrs.addSingleFace(face, sprite, info.color, Vec3d(x1, y1, z1), Vec3d(x2, y2, z2), info.from, info.to)
            }
        }

        rawrs.mapTo(quads) { it.bake(format) }
    }
}
