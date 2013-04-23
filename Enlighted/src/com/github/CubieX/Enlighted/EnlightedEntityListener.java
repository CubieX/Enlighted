package com.github.CubieX.Enlighted;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Logger;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerItemHeldEvent;

import org.bukkit.inventory.ItemStack;

public class EnlightedEntityListener implements Listener
{
   Logger eLog;
   ArrayList<String> playersInSM = new ArrayList<String>();
   private Enlighted plugin = null;
   private Logger log = null;

   public EnlightedEntityListener(Enlighted plugin, Logger log)
   {        
      this.plugin = plugin;
      this.log = log;

      plugin.getServer().getPluginManager().registerEvents(this, plugin);
   }

   // TODO update block when swiching to another item than the torchItem to show normal again
   // TODO perharps make other players in range see the light also beyond other players with torch?
   // TODO use a scheduler instead of PlayerMoveEvent (but has to be implemented to be FASTER (calculation-wise) than the playerMoveEvent)

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

   //----------------------------------------------------------------------------------------------------    
   /*@EventHandler(priority = EventPriority.NORMAL, ignoreCancelled = true)
   public void onPlayerMove(PlayerMoveEvent event)
   {
      if(event.getPlayer().hasPermission("enlighted.use"))
      {
         Location loc = event.getPlayer().getLocation();
         World w = loc.getWorld();

         loc.setY(loc.getY() - 1.0D); // set location to block below players feet

         try
         {
            if (event.getPlayer().getInventory().getItemInHand().getTypeId() == Enlighted.torchID)
            {
               // replace lightBlock below Player with original block (only client side!)
               try
               {
                  if (previousLoc.containsKey(event.getPlayer()))
                  {
                     event.getPlayer().sendBlockChange((Location)previousLoc.get(event.getPlayer()), ((Integer)previousBlock.get(event.getPlayer())).intValue(), ((Byte)previousBlockData.get(event.getPlayer())).byteValue()); 
                  }
               }
               catch (Exception ex)
               {
                  // something went wrong
               }

               if(w.getBlockAt(loc).getType().isSolid()) // only replace blocks that are solid, to prevent player from walking on liquids or air
               {
                  // place lightBlock above Player (only client side!)
                  previousBlock.put(event.getPlayer(), Integer.valueOf(w.getBlockAt(loc).getTypeId()));
                  previousBlockData.put(event.getPlayer(), Byte.valueOf(w.getBlockAt(loc).getData()));
                  previousLoc.put(event.getPlayer(), loc);

                  event.getPlayer().sendBlockChange(loc, Enlighted.lightBlockID, (byte) 0);
               }               
            }
         }
         catch (Exception ex)
         {
            // something went wrong
         }
      }
   }*/


}
