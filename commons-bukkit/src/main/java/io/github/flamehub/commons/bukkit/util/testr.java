package io.github.flamehub.commons.bukkit.util;

import java.util.Base64;

public class testr {

    public static void main(String[] args) {
        String decoded = new String(Base64.getDecoder().decode("ewogICJ0aW1lc3RhbXAiIDogMTcxMjk2Mjg2OTAxMywKICAicHJvZmlsZUlkIiA6ICI2ZjMzMGFkYjg5MGE0NDkxOTQ5MWU0MDY0MTZmMjVmZiIsCiAgInByb2ZpbGVOYW1lIiA6ICJSaXRvQWxlam8iLAogICJ0ZXh0dXJlcyIgOiB7CiAgICAiU0tJTiIgOiB7CiAgICAgICJ1cmwiIDogImh0dHA6Ly90ZXh0dXJlcy5taW5lY3JhZnQubmV0L3RleHR1cmUvNGEwNTU4ZjdjNGQ5YmJkZDUxZGNjMzlmYmYyZWUzOTY1ZDc2NjRhYzcxMzc0Y2FjM2U1Y2U3NDUwNTVhODQ1NiIKICAgIH0KICB9Cn0="));
        System.out.println(decoded);
    }

}
