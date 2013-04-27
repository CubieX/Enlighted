package com.github.CubieX.Enlighted;

import java.util.ArrayList;
import java.util.HashMap;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Player;

public class EnlightedSchedulerHandler
{
   private Enlighted plugin = null;   
   private HashMap<Player, Location> previousLoc = new HashMap(); //FIXME wie besser machen?
   private HashMap<Player, Integer> previousBlock = new HashMap();
   private HashMap<Player, Byte> previousBlockData = new HashMap();
   //private final double diagDistToNextBlockCenter = 1.0D * Math.sqrt(2);
   ArrayList<String> nearPlayersWithPermAndTorch = new ArrayList<String>();

   public EnlightedSchedulerHandler(Enlighted plugin)
   {
      this.plugin = plugin;
   }

   public void startLightBlockUpdaterScheduler_SynchRepeated()
   {      
      plugin.getServer().getScheduler().runTaskTimer(plugin, new Runnable()
      {
         public void run()
         {
            try
            {
               Location loc = null;
               World w = null;
               Player[] currOnlinePlayers = plugin.getServer().getOnlinePlayers();

               for(Player currPlayer : currOnlinePlayers)
               {
                  // get player and handle him
                  if(currPlayer.hasPermission("enlighted.use"))
                  {
                     if (currPlayer.getInventory().getItemInHand().getTypeId() == Enlighted.torchID)
                     {
                        loc = currPlayer.getLocation();
                        w = loc.getWorld();

                        if(Enlighted.debug){currPlayer.sendMessage("Orientation: " +  plugin.getOrientation(loc));}

                        loc.setY(loc.getY() - 1.0D); // set location to block below players feet

                        // set location to block behind the block where player is standing on (relative to his view orientation)
                        // Advantage: Player will see the light block less often 
                        // Disadvantage: If standing with the back turned to a wall, the block will be obstructed and deliver no light
                        /*if(null != plugin.getOrientation(loc))
                     {
                        switch(plugin.getOrientation(loc))
                        {
                        case N:
                           loc.setZ(loc.getZ() + 1.0D);
                           break;
                        case NE:
                           loc.setZ(loc.getZ() + diagDistToNextBlockCenter);
                           loc.setX(loc.getX() - diagDistToNextBlockCenter);
                           break;
                        case E:
                           loc.setX(loc.getX() - 1.0D);
                           break;
                        case SE:
                           loc.setX(loc.getX() - diagDistToNextBlockCenter);
                           loc.setZ(loc.getZ() - diagDistToNextBlockCenter);
                           break;
                        case S:
                           loc.setZ(loc.getZ() - 1.0D);
                           break;
                        case SW:
                           loc.setZ(loc.getZ() - diagDistToNextBlockCenter);
                           loc.setX(loc.getX() + diagDistToNextBlockCenter);
                           break;
                        case W:
                           loc.setX(loc.getX() + 1.0D);
                           break;
                        case NW:
                           loc.setX(loc.getX() + diagDistToNextBlockCenter);
                           loc.setZ(loc.getZ() + diagDistToNextBlockCenter);
                           break;
                        default:
                           // should never happen
                           break;
                        }
                     }*/

                        // REPLACE last placed lightBlock with original block (only client side!)
                        if (previousLoc.containsKey(currPlayer))
                        {  
                           if(Enlighted.globalLight)
                           {
                              // send fake packet to all players in the current world
                              for(Player actPlayer : currOnlinePlayers)
                              {
                                 if(actPlayer.getWorld().equals(currPlayer.getWorld()))
                                 {
                                    actPlayer.sendBlockChange((Location)previousLoc.get(currPlayer), ((Integer)previousBlock.get(currPlayer)).intValue(), ((Byte)previousBlockData.get(currPlayer)).byteValue());                                 
                                 }
                              }
                           }
                           else
                           {
                              // send fake packet only to the current player
                              currPlayer.sendBlockChange((Location)previousLoc.get(currPlayer), ((Integer)previousBlock.get(currPlayer)).intValue(), ((Byte)previousBlockData.get(currPlayer)).byteValue());
                           }
                        }

                        // PLACE new lightBlock
                        if(w.getBlockAt(loc).getType().isSolid()) // only replace blocks that are solid, to prevent player from walking on liquids or air
                        {
                           // save the block before sending the fake packet. 
                           previousBlock.put(currPlayer, Integer.valueOf(w.getBlockAt(loc).getTypeId()));
                           previousBlockData.put(currPlayer, Byte.valueOf(w.getBlockAt(loc).getData()));
                           previousLoc.put(currPlayer, loc);

                           // "place" new lightBlock (only client side!)
                           if(Enlighted.globalLight)
                           {
                              // send fake packet to all players in the current world
                              for(Player actPlayer : currOnlinePlayers)
                              {
                                 if(actPlayer.getWorld().equals(currPlayer.getWorld()))
                                 {
                                    actPlayer.sendBlockChange(loc, Enlighted.lightBlockID, (byte) 0);
                                 }
                              }
                           }
                           else
                           {
                              // send fake packet only to the current player
                              currPlayer.sendBlockChange(loc, Enlighted.lightBlockID, (byte) 0);
                           }
                        }
                     }
                  }
               }
            }
            catch(Exception ex)
            {
               // something went wrong.
            }
         }
      }, 20*5L, 10*1L); // 5 sec delay, 0.5 sec period        
   }

   public void startBlockChangeDelayerScheduler_SynchDelayed(final Location[] locList, final Player currPlayer)
   {
      plugin.getServer().getScheduler().runTaskLater(plugin, new Runnable()
      {
         public void run()
         {
            try
            {
               for(int i = 0; i < locList.length; i++)
               {
                  currPlayer.sendBlockChange(locList[i], Material.AIR, (byte) 0);
               }
            }
            catch (Exception ex)
            {
               // Player probably no longer online
            }
         }
      }, 1L); // 1 tick delay
   }

   public HashMap<Player, Location> getPreviousLocMap()
   {
      return (previousLoc);
   }

   public HashMap<Player, Integer> getPreviousBlockMap()
   {
      return (previousBlock);
   }

   public HashMap<Player, Byte> getPreviousBlockDataMap()
   {
      return (previousBlockData);
   }
}
