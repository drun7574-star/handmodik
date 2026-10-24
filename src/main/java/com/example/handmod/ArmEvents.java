package com.example.handmod;

import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents;
import net.fabricmc.fabric.api.event.player.AttackBlockCallback;
import net.fabricmc.fabric.api.event.player.AttackEntityCallback;
import net.fabricmc.fabric.api.event.player.PlayerBlockBreakEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.fabricmc.fabric.api.event.player.UseEntityCallback;
import net.fabricmc.fabric.api.event.player.UseItemCallback;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Arm;
import net.minecraft.util.Hand;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public final class ArmEvents {
    private ArmEvents() {}

    /** Нельзя действовать рукой, которой нет. Иголка — исключение (её держат «в зубах»). */
    private static boolean blocked(PlayerEntity player, World world, Hand hand) {
        if (world.isClient) {
            return false; // клиент не знает про теги, решает сервер
        }
        if (player.getStackInHand(hand).isOf(ModItems.NEEDLE)) {
            return false;
        }
        if (Arms.handUnusable(player, hand)) {
            player.sendMessage(Text.translatable("message.handmod.no_arm_here"), true);
            return true;
        }
        return false;
    }

    public static void register() {
        AttackBlockCallback.EVENT.register((player, world, hand, pos, direction) ->
                blocked(player, world, hand) ? ActionResult.FAIL : ActionResult.PASS);

        AttackEntityCallback.EVENT.register((player, world, hand, entity, hit) ->
                blocked(player, world, hand) ? ActionResult.FAIL : ActionResult.PASS);

        UseBlockCallback.EVENT.register((player, world, hand, hit) ->
                blocked(player, world, hand) ? ActionResult.FAIL : ActionResult.PASS);

        UseEntityCallback.EVENT.register((player, world, hand, entity, hit) ->
                blocked(player, world, hand) ? ActionResult.FAIL : ActionResult.PASS);

        UseItemCallback.EVENT.register((player, world, hand) -> {
            ItemStack stack = player.getStackInHand(hand);
            return blocked(player, world, hand)
                    ? TypedActionResult.fail(stack)
                    : TypedActionResult.pass(stack);
        });

        // Страховка: ломать блоки без основной руки нельзя
        PlayerBlockBreakEvents.BEFORE.register((world, player, pos, state, blockEntity) ->
                world.isClient || !Arms.handUnusable(player, Hand.MAIN_HAND));

        // После смерти руки «отрастают»
        ServerPlayerEvents.AFTER_RESPAWN.register((oldPlayer, newPlayer, alive) -> {
            if (!alive) {
                Arms.setMissing(newPlayer, Arm.RIGHT, false);
                Arms.setMissing(newPlayer, Arm.LEFT, false);
            }
        });
    }
}
