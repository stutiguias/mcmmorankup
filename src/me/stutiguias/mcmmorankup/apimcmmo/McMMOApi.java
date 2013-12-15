package me.stutiguias.mcmmorankup.apimcmmo;

import org.bukkit.entity.Player;
import com.gmail.nossr50.api.ExperienceAPI;
import com.gmail.nossr50.datatypes.skills.SkillType;

public class McMMOApi {

    public static int getPowerLevel(Player player) {
        return ExperienceAPI.getPowerLevel(player);
    }

    public static int getSkillLevel(Player player, String skill) {
        return ExperienceAPI.getLevel(player, skill);
    }

    public static int getSkillLevelOffline(String playerName, String skill) {
        return ExperienceAPI.getLevelOffline(playerName, skill);
    }

    public static int getPowerLevelOffline(String playerName) {
        return ExperienceAPI.getPowerLevelOffline(playerName);
    }

    public static SkillType getSkillType(String skill) {
        if (skill.equalsIgnoreCase("EXCAVATION")) {
            return SkillType.EXCAVATION;
        }
        if (skill.equalsIgnoreCase("FISHING")) {
            return SkillType.FISHING;
        }
        if (skill.equalsIgnoreCase("HERBALISM")) {
            return SkillType.HERBALISM;
        }
        if (skill.equalsIgnoreCase("MINING")) {
            return SkillType.MINING;
        }
        if (skill.equalsIgnoreCase("AXES")) {
            return SkillType.AXES;
        }
        if (skill.equalsIgnoreCase("ARCHERY")) {
            return SkillType.ARCHERY;
        }
        if (skill.equalsIgnoreCase("SWORDS")) {
            return SkillType.SWORDS;
        }
        if (skill.equalsIgnoreCase("TAMING")) {
            return SkillType.TAMING;
        }
        if (skill.equalsIgnoreCase("UNARMED")) {
            return SkillType.UNARMED;
        }
        if (skill.equalsIgnoreCase("ACROBATICS")) {
            return SkillType.ACROBATICS;
        }
        if (skill.equalsIgnoreCase("REPAIR")) {
            return SkillType.REPAIR;
        }
        if (skill.equalsIgnoreCase("WOODCUTTING")) {
            return SkillType.WOODCUTTING;
        }
        if (skill.equalsIgnoreCase("SMELTING")) {
            return SkillType.SMELTING;
        }
        return null;
    }

    public static int getXp(Player pl, String skill) {
        if (skill.equalsIgnoreCase("POWERLEVEL")) {
            return getPowerLevel(pl);
        }
        return ExperienceAPI.getXP(pl, skill);
    }

    public static int getXpToNextLevel(Player pl, String skill) {
        if (skill.equalsIgnoreCase("POWERLEVEL")) {
            return ExperienceAPI.getPowerLevelCap();
        }
        return ExperienceAPI.getXPToNextLevel(pl, skill);
    }
}
