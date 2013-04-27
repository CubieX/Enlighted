package com.github.CubieX.Enlighted;

import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.inventory.ItemStack;

public class EnlightedEntityListener implements Listener
{
   Logger eLog;
   private Enlighted plugin = null;

   public EnlightedEntityListener(Enlighted plugin)
   {        
      this.plugin = plugin;

      plugin.getServer().getPluginManager().registerEvents(this, plugin);
   }

   //----------------------------------------------------------------------------------------------------    
   @EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
   public void onPlayerItemHeld(PlayerItemHeldEvent event)
   {
      try
      {
         if(event.getPlayer().getInventory().getItem(event.getPreviousSlot()).getTypeId() == Enlighted.torchID) // only needed if player has had a torch in hand until now
         {
            if(event.getPlayer().hasPermission("enlighted.use"))
            {
               ItemStack newItem;            
               newItem = event.getPlayer().getInventory().getItem(event.getNewSlot());

               if ((null == newItem) || newItem.getTypeId() != Enlighted.torchID) // player has no longer a torch in hand
               {
                  // replace lightBlock below Player with original block (only client side!)
                  try
                  {
                     if (plugin.getPreviousLocMap().containsKey(event.getPlayer()))
                     {
                        event.getPlayer().sendBlockChange((Location)plugin.getPreviousLocMap().get(event.getPlayer()), ((Integer)plugin.getPreviousBlockMap().get(event.getPlayer())).intValue(), ((Byte)plugin.getPreviousBlockDataMap().get(event.getPlayer())).byteValue()); 
                     }
                  }
                  catch (Exception ex)
                  {
                     // something went wrong
                  }
               }               
            }
         }
      }
      catch(Exception ex)
      {
         // player is probably no longer online
      }
   }   
}
