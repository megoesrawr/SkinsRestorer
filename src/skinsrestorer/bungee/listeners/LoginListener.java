package skinsrestorer.bungee.listeners;

import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.ServerConnectedEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.connection.LoginResult.Property;
import net.md_5.bungee.event.EventHandler;
import skinsrestorer.bungee.SkinApplier;
import skinsrestorer.bungee.SkinsRestorer;
import skinsrestorer.shared.storage.Config;
import skinsrestorer.shared.storage.SkinStorage;
import skinsrestorer.shared.utils.MojangAPI;
import skinsrestorer.shared.utils.MojangAPI.SkinRequestException;

public class LoginListener implements Listener {

	@EventHandler
	public void onLogin(LoginEvent e) {
		if (Config.DISABLE_ONJOIN_SKINS || e.isCancelled())
			return;

		String skinname = SkinStorage.getPlayerSkin(e.getConnection().getName());

		if (skinname == null || skinname.isEmpty())
			skinname = e.getConnection().getName();

		String skin = skinname;

		e.registerIntent(SkinsRestorer.getInstance());
		ProxyServer.getInstance().getScheduler().runAsync(SkinsRestorer.getInstance(), new Runnable() {

			@Override
			public void run() {
				try {
					Property props = (Property) MojangAPI.getSkinProperty(MojangAPI.getUUID(skin));
					SkinStorage.setSkinData(skin, props);
				} catch (SkinRequestException ex) {
					ex.printStackTrace();
				}
				e.completeIntent(SkinsRestorer.getInstance());

			}
		});
	}

	@EventHandler
	public void onServerConnect(ServerConnectedEvent e) {
		if (Config.DISABLE_ONJOIN_SKINS)
			return;

		SkinApplier.applySkin(e.getPlayer());
	}

}
