package org.portality.create_security.Index;

import dev.engine_room.flywheel.lib.model.baked.PartialModel;
import org.portality.create_security.Create_security;

public class CSPartalModels {
    public static final PartialModel
            CAP = block("card_inscriber_top"),
            GATE = block("ticket_gate_part"),
            CARD = block("card_inscriber_card"),
            TICKET = block("card_inscriber_ticket");

    private static PartialModel block(String path) {
        return PartialModel.of(Create_security.asResource("block/" + path));
    }

    private static PartialModel item(String path) {
        return PartialModel.of(Create_security.asResource("item/" + path));
    }

    public static void register(){

    }
}
