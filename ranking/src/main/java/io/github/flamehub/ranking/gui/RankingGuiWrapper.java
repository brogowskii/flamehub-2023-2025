package io.github.flamehub.ranking.gui;

import java.io.Serializable;

public final class RankingGuiWrapper implements Serializable {

    private final String id;
    private final String guiName;

    public RankingGuiWrapper(String id, String guiName) {
        this.id = id;
        this.guiName = guiName;
    }

    public String getId() {
        return id;
    }

    public String getGuiName() {
        return guiName;
    }
}
