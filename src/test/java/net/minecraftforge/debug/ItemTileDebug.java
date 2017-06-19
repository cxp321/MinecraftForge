package net.minecraftforge.debug;

import net.minecraft.block.Block;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.potion.Potion;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.debug.ObjectHolderTest.CustomRegistryEntry;
import net.minecraftforge.debug.ObjectHolderTest.ICustomRegistryEntry;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.common.registry.GameRegistry.ObjectHolder;
import net.minecraftforge.registries.RegistryBuilder;

import static org.lwjgl.opengl.GL11.*;

@Mod(modid = ItemTileDebug.MODID, name = "ForgeDebugItemTile", version = "1.0", acceptableRemoteVersions = "*")
public class ItemTileDebug
{
    public static final String MODID = "forgedebugitemtile";
    @ObjectHolder(TestBlock.name)
    public static final Block TEST_BLOCK = null;

    @SidedProxy
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event)
    {
        proxy.preInit(event);
    }

    @EventHandler
    public void init(FMLInitializationEvent event)
    {
        proxy.init(event);
    }

    @Mod.EventBusSubscriber(modid = MODID)
    public static class Registration
    {
        @SubscribeEvent
        public static void registerBlocks(RegistryEvent.Register<Block> event)
        {
            event.getRegistry().register(new TestBlock());
        }

        @SubscribeEvent
        public static void registerItems(RegistryEvent.Register<Item> event)
        {
            event.getRegistry().register(new ItemBlock(TEST_BLOCK).setRegistryName(TEST_BLOCK.getRegistryName()));
        }
    }

    public static class CommonProxy
    {
        public void preInit(FMLPreInitializationEvent event)
        {
            GameRegistry.registerTileEntity(CustomTileEntity.class, MODID.toLowerCase() + ":custom_tile_entity");
        }
        public void init(FMLInitializationEvent event){}
    }

    public static class ServerProxy extends CommonProxy{}

    public static class ClientProxy extends CommonProxy
    {
        private static ModelResourceLocation itemLocation = new ModelResourceLocation(TEST_BLOCK.getRegistryName(), "normal");

        @SuppressWarnings("deprecation")
        @Override
        public void preInit(FMLPreInitializationEvent event)
        {
            super.preInit(event);
            Item item = ForgeRegistries.ITEMS.getValue(new ResourceLocation(MODID, TestBlock.name));
            ForgeHooksClient.registerTESRItemStack(item, 0, CustomTileEntity.class);
            ModelLoader.setCustomModelResourceLocation(item, 0, itemLocation);
            MinecraftForge.EVENT_BUS.register(BakeEventHandler.instance);
        }

        @Override
        public void init(FMLInitializationEvent event)
        {
            ClientRegistry.bindTileEntitySpecialRenderer(CustomTileEntity.class, TestTESR.instance);
        }
    }

    public static class BakeEventHandler
    {
        public static final BakeEventHandler instance = new BakeEventHandler();

        private BakeEventHandler()
        {
        }

        @SubscribeEvent
        public void onModelBakeEvent(ModelBakeEvent event)
        {
            event.getModelManager().getBlockModelShapes().registerBuiltInBlocks(TEST_BLOCK);
        }
    }

    public static class TestTESR extends TileEntitySpecialRenderer<CustomTileEntity>
    {
        private static final TestTESR instance = new TestTESR();

        private TestTESR()
        {
        }

        @Override
        public void func_192841_a(CustomTileEntity p_180535_1_, double x, double y, double z, float p_180535_8_, int p_180535_9_, float partial)
        {
            glPushMatrix();
            glTranslated(x, y, z);
            GlStateManager.disableTexture2D();
            GlStateManager.disableLighting();
            glColor4f(.2f, 1, .1f, 1);
            glBegin(GL_QUADS);
            glVertex3f(0, .5f, 0);
            glVertex3f(0, .5f, 1);
            glVertex3f(1, .5f, 1);
            glVertex3f(1, .5f, 0);
            glEnd();
            glPopMatrix();
            GlStateManager.enableTexture2D();
            GlStateManager.enableLighting();
        }
    }

    public static class TestBlock extends BlockContainer
    {
        public static final String name = "custom_model_block";

        private TestBlock()
        {
            super(Material.IRON);
            setCreativeTab(CreativeTabs.BUILDING_BLOCKS);
            setUnlocalizedName(MODID + ":" + name);
            setRegistryName(new ResourceLocation(MODID, name));
        }

        @Override
        public boolean isOpaqueCube(IBlockState state)
        {
            return false;
        }

        @Override
        public boolean isFullCube(IBlockState state)
        {
            return false;
        }

        @Override
        public boolean causesSuffocation(IBlockState state)
        {
            return false;
        }

        @Override
        public TileEntity createNewTileEntity(World world, int meta)
        {
            return new CustomTileEntity();
        }
    }

    public static class CustomTileEntity extends TileEntity
    {
    }
}
