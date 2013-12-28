/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.stutiguias.mcmmorankup.config;

import java.io.IOException;
import me.stutiguias.mcmmorankup.Mcmmorankup;
import org.bukkit.configuration.file.FileConfiguration;

/**
 *
 * @author Daniel
 */
public class MessageConfig {
    
    private final ConfigAccessor message;

    
    public MessageConfig(Mcmmorankup instance,String language) throws IOException {
        
        message = new ConfigAccessor(instance,language + ".yml");
        message.setupConfig();
        FileConfiguration fm = message.getConfig();

        // MessageConfig
        HabilitySet                 = fm.getString("Hability.Set");
        HabilitySetFail             = fm.getString("Hability.SetFail");
        BaseRanksListing            = fm.getString("Hability.Title");
        NotShowInfo                 = fm.getString("Hability.NotShowInfo");
        DefaultSkilltoRank          = fm.getString("Hability.DefaultSkilltoRank");
        
        NotHaveProfile              = fm.getString("Error.McMMONotHaveProfile");
        NotAvailable                = fm.getString("Error.NotAvailable");
        NoAccess                    = fm.getString("Error.NoAccess");
        NoLongerExists              = fm.getString("Error.NoLongerExists");
        CommandAttempt              = fm.getString("Error.CommandAttempt");
        NoPermPlayerFeeds           = fm.getString("Error.NoPermPlayerFeeds");
        PlayerFeedsDisabled         = fm.getString("Error.PlayerFeedsDisabled");
        IgnoredRankLineSet          = fm.getString("Error.IgnoredRankLineSet");
        
        PromosIgnored               = fm.getString("Rank.PromosIgnored");
        RankInfoTitle               = fm.getString("Rank.RankInfoTitle");
        PromoteTitle                = fm.getString("Rank.PromoteTitle");
        DemoteTitle                 = fm.getString("Rank.DemoteTitle");
        RankInfoLine1               = fm.getString("Rank.RankInfoLine1");
        RankInfoLine2               = fm.getString("Rank.RankInfoLine2");
        RankInfoLine3               = fm.getString("Rank.RankInfoLine3");
        RankInfoMax                 = fm.getString("Rank.RankInfoHighest");

        Demotion                    = fm.getString("RankChange.Demotion");
        Promotion                   = fm.getString("RankChange.Promotion");
        Sucess                      = fm.getString("RankChange.Sucess");
        Fail                        = fm.getString("RankChange.Fail");    
        BroadcastRankupTitle        = fm.getString("RankChange.BroadcastRankupTitle");
        RankPromoteDemote           = fm.getString("RankChange.PromoteDemote");
        Promote                     = fm.getString("RankChange.Promote");
        Demote                      = fm.getString("RankChange.Demote");
        
        AbilityDisabled             = fm.getString("Hab.Disabled");
        AbilityEnabled              = fm.getString("Hab.Enabled");
        HabListCurRankLine          = fm.getString("Hab.CurRankLine");
        HabListPrefixBuy            = fm.getString("Hab.PrefixBuy");
        HabListLevel                = fm.getString("Hab.Level");
        
        SetGender                   = fm.getString("Gender.Set");
        
        RankChecking                = fm.getString("Auto.RankChecking");
        RankCheckingIgnore          = fm.getString("Auto.RankCheckingIgnore");
        
        LastQuitStatsFail           = fm.getString("Stats.LastQuitStatsFail");
        
        McmmoXpGain                 = fm.getString("Mcmmo.XpGain");

        BuyMenu                     = fm.getString("Buy.Menu");
        BuyGeneral                  = fm.getString("Buy.General");
        BuyPurchaseInfo             = fm.getString("Buy.PurchaseInfo");
        BuyProfile                  = fm.getString("Buy.Profile");
        BuyPurchaseXp               = fm.getString("Buy.PurchaseXp");
        BuyPurchaseBuks             = fm.getString("Buy.PurchaseBuks");
        BuyPurchaseReq              = fm.getString("Buy.PurchaseReq");
        BuyPurchaseNot              = fm.getString("Buy.PurchaseNot");
        BuyIgnoreNoPurchase         = fm.getString("Buy.IgnoreNoPurchase");
        BuyDisabled                 = fm.getString("Buy.Disabled");
        BuyPurchaseConfirm          = fm.getString("Buy.PurchaseConfirm");
        BuyListEntry                = fm.getString("Buy.ListEntry");
        BuyInvalidEntry             = fm.getString("Buy.InvalidEntry");
        BuyInvalidEntryRedo         = fm.getString("Buy.InvalidEntryRedo");
        BuyNoPermBuks               = fm.getString("Buy.NoPermBuks");
        BuyNoPermXp                 = fm.getString("Buy.NoPermXp");
        BuyLevels                   = fm.getString("Buy.Levels");
        BuyPoints                   = fm.getString("Buy.Points");
        BuyPurchase                 = fm.getString("Buy.Purchase");
        
        DisplayInformAll            = fm.getString("Display.InformAll");
        DisplayValidGender          = fm.getString("Display.ValidGender");
        DisplayValidSkill           = fm.getString("Display.ValidSkill");
        DisplayTitle                = fm.getString("Display.Title");
        DisplayLine                 = fm.getString("Display.Line");
        
        Rewarded                    = fm.getString("Rewarded.Message");
        RewardedActionGive          = fm.getString("Rewarded.Give");
        RewardedActionTake          = fm.getString("Rewarded.Take");
        
        HelpView                    = fm.getString("Help.View");
        HelpRank                    = fm.getString("Help.Rank");
        HelpMaleFemale              = fm.getString("Help.MaleFemale");
        HelpHab                     = fm.getString("Help.Hab");
        HelpSethab                  = fm.getString("Help.Sethab");
        HelpSethabIgnore            = fm.getString("Help.SethabIgnore");
        HelpDisplayHab              = fm.getString("Help.DisplayHab");
        HelpFeeds                   = fm.getString("Help.Feeds");
                    
        MessageSeparator            = fm.getString("Menu.MessageSeparator");
        GeneralMessages             = fm.getString("Menu.GeneralMessages");
        PlayerWarnings              = fm.getString("Menu.PlayerWarnings");
        
        NotAvailable = PlayerWarnings + NotAvailable;
        IgnoredRankLineSet = GeneralMessages + IgnoredRankLineSet;
        CommandAttempt = PlayerWarnings + CommandAttempt;
        Sucess = GeneralMessages + Sucess;
        Demotion = GeneralMessages + Demotion;
        Fail = GeneralMessages + Fail;
        PromosIgnored = GeneralMessages + PromosIgnored;
        LastQuitStatsFail = PlayerWarnings + LastQuitStatsFail; 
        HabilitySet = GeneralMessages + HabilitySet;
        PlayerFeedsDisabled = PlayerWarnings + PlayerFeedsDisabled;
        NoPermPlayerFeeds = PlayerWarnings + NoPermPlayerFeeds;
        NoAccess = PlayerWarnings + NoAccess;
        NoLongerExists = PlayerWarnings + NoLongerExists;
    }
  
    public void Reload() {
        message.reloadConfig();
    }
    
    public String HabilitySet;
    public String NotHaveProfile;
    public String Demotion;
    public String Promotion;
    public String Sucess;
    public String Fail;
    public String NotAvailable;
    public String NoAccess;
    public String AbilityEnabled;
    public String AbilityDisabled;
    public String SetGender;
    public String RankInfoTitle;
    public String PromoteTitle;
    public String DemoteTitle;
    public String BroadcastRankupTitle;
    public String RankInfoLine1;
    public String RankInfoLine2;
    public String RankInfoLine3;
    public String RankInfoMax;
    public String RankPromoteDemote;
    public String BaseRanksListing;
    public String NotShowInfo;
    public String MaxedOutLevel;
    public String PromosIgnored;
    public String CommandAttempt;
    public String RankChecking;
    public String RankCheckingIgnore;
    public String NoLongerExists;
    public String LastQuitStats;
    public String LastQuitStatsFail;
    public String HabilitySetFail;
    public String IgnoredRankLineSet;
    public String NoPermPlayerFeeds;
    public String PlayerFeedsDisabled;
    public String DefaultSkilltoRank;
    public String HabListLevel;
    public String HabListCurRankLine;
    public String HabListPrefixBuy;
    public String McmmoXpGain;
    public String BuyMenu;
    public String BuyNoPermBuks;
    public String BuyNoPermXp;
    public String BuyGeneral;
    public String BuyPurchaseInfo;
    public String BuyProfile;
    public String BuyPurchaseXp;
    public String BuyPurchaseBuks;
    public String BuyPurchaseReq;
    public String BuyPurchaseNot;
    public String BuyIgnoreNoPurchase;
    public String BuyDisabled;
    public String BuyPurchaseConfirm;
    public String BuyListEntry;
    public String BuyInvalidEntry;
    public String BuyInvalidEntryRedo;
    public String BuyLevels;
    public String BuyPoints;
    public String BuyPurchase;
    public String DisplayInformAll;
    public String DisplayValidGender;
    public String DisplayValidSkill;
    public String DisplayTitle;
    public String DisplayLine;
    public String Rewarded;
    public String RewardedActionGive;
    public String RewardedActionTake;
    public String HelpView;
    public String HelpRank;
    public String HelpMaleFemale;
    public String HelpHab;             
    public String HelpSethab;
    public String HelpSethabIgnore;
    public String HelpDisplayHab;
    public String HelpFeeds;            
    public String Promote;
    public String Demote;
    public String MessageSeparator;
    public String GeneralMessages;
    public String PlayerWarnings;
}
