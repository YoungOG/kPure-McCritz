package com.mccritz.kpure.listeners;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.UUID;

import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;

import com.google.common.base.Charsets;
import com.mccritz.kpure.kPure;
import com.mccritz.kpure.punishment.PunishmentManager;
import com.mccritz.kpure.utils.NameHistory;

public class UUIDVerifierListener implements Listener {

    private PunishmentManager pum = kPure.getInstance().getPunishmentManager();

    @EventHandler
    public void onPlayerJoin(final PlayerJoinEvent event) {
	final UUID uuid = event.getPlayer().getUniqueId();
	final String name = event.getPlayer().getName();

	new BukkitRunnable() {

	    @Override
	    public void run() {
		final NameHistory history = getProfileNameHistory(uuid);

		new BukkitRunnable() {

		    @Override
		    public void run() {
			if (history == null || history.isEmpty()
				|| !history.get(history.size() - 1).getName().equals(name)) {
			    String ip = event.getPlayer().getAddress().getAddress().getHostAddress();

			    pum.banIP(Bukkit.getConsoleSender(), ip, "UUID mismatch");
			}
		    }

		}.runTask(kPure.getInstance());
	    }

	}.runTaskAsynchronously(kPure.getInstance());
    }

    private static NameHistory getProfileNameHistory(final UUID uuid) {
	final CloseableHttpClient client = HttpClients.createDefault();
	final HttpGet method = new HttpGet(
		"https://api.mojang.com/user/profiles/" + uuid.toString().replace("-", "") + "/names");
	try (final CloseableHttpResponse response = client.execute(method)) {
	    switch (response.getStatusLine().getStatusCode()) {
	    case 200:
		try (final Reader reader = new InputStreamReader(response.getEntity().getContent(), Charsets.UTF_8)) {
		    return kPure.GSON.fromJson(reader, NameHistory.class);
		}
	    case 204:
		return null;
	    default:
		throw new IOException(response.getStatusLine().getReasonPhrase());
	    }
	} catch (IOException e) {
	    e.printStackTrace();
	}

	return null;
    }

}