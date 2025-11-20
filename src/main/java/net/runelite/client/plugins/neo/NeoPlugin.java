package net.runelite.client.plugins.neo;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.inject.Provides;
import java.awt.image.BufferedImage;
import java.util.List;
import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.events.ChatMessage;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.ClientToolbar;
import net.runelite.client.ui.NavigationButton;
import net.runelite.client.util.ImageUtil;
import net.runelite.client.util.Text;

import static net.runelite.client.plugins.neo.NeoConfig.CONFIG_KEY;
import static net.runelite.client.plugins.neo.NeoConfig.GROUP_NAME;

@Slf4j
@PluginDescriptor(
        name = "Neo Keyword Filter",
        description = "Allows users to add keywords and react to them in chat (e.g., muting or flagging for report).",
        tags = {"chat", "filter", "keyword", "panel", "user", "report"}
)
public class NeoPlugin extends Plugin
{
    @Inject
    private ClientToolbar clientToolbar;

    @Inject
    private NeoPanel panel;

    @Inject
    private ConfigManager configManager;

    @Inject
    private NeoConfig config;

    @Inject
    private Gson gson;

    @Inject
    private Client client;

    private NavigationButton navButton;

    @Override
    protected void startUp() throws Exception
    {
        // 1. Setup UI Panel and Navigation Button
        final BufferedImage icon = ImageUtil.loadImageResource(getClass(), "/neo_icon.png");

        navButton = NavigationButton.builder()
                .tooltip("Neo Filter")
                .icon(icon)
                .priority(3)
                .panel(panel)
                .build();

        clientToolbar.addNavigation(navButton);

        // 2. Load Filters
        loadFilterEntries();
    }

    @Override
    protected void shutDown() throws Exception
    {
        clientToolbar.removeNavigation(navButton);
        panel.clearFilterEntries();
    }

    /**
     * Loads the list of keyword filters from the configuration manager and updates the UI panel.
     */
    private void loadFilterEntries()
    {
        String json = configManager.getConfiguration(GROUP_NAME, CONFIG_KEY);

        if (json == null || json.isEmpty() || json.equals("[]"))
        {
            return;
        }

        try
        {
            // Use TypeToken to correctly deserialize the List of FilterEntry objects
            List<FilterEntry> filters = gson.fromJson(json, new TypeToken<List<FilterEntry>>() {}.getType());

            if (filters != null)
            {
                for (FilterEntry filter : filters)
                {
                    panel.addFilterEntry(filter);
                }
            }
        }
        catch (Exception e)
        {
            log.error("Failed to load filter entries from config.", e);
        }
    }

    /**
     * Saves the current list of keyword filters from the UI panel to the configuration manager as a JSON string.
     */
    public void saveFilterEntries()
    {
        List<FilterEntry> filters = panel.getAllFilterEntries();
        String json = gson.toJson(filters);
        configManager.setConfiguration(GROUP_NAME, CONFIG_KEY, json);
    }

    /**
     * Listens for incoming ChatMessage events to apply filtering rules.
     * @param chatMessage The incoming chat message event.
     */
    @Subscribe
    public void onChatMessage(ChatMessage chatMessage)
    {
        // 1. Sanitize and normalize the message for comparison (remove tags, lower case)
        String message = Text.removeTags(chatMessage.getMessage()).toLowerCase();
        List<FilterEntry> filters = panel.getAllFilterEntries();

        for (FilterEntry filter : filters)
        {
            // Ensure the filter is enabled and the message contains the keyword
            if (filter.isEnabled() && message.contains(filter.getKeyword().toLowerCase()))
            {
                log.info("Keyword '{}' found in chat message: {}", filter.getKeyword(), chatMessage.getMessage());

                // 1. Report Action: Log or flag the message for reporting.
                if (filter.isReport())
                {
                    // This logs a warning in the client for review, simulating a report flag.
                    log.warn("ACTION: FLAG for REPORTING chat message by user {}: {}",
                            Text.removeTags(chatMessage.getName()), chatMessage.getMessage());
                }

                // 2. Mute Action: Consumes the message, preventing display.
                if (filter.isMute())
                {
                    // FIX: Replaced chatMessage.getMessageNode().setRuneLiteParsed(true)
                    // with the older, compatible method of muting by setting the message to empty.
                    chatMessage.getMessageNode().setValue("");
                }

                // Stop processing other filters once one has matched.
                return;
            }
        }
    }

    @Provides
    NeoConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(NeoConfig.class);
    }
}