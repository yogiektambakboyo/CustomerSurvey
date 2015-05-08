package com.bcp.CustomerSurvey;

import android.content.Context;
import android.view.*;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.List;
import java.util.Map;


public class Adapter_MainMenu extends SimpleAdapter {

    private List<Map<String, String>> itemList;
    private Context mContext;
    private final String TAG_ICON = "1";
    private final String TAG_MENU = "menu";
    private final String TAG_ID = "id";


    public Adapter_MainMenu(Context context, List<? extends Map<String, ?>> data,
                               int resource, String[] from, int[] to) {
        super(context, data, resource, from, to);

        this.itemList = (List<Map<String, String>>) data;
        this.mContext = context;
    }

    /* A Static class for holding the elements of each List View Item
     * This is created as per Google UI Guideline for faster performance */
    class ViewHolder {
        TextView ListHeader;
        TextView ListDesc;
        LinearLayout ListBG;
        ImageView Listicon;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder holder = null;

        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.l_mainmenu, null);
            holder = new ViewHolder();

            // get the textview's from the convertView
            holder.ListHeader = (TextView) convertView.findViewById(R.id.MainMenuNama);
            holder.ListDesc = (TextView) convertView.findViewById(R.id.MainMenuID);
            holder.ListBG = (LinearLayout) convertView.findViewById(R.id.ListLayout);
            holder.Listicon = (ImageView) convertView.findViewById(R.id.imageViewOP);

            // store it in a Tag as its the first time this view is generated
            convertView.setTag(holder);
        } else {
            /* get the View from the existing Tag */
            holder = (ViewHolder) convertView.getTag();
        }

        /* update the textView's text and color of list item */
        holder.ListHeader.setText(itemList.get(position).get(TAG_MENU));
        holder.ListDesc.setText(itemList.get(position).get(TAG_ID));
        holder.Listicon.setBackgroundResource(Integer.parseInt(itemList.get(position).get(TAG_ICON)));

        holder.ListBG.setAlpha(0.95f);
        return convertView;
    }
}
