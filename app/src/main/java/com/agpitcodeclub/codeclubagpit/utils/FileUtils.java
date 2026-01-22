package com.agpitcodeclub.codeclubagpit.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;

public class FileUtils {

    public static String getPath(Context context, Uri uri) {

        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = null;

        try {
            cursor = context.getContentResolver()
                    .query(uri, projection, null, null, null);

            if (cursor != null && cursor.moveToFirst()) {
                int columnIndex =
                        cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
                return cursor.getString(columnIndex);
            }

        } finally {
            if (cursor != null) cursor.close();
        }

        return null;
    }
}
