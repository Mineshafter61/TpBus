package mikeshafter.tpbus;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;

public final class TpBus extends JavaPlugin implements Listener, CommandExecutor {
  @Override
  public void onEnable() {
    getConfig().options().copyDefaults(true); saveConfig();
    getServer().getPluginManager().registerEvents(this,this);
  }

  @Override
  public void onDisable() {
    saveConfig();
    getServer().getConsoleSender().sendMessage(ChatColor.AQUA+"TpBus Plugin has been disabled!");
  }
  
  //FileConfiguration config = this.getConfig();

  @EventHandler
  public void onSignClick(PlayerInteractEvent event) {
    if (event.getClickedBlock() != null){
      Player player = event.getPlayer();
      Block block = event.getClickedBlock();
      BlockState state = block.getState();
      BlockData blockData = block.getBlockData();
      if (event.getAction() == Action.RIGHT_CLICK_BLOCK && state instanceof Sign && blockData instanceof WallSign){
        Sign sign = (Sign) state;
        if (sign.getLine(0).equalsIgnoreCase("[BusStop]")){
          String route = sign.getLine(1);
          int stop = Integer.parseInt(sign.getLine(2));
          FileConfiguration stops = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "stops.yml"));
          Location location = block.getLocation();
          if (player.hasPermission("tpbus.editbusstop")) {stops.set(route+"."+stop, location); saveConfig(); }
          location = (Location) stops.get(route+"."+(stop+1));
          if (location != null){
            location.setX(location.getX()+0.5);
            location.setZ(location.getZ()+0.5);
            player.sendMessage(ChatColor.YELLOW+"Route name: "+route+". Next stop: Bus stop no. "+(stop+1));
            player.teleport(location);
            location.setX(location.getX()-0.5);
            location.setZ(location.getZ()-0.5); }
          else {
            if (player.hasPermission("tpbus.editbusstop")) player.sendMessage(ChatColor.YELLOW+"Registered Bus Stop "+stop+" on route "+route+".");
            else player.sendMessage(ChatColor.RED+"You do not have the permission to register a bus stop!");
          }
        }
      }
    }
  }
  
  @Override
  public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
    if (sender.hasPermission("tpbus.editbusstop")){
      try{
        String route = args[0];
        int stop = Integer.parseInt(args[1]);
        FileConfiguration stops = YamlConfiguration.loadConfiguration(new File(getDataFolder(), "stops.yml"));
        stops.set(route+"."+stop, null);
        saveConfig();
        sender.sendMessage(ChatColor.YELLOW+"Bus Stop "+stop+" on route "+route+" has been unregistered.");
        return true;
      } catch (Exception e) {sender.sendMessage("Error: usage: /delbusstop <route> <stop>");}
    }
    else sender.sendMessage(ChatColor.RED+"You do not have the permission to delete a bus stop!");
    return false;
  }
}
