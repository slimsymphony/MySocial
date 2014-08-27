package frank.incubator.android.mysocial;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import frank.incubator.android.mysocial.common.Constants;
import frank.incubator.android.mysocial.dummy.ListAdapter;
import frank.incubator.android.mysocial.model.Feed;
import frank.incubator.android.mysocial.service.DefaultFeedManagerImpl;
import frank.incubator.android.mysocial.service.FeedManager;

/**
 * A fragment representing a single Item detail screen.
 * This fragment is either contained in a {@link ItemListActivity}
 * in two-pane mode (on tablets) or a {@link ItemDetailActivity}
 * on handsets.
 */
public class ItemDetailFragment extends Fragment {
    /**
     * The fragment argument representing the item ID that this fragment
     * represents.
     */
    public static final String ARG_ITEM_ID = "item_id";

    /**
     * The dummy content this fragment is presenting.
     */
    private ListAdapter.ListItem mItem;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public ItemDetailFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments().containsKey(ARG_ITEM_ID)) {
            // Load the dummy content specified by the fragment
            // arguments. In a real-world scenario, use a Loader
            // to load content from a content provider.
            mItem = ListAdapter.ITEM_MAP.get(getArguments().getString(ARG_ITEM_ID));
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        /*View rootView = inflater.inflate(R.layout.fragment_item_detail, container, false);

        // Show the dummy content as text in a TextView.
        if (mItem != null) {
            ((TextView) rootView.findViewById(R.id.item_detail)).setText(mItem.viewName);
        }*/
        View rootView = null;
        if (mItem != null) {
            if (mItem.id.equals("Feeds")) {
                rootView = inflater.inflate(R.layout.feeds_items, container, false);
                final ListView lv = (ListView) rootView.findViewById(R.id.feeds);
                Button newBtn = (Button) rootView.findViewById(R.id.newFeed);
                final FeedManager fm = DefaultFeedManagerImpl.getInstance(this.getActivity());
                final LayoutInflater inflaterR = inflater;
                final ViewGroup containerR = container;
                List<Feed> feeds = fm.queryFeed(null, 50, "id DESC");
                final ArrayAdapter<Feed> adapter = new ArrayAdapter<Feed>(this.getActivity(), android.R.layout.simple_list_item_1, feeds);
                lv.setAdapter(adapter);
                newBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Log.i(Constants.LOG_TAG, "Click New feed Button.");
                        final View layout = inflaterR.inflate(R.layout.newfeed_view,(ViewGroup)view.findViewById(R.id.newFeedDialog));
                        new AlertDialog.Builder(view.getContext()).setTitle("Input New Feed!").
                                setIcon(android.R.drawable.ic_dialog_info).setView(layout).
                                setPositiveButton("Ok", new DialogInterface.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface di, int i) {
                                        String title = ((EditText)layout.findViewById(R.id.titleField)).getText().toString();
                                        String content = ((EditText)layout.findViewById(R.id.contentField)).getText().toString();
                                        Feed feed = new Feed();
                                        feed.setTopic(title);
                                        feed.setContent(content);
                                        feed.setTimestamp(new Timestamp(System.currentTimeMillis()));
                                        long id = fm.newFeed(feed);
                                        Log.i(Constants.LOG_TAG,"New feed id:" + id);
                                        di.dismiss();
                                        adapter.clear();
                                        adapter.addAll(fm.queryFeed(null, 50, "id DESC"));
                                        adapter.notifyDataSetChanged();
                                    }
                                }).setNegativeButton("Cancel", null).show();
                    }
                });

            }
        }
        return rootView;
    }
}
