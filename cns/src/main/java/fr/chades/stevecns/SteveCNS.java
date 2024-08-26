package fr.chades.stevecns;

import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.ClientPlayerNetworkEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.EntityJoinLevelEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(SteveCNS.MODID)
public class SteveCNS
{
    private static SteveCNS instance = null;
    // Define mod id in a common place for everything to reference
    public static final String MODID = "stevecns";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    private static SteveAPI steveAPI;

    private static boolean shouldPitchMoveHead = false;
    private static float targetPitch = 90.0F;
    private static float pitchIncrement = 2.0F;
    private static boolean shouldYawMoveHead = false;
    private static float targetYaw = 90.0F;
    private static float yawIncrement = 4.0F;

    static {
        try {
            steveAPI = new SteveAPI(6749);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static SteveCNS getInstance() {
        if (instance == null) {
            instance = new SteveCNS();
        }
        return instance;
    }

    public SteveCNS()
    {
        instance = this;
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register ourselves for server and other game events we are interested in
        MinecraftForge.EVENT_BUS.register(this);

        // Register our mod's ForgeConfigSpec so that Forge can create and load the config file for us
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {}

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE, value = Dist.CLIENT)
    public static class ClientModEvents
    {

        @SubscribeEvent
        public static void onClientPlayerLogin(ClientPlayerNetworkEvent.LoggingIn event) {
            // This event is triggered when a player joins the server
            if (Minecraft.getInstance().player != null) {
                LocalPlayer player = Minecraft.getInstance().player;
                player.connection.sendChat("Hello I'm SteveBrain! I'm initializing myself ...");
                SteveCNS.startPitchHeadRotation(90f);
                try {
                    SteveBrainSynapse.getInstance().setup();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (URISyntaxException e) {
                    Minecraft.getInstance().player.connection.sendChat("Something went wrong while setting up SteveBrain! Shutting down");
                    throw new RuntimeException(e);
                }
                SteveCNS.startPitchHeadRotation(0f);
                Minecraft.getInstance().player.connection.sendChat("Ready ! Talk to me :)");
            }
        }

        @SubscribeEvent
        public static void onClientTick(TickEvent.ClientTickEvent event) {
            Minecraft minecraft = Minecraft.getInstance();
            LocalPlayer player = minecraft.player;

            if (player != null) {
                if (shouldPitchMoveHead) {
                    System.out.println("Moving head");
                    if (player.getXRot() == targetPitch) {
                        shouldPitchMoveHead = false;
                    } else {

                        if (player.getXRot() < targetPitch) {
                            player.setXRot(Math.min(player.getXRot() + pitchIncrement, targetPitch));
                        } else if (player.getXRot() > targetPitch) {
                            player.setXRot(Math.max(player.getXRot() - pitchIncrement, targetPitch));
                        }

                        player.xRotO = player.getXRot();
                    }
                }

                if (shouldYawMoveHead) {
                    if (player.getYRot() == targetYaw) {
                        shouldYawMoveHead = false;
                    } else {
                        if (player.getYRot() < targetYaw) {
                            player.setYRot(Math.min(player.getYRot() + yawIncrement, targetYaw));
                        } else if (player.getYRot() > targetYaw) {
                            player.setYRot(Math.max(player.getYRot() - yawIncrement, targetYaw));
                        }

                        player.yRotO = player.getYRot();
                    }
                }
            }
        }
    }

    public static void startPitchHeadRotation(float pitch) {
        targetPitch = pitch;
        shouldPitchMoveHead = true;
    }

    public static void startYawHeadRotation(float yaw) {
        targetYaw = yaw;
        shouldYawMoveHead = true;
    }

}
