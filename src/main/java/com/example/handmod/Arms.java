package com.example.handmod;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;

/** Состояние рук игрока хранится в командных тегах сущности (сохраняются в NBT игрока). */
public final class Arms {
    private static final String TAG_RIGHT = "handmod_no_right_arm";
    private static final String TAG_LEFT = "handmod_no_left_arm";

    private Arms() {}

    private static String tag(Arm arm) {
        return arm == Arm.RIGHT ? TAG_RIGHT : TAG_LEFT;
    }

    public static boolean isMissing(PlayerEntity player, Arm arm) {
        return player.getCommandTags().contains(tag(arm));
    }

    public static void setMissing(PlayerEntity player, Arm arm, boolean missing) {
        if (missing) {
            player.addCommandTag(tag(arm));
        } else {
            player.removeCommandTag(tag(arm));
        }
    }

    /** Основная рука игрока (из настроек) соответствует MAIN_HAND, вторая — OFF_HAND. */
    public static Arm armForHand(PlayerEntity player, Hand hand) {
        Arm main = player.getMainArm();
        return hand == Hand.MAIN_HAND ? main : main.getOpposite();
    }

    public static boolean handUnusable(PlayerEntity player, Hand hand) {
        return isMissing(player, armForHand(player, hand));
    }
}
