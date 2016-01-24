package com.breakmc.pure.commands;

import com.breakmc.pure.utils.MessageManager;
import com.breakmc.pure.utils.command.BaseCommand;
import com.breakmc.pure.utils.command.CommandUsageBy;
import com.breakmc.pure.utils.database.DatabaseManager;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCollection;
import com.mongodb.DBCursor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Calendar;
import java.util.Date;

public class CommandRegister extends BaseCommand {

    private DBCollection collection = DatabaseManager.getInstance().getRegisterDatabase().getCollection("users");

    public CommandRegister() {
        super("register", null, CommandUsageBy.PlAYER);
        setUsage("&cImproper usage! /register (password)");
        setMinArgs(1);
        setMaxArgs(1);
    }

    public void execute(CommandSender sender, String[] args) {
        Player p = (Player) sender;

        if (args.length == 1) {
            if (args[0].length() < 3) {
                MessageManager.sendMessage(p, "&cYour password must be atleast 3 characters long.");
                return;
            }

            if (!args[0].matches("[A-Za-z0-9]+")) {
                MessageManager.sendMessage(p, "&cYour password must be alphanumerical.");
                return;
            }

            if (!hasRegistered(p)) {
                Date date = new Date();
                Calendar cal = Calendar.getInstance();
                cal.setTime(date);

                BasicDBObject dbo = new BasicDBObject("username", p.getName());
                dbo.put("uuid", p.getUniqueId().toString());
                dbo.put("password", BCrypt.hashpw(args[0], BCrypt.gensalt()));
                dbo.put("date", "" + cal.get(Calendar.MONTH) + "/" + cal.get(Calendar.DAY_OF_MONTH) + "/" + cal.get(Calendar.YEAR));
                dbo.put("posts", new BasicDBList());
                dbo.put("tickets", new BasicDBList());
                dbo.put("apps", new BasicDBList());

                if (p.isOp()) {
                    dbo.put("admin", true);
                }

                collection.insert(dbo);
                MessageManager.sendMessage(p, "&aSuccessfully registered!");
            } else {
                DBCursor dbc = collection.find(new BasicDBObject("uuid", p.getUniqueId().toString()));

                if (dbc.hasNext()) {
                    BasicDBObject old = (BasicDBObject) dbc.next();

                    old.put("password", BCrypt.hashpw(args[0], BCrypt.gensalt()));

                    collection.update(old, old);

                    MessageManager.sendMessage(p, "&aYour password has been changed!");
                } else {
                    MessageManager.sendMessage(p, "&cAn error occured while setting your password.");
                }
            }
        }
    }

    public boolean hasRegistered(Player player) {
        BasicDBObject search = new BasicDBObject("uuid", player.getUniqueId().toString());
        DBCursor dbc = collection.find(search);

        return dbc.hasNext();
    }
}
