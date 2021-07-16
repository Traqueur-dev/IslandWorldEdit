package fr.traqueur.smeltblock.worldedit.managers.profiles;

import com.google.common.collect.Lists;
import com.google.gson.reflect.TypeToken;
import fr.traqueur.smeltblock.worldedit.IslandWorldEdit;
import fr.traqueur.smeltblock.worldedit.api.jsons.DiscUtil;
import fr.traqueur.smeltblock.worldedit.api.jsons.JsonPersist;
import fr.traqueur.smeltblock.worldedit.managers.profiles.clazz.Profile;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.List;

public class ProfileManager implements JsonPersist {

    private static ProfileManager singleton;

    private List<Profile> profiles;

    public ProfileManager() {
        singleton = this;
        profiles = Lists.newArrayList();
    }

    public boolean exist(Profile p) {
        for (Profile profile : getProfiles()) {
            if (profile.getName().equalsIgnoreCase(p.getName())) {
                return true;
            }
        }
        return false;
    }

    public Profile getProfile(Player player) {
        return profiles.stream().filter(pr -> pr.getName().equals(player.getName())).findFirst().orElse(null);
    }

    public void createProfile(Player player) {
        Profile profile = new Profile(player);
        this.createProfile(profile);
    }

    public void createProfile(Profile profile) {
        if(!this.exist(profile)) {
            profiles.add(profile);
        }
    }

    @Override
    public File getFile() {
        return new File(IslandWorldEdit.getInstance().getDataFolder(), "/profiles/");
    }

    public void loadData() {
        if (!this.getFile().exists()) {
            this.getFile().mkdir();
        }
        File[] files = this.getFile().listFiles();
        if(files == null || files.length == 0) {return;}
        for (File file : files) {
            String content = DiscUtil.readCatch(file);
            if (content != null) {
                try {
                    Profile profile = IslandWorldEdit.getInstance().getGson().fromJson(content, new TypeToken<Profile>() {
                    }.getType());
                    this.createProfile(profile);
                } catch (Exception exception) {
                    exception.printStackTrace();
                }
            }
        }
    }

    @Override
    public void saveData() {
        for (Profile profile : this.profiles) {
            DiscUtil.writeCatch(profile.getProfileFile(), IslandWorldEdit.getInstance().getGson().toJson(profile));
        }
    }

    public List<Profile> getProfiles() {
        return profiles;
    }

    public static ProfileManager getSingleton() {
        return singleton;
    }


}
