package com.mccritz.kpure.profile;

import com.mccritz.kpure.kPure;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bson.conversions.Bson;

import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.regex.Pattern;

public class ProfileLoadCallable implements Callable<Profile> {

    private MongoCollection<Document> pCollection = kPure.getInstance().getMongoDatabase().getCollection("profiles");

    private UUID id;
    private String name;

    public ProfileLoadCallable(UUID id) {
        this.id = id;
    }

    public ProfileLoadCallable(String name) {
        this.name = name;
    }

    @Override
    public Profile call() throws Exception {
        Bson filter = null;

        if (id != null) {
            filter = Filters.eq("uniqueID", this.id.toString());
        } else if (name != null) {
            filter = Filters.eq("currentName", Pattern.compile("^" + name + "$", Pattern.CASE_INSENSITIVE));
        }

        Document document = pCollection.find(filter).first();

        if (document != null)
            return new Profile(document);
        else
            return null;
    }
}