package com.example.android.saffronfromzr;

import com.google.firebase.database.IgnoreExtraProperties;

@IgnoreExtraProperties
public class production {
  private   Boolean  pattern;
 private    Boolean  cutting;
 private    Boolean  handwork;
  private   Boolean  stitching;
    public production()
    {

    }
    public production(Boolean pattern,Boolean cutting,Boolean handwork,Boolean stitching)
    {
        this.pattern=pattern;
        this.cutting=cutting;
        this.handwork=handwork;
        this.stitching=stitching;
    }

    public Boolean getPattern() {
        return pattern;
    }

    public Boolean getCutting() {
        return cutting;
    }

    public Boolean getHandwork() {
        return handwork;
    }

    public Boolean getStitching() {
        return stitching;
    }
}
