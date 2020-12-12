package io.github.freeze_dolphin.custom_gear;

import java.io.File;

import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import me.mrCookieSlime.CSCoreLibPlugin.general.Inventory.Item.CustomItem;
import me.mrCookieSlime.CSCoreLibPlugin.general.String.StringUtils;
import me.mrCookieSlime.Slimefun.Lists.Categories;
import me.mrCookieSlime.Slimefun.Lists.RecipeType;
import me.mrCookieSlime.Slimefun.Objects.Research;
import me.mrCookieSlime.Slimefun.Objects.SlimefunItem.SlimefunItem;
import me.mrCookieSlime.Slimefun.Setup.SlimefunManager;
import me.mrCookieSlime.Slimefun.api.Slimefun;

public class CustomGear extends JavaPlugin {

	private static int id, swordL, armorL;
	public static FileConfiguration cfg;

	private static String format;
	private static String sword;
	private static String helmet, chestplate, leggings, boots;

	@Override
	public void onEnable() {

		File cfgf = new File(this.getDataFolder().getPath() + File.separator + "config.yml");
		if (!cfgf.exists()) {
			this.saveDefaultConfig();
		}
		cfg = this.getConfig();

		id = cfg.getInt("research.research-id-start");
		swordL = cfg.getInt("research.sword-level");
		armorL = cfg.getInt("research.armor-level");

		format = cfg.getString("translations.format");
		ConfigurationSection tt = cfg.getConfigurationSection("translations.type");
		sword = tt.getString("sword");
		helmet = tt.getString("helmet");
		chestplate = tt.getString("chestplate");
		leggings = tt.getString("leggings");
		boots = tt.getString("boots");

		ConfigurationSection tm = cfg.getConfigurationSection("translations.material");

		for (String m : tm.getKeys(false)) {
			if (cfg.contains("materials.sword." + m)) {
				String mm = cfg.getString("materials.sword." + m);
				registerSword(tm.getString(m), Material.valueOf(mm), getComponent(m), SlimefunItem.getItem(cfg.getString("ingredients." + m)), getEnchantments("sword", m));
			}
			if (cfg.contains("materials.armor." + m)) {
				String mm = cfg.getString("materials.armor." + m);
				registerArmor(tm.getString(m), mm, getComponent(m), SlimefunItem.getItem(cfg.getString("ingredients." + m)), getEnchantments("armor", m));
			}
		}
	}

	private void registerSword(String name, Material type, String component, ItemStack item, String... enchantments) {
		ItemStack is = new CustomItem(type, "&r" + format(format, name, sword) + "&r", enchantments, 0);

		new SlimefunItem(Categories.WEAPONS, is, component + "_SWORD", RecipeType.ENHANCED_CRAFTING_TABLE, new ItemStack[] {
				null, item, null, 
				null, item, null, 
				null, new ItemStack(Material.STICK), null
		}).register();

		Slimefun.registerResearch(new Research(++id, StringUtils.format(component) + " Sword", swordL), is);
	}

	private void registerArmor(String name, String material, String component, ItemStack item, String... enchantments) {

		ItemStack[] armor = new ItemStack[] { 
				new CustomItem(Material.getMaterial(material + "_HELMET"), "&r" + format(format, name, helmet) + "&r", enchantments, 0), 
				new CustomItem(Material.getMaterial(material + "_CHESTPLATE"), "&r" + format(format, name, chestplate) + "&r", enchantments, 0),
				new CustomItem(Material.getMaterial(material + "_LEGGINGS"), "&r" + format(format, name, leggings) + "&r", enchantments, 0),
				new CustomItem(Material.getMaterial(material + "_BOOTS"), "&r" + format(format, name, boots) + "&r", enchantments, 0)
		};

		SlimefunManager.registerArmorSet(item, armor, component, false, false);

		Slimefun.registerResearch(new Research(++id, StringUtils.format(component) + " Armor", armorL), armor);
	}

	private static String getComponent(String lowerType) {
		return lowerType.toUpperCase().replaceAll("-", "_");
	}

	private static String[] getEnchantments(String type, String lowerType) {
		return cfg.getString("enchantments." + type + "." + lowerType).split(" ");
	}

	private static String format(String format, String material, String type) {
		return format.replaceAll("<material>", material).replaceAll("<type>", type);
	}

}
