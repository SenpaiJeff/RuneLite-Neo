package net.runelite.client.plugins.neo;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.inject.Inject;
import javax.swing.BoxLayout; // New import for vertical layout
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.TitleLabel;
import net.runelite.client.ui.components.scrollablecontainer.ScrollableContainer;

/**
 * The main UI panel for the Neo Keyword Filter plugin, handling the list
 * of filter entries and the "Add" functionality.
 * * NOTE: DragAndDropListListener was removed to resolve compilation issues related to
 * dependency versioning. Entries will be displayed in order without drag-and-drop support.
 */
public class NeoPanel extends PluginPanel // Removed "implements DragAndDropListListener"
{
    private final NeoPlugin plugin;
    // Container uses ScrollableContainer to allow vertical scrolling
    private final JPanel container = new ScrollableContainer();
    private final JTextField keywordInput = new JTextField();
    private final JButton addButton = new JButton("Add");

    private final List<FilterEntryPanel> entryPanels = new ArrayList<>();

    @Inject
    public NeoPanel(NeoPlugin plugin)
    {
        this.plugin = plugin;

        setLayout(new BorderLayout());
        setBorder(new EmptyBorder(10, 10, 10, 10));

        // --- Title and Introduction ---
        JPanel header = new JPanel(new BorderLayout());
        header.add(new TitleLabel("Neo Keyword Filter"), BorderLayout.NORTH);

        JLabel description = new JLabel("<html>Add keywords to mute chat messages.</html>");
        description.setBorder(new EmptyBorder(5, 0, 10, 0));
        header.add(description, BorderLayout.CENTER);

        add(header, BorderLayout.NORTH);

        // --- Add New Filter Input ---
        JPanel inputPanel = new JPanel(new BorderLayout(5, 0));
        inputPanel.setBorder(new EmptyBorder(0, 0, 10, 0));

        keywordInput.setToolTipText("Enter a keyword or phrase to filter");
        inputPanel.add(keywordInput, BorderLayout.CENTER);

        addButton.addActionListener(getAddButtonListener());
        addButton.setPreferredSize(new Dimension(50, 24));
        inputPanel.add(addButton, BorderLayout.EAST);

        // Combine input and list into a main panel
        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(inputPanel, BorderLayout.NORTH);

        // --- Container for Filter Entries ---
        // Changed layout to standard BoxLayout for vertical stacking
        container.setLayout(new BoxLayout(container, BoxLayout.Y_AXIS));
        mainPanel.add(container, BorderLayout.CENTER);

        add(mainPanel, BorderLayout.CENTER);
    }

    /**
     * Creates the ActionListener for the 'Add' button.
     */
    private ActionListener getAddButtonListener()
    {
        return e ->
        {
            String keyword = keywordInput.getText().trim();

            if (keyword.isEmpty())
            {
                return;
            }

            // Create a new filter entry (enabled=true, mute=true, report=false by default)
            FilterEntry newFilter = new FilterEntry(keyword, true, true, false);
            addFilterEntry(newFilter);

            keywordInput.setText(""); // Clear the input field

            plugin.saveFilterEntries();
        };
    }

    /**
     * Adds a FilterEntry to the UI list and updates the panel.
     * @param filter The data object for the filter.
     */
    public void addFilterEntry(FilterEntry filter)
    {
        FilterEntryPanel newPanel = new FilterEntryPanel(filter, plugin, this);

        entryPanels.add(newPanel);
        container.add(newPanel);

        // Revalidate and repaint the container and its parent to ensure the new component is shown
        container.revalidate();
        container.repaint();
    }

    /**
     * Removes a FilterEntryPanel from the UI and its associated data model.
     * @param entryPanel The UI panel to remove.
     */
    public void removeEntry(FilterEntryPanel entryPanel)
    {
        entryPanels.remove(entryPanel);
        container.remove(entryPanel);

        plugin.saveFilterEntries();

        // Revalidate and repaint the container and its parent to ensure the removal is shown
        container.revalidate();
        container.repaint();
    }

    /**
     * Called by FilterEntryPanel when a keyword or checkbox is modified.
     */
    public void onFilterUpdated()
    {
        plugin.saveFilterEntries();
    }

    /**
     * Clears all entries from the UI. Used during shutdown or initial load reset.
     */
    public void clearFilterEntries()
    {
        entryPanels.clear();
        container.removeAll();
        revalidate();
        repaint();
    }

    /**
     * Gathers all current filter entries from the UI panels.
     * @return A list of FilterEntry data objects.
     */
    public List<FilterEntry> getAllFilterEntries()
    {
        List<FilterEntry> filters = new ArrayList<>();
        for (FilterEntryPanel entryPanel : entryPanels)
        {
            filters.add(entryPanel.getFilter());
        }
        return filters;
    }

    // --- Removed DragAndDropListListener implementation methods (itemMoved and getContainer) ---
    // This removes the reliance on DragAndDropList and DragAndDropUtil.
}