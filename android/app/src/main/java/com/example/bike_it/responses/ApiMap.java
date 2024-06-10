package com.example.bike_it.responses;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.util.Base64;

public class ApiMap {
    int id = 0;

    String imageBase64;
    String points;
    String createdAt;

    private Bitmap image;

    public int getId() { return id; }

    public String getImageBase64()
    {
        return imageBase64;
    }

    public String getPoints()
    {
        return points;
    }
    public String getCreatedAt()
    {
        return createdAt;
    }

    public Bitmap getImage()
    {
        if(image == null)
        {
            if(imageBase64 != null)
            {
                try
                {
                    byte[] decodedBytes = Base64.getDecoder().decode(imageBase64);
                    this.image = BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
                }
                catch(Exception e)
                {
                    this.image = null;
                }
            }
        }

        return image;
    }

}
