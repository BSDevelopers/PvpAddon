package addon.brainsynder.pvp;

import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import simplepets.brainsynder.addon.AddonConfig;
import simplepets.brainsynder.addon.PetModule;
import simplepets.brainsynder.api.Namespace;
import simplepets.brainsynder.api.plugin.SimplePets;
import simplepets.brainsynder.api.user.PetUser;

@Namespace(namespace = "PvpAddon")
public class PvpAddon extends PetModule {
    private boolean removePlayerPet = false;
    private boolean removeAttackingPlayerPet = false;

    @Override
    public void init() {

    }

    @Override
    public void loadDefaults(AddonConfig config) {
        config.addComment("Remove-Pet", "This addon will be updated later on to add some more features");
        config.addDefault("Remove-Pet.From-Player", false, "Settings this to `true` will make it so when the player being hit is attacked, it will remove their pet(s)");
        config.addDefault("Remove-Pet.From-Attacker", false, "Settings this to `true` will make it so when a player attacks another player, it will remove their pet(s)");

        removePlayerPet = config.getBoolean("Remove-Pet.From-Player", false);
        removeAttackingPlayerPet = config.getBoolean("Remove-Pet.From-Attacker", false);
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onHit (EntityDamageByEntityEvent event) {
        if (!isEnabled()) return;
        // Will ensure it is a player getting damaged
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        if (event.isCancelled()) return;

        PetUser user = SimplePets.getUserManager().getPetUser(player).get();
        // PVP
        if (event.getDamager() instanceof Player) makeCodeCleaner(user, (Player) event.getDamager());

        // Indirect PVP (e.g. Arrow, Snowball, Egg)
        if (event.getDamager() instanceof Projectile) {
            Projectile projectile = (Projectile) event.getDamager();
            if (projectile.getShooter() instanceof Player) makeCodeCleaner(user, (Player) projectile.getShooter());
        }
    }

    // This is simply so im not repeating myself xD
    private void makeCodeCleaner (PetUser user, Player damager) {
        if (user.hasPets() && removePlayerPet) user.removePets();
        if (!removeAttackingPlayerPet) return;
        SimplePets.getUserManager().getPetUser(damager.getUniqueId()).ifPresent(attackingUser -> {
            if (attackingUser.hasPets()) attackingUser.removePets();
        });
    }

}
