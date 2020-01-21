package com.candykick.startup.view.adviserAct;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by candykick on 2019. 9. 15..
 */

public interface AdviserUpload1Interface {
    String getTitle();
    String getCategory();
    boolean getIsWriterOpen();
    ArrayList<File> getImageSrc();

    void setTitle(String title);
    void setCategory(String category);
    void setIsWriterOpen(boolean isWriterOpen);
    void setImageSrc(ArrayList<String> imageSrc);
}
