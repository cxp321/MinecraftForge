package net.minecraftforge.debug;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;

import javax.annotation.Nullable;
import java.util.Random;

@Mod(modid = ItemLayerModelDebug.MODID, name = "ForgeDebugItemLayerModel", version = ItemLayerModelDebug.VERSION, acceptableRemoteVersions = "*")
public class ItemLayerModelDebug
{
    public static final String MODID = "forgedebugitemlayermodel";
    public static final String VERSION = "1.0";
    @ObjectHolder("test_item")
    public static final Item TEST_ITEM = null;

    @Mod.EventBusSubscriber(modid = MODID)
    public static class Registration
    {
        @SubscribeEvent
        public static void registrItems(RegistryEvent.Register<Item> event)
        {
            event.getRegistry().register(new TestItem());
        }

        @SubscribeEvent
        public static void registerModels(ModelRegistryEvent event)
        {
            ModelLoader.setCustomModelResourceLocation(TEST_ITEM, 0, new ModelResourceLocation(MODID.toLowerCase() + ":" + TestItem.name, "inventory"));
        }
    }

    public static final class TestItem extends Item
    {
        public static final String name = "test_item";

        private TestItem()
        {
            setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
            setUnlocalizedName(MODID + ":" + name);
            setRegistryName(new ResourceLocation(MODID, name));
        }

        @Override
        public void onUpdate(ItemStack stack, World worldIn, Entity entityIn, int itemSlot, boolean isSelected)
        {
            NBTTagCompound tag = new NBTTagCompound();
            tag.setInteger("foo", new Random().nextInt());
            stack.setTagCompound(tag);
            stack.setStackDisplayName(String.valueOf(new Random().nextInt()));
        }

        @Override
        public boolean shouldCauseBlockBreakReset(ItemStack oldStack, ItemStack newStack)
        {
            return shouldCauseReequipAnimation(oldStack, newStack, false);
        }

        @Override
        public boolean shouldCauseReequipAnimation(ItemStack oldStack, ItemStack newStack, boolean slotChanged)
        {
            oldStack = oldStack.copy();
            oldStack.setTagCompound(null);
            newStack = newStack.copy();
            newStack.setTagCompound(null);
            return !ItemStack.areItemStacksEqual(oldStack, newStack);
        }

        @Override
        public int getHarvestLevel(ItemStack stack, String toolClass, @Nullable EntityPlayer player, @Nullable IBlockState blockState)
        {
            // This tool is a super pickaxe if the player is wearing a helment
            if ("pickaxe".equals(toolClass) && player != null && !player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).isEmpty())
            {
                return 5;
            }
            return super.getHarvestLevel(stack, toolClass, player, blockState);
        }

        @Override
        public float getStrVsBlock(ItemStack stack, IBlockState state)
        {
            return 10f;
        }
    }
}
