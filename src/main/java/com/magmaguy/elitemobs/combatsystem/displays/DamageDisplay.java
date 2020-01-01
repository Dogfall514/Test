package com.magmaguy.elitemobs.combatsystem.displays;

import com.magmaguy.elitemobs.ChatColorConverter;
import com.magmaguy.elitemobs.EntityTracker;
import com.magmaguy.elitemobs.combatsystem.EliteMobDamagedByPlayerHandler;
import com.magmaguy.elitemobs.config.MobCombatSettingsConfig;
import com.magmaguy.elitemobs.config.enchantments.EnchantmentsConfig;
import com.magmaguy.elitemobs.items.customenchantments.CriticalStrikesEnchantment;
import com.magmaguy.elitemobs.utils.DialogArmorStand;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.ArmorStand;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.util.Vector;

import java.util.Random;

public class DamageDisplay implements Listener {

    public static boolean isCriticalHit = false;

    @EventHandler(priority = EventPriority.MONITOR)
    public void onHit(EntityDamageEvent event) {

        if (EliteMobDamagedByPlayerHandler.display) {
            EliteMobDamagedByPlayerHandler.display = false;
            displayDamage(event.getEntity(), EliteMobDamagedByPlayerHandler.damage);
            return;
        }

        if (event.isCancelled()) return;

        if (!(event.getEntity() instanceof LivingEntity) || event.getEntity() instanceof ArmorStand) return;

        if (MobCombatSettingsConfig.onlyShowDamageForEliteMobs) {

            if (EntityTracker.isEliteMob(event.getEntity()) && event.getEntity() instanceof LivingEntity) {
                if (event.getDamage() > 0)
                    displayDamage(event.getEntity(),
                            event.getFinalDamage() /
                                    ((LivingEntity) event.getEntity()).getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue() *
                                    EntityTracker.getEliteMobEntity(event.getEntity()).getMaxHealth());
            } else if (EntityTracker.isSuperMob(event.getEntity()))
                displayDamage(event.getEntity(), event.getFinalDamage());

        } else if (event.getDamage() > 0) displayDamage(event.getEntity(), event.getFinalDamage());


    }

    public static void displayDamage(Entity entity, double damage) {

        if (!MobCombatSettingsConfig.displayDamageOnHit) return;

        Location entityLocation = entity.getLocation();

        Random random = new Random();
        double randomCoordX = (random.nextDouble() * 2) - 1 + entityLocation.getX();
        double randomCoordZ = (random.nextDouble() * 2) - 1 + entityLocation.getZ();

        Location newLocation = new Location(entityLocation.getWorld(), randomCoordX, entityLocation.getY() + 1.5, randomCoordZ);

        if (isCriticalHit) {
            isCriticalHit = false;
            DialogArmorStand.createDialogArmorStand(
                    newLocation.clone(),
                    ChatColorConverter.convert(EnchantmentsConfig.getEnchantment("critical_strikes.yml").getFileConfiguration()
                            .getString("criticalHitColor") + "" + ChatColor.BOLD + "" + (int) damage + ""));
            CriticalStrikesEnchantment.criticalStrikePopupMessage(newLocation.clone().add(new Vector(0, 0.2, 0)));
            return;
        }

        DialogArmorStand.createDialogArmorStand(newLocation, ChatColor.RED + "" + ChatColor.BOLD + "" + (int) damage + "");

    }

}
