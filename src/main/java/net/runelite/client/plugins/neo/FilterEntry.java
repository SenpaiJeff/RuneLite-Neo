package net.runelite.client.plugins.neo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Data model for a single keyword filter entry.
 * Uses Lombok annotations to automatically generate getters, setters,
 * constructors, and toString methods.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FilterEntry
{
    private String keyword;
    private boolean enabled;
    private boolean mute;
    // private boolean report;

    public FilterEntry(String keyword, boolean enabled, boolean mute) // <-- THIS IS THE DUPLICATE
    {
        this.keyword = keyword;
        this.enabled = enabled;
        this.mute = mute;
        // this.report = false;
    }
}