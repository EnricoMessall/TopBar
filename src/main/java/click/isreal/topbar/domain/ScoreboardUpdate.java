package click.isreal.topbar.domain;

public record ScoreboardUpdate(String title, String rawText, String text) {
    @Override
    public String toString() {
        return "ScoreboardUpdate{" +
                "title='" + title + '\'' +
                ", rawText='" + rawText + '\'' +
                ", text='" + text + '\'' +
                '}';
    }
}
