package com.moutamid.offlinemap;

import android.content.res.AssetManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.mapsforge.core.model.LatLong;
import org.mapsforge.map.android.graphics.AndroidGraphicFactory;
import org.mapsforge.map.android.util.AndroidUtil;
import org.mapsforge.map.android.view.MapView;
import org.mapsforge.map.datastore.MapDataStore;
import org.mapsforge.map.layer.cache.TileCache;
import org.mapsforge.map.layer.renderer.TileRendererLayer;
import org.mapsforge.map.reader.MapFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Mapwithstyle extends AppCompatActivity {

    private static final String MAP_FILE_NAME = "roads.map"; // Change this to your map file name

    private MapView mapView;
    private TileCache tileCache;
    private TileRendererLayer tileRendererLayer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AndroidGraphicFactory.createInstance(getApplication());
        setContentView(R.layout.activity_mapwithstyle);
        mapView = findViewById(R.id.mapView);
        mapView.setClickable(true);
        mapView.getMapScaleBar().setVisible(true);
        mapView.setBuiltInZoomControls(true);
        mapView.getMapZoomControls().setZoomLevelMin((byte) 10);
        mapView.getMapZoomControls().setZoomLevelMax((byte) 20);

        copyMapFileFromAssets();

        File mapFile = new File(getFilesDir(), MAP_FILE_NAME);
        Log.d("data", mapFile+"   "+getFilesDir());
        MapDataStore mapDataStore = new MapFile(mapFile);
        tileCache = createTileCache(mapView);
        tileRendererLayer = new TileRendererLayer(tileCache, mapDataStore, mapView.getModel().mapViewPosition, AndroidGraphicFactory.INSTANCE);
        mapView.getLayerManager().getLayers().add(tileRendererLayer);

        LatLong location = new LatLong(109.02299, 9.454988); // Paris, France
        mapView.setCenter(location);
        mapView.setZoomLevel((byte) 12);

        mapView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Handle map click events
                Toast.makeText(Mapwithstyle.this, "Map clicked", Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onDestroy() {
        mapView.destroyAll();
        AndroidGraphicFactory.clearResourceMemoryCache();
        super.onDestroy();
    }

    private void copyMapFileFromAssets() {
        AssetManager assetManager = getAssets();
        InputStream inputStream = null;
        OutputStream outputStream = null;
        try {
            inputStream = assetManager.open(MAP_FILE_NAME);
            File outFile = new File(getFilesDir(), MAP_FILE_NAME);
            outputStream = new FileOutputStream(outFile);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (outputStream != null) {
                    outputStream.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private TileCache createTileCache(MapView mapView) {
        // Set the tile cache size based on the device's screen density
        float scaleFactor = getResources().getDisplayMetrics().density;
        int tileSize = (int) (256 * scaleFactor);
        int tileCacheSize = 5; // Adjust this as needed

        // Create the tile cache
        TileCache tileCache = AndroidUtil.createTileCache(
                this,
                "mapcache",
                mapView.getModel().displayModel.getTileSize(),
                scaleFactor,
                mapView.getModel().frameBufferModel.getOverdrawFactor()
        );

        return tileCache;
    }
}
