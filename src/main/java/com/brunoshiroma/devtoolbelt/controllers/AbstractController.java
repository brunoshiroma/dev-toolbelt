package com.brunoshiroma.devtoolbelt.controllers;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.ui.Model;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.logging.Logger;

public abstract class AbstractController {

    protected final SimpleDateFormat releaseDateParser = new SimpleDateFormat("yyyyMMdd-HHmmss");

    public static final String HTML_TITLE = "htmlTitle";

    public static final String HTML_DESCRIPTION = "htmlDescription";

    @Value("${release-date}")
    protected String releaseDate;

    protected void setUpModel(Model model){
        try {
            Date lastChangeDate = releaseDateParser.parse(releaseDate);
            Calendar lastChangeCal = Calendar.getInstance();
            lastChangeCal.setTime(lastChangeDate);

            model.addAttribute("changeYear", String.valueOf(lastChangeCal.get(Calendar.YEAR)));
            model.addAttribute("CACHE_VERSION", releaseDate);
        } catch (ParseException e) {
            Logger.getGlobal().throwing("StaticController", "setUpModel", e);
        }
    }

}
