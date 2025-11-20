package net.runelite.client.plugins.neo;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

public class NeoPluginTest
{
    public static void main(String[] args) throws Exception
    {
        // This tells RuneLite to load your specific plugin on startup
        ExternalPluginManager.loadBuiltin(NeoPlugin.class);
        RuneLite.main(args);
    }
}