package com.mccritz.kpure.profile;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.stream.Collectors;

import org.bson.Document;

import com.mccritz.kpure.kPure;
import com.mccritz.kpure.punishment.punishments.PermanentBan;
import com.mccritz.kpure.punishment.punishments.PermanentMute;
import com.mccritz.kpure.punishment.punishments.TemporaryBan;
import com.mccritz.kpure.punishment.punishments.TemporaryMute;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;

public class ProfileSaveCallable implements Callable<Void> {

    private Profile profile;
    private MongoCollection<Document> pCollection = kPure.getInstance().getMongoDatabase().getCollection("profiles");

    public ProfileSaveCallable(Profile profile) {
	this.profile = profile;
    }

    @Override
    public Void call() throws Exception {
	Document doc = new Document("uniqueID", profile.getUniqueID().toString());
	doc.append("currentName", profile.getCurrentName());
	doc.append("currentIP", profile.getCurrentIP());
	doc.append("dateCreated", profile.getDateCreated());
	doc.append("group", profile.getGroup());
	doc.append("playtime", profile.getPlaytime());
	doc.append("logins", profile.getLogins());
	doc.append("pin", profile.getPin());
	doc.append("altList", profile.getAltList().stream().map(UUID::toString).collect(Collectors.toList()));
	doc.append("nameList", profile.getNameList());
	doc.append("ipList", profile.getIpList());

	List<Document> docs1 = new ArrayList<>();
	for (PermanentBan b : profile.getPermanentBans()) {
	    Document bDoc = new Document();

	    if (b.getPunisherUUID() != null) {
		bDoc.append("punisherUUID", b.getPunisherUUID().toString());
	    }

	    bDoc.append("reason", b.getReason());
	    bDoc.append("dateCreated", b.getDateIssued());
	    bDoc.append("isActive", b.isActive());
	    docs1.add(bDoc);
	}
	doc.append("permanent-bans", docs1);

	List<Document> docs2 = new ArrayList<>();
	for (TemporaryBan b : profile.getTemporaryBans()) {
	    Document bDoc = new Document();
	    if (System.currentTimeMillis() >= b.getLength()) {
		b.setActive(false);
	    }

	    if (b.getPunisherUUID() != null) {
		bDoc.append("punisherUUID", b.getPunisherUUID().toString());
	    }

	    bDoc.append("length", b.getLength());
	    bDoc.append("reason", b.getReason());
	    bDoc.append("dateCreated", b.getDateIssued());
	    bDoc.append("isActive", b.isActive());
	    docs2.add(bDoc);
	}
	doc.append("temporary-bans", docs2);

	List<Document> docs3 = new ArrayList<>();
	for (PermanentMute b : profile.getPermanentMutes()) {
	    Document bDoc = new Document();

	    if (b.getPunisherUUID() != null) {
		bDoc.append("punisherUUID", b.getPunisherUUID().toString());
	    }

	    bDoc.append("reason", b.getReason());
	    bDoc.append("dateCreated", b.getDateIssued());
	    bDoc.append("isActive", b.isActive());
	    docs3.add(bDoc);
	}
	doc.append("permanent-mutes", docs3);

	List<Document> docs4 = new ArrayList<>();
	for (TemporaryMute b : profile.getTemporaryMutes()) {
	    Document bDoc = new Document();
	    if (System.currentTimeMillis() >= b.getLength()) {
		b.setActive(false);
	    }

	    if (b.getPunisherUUID() != null) {
		bDoc.append("punisherUUID", b.getPunisherUUID().toString());
	    }

	    bDoc.append("length", b.getLength());
	    bDoc.append("reason", b.getReason());
	    bDoc.append("dateCreated", b.getDateIssued());
	    bDoc.append("isActive", b.isActive());
	    docs4.add(bDoc);
	}
	doc.append("temporary-mutes", docs4);

	Document document = pCollection.find(Filters.eq("uniqueID", profile.getUniqueID().toString())).first();

	if (document == null) {
	    pCollection.insertOne(doc);
	} else {
	    pCollection.replaceOne(Filters.eq("uniqueID", profile.getUniqueID().toString()), doc);
	}
	return null;
    }

}