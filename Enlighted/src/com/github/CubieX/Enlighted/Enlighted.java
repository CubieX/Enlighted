package com.github.CubieX.Enlighted;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Enlighted extends JavaPlugin
{
   public static final Logger log = Logger.getLogger("Minecraft");
   ArrayList<String> playersInSM = new ArrayList<String>();

   static final String logPrefix = "[TimedRanks] "; // Prefix to go in front of all log entries

   private Enlighted plugin = null;
   private EnlightedCommandHandler comHandler = null;
   private EnlightedConfigHandler cHandler = null;
   private EnlightedEntityListener eListener = null;
   private EnlightedSchedulerHandler schedHandler = null;

   public static enum cardinals {N, NE, E, SE, S, SW, W, NW}

   static boolean debug = false;

   public static int torchID = 50;              // ID of item to be held in hand for activating light
   public static int lightBlockID = 89;         // ID of block that emits light
   public static boolean globalLight = false;   // globally visible lightBlocks?

   //*************************************************
   static String usedConfigVersion = "1"; // Update this every time the config file version changes, so the plugin knows, if there is a suiting config present
   //*************************************************

   @Override
   public void onEnable()
   {
      this.plugin = this;
      cHandler = new EnlightedConfigHandler(this);

      if(!checkConfigFileVersion())
      {
         log.severe(logPrefix + "Outdated or corrupted config file(s). Please delete your config files."); 
         log.severe(logPrefix + "will generate a new config for you.");
         log.severe(logPrefix + "will be disabled now. Config file is outdated or corrupted.");
         getServer().getPluginManager().disablePlugin(this);
         return;
      }

      if (!hookToPermissionSystem())
      {
         log.info(logPrefix + " - Disabled due to no superperms compatible permission system found!");
         getServer().getPluginManager().disablePlugin(this);
         return;
      }

      eListener = new EnlightedEntityListener(this, log);      
      comHandler = new EnlightedCommandHandler(this, cHandler, log);      
      getCommand("el").setExecutor(comHandler);

      schedHandler = new EnlightedSchedulerHandler(this);

      readConfigValues();

      schedHandler.startLightBlockUpdaterScheduler_SynchRepeated();

      log.info(this.getDescription().getName() + " version " + getDescription().getVersion() + " is enabled!");
   }

   private boolean checkConfigFileVersion()
   {      
      boolean configOK = false;     

      if(cHandler.getConfig().isSet("config_version"))
      {
         String configVersion = cHandler.getConfig().getString("config_version");

         if(configVersion.equals(usedConfigVersion))
         {
            configOK = true;
         }
      }

      return (configOK);
   }

   private boolean hookToPermissionSystem()
   {
      if ((getServer().getPluginManager().getPlugin("PermissionsEx") == null) &&
            (getServer().getPluginManager().getPlugin("bPermissions") == null) &&
            (getServer().getPluginManager().getPlugin("zPermissions") == null) &&
            (getServer().getPluginManager().getPlugin("PermissionsBukkit") == null))
      {
         return false;
      }
      else
      {
         return true;
      }
   }

   public void readConfigValues()
   {
      debug = cHandler.getConfig().getBoolean("debug");
      torchID = cHandler.getConfig().getInt("torchID");
      lightBlockID = cHandler.getConfig().getInt("lightBlockID");
      globalLight = cHandler.getConfig().getBoolean("globalLight");
   }

   @Override
   public void onDisable()
   {      
      cHandler = null;
      eListener = null;
      comHandler = null;
      schedHandler = null;
      log.info(this.getDescription().getName() + " version " + getDescription().getVersion() + " is disabled!");
   }

   public HashMap<Player, Integer> getPreviousBlockMap()
   {
      return (schedHandler.getPreviousBlockMap());
   }

   public HashMap<Player, Byte> getPreviousBlockDataMap()
   {
      return (schedHandler.getPreviousBlockDataMap());
   }

   public HashMap<Player, Location> getPreviousLocMap()
   {
      return (schedHandler.getPreviousLocMap());
   }

   public cardinals getOrientation(Location playerLoc)
   {
      double rotation = (playerLoc.getYaw() + 180) % 360;

      if (rotation < 0)
      {
         rotation += 360.0;
      }

      if (0 <= rotation && rotation < 22.5) {
         return cardinals.N;
      } else if (22.5 <= rotation && rotation < 67.5) {
         return cardinals.NE;
      } else if (67.5 <= rotation && rotation < 112.5) {
         return cardinals.E;
      } else if (112.5 <= rotation && rotation < 157.5) {
         return cardinals.SE;
      } else if (157.5 <= rotation && rotation < 202.5) {
         return cardinals.S;
      } else if (202.5 <= rotation && rotation < 247.5) {
         return cardinals.SW;
      } else if (247.5 <= rotation && rotation < 292.5) {
         return cardinals.W;
      } else if (292.5 <= rotation && rotation < 337.5) {
         return cardinals.NW;
      } else if (337.5 <= rotation && rotation < 360.0) {
         return cardinals.N;
      } else {
         return null;
      }
   }
}


