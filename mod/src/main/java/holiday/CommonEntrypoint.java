package holiday;

import com.mojang.serialization.Codec;
import holiday.baritone.BaritoneInit;
import holiday.block.HolidayServerBlocks;
import holiday.block.entity.HolidayServerBlockEntityTypes;
import holiday.component.HolidayServerDataComponentTypes;
import holiday.entity.HolidayServerEntities;
import holiday.entity.effect.HolidayServerEffects;
import holiday.event.InhibitEvent;
import holiday.item.HolidayServerItems;
import holiday.loot.HolidayServerLootContextTypes;
import holiday.screen.HolidayServerScreenHandlers;
import holiday.screen.StorageTerminalScreenHandler;
import holiday.sound.HolidayServerSoundEvents;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry;
import net.fabricmc.fabric.api.attachment.v1.AttachmentType;
import net.fabricmc.fabric.api.gamerule.v1.GameRuleBuilder;
import net.fabricmc.fabric.api.loot.v3.LootTableEvents;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationConnectionEvents;
import net.fabricmc.fabric.api.networking.v1.ServerConfigurationNetworking;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.block.BlockState;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.attribute.EntityAttribute;
import net.minecraft.loot.condition.KilledByPlayerLootCondition;
import net.minecraft.network.DisconnectionInfo;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.codec.PacketCodecs;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.packet.Packet;
import net.minecraft.registry.Registries;
import net.minecraft.resource.featuretoggle.FeatureFlags;
import net.minecraft.resource.featuretoggle.FeatureSet;
import net.minecraft.server.network.ServerConfigurationNetworkHandler;
import net.minecraft.server.network.ServerPlayerConfigurationTask;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.rule.GameRule;
import net.minecraft.world.rule.GameRuleCategory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.util.Optional;
import java.util.function.Consumer;

@SuppressWarnings("UnstableApiUsage")
public class CommonEntrypoint implements ModInitializer {
    public static final String MOD_ID = "holiday-server-mod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    public static final String CURRENT_VERSION = FabricLoader.getInstance()
        .getModContainer(MOD_ID)
        .get()
        .getMetadata()
        .getVersion()
        .getFriendlyString();

    private static final AttachmentType<Boolean> ANIMALS_REGENERATED_CHUNK_TYPE = AttachmentRegistry.create(
        identifier("animals_regenerated"),
        builder -> builder.initializer(() -> Boolean.FALSE).persistent(Codec.BOOL)
    );

    private static final Identifier EPORTAL_GAMERULE_ID = identifier("endportal_enabled");
    public static final GameRule<Boolean> EPORTAL_GAMERULE = GameRuleBuilder.forBoolean(false).category(GameRuleCategory.PLAYER).buildAndRegister(EPORTAL_GAMERULE_ID);

    public static final FeatureSet FORCE_ENABLED_FEATURES = FeatureSet.of(FeatureFlags.MINECART_IMPROVEMENTS);

    @Override
    public void onInitialize() {
        HolidayServerBlocks.register();
        HolidayServerBlockEntityTypes.register();
        HolidayServerDataComponentTypes.register();
        HolidayServerItems.register();
        HolidayServerLootContextTypes.register();
        HolidayServerScreenHandlers.register();
        HolidayServerSoundEvents.register();
        HolidayServerEffects.register();
        HolidayServerEntities.register();
        BaritoneInit.onInitialize();

        PayloadTypeRegistry.configurationS2C().register(RequestVersionPayload.ID, RequestVersionPayload.PACKET_CODEC);
        PayloadTypeRegistry.configurationC2S().register(VersionResponsePayload.ID, VersionResponsePayload.PACKET_CODEC);

        PayloadTypeRegistry.playC2S().register(StorageTerminalSearchPayload.ID, StorageTerminalSearchPayload.PACKET_CODEC);

        ServerConfigurationConnectionEvents.CONFIGURE.register((handler, server) -> {
            if (ServerConfigurationNetworking.canSend(handler, RequestVersionPayload.ID)) {
                handler.addTask(new CheckVersionTask());
            } else {
                disconnect(handler, "unknown");
            }
        });

        ServerConfigurationNetworking.registerGlobalReceiver(VersionResponsePayload.ID, (payload, context) -> {
            if (!CURRENT_VERSION.equals(payload.version())) {
                disconnect(context.networkHandler(), payload.version());
                return;
            }

            context.networkHandler().completeTask(CheckVersionTask.KEY);
        });

        InhibitEvent.EVENT.register((this::getIsInhibited));

        LootTableEvents.MODIFY_DROPS.register((registryEntry, lootContext, stacks) -> {
            if (registryEntry.matchesKey(EntityType.WARDEN.getLootTableKey().orElseThrow()) && KilledByPlayerLootCondition.builder().build().test(lootContext)) {
                var stack = HolidayServerItems.ECHO_DUST.getDefaultStack();
                stack.setCount(3);
                stacks.add(stack);
            }
        });

        Registries.ITEM.addAlias(identifier("ender_paralyzer"), identifier("tele_inhibitor"));
        Registries.BLOCK.addAlias(identifier("ender_paralyzer"), identifier("tele_inhibitor"));
        ServerPlayNetworking.registerGlobalReceiver(StorageTerminalSearchPayload.ID, (payload, context) -> {
            if (context.player().currentScreenHandler instanceof StorageTerminalScreenHandler screenHandler) {
                screenHandler.updateSearch(payload.search(), payload.skip());
            }
        });

        PayloadTypeRegistry.playC2S().register(SelectAttributePayload.ID, SelectAttributePayload.CODEC);
        PayloadTypeRegistry.playS2C().register(SelectAttributePayload.ID, SelectAttributePayload.CODEC);
    }

    private static void disconnect(ServerConfigurationNetworkHandler handler, String currentVersion) {
        MutableText text = Text.literal("You must have the same version of the modpack installed to play on this server.");
        text.append("\n").append(Text.literal("Download the following version: ")).append(Text.literal(CURRENT_VERSION).formatted(Formatting.YELLOW));
        text.append("\n").append(Text.literal("You currently have version: ")).append(Text.literal(currentVersion).formatted(Formatting.RED));
        handler.disconnect(new DisconnectionInfo(
            text,
            Optional.empty(),
            Optional.of(URI.create("https://github.com/modmuss50/holiday-server-pack/commit/%s".formatted(CURRENT_VERSION)))
        ));
    }

    public static Identifier identifier(String path) {
        return Identifier.of(MOD_ID, path);
    }

    public record CheckVersionTask() implements ServerPlayerConfigurationTask {
        public static final Key KEY = new Key(RequestVersionPayload.ID.toString());

        @Override
        public void sendPacket(Consumer<Packet<?>> sender) {
            sender.accept(ServerConfigurationNetworking.createS2CPacket(new RequestVersionPayload()));
        }

        @Override
        public Key getKey() {
            return KEY;
        }
    }

    public record RequestVersionPayload() implements CustomPayload {
        public static final CustomPayload.Id<RequestVersionPayload> ID = new CustomPayload.Id<>(Identifier.of("holiday-server-mod", "request_version"));
        public static final PacketCodec<PacketByteBuf, RequestVersionPayload> PACKET_CODEC = PacketCodec.unit(new RequestVersionPayload());

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    public record VersionResponsePayload(String version) implements CustomPayload {
        public static final CustomPayload.Id<VersionResponsePayload> ID = new CustomPayload.Id<>(Identifier.of("holiday-server-mod", "version_response"));
        public static final PacketCodec<PacketByteBuf, VersionResponsePayload> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.STRING, VersionResponsePayload::version,
            VersionResponsePayload::new
        );

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    public boolean getIsInhibited(LivingEntity entity) {
        Box box = entity.getBoundingBox().expand(8.0D, 8.0D, 8.0D);
        int n = MathHelper.floor(box.minX);
        int o = MathHelper.floor(box.maxX);
        int p = MathHelper.floor(box.minY);
        int q = MathHelper.floor(box.maxY);
        int n1 = MathHelper.floor(box.minZ);
        int o1 = MathHelper.floor(box.maxZ);
        BlockPos.Mutable mutablePos = new BlockPos.Mutable();

        for (int p1 = n; p1 < o; p1++)
            for (int q1 = p; q1 < q; q1++)
                for (int n2 = n1; n2 < o1; n2++) {
                    BlockState state = entity.getEntityWorld().getBlockState(mutablePos.set(p1, q1, n2));
                    if (state.getBlock().equals(HolidayServerBlocks.TELE_INHIBITOR)) { //Set the custom block here
                        return true;
                    }
                }
        return false;
    }

    public record StorageTerminalSearchPayload(String search, int skip) implements CustomPayload {
        public static final CustomPayload.Id<StorageTerminalSearchPayload> ID = new CustomPayload.Id<>(Identifier.of("holiday-server-mod", "storage_terminal_search"));
        public static final PacketCodec<PacketByteBuf, StorageTerminalSearchPayload> PACKET_CODEC = PacketCodec.tuple(
            PacketCodecs.string(StorageTerminalScreenHandler.MAX_SEARCH_LENGTH), StorageTerminalSearchPayload::search,
            PacketCodecs.INTEGER, StorageTerminalSearchPayload::skip,
            StorageTerminalSearchPayload::new
        );

        public StorageTerminalSearchPayload(String search, int skip) {
            this.search = search;
            this.skip = Math.max(0, skip);
        }

        @Override
        public Id<? extends CustomPayload> getId() {
            return ID;
        }
    }

    public record SelectAttributePayload(int syncId, Identifier attributeId) implements CustomPayload {

        public static final PacketCodec<PacketByteBuf, SelectAttributePayload> CODEC =
            CustomPayload.codecOf(
                (payload, buf) -> {
                    buf.writeInt(payload.syncId);
                    buf.writeIdentifier(payload.attributeId);
                },
                buf -> new SelectAttributePayload(buf.readInt(), buf.readIdentifier())
            );

        public static final CustomPayload.Id<SelectAttributePayload> ID =
            new CustomPayload.Id<>(CommonEntrypoint.identifier("attribute_table_modifier"));

        @Override
        public CustomPayload.Id<? extends CustomPayload> getId() {
            return ID;
        }

        public void encode(PacketByteBuf buf) {
            buf.writeInt(syncId);
            buf.writeIdentifier(attributeId);
        }

        public static SelectAttributePayload decode(PacketByteBuf buf) {
            int syncId = buf.readInt();
            Identifier attrId = buf.readIdentifier();
            return new SelectAttributePayload(syncId, attrId);
        }

        public EntityAttribute resolveAttribute() {
            return Registries.ATTRIBUTE.get(attributeId);
        }

    }
}
