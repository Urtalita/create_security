package org.portality.create_security.Index;

import com.mojang.serialization.Codec;
import com.simibubi.create.AllBlocks;
import com.simibubi.create.AllItems;
import com.simibubi.create.AllTags;
import com.simibubi.create.foundation.data.BlockStateGen;
import com.simibubi.create.foundation.data.SharedProperties;
import com.tterrag.registrate.providers.RegistrateRecipeProvider;
import com.tterrag.registrate.util.entry.BlockEntityEntry;
import com.tterrag.registrate.util.entry.BlockEntry;
import com.tterrag.registrate.util.entry.ItemEntry;
import net.minecraft.core.UUIDUtil;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.SingleItemRecipeBuilder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.tags.BlockTags;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.*;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import org.jetbrains.annotations.ApiStatus;
import org.portality.create_security.Create_security;
import org.portality.create_security.blocks.gate.GateBE;
import org.portality.create_security.blocks.gate.GateBlock;
import org.portality.create_security.blocks.gate.GateRenderer;
import org.portality.create_security.blocks.inscriber.InscriberBE;
import org.portality.create_security.blocks.inscriber.InscriberBlock;
import org.portality.create_security.blocks.inscriber.InscriberRenderer;
import org.portality.create_security.blocks.reader.ReaderBE;
import org.portality.create_security.blocks.reader.ReaderBlock;
import org.portality.create_security.blocks.reader.ReaderBlockStateGenerator;
import org.portality.create_security.items.BlankCardItem;
import org.portality.create_security.items.CardItem;

import java.util.UUID;
import java.util.function.UnaryOperator;

import static com.simibubi.create.foundation.data.TagGen.axeOrPickaxe;
import static com.simibubi.create.foundation.data.TagGen.pickaxeOnly;
import static net.minecraft.world.level.block.Blocks.IRON_BARS;

public class Index {

    static {
        Create_security.CS_REGISTRATE.setCreativeTab(Create_security.MAIN_TAB);
    }

    private static final DeferredRegister.DataComponents DATA_COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, Create_security.MODID);

    public static final ItemEntry<Item> MAGNIFYING_GLASS = Create_security.CS_REGISTRATE
            .item("magnifying_glass", Item::new)
            .properties(p -> p.stacksTo(1))
            .recipe((c, b) ->
                    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, c.get(), 1)
                            .pattern(" a ")
                            .pattern("aba")
                            .pattern("ca ")
                            .define('a', AllItems.COPPER_SHEET)
                            .define('b', Items.AMETHYST_SHARD)
                            .define('c', Items.STICK)
                            .unlockedBy("has_ingredient",
                                    RegistrateRecipeProvider.has(Items.AMETHYST_SHARD))
                            .save(b)
            )
            .register();

    public static final ItemEntry<BlankCardItem> BLANK_CARD = Create_security.CS_REGISTRATE
            .item("blank_card", BlankCardItem::new)
            .properties(p -> p.stacksTo(16))
            .recipe((c, b) ->
                    SingleItemRecipeBuilder.stonecutting(
                            Ingredient.of(AllItems.COPPER_SHEET.get()), RecipeCategory.MISC, c.get())
                            .unlockedBy("has_ingredient",
                                    RegistrateRecipeProvider.has(AllItems.COPPER_SHEET))
                            .save(b))
            .register();

    public static final ItemEntry<CardItem> CARD = Create_security.CS_REGISTRATE
            .item("card", CardItem::new)
            .properties(p -> p.rarity(Rarity.UNCOMMON).stacksTo(1))
            .register();

    public static final ItemEntry<BlankCardItem> BLANK_TICKET = Create_security.CS_REGISTRATE
            .item("blank_ticket", BlankCardItem::new)
            .properties(p -> p.stacksTo(16))
            .recipe((c, b) ->
                    SingleItemRecipeBuilder.stonecutting(
                                    Ingredient.of(Items.PAPER), RecipeCategory.MISC, c.get(), 4)
                            .unlockedBy("has_ingredient",
                                    RegistrateRecipeProvider.has(Items.PAPER))
                            .save(b))
            .register();

    public static final ItemEntry<CardItem> TICKET = Create_security.CS_REGISTRATE
            .item("ticket", CardItem::new)
            .properties(p -> p.rarity(Rarity.UNCOMMON).stacksTo(1))
            .register();

    public static final BlockEntry<InscriberBlock> INSCRIBER = Create_security.registrate()
            .block("card_inscriber", InscriberBlock::new)
            .initialProperties(AllBlocks.ANDESITE_CASING::get)
            .initialProperties(SharedProperties::wooden)
            .properties(BlockBehaviour.Properties::noOcclusion)
            .transform(axeOrPickaxe())
            .blockstate(BlockStateGen.horizontalBlockProvider(true))
            .item((block, properties) -> new BlockItem(block, properties))
            .recipe((c, b) ->
                    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, c.get(), 1)
                            .pattern("zzz")
                            .pattern("csh")
                            .define('z', AllItems.ZINC_NUGGET)
                            .define('c', AllBlocks.ANDESITE_CASING)
                            .define('h', AllBlocks.SHAFT)
                            .define('s', AllBlocks.SCHEMATIC_TABLE)
                            .unlockedBy("has_ingredient",
                                    RegistrateRecipeProvider.has(AllBlocks.ANDESITE_CASING))
                            .save(b)
            )
            .build()
            .register();

    public static final BlockEntityEntry<InscriberBE> INSCRIBER_BE = Create_security.registrate()
            .blockEntity("card_inscriber", InscriberBE::new)
            .renderer(() -> InscriberRenderer::new)
            .validBlocks(INSCRIBER)
            .register();

    public static final BlockEntry<GateBlock> GATE = Create_security.registrate()
            .block("ticket_gate", GateBlock::new)
            .initialProperties(AllBlocks.ANDESITE_CASING::get)
            .initialProperties(SharedProperties::wooden)
            .properties(BlockBehaviour.Properties::noOcclusion)
            .transform(axeOrPickaxe())
            .blockstate(BlockStateGen.horizontalBlockProvider(true))
            .item((block, properties) -> new BlockItem(block, properties))
            .recipe((c, b) ->
                    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, c.get(), 1)
                            .pattern("aac")
                            .define('a', AllBlocks.SHAFT)
                            .define('c', AllBlocks.BRASS_CASING)
                            .unlockedBy("has_ingredient",
                                    RegistrateRecipeProvider.has(AllBlocks.BRASS_CASING))
                            .save(b)
            )
            .build()
            .register();

    public static final BlockEntityEntry<GateBE> GATE_BE = Create_security.registrate()
            .blockEntity("gate", GateBE::new)
            .renderer(() -> GateRenderer::new)
            .validBlocks(GATE)
            .register();

    public static final BlockEntry<ReaderBlock> READER = Create_security.registrate()
            .block("card_reader", ReaderBlock::new)
            .initialProperties(AllBlocks.ANDESITE_CASING::get)
            .initialProperties(SharedProperties::wooden)
            .properties(BlockBehaviour.Properties::noOcclusion)
            .transform(axeOrPickaxe())
            .blockstate(new ReaderBlockStateGenerator()::generate)
            .item((block, properties) -> new BlockItem(block, properties))
            .recipe((c, b) ->
                    ShapedRecipeBuilder.shaped(RecipeCategory.MISC, c.get(), 1)
                            .pattern("ntn")
                            .pattern("svs")
                            .define('n', Items.IRON_NUGGET)
                            .define('v', AllBlocks.ITEM_VAULT)
                            .define('t', AllItems.ELECTRON_TUBE)
                            .define('s', AllItems.IRON_SHEET)
                            .unlockedBy("has_ingredient",
                                    RegistrateRecipeProvider.has(AllBlocks.ITEM_VAULT))
                            .save(b)
            )
            .build()
            .register();

    public static final BlockEntityEntry<ReaderBE> READER_BE = Create_security.registrate()
            .blockEntity("card_reader", ReaderBE::new)
            .validBlocks(READER)
            .register();

    public static void register(){

    }

    public static final DataComponentType<CompoundTag> ENCRYPTED_STACKS = register(
            "encrypted_stacks",
            builder -> builder.persistent(CompoundTag.CODEC).networkSynchronized(ByteBufCodecs.COMPOUND_TAG)
    );

    public static final DataComponentType<Integer> CARD_TIER = register(
            "card_tier",
            builder -> builder.persistent(Codec.INT).networkSynchronized(ByteBufCodecs.INT)
    );

    public static final DataComponentType<UUID> PLAYER_ID = register(
            "player_id",
            builder -> builder.persistent(UUIDUtil.CODEC).networkSynchronized(UUIDUtil.STREAM_CODEC)
    );

    private static <T> DataComponentType<T> register(String name, UnaryOperator<DataComponentType.Builder<T>> builder) {
        DataComponentType<T> type = builder.apply(DataComponentType.builder()).build();
        DATA_COMPONENTS.register(name, () -> type);
        return type;
    }

    @ApiStatus.Internal
    public static void registerAllComponents(IEventBus modEventBus) {
        DATA_COMPONENTS.register(modEventBus);
    }
}
