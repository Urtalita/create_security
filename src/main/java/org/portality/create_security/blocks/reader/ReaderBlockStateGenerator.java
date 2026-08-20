package org.portality.create_security.blocks.reader;

import com.simibubi.create.content.redstone.diodes.AbstractDiodeBlock;
import com.simibubi.create.foundation.data.SpecialBlockStateGen;
import com.tterrag.registrate.providers.DataGenContext;
import com.tterrag.registrate.providers.RegistrateBlockstateProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.model.generators.BlockModelProvider;
import net.neoforged.neoforge.client.model.generators.ModelFile;
import org.portality.create_security.CreateSecurity;

import java.util.ArrayList;
import java.util.List;

public class ReaderBlockStateGenerator extends SpecialBlockStateGen {
    private List<ModelFile> models;

    @Override
    protected final int getXRotation(BlockState state) {
        return 0;
    }

    @Override
    protected final int getYRotation(BlockState state) {
        return horizontalAngle(state.getValue(AbstractDiodeBlock.FACING));
    }

    protected <T extends Block> List<ModelFile> createModels(DataGenContext<Block, T> ctx,
                                                             BlockModelProvider prov) {
        List<ModelFile> models = new ArrayList<>(2);
        String name = ctx.getName();
        ResourceLocation off = existing("card_reader");
        ResourceLocation on = existing("card_reader");

        models.add(prov.withExistingParent(name, off));
        models.add(prov.withExistingParent(name + "_on", on));

        return models;
    }

    protected ResourceLocation existing(String name) {
        return CreateSecurity.asResource("block/" + name);
    }

    protected int getModelIndex(BlockState state) {
        boolean powered = state.getValue(ReaderBlock.POWERED);

        if(powered) return 1;
        return 0;
    }

    @Override
    public final <T extends Block> ModelFile getModel(DataGenContext<Block, T> ctx, RegistrateBlockstateProvider prov,
                                                      BlockState state) {
        if (models == null)
            models = createModels(ctx, prov.models());
        return models.get(getModelIndex(state));
    }
}
