package com.breakmc.pure.commands;

import com.breakmc.pure.utils.MessageManager;
import com.breakmc.pure.utils.command.BaseCommand;
import com.breakmc.pure.utils.command.CommandUsageBy;
import com.breakmc.pure.utils.database.DatabaseManager;
import com.mongodb.async.SingleResultCallback;
import com.mongodb.async.client.MongoCollection;
import com.mongodb.client.model.Filters;
import com.mongodb.client.model.UpdateOptions;
import com.mongodb.client.result.UpdateResult;
import org.bson.Document;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.mindrot.jbcrypt.BCrypt;

import java.util.Calendar;
import java.util.Date;

public class CommandRegister extends BaseCommand {

    private MongoCollection<Document> collection = DatabaseManager.getInstance().getMongoRegisterDatabase().getCollection("users");

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

            Date date = new Date();
            Calendar cal = Calendar.getInstance();
            cal.setTime(date);

            Document doc = new Document("username", p.getName());
            doc.append("uuid", p.getUniqueId().toString());
            doc.append("password", BCrypt.hashpw(args[0], BCrypt.gensalt()));
            doc.append("date", "" + cal.get(Calendar.MONTH) + "/" + cal.get(Calendar.DAY_OF_MONTH) + "/" + cal.get(Calendar.YEAR));

            if (p.hasPermission("register.admin")) {
                doc.append("admin", true);
            }

            collection.find(Filters.eq("uuid", p.getUniqueId().toString())).first(new SingleResultCallback<Document>() {
                @Override
                public void onResult(Document document, Throwable throwable) {
                    if (throwable != null) {
                        throwable.printStackTrace();
                    } else {
                        if (document != null) {
                            collection.replaceOne(Filters.eq("uuid", p.getUniqueId().toString()), doc, new UpdateOptions().upsert(true), new SingleResultCallback<UpdateResult>() {
                                @Override
                                public void onResult(UpdateResult updateResult, Throwable t) {
                                    if (t != null) {
                                        t.printStackTrace();
                                    } else {
                                        if (updateResult.wasAcknowledged()) {
                                            MessageManager.sendMessage(p, "&aPassword successfully changed!");
                                        }
                                    }
                                }
                            });
                        } else {
                            collection.insertOne(doc, new SingleResultCallback<Void>() {
                                @Override
                                public void onResult(Void result, Throwable t) {
                                    if (t != null) {
                                        t.printStackTrace();
                                    } else {
                                        MessageManager.sendMessage(p, "&aSuccessfully registered!");
                                        System.out.println(p.getName() + " (" + p.getUniqueId() + ") has successfully registered.");
                                    }
                                }
                            });
                        }
                    }
                }
            });
        }
    }
}
