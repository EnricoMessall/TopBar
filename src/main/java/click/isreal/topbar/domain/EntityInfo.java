package click.isreal.topbar.domain;

import net.minecraft.entity.Entity;

public class EntityInfo {

    private final Entity entity;
    private final String text;

    public EntityInfo(Entity entity, String text){
        this.entity = entity;
        this.text = text;
    }

    public Entity getEntity() {
        return entity;
    }

    public String getText() {
        return text;
    }
}
