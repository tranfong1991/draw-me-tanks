package andytran.dmap_phone;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Andy Tran on 4/4/2016.
 */
public class HostAdapter extends ArrayAdapter<Host> {
    private Context context;
    private ArrayList<Host> hosts;

    public HostAdapter(Context context, ArrayList<Host> hosts){
        super(context, -1, hosts);
        this.context = context;
        this.hosts = hosts;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;
        if(v == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.host_item, null);

            ViewHolder holder = new ViewHolder();
            holder.serviceName = (TextView)v.findViewById(R.id.service_name);
            holder.ip = (TextView)v.findViewById(R.id.service_ip);
            holder.port = (TextView)v.findViewById(R.id.service_port);

            v.setTag(holder);
        }
        ViewHolder viewHolder = (ViewHolder)v.getTag();
        Host host = hosts.get(position);

        viewHolder.serviceName.setText(host.getName());
        viewHolder.ip.setText(host.getIp());
        viewHolder.port.setText(String.valueOf(host.getPort()));

        return v;
    }

    static class ViewHolder{
        public TextView serviceName;
        public TextView ip;
        public TextView port;
    }
}
