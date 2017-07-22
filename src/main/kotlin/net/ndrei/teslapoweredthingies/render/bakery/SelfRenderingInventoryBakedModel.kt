package net.ndrei.teslapoweredthingies.render.bakery

import net.minecraft.client.renderer.block.model.IBakedModel
import net.minecraft.client.renderer.block.model.ItemCameraTransforms
import net.minecraft.client.renderer.block.model.ItemOverrideList
import net.minecraft.client.renderer.vertex.VertexFormat
import net.minecraft.entity.EntityLivingBase
import net.minecraft.item.ItemStack
import net.minecraft.world.World
import net.minecraftforge.common.model.TRSRTransformation
import org.apache.commons.lang3.tuple.Pair
import java.util.*
import javax.vecmath.Matrix4f
import javax.vecmath.Vector3f

class SelfRenderingInventoryBakedModel(renderer: ISelfRenderingBlock, format: VertexFormat, val currentStack: ItemStack = ItemStack.EMPTY) : SelfRenderingBakedModel(renderer, format) {
    override val itemStack: ItemStack?
        get() = this.currentStack

    private val transforms: MutableMap<ItemCameraTransforms.TransformType, TRSRTransformation?> = EnumMap(ItemCameraTransforms.TransformType::class.java)
    private val flipX = TRSRTransformation(null, null, Vector3f(-1f, 1f, 1f), null)

    init {
        setTransform(ItemCameraTransforms.TransformType.GUI, this.getTransform(-15f, -3.5f, 0f, 30f, 225f, 0f, 0.625f))
        setTransform(ItemCameraTransforms.TransformType.GROUND, this.getTransform(-6f, -4f, -6f, 0f, 0f, 0f, 0.25f))
        setTransform(ItemCameraTransforms.TransformType.FIXED, this.getTransform(0f, 0f, 0f, 0f, 0f, 0f, 0.5f))
        addThirdPersonTransform(this.getTransform(-4f, -4f, -6f, 75f, 45f, 0f, 0.375f))
        addFirstPersonTransform(this.getTransform(-6f, -4f, -6f, 0f, 45f, 0f, 0.4f))
    }

    override fun isAmbientOcclusion() = true
    override fun isGui3d() = true

    override fun getOverrides(): ItemOverrideList {
        return object: ItemOverrideList(mutableListOf()) {
            override fun handleItemState(originalModel: IBakedModel?, stack: ItemStack?, world: World?, entity: EntityLivingBase?): IBakedModel {
                if ((originalModel is SelfRenderingInventoryBakedModel) && (stack != null) && !stack.isEmpty) {
                    return SelfRenderingInventoryBakedModel(originalModel.renderer, originalModel.format, stack)
                }
                return super.handleItemState(originalModel, stack, world, entity)
            }
        }
    }

    override fun handlePerspective(type: ItemCameraTransforms.TransformType): Pair<out IBakedModel, Matrix4f> {
        return Pair.of<IBakedModel, Matrix4f>(this, (transforms[type] ?: this.getTransform(0f, 0f, 0f, 0f, 0f, 0f, 1f)).matrix)
    }

    fun setTransform(type: ItemCameraTransforms.TransformType, transform: TRSRTransformation) {
        transforms[type] = transform
    }

    fun addThirdPersonTransform(transform: TRSRTransformation) {
        setTransform(ItemCameraTransforms.TransformType.THIRD_PERSON_RIGHT_HAND, transform)
        setTransform(ItemCameraTransforms.TransformType.THIRD_PERSON_LEFT_HAND, this.toLeftHand(transform))
    }

    fun addFirstPersonTransform(transform: TRSRTransformation) {
        setTransform(ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND, transform)
        setTransform(ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND, this.toLeftHand(transform))
    }

    fun toLeftHand(transform: TRSRTransformation): TRSRTransformation {
        return TRSRTransformation.blockCenterToCorner(this.flipX.compose(TRSRTransformation.blockCornerToCenter(transform)).compose(this.flipX))
    }

    fun getTransform(tx: Float, ty: Float, tz: Float, ax: Float, ay: Float, az: Float, s: Float): TRSRTransformation {
        return TRSRTransformation.blockCenterToCorner(TRSRTransformation(
                Vector3f(tx / 16, ty / 16, tz / 16),
                TRSRTransformation.quatFromXYZDegrees(Vector3f(ax, ay, az)),
                Vector3f(s, s, s),
                null))
    }
}