package com.kratos.arsenal.gun;

import com.kratos.arsenal.network.ModNetworking;
import com.kratos.arsenal.registry.ModSounds;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.projectile.ProjectileUtil;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.particle.ItemStackParticleEffect;
import net.minecraft.particle.ParticleTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvent;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Box;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.RaycastContext;
import net.minecraft.world.World;

import java.util.Random;

/**
 * Motor de disparo: balística por hitscan, dispersión, falloff de daño,
 * headshots, partículas (fogonazo, casquillos, impacto), sonido y retroceso.
 *
 * <p>Toda esta lógica se ejecuta en el servidor (autoridad) y propaga los
 * efectos a los clientes a través del mundo del servidor y de paquetes.</p>
 */
public final class GunEngine {

    private GunEngine() {}

    /**
     * Ejecuta un disparo completo del arma indicada.
     *
     * @param player jugador que dispara (en el servidor).
     * @param stack  pila del arma.
     * @param type   tipo de arma con sus estadísticas.
     */
    public static void fire(ServerPlayerEntity player, ItemStack stack, GunType type) {
        ServerWorld world = (ServerWorld) player.getWorld();
        Random random = new Random();

        boolean aiming = GunData.isAiming(stack);
        double spread = computeSpread(stack, type, player, aiming);
        Vec3d eye = player.getEyePos();

        // Lanzacohetes: proyectil explosivo simplificado (hitscan + explosión).
        if (type.isExplosive()) {
            fireExplosive(player, world, type, eye, random);
        } else {
            // Cada "pellet" es un rayo independiente (las escopetas disparan varios).
            for (int i = 0; i < Math.max(1, type.getPellets()); i++) {
                fireSingleRay(player, world, type, eye, spread, random);
            }
        }

        // --- Efectos comunes ---
        spawnMuzzleEffects(world, player, eye, GunData.isSilenced(stack));
        spawnShellCasing(world, player, eye);
        playShootSound(world, player, type, GunData.isSilenced(stack), random);

        // Retroceso enviado al cliente que dispara.
        float recoil = type.getRecoil() * recoilModifier(stack) * (aiming ? 0.6f : 1.0f);
        ModNetworking.sendRecoil(player, recoil, (random.nextFloat() - 0.5f) * recoil * 0.4f);
    }

    /** Calcula la dispersión efectiva teniendo en cuenta apuntado y accesorios. */
    private static double computeSpread(ItemStack stack, GunType type, ServerPlayerEntity player, boolean aiming) {
        double spread = type.getBaseSpread();

        // Apuntar reduce mucho la dispersión.
        if (aiming) {
            spread *= 0.35;
        }
        // Moverse o saltar la aumenta (balística básica de postura).
        if (!player.isOnGround()) {
            spread *= 1.8;
        } else if (player.getVelocity().horizontalLengthSquared() > 0.01) {
            spread *= 1.35;
        }
        if (player.isSneaking()) {
            spread *= 0.8;
        }

        // Modificadores de accesorios (mira, láser, etc.).
        AttachmentType sight = GunData.getAttachment(stack, AttachmentType.Slot.SIGHT);
        AttachmentType laser = GunData.getAttachment(stack, AttachmentType.Slot.LASER);
        if (sight != null) {
            spread *= (1.0 + sight.getSpreadModifier());
        }
        if (laser != null) {
            spread *= (1.0 + laser.getSpreadModifier());
        }
        return Math.max(0.0, spread);
    }

    private static float recoilModifier(ItemStack stack) {
        AttachmentType grip = GunData.getAttachment(stack, AttachmentType.Slot.GRIP);
        return grip != null ? grip.getRecoilModifier() : 1.0f;
    }

    /** Lanza un único rayo, resuelve el impacto y aplica el daño. */
    private static void fireSingleRay(ServerPlayerEntity player, ServerWorld world, GunType type,
                                      Vec3d eye, double spread, Random random) {
        // Dirección con dispersión gaussiana.
        Vec3d dir = player.getRotationVec(1.0f)
                .add(random.nextGaussian() * spread, random.nextGaussian() * spread, random.nextGaussian() * spread)
                .normalize();
        Vec3d end = eye.add(dir.multiply(type.getRange()));

        // Impacto contra bloques.
        BlockHitResult blockHit = world.raycast(new RaycastContext(
                eye, end, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, player));
        double blockDist = blockHit.getType() != HitResult.Type.MISS ? eye.distanceTo(blockHit.getPos()) : type.getRange();

        // Impacto contra entidades (sólo hasta donde llega el bloque).
        Vec3d entityEnd = eye.add(dir.multiply(blockDist));
        Box searchBox = player.getBoundingBox().stretch(dir.multiply(blockDist)).expand(1.0);
        EntityHitResult entityHit = ProjectileUtil.raycast(player, eye, entityEnd, searchBox,
                e -> !e.isSpectator() && e.canHit() && e != player, blockDist * blockDist);

        if (entityHit != null && entityHit.getEntity() instanceof LivingEntity target) {
            double hitDist = eye.distanceTo(entityHit.getPos());
            applyDamage(player, world, type, target, entityHit.getPos(), hitDist);
        } else if (blockHit.getType() != HitResult.Type.MISS) {
            // Partícula de impacto en el bloque.
            Vec3d p = blockHit.getPos();
            world.spawnParticles(ParticleTypes.SMOKE, p.x, p.y, p.z, 4, 0.0, 0.0, 0.0, 0.01);
            world.spawnParticles(ParticleTypes.CRIT, p.x, p.y, p.z, 6, 0.05, 0.05, 0.05, 0.05);
        }
    }

    /** Aplica daño con falloff por distancia y bonus por headshot. */
    private static void applyDamage(ServerPlayerEntity player, ServerWorld world, GunType type,
                                    LivingEntity target, Vec3d hitPos, double distance) {
        float damage = type.getDamage();

        // Falloff lineal de daño con la distancia.
        if (type.hasFalloff() && distance > type.getFalloffStart()) {
            double t = (distance - type.getFalloffStart()) / (type.getRange() - type.getFalloffStart());
            t = MathHelper.clamp(t, 0.0, 1.0);
            damage *= (float) (1.0 - 0.6 * t);
        }

        // Detección de headshot: el impacto está a la altura de la cabeza.
        boolean headshot = false;
        double relY = hitPos.y - target.getY();
        if (relY >= target.getEyeHeight(target.getPose()) - 0.18) {
            damage *= type.getHeadshotMultiplier();
            headshot = true;
        }

        target.damage(world.getDamageSources().playerAttack(player), damage);

        // Algunas armas (plasma) incendian al objetivo.
        if (type.getIgniteTicks() > 0) {
            target.setOnFireFor(type.getIgniteTicks() / 20);
        }

        // Partículas de impacto sobre la entidad.
        world.spawnParticles(ParticleTypes.DAMAGE_INDICATOR, hitPos.x, hitPos.y, hitPos.z, 4, 0.1, 0.1, 0.1, 0.0);
        if (headshot) {
            world.spawnParticles(ParticleTypes.CRIT, hitPos.x, hitPos.y, hitPos.z, 10, 0.1, 0.1, 0.1, 0.2);
            world.playSound(null, player.getX(), player.getY(), player.getZ(),
                    ModSounds.HEADSHOT, SoundCategory.PLAYERS, 1.0f, 1.0f);
        }
    }

    /** Disparo explosivo del lanzacohetes (hitscan + explosión en el punto de impacto). */
    private static void fireExplosive(ServerPlayerEntity player, ServerWorld world, GunType type,
                                      Vec3d eye, Random random) {
        Vec3d dir = player.getRotationVec(1.0f);
        Vec3d end = eye.add(dir.multiply(type.getRange()));

        BlockHitResult blockHit = world.raycast(new RaycastContext(
                eye, end, RaycastContext.ShapeType.COLLIDER, RaycastContext.FluidHandling.NONE, player));
        Box searchBox = player.getBoundingBox().stretch(dir.multiply(type.getRange())).expand(1.0);
        EntityHitResult entityHit = ProjectileUtil.raycast(player, eye, end, searchBox,
                e -> !e.isSpectator() && e.canHit() && e != player, type.getRange() * type.getRange());

        Vec3d impact;
        if (entityHit != null) {
            impact = entityHit.getPos();
        } else if (blockHit.getType() != HitResult.Type.MISS) {
            impact = blockHit.getPos();
        } else {
            impact = end;
        }

        // La explosión respeta la regla de juego mobGriefing.
        world.createExplosion(player, impact.x, impact.y, impact.z, type.getExplosionPower(),
                false, World.ExplosionSourceType.MOB);
        world.spawnParticles(ParticleTypes.EXPLOSION_EMITTER, impact.x, impact.y, impact.z, 1, 0, 0, 0, 0);
    }

    // ---- Efectos visuales/sonoros ----

    private static void spawnMuzzleEffects(ServerWorld world, ServerPlayerEntity player, Vec3d eye, boolean silenced) {
        Vec3d look = player.getRotationVec(1.0f);
        Vec3d muzzle = eye.add(look.multiply(1.1));
        int count = silenced ? 2 : 6;
        world.spawnParticles(ParticleTypes.FLAME, muzzle.x, muzzle.y, muzzle.z, count, 0.02, 0.02, 0.02, 0.0);
        world.spawnParticles(ParticleTypes.SMOKE, muzzle.x, muzzle.y, muzzle.z, silenced ? 1 : 3, 0.02, 0.02, 0.02, 0.01);
    }

    private static void spawnShellCasing(ServerWorld world, ServerPlayerEntity player, Vec3d eye) {
        // El casquillo sale expulsado hacia el lado derecho del jugador.
        Vec3d right = new Vec3d(-player.getRotationVec(1.0f).z, 0, player.getRotationVec(1.0f).x).normalize();
        Vec3d pos = eye.add(right.multiply(0.35)).add(0, -0.2, 0);
        ItemStackParticleEffect casing = new ItemStackParticleEffect(ParticleTypes.ITEM, new ItemStack(Items.GOLD_NUGGET));
        world.spawnParticles(casing, pos.x, pos.y, pos.z, 1, right.x * 0.2, 0.05, right.z * 0.2, 0.05);
    }

    private static void playShootSound(ServerWorld world, ServerPlayerEntity player, GunType type,
                                       boolean silenced, Random random) {
        SoundEvent sound = silenced ? ModSounds.SHOOT_SILENCED : ModSounds.forGun(type);
        float volume = silenced ? 0.6f : 1.0f;
        float pitch = 0.95f + random.nextFloat() * 0.1f;
        world.playSound(null, player.getX(), player.getY(), player.getZ(), sound, SoundCategory.PLAYERS, volume, pitch);
    }
}
