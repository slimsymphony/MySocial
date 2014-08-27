package frank.incubator.android.mysocial.dummy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import frank.incubator.android.mysocial.common.CommonUtils;

/**
 * Helper class for providing sample content for user interfaces created by
 * Android template wizards.
 * <p>
 * TODO: Replace all uses of this class before publishing your app.
 */
public class ListAdapter {

    /**
     * An array of sample (dummy) items.
     */
    public static List<ListItem> ITEMS = new ArrayList<ListItem>();

    /**
     * A map of sample (dummy) items, by ID.
     */
    public static Map<String, ListItem> ITEM_MAP = new HashMap<String, ListItem>();

    static {
        addItem(new ListItem("Feeds", "FeedsView"));
        addItem(new ListItem("Config", "ConfigView"));
        addItem(new ListItem("About", "AboutView"));
    }

    private static void addItem(ListItem item) {
        ITEMS.add(item);
        ITEM_MAP.put(item.id, item);
    }

    /**
     * A dummy item representing a piece of content.
     */
    public static class ListItem {
        public String id;
        public String viewName;

        public ListItem(String id, String viewName) {
            this.id = id;
            this.viewName = viewName;
        }

        @Override
        public String toString() {
            return id;
        }
    }
}
