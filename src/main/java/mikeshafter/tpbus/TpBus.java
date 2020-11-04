package mikeshafter.tpbus;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockState;
import org.bukkit.block.Sign;
import org.bukkit.block.data.BlockData;
import org.bukkit.block.data.type.WallSign;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;
public final class TpBus extends JavaPlugin implements Listener{
  @Override
  public void onEnable() {
    getConfig().options().copyDefaults(true);
    saveConfig();
    getServer().getPluginManager().registerEvents(this,this); }
  @Override
  public void onDisable() {
    saveConfig();
    getServer().getConsoleSender().sendMessage(ChatColor.AQUA+"TpBus Plugin has been disabled!"); }
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
          FileConfiguration config = this.getConfig();
          Location location = block.getLocation();
          config.set(route+"."+stop, location);
          saveConfig();
          location = (Location) config.get(route+"."+(stop+1));
          if (location != null){
            location.setX(location.getX()+0.5);
            location.setZ(location.getZ()+0.5);
            player.sendMessage(ChatColor.YELLOW+"Route name: "+route+". Next stop: Bus stop no. "+(stop+1));
            player.teleport(location);
            location.setX(location.getX()-0.5);
            location.setZ(location.getZ()-0.5); }}}}}}