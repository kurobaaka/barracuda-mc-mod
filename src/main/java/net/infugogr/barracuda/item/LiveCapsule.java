package net.infugogr.barracuda.item;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


public class LiveCapsule extends Item {
    private Entity entity1;

    public LiveCapsule(Entity entity1, Settings settings) {
        super(settings);
        this.entity1 = entity1;
    }

    @Override
    public ActionResult useOnEntity(ItemStack stack, PlayerEntity user, LivingEntity entity, Hand hand) {
        entity1 = entity.getType().create(user.getWorld());
        return super.useOnEntity(stack, user, entity, hand);
    }

    @Override
    public ActionResult useOnBlock(ItemUsageContext context) {
        if (!context.getWorld().isClient() && entity1 != null) {
            BlockPos pos = context.getBlockPos();
            entity1.setPos(pos.getX(), pos.getY() + 1, pos.getZ());
            context.getWorld().spawnEntity(entity1);
            return super.useOnBlock(context);
        }
        return null;
    }
}
