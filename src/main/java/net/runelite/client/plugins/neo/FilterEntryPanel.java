package net.runelite.client.plugins.neo;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JCheckBox;
import javax.swing.JButton;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;
import net.runelite.client.ui.ColorScheme;
import net.runelite.client.ui.PluginPanel;
import net.runelite.client.ui.components.FlatTextField;
import net.runelite.client.util.SwingUtil;

/**
 * A panel representing a single FilterEntry in the NeoPanel list.
 */
public class FilterEntryPanel extends JPanel
{
    private final NeoPlugin plugin;
    private final NeoPanel parentPanel;
    private final FilterEntry filter;

    private final FlatTextField textField = new FlatTextField();
    private final JCheckBox enabledCheckbox = new JCheckBox("Enabled");
    private final JCheckBox muteCheckbox = new JCheckBox("Mute");
    private final JCheckBox reportCheckbox = new JCheckBox("Report"); // NEW
    private final JButton removeButton = new JButton("X");

    public FilterEntryPanel(FilterEntry filter, NeoPlugin plugin, NeoPanel parentPanel)
    {
        this.filter = filter;
        this.plugin = plugin;
        this.parentPanel = parentPanel;

        // --- Panel Setup ---
        setBackground(ColorScheme.DARKER_GRAY_COLOR);
        setLayout(new BorderLayout(5, 0));
        setBorder(new EmptyBorder(5, 5, 5, 5));
        setPreferredSize(new Dimension(PluginPanel.PANEL_WIDTH - 20, 35));

        // --- Keyword Input Field ---
        textField.setText(filter.getKeyword());
        textField.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        textField.setBorder(new EmptyBorder(0, 0, 0, 0));
        textField.setEditable(true); // Allow editing the keyword

        // Save keyword on focus loss
        textField.getTextField().addFocusListener(new FocusAdapter()
        {
            @Override
            public void focusLost(FocusEvent e)
            {
                saveKeyword();
            }
        });

        // Save keyword on Enter key press
        textField.getTextField().addKeyListener(new KeyAdapter()
        {
            @Override
            public void keyPressed(KeyEvent e)
            {
                if (e.getKeyCode() == KeyEvent.VK_ENTER)
                {
                    saveKeyword();
                    // Optional: remove focus after pressing enter
                    textField.getTextField().getParent().requestFocusInWindow();
                }
            }
        });

        add(textField, BorderLayout.CENTER);

        // --- Controls Panel (Checkboxes and Remove Button) ---
        JPanel controlsPanel = new JPanel(new BorderLayout(5, 0));
        controlsPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);

        // Using FlowLayout to ensure all three checkboxes fit side-by-side
        JPanel checkboxPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 0));
        checkboxPanel.setBackground(ColorScheme.DARKER_GRAY_COLOR);


        // Enabled Checkbox
        enabledCheckbox.setSelected(filter.isEnabled());
        enabledCheckbox.setToolTipText("If checked, this filter is active.");
        enabledCheckbox.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        enabledCheckbox.addActionListener(e ->
        {
            filter.setEnabled(enabledCheckbox.isSelected());
            parentPanel.onFilterUpdated();
        });

        // Mute Checkbox
        muteCheckbox.setSelected(filter.isMute());
        muteCheckbox.setToolTipText("If checked, the chat message containing this keyword will be muted (consumed).");
        muteCheckbox.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        muteCheckbox.addActionListener(e ->
        {
            filter.setMute(muteCheckbox.isSelected());
            parentPanel.onFilterUpdated();
        });

        // Report Checkbox (NEW)
        reportCheckbox.setSelected(filter.isReport());
        reportCheckbox.setToolTipText("If checked, a message matching this keyword will be flagged for logging/reporting.");
        reportCheckbox.setBackground(ColorScheme.DARKER_GRAY_COLOR);
        reportCheckbox.addActionListener(e ->
        {
            filter.setReport(reportCheckbox.isSelected());
            parentPanel.onFilterUpdated();
        });


        // Add Checkboxes
        checkboxPanel.add(enabledCheckbox);
        checkboxPanel.add(muteCheckbox);
        checkboxPanel.add(reportCheckbox); // Added new checkbox
        controlsPanel.add(checkboxPanel, BorderLayout.CENTER);

        // Remove Button
        removeButton.setText("X");
        SwingUtil.remove.setToolTipText(removeButton, "Remove filter.");
        removeButton.setPreferredSize(new Dimension(20, 20));
        removeButton.addActionListener(e -> parentPanel.removeEntry(this));
        controlsPanel.add(removeButton, BorderLayout.EAST);

        add(controlsPanel, BorderLayout.EAST);
    }

    /**
     * Updates the FilterEntry's keyword and saves the config if the keyword changed.
     */
    private void saveKeyword()
    {
        String newKeyword = textField.getText().trim();
        if (!newKeyword.isEmpty() && !newKeyword.equals(filter.getKeyword()))
        {
            filter.setKeyword(newKeyword);
            parentPanel.onFilterUpdated();
        }
        else if (newKeyword.isEmpty())
        {
            // Revert to original keyword if new input is empty (optional)
            textField.setText(filter.getKeyword());
        }
    }

    public FilterEntry getFilter()
    {
        return filter;
    }
}