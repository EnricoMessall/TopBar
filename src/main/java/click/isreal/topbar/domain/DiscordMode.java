package click.isreal.topbar.domain;

import click.isreal.topbar.Topbar;

public enum DiscordMode {
    OFF,
    MINIMAL,
    FULL;

    public boolean isActive() {
        return Topbar.getInstance().getDiscordMode() == this;
    }
}
