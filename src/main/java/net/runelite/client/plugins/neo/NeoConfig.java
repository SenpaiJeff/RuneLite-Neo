package net.runelite.client.plugins.neo;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;

@ConfigGroup("neokeywordfilter")
public interface NeoConfig extends Config
{
    String GROUP_NAME = "neokeywordfilter";
    String CONFIG_KEY = "filters";

    @ConfigItem(
            keyName = CONFIG_KEY,
            name = "Keyword Filters",
            description = "The list of custom keyword filters, stored as JSON."
    )
    default String filters()
    {
        return "[]"; // Default to an empty JSON array
    }

    // Example config item (can be removed if not needed)
    @ConfigItem(
            keyName = "showExample",
            name = "Show Example",
            description = "Example configuration item."
    )
    default boolean showExample()
    {
        return false;
    }
}