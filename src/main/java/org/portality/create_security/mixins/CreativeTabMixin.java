package org.portality.create_security.mixins;

import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import org.portality.create_security.Create_security;
import org.portality.create_security.Index.Index;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;

import java.util.*;
import java.util.function.Consumer;

@Mixin(CreativeModeTab.class)
public class CreativeTabMixin {
    @Shadow
    private Collection<ItemStack> displayItems;

    @Shadow private Set<ItemStack> displayItemsSearchTab;

    @WrapMethod(method = "buildContents")
    private void createSprings$buildContents(final CreativeModeTab.ItemDisplayParameters parameters, final Operation<Void> original) {
        final CreativeModeTab self = (CreativeModeTab) (Object) this;
        if(self == Create_security.MAIN_TAB.get()) {
            final List<ItemStack> displayItems = new LinkedList<>();
            final Set<ItemStack> searchItems = new LinkedHashSet<>();
            create_Springs_1_21_1$processItems(displayItems::add, searchItems::add);
            this.displayItems = displayItems;
            this.displayItemsSearchTab = searchItems;
            return;
        }
        original.call(parameters);
    }

    @Unique
    private static void create_Springs_1_21_1$processItems(final Consumer<ItemStack> displayItems, final Consumer<ItemStack> searchItems) {
        List<ItemStack> itemsToAdd = new ArrayList<>();

        itemsToAdd = create_Springs_1_21_1$getOrdering();

        int count = 0;
        for (ItemStack stack : itemsToAdd) {
            ItemStack finalStack = create_Springs_1_21_1$applyTransform(stack, displayItems, searchItems);

            displayItems.accept(finalStack);
            searchItems.accept(finalStack);
            count++;
        }

        int padding = 9 - (count % 9);
        if (padding < 9) {
            for (int i = 0; i < padding; i++) {
                displayItems.accept(ItemStack.EMPTY);
            }
        }
    }

    @Unique
    private static List<ItemStack> create_Springs_1_21_1$getOrdering(){
        ArrayList<ItemStack> output = new ArrayList<>();

        output.add(Index.INSCRIBER.asStack());
        output.add(Index.GATE.asStack());
        output.add(Index.READER.asStack());

        output.add(Index.BLANK_CARD.asStack());
        output.add(Index.BLANK_TICKET.asStack());
        output.add(Index.MAGNIFYING_GLASS.asStack());

        return output;
    }

    @Unique
    private static ItemStack create_Springs_1_21_1$applyTransform(final ItemStack item, Consumer<ItemStack> displayItems, Consumer<ItemStack> searchItems) {
        return item;
    }
}
