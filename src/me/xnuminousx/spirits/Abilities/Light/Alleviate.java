package me.xnuminousx.spirits.Abilities.Light;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.projectkorra.projectkorra.GeneralMethods;
import com.projectkorra.projectkorra.ability.AddonAbility;
import com.projectkorra.projectkorra.configuration.ConfigManager;
import com.projectkorra.projectkorra.util.DamageHandler;

import me.xnuminousx.spirits.Abilities.LightAbility;
import net.md_5.bungee.api.ChatColor;

public class Alleviate extends LightAbility implements AddonAbility {

	private Location location;
	private int currPoint;
	private double range;
	private long time;
	private long potInt;
	private long healInt;
	private long cooldown;
	private String hexColor;

	public Alleviate(Player player) {
		super(player);

		if (!bPlayer.canBend(this)) {
			return;
		}
		
		setFields();
		time = System.currentTimeMillis();
		start();
	}

	private void setFields() {
		this.cooldown = ConfigManager.getConfig().getLong("ExtraAbilities.Spirits.Alleviate.Cooldown");
		this.range = ConfigManager.getConfig().getDouble("ExtraAbilities.Spirits.Alleviate.Radius");
		this.potInt = ConfigManager.getConfig().getLong("ExtraAbilities.Spirits.Alleviate.PotionInterval");
		this.healInt = ConfigManager.getConfig().getLong("ExtraAbilities.Spirits.Alleviate.HealInterval");
		this.hexColor = ConfigManager.getConfig().getString("ExtraAbilities.Spirits.Alleviate.ParticleColor (Has to be 6 characters)");
		this.location = player.getLocation();
	}

	@Override
	public void progress() {
		if (player.isDead() || !player.isOnline() || GeneralMethods.isRegionProtectedFromBuild(this, location)) {
			remove();
			return;
		}
		if (!bPlayer.getBoundAbilityName().equals(getName())) {
			bPlayer.addCooldown(this);
			remove();
			return;
		}
		
		if (player.isSneaking()) {
			effect(200, 0.04F);
			
		} else {
			bPlayer.addCooldown(this);
			remove();
			return;
		}
	}
	
	public void effect(int points, float size) {
		
		for (Entity target : GeneralMethods.getEntitiesAroundPoint(player.getLocation(), range)) {
			if (((target instanceof LivingEntity)) && (target.getEntityId() != player.getEntityId())) {
				Location tarLoc = target.getLocation();
				LivingEntity le = (LivingEntity)target;
				
				for (int i = 0; i < 6; i++) {
					currPoint += 360 / points;
					if (currPoint > 360) {
						currPoint = 0;
					}
					double angle = currPoint * Math.PI / 180 * Math.cos(Math.PI);
					double x = size * (Math.PI * 4 - angle) * Math.cos(angle + i);
		            double y = 1.2 * Math.cos(angle) + 1.2;
		            double z = size * (Math.PI * 4 - angle) * Math.sin(angle + i);
					tarLoc.add(x, y, z);
					GeneralMethods.displayColoredParticle(tarLoc, hexColor, 0, 0, 0);
					tarLoc.subtract(x, y, z);
				}
				
				if (System.currentTimeMillis() - time > potInt) {
					for (PotionEffect targetEffect : le.getActivePotionEffects()) {
						if (isNegativeEffect(targetEffect.getType())) {
							le.removePotionEffect(targetEffect.getType());
						}
					}
				}
				if (System.currentTimeMillis() - time > healInt) {
					le.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 1), true);
					DamageHandler.damageEntity(player, 6, this);
					remove();
					return;
				}
			}
		}
	}

	@Override
	public long getCooldown() {
		return cooldown;
	}

	@Override
	public Location getLocation() {
		return null;
	}

	@Override
	public String getName() {
		return "Alleviate";
	}
	
	@Override
	public String getDescription() {
		return ChatColor.AQUA + "" + ChatColor.BOLD + "Utility: " + ChatColor.WHITE + "Use this ability to relieve your friends and allies of their negative potion effects. Keep using it and you'll give them a small boost of your own health.";
	}
	
	@Override
	public String getInstructions() {
		return ChatColor.AQUA + "Hold shift";
	}

	@Override
	public String getAuthor() {
		return ChatColor.AQUA + "xNuminousx";
	}

	@Override
	public String getVersion() {
		return ChatColor.AQUA + "1.0";
	}

	@Override
	public boolean isExplosiveAbility() {
		return false;
	}

	@Override
	public boolean isHarmlessAbility() {
		return false;
	}

	@Override
	public boolean isIgniteAbility() {
		return false;
	}

	@Override
	public boolean isSneakAbility() {
		return false;
	}

	@Override
	public void load() {

	}

	@Override
	public void stop() {

	}

}