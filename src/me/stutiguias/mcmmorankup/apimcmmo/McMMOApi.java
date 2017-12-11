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
        return SkillType.getSkill(skill);
    }

    public static int getXp(Player pl, String skill) {
        if (skill.equalsIgnoreCase("CUSTOM")) return 0;
        if (skill.equalsIgnoreCase("POWERLEVEL")) {
            return getPowerLevel(pl);
        }
        return ExperienceAPI.getXP(pl, skill);
    }

    public static int getXpToNextLevel(Player pl, String skill) {
        if (skill.equalsIgnoreCase("CUSTOM")) return 0;
        if (skill.equalsIgnoreCase("POWERLEVEL")) {
            return ExperienceAPI.getPowerLevelCap();
        }
        return ExperienceAPI.getXPToNextLevel(pl, skill);
    }
}
