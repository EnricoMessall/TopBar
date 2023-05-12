package click.isreal.topbar.domain;

import net.minecraft.util.Formatting;

import java.util.function.Consumer;
import java.util.function.Predicate;

public enum ScoreboardMapping implements Predicate<ScoreboardUpdate>, Consumer<ScoreboardUpdate> {
    KFFA_WORLD(
            (update) -> update.title().matches(".*KnockbackFFA.*"),
            (update) -> {
                String trimmedName = update.title()
                        .replaceAll("-[0-9]", "").replaceAll("[^0-9\\:]", "");
                UserData.current().setMixelWorld(MixelWorld.KFFA)
                        .setKffaMapSwitch(Formatting.DARK_AQUA + " [" + trimmedName + "]");
            }),
    CB_WORLD(
            (update) -> update.title().matches(".*CityBuild.*"),
            (update) -> {
                MixelWorld world = MixelWorld.findWorld(update.text());
                if(world != MixelWorld.OTHER) UserData.current().setMixelWorld(world);
            }
    );

    private final Predicate<ScoreboardUpdate> isValid;
    private final Consumer<ScoreboardUpdate> action;
    ScoreboardMapping(Predicate<ScoreboardUpdate> isValid, Consumer<ScoreboardUpdate> action) {
        this.isValid = isValid;
        this.action = action;
    }

    @Override
    public boolean test(ScoreboardUpdate update) {
        return this.isValid.test(update);
    }

    @Override
    public void accept(ScoreboardUpdate update) {
        this.action.accept(update);
    }
}
