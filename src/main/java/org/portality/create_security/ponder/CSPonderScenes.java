package org.portality.create_security.ponder;

import com.simibubi.create.content.redstone.analogLever.AnalogLeverBlockEntity;
import com.simibubi.create.foundation.ponder.CreateSceneBuilder;
import net.createmod.catnip.math.Pointing;
import net.createmod.ponder.api.scene.SceneBuilder;
import net.createmod.ponder.api.scene.SceneBuildingUtil;
import net.createmod.ponder.api.scene.Selection;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Blocks;
import org.portality.create_security.Index.Index;
import org.portality.create_security.blocks.gate.GateBE;
import org.portality.create_security.blocks.inscriber.InscriberBE;
import org.portality.create_security.blocks.reader.ReaderBE;

public class CSPonderScenes {
    public static void gate(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("gate", "Using Ticket gates");
        scene.configureBasePlate(0, 0, 3);
        scene.world().showSection(util.select().layer(0), Direction.UP);
        scene.showBasePlate();

        Selection gateSelection = util.select().position(1, 1, 1);
        BlockPos gate = new BlockPos(1, 1, 1);

        scene.idle(10);
        scene.world().showSection(gateSelection, Direction.DOWN);
        scene.rotateCameraY(180);
        scene.idle(20);

        scene.overlay().showText(70)
                .placeNearTarget()
                .text("Configure ticket gate by using it with encoded card of ticket")
                .attachKeyFrame()
                .pointAt(util.vector().of(1.5, 1.5, 1.5));
        scene.idle(90);

        ItemStack dirt = new ItemStack(Index.CARD.asItem());
        scene.overlay()
                .showControls(util.vector().of(1.5, 1.5, 1.5), Pointing.DOWN, 40).withItem(dirt).rightClick();
        scene.idle(40);

        scene.overlay().showText(90)
                .placeNearTarget()
                .text("after configuration, ticket gate can be only opened by card or ticket with the same code")
                .attachKeyFrame()
                .pointAt(util.vector().of(1.5, 1.5, 1.5));
        scene.idle(110);

        scene.overlay()
                .showControls(util.vector().of(1.5, 1.5, 1.5), Pointing.LEFT, 40).withItem(dirt).rightClick();
        scene.idle(40);

        scene.world().modifyBlockEntityNBT(gateSelection, GateBE.class, n -> {
            n.putBoolean("isGateOpen", true);
            n.putInt("openTicks", 40);
        });
        scene.idle(40);
        scene.world().modifyBlockEntityNBT(gateSelection, GateBE.class, n -> n.putBoolean("isGateOpen", false));

        scene.overlay().showText(90)
                .placeNearTarget()
                .text("ticket gate is also able to scan cards or tickets on ground nearby")
                .attachKeyFrame()
                .pointAt(util.vector().of(1.5, 1.5, 1.5));
        scene.idle(110);

        scene.markAsFinished();
    }

    public static void reader(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("card_reader", "Using Card Reader");
        scene.configureBasePlate(0, 0, 3);
        scene.world().showSection(util.select().layer(0), Direction.UP);
        scene.showBasePlate();

        Selection gateSelection = util.select().position(1, 1, 1);
        BlockPos gate = new BlockPos(1, 1, 1);

        scene.idle(10);
        scene.world().toggleRedstonePower(gateSelection);
        scene.world().showSection(util.select().layer(1), Direction.DOWN);
        scene.idle(10);
        scene.rotateCameraY(180);
        scene.idle(20);

        scene.overlay().showText(70)
                .placeNearTarget()
                .text("Configure card reader by using it with encoded card of ticket")
                .attachKeyFrame()
                .pointAt(util.vector().of(1.5, 1.5, 1.5));
        scene.idle(90);

        ItemStack dirt = new ItemStack(Index.CARD.asItem());
        scene.overlay()
                .showControls(util.vector().of(1.5, 1.5, 1.5), Pointing.DOWN, 40).withItem(dirt).rightClick();
        scene.idle(40);

        scene.overlay().showText(90)
                .placeNearTarget()
                .text("after configuration, card reader can be only activated by card or ticket with the same code")
                .attachKeyFrame()
                .pointAt(util.vector().of(1.5, 1.5, 1.5));
        scene.idle(110);

        scene.overlay()
                .showControls(util.vector().of(1.5, 1.5, 1.5), Pointing.LEFT, 40).withItem(dirt).rightClick();
        scene.idle(40);
        scene.world().toggleRedstonePower(util.select().layer(1));
        scene.idle(30);
        scene.world().toggleRedstonePower(util.select().layer(1));

        scene.overlay().showText(90)
                .placeNearTarget()
                .text("card reader is also able to scan cards or tickets on ground nearby")
                .attachKeyFrame()
                .pointAt(util.vector().of(1.5, 1.5, 1.5));
        scene.idle(110);

        scene.markAsFinished();
    }

    public static void inscriber(SceneBuilder builder, SceneBuildingUtil util) {
        CreateSceneBuilder scene = new CreateSceneBuilder(builder);
        scene.title("inscriber", "Using Card Reader");
        scene.configureBasePlate(0, 0, 3);
        scene.world().showSection(util.select().layer(0), Direction.UP);
        scene.showBasePlate();

        BlockPos inscriber = new BlockPos(1, 1, 1);
        scene.idle(10);
        scene.world().showSection(util.select().position(inscriber), Direction.DOWN);
        scene.idle(10);

        scene.overlay().showText(60)
                .placeNearTarget()
                .text("Supply Inscriber with rotational force")
                .attachKeyFrame()
                .pointAt(util.vector().of(1.5, 1.5, 1.5));
        scene.idle(80);

        scene.world().showSection(util.select().position(1, 1, 3), Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(util.select().position(1, 1, 2), Direction.DOWN);
        scene.idle(10);
        scene.world().showSection(util.select().position(0, 1, 1), Direction.DOWN);
        scene.idle(10);
        scene.world().modifyKineticSpeed(util.select().everywhere(), f -> 16f);
        scene.world().modifyKineticSpeed(util.select().position(2, 0, 3), f -> -8f);
        scene.idle(10);

        scene.world().modifyBlockEntityNBT(util.select().position(inscriber), InscriberBE.class, n -> {
            n.putBoolean("open", true);
        });

        scene.idle(10);

        scene.overlay().showText(60)
                .placeNearTarget()
                .text("add empty Card or Ticket and Ink Sac")
                .attachKeyFrame()
                .pointAt(util.vector().of(1.5, 1.5, 1.5));
        scene.idle(80);

        scene.world().showSection(util.select().position(2, 1, 1), Direction.DOWN);
        scene.idle(10);

        ItemStack dirt = new ItemStack(Index.CARD.asItem());
        scene.overlay()
                .showControls(util.vector().of(2.5f, 2, 1.5), Pointing.DOWN, 20).withItem(dirt);
        scene.idle(20);
        dirt = new ItemStack(Items.INK_SAC);
        scene.idle(5);
        scene.overlay()
                .showControls(util.vector().of(2.5f, 2, 1.5), Pointing.DOWN, 20).withItem(dirt);
        scene.idle(20);

        scene.world().modifyBlockEntityNBT(util.select().position(inscriber), InscriberBE.class, n -> {
            n.putBoolean("open", false);
        });

        scene.overlay().showText(50)
                .placeNearTarget()
                .text("Configure code in the UI")
                .attachKeyFrame()
                .pointAt(util.vector().of(1.5, 1.5, 1.5));
        scene.idle(60);

        scene.overlay()
                .showControls(util.vector().of(1.5f, 2, 1.5), Pointing.DOWN, 20).rightClick();
        scene.idle(20);

        scene.world().showSection(util.select().position(1, 1, 0), Direction.DOWN);
        scene.idle(10);

        scene.overlay().showText(70)
                .placeNearTarget()
                .text("by applying redstone signal inscriber can be stopped")
                .attachKeyFrame()
                .pointAt(util.vector().of(1.5, 1.5, 0.5));
        scene.idle(80);

        scene.markAsFinished();
    }
}
