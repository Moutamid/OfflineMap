package com.moutamid.offlinemap.offline;

import com.google.android.gms.maps.model.TileProvider;
import com.moutamid.offlinemap.offline.tilemaps.OfflineTileProvider;


public class OfflineMapManager {
	public static TileProvider getOfflineTileProvider() {
		return new OfflineTileProvider();
	}
	
	public static boolean hasDownloadedTileMaps() {
		return true;
	}
}
