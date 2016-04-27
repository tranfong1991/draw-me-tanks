package andytran.dmap_phone;

import android.app.Activity;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

/**
 * Created by Andy Tran on 4/26/2016.
 */
public class ChangeIPDiaglogFragment extends DialogFragment {
    public interface ChangeIPListener{
        void onChangeIP(String newIp);
    }

    private ChangeIPListener listener;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            listener = (ChangeIPListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString() + "must implement ChangeIPListener");
        }    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String prefName = getResources().getString(R.string.pref_name);
        final String prefIp = getResources().getString(R.string.pref_ip);

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_change_ip, null);
        final EditText ipTxt = (EditText)view.findViewById(R.id.txt_ip);

        final SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(getActivity());
        ipTxt.setText(pref.getString(prefIp, ""));

        // Inflate and set the layout for the dialog
        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                .setTitle("Change IP")
                // Add action buttons
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        String ip = ipTxt.getText().toString();

                        if (ip.length() > 0) {
                            SharedPreferences.Editor editor = pref.edit();
                            editor.putString(prefIp, ip);
                            editor.apply();

                            listener.onChangeIP(ip);
                        }
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        ChangeIPDiaglogFragment.this.getDialog().cancel();
                    }
                });
        return builder.create();
    }
}
