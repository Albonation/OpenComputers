package li.cil.oc.client.renderer.block

import li.cil.oc.client.Textures
import li.cil.oc.common.block
import li.cil.oc.common.tileentity
import li.cil.oc.util.ExtendedAABB._
import net.minecraft.block.state.IBlockState
import net.minecraft.client.renderer.block.model.BakedQuad
import net.minecraft.item.ItemStack
import net.minecraftforge.client.model.ISmartItemModel
import net.minecraftforge.common.property.IExtendedBlockState

import scala.collection.convert.WrapAsJava.bufferAsJavaList
import scala.collection.mutable

object PrintModel extends SmartBlockModelBase with ISmartItemModel {
  override def handleBlockState(state: IBlockState) = state match {
    case extended: IExtendedBlockState => new BlockModel(extended)
    case _ => missingModel
  }

  override def handleItemState(stack: ItemStack) = new ItemModel(stack)

  class BlockModel(val state: IExtendedBlockState) extends SmartBlockModelBase {
    override def getGeneralQuads =
      state.getValue(block.property.PropertyTile.Tile) match {
        case print: tileentity.Print =>
          val faces = mutable.ArrayBuffer.empty[BakedQuad]

          for (shape <- if (print.state) print.data.stateOn else print.data.stateOff) {
            val bounds = shape.bounds.rotateTowards(print.facing)
            var texture = Textures.getSprite(shape.texture)
            if (texture.getIconName == "missingno") {
              texture = Textures.getSprite("minecraft:blocks/" + shape.texture)
            }
            faces ++= bakeQuads(makeBox(bounds.min, bounds.max), Array.fill(6)(texture), None)
          }

          bufferAsJavaList(faces)
        case _ => super.getGeneralQuads
      }
  }

  class ItemModel(val stack: ItemStack) extends SmartBlockModelBase {
    override protected def textureScale = 32f

    override def getGeneralQuads = {
      val faces = mutable.ArrayBuffer.empty[BakedQuad]

      //      Textures.bind(Textures.Model.DroneItem)

      bufferAsJavaList(faces)
    }
  }

}