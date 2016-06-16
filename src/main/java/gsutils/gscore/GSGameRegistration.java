package gsutils.gscore;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * Created by mspellecacy on 6/11/2016.
 */
public class GSGameRegistration {

    private String game;

    @JsonProperty("game_display_name")
    private String gameDisplayName;

    @JsonProperty("icon_color_id")
    private Integer iconColorId;

    public GSGameRegistration() {
    }

    public GSGameRegistration(String game, String gameDisplayName, Integer iconColorId) {
        this.game = game;
        this.gameDisplayName = gameDisplayName;
        this.iconColorId = iconColorId;
    }

    public String getGame() {
        return game;
    }

    public void setGame(String game) {
        this.game = game;
    }

    public String getGameDisplayName() {
        return gameDisplayName;
    }

    public void setGameDisplayName(String gameDisplayName) {
        this.gameDisplayName = gameDisplayName;
    }

    public Integer getIconColorId() {
        return iconColorId;
    }

    public void setIconColorId(Integer iconColorId) {
        this.iconColorId = iconColorId;
    }
}
