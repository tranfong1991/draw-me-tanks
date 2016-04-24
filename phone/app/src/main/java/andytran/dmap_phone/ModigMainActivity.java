package andytran.dmap_phone;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import java.util.Random;

import timothy.dmap_phone.InstructionalGraphic;

public class ModigMainActivity extends Activity {

    private MockGraphicDatabase database = new MockGraphicDatabase();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modig_main);

        final Button edit_button = (Button) findViewById(R.id.modig_main_edit_graphic);
        final Button new_button = (Button) findViewById(R.id.modig_main_new_graphic);


        /**
         * Edit Graphic button response
         */
        edit_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                InstructionalGraphic ig = initializeIG();
                sendIntent(ig);
            }
        });

        /**
         * New Graphic button response
         */
        new_button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                InstructionalGraphic ig = createIG();
                sendIntent(ig);
            }
        });
    }

    /**
     * Sends intent containing an InstructionalGraphic.
     * @param ig InstructionalGraphic to send
     */
    private void sendIntent(InstructionalGraphic ig) {
        InstructionalGraphicChangeRecord record = new InstructionalGraphicChangeRecord(ig);
        Intent intent = new Intent(this, ModifyInstructionalGraphicActivity.class);
        intent.putExtra(InstructionalGraphicChangeRecord.class.getName(), record);

        startActivity(intent);
        return;
    }

    /**
     * Creates and fills an InstructionalGraphic for the edit graphic option.
     * @return An InstructionalGraphic containing a random number of images.
     */
    private InstructionalGraphic initializeIG() {
        InstructionalGraphic ig = createIG();
        populateIG(ig);
        return ig;
    }

    /**
     * Creates an empty InstructionalGraphic.
     * TODO:
     *      Add user-specified name instead of temp name.
     * @return empty, named InstructionalGraphic
     */
    private InstructionalGraphic createIG() {
        String name = new String("Temp Name");
        InstructionalGraphic ig = new InstructionalGraphic(name);
        return ig;
    }

    /**
     * Fills an InstructionalGraphic with a random number of images.
     * @param ig The InstructionalGraphic to fill.
     */
    private void populateIG(InstructionalGraphic ig) {
        int number_rounds = getNumberRounds();
        for(int i = 0; i < number_rounds; ++i) {
            ig.addImage(i, database.get_random_graphic());
        }
        return;
    }

    /**
     * Generates a random number for the number of images in an InstructionalGraphic.
     * @return a random number.
     */
    private int getNumberRounds() {
        Random rand = new Random(System.currentTimeMillis());
        int number_rounds = Math.abs(rand.nextInt())%5 + 1;
        return number_rounds;
    }
}
